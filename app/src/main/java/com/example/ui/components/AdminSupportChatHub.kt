package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.SupportChatFeedState
import com.example.data.model.SupportChatMessage
import com.example.data.model.SupportChatThread
import com.example.firebase.AuthUserSummary
import com.example.firebase.BOOTSTRAPPED_ADMIN_EMAIL
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

private val adminQuickReplyTemplates = listOf(
  "আসসালামু আলাইকুম! সাজগোজ ফ্যাশনে স্বাগতম, কীভাবে সাহায্য করতে পারি?",
  "আপনার অর্ডারটি নিশ্চিত করা হয়েছে ও শীঘ্রই ডেলিভারির জন্য পাঠানো হবে।",
  "আপনার পার্সেলটি কুরিয়ারে হস্তান্তর করা হয়েছে। খুব দ্রুত হাতে পাবেন ইনশাআল্লাহ।",
  "ধন্যবাদ! অন্য কোনো তথ্য লাগলে নির্দ্বিধায় আমাদের জানান।"
)

private fun formatAdminChatTime(ts: Timestamp?): String {
  if (ts == null) return "এখনই"
  return try {
    SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(ts.toDate())
  } catch (_: Exception) {
    ""
  }
}

@Composable
fun AdminSupportChatHub(
  authUser: AuthUserSummary?,
  isAuthBusy: Boolean,
  threadsState: SupportChatFeedState<List<SupportChatThread>>,
  selectedChatId: String?,
  messagesState: SupportChatFeedState<List<SupportChatMessage>>,
  isSendingReply: Boolean,
  onGoogleSignInAdmin: () -> Unit,
  onSelectThread: (String?) -> Unit,
  onSendReply: (String, String) -> Unit,
  onToggleThreadStatus: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  if (authUser?.isAdmin != true) {
    Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      GlossyAdminCard(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 460.dp),
        cornerRadius = 22.dp,
        accentBorderColor = AdminCardBorderWarm,
        elevation = 8.dp,
        contentPadding = 22.dp
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(AdminV2SkySoft),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Forum,
              contentDescription = "সাপোর্ট চ্যাট ইনবক্স",
              tint = AdminV2CyanDeep,
              modifier = Modifier.size(28.dp)
            )
          }
          Text(
            text = "রিয়েল-টাইম কাস্টমার সাপোর্ট ইনবক্স",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = AdminInkPrimary
          )
          Text(
            text = "গ্রাহকদের লাইভ চ্যাট দেখতে এবং উত্তর দিতে অনুগ্রহ করে ভেরিফাইড অ্যাডমিন Google অ্যাকাউন্ট ($BOOTSTRAPPED_ADMIN_EMAIL) দিয়ে সাইন-ইন করুন।",
            fontSize = 12.5.sp,
            lineHeight = 18.sp,
            color = AdminInkSecondary
          )
          Button(
            onClick = onGoogleSignInAdmin,
            enabled = !isAuthBusy,
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 48.dp)
              .testTag("admin_support_google_signin_btn")
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
              Text("Google দিয়ে অ্যাডমিন সাইন-ইন করুন", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
    return
  }

  val allThreads = (threadsState as? SupportChatFeedState.Ready)?.data.orEmpty()
  var searchQuery by remember { mutableStateOf("") }
  var filterMode by remember { mutableStateOf("all") } // all, open, resolved

  val filteredThreads = remember(allThreads, searchQuery, filterMode) {
    val q = searchQuery.trim().lowercase()
    allThreads.filter { t ->
      val matchesFilter = when (filterMode) {
        "unread" -> t.unreadByAdmin > 0
        "open" -> t.status != "resolved"
        "resolved" -> t.status == "resolved"
        else -> true
      }
      val matchesSearch = q.isEmpty() ||
        t.customerName.lowercase().contains(q) ||
        t.customerEmail.lowercase().contains(q) ||
        t.lastMessage.lowercase().contains(q) ||
        t.orderId.lowercase().contains(q)
      matchesFilter && matchesSearch
    }
  }

  val selectedThread = remember(allThreads, selectedChatId) {
    allThreads.firstOrNull { it.id == selectedChatId || it.userId == selectedChatId }
  }

  val totalUnread = remember(allThreads) { allThreads.sumOf { it.unreadByAdmin } }
  val openCount = remember(allThreads) { allThreads.count { it.status != "resolved" } }

  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isTwoPane = maxWidth >= 680.dp

    if (!isTwoPane) {
      BackHandler(enabled = selectedChatId != null) {
        onSelectThread(null)
      }
    }

    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Top Summary Bar
      GlossyAdminCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        contentPadding = 12.dp
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "💬 লাইভ কাস্টমার সাপোর্ট ইনবক্স",
              fontSize = 15.5.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary
            )
            Text(
              text = "মোট ${allThreads.size}টি কথোপকথন • চলমান $openCount • অপঠিত $totalUnread",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = AdminV2CyanDeep
            )
          }
          if (totalUnread > 0) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = AdminV2Rose
            ) {
              Text(
                text = "$totalUnread নতুন বার্তা",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }
      }

      if (isTwoPane) {
        Row(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          AdminThreadListPane(
            threadsState = threadsState,
            threads = filteredThreads,
            selectedChatId = selectedChatId,
            searchQuery = searchQuery,
            filterMode = filterMode,
            onSearchChange = { searchQuery = it },
            onFilterChange = { filterMode = it },
            onSelectThread = { onSelectThread(it) },
            modifier = Modifier
              .weight(0.38f)
              .fillMaxHeight()
          )
          AdminConversationPane(
            selectedThread = selectedThread,
            messagesState = messagesState,
            isSendingReply = isSendingReply,
            showBackButton = false,
            onBack = { onSelectThread(null) },
            onSendReply = onSendReply,
            onToggleThreadStatus = onToggleThreadStatus,
            modifier = Modifier
              .weight(0.62f)
              .fillMaxHeight()
          )
        }
      } else {
        if (selectedChatId == null || selectedThread == null) {
          AdminThreadListPane(
            threadsState = threadsState,
            threads = filteredThreads,
            selectedChatId = selectedChatId,
            searchQuery = searchQuery,
            filterMode = filterMode,
            onSearchChange = { searchQuery = it },
            onFilterChange = { filterMode = it },
            onSelectThread = { onSelectThread(it) },
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
          )
        } else {
          AdminConversationPane(
            selectedThread = selectedThread,
            messagesState = messagesState,
            isSendingReply = isSendingReply,
            showBackButton = true,
            onBack = { onSelectThread(null) },
            onSendReply = onSendReply,
            onToggleThreadStatus = onToggleThreadStatus,
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
          )
        }
      }
    }
  }
}

