package com.example

import com.example.data.model.Coupon
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.ui.components.FaqBotEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeFaqBotEngineTest {

  private val sampleConfig = StoreConfig(
    storeName = "SAJGOJ FASHION",
    phone = "+8801805119888",
    whatsapp = "+8801805119888",
    returnPolicyDays = 7
  )

  private val sampleOrders = listOf(
    Order(
      id = "ORD-1002",
      customerName = "সাদিয়া ইসলাম",
      phone = "01712345678",
      district = "ঢাকা",
      thana = "ধানমন্ডি",
      address = "বাড়ি ১২",
      items = emptyList(),
      subtotal = 2400,
      deliveryCharge = 0,
      discount = 0,
      total = 2400,
      paymentMethod = "COD",
      status = "Shipped",
      date = "2026-11-22",
      trackingId = "SF-998877"
    )
  )

  private val sampleCoupons = listOf(
    Coupon(id = "c1", code = "EID2026", type = "%", value = 10, minOrder = 1500, expiry = "2026-12-31", isActive = true),
    Coupon(id = "c2", code = "OLD10", type = "Flat", value = 100, minOrder = 1000, expiry = "2025-01-01", isActive = false)
  )

  private val sampleProducts = listOf(
    Product(
      id = "p1",
      name = "জামদানি শাড়ি গোল্ডেন",
      price = 2500,
      stock = 8,
      category = "শাড়ি",
      imageUrl = "",
      sku = "SAREE-001",
      description = "",
      regularPrice = 3000,
      enabledForWeb = true,
      isDraft = false
    )
  )

  @Test
  fun returnPolicyQuery_returnsConfiguredDaysAndSupportActions() {
    val msg = FaqBotEngine.reply("রিটার্ন ও রিফান্ড পলিসি কী?", sampleConfig, sampleOrders, sampleCoupons, sampleProducts)
    assertTrue(msg.text.contains("7 দিন"))
    assertTrue(msg.showSupportActions)
  }

  @Test
  fun deliveryChargeQuery_returnsDynamicShippingFees() {
    val msg = FaqBotEngine.reply("ডেলিভারি চার্জ কত?", sampleConfig, sampleOrders, sampleCoupons, sampleProducts)
    assertTrue(msg.text.contains("৳60"))
    assertTrue(msg.text.contains("৳120"))
    assertTrue(msg.text.contains("৳2000"))
  }

  @Test
  fun orderIdQuery_returnsMatchingLiveOrder() {
    val msg = FaqBotEngine.reply("ORD-1002", sampleConfig, sampleOrders, sampleCoupons, sampleProducts)
    assertEquals(1, msg.matchedOrders.size)
    assertEquals("ORD-1002", msg.matchedOrders.first().id)
    assertEquals("Shipped", msg.matchedOrders.first().status)
  }

  @Test
  fun couponQuery_returnsOnlyActiveCoupons() {
    val msg = FaqBotEngine.reply("কুপন কোড আছে?", sampleConfig, sampleOrders, sampleCoupons, sampleProducts)
    assertEquals(1, msg.coupons.size)
    assertEquals("EID2026", msg.coupons.first().code)
  }

  @Test
  fun productNameQuery_returnsMatchingProduct() {
    val msg = FaqBotEngine.reply("জামদানি শাড়ি", sampleConfig, sampleOrders, sampleCoupons, sampleProducts)
    assertEquals(1, msg.matchedProducts.size)
    assertEquals("p1", msg.matchedProducts.first().id)
  }

  @Test
  fun contactQuery_includesPhoneAndMapAction() {
    val msg = FaqBotEngine.reply("শোরুম ঠিকানা ও যোগাযোগ নম্বর", sampleConfig, sampleOrders, sampleCoupons, sampleProducts)
    assertTrue(msg.text.contains("+8801805119888"))
    assertTrue(msg.showSupportActions)
    assertTrue(msg.showMapAction)
  }
}
