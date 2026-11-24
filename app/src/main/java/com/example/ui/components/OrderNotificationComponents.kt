package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OrderStatusNotification
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.RoyalMaroon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class NotificationVisualStyle(
  val containerBg: Color,
  val borderColor: Color,
  val accentColor: Color,
  val badgeBg: Color,
  val icon: ImageVector
)

private fun resolveNotificationVisualStyle(status: String): NotificationVisualStyle {
  return when (status.trim().lowercase(Locale.ROOT)) {
    "shipped", "shipping", "to ship" -> NotificationVisualStyle(
      containerBg = Color(0xFFF0F9FF),
      borderColor = Color(0xFF7DD3FC),
      accentColor = Color(0xFF0369A1),
      badgeBg = Color(0xFFE0F2FE),
      icon = Icons.Default.LocalShipping
    )
    "delivered" -> NotificationVisualStyle(
      containerBg = Color(0xFFECFDF5),
      borderColor = Color(0xFF6EE7B7),
      accentColor = Color(0xFF047857),
      badgeBg = Color(0xFFD1FAE5),
      icon = Icons.Default.CheckCircle
    )
    "cancelled", "cancellation", "failed delivered" -> NotificationVisualStyle(
      containerBg = Color(0xFFFEF2F2),
      borderColor = Color(0xFFFCA5A5),
      accentColor = Color(0xFFB91C1C),
      badgeBg = Color(0xFFFEE2E2),
      icon = Icons.Default.WarningAmber
    )
    else -> NotificationVisualStyle(
      containerBg = Color(0xFFFFFBEB),
      borderColor = BorderGold,
      accentColor = RoyalMaroon,
      badgeBg = Color(0xFFFEF3C7),
      icon = Icons.Default.NotificationsActive
    )
  }
}

private fun formatNotificationTime(timestampMs: Long): String {
  if (timestampMs <= 0L) return "এইমাত্র"
  return try {
    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(timestampMs))
  } catch (_: Exception) {
    ""
  }
}

