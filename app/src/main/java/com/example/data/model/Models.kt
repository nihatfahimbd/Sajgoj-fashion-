package com.example.data.model

data class Product(
  val id: String,
  val name: String,
  val price: Int,
  val stock: Int,
  val category: String,
  val imageUrl: String,
  val sku: String,
  val description: String,
  val rating: Float = 4.8f,
  val reviewsCount: Int = 18,
  val sourceUrl: String = "",
  val colors: List<String> = emptyList(),
  val sizes: List<String> = emptyList(),
  val ageGroups: List<String> = emptyList(),
  val stockUpdatedAt: Long = 0L,
  val lastSyncSource: String = "",
  val isHotSale: Boolean = false,
  val isBestDeal: Boolean = false,
  val isB2G1: Boolean = false,
  val createdAt: Long = 0L,
  val regularPrice: Int = 0,
  val offerPrice: Int = 0
)

data class HomeSectionConfig(
  val id: String,
  val type: String, // top_banner, voucher_strip, category_slider, new_products, hot_sale, combo_packs, b2g1_offers, best_deals, reels_showcase, all_products, category_products, promo_banner, custom_products
  val title: String,
  val subtitle: String = "",
  val icon: String = "✨",
  val badge: String = "",
  val layout: String = "slider3", // slider3, grid3x2, grid3x3, banner, strip
  val columnsPerRow: Int = 3, // 2, 3, 4 cards per row
  val rowCount: Int = 1, // 1..4 rows/lines
  val displayStyle: String = "slider", // "slider" or "grid"
  val enabled: Boolean = true,
  val isCustom: Boolean = false,
  val categoryName: String = "",
  val productIds: List<String> = emptyList(),
  val imageUrl: String = "",
  val bannerButtonText: String = "অফার দেখুন",
  val bannerLinkCategory: String = "All",
  val maxItems: Int = 12
)

data class ReelItem(
  val id: String,
  val title: String,
  val caption: String = "",
  val videoUrl: String = "",
  val posterUrl: String = "",
  val productId: String = "",
  val views: String = "1.5k",
  val likes: Int = 0,
  val badge: String = "হট ট্রেন্ডিং 🔥",
  val price: Int = 1490,
  val active: Boolean = true,
  val userLiked: Boolean = false
)

data class Category(
  val id: String,
  val name: String,
  val icon: String = "✨",
  val imageUrl: String = "",
  val type: String = "main", // "parent", "main", "sub"
  val parentId: String = "",
  val badge: String = "",
  val showInSlider: Boolean = true
)

data class CartItem(
  val product: Product,
  val quantity: Int,
  val selectedColor: String = "",
  val selectedSize: String = "",
  val selectedAgeGroup: String = ""
) {
  val itemKey: String get() = "${product.id}_${selectedColor}_${selectedSize}_${selectedAgeGroup}"
}

data class OrderItem(
  val productId: String,
  val name: String,
  val quantity: Int,
  val price: Int,
  val color: String = "",
  val size: String = "",
  val ageGroup: String = ""
)

data class Order(
  val id: String,
  val customerName: String,
  val phone: String,
  val district: String,
  val thana: String,
  val address: String,
  val items: List<OrderItem>,
  val subtotal: Int,
  val deliveryCharge: Int,
  val discount: Int,
  val couponCode: String = "",
  val total: Int,
  val paymentMethod: String,
  val status: String, // Pending, Confirmed, Shipped, Delivered, Cancelled
  val date: String,
  val trackingId: String = "",
  val stockRestockedOnCancel: Boolean = false
)

data class Coupon(
  val id: String,
  val code: String,
  val type: String, // "%" or "Flat"
  val value: Int,
  val minOrder: Int,
  val expiry: String,
  val isActive: Boolean = true
)

data class BannerItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val buttonText: String,
  val imageUrl: String,
  val isActive: Boolean = true
)

