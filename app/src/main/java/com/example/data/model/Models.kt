package com.example.data.model

data class ProductVariation(
  val id: String = "",
  val size: String = "",
  val color: String = "",
  val ageGroup: String = "",
  val price: Int = 0,
  val specialPrice: Int = 0,
  val specialPriceStart: String = "",
  val specialPriceEnd: String = "",
  val stock: Int = 0,
  val enabled: Boolean = true,
  val sku: String = ""
)

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
  val offerPrice: Int = 0,
  val videoEnabled: Boolean = false,
  val videoType: String = "upload", // "upload", "youtube", "facebook_reels"
  val videoUrl: String = "",
  val videoThumbUrl: String = "",
  val additionalImages: List<String> = emptyList(),
  val brand: String = "No Brand",
  val attributes: Map<String, String> = emptyMap(),
  val isAvailable: Boolean = true,
  val isDraft: Boolean = false,
  val specialPriceEnabled: Boolean = false,
  val specialPriceStart: String = "",
  val specialPriceEnd: String = "",
  val shippingWeightKg: String = "",
  val shippingDimensions: String = "",
  val shippingDangerousGoods: String = "None",
  val shippingExpressChandpur: Boolean = true,
  val shippingNote: String = "",
  val warrantyType: String = "No Warranty",
  val warrantyPeriod: String = "",
  val warrantyPolicy: String = "",
  val highlights: List<String> = emptyList(),
  val enabledForWeb: Boolean = true,
  val enabledForPos: Boolean = true,
  val variations: List<ProductVariation> = emptyList(),
  val variantImages: Map<String, String> = emptyMap(),
  val variantOrder: List<String> = listOf("Size", "Color Family"),
  val variantImageType: String = "Color Family"
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
  val selectedAgeGroup: String = "",
  val isFreeGift: Boolean = false,
  val freeGiftOriginId: String = "",
  val originalPrice: Int = 0,
  val variantPrice: Int = 0,
  val variantImageUrl: String = ""
) {
  val itemKey: String
    get() = if (isFreeGift) {
      "FREE_GIFT_${product.id}"
    } else {
      "${product.id}_${selectedColor}_${selectedSize}_${selectedAgeGroup}"
    }

  val effectiveUnitPrice: Int
    get() = when {
      isFreeGift -> 0
      variantPrice > 0 -> variantPrice
      else -> product.price
    }

  val effectiveImageUrl: String
    get() = variantImageUrl.ifBlank { product.imageUrl }
}

data class VariantSummaryResult(
  val totalStock: Int,
  val minRegularPrice: Int,
  val minOfferPrice: Int,
  val effectiveSellingPrice: Int,
  val specialPriceEnabled: Boolean,
  val specialPriceStart: String,
  val specialPriceEnd: String
)

object ProductVariantHelper {
  fun findVariation(
    product: Product,
    color: String,
    size: String,
    ageGroup: String = ""
  ): ProductVariation? {
    if (product.variations.isEmpty()) return null
    val normC = color.trim()
    val normS = size.trim()
    val normA = ageGroup.trim()
    return product.variations.firstOrNull { v ->
      (v.color.isBlank() || normC.isBlank() || v.color.equals(normC, ignoreCase = true)) &&
        (v.size.isBlank() || normS.isBlank() || v.size.equals(normS, ignoreCase = true)) &&
        (v.ageGroup.isBlank() || normA.isBlank() || v.ageGroup.equals(normA, ignoreCase = true)) &&
        (v.color.equals(normC, ignoreCase = true) || (v.color.isBlank() && normC.isBlank())) &&
        (v.size.equals(normS, ignoreCase = true) || (v.size.isBlank() && normS.isBlank()))
    } ?: product.variations.firstOrNull { v ->
      val cMatch = normC.isBlank() || v.color.equals(normC, ignoreCase = true)
      val sMatch = normS.isBlank() || v.size.equals(normS, ignoreCase = true)
      cMatch && sMatch
    }
  }

  fun isWithinDatePeriod(start: String, end: String, todayIso: String = ""): Boolean {
    val s = start.trim()
    val e = end.trim()
    if (s.isBlank() && e.isBlank()) return true
    val refDate = todayIso.ifBlank {
      try {
        java.time.LocalDate.now().toString()
      } catch (_: Exception) {
        ""
      }
    }
    if (refDate.isBlank()) return true
    if (s.isNotBlank() && refDate < s) return false
    if (e.isNotBlank() && refDate > e) return false
    return true
  }