@Composable
fun OrderStatusAlertBanner(
  notification: OrderStatusNotification?,
  onViewOrder: (OrderStatusNotification) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = notification != null,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    modifier = modifier
  ) {
    notification?.let { item ->
      val style = resolveNotificationVisualStyle(item.newStatus)
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = style.containerBg,
        border = BorderStroke(1.5.dp, style.borderColor),
        shadowElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .testTag("order_status_alert_banner")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(style.badgeBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = style.icon,
                contentDescription = "অর্ডার স্ট্যাটাস অ্যালার্ট",
                tint = style.accentColor,
                modifier = Modifier.size(22.dp)
              )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = style.badgeBg
                ) {
                  Text(
                    text = "${item.previousStatus} → ${item.newStatus}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = style.accentColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Text(
                  text = "#${item.orderId}",
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF475569),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = item.message,
                fontSize = 11.5.sp,
                color = Color(0xFF334155),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("order_alert_banner_dismiss_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "অ্যালার্ট বন্ধ করুন",
                tint = Color(0xFF475569),
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (item.trackingId.isNotBlank()) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, style.borderColor)
              ) {
                Text(
                  text = "🚚 ${item.deliveredBy.ifBlank { "Courier" }}: ${item.trackingId}",
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = style.accentColor,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            } else {
              Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
              onClick = { onViewOrder(item) },
              colors = ButtonDefaults.buttonColors(
                containerColor = style.accentColor,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("order_alert_banner_view_btn")
            ) {
              Text("অর্ডার দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun OrderNotificationsDialog(
  notifications: List<OrderStatusNotification>,
  hasPushPermission: Boolean,
  fcmTokenPresent: Boolean,
  onRequestPushPermission: () -> Unit,
  onNotificationClick: (OrderStatusNotification) -> Unit,
  onMarkAllRead: () -> Unit,
  onClearAll: () -> Unit,
  onDismiss: () -> Unit
) {
  val unreadCount = notifications.count { !it.read }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CreamBackground,
      border = BorderStroke(1.dp, BorderGold),
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 580.dp)
        .testTag("order_notifications_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "🔔 অর্ডার নোটিফিকেশন",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = RoyalMaroon
            )
            Text(
              text = if (unreadCount > 0) "$unreadCount টি নতুন স্ট্যাটাস আপডেট" else "সব অর্ডার আপডেট পড়া হয়েছে",
              fontSize = 11.sp,
              color = AntiqueGoldDark,
              fontWeight = FontWeight.SemiBold
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("close_notifications_dialog_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "নোটিফিকেশন বন্ধ করুন",
              tint = RoyalMaroon
            )
          }
        }

        // Push Notification / FCM Status Banner
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (hasPushPermission) Color(0xFFECFDF5) else CreamSurface,
          border = BorderStroke(1.dp, if (hasPushPermission) Color(0xFF6EE7B7) else BorderGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = if (hasPushPermission) Icons.Default.NotificationsActive else Icons.Default.Notifications,
              contentDescription = "পুশ নোটিফিকেশন স্ট্যাটাস",
              tint = if (hasPushPermission) Color(0xFF047857) else RoyalMaroon,
              modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = if (hasPushPermission) "অর্ডার পুশ অ্যালার্ট চালু আছে" else "তাৎক্ষণিক পুশ অ্যালার্ট চালু করুন",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
              )
              Text(
                text = if (hasPushPermission) {
                  if (fcmTokenPresent) "FCM টোকেন ও রিয়েল-টাইম Firestore সিঙ্ক সক্রিয়" else "ইন-অ্যাপ টোস্ট ও সিস্টেম নোটিফিকেশন সক্রিয়"
                } else {
                  "অর্ডার Shipped বা Delivered হলে সাথে সাথে ফোনে নোটিফিকেশন পান"
                },
                fontSize = 10.5.sp,
                color = Color(0xFF475569)
              )
            }
            if (!hasPushPermission) {
              Button(
                onClick = onRequestPushPermission,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .heightIn(min = 48.dp)
                  .testTag("enable_push_notifications_btn")
              ) {
                Text("চালু করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Quick Actions Row
        if (notifications.isNotEmpty()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            TextButton(
              onClick = onMarkAllRead,
              enabled = unreadCount > 0,
              modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("mark_all_notifications_read_btn")
            ) {
              Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("সব পড়া হয়েছে", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(
              onClick = onClearAll,
              modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("clear_all_notifications_btn")
            ) {
              Text("সব মুছুন", fontSize = 11.5.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.SemiBold)
            }
          }
        }

        HorizontalDivider(color = BorderGold.copy(alpha = 0.4f))

        // Notification Feed
        if (notifications.isEmpty()) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.NotificationsNone,
              contentDescription = "কোনো নোটিফিকেশন নেই",
              tint = AntiqueGoldDark,
              modifier = Modifier.size(40.dp)
            )
            Text(
              text = "এখনো কোনো অর্ডার স্ট্যাটাস অ্যালার্ট নেই",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
            Text(
              text = "অ্যাডমিন আপনার অর্ডারের স্ট্যাটাস পরিবর্তন করলে (যেমন Pending → Shipped) এখানে সাথে সাথে অ্যালার্ট দেখতে পাবেন।",
              fontSize = 11.sp,
              color = Color(0xFF475569)
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(notifications, key = { it.id }) { item ->
              val style = resolveNotificationVisualStyle(item.newStatus)
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (item.read) Color.White else style.containerBg,
                border = BorderStroke(
                  width = if (item.read) 1.dp else 1.5.dp,
                  color = if (item.read) Color(0xFFE2E8F0) else style.borderColor
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .clickable { onNotificationClick(item) }
                  .testTag("notification_item_${item.id}")
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.Top,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(style.badgeBg),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = style.icon,
                      contentDescription = item.newStatus,
                      tint = style.accentColor,
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = style.badgeBg
                      ) {
                        Text(
                          text = "${item.previousStatus} → ${item.newStatus}",
                          fontSize = 9.5.sp,
                          fontWeight = FontWeight.ExtraBold,
                          color = style.accentColor,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Text(
                          text = formatNotificationTime(item.createdAtMs),
                          fontSize = 9.5.sp,
                          color = Color(0xFF64748B)
                        )
                        if (!item.read) {
                          Box(
                            modifier = Modifier
                              .size(8.dp)
                              .clip(CircleShape)
                              .background(RoyalMaroon)
                          )
                        }
                      }
                    }

                    Text(
                      text = item.title,
                      fontSize = 12.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF0F172A)
                    )
                    Text(
                      text = item.message,
                      fontSize = 11.sp,
                      color = Color(0xFF334155)
                    )
                    if (item.trackingId.isNotBlank()) {
                      Text(
                        text = "Track: ${item.trackingId}${if (item.deliveredBy.isNotBlank()) " (${item.deliveredBy})" else ""}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = style.accentColor
                      )
                    }
                  }
                }
              }
            }
          }
        }

        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
        ) {
          Text("বন্ধ করুন", color = RoyalMaroon, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
