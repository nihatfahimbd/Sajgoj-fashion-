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
import com.example.data.model.StoreConfig
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

  // StateFlows
  private val _products = MutableStateFlow<List<Product>>(loadProducts())
  val products: StateFlow<List<Product>> = _products.asStateFlow()

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

    // Clear cart
    clearCart()

    return newOrder
  }

  // Order Management
  fun updateOrderStatus(orderId: String, newStatus: String) {
    _orders.value = _orders.value.map {
      if (it.id == orderId) it.copy(status = newStatus) else it
    }
  }

  fun dispatchToCourier(orderId: String, provider: String): String {
    val prefix = if (provider.contains("Steadfast", true)) "ST-" else "PTH-"
    val tracking = prefix + Random.nextInt(100000, 999999)
    _orders.value = _orders.value.map {
      if (it.id == orderId) it.copy(status = "Shipped", trackingId = tracking) else it
    }
    return tracking
  }

  // Product CRUD
  fun addProduct(product: Product) {
    _products.value = listOf(product) + _products.value
  }

  fun updateProduct(product: Product) {
    _products.value = _products.value.map { if (it.id == product.id) product else it }
  }

  fun deleteProduct(productId: String) {
    _products.value = _products.value.filterNot { it.id == productId }
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

  fun resetToDefaults() {
    _products.value = initialProducts
    _categories.value = initialCategories
    _orders.value = initialOrders
    _coupons.value = initialCoupons
    _banners.value = initialBanners
    _storeConfig.value = StoreConfig()
  }
}
