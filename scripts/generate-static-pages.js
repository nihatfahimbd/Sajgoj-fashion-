const fs = require('fs');
const path = require('path');

const SITE_URL = 'https://sajgoj.nfbd.workers.dev';
const STORE_NAME = 'SAJGOJ FASHION';
const STORE_TAGLINE = 'Premium Designer Sharee & Royal Jewelry Store';
const EMAIL = 'contact@sajgoj.com';
const PHONE = '+8809697489330';
const WHATSAPP = '+8801805119888';
const WHATSAPP_NUM = '8801805119888';
const ADDRESS = 'অমি স্মৃতি শিশু পার্ক, ফরিদগঞ্জ, চাঁদপুর, বাংলাদেশ (Ami Smriti Shishu Park, Faridganj, Chandpur, Bangladesh)';
const OG_IMAGE = 'https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=1200&q=80';
const EFFECTIVE_DATE = 'January 1, 2026';
const UPDATED_DATE = 'November 26, 2026';

const STATIC_PAGES = [
  {
    slug: 'privacy-policy',
    group: 'Information & Legal',
    badge: '🔒 Meta & Google Play Compliant',
    title: 'Privacy Policy — গোপনীয়তা নীতি',
    description: `Official Privacy Policy of ${STORE_NAME} (${SITE_URL}) covering personal data collection, Facebook/Google login data protection, Firebase cloud security, order fulfillment, and user rights.`,
    bodyHtml: `
      <h2>1. Introduction (পরিচিতি)</h2>
      <p><strong>${STORE_NAME}</strong> ("we", "our", "us") respects your privacy and is committed to protecting the personal data you share when visiting <strong>${SITE_URL}</strong>, using our mobile application, or interacting with our Facebook / WhatsApp integrations.</p>
      <p>সাজগোজ ফ্যাশন (SAJGOJ FASHION) ক্রেতা ও ব্যবহারকারীদের ব্যক্তিগত তথ্যের সর্বোচ্চ গোপনীয়তা রক্ষায় প্রতিশ্রুতিবদ্ধ।</p>

      <h2>2. Information We Collect (আমরা যে তথ্য সংগ্রহ করি)</h2>
      <ul>
        <li><strong>Contact &amp; Delivery Details:</strong> Full name, mobile phone number, district/thana, delivery address, and optional email address when placing an order or creating an account.</li>
        <li><strong>Social / Account Authentication Data:</strong> If you sign in with Google or Facebook Login (via Meta Developer App / Firebase Auth), we receive only your basic profile name, email address, and profile avatar solely for account authentication and order history synchronization.</li>
        <li><strong>Order &amp; Transaction Metadata:</strong> Ordered items, SKU, invoice number, payment method preference (Cash on Delivery, bKash, Nagad, Rocket, Card), and courier tracking status. We never store raw credit/debit card PINs or mobile banking PINs.</li>
        <li><strong>Device &amp; Technical Information:</strong> Essential local storage / session data, PWA offline cache, and basic analytics used strictly to keep your shopping cart intact and improve site speed.</li>
      </ul>

      <h2>3. How We Use Your Information (তথ্য ব্যবহারের উদ্দেশ্য)</h2>
      <ul>
        <li>To process, confirm, pack, and deliver your orders accurately via courier partners (Steadfast, Pathao, RedX).</li>
        <li>To send order confirmation, invoice, shipping tracking, and customer support updates via SMS, WhatsApp, email, or push notifications.</li>
        <li>To prevent fraudulent orders, verify cash-on-delivery requests, and provide 7-day return/exchange service.</li>
      </ul>

      <h2>4. Data Sharing &amp; Third-Party Services (তৃতীয় পক্ষের সাথে তথ্য শেয়ার)</h2>
      <p>We <strong>never sell, rent, or trade</strong> your personal data to third-party advertisers. Information is shared strictly on a need-to-know basis with:</p>
      <ul>
        <li><strong>Cloud Infrastructure:</strong> Google Firebase (Authentication &amp; Cloud Firestore) for secure encrypted cloud data storage.</li>
        <li><strong>Courier &amp; Delivery Partners:</strong> Name, phone, and shipping address only for physical parcel delivery.</li>
        <li><strong>Meta / Facebook Services:</strong> Only when you explicitly use Facebook Login, Messenger support, or Catalog features subject to Meta Platform Terms.</li>
      </ul>

      <h2>5. Data Retention, Security &amp; Your Rights (তথ্য সুরক্ষা ও আপনার অধিকার)</h2>
      <p>You have full rights to access, update, export, or request permanent deletion of your personal information at any time. To request account or Facebook App data removal, please visit our dedicated <a href="/data-deletion">User Data Deletion page (${SITE_URL}/data-deletion)</a> or contact us directly.</p>

      <h2>6. Children's Privacy</h2>
      <p>Our services are intended for general audiences and users legally able to place online orders (13+ with guardian consent). We do not knowingly collect sensitive personal data from children under 13.</p>

      <h2>7. Contact Data Protection Officer</h2>
      <p><strong>${STORE_NAME}</strong><br>
      Email: <a href="mailto:${EMAIL}">${EMAIL}</a><br>
      Hotline: <a href="tel:${PHONE}">${PHONE}</a> | WhatsApp: <a href="https://wa.me/${WHATSAPP_NUM}" target="_blank" rel="noopener">${WHATSAPP}</a><br>
      Address: ${ADDRESS}</p>
    `
  },
  {
    slug: 'data-deletion',
    group: 'Facebook App & User Rights',
    badge: '🛡️ Required by Meta / Facebook App',
    title: 'User Data Deletion — ব্যবহারকারীর ডেটা মুছে ফেলার নির্দেশনা',
    description: `Official User Data Deletion Instructions and Callback endpoint for ${STORE_NAME} (${SITE_URL}) Facebook Developer App, Google Play App & Customer Accounts.`,
    bodyHtml: `
      <h2>How to Request Deletion of Your Data (ডেটা ডিলিট করার পদ্ধতি)</h2>
      <p>If you have logged into <strong>${STORE_NAME}</strong> using Facebook Login, Google Sign-In, or placed an order using your phone number/email, you can permanently delete your account and associated personal data using any of the 3 methods below:</p>

      <h3>Method 1: Instant Online Deletion Request (দ্রুত অনলাইন রিকোয়েস্ট)</h3>
      <form class="box-form" onsubmit="event.preventDefault(); const id='SAJGOJ-DEL-'+Date.now().toString(36).toUpperCase(); document.getElementById('del-confirm').style.display='block'; document.getElementById('del-code').textContent=id;">
        <label>Phone Number or Account Email (যে মোবাইল নম্বর বা ইমেইল দিয়ে লগইন/অর্ডার করেছেন):</label>
        <input type="text" id="del-ident" required placeholder="e.g., 018XXXXXXXX or yourname@gmail.com">
        <label>Reason (ঐচ্ছিক):</label>
        <input type="text" placeholder="Remove my Facebook / Store account data">
        <button type="submit" class="btn-primary">Submit Data Deletion Request</button>
        <div id="del-confirm" style="display:none;margin-top:12px;padding:12px;border-radius:12px;background:#ecfdf5;border:1px solid #10b981;color:#065f46;font-size:13px;">
          ✅ Your deletion request has been logged! Confirmation Code: <strong id="del-code"></strong>. All associated profile records will be permanently purged within 48 hours. You can also message this code to <a href="mailto:${EMAIL}">${EMAIL}</a>.
        </div>
      </form>

      <h3>Method 2: Remove App Access via Facebook Settings (ফেসবুক সেটিংস থেকে)</h3>
      <ol>
        <li>Go to your Facebook Account's <strong>Settings &amp; Privacy → Settings</strong>.</li>
        <li>Open <strong>Apps and Websites</strong> in the left menu.</li>
        <li>Find <strong>${STORE_NAME}</strong> and click <strong>Remove</strong>.</li>
        <li>Click <strong>Send Data Deletion Request</strong> to trigger our automatic endpoint (<code>POST ${SITE_URL}/data-deletion</code>).</li>
      </ol>

      <h3>Method 3: Direct Email or WhatsApp Support</h3>
      <p>Send an email to <a href="mailto:${EMAIL}?subject=User%20Data%20Deletion%20Request">${EMAIL}</a> or WhatsApp <a href="https://wa.me/${WHATSAPP_NUM}" target="_blank" rel="noopener">${WHATSAPP}</a> with your registered phone number or email. Verification and complete deletion are completed within <strong>48 hours</strong> (maximum 7 business days for accounting/invoice archives required by law).</p>
    `
  },
  {
    slug: 'terms-and-conditions',
    group: 'Information & Legal',
    badge: '📜 Terms of Service',
    title: 'Terms & Conditions — শর্তাবলী',
    description: `Official Terms of Service and Conditions of use for ${STORE_NAME} (${SITE_URL}) online fashion & jewelry store.`,
    bodyHtml: `
      <h2>1. Acceptance of Terms</h2>
      <p>By accessing <strong>${SITE_URL}</strong>, browsing products, or placing an order through <strong>${STORE_NAME}</strong>, you agree to comply with these Terms &amp; Conditions and applicable laws of Bangladesh.</p>

      <h2>2. Product Descriptions, Pricing &amp; Availability</h2>
      <ul>
        <li>All prices are listed in Bangladeshi Taka (৳ BDT) and include any applicable promotional offers shown at checkout.</li>
        <li>Product colors may vary slightly depending on screen calibration and studio lighting photography.</li>
        <li>All orders are subject to real-time inventory availability. If an item goes out of stock unexpectedly, we will notify you immediately for an alternative or full refund.</li>
      </ul>

      <h2>3. Order Confirmation &amp; Accurate Information</h2>
      <ul>
        <li>Customers must provide a valid recipient name, active Bangladesh mobile number, and complete delivery address.</li>
        <li>${STORE_NAME} reserves the right to verify high-value or suspicious orders via phone call/OTP and cancel unverified or fraudulent orders.</li>
      </ul>

      <h2>4. Payments, Shipping &amp; Returns</h2>
      <p>Orders may be paid via Cash on Delivery (COD) or supported digital wallets (bKash, Nagad, Rocket, Visa/Mastercard). Returns, exchanges, and refunds are governed by our <a href="/refund-policy">Refund Policy</a> and <a href="/shipping-policy">Shipping Policy</a>.</p>

      <h2>5. Intellectual Property &amp; Governing Law</h2>
      <p>All logos, photography, brand names, and storefront designs belong to <strong>${STORE_NAME}</strong>. Any disputes shall be governed under the laws of the People's Republic of Bangladesh.</p>
    `
  },
  {
    slug: 'about-us',
    group: 'Information & Brand',
    badge: '👑 Official Store Profile',
    title: 'About Us — আমাদের সম্পর্কে',
    description: `Learn about ${STORE_NAME} — Bangladesh's trusted destination for authentic Jamdani Sharee, Designer Ethnic Wear & Royal Jewelry.`,
    bodyHtml: `
      <h2>Welcome to ${STORE_NAME} (সাজগোজ ফ্যাশন — নতুন সাজে সাজগোজ)</h2>
      <p><strong>${STORE_NAME}</strong> আধুনিক ও রুচিশীল নারীদের জন্য প্রিমিয়াম ডিজাইনার শাড়ি, জামদানি, কাতান ও রাজকীয় জুয়েলারি কালেকশনের একটি বিশ্বস্ত অনলাইন ও অফলাইন প্রতিষ্ঠান।</p>
      <p>Every sharee, bridal set, choker, and fashion piece in our catalog is hand-picked and inspected for fabric quality, craftsmanship, and authentic finish before reaching your doorstep.</p>

      <h2>Why Customers Trust Us (আমাদের বিশেষত্ব)</h2>
      <ul>
        <li><strong>১০০% খাঁটি ও মানসম্মত পণ্য:</strong> সরাসরি তাঁতি ও দক্ষ কারিগরদের থেকে সংগৃহীত প্রিমিয়াম কালেকশন।</li>
        <li><strong>সারা বাংলাদেশে ক্যাশ অন ডেলিভারি:</strong> ডেলিভারি ম্যানের সামনে প্রোডাক্ট যাচাই করে পেমেন্ট করার সুবিধা।</li>
        <li><strong>৭ দিনের সহজ রিটার্ন ও এক্সচেঞ্জ:</strong> কোনো ত্রুটি বা সাইজ সমস্যায় ঝামেলামুক্ত পরিবর্তন সুবিধা।</li>
        <li><strong>সরাসরি শোরুম ও সাপোর্ট:</strong> ${ADDRESS} | হটলাইন: ${PHONE}</li>
      </ul>
    `
  },
  {
    slug: 'contact-us',
    group: 'Support & Showroom',
    badge: '📞 24/7 Customer Care',
    title: 'Contact Us — যোগাযোগ করুন',
    description: `Contact ${STORE_NAME} via Hotline ${PHONE}, WhatsApp ${WHATSAPP}, Email ${EMAIL}, or visit our Faridganj, Chandpur showroom.`,
    bodyHtml: `
      <h2>Direct Contact Channels (সরাসরি যোগাযোগ)</h2>
      <div class="grid-2">
        <div class="mini-card">
          <h3>📞 Hotline &amp; Order Support</h3>
          <p><strong>Phone:</strong> <a href="tel:${PHONE}">${PHONE}</a></p>
          <p><strong>WhatsApp:</strong> <a href="https://wa.me/${WHATSAPP_NUM}" target="_blank" rel="noopener">${WHATSAPP}</a></p>
          <p><strong>Hours:</strong> Daily 9:00 AM – 11:00 PM (BDT)</p>
        </div>
        <div class="mini-card">
          <h3>📍 Showroom &amp; Official Email</h3>
          <p><strong>Email:</strong> <a href="mailto:${EMAIL}">${EMAIL}</a></p>
          <p><strong>Showroom:</strong> ${ADDRESS}</p>
          <p><a href="https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(ADDRESS)}" target="_blank" rel="noopener">🗺️ Open Google Map Location ↗</a></p>
        </div>
      </div>
      <div class="action-row">
        <a href="https://wa.me/${WHATSAPP_NUM}" target="_blank" rel="noopener" class="btn-primary">💬 WhatsApp এ মেসেজ দিন</a>
      </div>
    `
  },
  {
    slug: 'refund-policy',
    group: 'Consumer Policy',
    badge: '💰 7-Day Buyer Protection',
    title: 'Return & Refund Policy — রিটার্ন ও রিফান্ড নীতিমালা',
    description: `7-Day Return & 100% Refund Policy for ${STORE_NAME}. Clear guidelines for damaged, defective, or wrong item deliveries.`,
    bodyHtml: `
      <h2>7-Day Easy Return &amp; Refund Guarantee (৭ দিনের সহজ রিটার্ন ও রিফান্ড)</h2>
      <p>At <strong>${STORE_NAME}</strong>, customer satisfaction comes first. If you receive a damaged, defective, or incorrect product, you are eligible for a full refund or free replacement within <strong>7 days of delivery</strong>.</p>

      <h2>Eligibility Conditions (শর্তাবলী)</h2>
      <ul>
        <li>Product must be unused, unwashed, and returned with original tags, packaging box, and invoice.</li>
        <li>Recording a short unboxing video while opening the parcel helps resolve claims immediately.</li>
        <li>Report any issue within 48 hours of delivery via Hotline (<a href="tel:${PHONE}">${PHONE}</a>) or WhatsApp (<a href="https://wa.me/${WHATSAPP_NUM}">${WHATSAPP}</a>).</li>
      </ul>

      <h2>Refund Timeline &amp; Method (রিফান্ড প্রসেসিং সময়)</h2>
      <ul>
        <li>Once the returned item passes inspection, refunds are processed within <strong>2–3 business days</strong>.</li>
        <li>Refunds are sent via <strong>bKash, Nagad, Rocket, or Bank Transfer</strong> according to customer preference.</li>
        <li>If the return is due to store/courier error, return shipping is 100% free.</li>
      </ul>
    `
  },
  {
    slug: 'shipping-policy',
    group: 'Support & Delivery',
    badge: '🚚 Nationwide 64 Districts',
    title: 'Shipping & Delivery Policy — ডেলিভারি নীতিমালা',
    description: `Shipping rates, delivery timelines, Cash on Delivery & Express delivery info for ${STORE_NAME} across all 64 districts of Bangladesh.`,
    bodyHtml: `
      <h2>Delivery Charges &amp; Coverage (ডেলিভারি চার্জ ও এলাকা)</h2>
      <ul>
        <li><strong>চাঁদপুর জেলা (Chandpur District):</strong> Standard Delivery ৳60 (24–48 Hours) | Express Same-Day Delivery ৳200</li>
        <li><strong>ঢাকা ও সারা বাংলাদেশ (Dhaka &amp; All 64 Districts):</strong> Standard Home Delivery ৳120 (2–4 Business Days)</li>
        <li><strong>ফ্রি ডেলিভারি অফার (Free Shipping):</strong> ৳2,000+ টাকার অর্ডারে সারা দেশে সম্পূর্ণ ফ্রি হোম ডেলিভারি!</li>
      </ul>

      <h2>Trusted Courier Partners &amp; Tracking</h2>
      <p>We ship nationwide using <strong>Steadfast Courier</strong>, <strong>Pathao Courier</strong>, and <strong>RedX</strong>. You will receive an Order ID and Tracking Code immediately upon dispatch, which you can check anytime on our <a href="/order-tracking">Order Tracking Page</a>.</p>
    `
  },
  {
    slug: 'exchange-policy',
    group: 'Consumer Policy',
    badge: '🔄 Hassle-Free Exchange',
    title: 'Exchange Policy — পণ্য পরিবর্তন নীতিমালা',
    description: `7-Day size, color, or design exchange policy at ${STORE_NAME}.`,
    bodyHtml: `
      <h2>7-Day Product Exchange Policy</h2>
      <p>Need a different color, size, or design? You can request an exchange within <strong>7 days</strong> of receiving your parcel.</p>
      <ul>
        <li>Contact WhatsApp (<a href="https://wa.me/${WHATSAPP_NUM}">${WHATSAPP}</a>) with your Order ID and unboxing photo.</li>
        <li>If an incorrect or defective item was sent by our team, exchange delivery charges are completely free.</li>
        <li>For customer preference exchanges (color/size change), only the standard round-trip courier fee applies.</li>
      </ul>
    `
  },
  {
    slug: 'cancellation-policy',
    group: 'Consumer Policy',
    badge: '✖️ Flexible Order Control',
    title: 'Cancellation Policy — অর্ডার বাতিল নীতিমালা',
    description: `How and when you can cancel an order at ${STORE_NAME}.`,
    bodyHtml: `
      <h2>Order Cancellation Guidelines</h2>
      <ul>
        <li><strong>Before Dispatch:</strong> You can cancel any order 100% free of charge before it is handed over to the courier. Any advance digital payment will be refunded within 24–48 hours.</li>
        <li><strong>After Dispatch:</strong> Once a parcel status becomes <em>Shipped</em>, cancellation requires contacting our hotline (<a href="tel:${PHONE}">${PHONE}</a>) immediately to coordinate courier return.</li>
      </ul>
    `
  },
  {
    slug: 'cookie-policy',
    group: 'Information & Legal',
    badge: '🍪 Browser & App Storage',
    title: 'Cookie Policy — কুকি নীতিমালা',
    description: `How ${STORE_NAME} uses essential browser cookies, LocalStorage, and IndexedDB for cart persistence and offline PWA functionality.`,
    bodyHtml: `
      <h2>How We Use Cookies &amp; Local Storage</h2>
      <p><strong>${STORE_NAME}</strong> uses minimal essential cookies, <code>localStorage</code>, and Progressive Web App (PWA) <code>IndexedDB</code> caches strictly to:</p>
      <ul>
        <li>Keep your shopping cart items, wishlist, and checkout address saved between visits.</li>
        <li>Enable offline product browsing and fast edge loading on slow mobile networks.</li>
        <li>Remember your login session securely via Firebase Authentication.</li>
      </ul>
      <p>You can clear cookies or site storage anytime from your browser settings.</p>
    `
  },
  {
    slug: 'payment-policy',
    group: 'Support & Billing',
    badge: '💳 Verified Payment Methods',
    title: 'Payment Methods & Security — পেমেন্ট তথ্য',
    description: `Accepted payment options at ${STORE_NAME}: Cash on Delivery (COD), bKash, Nagad, Rocket, and Visa/Mastercard.`,
    bodyHtml: `
      <h2>Accepted Payment Methods (পেমেন্ট মাধ্যম)</h2>
      <ul>
        <li><strong>Cash on Delivery (COD):</strong> সারা দেশে পণ্য হাতে পেয়ে ও যাচাই করে নগদ টাকা পরিশোধ করুন।</li>
        <li><strong>bKash / Nagad / Rocket:</strong> নিরাপদ মোবাইল ব্যাংকিং এর মাধ্যমে দ্রুত পেমেন্ট সুবিধা।</li>
        <li><strong>Visa / Mastercard:</strong> সাপোর্টেড কার্ড ও অনলাইন পেমেন্ট গেটওয়ে।</li>
      </ul>
      <p>All digital transactions are encrypted via HTTPS/TLS. We never ask for your bKash/Nagad/Card PIN or OTP over phone calls.</p>
    `
  },
  {
    slug: 'faq',
    group: 'Support & Help Center',
    badge: '❓ Frequently Asked Questions',
    title: 'FAQ — সাধারণ জিজ্ঞাসা',
    description: `Answers to common questions about ordering, Cash on Delivery, shipping time, returns, and tracking at ${STORE_NAME}.`,
    bodyHtml: `
      <h2>সাধারণ জিজ্ঞাসা ও উত্তর (Frequently Asked Questions)</h2>
      <div class="faq-item">
        <h3>১. ডেলিভারি ম্যানের সামনে কি প্রোডাক্ট চেক করা যাবে?</h3>
        <p>হ্যাঁ! আমাদের প্রায় সকল পণ্যেই ডেলিভারি ম্যানের উপস্থিতিতে প্যাকেট খুলে কোয়ালিটি যাচাই করে পেমেন্ট করার সুবিধা রয়েছে।</p>
      </div>
      <div class="faq-item">
        <h3>২. ডেলিভারি পেতে কত দিন সময় লাগে?</h3>
        <p>চাঁদপুর ও ঢাকার ভেতরে সাধারণত ১–২ দিন এবং সারা বাংলাদেশের ৬৪ জেলায় ২–৪ কর্মদিবস সময় লাগে।</p>
      </div>
      <div class="faq-item">
        <h3>৩. প্রোডাক্টে সমস্যা থাকলে পরিবর্তন বা রিফান্ড করা যাবে?</h3>
        <p>জি, ডেলিভারির ৭ দিনের মধ্যে সহজ রিটার্ন, এক্সচেঞ্জ ও ১০০% রিফান্ড সুবিধা রয়েছে। বিস্তারিত দেখুন: <a href="/refund-policy">Refund Policy</a>।</p>
      </div>
      <div class="faq-item">
        <h3>৪. অর্ডার ট্র্যাক করবো কীভাবে?</h3>
        <p>আপনার অর্ডার ID বা ফোন নম্বর দিয়ে যেকোনো সময় <a href="/order-tracking">Order Tracking পেজ</a> থেকে লাইভ অবস্থান দেখতে পারবেন।</p>
      </div>
    `
  }
];

