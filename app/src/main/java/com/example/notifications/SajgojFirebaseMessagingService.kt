package com.example.notifications

import android.util.Log
import com.example.data.model.Order
import com.example.data.model.OrderStatusNotification
import com.example.firebase.GoogleAuthHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "SajgojFcmService"

class SajgojFirebaseMessagingService : FirebaseMessagingService() {

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    OrderNotificationHelper.saveFcmToken(applicationContext, token)
    val currentUser = try {
      Firebase.auth.currentUser
    } catch (_: Exception) {
      null
    }
    if (currentUser != null) {
      syncTokenToCustomerProfile(
        uid = currentUser.uid,
        displayName = currentUser.displayName.orEmpty().ifBlank { "সাজগোজ গ্রাহক" },
        email = currentUser.email.orEmpty(),
        token = token
      )
    }
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    val data = remoteMessage.data
    val orderId = data["orderId"]?.trim().orEmpty()
    val previousStatus = data["previousStatus"]?.trim().orEmpty().ifBlank { "Pending" }
    val newStatus = data["newStatus"]?.trim().orEmpty()
    val trackingId = data["trackingId"]?.trim().orEmpty()
    val deliveredBy = data["deliveredBy"]?.trim().orEmpty()
    val customerName = data["customerName"]?.trim().orEmpty()
    val explicitTitle = data["title"]?.trim().orEmpty()
      .ifBlank { remoteMessage.notification?.title?.trim().orEmpty() }
    val explicitBody = data["message"]?.trim().orEmpty()
      .ifBlank { data["body"]?.trim().orEmpty() }
      .ifBlank { remoteMessage.notification?.body?.trim().orEmpty() }

    val nowMs = System.currentTimeMillis()
    val notificationItem = if (orderId.isNotBlank() && newStatus.isNotBlank() && explicitTitle.isBlank()) {
      val syntheticOrder = Order(
        id = orderId,
        customerName = customerName,
        phone = "",
        district = "",
        thana = "",
        address = "",
        items = emptyList(),
        subtotal = 0,
        deliveryCharge = 0,
        discount = 0,
        total = 0,
        paymentMethod = "",
        status = newStatus,
        date = "",
        trackingId = trackingId,
        deliveredBy = deliveredBy
      )
      OrderNotificationHelper.buildStatusChangeNotification(
        order = syntheticOrder,
        previousStatus = previousStatus,
        channel = "fcm",
        timestampMs = nowMs
      )
    } else {
      OrderStatusNotification(
        id = "fcm_${orderId.ifBlank { "msg" }}_$nowMs",
        orderId = orderId.ifBlank { "SAJGOJ" },
        customerName = customerName,
        previousStatus = previousStatus,
        newStatus = newStatus.ifBlank { "Updated" },
        title = explicitTitle.ifBlank { "🔔 অর্ডার আপডেট" },
        message = explicitBody.ifBlank { "আপনার অর্ডারের স্ট্যাটাস আপডেট করা হয়েছে।" },
        trackingId = trackingId,
        deliveredBy = deliveredBy,
        channel = "fcm",
        read = false,
        createdAtMs = nowMs
      )
    }

    OrderNotificationHelper.postSystemNotification(applicationContext, notificationItem)
    OrderNotificationBus.publish(notificationItem)
  }

  private fun syncTokenToCustomerProfile(
    uid: String,
    displayName: String,
    email: String,
    token: String
  ) {
    try {
      val db = GoogleAuthHelper.createFirestore(applicationContext)
      val docRef = db.collection("customers").document(uid)
      docRef.get()
        .addOnSuccessListener { snap ->
          if (snap.exists()) {
            docRef.update(
              mapOf(
                "fcmToken" to token,
                "notificationsEnabled" to true,
                "updatedAt" to FieldValue.serverTimestamp()
              )
            ).addOnFailureListener { e ->
              Log.w(TAG, "Failed updating FCM token on existing customer profile", e)
            }
          } else {
            docRef.set(
              mapOf(
                "userId" to uid,
                "name" to displayName,
                "email" to email,
                "fcmToken" to token,
                "notificationsEnabled" to true,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
              )
            ).addOnFailureListener { e ->
              Log.w(TAG, "Failed creating customer profile with FCM token", e)
            }
          }
        }
        .addOnFailureListener { e ->
          Log.w(TAG, "Failed checking customer profile for FCM token sync", e)
        }
    } catch (e: Exception) {
      Log.w(TAG, "Unable to sync FCM token to Firestore", e)
    }
  }
}