@Composable
private fun AdminThreadListPane(
  threadsState: SupportChatFeedState<List<SupportChatThread>>,
  threads: List<SupportChatThread>,
  selectedChatId: String?,
  searchQuery: String,
  filterMode: String,
  onSearchChange: (String) -> Unit,
  onFilterChange: (String) -> Unit,
  onSelectThread: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  GlossyAdminCard(
    modifier = modifier,
    cornerRadius = 18.dp,
    contentPadding = 10.dp
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("গ্রাহকের নাম, ইমেইল বা বার্তা খুঁজুন...", fontSize = 12.sp) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "সার্চ",
            tint = AdminV2CyanDeep,
            modifier = Modifier.size(18.dp)
          )
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = adminOutlinedTextFieldColors(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("admin_support_search_input")
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val filters = listOf(
          "all" to "সব",
          "unread" to "অপঠিত",
          "open" to "চলমান",
          "resolved" to "সমাধানকৃত"
        )
        filters.forEach { (key, label) ->
          GlossySegmentPill(
            label = label,
            selected = filterMode == key,
            onClick = { onFilterChange(key) }
          )
        }
      }

      HorizontalDivider(color = AdminCardBorderSoft)

      Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
        when (threadsState) {
          is SupportChatFeedState.Loading -> {
            CircularProgressIndicator(
              color = AdminV2CyanDeep,
              modifier = Modifier.align(Alignment.Center)
            )
          }

          is SupportChatFeedState.Error -> {
            Text(
              text = threadsState.message,
              color = AdminV2Rose,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier
                .align(Alignment.Center)
                .padding(12.dp)
            )
          }

          else -> {
            if (threads.isEmpty()) {
              Column(
                modifier = Modifier
                  .align(Alignment.Center)
                  .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.MarkChatRead,
                  contentDescription = null,
                  tint = AdminV2CyanDeep,
                  modifier = Modifier.size(34.dp)
                )
                Text(
                  text = "কোনো সাপোর্ট মেসেজ পাওয়া যায়নি",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary
                )
                Text(
                  text = "গ্রাহকরা লাইভ চ্যাটে বার্তা পাঠালে এখানে রিয়েল-টাইমে দেখা যাবে।",
                  fontSize = 11.5.sp,
                  color = AdminInkMuted
                )
              }
            } else {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                items(threads, key = { it.id.ifBlank { it.userId } }) { thread ->
                  val chatId = thread.id.ifBlank { thread.userId }
                  val isSelected = selectedChatId == chatId
                  val isResolved = thread.status == "resolved"

                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) AdminV2SkySoft else Color.White,
                    border = BorderStroke(
                      width = if (isSelected) 1.5.dp else 1.dp,
                      color = if (isSelected) AdminV2CyanDeep else AdminCardBorderSoft
                    ),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(14.dp))
                      .clickable { onSelectThread(chatId) }
                      .testTag("admin_support_thread_$chatId")
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(
                            Brush.linearGradient(listOf(AdminV2Cyan, AdminV2CyanDeep))
                          ),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = thread.customerName.firstOrNull()?.uppercase() ?: "G",
                          fontSize = 15.sp,
                          fontWeight = FontWeight.Black,
                          color = Color.White
                        )
                      }

                      Column(modifier = Modifier.weight(1f)) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = thread.customerName.ifBlank { "সাজগোজ গ্রাহক" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminInkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                          )
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = formatAdminChatTime(thread.updatedAt),
                            fontSize = 9.5.sp,
                            color = AdminInkMuted
                          )
                        }

                        Text(
                          text = thread.lastMessage,
                          fontSize = 11.5.sp,
                          color = if (thread.unreadByAdmin > 0) AdminInkPrimary else AdminInkSecondary,
                          fontWeight = if (thread.unreadByAdmin > 0) FontWeight.Bold else FontWeight.Normal,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                          Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isResolved) Color(0xFFDCFCE7) else Color(0xFFE0F2FE)
                          ) {
                            Text(
                              text = if (isResolved) "Resolved" else "Open",
                              fontSize = 9.5.sp,
                              fontWeight = FontWeight.Bold,
                              color = if (isResolved) Color(0xFF15803D) else AdminV2CyanDeep,
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                          }
                          if (thread.orderId.isNotBlank()) {
                            Text(
                              text = "#${thread.orderId.takeLast(8)}",
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold,
                              color = AdminV2CyanDeep
                            )
                          }
                        }
                      }

                      if (thread.unreadByAdmin > 0) {
                        Surface(
                          shape = CircleShape,
                          color = AdminV2Rose
                        ) {
                          Text(
                            text = thread.unreadByAdmin.toString(),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
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
      }
    }
  }
}