data class StoreDesignConfig(
  val useCustomPageBg: Boolean = false,
  val pageBgStart: String = "#FFFBF7",
  val pageBgEnd: String = "#F6EEE4",
  val useCustomBlockBg: Boolean = false,
  val blockBgColor: String = "#FFFFFF",
  val blockBorderColor: String = "#E5C158",
  val blockTitleBg: String = "#0F294A",
  val blockTitleText: String = "#FFFFFF",
  val seeMoreBg: String = "#0F294A",
  val seeMoreText: String = "#FFFFFF",
  val reelsCaptionBg: String = "#DC2626",
  val reelsCaptionText: String = "#FFFFFF",
  val reelsCaptionLabel: String = "পন্যের ভিডিও দেখে অর্ডার করুন",
  val cardBodyBg: String = "#0F294A",
  val cardBodyBgEnd: String = "#1E3A8A",
  val cardBorderColor: String = "#FFFFFF",
  val cardTitleBg: String = "#EF4444",
  val cardTitleBgEnd: String = "#DC2626",
  val cardTitleText: String = "#FFFFFF",
  val cardPriceBg: String = "#FFF8F0",
  val cardPriceBorder: String = "#F3D5B5",
  val cardRegularPriceColor: String = "#0F294A",
  val cardOfferPriceColor: String = "#DC2626",
  val cardBtnStyle: String = "pill", // "pill", "rounded", "square", "outline"
  val cardBtnBg: String = "#FB7185",
  val cardBtnBgEnd: String = "#E11D48",
  val cardBtnText: String = "#FFFFFF",
  val cardActionLabel: String = "পন্যটি দেখুন",
  val cardWishlistCircleBg: String = "#FFFFFF",
  val cardWishlistActiveBg: String = "#E11D48",
  val cardWishlistIcon: String = "❤️",
  val useCustomHeaderColors: Boolean = false,
  val announcementBg: String = "#7A1C2B",
  val announcementTextColor: String = "#FFFBF7",
  val headerBg: String = "#FFFFFF",
  val headerTextColor: String = "#7A1C2B",
  val cartBg: String = "#7A1C2B",
  val cartTextColor: String = "#FFFFFF",
  val cartBadgeBg: String = "#D4AF37",
  val cartBadgeText: String = "#2A120E",
  val wishlistBg: String = "#FCA5A5",
  val wishlistTextColor: String = "#000000"
)

data class StoreConfig(
  val storeName: String = "SAJGOJ FASHION",
  val tagline: String = "ঐতিহ্যবাহী আভিজাত্য, আধুনিক সাজ",
  val announcementText: String = "🔥 Eid Special: ২০০০ টাকার অর্ডারে সারা দেশে ডেলিভারি সম্পূর্ণ ফ্রি!",
  val isAnnouncementEnabled: Boolean = true,
  val phone: String = "018051198888",
  val whatsapp: String = "88018051198888",
  val email: String = "contact@sajgoj.com",
  val address: String = "Shop 12, Eastern Plaza, Hatirpool, Dhaka-1205",
  val bkashEnabled: Boolean = true,
  val nagadEnabled: Boolean = true,
  val rocketEnabled: Boolean = true,
  val codEnabled: Boolean = true,
  val visaEnabled: Boolean = true,
  val returnPolicyDays: Int = 7,
  val design: StoreDesignConfig = StoreDesignConfig()
)

data class CourierConfig(
  val steadfastApiKey: String = "st_live_983204928340",
  val steadfastSecret: String = "••••••••••••••••",
  val pathaoClientId: String = "pathao_user_883",
  val pathaoClientSecret: String = "••••••••••••"
)

data class PixelConfig(
  val isEnabled: Boolean = true,
  val pixelId: String = "123456789012345",
  val pageView: Boolean = true,
  val addToCart: Boolean = true,
  val purchase: Boolean = true
)

