package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BannerItem
import com.example.data.model.CartItem
import com.example.data.model.Category
import com.example.data.model.ComboPack
import com.example.data.model.Coupon
import com.example.data.model.CourierConfig
import com.example.data.model.HomeSectionConfig
import com.example.data.model.InventorySyncEvent
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.PixelConfig
import com.example.data.model.PosInventoryItem
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.data.model.ReelItem
import com.example.data.model.ReturnRecord
import com.example.data.model.StoreConfig
import com.example.data.model.SyncErrorLog
import com.example.data.model.SyncHealthState
import com.example.data.repository.StoreRepository
import com.example.util.VisualImageSearchHelper
import com.example.util.VisualSearchAnalysis
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

data class ImageSearchState(
  val imageUri: String,
  val fileName: String,
  val colorLabelBn: String,
  val colorHex: Long,
  val matchedIds: List<String>,
  val scores: Map<String, Int>
)

enum class AppScreen {
  STOREFRONT,
  ADMIN_PANEL
}

enum class AdminTab(val title: String, val icon: String) {
  DASHBOARD("ড্যাশবোর্ড", "📊"),
  HOME_PAGE_SETTING("Home Page Setting", "🏠"),
  PRODUCTS("প্রোডাক্ট", "👗"),
  ORDERS("অর্ডার", "📦"),
  MARKETING("মার্কেটিং", "🎁"),
  SETTINGS("সেটিংস", "⚙️")
}

class StoreViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = StoreRepository(application)

  val products: StateFlow<List<Product>> = repository.products
  val reviews: StateFlow<List<ProductReview>> = repository.reviews
  val categories: StateFlow<List<Category>> = repository.categories
  val cart: StateFlow<List<CartItem>> = repository.cart
  val orders: StateFlow<List<Order>> = repository.orders
  val returns: StateFlow<List<ReturnRecord>> = repository.returns
  val coupons: StateFlow<List<Coupon>> = repository.coupons
  val banners: StateFlow<List<BannerItem>> = repository.banners
  val homeSections: StateFlow<List<HomeSectionConfig>> = repository.homeSections
  val reels: StateFlow<List<ReelItem>> = repository.reels
  val combos: StateFlow<List<ComboPack>> = repository.combos
  val storeConfig: StateFlow<StoreConfig> = repository.storeConfig
  val courierConfig: StateFlow<CourierConfig> = repository.courierConfig
  val pixelConfig: StateFlow<PixelConfig> = repository.pixelConfig
  val posInventory: StateFlow<Map<String, PosInventoryItem>> = repository.posInventory
  val syncEvents: StateFlow<List<InventorySyncEvent>> = repository.syncEvents
  val syncHealth: StateFlow<SyncHealthState> = repository.syncHealth
  val syncErrorLogs: StateFlow<List<SyncErrorLog>> = repository.syncErrorLogs

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

  private val _imageSearchState = MutableStateFlow<ImageSearchState?>(null)
  val imageSearchState: StateFlow<ImageSearchState?> = _imageSearchState.asStateFlow()

  private val _homeScrollTrigger = MutableStateFlow(0)
  val homeScrollTrigger: StateFlow<Int> = _homeScrollTrigger.asStateFlow()

  private val _selectedProduct = MutableStateFlow<Product?>(null)
  val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

  private val _selectedOrderInvoice = MutableStateFlow<Order?>(null)
  val selectedOrderInvoice: StateFlow<Order?> = _selectedOrderInvoice.asStateFlow()

  private val _selectedShippingLabelOrder = MutableStateFlow<Order?>(null)
  val selectedShippingLabelOrder: StateFlow<Order?> = _selectedShippingLabelOrder.asStateFlow()

  private val _isCartOpen = MutableStateFlow(false)
  val isCartOpen: StateFlow<Boolean> = _isCartOpen.asStateFlow()

  private val _isCheckoutOpen = MutableStateFlow(false)
  val isCheckoutOpen: StateFlow<Boolean> = _isCheckoutOpen.asStateFlow()

  private val _toastEvent = MutableSharedFlow<String>()
  val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

  // Filtered Products (supports bilingual voice/text search, category filter, and visual image search)
  val filteredProducts: StateFlow<List<Product>> = combine(
    repository.products,
    _searchQuery,
    _selectedCategory,
    _imageSearchState
  ) { productList, query, cat, imgState ->
    val trimmed = query.trim().lowercase()
    val expandedTerms = if (trimmed.isNotEmpty()) VisualImageSearchHelper.expandSearchAliases(trimmed) else emptyList()

    val filtered = productList.filter { product ->
      val matchesSearch = if (trimmed.isEmpty()) {
        true
      } else {
        val haystack = "${product.name} ${product.sku} ${product.category} ${product.description} ${product.colors.joinToString(" ")}".lowercase()
        haystack.contains(trimmed) || expandedTerms.any { it.isNotEmpty() && haystack.contains(it) }
      }
      val matchesCategory = cat == "All" || product.category == cat
      val matchesImage = imgState == null || imgState.matchedIds.contains(product.id)
      matchesSearch && matchesCategory && matchesImage
    }

    if (imgState != null) {
      filtered.sortedByDescending { imgState.scores[it.id] ?: 0 }
    } else {
      filtered
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

  fun applyImageSearch(uriString: String, fileName: String, analysis: VisualSearchAnalysis) {
    _imageSearchState.value = ImageSearchState(
      imageUri = uriString,
      fileName = fileName,
      colorLabelBn = analysis.colorProfile.labelBn,
      colorHex = analysis.colorProfile.colorHex,
      matchedIds = analysis.matchedProductIds,
      scores = analysis.scores
    )
    _searchQuery.value = ""
    _selectedCategory.value = "All"
    _currentScreen.value = AppScreen.STOREFRONT
    emitToast("🖼️ ছবির রঙ (${analysis.colorProfile.labelBn}) অনুযায়ী ${analysis.matchedProductIds.size}টি পণ্য পাওয়া গেছে!")
  }

  fun clearImageSearch() {
    _imageSearchState.value = null
  }

  fun resetHomeFilters() {
    _searchQuery.value = ""
    _selectedCategory.value = "All"
    _imageSearchState.value = null
    _currentScreen.value = AppScreen.STOREFRONT
    _homeScrollTrigger.value = _homeScrollTrigger.value + 1
    emitToast("🏠 হোম পেজ ও সকল পণ্য প্রদর্শিত হচ্ছে")
  }

  fun handleScannedPayload(rawText: String): Boolean {
    val clean = rawText.trim()
    if (clean.isEmpty()) return false
    val matched = VisualImageSearchHelper.findProductByScanPayload(clean, products.value)
    return if (matched != null) {
      _currentScreen.value = AppScreen.STOREFRONT
      _selectedProduct.value = matched
      emitToast("🎉 পণ্য পাওয়া গেছে: ${matched.name}")
      true
    } else {
      _searchQuery.value = clean
      _selectedCategory.value = "All"
      _imageSearchState.value = null
      _currentScreen.value = AppScreen.STOREFRONT
      emitToast("📷 কোড '$clean' দিয়ে প্রোডাক্ট খোঁজা হচ্ছে...")
      false
    }
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

  fun openShippingLabel(order: Order) {
    _selectedShippingLabelOrder.value = order
  }

  fun closeShippingLabel() {
    _selectedShippingLabelOrder.value = null
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

  fun addToCart(
    product: Product,
    quantity: Int = 1,
    color: String = "",
    size: String = "",
    ageGroup: String = ""
  ) {
    val success = repository.addToCart(
      product = product,
      quantity = quantity,
      color = color,
      size = size,
      ageGroup = ageGroup
    )
    if (success) {
      val variantText = listOfNotNull(
        if (color.isNotBlank()) "কালার: $color" else null,
        if (size.isNotBlank()) "সাইজ: $size" else null,
        if (ageGroup.isNotBlank()) "বয়স: $ageGroup" else null
      ).joinToString(" | ")

      val toastMsg = if (variantText.isNotBlank()) {
        "\"${product.name}\" ($variantText) কার্টে যোগ করা হয়েছে! 🛒"
      } else {
        "\"${product.name}\" কার্টে যোগ করা হয়েছে! 🛒"
      }
      emitToast(toastMsg)
    } else {
      emitToast("দুঃখিত, এই পণ্যটি স্টক আউট!")
    }
  }

  fun addComboToCart(combo: ComboPack) {
    val success = repository.addComboToCart(combo)
    if (success) {
      emitToast("🎁 \"${combo.title}\" কম্বো প্যাক কার্টে যোগ করা হয়েছে! (সেভ ৳${combo.savings}) 🛒")
    } else {
      emitToast("দুঃখিত, এই কম্বো প্যাকটি বর্তমানে স্টক আউট!")
    }
  }

  fun updateCartQty(itemKey: String, delta: Int) {
    repository.updateCartQuantity(itemKey, delta)
  }

  fun removeFromCart(itemKey: String) {
    repository.removeFromCart(itemKey)
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
    if (status == "Cancelled") {
      emitToast("অর্ডার $orderId বাতিল ও স্টক অটোমেটিক স্টোরফ্রন্ট + POS-এ রিস্টক হয়েছে! 🔄")
    } else {
      emitToast("অর্ডার $orderId এর স্ট্যাটাস \"$status\" এ আপডেট করা হয়েছে।")
    }
  }

  fun recordPosSale(
    product: Product,
    quantity: Int,
    customerName: String = "কাউন্টার ক্রেতা (POS)",
    paymentMethod: String = "POS Cash"
  ): Order? {
    val qty = quantity.coerceIn(1, product.stock.coerceAtLeast(1))
    val item = OrderItem(
      productId = product.id,
      name = product.name,
      quantity = qty,
      price = product.price
    )
    val order = repository.recordPosSale(
      items = listOf(item),
      customerName = customerName,
      paymentMethod = paymentMethod
    )
    if (order != null) {
      emitToast("🧾 POS বিক্রয় #${order.id} সম্পন্ন! স্টোরফ্রন্ট ও POS স্টক সিঙ্ক হয়েছে (-${qty} পিস) 🔄")
    }
    return order
  }

  fun reconcileInventoryNow() {
    val count = repository.reconcileInventoryNow()
    emitToast("🔄 দ্বিমুখী সিঙ্ক সম্পন্ন! ${if (count > 0) "$count টি পণ্যের স্টক সমন্বয় হয়েছে" else "সব স্টক ১০০% সিঙ্কড আছে"} ✅")
  }

  fun clearSyncErrorLogs() {
    repository.clearSyncErrorLogs()
    emitToast("🗑️ POS সিঙ্ক ত্রুটির লগ পরিষ্কার করা হয়েছে।")
  }

  fun dispatchCourier(orderId: String, provider: String) {
    val tracking = repository.dispatchToCourier(orderId, provider)
    emitToast("অর্ডার $orderId $provider-এ ডিসপ্যাচ করা হয়েছে! ট্র্যাকিং: $tracking 🚚")
  }

  fun addReturnRecord(record: ReturnRecord) {
    repository.addReturnRecord(record)
    val settlementMsg = if (record.isExchange) {
      if (record.priceDifference > 0) {
        "কাস্টমার থেকে +৳${record.priceDifference} আদায়যোগ্য (${record.settlementMethod})"
      } else if (record.priceDifference < 0) {
        "কাস্টমারকে ৳${kotlin.math.abs(record.priceDifference)} ফেরতযোগ্য (${record.settlementMethod})"
      } else {
        "সমমূল্য পণ্য বিনিময় (০ ৳ ব্যালেন্স)"
      }
    } else {
      "কাস্টমারকে ৳${record.totalReturnValue} ফেরত (${record.settlementMethod})"
    }
    emitToast("🎉 রিটার্ন ও এক্সচেঞ্জ মেমো #${record.id} তৈরি হয়েছে! $settlementMsg")
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

  fun toggleHomeSection(sectionId: String) {
    repository.toggleHomeSection(sectionId)
    emitToast("হোম পেজ ব্লক প্রদর্শনের অবস্থা আপডেট হয়েছে।")
  }

  fun moveHomeSection(sectionId: String, delta: Int) {
    repository.moveHomeSection(sectionId, delta)
  }

  fun updateHomeSection(sectionId: String, transform: (HomeSectionConfig) -> HomeSectionConfig) {
    repository.updateHomeSection(sectionId, transform)
  }

  fun toggleProductInHomeSection(sectionId: String, productId: String) {
    repository.toggleProductInHomeSection(sectionId, productId)
  }

  fun addCustomHomeSection(section: HomeSectionConfig) {
    repository.addCustomHomeSection(section)
    emitToast("নতুন ব্লক \"${section.title}\" হোম পেজে যুক্ত হয়েছে! ✨")
  }

  fun deleteHomeSection(sectionId: String) {
    repository.deleteHomeSection(sectionId)
    emitToast("হোম পেজ ব্লকটি সরানো হয়েছে।")
  }

  fun resetHomeSectionsToDefault() {
    repository.resetHomeSectionsToDefault()
    emitToast("হোম পেজ লেআউট ডিফল্ট অবস্থায় ফিরিয়ে আনা হয়েছে। 🏠")
  }

  fun likeReel(reelId: String) {
    repository.likeReel(reelId)
  }

  fun emitToast(msg: String) {
    viewModelScope.launch {
      _toastEvent.emit(msg)
    }
  }
}