@Composable
private fun AdminConversationPane(
  selectedThread: SupportChatThread?,
  messagesState: SupportChatFeedState<List<SupportChatMessage>>,
  isSendingReply: Boolean,
  showBackButton: Boolean,
  onBack: () -> Unit,
  onSendReply: (String, String) -> Unit,
  onToggleThreadStatus: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  GlossyAdminCard(
    modifier = modifier,
    cornerRadius = 18.dp,
    contentPadding = 0.dp
  ) {
    if (selectedThread == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Forum,
            contentDescription = null,
            tint = AdminV2CyanDeep,
            modifier = Modifier.size(38.dp)
          )
          Text(
            text = "যেকোনো একটি গ্রাহক চ্যাট নির্বাচন করুন",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Text(
            text = "বাম পাশের তালিকা থেকে কোনো কথোপকথনে ট্যাপ করলে মেসেজ দেখা ও রিপ্লাই দেওয়া যাবে।",
            fontSize = 12.sp,
            color = AdminInkMuted
          )
        }
      }
      return@GlossyAdminCard
    }

    val chatId = selectedThread.id.ifBlank { selectedThread.userId }
    val messages = (messagesState as? SupportChatFeedState.Ready)?.data.orEmpty()
    val listState = rememberLazyListState()
    var replyText by remember(chatId) { mutableStateOf("") }

    LaunchedEffect(messages.size) {
      if (messages.isNotEmpty()) {
        listState.animateScrollToItem(messages.size - 1)
      }
    }

    Column(modifier = Modifier.fillMaxSize()) {
      // Conversation Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AdminV2IceSurface)
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (showBackButton) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(48.dp)
              .testTag("admin_support_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "তালিকায় ফিরুন",
              tint = AdminV2CyanDeep
            )
          }
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = selectedThread.customerName.ifBlank { "সাজগোজ গ্রাহক" },
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = AdminInkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            if (selectedThread.customerEmail.isNotBlank()) {
              Text(
                text = selectedThread.customerEmail,
                fontSize = 11.sp,
                color = AdminInkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            if (selectedThread.orderId.isNotBlank()) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = AdminV2SkySoft
              ) {
                Text(
                  text = "অর্ডার #${selectedThread.orderId.takeLast(8)}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminV2CyanDeep,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }

        val isResolved = selectedThread.status == "resolved"
        OutlinedButton(
          onClick = { onToggleThreadStatus(chatId, selectedThread.status) },
          border = BorderStroke(1.dp, if (isResolved) AdminV2CyanDeep else Color(0xFF15803D)),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier
            .heightIn(min = 48.dp)
            .testTag("admin_support_status_toggle_btn")
        ) {
          Icon(
            imageVector = if (isResolved) Icons.Default.Refresh else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isResolved) AdminV2CyanDeep else Color(0xFF15803D),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isResolved) "Reopen" else "Resolve",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isResolved) AdminV2CyanDeep else Color(0xFF15803D)
          )
        }
      }

      HorizontalDivider(color = AdminCardBorderSoft)

      // Messages Feed
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .background(Color(0xFFF8FAFC))
      ) {
        when (messagesState) {
          is SupportChatFeedState.Loading -> {
            CircularProgressIndicator(
              color = AdminV2CyanDeep,
              modifier = Modifier.align(Alignment.Center)
            )
          }

          is SupportChatFeedState.Error -> {
            Text(
              text = messagesState.message,
              color = AdminV2Rose,
              fontSize = 12.sp,
              modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
            )
          }

          else -> {
            LazyColumn(
              state = listState,
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(12.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(messages, key = { it.id.ifBlank { "${it.senderId}_${it.text.hashCode()}" } }) { msg ->
                val isAdminMsg = msg.senderRole == "admin"
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalAlignment = if (isAdminMsg) Alignment.End else Alignment.Start
                ) {
                  Surface(
                    shape = RoundedCornerShape(
                      topStart = 16.dp,
                      topEnd = 16.dp,
                      bottomStart = if (isAdminMsg) 16.dp else 4.dp,
                      bottomEnd = if (isAdminMsg) 4.dp else 16.dp
                    ),
                    color = if (isAdminMsg) AdminV2CyanDeep else Color.White,
                    border = if (isAdminMsg) null else BorderStroke(1.dp, AdminCardBorderSoft),
                    modifier = Modifier.widthIn(max = 340.dp)
                  ) {
                    Column(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                      verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                      Text(
                        text = if (isAdminMsg) "অ্যাডমিন রিপ্লাই" else msg.senderName.ifBlank { "গ্রাহক" },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isAdminMsg) Color.White.copy(alpha = 0.85f) else AdminV2CyanDeep
                      )
                      Text(
                        text = msg.text,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = if (isAdminMsg) Color.White else AdminInkPrimary
                      )
                      Text(
                        text = formatAdminChatTime(msg.createdAt),
                        fontSize = 9.5.sp,
                        color = if (isAdminMsg) Color.White.copy(alpha = 0.78f) else AdminInkMuted
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Quick Reply Templates Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AdminV2IceSurface)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        adminQuickReplyTemplates.forEach { tpl ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, AdminCardBorderWarm),
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable { replyText = tpl }
          ) {
            Text(
              text = tpl.take(36) + if (tpl.length > 36) "…" else "",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = AdminV2CyanDeep,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      HorizontalDivider(color = AdminCardBorderSoft)

      // Reply Composer
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = replyText,
          onValueChange = { replyText = it },
          placeholder = {
            Text("গ্রাহককে উত্তর লিখুন...", fontSize = 13.sp, color = AdminInkMuted)
          },
          singleLine = true,
          shape = RoundedCornerShape(20.dp),
          colors = adminOutlinedTextFieldColors(),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(
            onSend = {
              if (replyText.isNotBlank() && !isSendingReply) {
                val payload = replyText
                replyText = ""
                onSendReply(chatId, payload)
              }
            }
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("admin_support_reply_input")
        )

        val canSend = replyText.isNotBlank() && !isSendingReply
        Surface(
          shape = CircleShape,
          color = if (canSend) AdminV2CyanDeep else AdminV2CyanDeep.copy(alpha = 0.35f),
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(enabled = canSend) {
              val payload = replyText
              replyText = ""
              onSendReply(chatId, payload)
            }
            .testTag("admin_support_reply_send_btn")
        ) {
          Box(contentAlignment = Alignment.Center) {
            if (isSendingReply) {
              CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
              )
            } else {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "রিপ্লাই পাঠান",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  }
}
