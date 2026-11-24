const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const CUSTOMER_UID = "customer_alice_123";
const OTHER_UID = "customer_bob_456";
const ADMIN_UID = "admin_nihat_789";
const ADMIN_EMAIL = "nihatfahimbd@gmail.com";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function nowPast() {
  return new Date(Date.now() - 1000);
}

function futureDate() {
  return new Date(Date.now() + 120000);
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function customerCtx(uid = CUSTOMER_UID) {
  return testEnv.authenticatedContext(uid, {
    email: `${uid}@example.com`,
    email_verified: true,
  });
}

function adminCtx() {
  return testEnv.authenticatedContext(ADMIN_UID, {
    email: ADMIN_EMAIL,
    email_verified: true,
  });
}

function unverifiedSpoofAdminCtx() {
  return testEnv.authenticatedContext("spoof_uid", {
    email: ADMIN_EMAIL,
    email_verified: false,
  });
}

// ---------------------------------------------------------------------------
// 1. Catalog (`products`) — Public read, verified admin only writes
// ---------------------------------------------------------------------------
test("products: unauthenticated read succeeds, non-admin and spoofed-admin writes fail", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = customerCtx().firestore();
  const spoofDb = unverifiedSpoofAdminCtx().firestore();
  const admDb = adminCtx().firestore();

  const validProduct = {
    name: "এক্সক্লুসিভ কাতান ডিজাইনার শাড়ি",
    price: 3500,
    stock: 8,
    category: "শাড়ি",
    imageUrl: "res://banner_eid_special",
    sku: "SAJ-001",
    description: "খাঁটি বেনারসি কাতান শাড়ি।",
    colors: ["রয়েল রেড", "মেরুন"],
    sizes: ["Free Size"],
    ageGroups: [],
    stockUpdatedAt: 1729500001000,
    lastSyncSource: "ADMIN_ADD",
    isHotSale: true,
    isBestDeal: true,
    isB2G1: false,
    createdAt: 1729500001000,
    regularPrice: 4200,
    offerPrice: 3500,
    videoEnabled: false,
    videoType: "upload",
    videoUrl: "",
    videoThumbUrl: "",
    additionalImages: [],
    updatedAt: nowPast(),
  };

  // Admin creates valid product matching Kotlin StoreRepository.addProduct()
  await assertSucceeds(admDb.collection("products").doc("p1").set(validProduct));

  // Public storefront read succeeds
  await assertSucceeds(unauthDb.collection("products").get());
  await assertSucceeds(unauthDb.collection("products").doc("p1").get());

  // Non-admin & unverified spoofed-admin cannot create or edit products
  await assertFails(aliceDb.collection("products").doc("p2").set(validProduct));
  await assertFails(spoofDb.collection("products").doc("p2").set(validProduct));

  // Admin partial stock update succeeds, immutable createdAt mutation fails
  await assertSucceeds(
    admDb.collection("products").doc("p1").update({
      stock: 7,
      stockUpdatedAt: 1729500005000,
      lastSyncSource: "STOREFRONT_SALE",
      updatedAt: nowPast(),
    })
  );
  await assertFails(
    admDb.collection("products").doc("p1").update({
      createdAt: 999999,
      updatedAt: nowPast(),
    })
  );
  // Shadow/ghost field injection rejected
  await assertFails(
    admDb.collection("products").doc("p_bad").set({ ...validProduct, isVerifiedHack: true })
  );
});