  fun resolvePricing(
    product: Product,
    color: String,
    size: String,
    ageGroup: String = "",
    todayIso: String = ""
  ): Pair<Int, Int> {
    val matched = findVariation(product, color, size, ageGroup)
    if (matched != null && matched.price > 0) {
      val reg = matched.price
      val start = matched.specialPriceStart.ifBlank { product.specialPriceStart }
      val end = matched.specialPriceEnd.ifBlank { product.specialPriceEnd }
      val validSpecial = matched.specialPrice in 1 until reg && isWithinDatePeriod(start, end, todayIso)
      val sell = if (validSpecial) matched.specialPrice else reg
      return sell to reg
    }
    val reg = if (product.regularPrice > product.price) product.regularPrice else product.price
    val periodOk = isWithinDatePeriod(product.specialPriceStart, product.specialPriceEnd, todayIso)
    val offer = if (product.specialPriceEnabled && product.offerPrice in 1 until reg && periodOk) {
      product.offerPrice
    } else {
      0
    }
    val sell = if (offer > 0) offer else product.price
    return sell to reg
  }

  fun resolveStock(
    product: Product,
    color: String,
    size: String,
    ageGroup: String = ""
  ): Int {
    if (product.variations.isEmpty()) return product.stock.coerceAtLeast(0)
    val matched = findVariation(product, color, size, ageGroup)
    return if (matched != null) {
      if (matched.enabled) matched.stock.coerceAtLeast(0) else 0
    } else {
      product.stock.coerceAtLeast(0)
    }
  }

  fun resolveImage(product: Product, color: String, size: String = ""): String {
    if (color.isNotBlank()) {
      product.variantImages[color]?.takeIf { it.isNotBlank() }?.let { return it }
    }
    if (size.isNotBlank()) {
      product.variantImages[size]?.takeIf { it.isNotBlank() }?.let { return it }
    }
    return product.imageUrl
  }

  fun summarizeVariations(variations: List<ProductVariation>): VariantSummaryResult {
    val active = variations.filter { it.enabled }
    val totalStock = active.sumOf { it.stock.coerceAtLeast(0) }
    val positivePrices = active.mapNotNull { it.price.takeIf { p -> p > 0 } }
    val minReg = positivePrices.minOrNull() ?: 0
    val validSpecials = active.filter { it.price > 0 && it.specialPrice in 1 until it.price }
    val minOffer = validSpecials.minOfOrNull { it.specialPrice } ?: 0
    val firstStart = validSpecials.firstOrNull { it.specialPriceStart.isNotBlank() }?.specialPriceStart ?: ""
    val firstEnd = validSpecials.firstOrNull { it.specialPriceEnd.isNotBlank() }?.specialPriceEnd ?: ""
    val effSelling = if (minOffer > 0) minOffer else minReg
    return VariantSummaryResult(
      totalStock = totalStock,
      minRegularPrice = minReg,
      minOfferPrice = minOffer,
      effectiveSellingPrice = effSelling,
      specialPriceEnabled = minOffer > 0,
      specialPriceStart = firstStart,
      specialPriceEnd = firstEnd
    )
  }

  fun applyVariantStockDeltas(
    product: Product,
    variantDeltas: List<Triple<String, String, Int>>,
    fallbackTotalDelta: Int
  ): Product {
    if (product.variations.isEmpty()) {
      return product.copy(stock = (product.stock + fallbackTotalDelta).coerceAtLeast(0))
    }
    val hasSpecificVariants = variantDeltas.any { it.first.isNotBlank() || it.second.isNotBlank() }
    if (!hasSpecificVariants) {
      return product.copy(stock = (product.stock + fallbackTotalDelta).coerceAtLeast(0))
    }
    var remainingList = product.variations.toMutableList()
    var appliedAny = false
    variantDeltas.forEach { (col, sz, d) ->
      if (d == 0) return@forEach
      val idx = remainingList.indexOfFirst { v ->
        (col.isBlank() || v.color.equals(col, ignoreCase = true)) &&
          (sz.isBlank() || v.size.equals(sz, ignoreCase = true) || v.ageGroup.equals(sz, ignoreCase = true))
      }
      if (idx >= 0) {
        val current = remainingList[idx]
        remainingList[idx] = current.copy(stock = (current.stock + d).coerceAtLeast(0))
        appliedAny = true
      }
    }
    if (!appliedAny) {
      return product.copy(stock = (product.stock + fallbackTotalDelta).coerceAtLeast(0))
    }
    val recalculatedTotal = remainingList.filter { it.enabled }.sumOf { it.stock.coerceAtLeast(0) }
    return product.copy(
      variations = remainingList,
      stock = recalculatedTotal
    )
  }