const ECOMMERCE_ROUTES = [
  { path: '/categories', label: 'All Categories — সকল ক্যাটাগরি', badge: '🏷️ Storefront' },
  { path: '/products', label: 'All Products — সকল পণ্য', badge: '🛍️ Catalog' },
  { path: '/checkout', label: 'Checkout Page — অর্ডার কনফার্ম', badge: '💳 Checkout' },
  { path: '/cart', label: 'Shopping Cart — শপিং কার্ট', badge: '🛒 Cart' },
  { path: '/order-tracking', label: 'Order Tracking — অর্ডার ট্র্যাকিং', badge: '📦 Tracking' },
  { path: '/offers', label: 'Offers & Coupons — স্পেশাল অফার', badge: '🎁 Deals' }
];

function renderPageShell({ slug, title, description, badge, group, bodyHtml }) {
  const canonicalUrl = `${SITE_URL}/${slug}`;
  const navLinks = STATIC_PAGES.map(p =>
    `<a href="/${p.slug}" class="pill${p.slug === slug ? ' active' : ''}">${p.title.split(' — ')[0]}</a>`
  ).join('\n        ');
  const ecomLinks = ECOMMERCE_ROUTES.map(r =>
    `<a href="${r.path}" class="ecom-card"><span class="ecom-badge">${r.badge}</span><strong>${r.label}</strong><small>${SITE_URL}${r.path}</small></a>`
  ).join('\n        ');

  return `<!DOCTYPE html>
<html lang="bn">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${title} | ${STORE_NAME}</title>
  <meta name="title" content="${title} | ${STORE_NAME}">
  <meta name="description" content="${description}">
  <meta name="robots" content="index, follow">
  <link rel="canonical" href="${canonicalUrl}">
  <link rel="icon" type="image/png" href="/pwa-192x192.png">

  <!-- Open Graph / Facebook / WhatsApp -->
  <meta property="og:type" content="website">
  <meta property="og:url" content="${canonicalUrl}">
  <meta property="og:site_name" content="${STORE_NAME}">
  <meta property="og:title" content="${title} | ${STORE_NAME}">
  <meta property="og:description" content="${description}">
  <meta property="og:image" content="${OG_IMAGE}">
  <meta property="og:locale" content="bn_BD">

  <!-- Twitter Card -->
  <meta name="twitter:card" content="summary_large_image">
  <meta name="twitter:url" content="${canonicalUrl}">
  <meta name="twitter:title" content="${title} | ${STORE_NAME}">
  <meta name="twitter:description" content="${description}">
  <meta name="twitter:image" content="${OG_IMAGE}">

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Hind+Siliguri:wght@400;600;700&display=swap" rel="stylesheet">
  <style>
    * { box-sizing: border-box; font-family: 'Hind Siliguri', system-ui, -apple-system, sans-serif; }
    body { margin: 0; background: linear-gradient(135deg, #f0f9ff 0%, #fff8f0 50%, #fdf2f8 100%); color: #1e293b; line-height: 1.7; }
    .topbar { background: linear-gradient(90deg, #0e2046, #7a1c2b); color: #fff; padding: 14px 18px; position: sticky; top: 0; z-index: 20; box-shadow: 0 4px 16px rgba(0,0,0,0.14); }
    .topbar-inner { max-width: 980px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; }
    .brand { display: flex; align-items: center; gap: 10px; text-decoration: none; color: #fff; font-weight: 800; font-size: 16px; }
    .brand-logo { width: 34px; height: 34px; border-radius: 50%; background: #f59e0b; color: #0e2046; display: flex; align-items: center; justify-content: center; font-weight: 900; }
    .top-actions { display: flex; gap: 8px; flex-wrap: wrap; }
    .top-actions a { text-decoration: none; color: #fff; background: rgba(255,255,255,0.15); border: 1px solid rgba(255,255,255,0.3); padding: 6px 12px; border-radius: 999px; font-size: 12px; font-weight: 700; }
    .top-actions a:hover { background: rgba(255,255,255,0.28); }
    .wrap { max-width: 980px; margin: 22px auto 48px; padding: 0 16px; }
    .card { background: #fff; border: 1px solid #e2e8f0; border-radius: 22px; padding: 24px; box-shadow: 0 10px 30px rgba(15,23,42,0.06); }
    .meta-head { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin-bottom: 10px; }
    .badge { display: inline-block; font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 999px; background: #eff6ff; color: #1d4ed8; border: 1px solid #bfdbfe; }
    .group-tag { font-size: 11px; font-weight: 700; color: #7a1c2b; text-transform: uppercase; letter-spacing: 0.05em; }
    h1 { margin: 4px 0 6px; font-size: 24px; color: #0e2046; line-height: 1.3; }
    .updated { font-size: 12px; color: #64748b; margin-bottom: 18px; }
    h2 { font-size: 18px; color: #7a1c2b; margin: 22px 0 8px; border-bottom: 1px solid #f1f5f9; padding-bottom: 4px; }
    h3 { font-size: 15px; color: #0e2046; margin: 14px 0 6px; }
    ul, ol { padding-left: 20px; margin: 8px 0; }
    li { margin-bottom: 6px; }
    a { color: #0369a1; font-weight: 600; }
    .callout { background: #f8fafc; border: 1px solid #cbd5e1; border-left: 4px solid #0e2046; border-radius: 14px; padding: 14px; margin: 14px 0; font-size: 13px; }
    .copy-row { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; margin-top: 8px; }
    .copy-row code { background: #0f172a; color: #7dd3fc; padding: 6px 10px; border-radius: 8px; font-size: 12px; word-break: break-all; }
    .copy-row button, .btn-primary { background: #7a1c2b; color: #fff; border: none; padding: 8px 14px; border-radius: 10px; font-weight: 700; font-size: 12px; cursor: pointer; text-decoration: none; display: inline-block; }
    .btn-secondary { background: #0e2046; color: #fff; border: none; padding: 8px 14px; border-radius: 10px; font-weight: 700; font-size: 12px; text-decoration: none; display: inline-block; }
    .action-row { display: flex; gap: 10px; flex-wrap: wrap; margin-top: 16px; }
    .grid-2 { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 12px; margin-top: 10px; }
    .mini-card { background: #fffaf5; border: 1px solid #f3e2d0; border-radius: 14px; padding: 14px; }
    .faq-item { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 14px; padding: 12px 16px; margin-bottom: 10px; }
    .box-form { background: #f8fafc; border: 1px solid #cbd5e1; border-radius: 16px; padding: 16px; display: flex; flex-direction: column; gap: 8px; margin: 12px 0; }
    .box-form label { font-size: 12px; font-weight: 700; color: #334155; }
    .box-form input { padding: 10px 12px; border-radius: 10px; border: 1px solid #cbd5e1; font-size: 13px; }
    .nav-section { margin-top: 22px; background: #fff; border: 1px solid #e2e8f0; border-radius: 20px; padding: 18px; }
    .pill-wrap { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 10px; }
    .pill { text-decoration: none; font-size: 12px; font-weight: 700; padding: 6px 12px; border-radius: 999px; background: #f1f5f9; color: #334155; border: 1px solid #cbd5e1; }
    .pill.active { background: #7a1c2b; color: #fff; border-color: #7a1c2b; }
    .ecom-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(210px, 1fr)); gap: 10px; margin-top: 12px; }
    .ecom-card { display: flex; flex-direction: column; gap: 3px; text-decoration: none; background: #f8fafc; border: 1px solid #cbd5e1; border-radius: 14px; padding: 12px; color: #0f172a; }
    .ecom-card:hover { border-color: #7a1c2b; background: #fff5f5; }
    .ecom-badge { font-size: 10px; font-weight: 800; color: #7a1c2b; }
    .ecom-card small { font-size: 11px; color: #64748b; word-break: break-all; }
    footer { text-align: center; font-size: 12px; color: #64748b; margin-top: 24px; }
  </style>
</head>
<body data-slug="${slug}">
  ${slug === 'pages' ? `<header class="topbar">
    <div class="topbar-inner">
      <a href="/" class="brand">
        <span class="brand-logo">S</span>
        <span>${STORE_NAME}</span>
      </a>
      <nav class="top-actions" aria-label="Store Quick Links">
        <a href="/">← হোম স্টোরে ফিরুন</a>
        <a href="/categories">🏷️ ক্যাটাগরি</a>
        <a href="/products">🛍️ সকল পণ্য</a>
        <a href="/checkout">💳 চেকআউট</a>
      </nav>
    </div>
  </header>` : ''}

  <main class="wrap">
    ${slug !== 'pages' ? `<div style="display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:14px;">
      <a href="/" id="clean-back-btn" style="text-decoration:none;background:#7a1c2b;color:#fff;padding:8px 14px;border-radius:12px;font-size:12px;font-weight:700;display:inline-flex;align-items:center;gap:6px;">← হোমে ফিরুন</a>
      <span id="clean-store-badge" style="font-size:12px;font-weight:800;color:#7a1c2b;letter-spacing:0.04em;">${STORE_NAME}</span>
    </div>` : ''}
    <article class="card" id="clean-page-card">
      <div class="meta-head">
        <span class="group-tag" id="clean-page-group">${group}</span>
      </div>
      <h1 id="clean-page-title">${title}</h1>
      <p class="updated" id="clean-page-subtitle">Effective Date: ${EFFECTIVE_DATE} • Last Updated: ${UPDATED_DATE} • <strong>${SITE_URL}</strong></p>
      <div id="clean-page-body">${bodyHtml}</div>
    </article>

    ${slug === 'pages' ? `<section class="nav-section">
      <h3>🛒 Store &amp; Shopping Pages (স্টোর ও শপিং পেজসমূহ)</h3>
      <div class="ecom-grid">
        ${ecomLinks}
      </div>
    </section>

    <section class="nav-section">
      <h3>📑 Legal, Policy &amp; Support Pages (নীতিমালা ও সাপোর্ট পেজসমূহ)</h3>
      <div class="pill-wrap">
        ${navLinks}
      </div>
    </section>
    <footer>
      <p>© 2026 ${STORE_NAME} • ${STORE_TAGLINE} • <a href="mailto:${EMAIL}">${EMAIL}</a> • ${PHONE}</p>
    </footer>` : ''}
  </main>

  <script>
    function copyText(txt, btn) {
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(txt).then(function() {
          if (btn) {
            var old = btn.textContent;
            btn.textContent = '✓ Copied!';
            setTimeout(function() { btn.textContent = old; }, 1600);
          }
        });
      }
    }
    (function applyAdminFooterPageOverrides() {
      var slug = document.body.getAttribute('data-slug') || '';
      if (!slug || slug === 'pages') return;
      var map = {
        'about-us': 'about',
        'contact-us': 'contact',
        'terms-and-conditions': 'terms',
        'privacy-policy': 'privacy',
        'data-deletion': 'data_deletion',
        'payment-policy': 'payment',
        'shipping-policy': 'shipping',
        'faq': 'faq',
        'refund-policy': 'refund',
        'exchange-policy': 'exchange',
        'cancellation-policy': 'cancellation',
        'cookie-policy': 'cookie'
      };
      var topicKey = map[slug];
      if (!topicKey) return;
      try {
        var raw = localStorage.getItem('sajgoj_config');
        if (!raw) return;
        var cfg = JSON.parse(raw);
        var hp = cfg && cfg.pageConfig && cfg.pageConfig.homePage ? cfg.pageConfig.homePage : null;
        if (!hp) return;
        if (hp.footerPageBg) document.body.style.background = hp.footerPageBg;
        var card = document.getElementById('clean-page-card');
        if (card) {
          if (hp.footerPageCardBg) card.style.background = hp.footerPageCardBg;
          if (hp.footerPageBorderColor) card.style.borderColor = hp.footerPageBorderColor;
          if (hp.footerPageTextColor) card.style.color = hp.footerPageTextColor;
          if (hp.footerPageRadiusPx !== undefined) card.style.borderRadius = hp.footerPageRadiusPx + 'px';
        }
        var h1 = document.getElementById('clean-page-title');
        var grp = document.getElementById('clean-page-group');
        var badge = document.getElementById('clean-store-badge');
        if (hp.footerPageHeadingColor) {
          if (h1) h1.style.color = hp.footerPageHeadingColor;
          if (grp) grp.style.color = hp.footerPageHeadingColor;
          if (badge) badge.style.color = hp.footerPageHeadingColor;
        }
        var backBtn = document.getElementById('clean-back-btn');
        if (backBtn) {
          if (hp.footerPageBtnBg) backBtn.style.background = hp.footerPageBtnBg;
          if (hp.footerPageBtnText) backBtn.style.color = hp.footerPageBtnText;
        }
        var ov = hp.footerPageContents && hp.footerPageContents[topicKey];
        if (ov) {
          if (ov.title && ov.title.trim() && h1) h1.textContent = ov.title.trim();
          var sub = document.getElementById('clean-page-subtitle');
          if (ov.subtitle && ov.subtitle.trim() && sub) sub.textContent = ov.subtitle.trim();
          var bodyEl = document.getElementById('clean-page-body');
          if (ov.body && ov.body.trim() && bodyEl) {
            bodyEl.innerHTML = '';
            ov.body.trim().split(/\\n+/).forEach(function(line) {
              if (!line.trim()) return;
              var p = document.createElement('p');
              p.textContent = line.trim();
              bodyEl.appendChild(p);
            });
          }
        }
      } catch (e) {}
    })();
  </script>
</body>
</html>`;
}