// ---------------------------------------------------------------------------
// 2. Customer Reviews (`reviews`) — Public read, owner-scoped create/update
// ---------------------------------------------------------------------------
test("reviews: authenticated user writes own review, cross-user update and future timestamps fail", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = customerCtx(CUSTOMER_UID).firestore();
  const bobDb = customerCtx(OTHER_UID).firestore();

  const validReview = {
    userId: CUSTOMER_UID,
    productId: "p1",
    customerName: "সুমাইয়া রহমান",
    rating: 5,
    comment: "খুব সুন্দর শাড়ি!",
    date: "2026-11-18",
    createdAt: nowPast(),
    verified: true,
  };

  // Unauthenticated write rejected
  await assertFails(unauthDb.collection("reviews").doc("rev_1").set(validReview));

  // Identity spoofing rejected (Alice writing with Bob's userId)
  await assertFails(
    aliceDb.collection("reviews").doc("rev_spoof").set({ ...validReview, userId: OTHER_UID })
  );

  // Tampered future timestamp rejected
  await assertFails(
    aliceDb.collection("reviews").doc("rev_future").set({ ...validReview, createdAt: futureDate() })
  );

  // Out-of-bounds rating rejected
  await assertFails(
    aliceDb.collection("reviews").doc("rev_bad_rate").set({ ...validReview, rating: 6 })
  );

  // Valid review succeeds
  await assertSucceeds(aliceDb.collection("reviews").doc("rev_1").set(validReview));
  await assertSucceeds(unauthDb.collection("reviews").get());

  // Cross-user modify/delete fails
  await assertFails(
    bobDb.collection("reviews").doc("rev_1").update({ comment: "Tampered", updatedAt: nowPast() })
  );
  await assertFails(bobDb.collection("reviews").doc("rev_1").delete());

  // Owner partial edit succeeds
  await assertSucceeds(
    aliceDb.collection("reviews").doc("rev_1").update({
      rating: 4,
      comment: "আপডেটেড রিভিউ",
      updatedAt: nowPast(),
    })
  );
});

// ---------------------------------------------------------------------------
// 3. Orders (`orders`) — Strict PII isolation, filtered query, admin workflow
// ---------------------------------------------------------------------------
test("orders: owner creates Pending order, filtered list succeeds, unfiltered/cross-user PII reads fail", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = customerCtx(CUSTOMER_UID).firestore();
  const bobDb = customerCtx(OTHER_UID).firestore();
  const admDb = adminCtx().firestore();

  const aliceOrder = {
    userId: CUSTOMER_UID,
    customer: "ফাতেমা আক্তার",
    customerName: "ফাতেমা আক্তার",
    phone: "01712345678",
    district: "ঢাকা",
    thana: "ধানমন্ডি",
    address: "বাসা ১২, রোড ৫",
    items: [{ productId: "p1", name: "কাতান শাড়ি", quantity: 1, price: 3500, color: "লাল", size: "Free Size", ageGroup: "" }],
    subtotal: 3500,
    deliveryCharge: 0,
    discount: 0,
    couponCode: "",
    total: 3500,
    paymentMethod: "Cash on Delivery",
    status: "Pending",
    date: "2026-11-18",
    trackingId: "",
    stockRestockedOnCancel: false,
    createdAt: nowPast(),
    updatedAt: nowPast(),
  };

  // Unauthenticated cannot create or read orders
  await assertFails(unauthDb.collection("orders").doc("ORD-1001").set(aliceOrder));
  await assertFails(unauthDb.collection("orders").get());

  // Alice cannot bypass Pending to self-mark Delivered
  await assertFails(
    aliceDb.collection("orders").doc("ORD-CHEAT").set({ ...aliceOrder, status: "Delivered" })
  );

  // Alice creates valid Pending order matching StoreRepository.placeOrder()
  await assertSucceeds(aliceDb.collection("orders").doc("ORD-1001").set(aliceOrder));

  // Cross-user PII read rejected
  await assertFails(bobDb.collection("orders").doc("ORD-1001").get());

  // Unfiltered list by non-admin customer rejected
  await assertFails(aliceDb.collection("orders").get());

  // Filtered Kotlin query (`whereEqualTo("userId", uid)`) succeeds for owner
  await assertSucceeds(aliceDb.collection("orders").where("userId", "==", CUSTOMER_UID).get());

  // Admin can list all orders and update status + trackingId
  await assertSucceeds(admDb.collection("orders").get());
  await assertSucceeds(
    admDb.collection("orders").doc("ORD-1001").update({
      status: "Shipped",
      trackingId: "ST-884219",
      updatedAt: nowPast(),
    })
  );

  // Alice cannot change status after Shipped
  await assertFails(
    aliceDb.collection("orders").doc("ORD-1001").update({
      status: "Cancelled",
      updatedAt: nowPast(),
    })
  );
});

