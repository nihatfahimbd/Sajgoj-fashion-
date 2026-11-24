package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Order
import com.example.data.model.StoreConfig
import com.example.data.model.SupportChatFeedState
import com.example.data.model.SupportChatMessage
import com.example.data.model.SupportChatThread
import com.example.firebase.AuthUserSummary
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.NavyBlueCardEnd
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.SlateTextSecondary
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

private val customerQuickInquiries = listOf(
  "আমার অর্ডারের ডেলিভারি আপডেট জানতে চাই",
  "পণ্যের সাইজ ও কালার সম্পর্কে জানতে চাই",
  "কাস্টম অর্ডার দিতে চাই",
  "রিটার্ন বা এক্সচেঞ্জ করতে চাই"
)

private fun formatChatTime(ts: Timestamp?): String {
  if (ts == null) return "এখনই"
  return try {
    SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(ts.toDate())
  } catch (_: Exception) {
    ""
  }
}

@Composable
fun LiveSupportChatDialog(
  config: StoreConfig,
  authUser: AuthUserSummary?,
  isAuthBusy: Boolean,
  orders: List<Order>,
  threadState: SupportChatFeedState<SupportChatThread?>,
  messagesState: SupportChatFeedState<List<SupportChatMessage>>,
  isSending: Boolean,
  onGoogleSignIn: () -> Unit,
  onSendMessage: (String, String) -> Unit,
  onMarkRead: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      border = BorderStroke(1.dp, BorderGold),
      tonalElevation = 6.dp,
      modifier = Modifier
        .padding(horizontal = 12.dp, vertical = 20.dp)
        .fillMaxWidth()
        .widthIn(max = 560.dp)
        .fillMaxHeight(0.86f)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(NavyBlue, NavyBlueCardEnd)))
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.SupportAgent,
                  contentDescription = "লাইভ সাপোর্ট",
                  tint = AntiqueGold,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "${config.storeName} লাইভ সাপোর্ট",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "সরাসরি অ্যাডমিনের সাথে রিয়েল-টাইম চ্যাট",
                  fontSize = 11.sp,
                  color = Color.White.copy(alpha = 0.88f)
                )
              }
            }
            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(48.dp)
                .testTag("live_support_dialog_close_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "সাপোর্ট চ্যাট বন্ধ করুন",
                tint = Color.White
              )
            }
          }
        }

        CustomerLiveSupportChatPane(
          authUser = authUser,
          isAuthBusy = isAuthBusy,
          orders = orders,
          threadState = threadState,
          messagesState = messagesState,
          isSending = isSending,
          onGoogleSignIn = onGoogleSignIn,
          onSendMessage = onSendMessage,
          onMarkRead = onMarkRead,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerLiveSupportChatPane(
  authUser: AuthUserSummary?,
  isAuthBusy: Boolean,
  orders: List<Order>,
  threadState: SupportChatFeedState<SupportChatThread?>,
  messagesState: SupportChatFeedState<List<SupportChatMessage>>,
  isSending: Boolean,
  onGoogleSignIn: () -> Unit,
  onSendMessage: (String, String) -> Unit,
  onMarkRead: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (authUser == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFFAFBFD))
        .padding(20.dp),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = CreamBackground,
        border = BorderStroke(1.dp, BorderGold),
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 420.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(RoyalMaroon.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Forum,
              contentDescription = "লাইভ চ্যাট",
              tint = RoyalMaroon,
              modifier = Modifier.size(28.dp)
            )
          }
          Text(
            text = "অ্যাডমিনের সাথে সরাসরি লাইভ চ্যাট",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          Text(
            text = "অর্ডার ট্র্যাকিং, ডেলিভারি বা কাস্টম ডিজাইনের জন্য সরাসরি আমাদের অ্যাডমিনের সাথে কথা বলুন। আপনার চ্যাট ইতিহাস নিরাপদে সংরক্ষিত থাকবে।",
            fontSize = 12.5.sp,
            lineHeight = 18.sp,
            color = DarkEspresso
          )
          Button(
            onClick = onGoogleSignIn,
            enabled = !isAuthBusy,
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 48.dp)
              .testTag("customer_live_chat_google_signin_btn")
          ) {
            if (isAuthBusy) {
              CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("সাইন-ইন হচ্ছে...", fontWeight = FontWeight.Bold)
            } else {
              Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Google দিয়ে সাইন-ইন করে চ্যাট শুরু করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
    return
  }

  val thread = (threadState as? SupportChatFeedState.Ready)?.data
  val messages = (messagesState as? SupportChatFeedState.Ready)?.data.orEmpty()
  var inputText by remember { mutableStateOf("") }
  var selectedOrderId by remember(thread?.orderId) { mutableStateOf(thread?.orderId.orEmpty()) }
  val listState = rememberLazyListState()

  LaunchedEffect(thread?.unreadByCustomer, messages.size) {
    if ((thread?.unreadByCustomer ?: 0) > 0) {
      onMarkRead()
    }
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(modifier = modifier.fillMaxSize()) {
    // Customer identity + thread status strip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CreamBackground)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = authUser.displayName,
          fontSize = 12.5.sp,
          fontWeight = FontWeight.Bold,
          color = DarkEspresso,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = authUser.email.ifBlank { "Google ভেরিফাইড গ্রাহক" },
          fontSize = 10.5.sp,
          color = SlateTextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      val isResolved = thread?.status == "resolved"
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isResolved) AccentGreen.copy(alpha = 0.14f) else NavyBlue.copy(alpha = 0.12f)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (isResolved) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = AccentGreen,
              modifier = Modifier.size(13.dp)
            )
          }
          Text(
            text = when {
              thread == null -> "নতুন চ্যাট"
              isResolved -> "সমাধানকৃত"
              else -> "চলমান (Live)"
            },
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isResolved) AccentGreen else NavyBlue
          )
        }
      }
    }

    // Optional recent order attachment selector
    if (orders.isNotEmpty()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "অর্ডার ট্যাগ:",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = SlateTextSecondary
        )
        orders.take(4).forEach { ord ->
          val active = selectedOrderId == ord.id
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (active) RoyalMaroon else CreamSurface,
            border = BorderStroke(1.dp, if (active) RoyalMaroon else BorderGold),
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable {
                selectedOrderId = if (active) "" else ord.id
              }
          ) {
            Text(
              text = "#${ord.id.takeLast(8)}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (active) Color.White else RoyalMaroon,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }
        }
      }
    }

    HorizontalDivider(color = BorderGold.copy(alpha = 0.6f))

    // Messages Feed
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .background(Color(0xFFFAFBFD))
    ) {
      when (messagesState) {
        is SupportChatFeedState.Loading -> {
          Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CircularProgressIndicator(color = RoyalMaroon, modifier = Modifier.size(28.dp))
            Text("বার্তা লোড হচ্ছে...", fontSize = 12.sp, color = SlateTextSecondary)
          }
        }

        is SupportChatFeedState.Error -> {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFEF2F2),
            border = BorderStroke(1.dp, RoyalMaroon.copy(alpha = 0.3f)),
            modifier = Modifier
              .align(Alignment.Center)
              .padding(16.dp)
          ) {
            Text(
              text = messagesState.message,
              fontSize = 12.5.sp,
              color = RoyalMaroon,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(14.dp)
            )
          }
        }

        else -> {
          if (messages.isEmpty()) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "👋 আমাদের সাপোর্ট টিম অনলাইনে আছে!",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Black,
                color = NavyBlue
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "নিচের যেকোনো প্রশ্ন বেছে নিন অথবা আপনার বার্তা লিখে পাঠান:",
                fontSize = 12.sp,
                color = SlateTextSecondary
              )
              Spacer(modifier = Modifier.height(14.dp))
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                customerQuickInquiries.forEach { preset ->
                  Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderGold),
                    modifier = Modifier
                      .clip(RoundedCornerShape(16.dp))
                      .clickable {
                        onSendMessage(preset, selectedOrderId)
                      }
                  ) {
                    Text(
                      text = preset,
                      fontSize = 11.5.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = RoyalMaroon,
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                  }
                }
              }
            }
          } else {
            LazyColumn(
              state = listState,
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(12.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(messages, key = { it.id.ifBlank { "${it.senderId}_${it.text.hashCode()}" } }) { msg ->
                val isCustomer = msg.senderRole != "admin"
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalAlignment = if (isCustomer) Alignment.End else Alignment.Start
                ) {
                  Surface(
                    shape = RoundedCornerShape(
                      topStart = 18.dp,
                      topEnd = 18.dp,
                      bottomStart = if (isCustomer) 18.dp else 4.dp,
                      bottomEnd = if (isCustomer) 4.dp else 18.dp
                    ),
                    color = if (isCustomer) RoyalMaroon else CreamSurface,
                    border = if (isCustomer) null else BorderStroke(1.dp, BorderGold),
                    modifier = Modifier.widthIn(max = 320.dp)
                  ) {
                    Column(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      if (!isCustomer) {
                        Text(
                          text = msg.senderName.ifBlank { "SAJGOJ সাপোর্ট" },
                          fontSize = 10.5.sp,
                          fontWeight = FontWeight.ExtraBold,
                          color = NavyBlue
                        )
                      }
                      if (msg.orderId.isNotBlank()) {
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (isCustomer) Color.White.copy(alpha = 0.18f) else NavyBlue.copy(alpha = 0.1f)
                        ) {
                          Text(
                            text = "অর্ডার #${msg.orderId.takeLast(8)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCustomer) Color.White else NavyBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                      Text(
                        text = msg.text,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = if (isCustomer) Color.White else DarkEspresso
                      )
                      Text(
                        text = formatChatTime(msg.createdAt),
                        fontSize = 9.5.sp,
                        color = if (isCustomer) Color.White.copy(alpha = 0.78f) else SlateTextSecondary
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    HorizontalDivider(color = BorderGold)

    // Composer Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        placeholder = {
          Text(
            text = "অ্যাডমিনকে বার্তা লিখুন...",
            fontSize = 13.sp,
            color = SlateTextSecondary
          )
        },
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = RoyalMaroon,
          unfocusedBorderColor = BorderGold,
          focusedContainerColor = CreamBackground,
          unfocusedContainerColor = CreamBackground
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(
          onSend = {
            if (inputText.isNotBlank() && !isSending) {
              val payload = inputText
              inputText = ""
              onSendMessage(payload, selectedOrderId)
            }
          }
        ),
        modifier = Modifier
          .weight(1f)
          .testTag("customer_live_chat_input")
      )

      val canSend = inputText.isNotBlank() && !isSending
      Surface(
        shape = CircleShape,
        color = if (canSend) RoyalMaroon else RoyalMaroon.copy(alpha = 0.35f),
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .clickable(enabled = canSend) {
            val payload = inputText
            inputText = ""
            onSendMessage(payload, selectedOrderId)
          }
          .testTag("customer_live_chat_send_btn")
      ) {
        Box(contentAlignment = Alignment.Center) {
          if (isSending) {
            CircularProgressIndicator(
              color = Color.White,
              strokeWidth = 2.dp,
              modifier = Modifier.size(18.dp)
            )
          } else {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "মেসেজ পাঠান",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}