data class ProductReview(
  val id: String = "",
  val productId: String = "",
  val customerName: String = "",
  val customerPhone: String = "",
  val orderId: String = "",
  val rating: Int = 5,
  val comment: String = "",
  val image: String = "",
  val date: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val verified: Boolean = true
)

data class ComboPack(
  val id: String = "",
  val title: String = "",
  val subtitle: String = "",
  val description: String = "",
  val badge: String = "স্পেশাল কম্বো",
  val imageUrl: String = "",
  val productIds: List<String> = emptyList(),
  val regularPrice: Int = 0,
  val comboPrice: Int = 0,
  val savings: Int = 0,
  val isActive: Boolean = true,
  val stock: Int = 5
)

data class ReturnItem(
  val productId: String,
  val name: String,
  val price: Int,
  val quantity: Int,
  val condition: String = "ভালো (বিক্রিযোগ্য)",
  val restock: Boolean = true,
  val color: String = "",
  val size: String = ""
)

data class ExchangeItem(
  val productId: String,
  val name: String,
  val price: Int,
  val quantity: Int,
  val color: String = "",
  val size: String = "",
  val sku: String = ""
)

data class ReturnRecord(
  val id: String,
  val orderId: String,
  val customerName: String,
  val phone: String,
  val date: String,
  val returnedItems: List<ReturnItem>,
  val totalReturnValue: Int,
  val isExchange: Boolean,
  val exchangeItems: List<ExchangeItem> = emptyList(),
  val totalExchangeValue: Int = 0,
  val deliveryFee: Int = 0,
  val priceDifference: Int = 0, // (totalExchangeValue + deliveryFee) - totalReturnValue
  val differenceType: String = "even_exchange", // customer_paid_extra, customer_refunded_balance, even_exchange, full_refund
  val settlementMethod: String = "Cash",
  val settlementTrxId: String = "",
  val settlementStatus: String = "paid",
  val reason: String = "পণ্য পরিবর্তন / অপছন্দ",
  val note: String = ""
)

data class PosInventoryItem(
  val productId: String = "",
  val name: String = "",
  val sku: String = "",
  val stock: Int = 0,
  val price: Int = 0,
  val updatedAt: Long = 0L,
  val lastSource: String = "STOREFRONT_SYNC",
  val lastOrigin: String = "",
  val lastReferenceId: String = ""
)

data class InventorySyncEvent(
  val id: String = "",
  val productId: String = "",
  val productName: String = "",
  val sku: String = "",
  val previousStock: Int = 0,
  val newStock: Int = 0,
  val delta: Int = 0,
  val source: String = "", // STOREFRONT_SALE, POS_SALE, RETURN_RESTOCK, RETURN_EXCHANGE, ORDER_CANCELLED_RESTOCK, RECONCILE
  val reason: String = "",
  val referenceId: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

data class SyncErrorLog(
  val id: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val source: String = "FIRESTORE_SYNC", // LOCAL_POS_STORAGE, FIRESTORE_POS_LISTENER, FIRESTORE_EVENTS_LISTENER, FIRESTORE_PRODUCTS_LISTENER, FIRESTORE_WRITE, RECONCILE
  val severity: String = "ERROR", // ERROR, WARNING
  val code: String = "",
  val message: String = "",
  val details: String = ""
)

data class SyncHealthState(
  val status: String = "CONNECTED", // CONNECTED, SYNCING, LOCAL_ONLY, ERROR
  val lastSyncTimestamp: Long = System.currentTimeMillis(),
  val lastLocalSyncTimestamp: Long = lastSyncTimestamp,
  val lastFirestoreSyncTimestamp: Long = 0L,
  val firestoreConnected: Boolean = false,
  val errorCount: Int = 0,
  val totalSyncedEvents: Int = 0,
  val storefrontProductCount: Int = 0,
  val posProductCount: Int = 0,
  val inSyncCount: Int = 0,
  val driftCount: Int = 0
)