// ---------------------------------------------------------------------------
// 4. Returns, POS Inventory & Audit Events (`returns`, `pos_inventory`, `inventory_sync_events`)
// ---------------------------------------------------------------------------
test("returns, pos_inventory, and inventory_sync_events: strictly verified-admin only", async () => {
  const aliceDb = customerCtx(CUSTOMER_UID).firestore();
  const admDb = adminCtx().firestore();

  const returnDoc = {
    userId: ADMIN_UID,
    orderId: "ORD-1001",
    customer: "ফাতেমা আক্তার",
    customerName: "ফাতেমা আক্তার",
    phone: "01712345678",
    date: "2026-11-18",
    createdAt: nowPast(),
    updatedAt: nowPast(),
    returnedItems: [{ productId: "p1", name: "কাতান শাড়ি", price: 3500, qty: 1, condition: "ভালো", restock: true, color: "লাল", size: "Free Size" }],
    isExchange: false,
    exchangeItems: [],
    totalReturnValue: 3500,
    totalExchangeValue: 0,
    deliveryFee: 0,
    priceDifference: -3500,
    differenceType: "refund",
    settlementMethod: "bKash",
    settlementTrxId: "TRX123",
    settlementStatus: "paid",
    reason: "সাইজ সমস্যা",
    note: "",
  };

  const posDoc = {
    productId: "p1",
    name: "কাতান শাড়ি",
    sku: "SAJ-001",
    stock: 7,
    price: 3500,
    updatedAt: 1729500005000,
    lastSource: "STOREFRONT_SALE",
    lastOrigin: "AND-1234",
    lastReferenceId: "ORD-1001",
  };

  const syncEventDoc = {
    productId: "p1",
    productName: "কাতান শাড়ি",
    sku: "SAJ-001",
    previousStock: 8,
    newStock: 7,
    delta: -1,
    source: "STOREFRONT_SALE",
    reason: "অনলাইন স্টোরফ্রন্ট বিক্রয়",
    referenceId: "ORD-1001",
    timestamp: 1729500005000,
  };

  // Customer denied all reads/writes on returns, pos_inventory, sync events
  await assertFails(aliceDb.collection("returns").doc("RET-1").set(returnDoc));
  await assertFails(aliceDb.collection("returns").get());
  await assertFails(aliceDb.collection("pos_inventory").doc("p1").set(posDoc));
  await assertFails(aliceDb.collection("pos_inventory").get());
  await assertFails(aliceDb.collection("inventory_sync_events").doc("SYNC-1").set(syncEventDoc));
  await assertFails(aliceDb.collection("inventory_sync_events").get());

  // Verified admin succeeds
  await assertSucceeds(admDb.collection("returns").doc("RET-1").set(returnDoc));
  await assertSucceeds(admDb.collection("returns").get());
  await assertSucceeds(admDb.collection("pos_inventory").doc("p1").set(posDoc));
  await assertSucceeds(admDb.collection("pos_inventory").get());
  await assertSucceeds(admDb.collection("inventory_sync_events").doc("SYNC-1").set(syncEventDoc));
  await assertSucceeds(admDb.collection("inventory_sync_events").get());

  // Sync events are immutable even for admin
  await assertFails(admDb.collection("inventory_sync_events").doc("SYNC-1").update({ newStock: 99 }));
  await assertFails(admDb.collection("inventory_sync_events").doc("SYNC-1").delete());
});

