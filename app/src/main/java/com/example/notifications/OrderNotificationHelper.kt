package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.Order
import com.example.data.model.OrderStatusNotification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object OrderNotificationBus {
  private val _events = MutableSharedFlow<OrderStatusNotification>(extraBufferCapacity = 32)
  val events: SharedFlow<OrderStatusNotification> = _events.asSharedFlow()

  fun publish(notification: OrderStatusNotification) {
    _events.tryEmit(notification)
  }
}

object OrderNotificationHelper {
  const val CHANNEL_ID = "sajgoj_order_updates"
  const val CHANNEL_NAME = "অর্ডার স্ট্যাটাস আপডেট"
  const val CHANNEL_DESCRIPTION = "অর্ডার কনফার্ম, শিপড এবং ডেলিভারি স্ট্যাটাস পরিবর্তনের তাৎক্ষণিক অ্যালার্ট"
  const val EXTRA_ORDER_ID = "extra_notification_order_id"

  private const val FCM_PREFS_NAME = "sajgoj_fcm_prefs"
  private const val KEY_FCM_TOKEN = "fcm_device_token"
  private const val KEY_PUSH_ENABLED = "push_notifications_enabled"

  fun transitionKey(orderId: String, newStatus: String): String {
    return "${orderId.trim()}:${newStatus.trim().lowercase()}"
  }

  fun buildStatusChangeNotification(
    order: Order,
    previousStatus: String,
    channel: String = "in_app",
    userId: String = "",
    timestampMs: Long = System.currentTimeMillis()
  ): OrderStatusNotification {
    val cleanPrev = previousStatus.ifBlank { "Pending" }
    val cleanNext = order.status.ifBlank { "Updated" }
    val shortId = order.id.trim()
    val trackingInfo = if (order.trackingId.isNotBlank()) {
      val partner = order.deliveredBy.ifBlank { "Courier" }
      " ($partner • Track: ${order.trackingId})"
    } else if (order.deliveredBy.isNotBlank()) {
      " (${order.deliveredBy})"
    } else {
      ""
    }

    val (title, body) = when (cleanNext.lowercase()) {
      "confirmed" -> {
        "✅ অর্ডার #$shortId কনফার্ম হয়েছে!" to
          "প্রিয় ${order.customerName.ifBlank { "গ্রাহক" }}, আপনার অর্ডারটি '$cleanPrev' থেকে 'Confirmed' অবস্থায় আপডেট হয়েছে।"
      }
      "processing" -> {
        "⚙️ অর্ডার #$shortId প্রস্তুত হচ্ছে" to
          "আপনার অর্ডারটি প্যাকিং ও প্রসেসিং পর্যায়ে রয়েছে ($cleanPrev → Processing)।"
      }
      "to ship" -> {
        "📦 অর্ডার #$shortId ডিসপ্যাচের জন্য প্রস্তুত" to
          "আপনার পার্সেল কুরিয়ারে হ্যান্ডওভার করার জন্য রেডি করা হয়েছে।"
      }
      "shipped", "shipping" -> {
        "🚚 অর্ডার #$shortId শিপড হয়েছে!" to
          "আপনার অর্ডারটি '$cleanPrev' থেকে '$cleanNext' হয়েছে ও কুরিয়ারে পাঠানো হয়েছে$trackingInfo।"
      }
      "delivered" -> {
        "🎉 অর্ডার #$shortId ডেলিভারি সম্পন্ন!" to
          "আপনার অর্ডারটি সফলভাবে ডেলিভারি হয়েছে। সাজগোজ ফ্যাশনের সাথে থাকার জন্য ধন্যবাদ!"
      }
      "failed delivered" -> {
        "⚠️ অর্ডার #$shortId ডেলিভারি চেষ্টা ব্যর্থ" to
          "কুরিয়ার ডেলিভারি সম্পন্ন করতে পারেনি। দ্রুত আমাদের সাপোর্টে যোগাযোগ করুন।"
      }
      "cancelled", "cancellation" -> {
        "❌ অর্ডার #$shortId বাতিল করা হয়েছে" to
          "আপনার অর্ডারটির স্ট্যাটাস '$cleanPrev' থেকে '$cleanNext' হিসেবে আপডেট করা হয়েছে।"
      }
      else -> {
        "🔔 অর্ডার #$shortId আপডেট: $cleanNext" to
          "আপনার অর্ডারের স্ট্যাটাস '$cleanPrev' থেকে '$cleanNext' হয়েছে$trackingInfo।"
      }
    }

    val safeId = "notif_${shortId}_${cleanNext.replace(Regex("[^a-zA-Z0-9_-]"), "_")}_$timestampMs"
    return OrderStatusNotification(
      id = safeId,
      orderId = order.id,
      userId = userId,
      customerName = order.customerName,
      previousStatus = cleanPrev,
      newStatus = cleanNext,
      title = title,
      message = body,
      trackingId = order.trackingId,
      deliveredBy = order.deliveredBy,
      channel = channel,
      read = false,
      createdAtMs = timestampMs
    )
  }

  fun toastSummary(notification: OrderStatusNotification): String {
    return "${notification.title} — ${notification.message}"
  }

  fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
      val existing = manager.getNotificationChannel(CHANNEL_ID)
      if (existing == null) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          CHANNEL_NAME,
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = CHANNEL_DESCRIPTION
          enableVibration(true)
        }
        manager.createNotificationChannel(channel)
      }
    }
  }

  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
  }

  fun postSystemNotification(
    context: Context,
    notification: OrderStatusNotification
  ): Boolean {
    return try {
      ensureNotificationChannel(context)
      if (!hasNotificationPermission(context)) {
        return false
      }
      val launchIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(EXTRA_ORDER_ID, notification.orderId)
      }
      val pendingIntent = PendingIntent.getActivity(
        context,
        notification.orderId.hashCode(),
        launchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
      val built = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(notification.title)
        .setContentText(notification.message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()
      NotificationManagerCompat.from(context).notify(
        notification.orderId.hashCode() xor notification.newStatus.hashCode(),
        built
      )
      true
    } catch (_: SecurityException) {
      false
    } catch (_: Exception) {
      false
    }
  }

  fun saveFcmToken(context: Context, token: String) {
    if (token.isBlank()) return
    context.getSharedPreferences(FCM_PREFS_NAME, Context.MODE_PRIVATE)
      .edit()
      .putString(KEY_FCM_TOKEN, token.trim())
      .putBoolean(KEY_PUSH_ENABLED, true)
      .apply()
  }

  fun getSavedFcmToken(context: Context): String {
    return context.getSharedPreferences(FCM_PREFS_NAME, Context.MODE_PRIVATE)
      .getString(KEY_FCM_TOKEN, "")
      .orEmpty()
  }
}
