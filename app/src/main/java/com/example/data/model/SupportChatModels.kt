package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class SupportChatThread(
  @DocumentId val id: String = "",
  val userId: String = "",
  val customerName: String = "",
  val customerEmail: String = "",
  val customerPhotoUrl: String = "",
  val lastMessage: String = "",
  val lastSenderRole: String = "customer",
  val status: String = "open",
  val unreadByAdmin: Int = 0,
  val unreadByCustomer: Int = 0,
  val orderId: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SupportChatMessage(
  @DocumentId val id: String = "",
  val senderId: String = "",
  val senderName: String = "",
  val senderRole: String = "customer",
  val text: String = "",
  val orderId: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null
)

sealed interface SupportChatFeedState<out T> {
  data object Idle : SupportChatFeedState<Nothing>
  data object Loading : SupportChatFeedState<Nothing>
  data class Ready<T>(val data: T) : SupportChatFeedState<T>
  data class Error(val message: String) : SupportChatFeedState<Nothing>
}