// ---------------------------------------------------------------------------
// 5. SMS Gateway Queue (`sms_queue`) — Strictly verified-admin only
// ---------------------------------------------------------------------------
test("sms_queue: admin creates, updates status, and deletes; non-admin & invalid status rejected", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = customerCtx(CUSTOMER_UID).firestore();
  const admDb = adminCtx().firestore();

  const validSms = {
    id: "SMS-1001",
    phone: "01712345678",
    customerName: "ফাতেমা আক্তার",
    message: "আপনার অর্ডার #ORD-1001 নিশ্চিত হয়েছে! - SAJGOJ FASHION",
    orderId: "ORD-1001",
    type: "order_alert",
    status: "pending",
    channel: "sim_gateway",
    createdAt: 1729500001000,
    updatedAt: 1729500001000,
    sentAt: 0,
    sentByDevice: "",
    errorReason: "",
  };

  // Non-admin & unauthenticated denied
  await assertFails(unauthDb.collection("sms_queue").doc("SMS-1001").set(validSms));
  await assertFails(aliceDb.collection("sms_queue").doc("SMS-1001").set(validSms));
  await assertFails(aliceDb.collection("sms_queue").get());

  // Verified admin creates valid queue item
  await assertSucceeds(admDb.collection("sms_queue").doc("SMS-1001").set(validSms));
  await assertSucceeds(admDb.collection("sms_queue").get());

  // Invalid status rejected
  await assertFails(
    admDb.collection("sms_queue").doc("SMS-1001").update({
      status: "hacked_status",
      updatedAt: 1729500002000,
    })
  );

  // Admin SIM gateway marks SMS as sent
  await assertSucceeds(
    admDb.collection("sms_queue").doc("SMS-1001").update({
      status: "sent",
      sentAt: 1729500003000,
      updatedAt: 1729500003000,
      sentByDevice: "Pixel Phone",
    })
  );

  // Admin can clean up sent queue entry
  await assertSucceeds(admDb.collection("sms_queue").doc("SMS-1001").delete());
});

