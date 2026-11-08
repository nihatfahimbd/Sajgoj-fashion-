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
  val ageGroups: List<String> = emptyList()
)

data class Category(
  val id: String,
  val name: String,
  val icon: String
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
  val trackingId: String = ""
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
  val returnPolicyDays: Int = 7
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