  fun variationToMap(v: ProductVariation): Map<String, Any> = mapOf(
    "id" to v.id,
    "size" to v.size,
    "color" to v.color,
    "ageGroup" to v.ageGroup,
    "price" to v.price,
    "specialPrice" to v.specialPrice,
    "specialPriceStart" to v.specialPriceStart,
    "specialPriceEnd" to v.specialPriceEnd,
    "stock" to v.stock,
    "enabled" to v.enabled,
    "sku" to v.sku
  )

  fun parseVariations(raw: Any?): List<ProductVariation> {
    val list = raw as? List<*> ?: return emptyList()
    return list.mapIndexedNotNull { idx, item ->
      val m = item as? Map<*, *> ?: return@mapIndexedNotNull null
      ProductVariation(
        id = m["id"]?.toString()?.ifBlank { "var_$idx" } ?: "var_$idx",
        size = m["size"]?.toString() ?: "",
        color = m["color"]?.toString() ?: "",
        ageGroup = m["ageGroup"]?.toString() ?: "",
        price = (m["price"] as? Number)?.toInt() ?: m["price"]?.toString()?.toIntOrNull() ?: 0,
        specialPrice = (m["specialPrice"] as? Number)?.toInt() ?: m["specialPrice"]?.toString()?.toIntOrNull() ?: 0,
        specialPriceStart = m["specialPriceStart"]?.toString() ?: "",
        specialPriceEnd = m["specialPriceEnd"]?.toString() ?: "",
        stock = ((m["stock"] as? Number)?.toInt() ?: m["stock"]?.toString()?.toIntOrNull() ?: 0).coerceAtLeast(0),
        enabled = (m["enabled"] as? Boolean) ?: true,
        sku = m["sku"]?.toString() ?: ""
      )
    }
  }
}