// ---------------------------------------------------------------------------
// 6. Real-Time Customer Support Chat (`support_chats` & `messages`)
// ---------------------------------------------------------------------------
test("support_chats & messages: owner-isolated customer chat, verified admin inbox/reply, and anti-spoofing rules", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = customerCtx(CUSTOMER_UID).firestore();
  const bobDb = customerCtx(OTHER_UID).firestore();
  const spoofDb = unverifiedSpoofAdminCtx().firestore();
  const admDb = adminCtx().firestore();

  const aliceThread = {
    userId: CUSTOMER_UID,
    customerName: "সুমাইয়া রহমান",
    customerEmail: "sumaiya@example.com",
    customerPhotoUrl: "",
    lastMessage: "আমার অর্ডার #ORD-1001 কখন ডেলিভারি হবে?",
    lastSenderRole: "customer",
    status: "open",
    unreadByAdmin: 1,
    unreadByCustomer: 0,
    orderId: "ORD-1001",
    createdAt: nowPast(),
    updatedAt: nowPast(),
  };

  const aliceMsg1 = {
    senderId: CUSTOMER_UID,
    senderName: "সুমাইয়া রহমান",
    senderRole: "customer",
    text: "আমার অর্ডার #ORD-1001 কখন ডেলিভারি হবে?",
    orderId: "ORD-1001",
    createdAt: nowPast(),
  };

  // Unauthenticated access denied
  await assertFails(unauthDb.collection("support_chats").doc(CUSTOMER_UID).set(aliceThread));
  await assertFails(unauthDb.collection("support_chats").doc(CUSTOMER_UID).get());
  await assertFails(unauthDb.collection("support_chats").get());
  await assertFails(
    unauthDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").doc("msg1").set(aliceMsg1)
  );

  // Customer cannot create thread under another UID or spoof role/unreadByCustomer/future time/id field
  await assertFails(aliceDb.collection("support_chats").doc(OTHER_UID).set(aliceThread));
  await assertFails(
    aliceDb.collection("support_chats").doc(CUSTOMER_UID).set({ ...aliceThread, lastSenderRole: "admin" })
  );
  await assertFails(
    aliceDb.collection("support_chats").doc(CUSTOMER_UID).set({ ...aliceThread, unreadByCustomer: 3 })
  );
  await assertFails(
    aliceDb.collection("support_chats").doc(CUSTOMER_UID).set({ ...aliceThread, createdAt: futureDate() })
  );
  await assertFails(
    aliceDb.collection("support_chats").doc(CUSTOMER_UID).set({ ...aliceThread, id: CUSTOMER_UID })
  );

  // Alice creates her own thread & first message
  await assertSucceeds(aliceDb.collection("support_chats").doc(CUSTOMER_UID).set(aliceThread));
  await assertSucceeds(
    aliceDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").doc("msg_1").set(aliceMsg1)
  );

  // Customer cannot spoof admin role in message subcollection
  await assertFails(
    aliceDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").doc("msg_spoof").set({
      ...aliceMsg1,
      senderRole: "admin",
    })
  );

  // Cross-user isolation: Bob & unverified-email spoofed admin cannot read/list/write Alice's thread or messages
  await assertFails(bobDb.collection("support_chats").doc(CUSTOMER_UID).get());
  await assertFails(bobDb.collection("support_chats").get());
  await assertFails(bobDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").get());
  await assertFails(
    bobDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").doc("msg_bob").set({
      ...aliceMsg1,
      senderId: OTHER_UID,
    })
  );
  await assertFails(spoofDb.collection("support_chats").get());
  await assertFails(
    spoofDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").doc("msg_spoof2").set({
      senderId: "spoof_uid",
      senderName: "Fake Admin",
      senderRole: "admin",
      text: "Spoof",
      createdAt: nowPast(),
    })
  );

  // Alice can read her own thread and messages (filtered list & direct get)
  await assertSucceeds(aliceDb.collection("support_chats").doc(CUSTOMER_UID).get());
  await assertSucceeds(aliceDb.collection("support_chats").where("userId", "==", CUSTOMER_UID).get());
  await assertSucceeds(aliceDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").get());

  // Admin can list all threads, read Alice's messages, mark read, and reply
  await assertSucceeds(admDb.collection("support_chats").get());
  await assertSucceeds(admDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").get());
  await assertSucceeds(
    admDb.collection("support_chats").doc(CUSTOMER_UID).update({
      unreadByAdmin: 0,
      updatedAt: nowPast(),
    })
  );
  await assertSucceeds(
    admDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").doc("msg_2").set({
      senderId: ADMIN_UID,
      senderName: "SAJGOJ সাপোর্ট টিম",
      senderRole: "admin",
      text: "আপনার পার্সেলটি আজ ডেলিভারির জন্য বের হয়েছে!",
      createdAt: nowPast(),
    })
  );
  await assertSucceeds(
    admDb.collection("support_chats").doc(CUSTOMER_UID).update({
      lastMessage: "আপনার পার্সেলটি আজ ডেলিভারির জন্য বের হয়েছে!",
      lastSenderRole: "admin",
      status: "open",
      unreadByAdmin: 0,
      unreadByCustomer: 1,
      updatedAt: nowPast(),
    })
  );

  // Alice marks thread read (unreadByCustomer -> 0), cannot tamper unreadByCustomer to >0 or mutate createdAt
  await assertFails(
    aliceDb.collection("support_chats").doc(CUSTOMER_UID).update({
      unreadByCustomer: 5,
      updatedAt: nowPast(),
    })
  );
  await assertSucceeds(
    aliceDb.collection("support_chats").doc(CUSTOMER_UID).update({
      unreadByCustomer: 0,
      updatedAt: nowPast(),
    })
  );

  // Admin resolves thread; Alice cannot delete thread or messages
  await assertSucceeds(
    admDb.collection("support_chats").doc(CUSTOMER_UID).update({
      status: "resolved",
      updatedAt: nowPast(),
    })
  );
  await assertFails(aliceDb.collection("support_chats").doc(CUSTOMER_UID).delete());
  await assertFails(
    admDb.collection("support_chats").doc(CUSTOMER_UID).collection("messages").doc("msg_1").delete()
  );
});

