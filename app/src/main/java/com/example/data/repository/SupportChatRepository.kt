package com.example.data.repository

import android.content.Context
import com.example.data.model.SupportChatFeedState
import com.example.data.model.SupportChatMessage
import com.example.data.model.SupportChatThread
import com.example.firebase.AuthUserSummary
import com.example.firebase.GoogleAuthHelper
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

class SupportChatRepository(private val context: Context) {

  private val firestore: FirebaseFirestore? by lazy {
    try {
      if (FirebaseApp.getApps(context).isNotEmpty()) {
        GoogleAuthHelper.createFirestore(context)
      } else {
        null
      }
    } catch (_: Exception) {
      null
    }
  }

  fun observeCustomerThread(uid: String): Flow<SupportChatFeedState<SupportChatThread?>> {
    val db = firestore ?: return flowOf(SupportChatFeedState.Ready(null))
    if (uid.isBlank()) return flowOf(SupportChatFeedState.Idle)

    return db.collection("support_chats")
      .document(uid)
      .snapshots()
      .map<DocumentSnapshot, SupportChatFeedState<SupportChatThread?>> { snap ->
        if (!snap.exists()) {
          SupportChatFeedState.Ready(null)
        } else {
          val thread = snap.toObject(
            SupportChatThread::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
          SupportChatFeedState.Ready(thread)
        }
      }
      .onStart { emit(SupportChatFeedState.Loading) }
      .catch { err ->
        if (err is CancellationException) throw err
        handleFirestoreError(err, OperationType.GET, "support_chats/$uid")
        emit(SupportChatFeedState.Error("চ্যাট থ্রেড লোড করা যায়নি। আবার চেষ্টা করুন।"))
      }
  }

  fun observeAdminThreads(): Flow<SupportChatFeedState<List<SupportChatThread>>> {
    val db = firestore ?: return flowOf(SupportChatFeedState.Ready(emptyList()))

    return db.collection("support_chats")
      .orderBy("updatedAt", Query.Direction.DESCENDING)
      .snapshots()
      .map<com.google.firebase.firestore.QuerySnapshot, SupportChatFeedState<List<SupportChatThread>>> { snap ->
        val threads = snap.documents.mapNotNull { doc ->
          doc.toObject(
            SupportChatThread::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
        }
        SupportChatFeedState.Ready(threads)
      }
      .onStart { emit(SupportChatFeedState.Loading) }
      .catch { err ->
        if (err is CancellationException) throw err
        handleFirestoreError(err, OperationType.LIST, "support_chats")
        emit(SupportChatFeedState.Error("সাপোর্ট ইনবক্স লোড করা যায়নি। ভেরিফাইড অ্যাডমিন Google সাইন-ইন নিশ্চিত করুন।"))
      }
  }

  fun observeMessages(chatId: String): Flow<SupportChatFeedState<List<SupportChatMessage>>> {
    val db = firestore ?: return flowOf(SupportChatFeedState.Ready(emptyList()))
    if (chatId.isBlank()) return flowOf(SupportChatFeedState.Idle)

    return db.collection("support_chats")
      .document(chatId)
      .collection("messages")
      .orderBy("createdAt", Query.Direction.ASCENDING)
      .snapshots()
      .map<com.google.firebase.firestore.QuerySnapshot, SupportChatFeedState<List<SupportChatMessage>>> { snap ->
        val msgs = snap.documents.mapNotNull { doc ->
          doc.toObject(
            SupportChatMessage::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
        }
        SupportChatFeedState.Ready(msgs)
      }
      .onStart { emit(SupportChatFeedState.Loading) }
      .catch { err ->
        if (err is CancellationException) throw err
        handleFirestoreError(err, OperationType.LIST, "support_chats/$chatId/messages")
        emit(SupportChatFeedState.Error("মেসেজ লোড করা যায়নি।"))
      }
  }

  suspend fun sendCustomerMessage(
    user: AuthUserSummary,
    text: String,
    orderId: String = ""
  ): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore সংযোগ প্রস্তুত নয়।"))
    val cleanText = text.trim().take(1000)
    if (user.uid.isBlank() || cleanText.isEmpty()) {
      return Result.failure(IllegalArgumentException("সঠিক বার্তা লিখুন।"))
    }
    val safeName = user.displayName.trim().ifBlank { "সাজগোজ গ্রাহক" }.take(120)
    val safeEmail = user.email.trim().take(160)
    val safePhoto = user.photoUrl.trim().take(1000)
    val cleanOrderId = orderId.trim().take(128)

    return try {
      val threadRef = db.collection("support_chats").document(user.uid)
      val msgRef = threadRef.collection("messages").document()
      val existingThread = threadRef.get().await()

      val batch = db.batch()
      if (!existingThread.exists()) {
        val threadPayload = mutableMapOf<String, Any?>(
          "userId" to user.uid,
          "customerName" to safeName,
          "customerEmail" to safeEmail,
          "customerPhotoUrl" to safePhoto,
          "lastMessage" to cleanText,
          "lastSenderRole" to "customer",
          "status" to "open",
          "unreadByAdmin" to 1,
          "unreadByCustomer" to 0,
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp()
        )
        if (cleanOrderId.isNotEmpty()) {
          threadPayload["orderId"] = cleanOrderId
        }
        batch.set(threadRef, threadPayload.filterValues { it != null })
      } else {
        val updatePayload = mutableMapOf<String, Any?>(
          "customerName" to safeName,
          "customerEmail" to safeEmail,
          "customerPhotoUrl" to safePhoto,
          "lastMessage" to cleanText,
          "lastSenderRole" to "customer",
          "status" to "open",
          "unreadByAdmin" to FieldValue.increment(1),
          "updatedAt" to FieldValue.serverTimestamp()
        )
        if (cleanOrderId.isNotEmpty()) {
          updatePayload["orderId"] = cleanOrderId
        }
        batch.update(threadRef, updatePayload.filterValues { it != null })
      }

      val msgPayload = mutableMapOf<String, Any?>(
        "senderId" to user.uid,
        "senderName" to safeName,
        "senderRole" to "customer",
        "text" to cleanText,
        "createdAt" to FieldValue.serverTimestamp()
      )
      if (cleanOrderId.isNotEmpty()) {
        msgPayload["orderId"] = cleanOrderId
      }
      batch.set(msgRef, msgPayload.filterValues { it != null })

      batch.commit().await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, "support_chats/${user.uid}")
      Result.failure(e)
    }
  }

  suspend fun sendAdminReply(
    adminUser: AuthUserSummary,
    chatId: String,
    text: String
  ): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore সংযোগ প্রস্তুত নয়।"))
    val cleanText = text.trim().take(1000)
    if (!adminUser.isAdmin || adminUser.uid.isBlank() || chatId.isBlank() || cleanText.isEmpty()) {
      return Result.failure(IllegalArgumentException("ভেরিফাইড অ্যাডমিন সেশন এবং সঠিক উত্তর প্রয়োজন।"))
    }
    val safeAdminName = adminUser.displayName.trim().ifBlank { "SAJGOJ সাপোর্ট টিম" }.take(120)

    return try {
      val threadRef = db.collection("support_chats").document(chatId)
      val msgRef = threadRef.collection("messages").document()
      val batch = db.batch()

      batch.update(
        threadRef,
        mapOf(
          "lastMessage" to cleanText,
          "lastSenderRole" to "admin",
          "status" to "open",
          "unreadByAdmin" to 0,
          "unreadByCustomer" to FieldValue.increment(1),
          "updatedAt" to FieldValue.serverTimestamp()
        )
      )

      batch.set(
        msgRef,
        mapOf(
          "senderId" to adminUser.uid,
          "senderName" to safeAdminName,
          "senderRole" to "admin",
          "text" to cleanText,
          "createdAt" to FieldValue.serverTimestamp()
        )
      )

      batch.commit().await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, "support_chats/$chatId")
      Result.failure(e)
    }
  }

  suspend fun markThreadReadByCustomer(chatId: String): Result<Unit> {
    val db = firestore ?: return Result.success(Unit)
    if (chatId.isBlank()) return Result.success(Unit)
    return try {
      db.collection("support_chats")
        .document(chatId)
        .update(
          mapOf(
            "unreadByCustomer" to 0,
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )
        .await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "support_chats/$chatId")
      Result.failure(e)
    }
  }

  suspend fun markThreadReadByAdmin(chatId: String): Result<Unit> {
    val db = firestore ?: return Result.success(Unit)
    if (chatId.isBlank()) return Result.success(Unit)
    return try {
      db.collection("support_chats")
        .document(chatId)
        .update(
          mapOf(
            "unreadByAdmin" to 0,
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )
        .await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "support_chats/$chatId")
      Result.failure(e)
    }
  }

  suspend fun updateThreadStatus(chatId: String, newStatus: String): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore সংযোগ প্রস্তুত নয়।"))
    if (chatId.isBlank()) return Result.failure(IllegalArgumentException("Invalid chatId"))
    val safeStatus = if (newStatus == "resolved") "resolved" else "open"
    return try {
      db.collection("support_chats")
        .document(chatId)
        .update(
          mapOf(
            "status" to safeStatus,
            "unreadByAdmin" to 0,
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )
        .await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, "support_chats/$chatId")
      Result.failure(e)
    }
  }
}
