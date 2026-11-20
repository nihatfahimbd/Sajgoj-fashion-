package com.example.sms

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.data.model.Order
import com.example.data.model.SmsTemplate

object SmsHelper {
  fun normalizePhone(raw: String): String {
    val cleaned = raw.trim().replace(Regex("[\\s\\-()]+"), "")
    if (cleaned.isEmpty()) return ""
    return when {
      cleaned.startsWith("+880") -> cleaned
      cleaned.startsWith("880") && cleaned.length >= 13 -> "+$cleaned"
      cleaned.startsWith("01") && cleaned.length == 11 -> "+88$cleaned"
      else -> cleaned
    }
  }

  fun isValidPhone(raw: String): Boolean {
    val digits = raw.filter { it.isDigit() }
    return digits.length in 7..15
  }

  fun containsNonAscii(text: String): Boolean = text.any { it.code > 127 }

  fun estimateSmsParts(text: String): Int {
    if (text.isEmpty()) return 0
    val isUnicode = containsNonAscii(text)
    val singleLimit = if (isUnicode) 70 else 160
    val multiLimit = if (isUnicode) 67 else 153
    return if (text.length <= singleLimit) 1 else ((text.length - 1) / multiLimit) + 1
  }

  fun formatTemplate(
    template: String,
    customerName: String = "",
    orderId: String = "",
    total: Int = 0,
    status: String = "",
    trackingId: String = "",
    storeName: String = "SAJGOJ FASHION"
  ): String {
    val safeName = customerName.trim().ifBlank { "সম্মানিত গ্রাহক" }
    val safeTracking = trackingId.trim().ifBlank { "প্রসেসিং" }
    return template
      .replace("{name}", safeName)
      .replace("{orderId}", orderId.trim())
      .replace("{total}", total.toString())
      .replace("{status}", status.trim())
      .replace("{trackingId}", safeTracking)
      .replace("{store}", storeName.trim().ifBlank { "SAJGOJ FASHION" })
  }

  fun buildOrderStatusSms(order: Order, storeName: String = "SAJGOJ FASHION"): String {
    val brand = storeName.trim().ifBlank { "SAJGOJ FASHION" }
    val name = order.customerName.trim().ifBlank { "সম্মানিত গ্রাহক" }
    val trackingPart = if (order.trackingId.isNotBlank()) " ট্র্যাকিং: ${order.trackingId}।" else ""
    return when {
      order.status.equals("Pending", ignoreCase = true) ->
        "আসসালামু আলাইকুম $name, $brand-এ আপনার অর্ডার #${order.id} (৳${order.total}) রিসিভ হয়েছে। শীঘ্রই কনফার্ম করা হবে।"
      order.status.equals("Confirmed", ignoreCase = true) ->
        "প্রিয় $name, আপনার অর্ডার #${order.id} (৳${order.total}) কনফার্ম করা হয়েছে! প্যাকিং চলছে। ধন্যবাদ - $brand"
      order.status.equals("Processing", ignoreCase = true) ->
        "প্রিয় $name, আপনার অর্ডার #${order.id} এখন প্যাকিং ও প্রসেসিং পর্যায়ে রয়েছে। - $brand"
      order.status.equals("Shipped", ignoreCase = true) ->
        "প্রিয় $name, আপনার অর্ডার #${order.id} কুরিয়ারে পাঠানো হয়েছে!$trackingPart দ্রুত ডেলিভারি পাবেন। - $brand"
      order.status.equals("Delivered", ignoreCase = true) ->
        "প্রিয় $name, আপনার অর্ডার #${order.id} সফলভাবে ডেলিভারি সম্পন্ন হয়েছে! কেনাকাটার জন্য ধন্যবাদ - $brand"
      order.status.equals("Cancelled", ignoreCase = true) ->
        "প্রিয় $name, আপনার অর্ডার #${order.id} বাতিল করা হয়েছে। যেকোনো সহায়তার জন্য কল করুন - $brand"
      else ->
        "প্রিয় $name, আপনার অর্ডার #${order.id} (৳${order.total}) বর্তমান স্ট্যাটাস: ${order.status}।$trackingPart - $brand"
    }
  }