// ---------------------------------------------------------------------------
// 7. Customer FCM Token & Order Status Notifications (`customers`, `order_notifications`)
// ---------------------------------------------------------------------------
test("customers & order_notifications: owner saves FCM token, admin dispatches notification, owner reads & marks read", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const aliceDb = customerCtx(CUSTOMER_UID).firestore();
  const bobDb = customerCtx(OTHER_UID).firestore();
  const admDb = adminCtx().firestore();

  // Alice registers profile with FCM token
  await assertSucceeds(
    aliceDb.collection("customers").doc(CUSTOMER_UID).set({
      userId: CUSTOMER_UID,
      name: "ফাতেমা আক্তার",
      email: "alice@example.com",
      fcmToken: "fcm_token_alice_001",
      notificationsEnabled: true,
      createdAt: nowPast(),
      updatedAt: nowPast(),
    })
  );

  // Bob cannot read or overwrite Alice's customer profile / FCM token
  await assertFails(bobDb.collection("customers").doc(CUSTOMER_UID).get());
  await assertFails(
    bobDb.collection("customers").doc(CUSTOMER_UID).update({
      fcmToken: "hijacked_token",
      updatedAt: nowPast(),
    })
  );

  // Alice updates her own FCM token
  await assertSucceeds(
    aliceDb.collection("customers").doc(CUSTOMER_UID).update({
      fcmToken: "fcm_token_alice_002",
      notificationsEnabled: true,
      updatedAt: nowPast(),
    })
  );

  const notifDoc = {
    userId: CUSTOMER_UID,
    orderId: "ORD-1001",
    customerName: "ফাতেমা আক্তার",
    previousStatus: "Pending",
    newStatus: "Shipped",
    title: "অর্ডার #ORD-1001 শিপড হয়েছে 🚚",
    message: "আপনার অর্ডারটি কুরিয়ারে পাঠানো হয়েছে (SteadFast: ST-554433)।",
    trackingId: "ST-554433",
    deliveredBy: "SteadFast",
    channel: "firestore",
    read: false,
    createdAtMs: 1795000000000,
    createdAt: nowPast(),
    updatedAt: nowPast(),
  };

  // Unauthenticated & customer cannot spoof creating order status notifications
  await assertFails(unauthDb.collection("order_notifications").doc("notif_1").set(notifDoc));
  await assertFails(aliceDb.collection("order_notifications").doc("notif_1").set(notifDoc));
  await assertFails(bobDb.collection("order_notifications").doc("notif_1").set({ ...notifDoc, userId: OTHER_UID }));

  // Verified admin creates notification
  await assertSucceeds(admDb.collection("order_notifications").doc("notif_1").set(notifDoc));

  // Unfiltered list or cross-user read denied
  await assertFails(aliceDb.collection("order_notifications").get());
  await assertFails(bobDb.collection("order_notifications").doc("notif_1").get());

  // Owner queries own notifications and marks read
  await assertSucceeds(
    aliceDb.collection("order_notifications").where("userId", "==", CUSTOMER_UID).get()
  );
  await assertSucceeds(
    aliceDb.collection("order_notifications").doc("notif_1").update({
      read: true,
      updatedAt: nowPast(),
    })
  );

  // Owner cannot tamper with newStatus or delete notification
  await assertFails(
    aliceDb.collection("order_notifications").doc("notif_1").update({
      newStatus: "Delivered",
      updatedAt: nowPast(),
    })
  );
  await assertFails(aliceDb.collection("order_notifications").doc("notif_1").delete());
  await assertSucceeds(admDb.collection("order_notifications").doc("notif_1").delete());
});


