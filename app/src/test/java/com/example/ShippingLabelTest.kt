package com.example

import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.StoreConfig
import com.example.ui.components.buildShippingQrPayload
import com.example.util.QrCodeGenerator
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShippingLabelTest {

  private fun sampleShippedOrder() = Order(
    id = "ORD-7788",
    customerName = "তানজিলা আক্তার",
    phone = "01712345678",
    district = "Dhaka",
    thana = "Dhanmondi",
    address = "House 14, Road 6",
    items = listOf(
      OrderItem(
        productId = "p1",
        name = "এক্সক্লুসিভ কাতান শাড়ি",
        quantity = 2,
        price = 2500,
        color = "Maroon"
      )
    ),
    subtotal = 5000,
    deliveryCharge = 80,
    discount = 0,
    total = 5080,
    paymentMethod = "Cash on Delivery",
    status = "Shipped",
    date = "2026-11-15",
    trackingId = "STF-998877"
  )

  @Test
  fun buildShippingQrPayload_containsRequiredOrderAndCustomerFields() {
    val order = sampleShippedOrder()
    val config = StoreConfig(storeName = "SAJGOJ FASHION")
    val payload = buildShippingQrPayload(order, config)

    val parsed = JSONObject(payload)
    assertEquals("SAJGOJ_SHIPPING_LABEL", parsed.getString("type"))
    assertEquals("ORD-7788", parsed.getString("orderId"))
    assertEquals("STF-998877", parsed.getString("tracking"))
    assertEquals("তানজিলা আক্তার", parsed.getString("customer"))
    assertEquals("01712345678", parsed.getString("phone"))
    assertEquals("Dhaka", parsed.getString("district"))
    assertEquals(5080, parsed.getInt("total"))
    assertEquals("Shipped", parsed.getString("status"))
  }

  @Test
  fun qrCodeGenerator_producesValidFinderTimingAndBitmap() {
    val payload = buildShippingQrPayload(sampleShippedOrder(), StoreConfig())
    val matrix = QrCodeGenerator.encode(payload)

    assertTrue("QR size should be at least 21x21", matrix.size >= 21)
    assertEquals(0, (matrix.size - 21) % 4)

    // Verify top-left 7x7 finder center (3,3) and outer ring
    assertTrue(matrix.get(3, 3))
    assertTrue(matrix.get(0, 0))
    assertFalse(matrix.get(1, 1))
    assertTrue(matrix.get(7, 0).not()) // white separator

    // Verify top-right and bottom-left finder centers
    assertTrue(matrix.get(matrix.size - 4, 3))
    assertTrue(matrix.get(3, matrix.size - 4))

    // Verify fixed dark module
    assertTrue(matrix.get(8, matrix.size - 8))

    // Verify Bitmap generation works cleanly
    val bmp = matrix.toBitmap(pixelSize = 240, quietZoneModules = 2)
    assertTrue(bmp.width >= 200)
    assertEquals(bmp.width, bmp.height)
  }
}
