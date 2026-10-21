package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BannerItem
import com.example.data.model.CartItem
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.CourierConfig
import com.example.data.model.Order
import com.example.data.model.PixelConfig
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.data.model.StoreConfig
import com.example.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
  STOREFRONT,
  ADMIN_PANEL
}

enum class AdminTab(val title: String, val icon: String) {
  DASHBOARD("ড্যাশবোর্ড", "📊"),
  PRODUCTS("পণ্যসমূহ", "👗"),
  ORDERS("অর্ডার", "📦"),
  CATEGORIES("ক্যাটাগরি", "🏷️"),
  COUPONS("কুপন", "🎟️"),
  COURIER("কুরিয়ার", "🚚"),
  BANNERS("ব্যানার", "🖼️"),
  SETTINGS("সেটিংস", "⚙️"),
  BACKUP("ব্যাকআপ", "💾")
}

class StoreViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = StoreRepository(application)

  val products: StateFlow<List<Product>> = repository.products
  val reviews: StateFlow<List<ProductReview>> = repository.reviews
  val categories: StateFlow<List<Category>> = repository.categories
  val cart: StateFlow<List<CartItem>> = repository.cart
  val orders: StateFlow<List<Order>> = repository.orders
  val coupons: StateFlow<List<Coupon>> = repository.coupons
  val banners: StateFlow<List<BannerItem>> = repository.banners
  val storeConfig: StateFlow<StoreConfig> = repository.storeConfig
  val courierConfig: StateFlow<CourierConfig> = repository.courierConfig
  val pixelConfig: StateFlow<PixelConfig> = repository.pixelConfig

  // UI State
  private val _currentScreen = MutableStateFlow(AppScreen.STOREFRONT)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _adminTab = MutableStateFlow(AdminTab.DASHBOARD)
  val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

  private val _isAdminLoggedIn = MutableStateFlow(false)
  val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow("All")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _selectedProduct = MutableStateFlow<Product?>(null)
  val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

  private val _selectedOrderInvoice = MutableStateFlow<Order?>(null)
  val selectedOrderInvoice: StateFlow<Order?> = _selectedOrderInvoice.asStateFlow()

  private val _isCartOpen = MutableStateFlow(false)
  val isCartOpen: StateFlow<Boolean> = _isCartOpen.asStateFlow()

  private val _isCheckoutOpen = MutableStateFlow(false)
  val isCheckoutOpen: StateFlow<Boolean> = _isCheckoutOpen.asStateFlow()

  private val _toastEvent = MutableSharedFlow<String>()
  val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

  // Filtered Products
  val filteredProducts: StateFlow<List<Product>> = combine(
    repository.products,
    _searchQuery,
    _selectedCategory
  ) { productList, query, cat ->
    productList.filter { product ->
      val matchesSearch = query.isBlank() ||
          product.name.contains(query, ignoreCase = true) ||
          product.sku.contains(query, ignoreCase = true) ||
          product.category.contains(query, ignoreCase = true)
      val matchesCategory = cat == "All" || product.category == cat
      matchesSearch && matchesCategory
    }
  }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  fun setScreen(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun setAdminTab(tab: AdminTab) {
    _adminTab.value = tab
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedCategory(category: String) {
    _selectedCategory.value = category
  }

  fun openProductDetail(product: Product) {
    _selectedProduct.value = product
  }

  fun closeProductDetail() {
    _selectedProduct.value = null
  }

  fun openInvoice(order: Order) {
    _selectedOrderInvoice.value = order
  }

  fun closeInvoice() {
    _selectedOrderInvoice.value = null
  }

  fun setCartOpen(open: Boolean) {
    _isCartOpen.value = open
  }

  fun setCheckoutOpen(open: Boolean) {
    _isCheckoutOpen.value = open
  }

  fun loginAdmin(password: String): Boolean {
    if (password == "admin123" || password == "sajgoj2026") {
      _isAdminLoggedIn.value = true
      _currentScreen.value = AppScreen.ADMIN_PANEL
      emitToast("স্বাগতম! অ্যাডমিন প্যানেল আনলক করা হয়েছে।")
      return true
    } else {
      emitToast("ভুল পাসওয়ার্ড! অনুগ্রহ করে সঠিক পাসওয়ার্ড দিন।")
      return false
    }
  }

  fun logoutAdmin() {
    _isAdminLoggedIn.value = false
    _currentScreen.value = AppScreen.STOREFRONT
    emitToast("অ্যাডমিন প্যানেল থেকে লগআউট সম্পন্ন হয়েছে।")
  }

  fun addToCart(product: Product, quantity: Int = 1) {
    val success = repository.addToCart(product, quantity)
    if (success) {
      emitToast("\"${product.name}\" কার্টে যোগ করা হয়েছে! 🛒")
    } else {
      emitToast("দুঃখিত, এই পণ্যটি স্টক আউট!")
    }
  }

  fun updateCartQty(productId: String, delta: Int) {
    repository.updateCartQuantity(productId, delta)
  }

  fun removeFromCart(productId: String) {
    repository.removeFromCart(productId)
    emitToast("পণ্যটি কার্ট থেকে সরানো হয়েছে।")
  }

  fun clearCart() {
    repository.clearCart()
  }

  fun addReview(productId: String, customerName: String, rating: Int, comment: String) {
    val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    val review = ProductReview(
      id = "rev-${System.currentTimeMillis()}",
      productId = productId,
      customerName = customerName.ifBlank { "ভেরিফাইড ক্রেতা" },
      rating = rating.coerceIn(1, 5),
      comment = comment,
      date = dateStr,
      createdAt = System.currentTimeMillis(),
      verified = true
    )
    repository.addReview(review)
    emitToast("ধন্যবাদ! আপনার মূল্যবান রিভিউটি সফলভাবে যুক্ত হয়েছে! ⭐")
  }

  fun placeOrder(
    customerName: String,
    phone: String,
    district: String,
    thana: String,
    address: String,
    paymentMethod: String,
    appliedCoupon: Coupon?
  ): Order? {
    val order = repository.placeOrder(
      customerName = customerName,
      phone = phone,
      district = district,
      thana = thana,
      address = address,
      paymentMethod = paymentMethod,
      appliedCoupon = appliedCoupon
    )
    if (order != null) {
      _isCheckoutOpen.value = false
      _isCartOpen.value = false
      emitToast("ধন্যবাদ ${order.customerName}! আপনার অর্ডার #${order.id} নিশ্চিত হয়েছে 🎉")
    } else {
      emitToast("অর্ডার সম্পন্ন করা সম্ভব হয়নি। কার্ট খালি!")
    }
    return order
  }

  fun updateOrderStatus(orderId: String, status: String) {
    repository.updateOrderStatus(orderId, status)
    emitToast("অর্ডার $orderId এর স্ট্যাটাস \"$status\" এ আপডেট করা হয়েছে।")
  }

  fun dispatchCourier(orderId: String, provider: String) {
    val tracking = repository.dispatchToCourier(orderId, provider)
    emitToast("অর্ডার $orderId $provider-এ ডিসপ্যাচ করা হয়েছে! ট্র্যাকিং: $tracking 🚚")
  }

  fun addProduct(product: Product) {
    repository.addProduct(product)
    emitToast("নতুন পণ্য \"${product.name}\" সফলভাবে যুক্ত হয়েছে! ✨")
  }

  fun updateProduct(product: Product) {
    repository.updateProduct(product)
    emitToast("পণ্য আপডেট সম্পন্ন হয়েছে! ✅")
  }

  fun deleteProduct(productId: String) {
    repository.deleteProduct(productId)
    emitToast("পণ্যটি মুছে ফেলা হয়েছে।")
  }

  fun addCategory(category: Category) {
    repository.addCategory(category)
    emitToast("নতুন ক্যাটাগরি \"${category.name}\" যুক্ত হয়েছে! 🏷️")
  }

  fun deleteCategory(categoryId: String) {
    repository.deleteCategory(categoryId)
    emitToast("ক্যাটাগরি মুছে ফেলা হয়েছে।")
  }

  fun addCoupon(coupon: Coupon) {
    repository.addCoupon(coupon)
    emitToast("কুপন কোড \"${coupon.code}\" যোগ করা হয়েছে! 🎟️")
  }

  fun toggleCoupon(couponId: String) {
    repository.toggleCoupon(couponId)
    emitToast("কুপন স্ট্যাটাস পরিবর্তন করা হয়েছে।")
  }

  fun deleteCoupon(couponId: String) {
    repository.deleteCoupon(couponId)
    emitToast("কুপন মুছে ফেলা হয়েছে।")
  }

  fun addBanner(banner: BannerItem) {
    repository.addBanner(banner)
    emitToast("নতুন ব্যানার যুক্ত হয়েছে! 🖼️")
  }

  fun toggleBanner(bannerId: String) {
    repository.toggleBanner(bannerId)
    emitToast("ব্যানার সক্রিয়/নিষ্ক্রিয় করা হয়েছে।")
  }

  fun deleteBanner(bannerId: String) {
    repository.deleteBanner(bannerId)
    emitToast("ব্যানার মুছে ফেলা হয়েছে।")
  }

  fun updateStoreConfig(config: StoreConfig) {
    repository.updateStoreConfig(config)
    emitToast("স্টোর সেটিংস সফলভাবে সংরক্ষিত হয়েছে! ⚙️")
  }

  fun updateCourierConfig(config: CourierConfig) {
    repository.updateCourierConfig(config)
    emitToast("কুরিয়ার কনফিগারেশন সংরক্ষিত হয়েছে! 🚚")
  }

  fun updatePixelConfig(config: PixelConfig) {
    repository.updatePixelConfig(config)
    emitToast("পিক্সেল ট্র্যাকিং সেটিংস আপডেট হয়েছে! 📊")
  }

  fun resetToDefaults() {
    repository.resetToDefaults()
    emitToast("স্টোরের সকল ডাটা ডিফল্ট অবস্থায় ফিরিয়ে আনা হয়েছে।")
  }

  fun emitToast(msg: String) {
    viewModelScope.launch {
      _toastEvent.emit(msg)
    }
  }
}
