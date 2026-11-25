package com.example.util

import com.example.data.model.Order
import com.example.data.model.Product
import org.json.JSONObject

sealed interface AdminQrLookupResult {
  data class OrderMatch(
    val order: Order,
    val matchedBy: String
  ) : AdminQrLookupResult

  data class ProductMatch(
    val product: Product,
    val matchedBy: String
  ) : AdminQrLookupResult

  data class NotFound(
    val rawCode: String
  ) : AdminQrLookupResult
}

object AdminQrLookupHelper {

  fun lookup(
    rawPayload: String,
    products: List<Product>,
    orders: List<Order>
  ): AdminQrLookupResult {
    val clean = rawPayload.trim()
    if (clean.isEmpty()) return AdminQrLookupResult.NotFound("")

    // 1. Parse JSON payloads such as ShippingLabelDialog's SAJGOJ_SHIPPING_LABEL QR
    if (clean.startsWith("{") && clean.endsWith("}")) {
      try {
        val json = JSONObject(clean)
        val jsonOrderId = json.optString("orderId").trim()
        val jsonTracking = json.optString("tracking").trim()
          .ifBlank { json.optString("trackingId").trim() }
        val jsonProductId = json.optString("productId").trim()
          .ifBlank { json.optString("id").trim() }
        val jsonSku = json.optString("sku").trim()

        if (jsonOrderId.isNotEmpty()) {
          findOrderById(jsonOrderId, orders)?.let {
            return AdminQrLookupResult.OrderMatch(it, "পার্সেল QR (অর্ডার ID)")
          }
        }
        if (jsonTracking.isNotEmpty()) {
          findOrderByTracking(jsonTracking, orders)?.let {
            return AdminQrLookupResult.OrderMatch(it, "পার্সেল QR (ট্র্যাকিং ID)")
          }
        }
        if (jsonProductId.isNotEmpty()) {
          products.find { it.id.equals(jsonProductId, ignoreCase = true) }?.let {
            return AdminQrLookupResult.ProductMatch(it, "QR কোড (প্রোডাক্ট ID)")
          }
        }
        if (jsonSku.isNotEmpty()) {
          products.find { it.sku.equals(jsonSku, ignoreCase = true) }?.let {
            return AdminQrLookupResult.ProductMatch(it, "QR কোড (SKU)")
          }
        }
      } catch (_: Exception) {
        // Fallback to plain token / URL matching below
      }
    }

    // 2. Check Order / Tracking deep links (#order-xxx, ?order=xxx, /order/xxx, ?tracking=xxx)
    val orderDeepLink = Regex(
      "#order-([a-zA-Z0-9_#-]+)|[?&](?:order|orderId)=([a-zA-Z0-9_#-]+)|/orders?/([a-zA-Z0-9_#-]+)",
      RegexOption.IGNORE_CASE
    )
      .find(clean)
      ?.let { m -> m.groupValues[1].ifBlank { m.groupValues[2] }.ifBlank { m.groupValues[3] } }
    if (!orderDeepLink.isNullOrBlank()) {
      findOrderById(orderDeepLink, orders)?.let {
        return AdminQrLookupResult.OrderMatch(it, "অর্ডার লিংক")
      }
    }

    val trackingDeepLink = Regex("[?&](?:tracking|trackingId)=([a-zA-Z0-9_-]+)", RegexOption.IGNORE_CASE)
      .find(clean)
      ?.groupValues
      ?.getOrNull(1)
    if (!trackingDeepLink.isNullOrBlank()) {
      findOrderByTracking(trackingDeepLink, orders)?.let {
        return AdminQrLookupResult.OrderMatch(it, "ট্র্যাকিং লিংক")
      }
    }

    // 3. Direct Order ID match (supports with or without leading '#')
    findOrderById(clean, orders)?.let {
      return AdminQrLookupResult.OrderMatch(it, "অর্ডার আইডি")
    }

    // 4. Direct Courier Tracking ID match
    findOrderByTracking(clean, orders)?.let {
      return AdminQrLookupResult.OrderMatch(it, "কুরিয়ার ট্র্যাকিং আইডি")
    }

    // 5. Direct Product ID / SKU / Storefront QR deep-link (#product-p1, ?product=p1, /product/p1)
    val productDeepLink = Regex(
      "#product-([a-zA-Z0-9_-]+)|[?&]product=([a-zA-Z0-9_-]+)|/products?/([a-zA-Z0-9_-]+)",
      RegexOption.IGNORE_CASE
    )
      .find(clean)
      ?.let { m -> m.groupValues[1].ifBlank { m.groupValues[2] }.ifBlank { m.groupValues[3] } }
    if (!productDeepLink.isNullOrBlank()) {
      products.find { it.id.equals(productDeepLink, ignoreCase = true) }?.let {
        return AdminQrLookupResult.ProductMatch(it, "প্রোডাক্ট QR লিংক")
      }
    }

    products.find { it.sku.isNotBlank() && it.sku.equals(clean, ignoreCase = true) }?.let {
      return AdminQrLookupResult.ProductMatch(it, "প্রোডাক্ট SKU")
    }

    products.find { it.id.equals(clean, ignoreCase = true) }?.let {
      return AdminQrLookupResult.ProductMatch(it, "প্রোডাক্ট ID")
    }

    VisualImageSearchHelper.findProductByScanPayload(clean, products)?.let {
      return AdminQrLookupResult.ProductMatch(it, "প্রোডাক্ট কোড ম্যাচ")
    }

    // 6. Customer phone number lookup (only when input looks like a 10+ digit phone number)
    val digitsOnly = clean.filter { it.isDigit() }
    if (digitsOnly.length >= 10) {
      orders.firstOrNull { order ->
        val orderPhoneDigits = order.phone.filter { it.isDigit() }
        orderPhoneDigits.isNotEmpty() &&
          (orderPhoneDigits == digitsOnly || orderPhoneDigits.endsWith(digitsOnly.takeLast(10)))
      }?.let {
        return AdminQrLookupResult.OrderMatch(it, "কাস্টমার মোবাইল নম্বর")
      }
    }

    return AdminQrLookupResult.NotFound(clean)
  }

  private fun findOrderById(candidate: String, orders: List<Order>): Order? {
    val normalized = candidate.trim().removePrefix("#")
    if (normalized.isEmpty()) return null
    return orders.find { order ->
      order.id.trim().removePrefix("#").equals(normalized, ignoreCase = true)
    }
  }

  private fun findOrderByTracking(candidate: String, orders: List<Order>): Order? {
    val normalized = candidate.trim()
    if (normalized.isEmpty()) return null
    return orders.find { order ->
      order.trackingId.isNotBlank() && order.trackingId.trim().equals(normalized, ignoreCase = true)
    }
  }
}