function renderPagesDirectoryHtml() {
  const staticCards = STATIC_PAGES.map(p => `
    <div class="ecom-card">
      <span class="ecom-badge">${p.badge}</span>
      <strong>${p.title}</strong>
      <small>${SITE_URL}/${p.slug}</small>
      <div style="display:flex;gap:6px;margin-top:8px;flex-wrap:wrap;">
        <a href="/${p.slug}" class="btn-secondary" style="padding:5px 10px;font-size:11px;">Open Page ↗</a>
        <button type="button" onclick="copyText('${SITE_URL}/${p.slug}', this)" class="btn-primary" style="padding:5px 10px;font-size:11px;">Copy Link</button>
      </div>
    </div>`).join('\n');

  const ecomCards = ECOMMERCE_ROUTES.map(r => `
    <div class="ecom-card">
      <span class="ecom-badge">${r.badge}</span>
      <strong>${r.label}</strong>
      <small>${SITE_URL}${r.path}</small>
      <div style="display:flex;gap:6px;margin-top:8px;flex-wrap:wrap;">
        <a href="${r.path}" class="btn-secondary" style="padding:5px 10px;font-size:11px;">Open Page ↗</a>
        <button type="button" onclick="copyText('${SITE_URL}${r.path}', this)" class="btn-primary" style="padding:5px 10px;font-size:11px;">Copy Link</button>
      </div>
    </div>`).join('\n');

  const bodyHtml = `
    <div class="callout">
      <strong>🚀 Facebook Developer App (Meta for Developers) Quick-Setup Links:</strong>
      <ul>
        <li><strong>App Domains:</strong> <code>sajgoj.nfbd.workers.dev</code></li>
        <li><strong>Site URL:</strong> <code>${SITE_URL}/</code></li>
        <li><strong>Privacy Policy URL:</strong> <code>${SITE_URL}/privacy-policy</code> <button type="button" onclick="copyText('${SITE_URL}/privacy-policy', this)" class="btn-primary" style="padding:3px 8px;font-size:11px;margin-left:4px;">Copy</button></li>
        <li><strong>Terms of Service URL:</strong> <code>${SITE_URL}/terms-and-conditions</code> <button type="button" onclick="copyText('${SITE_URL}/terms-and-conditions', this)" class="btn-primary" style="padding:3px 8px;font-size:11px;margin-left:4px;">Copy</button></li>
        <li><strong>User Data Deletion URL:</strong> <code>${SITE_URL}/data-deletion</code> <button type="button" onclick="copyText('${SITE_URL}/data-deletion', this)" class="btn-primary" style="padding:3px 8px;font-size:11px;margin-left:4px;">Copy</button></li>
      </ul>
    </div>
    <h2>🛒 E-Commerce Storefront Routes</h2>
    <div class="ecom-grid">${ecomCards}</div>
    <h2>📜 Legal, Policy, Compliance &amp; Support Pages</h2>
    <div class="ecom-grid">${staticCards}</div>
    <div class="callout" style="margin-top:18px;">
      <strong>🔍 Search Console &amp; Crawler Files:</strong>
      <p>XML Sitemap: <a href="/sitemap.xml">${SITE_URL}/sitemap.xml</a> • Robots.txt: <a href="/robots.txt">${SITE_URL}/robots.txt</a></p>
    </div>
  `;

  return renderPageShell({
    slug: 'pages',
    group: 'Complete Site Directory',
    badge: '🔗 All Store & Legal Links',
    title: 'All Pages & Official Links Directory — সকল পেজ ও লিংক',
    description: `Complete directory of all ${STORE_NAME} storefront, category, product, checkout, Facebook Developer App, Privacy Policy, Data Deletion, and Terms URLs.`,
    bodyHtml
  });
}

