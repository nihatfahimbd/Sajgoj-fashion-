package com.example

import com.example.data.model.Order
import com.example.sms.SmsHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsHelperTest {

  @Test
  fun normalizePhone_formatsBangladeshNumbers() {
    assertEquals("+8801805119888", SmsHelper.normalizePhone("01805119888"))
    assertEquals("+8801711223344", SmsHelper.normalizePhone("8801711-223 344"))
    assertEquals("+8801912345678", SmsHelper.normalizePhone("+8801912345678"))
    assertEquals("", SmsHelper.normalizePhone("   "))
  }

  @Test
  fun isValidPhone_validatesDigitLengthRange() {
    assertTrue(SmsHelper.isValidPhone("+8801805119888"))
    assertTrue(SmsHelper.isValidPhone("01711223344"))
    assertFalse(SmsHelper.isValidPhone("12345"))
    assertFalse(SmsHelper.isValidPhone(""))
  }

  @Test
  fun estimateSmsParts_handlesAsciiAndBanglaUnicode() {
    assertEquals(0, SmsHelper.estimateSmsParts(""))
    assertEquals(1, SmsHelper.estimateSmsParts("Hello Sajgoj Order Confirmed"))
    assertEquals(2, SmsHelper.estimateSmsParts("A".repeat(161)))
    assertTrue(SmsHelper.containsNonAscii("সালাম"))
    assertEquals(1, SmsHelper.estimateSmsParts("প".repeat(70)))
    assertEquals(2, SmsHelper.estimateSmsParts("প".repeat(71)))
  }

  @Test
  fun formatTemplate_replacesPlaceholdersGracefully() {
    val msg = SmsHelper.formatTemplate(
      template = "প্রিয় {name}, অর্ডার #{orderId} (৳{total}) ট্র্যাকিং: {trackingId} - {store}",
      customerName = "",
      orderId = "ORD-101",
      total = 2500,
      trackingId = "",
      storeName = "SAJGOJ FASHION"
    )
    assertTrue(msg.contains("সম্মানিত গ্রাহক"))
    assertTrue(msg.contains("#ORD-101"))
    assertTrue(msg.contains("৳2500"))
    assertTrue(msg.contains("প্রসেসিং"))
    assertTrue(msg.contains("SAJGOJ FASHION"))
  }

  @Test
  fun buildOrderStatusSms_generatesTrackingAwareShippedMessage() {
    val order = Order(
      id = "ORD-55",
      customerName = "নাফিসা তাসনিম",
      phone = "01805119888",
      district = "Dhaka",
      thana = "Dhanmondi",
      address = "Dhaka",
      items = emptyList(),
      subtotal = 3140,
      deliveryCharge = 60,
      discount = 0,
      total = 3200,
      paymentMethod = "COD",
      status = "Shipped",
      date = "2026-11-20",
      trackingId = "ST-902110"
    )
    val sms = SmsHelper.buildOrderStatusSms(order, "SAJGOJ FASHION")
    assertTrue(sms.contains("নাফিসা তাসনিম"))
    assertTrue(sms.contains("#ORD-55"))
    assertTrue(sms.contains("ST-902110"))
    assertTrue(sms.contains("SAJGOJ FASHION"))
  }
}
