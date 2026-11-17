package com.example

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.OrderItem
import com.example.data.repository.StoreRepository
import com.example.data.sync.LocalInventoryStorageSync
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocalInventoryStorageSyncTest {

  private lateinit var context: Context
  private lateinit var prefs: SharedPreferences

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    prefs = context.getSharedPreferences("sajgoj_store_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()
  }

  private fun readStockFromJsonArray(key: String, productId: String): Int {
    val raw = prefs.getString(key, null) ?: return -1
    val arr = JSONArray(raw)
    for (i in 0 until arr.length()) {
      val obj = arr.optJSONObject(i) ?: continue
      if (obj.optString("id") == productId || obj.optString("productId") == productId) {
        return obj.optInt("stock", -1)
      }
    }
    return -1
  }

  @Test
  fun storefrontOrder_updatesBothWebsiteAndPosLocalStorage() {
    val repo = StoreRepository(context)
    val initialStock = repo.products.value.first { it.id == "p1" }.stock
    assertEquals(initialStock, readStockFromJsonArray(LocalInventoryStorageSync.KEY_WEBSITE_PRODUCTS, "p1"))
    assertEquals(initialStock, readStockFromJsonArray(LocalInventoryStorageSync.KEY_POS_PRODUCTS, "p1"))

    val p1 = repo.products.value.first { it.id == "p1" }
    repo.addToCart(p1, quantity = 2)
    val order = repo.placeOrder(
      customerName = "আয়েশা সিদ্দিকা",
      phone = "01711111111",
      district = "ঢাকা",
      thana = "ধানমন্ডি",
      address = "রোড ৯",
      paymentMethod = "Cash on Delivery",
      appliedCoupon = null
    )
    assertNotNull(order)

    val expectedStock = (initialStock - 2).coerceAtLeast(0)
    assertEquals(expectedStock, repo.products.value.first { it.id == "p1" }.stock)
    assertEquals(expectedStock, repo.posInventory.value["p1"]?.stock)
    assertEquals(expectedStock, readStockFromJsonArray(LocalInventoryStorageSync.KEY_WEBSITE_PRODUCTS, "p1"))
    assertEquals(expectedStock, readStockFromJsonArray(LocalInventoryStorageSync.KEY_POS_PRODUCTS, "p1"))
  }

  @Test
  fun posSale_updatesBothPosAndWebsiteLocalStorage() {
    val repo = StoreRepository(context)
    val initialStock = repo.products.value.first { it.id == "p2" }.stock

    val sale = repo.recordPosSale(
      items = listOf(OrderItem(productId = "p2", name = "রাজওয়াড়ি কুন্দন ব্রাইডাল চোকার সেট", quantity = 3, price = 2999)),
      customerName = "POS ক্রেতা",
      paymentMethod = "POS Cash"
    )
    assertNotNull(sale)

    val expectedStock = (initialStock - 3).coerceAtLeast(0)
    assertEquals(expectedStock, repo.products.value.first { it.id == "p2" }.stock)
    assertEquals(expectedStock, repo.posInventory.value["p2"]?.stock)
    assertEquals(expectedStock, readStockFromJsonArray(LocalInventoryStorageSync.KEY_POS_PRODUCTS, "p2"))
    assertEquals(expectedStock, readStockFromJsonArray(LocalInventoryStorageSync.KEY_WEBSITE_PRODUCTS, "p2"))
  }

  @Test
  fun externalPosLocalStorageWrite_triggersListenerAndUpdatesWebsiteList() {
    val repo = StoreRepository(context)
    val posRaw = prefs.getString(LocalInventoryStorageSync.KEY_POS_PRODUCTS, null)!!
    val arr = JSONArray(posRaw)
    val futureTs = System.currentTimeMillis() + 10_000L

    for (i in 0 until arr.length()) {
      val obj = arr.getJSONObject(i)
      if (obj.optString("id") == "p4" || obj.optString("productId") == "p4") {
        obj.put("stock", 5)
        obj.put("stockUpdatedAt", futureTs)
        obj.put("updatedAt", futureTs)
        obj.put("lastSource", "POS_CHECKOUT")
      }
    }

    // Commit external write to POS local storage key; listener should auto-sync Website list
    prefs.edit().putString(LocalInventoryStorageSync.KEY_POS_PRODUCTS, arr.toString()).commit()

    assertEquals(5, repo.products.value.first { it.id == "p4" }.stock)
    assertEquals(5, repo.posInventory.value["p4"]?.stock)
    assertEquals(5, readStockFromJsonArray(LocalInventoryStorageSync.KEY_WEBSITE_PRODUCTS, "p4"))
  }

  @Test
  fun externalWebsiteLocalStorageWrite_triggersListenerAndUpdatesPosList() {
    val repo = StoreRepository(context)
    val webRaw = prefs.getString(LocalInventoryStorageSync.KEY_WEBSITE_PRODUCTS, null)!!
    val arr = JSONArray(webRaw)
    val futureTs = System.currentTimeMillis() + 10_000L

    for (i in 0 until arr.length()) {
      val obj = arr.getJSONObject(i)
      if (obj.optString("id") == "p5") {
        obj.put("stock", 2)
        obj.put("stockUpdatedAt", futureTs)
        obj.put("lastSyncSource", "WEBSITE_CHECKOUT")
      }
    }

    // Commit external write to Website local storage key; listener should auto-sync POS list
    prefs.edit().putString(LocalInventoryStorageSync.KEY_WEBSITE_PRODUCTS, arr.toString()).commit()

    assertEquals(2, repo.products.value.first { it.id == "p5" }.stock)
    assertEquals(2, repo.posInventory.value["p5"]?.stock)
    assertEquals(2, readStockFromJsonArray(LocalInventoryStorageSync.KEY_POS_PRODUCTS, "p5"))
  }

  @Test
  fun staleLocalStorageWrite_doesNotOverwriteNewerTransaction() {
    val repo = StoreRepository(context)
    val p1 = repo.products.value.first { it.id == "p1" }
    val initialStock = p1.stock

    repo.addToCart(p1, quantity = 3)
    repo.placeOrder(
      customerName = "তাসনিম",
      phone = "01900000000",
      district = "ঢাকা",
      thana = "উত্তরা",
      address = "সেক্টর ৭",
      paymentMethod = "bKash",
      appliedCoupon = null
    )
    val expectedAfterOrder = (initialStock - 3).coerceAtLeast(0)
    assertEquals(expectedAfterOrder, repo.products.value.first { it.id == "p1" }.stock)

    // Attempt to write stale POS snapshot from the past
    val posRaw = prefs.getString(LocalInventoryStorageSync.KEY_POS_PRODUCTS, null)!!
    val arr = JSONArray(posRaw)
    for (i in 0 until arr.length()) {
      val obj = arr.getJSONObject(i)
      if (obj.optString("id") == "p1" || obj.optString("productId") == "p1") {
        obj.put("stock", initialStock)
        obj.put("stockUpdatedAt", 100L)
        obj.put("updatedAt", 100L)
      }
    }
    prefs.edit().putString(LocalInventoryStorageSync.KEY_POS_PRODUCTS, arr.toString()).commit()

    assertEquals(expectedAfterOrder, repo.products.value.first { it.id == "p1" }.stock)
  }

  @Test
  fun corruptedLocalPosStorage_recordsDiagnosticErrorLog_andPersistsTimestamp() {
    // Write malformed JSON into POS local storage prior to StoreRepository initialization
    prefs.edit().putString(LocalInventoryStorageSync.KEY_POS_PRODUCTS, "{corrupted_json").commit()

    val repo = StoreRepository(context)
    assertTrue(repo.syncErrorLogs.value.isNotEmpty())
    val firstError = repo.syncErrorLogs.value.first()
    assertEquals("LOCAL_POS_STORAGE", firstError.source)
    assertTrue(repo.syncHealth.value.lastLocalSyncTimestamp > 0L)
    assertTrue(prefs.getLong(LocalInventoryStorageSync.KEY_POS_LAST_SYNC_TS, 0L) > 0L)

    // Clearing error logs updates both StateFlow and SharedPreferences
    repo.clearSyncErrorLogs()
    assertEquals(0, repo.syncErrorLogs.value.size)
    assertEquals(0, repo.syncHealth.value.errorCount)
  }
}
