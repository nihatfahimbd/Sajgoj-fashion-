package com.example.data.sync

import com.example.data.model.InventorySyncEvent
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.PosInventoryItem
import com.example.data.model.Product
import com.example.data.model.ProductVariantHelper
import com.example.data.model.ReturnRecord
import com.example.data.model.SyncErrorLog
import com.example.data.model.SyncHealthState
import com.example.firebase.GoogleAuthHelper
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class StockDeltaRequest(
  val productId: String,
  val productName: String = "",
  val sku: String = "",
  val delta: Int,
  val reason: String = "",
  val color: String = "",
  val size: String = "",
  val ageGroup: String = ""
)

/**
 * Real-time bidirectional synchronization engine between the main Storefront database (`products`)
 * and the POS inventory (`pos_inventory`), recording auditable `inventory_sync_events`.
 */
class InventorySyncService(
  private val firestore: FirebaseFirestore? = null,
  private val clock: () -> Long = { System.currentTimeMillis() },
  val clientId: String = "AND-${UUID.randomUUID().toString().take(8)}",
  private val localStorageSync: LocalInventoryStorageSync? = null
) {

  private val _posInventory = MutableStateFlow<Map<String, PosInventoryItem>>(emptyMap())
  val posInventory: StateFlow<Map<String, PosInventoryItem>> = _posInventory.asStateFlow()

  private val _syncEvents = MutableStateFlow<List<InventorySyncEvent>>(emptyList())
  val syncEvents: StateFlow<List<InventorySyncEvent>> = _syncEvents.asStateFlow()

  private val _syncErrorLogs = MutableStateFlow<List<SyncErrorLog>>(
    localStorageSync?.loadSyncErrorLogs() ?: emptyList()
  )
  val syncErrorLogs: StateFlow<List<SyncErrorLog>> = _syncErrorLogs.asStateFlow()

  @Volatile
  private var lastLocalSyncTs: Long =
    localStorageSync?.getLastLocalSyncTimestamp()?.takeIf { it > 0L } ?: clock()

  @Volatile
  private var lastFirestoreSyncTs: Long =
    localStorageSync?.getLastCloudSyncTimestamp() ?: 0L

  @Volatile
  private var cachedProducts: List<Product> = emptyList()

  private val _syncHealth = MutableStateFlow(
    SyncHealthState(
      status = computeStatus(firestore != null, _syncErrorLogs.value, lastFirestoreSyncTs),
      lastSyncTimestamp = maxOf(lastLocalSyncTs, lastFirestoreSyncTs),
      lastLocalSyncTimestamp = lastLocalSyncTs,
      lastFirestoreSyncTimestamp = lastFirestoreSyncTs,
      firestoreConnected = firestore != null && _syncErrorLogs.value.none {
        it.severity == "ERROR" && it.timestamp >= lastFirestoreSyncTs
      },
      errorCount = _syncErrorLogs.value.size
    )
  )
  val syncHealth: StateFlow<SyncHealthState> = _syncHealth.asStateFlow()

  private var posListener: ListenerRegistration? = null
  private var eventsListener: ListenerRegistration? = null

  private fun computeStatus(
    hasFirestore: Boolean,
    errors: List<SyncErrorLog>,
    cloudTs: Long
  ): String {
    if (!hasFirestore) return "LOCAL_ONLY"
    val hasActiveError = errors.any { it.severity == "ERROR" && it.timestamp >= cloudTs }
    return if (hasActiveError) "ERROR" else "CONNECTED"
  }

  fun markLocalSync(
    timestamp: Long = clock(),
    products: List<Product> = cachedProducts
  ) {
    lastLocalSyncTs = maxOf(lastLocalSyncTs, timestamp)
    localStorageSync?.saveLastLocalSyncTimestamp(lastLocalSyncTs)
    refreshHealth(products)
  }

  fun markFirestoreSync(
    timestamp: Long = clock(),
    products: List<Product> = cachedProducts
  ) {
    lastFirestoreSyncTs = maxOf(lastFirestoreSyncTs, timestamp)
    localStorageSync?.saveLastCloudSyncTimestamp(lastFirestoreSyncTs)
    refreshHealth(products)
  }

  fun recordSyncError(
    source: String,
    severity: String = "ERROR",
    code: String = "",
    message: String,
    details: String = "",
    products: List<Product> = cachedProducts
  ) {
    val now = clock()
    val log = SyncErrorLog(
      id = "ERR-$now-${UUID.randomUUID().toString().take(4)}",
      timestamp = now,
      source = source,
      severity = severity,
      code = code,
      message = message,
      details = details
    )
    val updated = (listOf(log) + _syncErrorLogs.value).take(50)
    _syncErrorLogs.value = updated
    localStorageSync?.saveSyncErrorLogs(updated)
    refreshHealth(products)
  }

  fun clearSyncErrorLogs(products: List<Product> = cachedProducts) {
    _syncErrorLogs.value = emptyList()
    localStorageSync?.clearSyncErrorLogs()
    refreshHealth(products)
  }

  fun startListening(
    getCurrentProducts: () -> List<Product>,
    onRemotePosUpdateProducts: (List<Product>) -> Unit
  ) {
    val db = firestore
    if (db == null) {
      refreshHealth(getCurrentProducts())
      return
    }
    stopListening()

    posListener = db.collection("pos_inventory").addSnapshotListener { snapshot, error ->
      val currentProducts = getCurrentProducts()
      if (error != null) {
        recordSyncError(
          source = "FIRESTORE_POS_LISTENER",
          severity = "ERROR",
          code = error.code.name,
          message = error.localizedMessage ?: "Firestore pos_inventory স্ন্যাপশট ত্রুটি",
          details = "collection=pos_inventory",
          products = currentProducts
        )
        return@addSnapshotListener
      }
      if (snapshot == null) {
        recordSyncError(
          source = "FIRESTORE_POS_LISTENER",
          severity = "WARNING",
          code = "NULL_SNAPSHOT",
          message = "pos_inventory থেকে খালি স্ন্যাপশট পাওয়া গেছে",
          details = "collection=pos_inventory",
          products = currentProducts
        )
        return@addSnapshotListener
      }

      val remoteMap = mutableMapOf<String, PosInventoryItem>()
      val incomingChanges = mutableListOf<PosInventoryItem>()

      for (doc in snapshot.documents) {
        try {
          val productId = doc.getString("productId") ?: doc.id
          val item = PosInventoryItem(
            productId = productId,
            name = doc.getString("name") ?: "",
            sku = doc.getString("sku") ?: "",
            stock = (doc.getLong("stock") ?: 0L).toInt().coerceAtLeast(0),
            price = (doc.getLong("price") ?: 0L).toInt(),
            updatedAt = doc.getLong("updatedAt") ?: 0L,
            lastSource = doc.getString("lastSource") ?: "POS_CLOUD",
            lastOrigin = doc.getString("lastOrigin") ?: "",
            lastReferenceId = doc.getString("lastReferenceId") ?: ""
          )
          remoteMap[productId] = item
          if (item.lastOrigin != clientId) {
            incomingChanges.add(item)
          }
        } catch (e: Exception) {
          recordSyncError(
            source = "FIRESTORE_POS_PARSE",
            severity = "WARNING",
            code = e.javaClass.simpleName,
            message = "pos_inventory ডকুমেন্ট পার্স করতে ব্যর্থ",
            details = "doc=${doc.id} • ${e.localizedMessage ?: e.toString()}",
            products = currentProducts
          )
        }
      }

      _posInventory.value = remoteMap
      lastFirestoreSyncTs = maxOf(lastFirestoreSyncTs, clock())
      localStorageSync?.saveLastCloudSyncTimestamp(lastFirestoreSyncTs)

      if (incomingChanges.isNotEmpty() && currentProducts.isNotEmpty()) {
        var changed = false
        val updatedProducts = currentProducts.map { product ->
          val remote = incomingChanges.find {
            it.productId == product.id ||
              (it.sku.isNotBlank() && it.sku.equals(product.sku, ignoreCase = true))
          }
          if (remote != null && remote.stock != product.stock && remote.updatedAt > product.stockUpdatedAt) {
            changed = true
            if (canWriteAdminCloud()) {
              db.collection("products").document(product.id).update(
                mapOf(
                  "stock" to remote.stock,
                  "stockUpdatedAt" to remote.updatedAt,
                  "lastSyncSource" to remote.lastSource,
                  "updatedAt" to FieldValue.serverTimestamp()
                )
              ).addOnSuccessListener {
                markFirestoreSync(products = currentProducts)
              }.addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.UPDATE, "products/${product.id}")
                recordSyncError(
                  source = "FIRESTORE_WRITE",
                  severity = "ERROR",
                  code = e.javaClass.simpleName,
                  message = e.localizedMessage ?: "স্টোরফ্রন্ট প্রোডাক্ট স্টক আপডেট ব্যর্থ",
                  details = "collection=products doc=${product.id}"
                )
              }
            }
            product.copy(
              stock = remote.stock,
              stockUpdatedAt = remote.updatedAt,
              lastSyncSource = remote.lastSource
            )
          } else {
            product
          }
        }
        if (changed) {
          onRemotePosUpdateProducts(updatedProducts)
          refreshHealth(updatedProducts)
          return@addSnapshotListener
        }
      }

      refreshHealth(currentProducts)
    }

    eventsListener = db.collection("inventory_sync_events")
      .orderBy("timestamp", Query.Direction.DESCENDING)
      .limit(50)
      .addSnapshotListener { snapshot, error ->
        val currentProducts = getCurrentProducts()
        if (error != null) {
          recordSyncError(
            source = "FIRESTORE_EVENTS_LISTENER",
            severity = "ERROR",
            code = error.code.name,
            message = error.localizedMessage ?: "inventory_sync_events লিসেনার ত্রুটি",
            details = "collection=inventory_sync_events",
            products = currentProducts
          )
          return@addSnapshotListener
        }
        if (snapshot == null) return@addSnapshotListener
        val events = snapshot.documents.mapNotNull { doc ->
          try {
            InventorySyncEvent(
              id = doc.getString("id") ?: doc.id,
              productId = doc.getString("productId") ?: "",
              productName = doc.getString("productName") ?: "",
              sku = doc.getString("sku") ?: "",
              previousStock = (doc.getLong("previousStock") ?: 0L).toInt(),
              newStock = (doc.getLong("newStock") ?: 0L).toInt(),
              delta = (doc.getLong("delta") ?: 0L).toInt(),
              source = doc.getString("source") ?: "",
              reason = doc.getString("reason") ?: "",
              referenceId = doc.getString("referenceId") ?: "",
              timestamp = doc.getLong("timestamp") ?: 0L
            )
          } catch (e: Exception) {
            recordSyncError(
              source = "FIRESTORE_EVENTS_PARSE",
              severity = "WARNING",
              code = e.javaClass.simpleName,
              message = "সিঙ্ক ইভেন্ট ডকুমেন্ট পার্স করতে ব্যর্থ",
              details = "doc=${doc.id} • ${e.localizedMessage ?: e.toString()}",
              products = currentProducts
            )
            null
          }
        }
        lastFirestoreSyncTs = maxOf(lastFirestoreSyncTs, clock())
        localStorageSync?.saveLastCloudSyncTimestamp(lastFirestoreSyncTs)
        if (events.isNotEmpty()) {
          _syncEvents.value = events
        }
        refreshHealth(currentProducts)
      }
  }

  fun stopListening() {
    posListener?.remove()
    posListener = null
    eventsListener?.remove()
    eventsListener = null
  }

  /**
   * Mirrors storefront catalog updates (initial load, product add/edit) into POS inventory
   * without overwriting newer POS updates.
   */
  fun mirrorCatalogToPos(
    products: List<Product>,
    source: String = "STOREFRONT_CATALOG",
    pushToCloud: Boolean = true
  ) {
    val now = clock()
    val nextMap = _posInventory.value.toMutableMap()

    products.forEach { product ->
      val existing = nextMap[product.id]
      val productTs = if (product.stockUpdatedAt > 0L) product.stockUpdatedAt else now
      if (existing == null || existing.stock != product.stock && productTs >= existing.updatedAt) {
        val mirrored = PosInventoryItem(
          productId = product.id,
          name = product.name,
          sku = product.sku,
          stock = product.stock.coerceAtLeast(0),
          price = product.price,
          updatedAt = productTs,
          lastSource = source,
          lastOrigin = clientId,
          lastReferenceId = ""
        )
        nextMap[product.id] = mirrored
        if (pushToCloud) {
          pushPosInventoryDoc(mirrored)
        }
      }
    }

    _posInventory.value = nextMap
    refreshHealth(products)
  }

  private fun canWriteAdminCloud(): Boolean {
    if (firestore == null) return false
    return try {
      GoogleAuthHelper.toSummary(Firebase.auth.currentUser)?.isAdmin == true
    } catch (_: Exception) {
      false
    }
  }

  fun removeProductFromPos(productId: String) {
    val nextMap = _posInventory.value.toMutableMap()
    nextMap.remove(productId)
    _posInventory.value = nextMap
    if (!canWriteAdminCloud()) return
    try {
      firestore?.collection("pos_inventory")?.document(productId)?.delete()
        ?.addOnSuccessListener { markFirestoreSync() }
        ?.addOnFailureListener { e ->
          recordSyncError(
            source = "FIRESTORE_WRITE",
            severity = "ERROR",
            code = e.javaClass.simpleName,
            message = e.localizedMessage ?: "pos_inventory ডকুমেন্ট মুছতে ব্যর্থ",
            details = "collection=pos_inventory doc=$productId"
          )
        }
    } catch (e: Exception) {
      recordSyncError(
        source = "FIRESTORE_WRITE",
        severity = "ERROR",
        code = e.javaClass.simpleName,
        message = e.localizedMessage ?: "pos_inventory ডকুমেন্ট মুছতে ব্যতিক্রম",
        details = "collection=pos_inventory doc=$productId"
      )
    }
  }

  fun applySaleStockDeduction(
    products: List<Product>,
    items: List<OrderItem>,
    orderId: String
  ): List<Product> {
    val deltas = items.map {
      StockDeltaRequest(
        productId = it.productId,
        productName = it.name,
        delta = -it.quantity.coerceAtLeast(1),
        reason = "অনলাইন স্টোরফ্রন্ট অর্ডার #$orderId",
        color = it.color,
        size = it.size,
        ageGroup = it.ageGroup
      )
    }
    return applyStockDeltaBatch(
      products = products,
      deltas = deltas,
      source = "STOREFRONT_SALE",
      reason = "অনলাইন স্টোরফ্রন্ট বিক্রয়",
      referenceId = orderId
    )
  }

  fun applyPosSaleDeduction(
    products: List<Product>,
    items: List<OrderItem>,
    saleId: String
  ): List<Product> {
    val deltas = items.map {
      StockDeltaRequest(
        productId = it.productId,
        productName = it.name,
        delta = -it.quantity.coerceAtLeast(1),
        reason = "POS কাউন্টার বিক্রয় #$saleId",
        color = it.color,
        size = it.size,
        ageGroup = it.ageGroup
      )
    }
    return applyStockDeltaBatch(
      products = products,
      deltas = deltas,
      source = "POS_SALE",
      reason = "POS কাউন্টার বিক্রয়",
      referenceId = saleId
    )
  }

  fun applyReturnAndExchange(
    products: List<Product>,
    record: ReturnRecord
  ): List<Product> {
    val deltas = mutableListOf<StockDeltaRequest>()
    record.returnedItems.forEach { ret ->
      if (ret.restock) {
        deltas.add(
          StockDeltaRequest(
            productId = ret.productId,
            productName = ret.name,
            delta = ret.quantity.coerceAtLeast(1),
            reason = "সেলস রিটার্ন রিস্টক (${record.orderId})"
          )
        )
      }
    }
    if (record.isExchange) {
      record.exchangeItems.forEach { ex ->
        deltas.add(
          StockDeltaRequest(
            productId = ex.productId,
            productName = ex.name,
            sku = ex.sku,
            delta = -ex.quantity.coerceAtLeast(1),
            reason = "এক্সচেঞ্জ পণ্য ইস্যু (${record.orderId})"
          )
        )
      }
    }
    if (deltas.isEmpty()) return products

    val source = if (record.isExchange) "RETURN_EXCHANGE" else "RETURN_RESTOCK"
    val reason = if (record.isExchange) {
      "রিটার্ন ও এক্সচেঞ্জ (${record.orderId})"
    } else {
      "সেলস রিটার্ন রিস্টক (${record.orderId})"
    }
    return applyStockDeltaBatch(
      products = products,
      deltas = deltas,
      source = source,
      reason = reason,
      referenceId = record.id
    )
  }

  fun applyOrderCancellationRestock(
    products: List<Product>,
    order: Order,
    restoreStock: Boolean
  ): List<Product> {
    if (order.items.isEmpty()) return products
    val multiplier = if (restoreStock) 1 else -1
    val deltas = order.items.map {
      StockDeltaRequest(
        productId = it.productId,
        productName = it.name,
        delta = multiplier * it.quantity.coerceAtLeast(1),
        reason = if (restoreStock) {
          "অর্ডার #${order.id} বাতিল হওয়ায় অটো-রিস্টক"
        } else {
          "অর্ডার #${order.id} পুনরায় সক্রিয় হওয়ায় স্টক সমন্বয়"
        },
        color = it.color,
        size = it.size,
        ageGroup = it.ageGroup
      )
    }
    return applyStockDeltaBatch(
      products = products,
      deltas = deltas,
      source = if (restoreStock) "ORDER_CANCELLED_RESTOCK" else "ORDER_REOPENED_DEDUCT",
      reason = if (restoreStock) "অর্ডার বাতিল রিস্টক" else "অর্ডার পুনরায় সক্রিয়",
      referenceId = order.id
    )
  }

  /**
   * Core bidirectional batch applier: updates storefront products, POS inventory mirror,
   * Firestore documents (`products`, `pos_inventory`), and sync audit events.
   */
  fun applyStockDeltaBatch(
    products: List<Product>,
    deltas: List<StockDeltaRequest>,
    source: String,
    reason: String,
    referenceId: String
  ): List<Product> {
    if (deltas.isEmpty()) return products

    val now = clock()
    val nextPosMap = _posInventory.value.toMutableMap()
    val newEvents = mutableListOf<InventorySyncEvent>()

    val updatedProducts = products.map { product ->
      val matched = deltas.filter { req ->
        (req.productId.isNotBlank() && req.productId == product.id) ||
          (req.sku.isNotBlank() && req.sku.equals(product.sku, ignoreCase = true)) ||
          (req.productName.isNotBlank() && req.productName == product.name)
      }
      if (matched.isEmpty()) {
        product
      } else {
        val totalDelta = matched.sumOf { it.delta }
        if (totalDelta == 0) {
          product
        } else {
          val prevStock = product.stock
          val variantDeltas = matched.mapNotNull { req ->
            val szOrAge = req.size.ifBlank { req.ageGroup }
            if (req.color.isNotBlank() || szOrAge.isNotBlank()) {
              Triple(req.color, szOrAge, req.delta)
            } else null
          }
          val variantAdjusted = ProductVariantHelper.applyVariantStockDeltas(
            product = product,
            variantDeltas = variantDeltas,
            fallbackTotalDelta = totalDelta
          )
          val nextStock = variantAdjusted.stock.coerceAtLeast(0)
          val updated = variantAdjusted.copy(
            stock = nextStock,
            stockUpdatedAt = now,
            lastSyncSource = source
          )

          val event = InventorySyncEvent(
            id = "SYNC-$now-${product.id}-${UUID.randomUUID().toString().take(4)}",
            productId = product.id,
            productName = product.name,
            sku = product.sku,
            previousStock = prevStock,
            newStock = nextStock,
            delta = nextStock - prevStock,
            source = source,
            reason = matched.first().reason.ifBlank { reason },
            referenceId = referenceId,
            timestamp = now
          )
          newEvents.add(event)

          val posItem = PosInventoryItem(
            productId = product.id,
            name = product.name,
            sku = product.sku,
            stock = nextStock,
            price = product.price,
            updatedAt = now,
            lastSource = source,
            lastOrigin = clientId,
            lastReferenceId = referenceId
          )
          nextPosMap[product.id] = posItem

          pushStorefrontStockDoc(updated)
          pushPosInventoryDoc(posItem)
          pushSyncEventDoc(event)

          updated
        }
      }
    }

    if (newEvents.isNotEmpty()) {
      _posInventory.value = nextPosMap
      _syncEvents.value = (newEvents + _syncEvents.value).take(60)
      refreshHealth(updatedProducts)
    }

    return updatedProducts
  }

  /**
   * Reconciles any stock drift between Storefront `products` and `pos_inventory`
   * using latest `stockUpdatedAt` / `updatedAt` timestamp precedence.
   */
  fun reconcileStorefrontAndPos(currentProducts: List<Product>): Pair<List<Product>, Int> {
    val now = clock()
    val currentPosMap = _posInventory.value.toMutableMap()
    val reconcileEvents = mutableListOf<InventorySyncEvent>()
    var reconciledCount = 0

    val updatedProducts = currentProducts.map { product ->
      val posItem = currentPosMap[product.id]
      if (posItem == null) {
        val createdPos = PosInventoryItem(
          productId = product.id,
          name = product.name,
          sku = product.sku,
          stock = product.stock.coerceAtLeast(0),
          price = product.price,
          updatedAt = now,
          lastSource = "RECONCILE",
          lastOrigin = clientId,
          lastReferenceId = "AUTO_RECONCILE"
        )
        currentPosMap[product.id] = createdPos
        pushPosInventoryDoc(createdPos)
        reconciledCount++
        product.copy(stockUpdatedAt = maxOf(product.stockUpdatedAt, now))
      } else if (posItem.stock != product.stock) {
        reconciledCount++
        val usePos = posItem.updatedAt > product.stockUpdatedAt
        val winningStock = if (usePos) posItem.stock.coerceAtLeast(0) else product.stock.coerceAtLeast(0)
        val winningSource = if (usePos) posItem.lastSource.ifBlank { "POS_CLOUD" } else "STOREFRONT_RECONCILE"
        val winningTs = maxOf(posItem.updatedAt, product.stockUpdatedAt, now)

        val ev = InventorySyncEvent(
          id = "REC-$now-${product.id}",
          productId = product.id,
          productName = product.name,
          sku = product.sku,
          previousStock = product.stock,
          newStock = winningStock,
          delta = winningStock - product.stock,
          source = winningSource,
          reason = "দ্বিমুখী স্টক রিকনসিলিয়েশন",
          referenceId = "RECONCILE",
          timestamp = now
        )
        reconcileEvents.add(ev)
        pushSyncEventDoc(ev)

        val updatedProduct = product.copy(
          stock = winningStock,
          stockUpdatedAt = winningTs,
          lastSyncSource = winningSource
        )
        val updatedPos = posItem.copy(
          stock = winningStock,
          updatedAt = winningTs,
          lastSource = winningSource,
          lastOrigin = clientId,
          lastReferenceId = "RECONCILE"
        )
        currentPosMap[product.id] = updatedPos
        pushStorefrontStockDoc(updatedProduct)
        pushPosInventoryDoc(updatedPos)
        updatedProduct
      } else {
        product
      }
    }

    _posInventory.value = currentPosMap
    if (reconcileEvents.isNotEmpty()) {
      _syncEvents.value = (reconcileEvents + _syncEvents.value).take(60)
    }
    refreshHealth(updatedProducts)
    return updatedProducts to reconciledCount
  }

  /**
   * Simulates or applies an external POS inventory snapshot directly (e.g. for cross-terminal
   * testing or offline POS bridge imports) with timestamp conflict protection.
   */
  fun applyRemotePosItem(currentProducts: List<Product>, remoteItem: PosInventoryItem): List<Product> {
    val nextMap = _posInventory.value.toMutableMap()
    nextMap[remoteItem.productId] = remoteItem
    _posInventory.value = nextMap

    val updated = currentProducts.map { product ->
      if (product.id == remoteItem.productId &&
        remoteItem.stock != product.stock &&
        remoteItem.updatedAt > product.stockUpdatedAt
      ) {
        val delta = remoteItem.stock - product.stock
        val ev = InventorySyncEvent(
          id = "EXT-${remoteItem.updatedAt}-${product.id}",
          productId = product.id,
          productName = product.name,
          sku = product.sku,
          previousStock = product.stock,
          newStock = remoteItem.stock,
          delta = delta,
          source = remoteItem.lastSource.ifBlank { "POS_SALE" },
          reason = "POS রিমোট সিঙ্ক আপডেট",
          referenceId = remoteItem.lastReferenceId,
          timestamp = remoteItem.updatedAt
        )
        _syncEvents.value = (listOf(ev) + _syncEvents.value).take(60)
        product.copy(
          stock = remoteItem.stock,
          stockUpdatedAt = remoteItem.updatedAt,
          lastSyncSource = remoteItem.lastSource
        )
      } else {
        product
      }
    }
    refreshHealth(updated)
    return updated
  }

  fun refreshHealth(products: List<Product>) {
    cachedProducts = products
    val posMap = _posInventory.value
    var inSync = 0
    var drift = 0
    products.forEach { product ->
      val pos = posMap[product.id]
      if (pos != null && pos.stock == product.stock) {
        inSync++
      } else {
        drift++
      }
    }
    val errors = _syncErrorLogs.value
    val status = computeStatus(firestore != null, errors, lastFirestoreSyncTs)
    val connected = firestore != null && errors.none {
      it.severity == "ERROR" && it.timestamp >= lastFirestoreSyncTs
    }
    _syncHealth.value = SyncHealthState(
      status = status,
      lastSyncTimestamp = maxOf(lastLocalSyncTs, lastFirestoreSyncTs),
      lastLocalSyncTimestamp = lastLocalSyncTs,
      lastFirestoreSyncTimestamp = lastFirestoreSyncTs,
      firestoreConnected = connected,
      errorCount = errors.size,
      totalSyncedEvents = _syncEvents.value.size,
      storefrontProductCount = products.size,
      posProductCount = posMap.size,
      inSyncCount = inSync,
      driftCount = drift
    )
  }

  private fun pushStorefrontStockDoc(product: Product) {
    if (!canWriteAdminCloud()) return
    try {
      val updatePayload = buildMap<String, Any> {
        put("stock", product.stock)
        put("stockUpdatedAt", product.stockUpdatedAt)
        put("lastSyncSource", product.lastSyncSource)
        put("updatedAt", FieldValue.serverTimestamp())
        if (product.variations.isNotEmpty()) {
          put("variations", product.variations.map(ProductVariantHelper::variationToMap))
        }
      }
      firestore?.collection("products")?.document(product.id)?.update(updatePayload)
        ?.addOnSuccessListener {
          markFirestoreSync()
        }?.addOnFailureListener { e ->
        handleFirestoreError(e, OperationType.UPDATE, "products/${product.id}")
        recordSyncError(
          source = "FIRESTORE_WRITE",
          severity = "ERROR",
          code = e.javaClass.simpleName,
          message = e.localizedMessage ?: "Firestore products স্টক আপডেট ব্যর্থ",
          details = "collection=products doc=${product.id} (${product.name})"
        )
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "products/${product.id}")
      recordSyncError(
        source = "FIRESTORE_WRITE",
        severity = "ERROR",
        code = e.javaClass.simpleName,
        message = e.localizedMessage ?: "Firestore products স্টক আপডেট ব্যতিক্রম",
        details = "collection=products doc=${product.id}"
      )
    }
  }

  private fun pushPosInventoryDoc(item: PosInventoryItem) {
    if (!canWriteAdminCloud()) return
    try {
      firestore?.collection("pos_inventory")?.document(item.productId)?.set(
        mapOf(
          "productId" to item.productId,
          "name" to item.name,
          "sku" to item.sku,
          "stock" to item.stock,
          "price" to item.price,
          "updatedAt" to item.updatedAt,
          "lastSource" to item.lastSource,
          "lastOrigin" to item.lastOrigin,
          "lastReferenceId" to item.lastReferenceId
        ),
        SetOptions.merge()
      )?.addOnSuccessListener {
        markFirestoreSync()
      }?.addOnFailureListener { e ->
        handleFirestoreError(e, OperationType.WRITE, "pos_inventory/${item.productId}")
        recordSyncError(
          source = "FIRESTORE_WRITE",
          severity = "ERROR",
          code = e.javaClass.simpleName,
          message = e.localizedMessage ?: "Firestore pos_inventory সিঙ্ক ব্যর্থ",
          details = "collection=pos_inventory doc=${item.productId} (${item.name})"
        )
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, "pos_inventory/${item.productId}")
      recordSyncError(
        source = "FIRESTORE_WRITE",
        severity = "ERROR",
        code = e.javaClass.simpleName,
        message = e.localizedMessage ?: "Firestore pos_inventory সিঙ্ক ব্যতিক্রম",
        details = "collection=pos_inventory doc=${item.productId}"
      )
    }
  }

  private fun pushSyncEventDoc(event: InventorySyncEvent) {
    if (!canWriteAdminCloud()) return
    try {
      firestore?.collection("inventory_sync_events")?.document(event.id)?.set(
        mapOf(
          "productId" to event.productId,
          "productName" to event.productName,
          "sku" to event.sku,
          "previousStock" to event.previousStock,
          "newStock" to event.newStock,
          "delta" to event.delta,
          "source" to event.source,
          "reason" to event.reason,
          "referenceId" to event.referenceId,
          "timestamp" to event.timestamp
        )
      )?.addOnSuccessListener {
        markFirestoreSync()
      }?.addOnFailureListener { e ->
        handleFirestoreError(e, OperationType.CREATE, "inventory_sync_events/${event.id}")
        recordSyncError(
          source = "FIRESTORE_WRITE",
          severity = "ERROR",
          code = e.javaClass.simpleName,
          message = e.localizedMessage ?: "সিঙ্ক অডিট ইভেন্ট সংরক্ষণ ব্যর্থ",
          details = "collection=inventory_sync_events doc=${event.id}"
        )
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "inventory_sync_events/${event.id}")
      recordSyncError(
        source = "FIRESTORE_WRITE",
        severity = "ERROR",
        code = e.javaClass.simpleName,
        message = e.localizedMessage ?: "সিঙ্ক অডিট ইভেন্ট ব্যতিক্রম",
        details = "collection=inventory_sync_events doc=${event.id}"
      )
    }
  }
}
