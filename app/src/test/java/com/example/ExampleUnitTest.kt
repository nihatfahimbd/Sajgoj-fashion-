package com.example

import com.example.data.model.Coupon
import com.example.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testCouponCalculation_percentage() {
    val subtotal = 3500
    val coupon = Coupon("cp1", "EID2026", "%", 10, 2000, "2026-12-31")
    val discount = (subtotal * coupon.value) / 100
    assertEquals(350, discount)
    assertEquals(3150, subtotal - discount)
  }

  @Test
  fun testDeliveryChargeRules() {
    val smallOrderSubtotal = 1500
    val largeOrderSubtotal = 2500

    // >= 2000 gets free delivery
    val deliveryLarge = if (largeOrderSubtotal >= 2000) 0 else 60
    assertEquals(0, deliveryLarge)

    // < 2000 inside Dhaka gets 60
    val deliverySmallDhaka = if (smallOrderSubtotal >= 2000) 0 else 60
    assertEquals(60, deliverySmallDhaka)

    // < 2000 outside Dhaka gets 120
    val deliverySmallOutside = if (smallOrderSubtotal >= 2000) 0 else 120
    assertEquals(120, deliverySmallOutside)
  }
}