data class OrderItem(
  val productId: String,
  val name: String,
  val quantity: Int,
  val price: Int,
  val color: String = "",
  val size: String = "",
  val ageGroup: String = "",
  val isFreeGift: Boolean = false,
  val freeGiftOriginId: String = ""
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
  val status: String, // Pending, Confirmed, Processing, To Ship, Shipped, Shipping, Delivered, Failed Delivered, Cancelled, Cancellation
  val date: String,
  val trackingId: String = "",
  val stockRestockedOnCancel: Boolean = false,
  val orderType: String = "Normal", // "Normal", "Custom"
  val deliveryType: String = "Standard", // "Standard", "Express" (Express Chandpur exclusive)
  val deliveredBy: String = "", // "", "Pathao", "SteadFast", "Digital"
  val landmark: String = "",
  val addressCategory: String = "Home", // "Home", "Office"
  val defaultShippingAddress: Boolean = false,
  val createdAt: Long = 0L
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

data class ShippingOptionItem(
  val id: String,
  val title: String,
  val subtitle: String = "",
  val badge: String = "",
  val fee: Int = 60,
  val freeAbove: Int = 2000,
  val estimatedDays: String = "২-৪ কার্যদিবস",
  val deliveryType: String = "Standard", // "Standard", "Express"
  val districtRule: String = "ALL", // "ALL", "CHANDPUR_ONLY", "CUSTOM"
  val customDistricts: List<String> = emptyList(),
  val enabled: Boolean = true
)

data class ShippingInfoConfig(
  val sectionTitle: String = "Delivery Type & Shipping Info",
  val chandpurExpressNotice: String = "⚡ Express Delivery শুধুমাত্র চাঁদপুর জেলার জন্য প্রযোজ্য",
  val chandpurStandardFee: Int = 60,
  val sectionBgColor: String = "#F0F9FF",
  val blockBgColor: String = "#FFFFFF",
  val borderColor: String = "#0284C7",
  val textColor: String = "#0F172A",
  val buttonBgColor: String = "#0284C7",
  val buttonTextColor: String = "#FFFFFF",
  val options: List<ShippingOptionItem> = defaultShippingOptions()
)

fun defaultShippingOptions(): List<ShippingOptionItem> = listOf(
  ShippingOptionItem(
    id = "ship-std",
    title = "Standard Delivery",
    subtitle = "সারা বাংলাদেশে ৬৪ জেলায় বিশ্বস্ত হোম ডেলিভারি (২০০০৳+ ফ্রি)",
    badge = "সারা বাংলাদেশ",
    fee = 120,
    freeAbove = 2000,
    estimatedDays = "২-৪ কার্যদিবস",
    deliveryType = "Standard",
    districtRule = "ALL",
    enabled = true
  ),
  ShippingOptionItem(
    id = "ship-exp",
    title = "Express Delivery (Fast)",
    subtitle = "সুপার ফাস্ট একই দিনে / ২৪ ঘন্টায় ডেলিভারি (শুধুমাত্র চাঁদপুর জেলা)",
    badge = "শুধুমাত্র চাঁদপুর",
    fee = 200,
    freeAbove = 0,
    estimatedDays = "২৪ ঘন্টা / একই দিন",
    deliveryType = "Express",
    districtRule = "CHANDPUR_ONLY",
    enabled = true
  )
)

data class B2G1Config(
  val enabled: Boolean = true,
  val title: String = "Buy 2 Get 1 Free অফার",
  val subtitle: String = "নির্বাচিত ২টি পণ্য কার্টে নিলেই পাচ্ছেন ১টি প্রিমিয়াম উপহার একদম ফ্রি!",
  val minItems: Int = 2,
  val freeProductId: String = "p4",
  val eligibleProductIds: List<String> = emptyList()
)

data class StoreConfig(
  val storeName: String = "SAJGOJ FASHION",
  val tagline: String = "ঐতিহ্যবাহী আভিজাত্য, আধুনিক সাজ",
  val announcementText: String = "🔥 Eid Special: ২০০০ টাকার অর্ডারে সারা দেশে ডেলিভারি সম্পূর্ণ ফ্রি!",
  val isAnnouncementEnabled: Boolean = true,
  val phone: String = "+8801805119888",
  val whatsapp: String = "+8801805119888",
  val email: String = "contact@sajgoj.com",
  val address: String = "Shop 12, Eastern Plaza, Hatirpool, Dhaka-1205",
  val showGoogleMap: Boolean = true,
  val googleMapQuery: String = "Eastern Plaza, Hatirpool, Dhaka-1205",
  val googleMapUrl: String = "https://www.google.com/maps/search/?api=1&query=Eastern+Plaza%2C+Hatirpool%2C+Dhaka-1205",
  val bkashEnabled: Boolean = true,
  val nagadEnabled: Boolean = true,
  val rocketEnabled: Boolean = true,
  val codEnabled: Boolean = true,
  val visaEnabled: Boolean = true,
  val returnPolicyDays: Int = 7,
  val smsGatewayDeviceEnabled: Boolean = false,
  val smsAutoSendOnOrderStatus: Boolean = false,
  val smsDelaySeconds: Int = 4,
  val design: StoreDesignConfig = StoreDesignConfig(),
  val shippingInfo: ShippingInfoConfig = ShippingInfoConfig(),
  val b2g1: B2G1Config = B2G1Config(),
  val pageConfig: PageConfiguration = PageConfiguration()
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

data class SmsQueueItem(
  val id: String,
  val phone: String,
  val customerName: String = "",
  val message: String,
  val orderId: String = "",
  val type: String = "custom", // order_alert, status_update, bulk_promo, custom
  val status: String = "pending", // pending, sending, sent, failed, cancelled
  val channel: String = "sim_gateway", // sim_gateway, intent_manual
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val sentAt: Long = 0L,
  val sentByDevice: String = "",
  val errorReason: String = ""
)

data class SmsTemplate(
  val id: String,
  val title: String,
  val type: String,
  val body: String
)

