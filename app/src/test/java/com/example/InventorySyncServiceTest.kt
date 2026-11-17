package com.example

import com.example.data.model.ExchangeItem
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.PosInventoryItem
import com.example.data.model.Product
import com.example.data.model.ReturnItem
import com.example.data.model.ReturnRecord
import com.example.data.sync.InventorySyncService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InventorySyncServiceTest {

  private fun sampleProducts() = listOf(
    Product(
      id = "p1",
      name = "কাতান শাড়ি",
      price = 3500,
      stock = 10,
      category = "শাড়ি",
      imageUrl = "",
      sku = "SAJ-001",
      description = "",
      stockUpdatedAt = 1000L
    ),
    Product(
      id = "p2",
      name = "কুন্দন চোকার",
      price = 2999,
      stock = 15,
      category = "নেকলেস",
      imageUrl = "",
      sku = "SAJ-002",
      description = "",
      stockUpdatedAt = 1000L
    )
  )

  @Test
  fun storefrontSale_updatesBothStorefrontAndPosInventory() {
    var time = 2000L
    val service = InventorySyncService(firestore = null, clock = { time })
    val initial = sampleProducts()
    service.mirrorCatalogToPos(initial, pushToCloud = false)

    val updated = service.applySaleStockDeduction(
      products = initial,
      items = listOf(OrderItem(productId = "p1", name = "কাতান শাড়ি", quantity = 3, price = 3500)),
      orderId = "ORD-101"
    )

    assertEquals(7, updated.first { it.id == "p1" }.stock)
    assertEquals(7, service.posInventory.value["p1"]?.stock)
    assertEquals(0, service.syncHealth.value.driftCount)
    assertEquals("STOREFRONT_SALE", service.syncEvents.value.first().source)
    assertEquals(-3, service.syncEvents.value.first().delta)
  }

  @Test
  fun posSale_updatesStorefrontProductsAndAuditLog() {
    var time = 3000L
    val service = InventorySyncService(firestore = null, clock = { time })
    val initial = sampleProducts()
    service.mirrorCatalogToPos(initial, pushToCloud = false)

    val updated = service.applyPosSaleDeduction(
      products = initial,
      items = listOf(OrderItem(productId = "p2", name = "কুন্দন চোকার", quantity = 4, price = 2999)),
      saleId = "POS-9001"
    )

    assertEquals(11, updated.first { it.id == "p2" }.stock)
    assertEquals(11, service.posInventory.value["p2"]?.stock)
    assertEquals("POS_SALE", service.syncEvents.value.first().source)
    assertEquals("POS-9001", service.syncEvents.value.first().referenceId)
  }

  @Test
  fun remotePosUpdate_respectsTimestampPrecedence_noEchoOverwrite() {
    val service = InventorySyncService(firestore = null, clock = { 5000L })
    val initial = sampleProducts() // stockUpdatedAt = 1000L

    // Newer POS update -> should update storefront
    val afterNewer = service.applyRemotePosItem(
      currentProducts = initial,
      remoteItem = PosInventoryItem(
        productId = "p1",
        name = "কাতান শাড়ি",
        sku = "SAJ-001",
        stock = 6,
        price = 3500,
        updatedAt = 2500L,
        lastSource = "POS_SALE",
        lastReferenceId = "POS-22"
      )
    )
    assertEquals(6, afterNewer.first { it.id == "p1" }.stock)
    assertEquals(2500L, afterNewer.first { it.id == "p1" }.stockUpdatedAt)

    // Stale POS update (older timestamp) -> should NOT overwrite newer storefront stock
    val afterStale = service.applyRemotePosItem(
      currentProducts = afterNewer,
      remoteItem = PosInventoryItem(
        productId = "p1",
        name = "কাতান শাড়ি",
        sku = "SAJ-001",
        stock = 9,
        price = 3500,
        updatedAt = 1500L,
        lastSource = "POS_SALE"
      )
    )
    assertEquals(6, afterStale.first { it.id == "p1" }.stock)
  }

  @Test
  fun returnAndExchange_restocksSellableAndDeductsExchangeInBothCatalogs() {
    val service = InventorySyncService(firestore = null, clock = { 6000L })
    val initial = sampleProducts()
    service.mirrorCatalogToPos(initial, pushToCloud = false)

    val record = ReturnRecord(
      id = "RET-500",
      orderId = "ORD-101",
      customerName = "তানিয়া",
      phone = "01700000000",
      date = "2026-11-14",
      returnedItems = listOf(
        ReturnItem(productId = "p1", name = "কাতান শাড়ি", price = 3500, quantity = 2, restock = true),
        ReturnItem(productId = "p2", name = "কুন্দন চোকার", price = 2999, quantity = 1, condition = "ক্ষতিগ্রস্ত", restock = false)
      ),
      totalReturnValue = 9999,
      isExchange = true,
      exchangeItems = listOf(
        ExchangeItem(productId = "p2", name = "কুন্দন চোকার", price = 2999, quantity = 2, sku = "SAJ-002")
      ),
      totalExchangeValue = 5998
    )

    val updated = service.applyReturnAndExchange(initial, record)

    // p1: 10 + 2 (restock=true) = 12
    assertEquals(12, updated.first { it.id == "p1" }.stock)
    assertEquals(12, service.posInventory.value["p1"]?.stock)

    // p2: 15 (restock=false on returned item, -2 for exchange) = 13
    assertEquals(13, updated.first { it.id == "p2" }.stock)
    assertEquals(13, service.posInventory.value["p2"]?.stock)
    assertEquals(0, service.syncHealth.value.driftCount)
  }

  @Test
  fun orderCancellation_restoresAndReopenDeductsAcrossBothPlatforms() {
    val service = InventorySyncService(firestore = null, clock = { 7000L })
    val initial = sampleProducts()
    service.mirrorCatalogToPos(initial, pushToCloud = false)

    val order = Order(
      id = "ORD-777",
      customerName = "সাদিয়া",
      phone = "01800000000",
      district = "ঢাকা",
      thana = "ধানমন্ডি",
      address = "রোড ৫",
      items = listOf(OrderItem(productId = "p1", name = "কাতান শাড়ি", quantity = 2, price = 3500)),
      subtotal = 7000,
      deliveryCharge = 0,
      discount = 0,
      total = 7000,
      paymentMethod = "COD",
      status = "Cancelled",
      date = "2026-11-14"
    )

    val afterCancel = service.applyOrderCancellationRestock(initial, order, restoreStock = true)
    assertEquals(12, afterCancel.first { it.id == "p1" }.stock)
    assertEquals(12, service.posInventory.value["p1"]?.stock)

    val afterReopen = service.applyOrderCancellationRestock(afterCancel, order, restoreStock = false)
    assertEquals(10, afterReopen.first { it.id == "p1" }.stock)
    assertEquals(10, service.posInventory.value["p1"]?.stock)
  }

  @Test
  fun reconcileStorefrontAndPos_repairsDriftUsingNewestTimestamp() {
    val service = InventorySyncService(firestore = null, clock = { 9000L })
    val products = sampleProducts()

    // Inject newer POS stock that drifted (stock=4, updatedAt=4000L vs product stock=10, updatedAt=1000L)
    val drifted = service.applyRemotePosItem(
      currentProducts = products.map { it.copy(stockUpdatedAt = 5000L) }, // pretend local was newer for p1, so applyRemotePosItem ignores it
      remoteItem = PosInventoryItem(
        productId = "p1",
        name = "কাতান শাড়ি",
        sku = "SAJ-001",
        stock = 4,
        price = 3500,
        updatedAt = 2000L,
        lastSource = "POS_SALE"
      )
    )
    assertTrue(service.syncHealth.value.driftCount > 0)

    val (reconciled, count) = service.reconcileStorefrontAndPos(drifted)
    assertEquals(2, count)
    assertEquals(10, reconciled.first { it.id == "p1" }.stock)
    assertEquals(10, service.posInventory.value["p1"]?.stock)
    assertEquals(0, service.syncHealth.value.driftCount)
  }

  @Test
  fun syncDiagnostic_capturesErrors_updatesTimestamps_andClearsLogs() {
    var now = 10_000L
    val service = InventorySyncService(firestore = null, clock = { now })
    val products = sampleProducts()

    now = 12_500L
    service.markLocalSync(timestamp = now, products = products)
    assertEquals(12_500L, service.syncHealth.value.lastLocalSyncTimestamp)

    now = 15_000L
    service.markFirestoreSync(timestamp = now, products = products)
    assertEquals(15_000L, service.syncHealth.value.lastFirestoreSyncTimestamp)
    assertEquals(15_000L, service.syncHealth.value.lastSyncTimestamp)

    now = 16_000L
    service.recordSyncError(
      source = "FIRESTORE_WRITE",
      severity = "ERROR",
      code = "PERMISSION_DENIED",
      message = "Missing or insufficient permissions",
      details = "collection=pos_inventory doc=p1",
      products = products
    )

    assertEquals(1, service.syncErrorLogs.value.size)
    assertEquals(1, service.syncHealth.value.errorCount)
    assertEquals("PERMISSION_DENIED", service.syncErrorLogs.value.first().code)
    assertEquals("FIRESTORE_WRITE", service.syncErrorLogs.value.first().source)

    service.clearSyncErrorLogs(products)
    assertEquals(0, service.syncErrorLogs.value.size)
    assertEquals(0, service.syncHealth.value.errorCount)
  }
}