  fun defaultTemplates(storeName: String = "SAJGOJ FASHION"): List<SmsTemplate> = listOf(
    SmsTemplate(
      id = "tpl_order_confirm",
      title = "✅ অর্ডার কনফার্মেশন",
      type = "status_update",
      body = "প্রিয় {name}, আপনার অর্ডার #{orderId} (৳{total}) কনফার্ম হয়েছে! শীঘ্রই কুরিয়ারে পাঠানো হবে। - $storeName"
    ),
    SmsTemplate(
      id = "tpl_courier_shipped",
      title = "🚚 কুরিয়ার ট্র্যাকিং",
      type = "status_update",
      body = "প্রিয় {name}, আপনার অর্ডার #{orderId} কুরিয়ারে ডিসপ্যাচ হয়েছে। ট্র্যাকিং আইডি: {trackingId}। - $storeName"
    ),
    SmsTemplate(
      id = "tpl_delivered_thanks",
      title = "🎉 ডেলিভারি ও রিভিউ",
      type = "status_update",
      body = "প্রিয় {name}, অর্ডার #{orderId} ডেলিভারি সম্পন্ন! পণ্যের মান কেমন লাগলো জানাবেন। - $storeName"
    ),
    SmsTemplate(
      id = "tpl_eid_promo",
      title = "🎁 ঈদ ও স্পেশাল অফার (Bulk)",
      type = "bulk_promo",
      body = "প্রিয় {name}, $storeName-এ চলছে স্পেশাল ২০% ছাড় ও ফ্রি ডেলিভারি! আজই পছন্দের শাড়ি ও জুয়েলারি অর্ডার করুন।"
    ),
    SmsTemplate(
      id = "tpl_new_arrival",
      title = "✨ নতুন কালেকশন অ্যালার্ট (Bulk)",
      type = "bulk_promo",
      body = "হ্যালো {name}! আমাদের নতুন ব্রাইডাল ও জামদানি কালেকশন স্টকে এসেছে। ভিজিট করুন $storeName!"
    )
  )
}

object SimSmsGatewayManager {
  fun hasSendSmsPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.SEND_SMS
    ) == PackageManager.PERMISSION_GRANTED
  }

  fun deviceModelLabel(): String {
    val brand = Build.MANUFACTURER.orEmpty().trim()
    val model = Build.MODEL.orEmpty().trim()
    return "$brand $model".trim().ifBlank { "Admin Android Phone" }.take(80)
  }

  /**
   * Sends SMS directly from the phone's SIM card using Android's SmsManager.
   * Supports multi-part Bangla Unicode text automatically via divideMessage().
   */
  fun sendViaSim(context: Context, rawPhone: String, message: String): Result<Int> {
    if (!hasSendSmsPermission(context)) {
      return Result.failure(SecurityException("SIM SMS অনুমতি (SEND_SMS) দেওয়া হয়নি"))
    }
    val phone = SmsHelper.normalizePhone(rawPhone)
    if (!SmsHelper.isValidPhone(phone)) {
      return Result.failure(IllegalArgumentException("সঠিক মোবাইল নম্বর নয়: $rawPhone"))
    }
    val trimmedMsg = message.trim()
    if (trimmedMsg.isEmpty()) {
      return Result.failure(IllegalArgumentException("মেসেজ খালি রাখা যাবে না"))
    }
    return try {
      val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(SmsManager::class.java)
      } else {
        @Suppress("DEPRECATION")
        SmsManager.getDefault()
      }
      val parts = smsManager.divideMessage(trimmedMsg)
      if (parts.size > 1) {
        smsManager.sendMultipartTextMessage(phone, null, parts, null, null)
      } else {
        smsManager.sendTextMessage(phone, null, trimmedMsg, null, null)
      }
      Result.success(parts.size.coerceAtLeast(1))
    } catch (se: SecurityException) {
      Result.failure(se)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Play-Store-compliant 1-click SMS launcher (no SEND_SMS permission required).
   * Opens the device's default SMS app pre-filled with recipient & body.
   */
  fun launchPlaySafeSmsIntent(context: Context, rawPhone: String, message: String): Boolean {
    val phone = SmsHelper.normalizePhone(rawPhone)
    if (!SmsHelper.isValidPhone(phone)) {
      Toast.makeText(context, "⚠️ সঠিক মোবাইল নম্বর পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
      return false
    }
    return try {
      val uri = Uri.parse("smsto:${Uri.encode(phone)}")
      val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra("sms_body", message)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      Toast.makeText(context, "⚠️ ফোনে ডিফল্ট SMS অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
      false
    }
  }
}
