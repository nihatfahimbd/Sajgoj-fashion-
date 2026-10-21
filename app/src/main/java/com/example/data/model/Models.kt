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
  val sourceUrl: String = ""
)

data class Category(
  val id: String,
  val name: String,
  val icon: String
)

data class CartItem(
  val product: Product,
  val quantity: Int
)

data class OrderItem(
  val productId: String,
  val name: String,
  val quantity: Int,
  val price: Int
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
  val codEnabled: Boolean = true
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
  val rating: Int = 5,
  val comment: String = "",
  val date: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val verified: Boolean = true
)

