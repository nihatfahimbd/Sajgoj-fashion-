package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.data.model.BannerItem
import com.example.data.model.CartItem
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.CourierConfig
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.PixelConfig
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.data.model.StoreConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class StoreRepository(private val context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("sajgoj_store_prefs", Context.MODE_PRIVATE)

  // Initial Seed Data
  private val initialProducts = listOf(
    Product(
      id = "p1",
      name = "এক্সক্লুসিভ কাতান ডিজাইনার শাড়ি (Royal Red)",
      price = 3500,
      stock = 8,
      category = "শাড়ি",
      imageUrl = "res://${R.drawable.banner_eid_special_1792530116809}",
      sku = "SAJ-001",
      description = "খাঁটি বেনারসি ও কাতানের মেলবন্ধনে তৈরি সোনালী জরি পাড়যুক্ত জমকালো ডিজাইনার শাড়ি। উৎসব এবং বিয়ের অনুষ্ঠানের জন্য আদর্শ কালেকশন। সাথে রয়েছে আনস্টিচড ম্যাচিং ব্লাউজ পিস।"
    ),
    Product(
      id = "p2",
      name = "রাজওয়াড়ি কুন্দন ব্রাইডাল চোকার সেট",
      price = 2999,
      stock = 14,
      category = "নেকলেস",
      imageUrl = "res://${R.drawable.banner_jewelry_royal_1792530130914}",
      sku = "SAJ-002",
      description = "অ্যান্টিক গোল্ড প্লেটেড রয়্যাল কুন্দন চোকার, সাথে ম্যাচিং স্টেটমেন্ট ঝুমকা এবং টিকলি। রুচিশীল রত্নপাথর ও মুক্তোর কারুকার্যময় কাজ।"
    ),
    Product(
      id = "p3",
      name = "সিটি গোল্ড রাজকীয় বালা ও চুড়ি সেট (৪ পিস)",
      price = 1250,
      stock = 4,
      category = "চুড়ি",
      imageUrl = "res://${R.drawable.ic_app_icon_1792530102214}",
      sku = "SAJ-003",
      description = "লং লাস্টিং টেকসই প্রিমিয়াম সিটি গোল্ড চুড়ি সেট (৪ পিস)। প্রতিদিনের ব্যবহার এবং পার্টিতে পরার জন্য দারুণ ফিনিশিং।"
    ),
    Product(
      id = "p4",
      name = "রুবি স্টোন ক্রিস্টাল ব্রেসলেট পেয়ার",
      price = 890,
      stock = 20,
      category = "ব্রেসলেট",
      imageUrl = "res://${R.drawable.banner_jewelry_royal_1792530130914}",
      sku = "SAJ-004",
      description = "হালকা ও আকর্ষণীয় রুবি ক্রিস্টাল পাথরে খচিত অ্যাডজাস্টেবল ব্রেসলেট জোড়া। যেকোনো পোশাকের সাথে দারুণ মানানসই।"
    ),
    Product(
      id = "p5",
      name = "জামদানি কারুকাজ করা সুতি ও সিল্ক শাড়ি",
      price = 4200,
      stock = 6,
      category = "শাড়ি",
      imageUrl = "res://${R.drawable.banner_eid_special_1792530116809}",
      sku = "SAJ-005",
      description = "হাতে বোনা ঐতিহ্যবাহী জামদানি মোটিফ এবং নরম আরামদায়ক সিল্ক-সুতি টেক্সচার। আভিজাত্যে ভরপুর বিশেষ সংগ্রহ।"
    ),
    Product(
      id = "p6",
      name = "মীনাকারি ঝুমকো ও নোলক সেট",
      price = 1450,
      stock = 2,
      category = "নেকলেস",
      imageUrl = "res://${R.drawable.ic_app_icon_1792530102214}",
      sku = "SAJ-006",
      description = "রঙিন এনামেল কারুকাজ সমৃদ্ধ ঐতিহ্যবাহী মীনাকারি কানের দুল ও নোলক। উৎসবের সাজে দেবে এক রাজকীয় মাত্রা।"
    )
  )

  private val initialCategories = listOf(
    Category("c1", "শাড়ি", "👗"),
    Category("c2", "নেকলেস", "📿"),
    Category("c3", "চুড়ি", "💍"),
    Category("c4", "ব্রেসলেট", "✨"),
    Category("c5", "ব্রাইডাল সেট", "👑")
  )

  private val initialBanners = listOf(
    BannerItem(
      id = "b1",
      title = "Eid Collection 2026",
      subtitle = "এক্সক্লুসিভ ডিজাইনার শাড়ি ও রাজকীয় জুয়েলারি কালেকশন",
      buttonText = "কালেকশন দেখুন",
      imageUrl = "res://${R.drawable.banner_eid_special_1792530116809}",
      isActive = true
    ),
    BannerItem(
      id = "b2",
      title = "রাজওয়াড়ি ব্রাইডাল কালেকশন",
      subtitle = "ফ্ল্যাট ২০% ছাড় • খাঁটি কুন্দন ও মুক্তোর নিখুঁত কাজ",
      buttonText = "অফার লুফে নিন",
      imageUrl = "res://${R.drawable.banner_jewelry_royal_1792530130914}",
      isActive = true
    )
  )

  private val initialCoupons = listOf(
    Coupon("cp1", "EID2026", "%", 10, 2000, "2026-06-30", true),
    Coupon("cp2", "NEW100", "Flat", 100, 500, "2026-12-31", true),
    Coupon("cp3", "SAJGOJ20", "%", 20, 4000, "2026-12-31", true)
  )

  private val initialOrders = listOf(
    Order(
      id = "ORD-1001",
      customerName = "ফাতেমা আক্তার",
      phone = "01712345678",
      district = "ঢাকা",
      thana = "ধানমন্ডি",
      address = "বাসা ১২, রোড ৫, ধানমন্ডি",
      items = listOf(OrderItem("p1", "এক্সক্লুসিভ কাতান ডিজাইনার শাড়ি (Royal Red)", 1, 3500)),
      subtotal = 3500,
      deliveryCharge = 0,
      discount = 0,
      total = 3500,
      paymentMethod = "Cash on Delivery",
      status = "Pending",
      date = "2026-10-18",
      trackingId = ""
    ),
    Order(
      id = "ORD-1002",
      customerName = "নুসরাত জাহান",
      phone = "01813987654",
      district = "ঢাকা",
      thana = "গুলশান",
      address = "ফ্ল্যাট ৩বি, গুলশান এভিনিউ",
      items = listOf(
        OrderItem("p2", "রাজওয়াড়ি কুন্দন ব্রাইডাল চোকার সেট", 1, 2999),
        OrderItem("p4", "রুবি স্টোন ক্রিস্টাল ব্রেসলেট পেয়ার", 1, 890)
      ),
      subtotal = 3889,
      deliveryCharge = 0,
      discount = 388,
      couponCode = "EID2026",
      total = 3501,
      paymentMethod = "bKash",
      status = "Shipped",
      date = "2026-10-19",
      trackingId = "ST-884219"
    )
  )

  private val initialReviews = listOf(
    ProductReview(
      id = "rev-1",
      productId = "p1",
      customerName = "সুমাইয়া রহমান",
      rating = 5,
      comment = "শাড়িটা সামনাসামনি আরও অনেক সুন্দর! কাপড়ের কোয়ালিটি এবং জরি ও সুতার কাজ অসাধারণ। প্যাকেজিংও খুব প্রিমিয়াম ছিল।",
      date = "2026-10-15",
      verified = true
    ),
    ProductReview(
      id = "rev-2",
      productId = "p1",
      customerName = "তানজিনা ইসলাম",
      rating = 5,
      comment = "অসাধারণ কালেকশন! ঈদের জন্য নিয়েছিলাম, পরিবারের সবাই খুব প্রশংসা করেছে। ডেলিভারিও খুব দ্রুত পেয়েছি।",
      date = "2026-10-17",
      verified = true
    ),
    ProductReview(
      id = "rev-3",
      productId = "p1",
      customerName = "মেহজাবিন চৌধুরী",
      rating = 4,
      comment = "রংটা ছবিতে যেমন ছিল হুবহু তেমন। ব্লাউজ পিসটাও চমৎকার। সাজগোজকে ধন্যবাদ।",
      date = "2026-10-19",
      verified = true
    ),
    ProductReview(
      id = "rev-4",
      productId = "p2",
      customerName = "আফসানা মিমি",
      rating = 5,
      comment = "চোকার সেটটি রাজকীয় লুক দেয়! কুন্দনের ফিনিশিং অসম্ভব সুন্দর এবং ভারি মনে হলেও পরতে খুব আরামদায়ক।",
      date = "2026-10-12",
      verified = true
    ),
    ProductReview(
      id = "rev-5",
      productId = "p2",
      customerName = "নাদিয়া হাসান",
      rating = 5,
      comment = "ব্রাইডাল সাজের জন্য পারফেক্ট একটা সেট। ঝুমকা দুটোও চমৎকার মানিয়েছে। হাইলি রেকমেন্ডেড!",
      date = "2026-10-18",
      verified = true
    ),
    ProductReview(
      id = "rev-6",
      productId = "p3",
      customerName = "রোকসানা পারভীন",
      rating = 5,
      comment = "সিটি গোল্ডের ফিনিশিং একদম খাঁটি সোনার মতো চকচক করে। ৪ পিস বালা পেয়ে খুবই খুশি।",
      date = "2026-10-16",
      verified = true
    ),
    ProductReview(
      id = "rev-7",
      productId = "p4",
      customerName = "সাদিয়া আফরিন",
      rating = 5,
      comment = "রুবি স্টোনগুলো খুব উজ্জ্বল এবং ব্রেসলেটের সাইজ খুব সহজেই অ্যাডজাস্ট করা যায়। দারুণ উপহার হতে পারে।",
      date = "2026-10-14",
      verified = true
    )
  )

  // StateFlows
  private val _products = MutableStateFlow<List<Product>>(loadProducts())
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  private val _reviews = MutableStateFlow<List<ProductReview>>(initialReviews)
  val reviews: StateFlow<List<ProductReview>> = _reviews.asStateFlow()

  private val _categories = MutableStateFlow<List<Category>>(initialCategories)
  val categories: StateFlow<List<Category>> = _categories.asStateFlow()

  private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
  val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

  private val _orders = MutableStateFlow<List<Order>>(initialOrders)
  val orders: StateFlow<List<Order>> = _orders.asStateFlow()

  private val _coupons = MutableStateFlow<List<Coupon>>(initialCoupons)
  val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

  private val _banners = MutableStateFlow<List<BannerItem>>(initialBanners)
  val banners: StateFlow<List<BannerItem>> = _banners.asStateFlow()

  private val _storeConfig = MutableStateFlow(StoreConfig())
  val storeConfig: StateFlow<StoreConfig> = _storeConfig.asStateFlow()

  private val _courierConfig = MutableStateFlow(CourierConfig())
  val courierConfig: StateFlow<CourierConfig> = _courierConfig.asStateFlow()

  private val _pixelConfig = MutableStateFlow(PixelConfig())
  val pixelConfig: StateFlow<PixelConfig> = _pixelConfig.asStateFlow()

  private val firestore: FirebaseFirestore? by lazy {
    try {
      val dbId = context.getString(R.string.firestore_database_id)
      FirebaseFirestore.getInstance(FirebaseApp.getInstance(), dbId)
    } catch (e: Exception) {
      try {
        FirebaseFirestore.getInstance()
      } catch (e2: Exception) {
        null
      }
    }
  }

  init {
    initFirestoreSync()
  }

  private fun initFirestoreSync() {
    val db = firestore ?: return
    try {
      db.collection("products").addSnapshotListener { snapshot, error ->
        if (error == null && snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              val ratingVal = (doc.getDouble("rating") ?: doc.getLong("rating")?.toDouble() ?: 4.8).toFloat()
              val reviewsCountVal = (doc.getLong("reviewsCount") ?: doc.getLong("reviews") ?: 12L).toInt()
              Product(
                id = doc.getString("id") ?: doc.id,
                name = doc.getString("name") ?: "",
                price = (doc.getLong("price") ?: 0L).toInt(),
                stock = (doc.getLong("stock") ?: 0L).toInt(),
                category = doc.getString("category") ?: "শাড়ি",
                imageUrl = doc.getString("imageUrl") ?: (doc.get("images") as? List<*>)?.firstOrNull()?.toString() ?: "",
                sku = doc.getString("sku") ?: "",
                description = doc.getString("description") ?: "",
                rating = ratingVal,
                reviewsCount = reviewsCountVal,
                sourceUrl = doc.getString("sourceUrl") ?: ""
              )
            } catch (e: Exception) {
              null
            }
          }
          if (list.isNotEmpty()) {
            _products.value = list
          }
        }
      }

      db.collection("reviews").addSnapshotListener { snapshot, error ->
        if (error == null && snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              ProductReview(
                id = doc.getString("id") ?: doc.id,
                productId = doc.getString("productId") ?: "",
                customerName = doc.getString("customerName") ?: "",
                rating = (doc.getLong("rating") ?: 5L).toInt(),
                comment = doc.getString("comment") ?: "",
                date = doc.getString("date") ?: "",
                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                verified = doc.getBoolean("verified") ?: true
              )
            } catch (e: Exception) {
              null
            }
          }.sortedWith(compareByDescending<ProductReview> { it.date }.thenByDescending { it.createdAt })
          if (list.isNotEmpty()) {
            _reviews.value = list
          }
        } else if (snapshot != null && snapshot.isEmpty) {
          initialReviews.forEach { r ->
            db.collection("reviews").document(r.id).set(
              mapOf(
                "id" to r.id,
                "productId" to r.productId,
                "customerName" to r.customerName,
                "rating" to r.rating,
                "comment" to r.comment,
                "date" to r.date,
                "createdAt" to r.createdAt,
                "verified" to r.verified
              )
            )
          }
        }
      }

      db.collection("orders").addSnapshotListener { snapshot, error ->
        if (error == null && snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              Order(
                id = doc.getString("id") ?: doc.id,
                customerName = doc.getString("customer") ?: doc.getString("customerName") ?: "",
                phone = doc.getString("phone") ?: "",
                district = doc.getString("district") ?: "ঢাকা",
                thana = doc.getString("thana") ?: "সদর",
                address = doc.getString("address") ?: "",
                items = emptyList(),
                subtotal = (doc.getLong("subtotal") ?: 0L).toInt(),
                deliveryCharge = (doc.getLong("deliveryCharge") ?: 0L).toInt(),
                discount = (doc.getLong("discount") ?: 0L).toInt(),
                couponCode = doc.getString("couponCode") ?: "",
                total = (doc.getLong("total") ?: 0L).toInt(),
                paymentMethod = doc.getString("paymentMethod") ?: "Cash on Delivery",
                status = doc.getString("status") ?: "Pending",
                date = doc.getString("date") ?: "",
                trackingId = doc.getString("trackingId") ?: ""
              )
            } catch (e: Exception) {
              null
            }
          }
          if (list.isNotEmpty()) {
            _orders.value = list
          }
        }
      }
    } catch (e: Exception) {
      // Fallback gracefully
    }
  }

  private fun loadProducts(): List<Product> {
    return initialProducts
  }

  // Cart Operations
  fun addToCart(product: Product, quantity: Int = 1): Boolean {
    if (product.stock <= 0) return false
    val current = _cart.value.toMutableList()
    val index = current.indexOfFirst { it.product.id == product.id }
    if (index >= 0) {
      val existing = current[index]
      val newQty = (existing.quantity + quantity).coerceAtMost(product.stock)
      current[index] = existing.copy(quantity = newQty)
    } else {
      current.add(CartItem(product = product, quantity = quantity.coerceAtMost(product.stock)))
    }
    _cart.value = current
    return true
  }

  fun updateCartQuantity(productId: String, delta: Int) {
    val current = _cart.value.toMutableList()
    val index = current.indexOfFirst { it.product.id == productId }
    if (index >= 0) {
      val item = current[index]
      val newQty = item.quantity + delta
      if (newQty <= 0) {
        current.removeAt(index)
      } else {
        val maxStock = item.product.stock
        current[index] = item.copy(quantity = newQty.coerceAtMost(maxStock))
      }
      _cart.value = current
    }
  }

  fun removeFromCart(productId: String) {
    _cart.value = _cart.value.filterNot { it.product.id == productId }
  }

  fun clearCart() {
    _cart.value = emptyList()
  }

  // Order Placement
  fun placeOrder(
    customerName: String,
    phone: String,
    district: String,
    thana: String,
    address: String,
    paymentMethod: String,
    appliedCoupon: Coupon?
  ): Order? {
    val cartItems = _cart.value
    if (cartItems.isEmpty()) return null

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val deliveryCharge = if (subtotal >= 2000) 0 else if (district.contains("Dhaka", ignoreCase = true) || district.contains("ঢাকা")) 60 else 120

    var discount = 0
    if (appliedCoupon != null) {
      discount = if (appliedCoupon.type == "%") {
        (subtotal * appliedCoupon.value) / 100
      } else {
        appliedCoupon.value
      }
    }

    val total = (subtotal - discount).coerceAtLeast(0) + deliveryCharge
    val orderId = "ORD-${Random.nextInt(1000, 9999)}"
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val newOrder = Order(
      id = orderId,
      customerName = customerName,
      phone = phone,
      district = district,
      thana = thana.ifBlank { "সদর" },
      address = address,
      items = cartItems.map { OrderItem(it.product.id, it.product.name, it.quantity, it.product.price) },
      subtotal = subtotal,
      deliveryCharge = deliveryCharge,
      discount = discount,
      couponCode = appliedCoupon?.code ?: "",
      total = total,
      paymentMethod = paymentMethod,
      status = "Pending",
      date = currentDate,
      trackingId = ""
    )

    // Deduct stock for ordered products
    val updatedProducts = _products.value.map { p ->
      val inCart = cartItems.find { it.product.id == p.id }
      if (inCart != null) {
        p.copy(stock = (p.stock - inCart.quantity).coerceAtLeast(0))
      } else {
        p
      }
    }
    _products.value = updatedProducts

    // Add to orders
    _orders.value = listOf(newOrder) + _orders.value

    try {
      firestore?.collection("orders")?.document(newOrder.id)?.set(
        mapOf(
          "id" to newOrder.id,
          "customer" to newOrder.customerName,
          "customerName" to newOrder.customerName,
          "phone" to newOrder.phone,
          "district" to newOrder.district,
          "thana" to newOrder.thana,
          "address" to newOrder.address,
          "subtotal" to newOrder.subtotal,
          "deliveryCharge" to newOrder.deliveryCharge,
          "discount" to newOrder.discount,
          "couponCode" to newOrder.couponCode,
          "total" to newOrder.total,
          "paymentMethod" to newOrder.paymentMethod,
          "status" to newOrder.status,
          "date" to newOrder.date,
          "trackingId" to newOrder.trackingId
        )
      )
    } catch (e: Exception) {
      // Ignore
    }

    // Clear cart
    clearCart()

    return newOrder
  }

  // Order Management
  fun updateOrderStatus(orderId: String, newStatus: String) {
    _orders.value = _orders.value.map {
      if (it.id == orderId) it.copy(status = newStatus) else it
    }
    try {
      firestore?.collection("orders")?.document(orderId)?.update("status", newStatus)
    } catch (e: Exception) {}
  }

  fun dispatchToCourier(orderId: String, provider: String): String {
    val prefix = if (provider.contains("Steadfast", true)) "ST-" else "PTH-"
    val tracking = prefix + Random.nextInt(100000, 999999)
    _orders.value = _orders.value.map {
      if (it.id == orderId) it.copy(status = "Shipped", trackingId = tracking) else it
    }
    try {
      firestore?.collection("orders")?.document(orderId)?.update(
        mapOf(
          "status" to "Shipped",
          "trackingId" to tracking
        )
      )
    } catch (e: Exception) {}
    return tracking
  }

  // Product CRUD
  fun addProduct(product: Product) {
    _products.value = listOf(product) + _products.value
    try {
      firestore?.collection("products")?.document(product.id)?.set(
        mapOf(
          "id" to product.id,
          "name" to product.name,
          "price" to product.price,
          "stock" to product.stock,
          "category" to product.category,
          "imageUrl" to product.imageUrl,
          "sku" to product.sku,
          "description" to product.description
        )
      )
    } catch (e: Exception) {}
  }

  fun updateProduct(product: Product) {
    _products.value = _products.value.map { if (it.id == product.id) product else it }
    try {
      firestore?.collection("products")?.document(product.id)?.set(
        mapOf(
          "id" to product.id,
          "name" to product.name,
          "price" to product.price,
          "stock" to product.stock,
          "category" to product.category,
          "imageUrl" to product.imageUrl,
          "sku" to product.sku,
          "description" to product.description
        ),
        SetOptions.merge()
      )
    } catch (e: Exception) {}
  }

  fun deleteProduct(productId: String) {
    _products.value = _products.value.filterNot { it.id == productId }
    try {
      firestore?.collection("products")?.document(productId)?.delete()
    } catch (e: Exception) {}
  }

  // Category CRUD
  fun addCategory(category: Category) {
    _categories.value = _categories.value + category
  }

  fun deleteCategory(categoryId: String) {
    _categories.value = _categories.value.filterNot { it.id == categoryId }
  }

  // Coupon CRUD
  fun addCoupon(coupon: Coupon) {
    _coupons.value = listOf(coupon) + _coupons.value
  }

  fun toggleCoupon(couponId: String) {
    _coupons.value = _coupons.value.map {
      if (it.id == couponId) it.copy(isActive = !it.isActive) else it
    }
  }

  fun deleteCoupon(couponId: String) {
    _coupons.value = _coupons.value.filterNot { it.id == couponId }
  }

  // Banner CRUD
  fun addBanner(banner: BannerItem) {
    _banners.value = _banners.value + banner
  }

  fun toggleBanner(bannerId: String) {
    _banners.value = _banners.value.map {
      if (it.id == bannerId) it.copy(isActive = !it.isActive) else it
    }
  }

  fun deleteBanner(bannerId: String) {
    _banners.value = _banners.value.filterNot { it.id == bannerId }
  }

  // Store Configuration
  fun updateStoreConfig(config: StoreConfig) {
    _storeConfig.value = config
  }

  fun updateCourierConfig(config: CourierConfig) {
    _courierConfig.value = config
  }

  fun updatePixelConfig(config: PixelConfig) {
    _pixelConfig.value = config
  }

  // Backup & JSON Export
  fun exportBackupJson(): String {
    val root = JSONObject()
    root.put("version", "1.0-ANDROID")
    root.put("storeName", _storeConfig.value.storeName)
    root.put("totalProducts", _products.value.size)
    root.put("totalOrders", _orders.value.size)
    root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
    return root.toString(2)
  }

  // Customer Reviews
  fun addReview(review: ProductReview) {
    val updated = listOf(review) + _reviews.value
    _reviews.value = updated

    // Recalculate product rating
    val productReviews = updated.filter { it.productId == review.productId }
    if (productReviews.isNotEmpty()) {
      val avgRating = productReviews.map { it.rating }.average().toFloat()
      val roundedAvg = (Math.round(avgRating * 10.0) / 10.0).toFloat()
      val count = productReviews.size
      _products.value = _products.value.map { p ->
        if (p.id == review.productId) {
          p.copy(rating = roundedAvg, reviewsCount = count)
        } else {
          p
        }
      }

      val db = firestore
      if (db != null) {
        try {
          db.collection("products").document(review.productId).set(
            mapOf(
              "rating" to roundedAvg,
              "reviewsCount" to count
            ),
            SetOptions.merge()
          )
        } catch (e: Exception) {
          // ignore
        }
      }
    }

    val db = firestore ?: return
    try {
      db.collection("reviews").document(review.id).set(
        mapOf(
          "id" to review.id,
          "productId" to review.productId,
          "customerName" to review.customerName,
          "rating" to review.rating,
          "comment" to review.comment,
          "date" to review.date,
          "createdAt" to review.createdAt,
          "verified" to review.verified
        ),
        SetOptions.merge()
      )
    } catch (e: Exception) {
      // ignore
    }
  }

  fun resetToDefaults() {
    _products.value = initialProducts
    _reviews.value = initialReviews
    _categories.value = initialCategories
    _orders.value = initialOrders
    _coupons.value = initialCoupons
    _banners.value = initialBanners
    _storeConfig.value = StoreConfig()
  }
}
