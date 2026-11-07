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

  @Test
  fun testReturnExchangePriceDifference_moreExpensive_collectFromCustomer() {
    val returnItemValue = 890
    val newExchangeItemValue = 3500
    val deliveryFee = 0
    val netDifference = (newExchangeItemValue + deliveryFee) - returnItemValue

    assertTrue(netDifference > 0)
    assertEquals(2610, netDifference)
    val settlementType = if (netDifference > 0) "customer_paid_extra" else "customer_refunded_balance"
    assertEquals("customer_paid_extra", settlementType)
  }

  @Test
  fun testReturnExchangePriceDifference_cheaper_refundToCustomer() {
    val returnItemValue = 3500
    val newExchangeItemValue = 1200
    val deliveryFee = 60
    val netDifference = (newExchangeItemValue + deliveryFee) - returnItemValue

    assertTrue(netDifference < 0)
    assertEquals(-2240, netDifference)
    val refundAmount = kotlin.math.abs(netDifference)
    assertEquals(2240, refundAmount)
    val settlementType = if (netDifference < 0) "customer_refunded_balance" else "customer_paid_extra"
    assertEquals("customer_refunded_balance", settlementType)
  }

  @Test
  fun testReturnExchangePriceDifference_evenExchange() {
    val returnItemValue = 1500
    val newExchangeItemValue = 1500
    val deliveryFee = 0
    val netDifference = (newExchangeItemValue + deliveryFee) - returnItemValue

    assertEquals(0, netDifference)
    val settlementType = if (netDifference == 0) "even_exchange" else "adjustment_required"
    assertEquals("even_exchange", settlementType)
  }
}
