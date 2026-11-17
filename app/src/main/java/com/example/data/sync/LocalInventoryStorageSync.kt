package com.example.data.sync

import android.content.SharedPreferences
import com.example.data.model.PosInventoryItem
import com.example.data.model.Product
import com.example.data.model.SyncErrorLog
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Persists both Website (`sajgoj_store_products`) and POS (`sajgoj_products`) inventory lists
 * in local SharedPreferences and attaches an active [SharedPreferences.OnSharedPreferenceChangeListener]
 * so stock changes written to either environment immediately synchronize across both lists.
 */
class LocalInventoryStorageSync(
  private val prefs: SharedPreferences,
  private val clock: () -> Long = { System.currentTimeMillis() }
) {
  companion object {
    const val KEY_WEBSITE_PRODUCTS = "sajgoj_store_products"
    const val KEY_POS_PRODUCTS = "sajgoj_products"
    const val KEY_POS_LAST_SYNC_TS = "sajgoj_pos_last_sync_ts"
    const val KEY_CLOUD_LAST_SYNC_TS = "sajgoj_cloud_last_sync_ts"
    const val KEY_POS_SYNC_ERRORS = "sajgoj_pos_sync_errors"
    private const val MAX_STORED_ERRORS = 50
  }

  @Volatile
  private var isApplyingLocalSync = false

  @Volatile
  var errorReporter: ((source: String, code: String, message: String, details: String) -> Unit)? = null

  // Keep a strong reference so Android SharedPreferences' WeakHashMap does not GC the listener
  private var activeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

  fun startListening(
    getCurrentProducts: () -> List<Product>,
    onSynchronizedProducts: (updatedProducts: List<Product>, changedKey: String) -> Unit
  ) {
    stopListening()
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
      if (isApplyingLocalSync || key == null) return@OnSharedPreferenceChangeListener
      if (key != KEY_POS_PRODUCTS && key != KEY_WEBSITE_PRODUCTS) return@OnSharedPreferenceChangeListener

      val current = getCurrentProducts()
      if (current.isEmpty()) return@OnSharedPreferenceChangeListener

      if (key == KEY_POS_PRODUCTS) {
        val posItems = readPosItems()
        if (posItems.isEmpty()) return@OnSharedPreferenceChangeListener
        var changed = false
        val now = clock()
        val updated = current.map { product ->
          val match = posItems.find {
            it.productId == product.id ||
              (it.sku.isNotBlank() && it.sku.equals(product.sku, ignoreCase = true)) ||
              (it.name.isNotBlank() && it.name == product.name)
          }
          if (match != null && match.stock != product.stock &&
            (match.updatedAt > product.stockUpdatedAt || (product.stockUpdatedAt == 0L && match.updatedAt >= 0L))
          ) {
            changed = true
            product.copy(
              stock = match.stock.coerceAtLeast(0),
              stockUpdatedAt = if (match.updatedAt > 0L) match.updatedAt else now,
              lastSyncSource = match.lastSource.ifBlank { "POS_LOCAL_STORAGE" }
            )
          } else {
            product
          }
        }
        if (changed) {
          persistBoth(updated, keyToSkipNotify = KEY_POS_PRODUCTS)
          onSynchronizedProducts(updated, key)
        }
      } else if (key == KEY_WEBSITE_PRODUCTS) {
        val webItems = readWebsiteProducts()
        if (webItems.isEmpty()) return@OnSharedPreferenceChangeListener
        var changed = false
        val now = clock()
        val updated = current.map { product ->
          val match = webItems.find {
            it.id == product.id ||
              (it.sku.isNotBlank() && it.sku.equals(product.sku, ignoreCase = true))
          }
          if (match != null && match.stock != product.stock &&
            (match.stockUpdatedAt > product.stockUpdatedAt || (product.stockUpdatedAt == 0L && match.stockUpdatedAt >= 0L))
          ) {
            changed = true
            product.copy(
              stock = match.stock.coerceAtLeast(0),
              stockUpdatedAt = if (match.stockUpdatedAt > 0L) match.stockUpdatedAt else now,
              lastSyncSource = match.lastSyncSource.ifBlank { "WEBSITE_LOCAL_STORAGE" }
            )
          } else {
            product
          }
        }
        if (changed) {
          persistBoth(updated, keyToSkipNotify = KEY_WEBSITE_PRODUCTS)
          onSynchronizedProducts(updated, key)
        }
      }
    }
    activeListener = listener
    prefs.registerOnSharedPreferenceChangeListener(listener)
  }

  fun stopListening() {
    activeListener?.let {
      prefs.unregisterOnSharedPreferenceChangeListener(it)
    }
    activeListener = null
  }

  /**
   * Loads persisted Website & POS product lists from SharedPreferences, reconciles any newer stock
   * counts across both environments, and ensures both keys exist in sync.
   */
  fun loadAndReconcile(seedProducts: List<Product>): List<Product> {
    val storedWebsite = readWebsiteProducts()
    val storedPos = readPosItems()

    val baseList = if (storedWebsite.isNotEmpty()) {
      // Merge seed metadata (like drawable resource imageUrls) if missing, preserve saved stock
      seedProducts.map { seed ->
        val saved = storedWebsite.find { it.id == seed.id || (it.sku.isNotBlank() && it.sku == seed.sku) }
        if (saved != null && saved.regularPrice <= 0 && saved.offerPrice <= 0 && seed.regularPrice > saved.price) {
          saved.copy(
            regularPrice = seed.regularPrice,
            offerPrice = if (saved.price < seed.regularPrice) saved.price else seed.offerPrice
          )
        } else {
          saved ?: seed
        }
      } + storedWebsite.filter { saved -> seedProducts.none { it.id == saved.id } }
    } else {
      seedProducts
    }

    val reconciled = baseList.map { product ->
      val posMatch = storedPos.find {
        it.productId == product.id ||
          (it.sku.isNotBlank() && it.sku.equals(product.sku, ignoreCase = true))
      }
      if (posMatch != null && posMatch.stock != product.stock &&
        (posMatch.updatedAt > product.stockUpdatedAt || (product.stockUpdatedAt == 0L && posMatch.updatedAt > 0L))
      ) {
        product.copy(
          stock = posMatch.stock.coerceAtLeast(0),
          stockUpdatedAt = posMatch.updatedAt,
          lastSyncSource = posMatch.lastSource.ifBlank { "POS_LOCAL_RECONCILE" }
        )
      } else {
        product
      }
    }

    persistBoth(reconciled)
    return reconciled
  }

  fun persistBoth(
    products: List<Product>,
    posMap: Map<String, PosInventoryItem> = emptyMap(),
    keyToSkipNotify: String? = null
  ) {
    val webJson = serializeWebsiteProducts(products)
    val posJson = serializePosProducts(products, posMap)

    isApplyingLocalSync = true
    try {
      val now = clock()
      val editor = prefs.edit()
      if (keyToSkipNotify != KEY_WEBSITE_PRODUCTS && prefs.getString(KEY_WEBSITE_PRODUCTS, null) != webJson) {
        editor.putString(KEY_WEBSITE_PRODUCTS, webJson)
      }
      if (keyToSkipNotify != KEY_POS_PRODUCTS && prefs.getString(KEY_POS_PRODUCTS, null) != posJson) {
        editor.putString(KEY_POS_PRODUCTS, posJson)
      }
      editor.putLong(KEY_POS_LAST_SYNC_TS, now)
      editor.apply()
    } catch (e: Exception) {
      reportStorageError(
        source = "LOCAL_POS_STORAGE",
        code = e.javaClass.simpleName,
        message = "লোকাল POS স্টোরেজে সিঙ্ক ডেটা সংরক্ষণ ব্যর্থ হয়েছে",
        details = e.localizedMessage ?: e.toString()
      )
    } finally {
      isApplyingLocalSync = false
    }
  }

  fun readWebsiteProducts(): List<Product> {
    val raw = prefs.getString(KEY_WEBSITE_PRODUCTS, null) ?: return emptyList()
    return try {
      val arr = JSONArray(raw)
      buildList {
        for (i in 0 until arr.length()) {
          val o = arr.optJSONObject(i) ?: continue
          add(
            Product(
              id = o.optString("id"),
              name = o.optString("name"),
              price = o.optInt("price"),
              stock = o.optInt("stock").coerceAtLeast(0),
              category = o.optString("category", "শাড়ি"),
              imageUrl = o.optString("imageUrl"),
              sku = o.optString("sku"),
              description = o.optString("description"),
              rating = o.optDouble("rating", 4.8).toFloat(),
              reviewsCount = o.optInt("reviewsCount", 18),
              sourceUrl = o.optString("sourceUrl"),
              colors = jsonArrayToStrings(o.optJSONArray("colors")),
              sizes = jsonArrayToStrings(o.optJSONArray("sizes")),
              ageGroups = jsonArrayToStrings(o.optJSONArray("ageGroups")),
              stockUpdatedAt = o.optLong("stockUpdatedAt", 0L),
              lastSyncSource = o.optString("lastSyncSource", ""),
              isHotSale = o.optBoolean("isHotSale", false),
              isBestDeal = o.optBoolean("isBestDeal", false),
              isB2G1 = o.optBoolean("isB2G1", false),
              createdAt = o.optLong("createdAt", 0L),
              regularPrice = o.optInt("regularPrice", 0),
              offerPrice = o.optInt("offerPrice", 0)
            )
          )
        }
      }
    } catch (e: Exception) {
      reportStorageError(
        source = "LOCAL_STORE_STORAGE",
        code = e.javaClass.simpleName,
        message = "লোকাল স্টোরফ্রন্ট প্রোডাক্ট JSON পার্স করতে ত্রুটি হয়েছে",
        details = "key=$KEY_WEBSITE_PRODUCTS • ${e.localizedMessage ?: e.toString()}"
      )
      emptyList()
    }
  }

  fun readPosItems(): List<PosInventoryItem> {
    val raw = prefs.getString(KEY_POS_PRODUCTS, null) ?: return emptyList()
    return try {
      val arr = JSONArray(raw)
      buildList {
        for (i in 0 until arr.length()) {
          val o = arr.optJSONObject(i) ?: continue
          val pid = o.optString("productId").ifBlank { o.optString("id") }
          if (pid.isBlank()) continue
          add(
            PosInventoryItem(
              productId = pid,
              name = o.optString("name"),
              sku = o.optString("sku"),
              stock = o.optInt("stock").coerceAtLeast(0),
              price = o.optInt("price"),
              updatedAt = o.optLong("stockUpdatedAt", o.optLong("updatedAt", 0L)),
              lastSource = o.optString("lastSource", "POS_LOCAL_STORAGE"),
              lastReferenceId = o.optString("lastReferenceId", "")
            )
          )
        }
      }
    } catch (e: Exception) {
      reportStorageError(
        source = "LOCAL_POS_STORAGE",
        code = e.javaClass.simpleName,
        message = "লোকাল POS স্টোরেজ ডেটা পার্স করতে ত্রুটি হয়েছে",
        details = "key=$KEY_POS_PRODUCTS • ${e.localizedMessage ?: e.toString()}"
      )
      emptyList()
    }
  }

  fun getLastLocalSyncTimestamp(): Long = prefs.getLong(KEY_POS_LAST_SYNC_TS, 0L)

  fun saveLastLocalSyncTimestamp(timestamp: Long) {
    prefs.edit().putLong(KEY_POS_LAST_SYNC_TS, timestamp).apply()
  }

  fun getLastCloudSyncTimestamp(): Long = prefs.getLong(KEY_CLOUD_LAST_SYNC_TS, 0L)

  fun saveLastCloudSyncTimestamp(timestamp: Long) {
    prefs.edit().putLong(KEY_CLOUD_LAST_SYNC_TS, timestamp).apply()
  }

  fun loadSyncErrorLogs(): List<SyncErrorLog> {
    val raw = prefs.getString(KEY_POS_SYNC_ERRORS, null) ?: return emptyList()
    return try {
      val arr = JSONArray(raw)
      buildList {
        for (i in 0 until arr.length()) {
          val o = arr.optJSONObject(i) ?: continue
          add(
            SyncErrorLog(
              id = o.optString("id"),
              timestamp = o.optLong("timestamp", 0L),
              source = o.optString("source", "FIRESTORE_SYNC"),
              severity = o.optString("severity", "ERROR"),
              code = o.optString("code", ""),
              message = o.optString("message", ""),
              details = o.optString("details", "")
            )
          )
        }
      }
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun saveSyncErrorLogs(logs: List<SyncErrorLog>) {
    val trimmed = logs.take(MAX_STORED_ERRORS)
    val arr = JSONArray()
    trimmed.forEach { log ->
      arr.put(
        JSONObject().apply {
          put("id", log.id)
          put("timestamp", log.timestamp)
          put("source", log.source)
          put("severity", log.severity)
          put("code", log.code)
          put("message", log.message)
          put("details", log.details)
        }
      )
    }
    prefs.edit().putString(KEY_POS_SYNC_ERRORS, arr.toString()).apply()
  }

  fun appendSyncErrorLog(log: SyncErrorLog): List<SyncErrorLog> {
    val existing = loadSyncErrorLogs()
    val updated = (listOf(log) + existing).take(MAX_STORED_ERRORS)
    saveSyncErrorLogs(updated)
    return updated
  }

  fun clearSyncErrorLogs() {
    prefs.edit().remove(KEY_POS_SYNC_ERRORS).apply()
  }

  private fun reportStorageError(
    source: String,
    code: String,
    message: String,
    details: String
  ) {
    val reporter = errorReporter
    if (reporter != null) {
      reporter.invoke(source, code, message, details)
    } else {
      val now = clock()
      appendSyncErrorLog(
        SyncErrorLog(
          id = "ERR-$now-${UUID.randomUUID().toString().take(4)}",
          timestamp = now,
          source = source,
          severity = "ERROR",
          code = code,
          message = message,
          details = details
        )
      )
    }
  }

  private fun serializeWebsiteProducts(products: List<Product>): String {
    val arr = JSONArray()
    products.forEach { p ->
      val o = JSONObject().apply {
        put("id", p.id)
        put("name", p.name)
        put("price", p.price)
        put("stock", p.stock.coerceAtLeast(0))
        put("category", p.category)
        put("imageUrl", p.imageUrl)
        put("sku", p.sku)
        put("description", p.description)
        put("rating", p.rating.toDouble())
        put("reviewsCount", p.reviewsCount)
        put("sourceUrl", p.sourceUrl)
        put("colors", JSONArray(p.colors))
        put("sizes", JSONArray(p.sizes))
        put("ageGroups", JSONArray(p.ageGroups))
        put("stockUpdatedAt", p.stockUpdatedAt)
        put("lastSyncSource", p.lastSyncSource)
        put("isHotSale", p.isHotSale)
        put("isBestDeal", p.isBestDeal)
        put("isB2G1", p.isB2G1)
        put("createdAt", p.createdAt)
        put("regularPrice", p.regularPrice)
        put("offerPrice", p.offerPrice)
      }
      arr.put(o)
    }
    return arr.toString()
  }

  private fun serializePosProducts(
    products: List<Product>,
    posMap: Map<String, PosInventoryItem>
  ): String {
    val arr = JSONArray()
    products.forEach { p ->
      val posItem = posMap[p.id]
      val usePos = posItem != null && posItem.updatedAt > p.stockUpdatedAt
      val stock = (if (usePos) posItem!!.stock else p.stock).coerceAtLeast(0)
      val updatedAt = maxOf(p.stockUpdatedAt, posItem?.updatedAt ?: 0L)
      val source = (if (usePos) posItem!!.lastSource else p.lastSyncSource).ifBlank { "SYNC" }

      val o = JSONObject().apply {
        put("id", p.id)
        put("productId", p.id)
        put("name", p.name)
        put("sku", p.sku)
        put("price", p.price)
        put("stock", stock)
        put("stockUpdatedAt", updatedAt)
        put("updatedAt", updatedAt)
        put("lastSource", source)
        put("lastReferenceId", posItem?.lastReferenceId ?: "")
      }
      arr.put(o)
    }
    return arr.toString()
  }

  private fun jsonArrayToStrings(arr: JSONArray?): List<String> {
    if (arr == null) return emptyList()
    return buildList {
      for (i in 0 until arr.length()) {
        val v = arr.optString(i)
        if (v.isNotBlank()) add(v)
      }
    }
  }
}