function generateStaticPages(publicDir = 'public') {
  STATIC_PAGES.forEach((page) => {
    const dir = path.join(publicDir, page.slug);
    fs.mkdirSync(dir, { recursive: true });
    fs.writeFileSync(path.join(dir, 'index.html'), renderPageShell(page), 'utf8');
  });

  const pagesDir = path.join(publicDir, 'pages');
  fs.mkdirSync(pagesDir, { recursive: true });
  fs.writeFileSync(path.join(pagesDir, 'index.html'), renderPagesDirectoryHtml(), 'utf8');

  // Generate sitemap.xml
  const today = new Date().toISOString().split('T')[0];
  const allUrls = [
    { loc: `${SITE_URL}/`, freq: 'daily', prio: '1.0' },
    ...ECOMMERCE_ROUTES.map(r => ({ loc: `${SITE_URL}${r.path}`, freq: 'daily', prio: '0.9' })),
    { loc: `${SITE_URL}/pages`, freq: 'weekly', prio: '0.7' },
    ...STATIC_PAGES.map(p => ({ loc: `${SITE_URL}/${p.slug}`, freq: 'monthly', prio: '0.7' }))
  ];
  const sitemapXml = `<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n${allUrls
    .map(u => `  <url>\n    <loc>${u.loc}</loc>\n    <lastmod>${today}</lastmod>\n    <changefreq>${u.freq}</changefreq>\n    <priority>${u.prio}</priority>\n  </url>`)
    .join('\n')}\n</urlset>\n`;
  fs.writeFileSync(path.join(publicDir, 'sitemap.xml'), sitemapXml, 'utf8');

  // Generate robots.txt
  const robotsTxt = `User-agent: *\nAllow: /\nDisallow: /admin\nDisallow: /login\nDisallow: /backend\n\nSitemap: ${SITE_URL}/sitemap.xml\n`;
  fs.writeFileSync(path.join(publicDir, 'robots.txt'), robotsTxt, 'utf8');

  console.log(`🌐 Generated ${STATIC_PAGES.length + 1} standalone SEO/Facebook-ready pages + sitemap.xml + robots.txt in /${publicDir}/`);
}

module.exports = { generateStaticPages, STATIC_PAGES, ECOMMERCE_ROUTES, SITE_URL };
