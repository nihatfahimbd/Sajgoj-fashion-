package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BangladeshLocations
import com.example.data.model.CustomerLoyaltyStatus
import com.example.data.model.Order
import com.example.data.model.OrderTrackingHelper
import com.example.data.model.OrderTrackingStage
import com.example.data.model.OrderTrackingSyncState
import com.example.data.model.SavedAddress
import com.example.data.model.StoreConfig
import com.example.firebase.AuthUserSummary
import com.example.ui.components.CustomerOrderStatisticsCard
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.RoyalMaroonDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CustomerProfileTab(val label: String, val icon: String, val testTag: String) {
  OVERVIEW("ড্যাশবোর্ড", "📊", "profile_tab_overview"),
  ORDERS("অর্ডার হিস্ট্রি", "📦", "profile_tab_orders"),
  ADDRESSES("সেভড ঠিকানা", "📍", "profile_tab_addresses"),
  LOYALTY("VIP পয়েন্ট", "👑", "profile_tab_loyalty")
}

@Composable
fun CustomerProfileScreen(
  authUser: AuthUserSummary?,
  isAuthBusy: Boolean,
  orders: List<Order>,
  savedAddresses: List<SavedAddress>,
  loyaltyStatus: CustomerLoyaltyStatus,
  orderTrackingSyncState: OrderTrackingSyncState = OrderTrackingSyncState(),
  storeConfig: StoreConfig,
  unreadNotificationCount: Int,
  unreadSupportCount: Int,
  onGoogleSignIn: () -> Unit,
  onGoogleSignOut: () -> Unit,
  onBackToStore: () -> Unit,
  onStartShopping: () -> Unit,
  onOpenOrderInvoice: (Order) -> Unit,
  onRefreshOrderTracking: () -> Unit = {},
  onSaveAddress: (SavedAddress) -> Unit,
  onSetDefaultAddress: (String) -> Unit,
  onDeleteAddress: (String) -> Unit,
  onOpenNotifications: () -> Unit,
  onOpenLiveSupport: () -> Unit
) {
  BackHandler { onBackToStore() }

  var selectedTab by remember { mutableStateOf(CustomerProfileTab.OVERVIEW) }
  var orderStatusFilter by remember { mutableStateOf("All") }
  var showAddressEditor by remember { mutableStateOf(false) }
  var editingAddress by remember { mutableStateOf<SavedAddress?>(null) }

  val defaultAddress = remember(savedAddresses) {
    savedAddresses.firstOrNull { it.isDefault } ?: savedAddresses.firstOrNull()
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CreamBackground)
  ) {
    // Top Navigation Strip
    Surface(
      color = Color.White,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          IconButton(
            onClick = onBackToStore,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("profile_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "স্টোরে ফিরে যান",
              tint = RoyalMaroon
            )
          }
          Column {
            Text(
              text = "কাস্টমার প্রোফাইল ড্যাশবোর্ড",
              fontSize = 15.5.sp,
              fontWeight = FontWeight.Black,
              color = RoyalMaroon,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${storeConfig.storeName} মেম্বার পোর্টাল",
              fontSize = 11.sp,
              color = AntiqueGoldDark,
              maxLines = 1
            )
          }
        }

        if (authUser != null) {
          OutlinedButton(
            onClick = onGoogleSignOut,
            enabled = !isAuthBusy,
            border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.45f)),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier
              .heightIn(min = 48.dp)
              .testTag("profile_signout_button")
          ) {
            Icon(
              imageVector = Icons.Default.PowerSettingsNew,
              contentDescription = "লগআউট",
              tint = AccentRed,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "লগআউট",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = AccentRed
            )
          }
        }
      }
    }

    if (authUser == null) {
      // Dedicated Sign-In Gate Card (Unlocks Order History, Saved Addresses & VIP Loyalty)
      Box(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        contentAlignment = Alignment.TopCenter
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.2.dp, BorderGold),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(72.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.linearGradient(listOf(RoyalMaroon, RoyalMaroonDark))
                  )
                  .border(2.5.dp, AntiqueGold, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "👑", fontSize = 32.sp)
              }

              Text(
                text = "সাজগোজ কাস্টমার ড্যাশবোর্ড",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = RoyalMaroon
              )

              Text(
                text = "লগইন করে আপনার অর্ডার হিস্ট্রি, সেভ করা ডেলিভারি ঠিকানা এবং ভিআইপি লয়্যালটি পয়েন্ট এক জায়গায় দেখুন।",
                fontSize = 13.sp,
                color = Color(0xFF475569)
              )

              // 3 Core Unlocked Features Preview
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ProfileBenefitTile(
                  icon = "📦",
                  title = "সম্পূর্ণ অর্ডার হিস্ট্রি ও ইনভয়েস",
                  subtitle = "রিয়েল-টাইম অর্ডার স্ট্যাটাস, কুরিয়ার ট্র্যাকিং ও ডিজিটাল মেমো"
                )
                ProfileBenefitTile(
                  icon = "📍",
                  title = "সেভড ডেলিভারি ঠিকানা",
                  subtitle = "বাসা ও অফিসের ঠিকানা সেভ রাখুন, চেকআউটে ১-ট্যাপে ব্যবহার করুন"
                )
                ProfileBenefitTile(
                  icon = "👑",
                  title = "VIP লয়্যালটি পয়েন্ট ও ডিসকাউন্ট",
                  subtitle = "প্রতিটি অর্ডারে পয়েন্ট সংগ্রহ, ব্রোঞ্জ থেকে প্লাটিনাম এলিট ছাড়"
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Button(
                onClick = onGoogleSignIn,
                enabled = !isAuthBusy,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 52.dp)
                  .testTag("profile_signin_button")
              ) {
                if (isAuthBusy) {
                  CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("সাইন-ইন হচ্ছে...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                } else {
                  Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = AntiqueGold,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Google দিয়ে সাইন-ইন করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp
                  )
                }
              }

              OutlinedButton(
                onClick = onBackToStore,
                border = BorderStroke(1.dp, BorderGold),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 48.dp)
              ) {
                Text("স্টোরে ফিরে কেনাকাটা করুন", color = RoyalMaroon, fontWeight = FontWeight.Bold)
              }
            }
          }

          HelplineCard(storeConfig = storeConfig)
        }
      }
    } else {
      // Logged-In Dedicated Customer Profile Dashboard
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // 1. Hero Member Banner + KPI Row
        item {
          Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.Transparent,
            border = BorderStroke(1.2.dp, AntiqueGold),
            shadowElevation = 4.dp,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(22.dp))
              .background(
                Brush.linearGradient(listOf(RoyalMaroon, RoyalMaroonDark, NavyBlue))
              )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
                    .border(2.dp, AntiqueGold, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  val initial = authUser.displayName.trim().firstOrNull()?.uppercase() ?: "S"
                  Text(
                    text = initial,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = AntiqueGold
                  )
                }

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = authUser.displayName.ifBlank { "সম্মানিত গ্রাহক" },
                    fontSize = 17.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  if (authUser.email.isNotBlank()) {
                    Text(
                      text = authUser.email,
                      fontSize = 11.5.sp,
                      color = Color.White.copy(alpha = 0.85f),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = AntiqueGold
                  ) {
                    Text(
                      text = "${loyaltyStatus.currentTier.icon} ${loyaltyStatus.currentTier.nameBn} (${loyaltyStatus.currentTier.discountPercent}% VIP ছাড়)",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Black,
                      color = DarkEspresso,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                  }
                }
              }

              // 4 KPI Summary Tiles
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                DashboardKpiTile(
                  label = "উপলব্ধ পয়েন্ট",
                  value = "${loyaltyStatus.availablePoints}",
                  modifier = Modifier.weight(1f)
                )
                DashboardKpiTile(
                  label = "পয়েন্ট ভ্যালু",
                  value = "৳${loyaltyStatus.redeemableTaka}",
                  modifier = Modifier.weight(1f)
                )
                DashboardKpiTile(
                  label = "মোট অর্ডার",
                  value = "${orders.size}",
                  modifier = Modifier.weight(1f)
                )
                DashboardKpiTile(
                  label = "ঠিকানা",
                  value = "${savedAddresses.size}",
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        // 2. Segmented Tab Bar
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CustomerProfileTab.entries.forEach { tab ->
              val isSelected = selectedTab == tab
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) RoyalMaroon else Color.White,
                border = BorderStroke(1.dp, if (isSelected) RoyalMaroon else BorderGold),
                modifier = Modifier
                  .heightIn(min = 48.dp)
                  .clickable { selectedTab = tab }
                  .testTag(tab.testTag)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(text = tab.icon, fontSize = 14.sp)
                  Text(
                    text = tab.label,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else RoyalMaroon
                  )
                }
              }
            }
          }
        }

        // 3. Tab Body
        when (selectedTab) {
          CustomerProfileTab.OVERVIEW -> {
            // Live Real-Time Order Tracking Spotlight (Firestore Connected)
            val spotlightOrder = orders.firstOrNull {
              !it.status.equals("Cancelled", ignoreCase = true) &&
                !it.status.equals("Cancellation", ignoreCase = true)
            } ?: orders.firstOrNull()

            if (spotlightOrder != null) {
              item {
                LiveOrderTrackingSpotlightCard(
                  order = spotlightOrder,
                  syncState = orderTrackingSyncState,
                  onRefreshSync = onRefreshOrderTracking,
                  onOpenInvoice = { onOpenOrderInvoice(spotlightOrder) },
                  onViewAllOrders = { selectedTab = CustomerProfileTab.ORDERS }
                )
              }
            } else {
              item {
                FirestoreSyncStatusBanner(
                  syncState = orderTrackingSyncState,
                  onRefreshSync = onRefreshOrderTracking
                )
              }
            }

            // VIP Status Summary Card
            item {
              VipStatusCard(
                loyaltyStatus = loyaltyStatus,
                compact = true,
                onViewFullRoadmap = { selectedTab = CustomerProfileTab.LOYALTY }
              )
            }

            // Default Saved Address Preview Card
            item {
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderGold),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = RoyalMaroon,
                        modifier = Modifier.size(18.dp)
                      )
                      Text(
                        text = "ডিফল্ট ডেলিভারি ঠিকানা",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = RoyalMaroon
                      )
                    }
                    TextButton(
                      onClick = { selectedTab = CustomerProfileTab.ADDRESSES },
                      modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                      Text(
                        text = "ম্যানেজ করুন →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                      )
                    }
                  }

                  if (defaultAddress != null) {
                    SavedAddressSummaryContent(address = defaultAddress)
                  } else {
                    Text(
                      text = "এখনও কোনো ডিফল্ট ঠিকানা সেভ করা নেই। দ্রুত চেকআউটের জন্য আপনার ঠিকানা যোগ করুন।",
                      fontSize = 12.5.sp,
                      color = Color(0xFF64748B)
                    )
                    Button(
                      onClick = {
                        editingAddress = null
                        showAddressEditor = true
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                      Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("ঠিকানা যোগ করুন", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            // Order Statistics Card
            item {
              CustomerOrderStatisticsCard(orders = orders)
            }

            // Recent Order Preview Header
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "📦 সাম্প্রতিক অর্ডার হিস্ট্রি (${orders.size})",
                  fontSize = 14.5.sp,
                  fontWeight = FontWeight.Black,
                  color = RoyalMaroon
                )
                if (orders.isNotEmpty()) {
                  TextButton(
                    onClick = { selectedTab = CustomerProfileTab.ORDERS },
                    modifier = Modifier.heightIn(min = 48.dp)
                  ) {
                    Text("সব অর্ডার দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                  }
                }
              }
            }

            if (orders.isEmpty()) {
              item {
                EmptyOrdersCard(onStartShopping = onStartShopping)
              }
            } else {
              items(orders.take(3), key = { it.id }) { order ->
                OrderHistoryItemCard(
                  order = order,
                  onOpenInvoice = { onOpenOrderInvoice(order) }
                )
              }
            }

            // Utility Actions: Notifications & Live Support
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedButton(
                  onClick = onOpenNotifications,
                  border = BorderStroke(1.dp, BorderGold),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp)
                    .testTag("profile_notifications_button")
                ) {
                  Icon(Icons.Default.Notifications, contentDescription = null, tint = RoyalMaroon, modifier = Modifier.size(17.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (unreadNotificationCount > 0) "নোটিফিকেশন ($unreadNotificationCount)" else "নোটিফিকেশন",
                    color = RoyalMaroon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }

                Button(
                  onClick = onOpenLiveSupport,
                  colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp)
                    .testTag("profile_support_button")
                ) {
                  Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(17.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (unreadSupportCount > 0) "লাইভ চ্যাট ($unreadSupportCount)" else "লাইভ সাপোর্ট",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }

            item { HelplineCard(storeConfig = storeConfig) }
          }

          CustomerProfileTab.ORDERS -> {
            item {
              FirestoreSyncStatusBanner(
                syncState = orderTrackingSyncState,
                onRefreshSync = onRefreshOrderTracking
              )
            }

            item {
              CustomerOrderStatisticsCard(orders = orders)
            }

            item {
              val filters = listOf(
                "All" to "সব",
                "Pending" to "Pending",
                "Processing" to "Processing",
                "Shipped" to "Shipped",
                "Delivered" to "Delivered",
                "Cancelled" to "Cancelled"
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                filters.forEach { (key, label) ->
                  val active = orderStatusFilter == key
                  Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (active) RoyalMaroon else Color.White,
                    border = BorderStroke(1.dp, if (active) RoyalMaroon else BorderGold),
                    modifier = Modifier
                      .heightIn(min = 48.dp)
                      .clickable { orderStatusFilter = key }
                      .testTag("order_filter_${key.lowercase()}")
                  ) {
                    Box(
                      contentAlignment = Alignment.Center,
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                      Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.White else DarkEspresso
                      )
                    }
                  }
                }
              }
            }

            val filteredOrders = orders.filter { order ->
              when (orderStatusFilter) {
                "All" -> true
                "Processing" -> {
                  val st = order.status.trim().lowercase()
                  st == "processing" || st == "confirmed" || st == "to ship"
                }
                "Shipped" -> {
                  val st = order.status.trim().lowercase()
                  st == "shipped" || st == "shipping"
                }
                else -> order.status.equals(orderStatusFilter, ignoreCase = true)
              }
            }

            if (filteredOrders.isEmpty()) {
              item { EmptyOrdersCard(onStartShopping = onStartShopping) }
            } else {
              items(filteredOrders, key = { it.id }) { order ->
                OrderHistoryItemCard(
                  order = order,
                  onOpenInvoice = { onOpenOrderInvoice(order) }
                )
              }
            }
          }

          CustomerProfileTab.ADDRESSES -> {
            item {
              Button(
                onClick = {
                  editingAddress = null
                  showAddressEditor = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 50.dp)
                  .testTag("add_saved_address_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "নতুন ডেলিভারি ঠিকানা যোগ করুন",
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            if (savedAddresses.isEmpty()) {
              item {
                Card(
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  border = BorderStroke(1.dp, BorderGold),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Text(text = "📍", fontSize = 36.sp)
                    Text(
                      text = "এখনও কোনো ঠিকানা সংরক্ষিত নেই",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMaroon
                    )
                    Text(
                      text = "আপনার বাসা বা অফিসের ঠিকানা সেভ রাখলে চেকআউটের সময় স্বয়ংক্রিয়ভাবে পূরণ হয়ে যাবে।",
                      fontSize = 12.sp,
                      color = Color(0xFF64748B)
                    )
                  }
                }
              }
            } else {
              items(savedAddresses, key = { it.id }) { addr ->
                SavedAddressFullCard(
                  address = addr,
                  onSetDefault = { onSetDefaultAddress(addr.id) },
                  onEdit = {
                    editingAddress = addr
                    showAddressEditor = true
                  },
                  onDelete = { onDeleteAddress(addr.id) }
                )
              }
            }
          }

          CustomerProfileTab.LOYALTY -> {
            item {
              VipStatusCard(
                loyaltyStatus = loyaltyStatus,
                compact = false,
                onViewFullRoadmap = {}
              )
            }

            // Earn & Redeem Rules Card
            item {
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderGold),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier.padding(16.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text(
                    text = "🪙 পয়েন্ট অর্জন ও ব্যবহারের নিয়মাবলী",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = RoyalMaroon
                  )
                  Text(
                    text = "• প্রতি ${loyaltyStatus.spendPerPoint} টাকা কেনাকাটায় ১টি রিওয়ার্ড পয়েন্ট যুক্ত হয়।",
                    fontSize = 12.sp,
                    color = DarkEspresso
                  )
                  Text(
                    text = "• প্রতি ${loyaltyStatus.pointsPerTaka} পয়েন্ট = ১ টাকা সরাসরি ক্যাশ ছাড় (বর্তমান ব্যালেন্স মূল্য: ৳${loyaltyStatus.redeemableTaka})।",
                    fontSize = 12.sp,
                    color = DarkEspresso
                  )
                  Text(
                    text = "• মোট কেনাকাটা বাড়ার সাথে সাথে আপনার VIP টায়ার স্বয়ংক্রিয়ভাবে আপগ্রেড হবে।",
                    fontSize = 12.sp,
                    color = DarkEspresso
                  )
                }
              }
            }

            // Complete VIP Tier Roadmap
            item {
              Text(
                text = "🏆 সকল VIP মেম্বারশিপ লেভেল (Bronze → Platinum Elite)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = RoyalMaroon
              )
            }

            items(loyaltyStatus.allTiers, key = { it.id }) { tier ->
              val isCurrent = tier.id == loyaltyStatus.currentTier.id
              val isUnlocked = loyaltyStatus.lifetimeSpend >= tier.minSpend
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isCurrent) CreamSurface else Color.White,
                border = BorderStroke(
                  width = if (isCurrent) 2.dp else 1.dp,
                  color = if (isCurrent) RoyalMaroon else if (isUnlocked) BorderGold else Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(44.dp)
                      .clip(CircleShape)
                      .background(Color(tier.accentHex).copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = tier.icon, fontSize = 22.sp)
                  }
                  Column(modifier = Modifier.weight(1f)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Text(
                        text = tier.nameBn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = RoyalMaroon
                      )
                      Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (isCurrent) RoyalMaroon else if (isUnlocked) AccentGreen.copy(alpha = 0.14f) else Color(0xFFF1F5F9)
                      ) {
                        Text(
                          text = when {
                            isCurrent -> "বর্তমান"
                            isUnlocked -> "অর্জিত ✓"
                            else -> "৳${tier.minSpend}+"
                          },
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isCurrent) Color.White else if (isUnlocked) AccentGreen else Color(0xFF64748B),
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                      }
                    }
                    Text(
                      text = tier.perks,
                      fontSize = 11.5.sp,
                      color = Color(0xFF475569)
                    )
                    Text(
                      text = if (tier.discountPercent > 0) "ফ্ল্যাট ${tier.discountPercent}% VIP ডিসকাউন্ট" else "রিওয়ার্ড পয়েন্ট সংগ্রহ",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(tier.accentHex)
                    )
                  }
                  Icon(
                    imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) AccentGreen else Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
      }
    }
  }

  if (showAddressEditor) {
    SavedAddressEditorDialog(
      initial = editingAddress,
      defaultRecipientName = authUser?.displayName.orEmpty(),
      defaultPhone = defaultAddress?.phone.orEmpty(),
      onDismiss = { showAddressEditor = false },
      onSave = { addr ->
        onSaveAddress(addr)
        showAddressEditor = false
      }
    )
  }
}

@Composable
private fun ProfileBenefitTile(icon: String, title: String, subtitle: String) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = CreamSurface,
    border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.7f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(text = icon, fontSize = 22.sp)
      Column {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
        Text(text = subtitle, fontSize = 11.5.sp, color = Color(0xFF475569))
      }
    }
  }
}

@Composable
private fun DashboardKpiTile(label: String, value: String, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White.copy(alpha = 0.14f),
    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.45f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(
        text = value,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black,
        color = AntiqueGold,
        maxLines = 1
      )
      Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White.copy(alpha = 0.9f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun VipStatusCard(
  loyaltyStatus: CustomerLoyaltyStatus,
  compact: Boolean,
  onViewFullRoadmap: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurface),
    border = BorderStroke(1.4.dp, AntiqueGold),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Color.White)
              .border(1.dp, BorderGold, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = loyaltyStatus.currentTier.icon, fontSize = 24.sp)
          }
          Column {
            Text(
              text = "বর্তমান VIP স্ট্যাটাস",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AntiqueGoldDark
            )
            Text(
              text = loyaltyStatus.currentTier.nameBn,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = RoyalMaroon
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${loyaltyStatus.availablePoints} পয়েন্ট",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          Text(
            text = "= ৳${loyaltyStatus.redeemableTaka} ক্যাশ ছাড়",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGreen
          )
        }
      }

      HorizontalDivider(color = BorderGold.copy(alpha = 0.6f))

      if (loyaltyStatus.nextTier != null) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "পরবর্তী লেভেল: ${loyaltyStatus.nextTier.icon} ${loyaltyStatus.nextTier.nameBn}",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = DarkEspresso
          )
          Text(
            text = "আর ৳${loyaltyStatus.amountToNextTier} বাকি",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalMaroon
          )
        }
        LinearProgressIndicator(
          progress = { loyaltyStatus.progressFraction },
          color = RoyalMaroon,
          trackColor = Color.White,
          strokeCap = StrokeCap.Round,
          modifier = Modifier
            .fillMaxWidth()
            .height(9.dp)
            .clip(RoundedCornerShape(999.dp))
        )
        Text(
          text = "মোট কেনাকাটা: ৳${loyaltyStatus.lifetimeSpend} (${loyaltyStatus.validOrderCount} টি অর্ডার)",
          fontSize = 11.sp,
          color = Color(0xFF64748B)
        )
      } else {
        Text(
          text = "⚜️ অভিনন্দন! আপনি সর্বোচ্চ VIP মেম্বারশিপ \"প্লাটিনাম এলিট\" ক্লাবে রয়েছেন!",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = AccentGreen
        )
      }

      if (compact) {
        OutlinedButton(
          onClick = onViewFullRoadmap,
          border = BorderStroke(1.dp, BorderGold),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = AntiqueGoldDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("সকল VIP টায়ার ও পয়েন্ট সুবিধা দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
        }
      }
    }
  }
}

@Composable
private fun SavedAddressSummaryContent(address: SavedAddress) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(999.dp),
        color = CreamSurface,
        border = BorderStroke(1.dp, BorderGold)
      ) {
        Text(
          text = if (address.category == "Office") "🏢 Office" else "🏠 Home",
          fontSize = 10.5.sp,
          fontWeight = FontWeight.Bold,
          color = RoyalMaroon,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
      }
      Surface(
        shape = RoundedCornerShape(999.dp),
        color = AccentGreen.copy(alpha = 0.14f)
      ) {
        Text(
          text = "ডিফল্ট ✓",
          fontSize = 10.5.sp,
          fontWeight = FontWeight.Bold,
          color = AccentGreen,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
      }
    }
    Text(
      text = "${address.recipientName} • ${address.phone}",
      fontSize = 13.5.sp,
      fontWeight = FontWeight.Bold,
      color = DarkEspresso
    )
    Text(
      text = address.streetAddress,
      fontSize = 12.5.sp,
      color = Color(0xFF334155)
    )
    Text(
      text = "${address.thana}, ${address.district}" + if (address.landmark.isNotBlank()) " • ল্যান্ডমার্ক: ${address.landmark}" else "",
      fontSize = 11.5.sp,
      color = Color(0xFF64748B)
    )
  }
}

@Composable
private fun SavedAddressFullCard(
  address: SavedAddress,
  onSetDefault: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(if (address.isDefault) 1.6.dp else 1.dp, if (address.isDefault) RoyalMaroon else BorderGold),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = if (address.category == "Office") Icons.Default.Business else Icons.Default.Home,
            contentDescription = null,
            tint = RoyalMaroon,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = address.label.ifBlank { address.category },
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          if (address.isDefault) {
            Surface(
              shape = RoundedCornerShape(999.dp),
              color = AccentGreen.copy(alpha = 0.14f)
            ) {
              Text(
                text = "ডিফল্ট ঠিকানা",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGreen,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
          IconButton(
            onClick = onEdit,
            modifier = Modifier.minimumInteractiveComponentSize()
          ) {
            Icon(Icons.Default.Edit, contentDescription = "এডিট", tint = NavyBlue, modifier = Modifier.size(18.dp))
          }
          IconButton(
            onClick = onDelete,
            modifier = Modifier.minimumInteractiveComponentSize()
          ) {
            Icon(Icons.Default.Delete, contentDescription = "মুছুন", tint = AccentRed, modifier = Modifier.size(18.dp))
          }
        }
      }

      Text(
        text = "${address.recipientName} • ${address.phone}",
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold,
        color = DarkEspresso
      )
      Text(
        text = address.streetAddress,
        fontSize = 12.5.sp,
        color = Color(0xFF334155)
      )
      Text(
        text = "${address.thana}, ${address.district}" + if (address.landmark.isNotBlank()) " • ল্যান্ডমার্ক: ${address.landmark}" else "",
        fontSize = 11.5.sp,
        color = Color(0xFF64748B)
      )

      if (!address.isDefault) {
        OutlinedButton(
          onClick = onSetDefault,
          border = BorderStroke(1.dp, BorderGold),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
        ) {
          Text("ডিফল্ট ঠিকানা হিসেবে সেট করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
        }
      }
    }
  }
}

@Composable
private fun FirestoreSyncStatusBanner(
  syncState: OrderTrackingSyncState,
  onRefreshSync: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "firestore_live_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 900),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  val (badgeBg, borderCol, dotColor, statusText) = when {
    syncState.errorMessage.isNotBlank() -> QuadStyle(
      Color(0xFFFEF2F2), Color(0xFFFCA5A5), AccentRed,
      "Firestore সিঙ্ক সতর্কতা: ${syncState.errorMessage}"
    )
    syncState.isLiveServerSynced -> QuadStyle(
      Color(0xFFECFDF5), Color(0xFF6EE7B7), Color(0xFF059669),
      "Firestore রিয়েল-টাইম লাইভ সংযুক্ত"
    )
    syncState.isListening -> QuadStyle(
      Color(0xFFFFFBEB), Color(0xFFFCD34D), Color(0xFFD97706),
      "লোকাল ক্যাশ থেকে প্রদর্শিত (সিঙ্ক হচ্ছে...)"
    )
    else -> QuadStyle(
      Color(0xFFF1F5F9), Color(0xFFCBD5E1), Color(0xFF64748B),
      "অফলাইন মোড"
    )
  }

  val timeLabel = remember(syncState.lastSyncedAtMs) {
    if (syncState.lastSyncedAtMs <= 0L) "প্রস্তুত"
    else try {
      SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(syncState.lastSyncedAtMs))
    } catch (_: Exception) {
      "আপডেটেড"
    }
  }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = badgeBg,
    border = BorderStroke(1.dp, borderCol),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("firestore_live_sync_banner")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .alpha(if (syncState.isListening) pulseAlpha else 1f)
            .clip(CircleShape)
            .background(dotColor)
        )
        Column {
          Text(
            text = statusText,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = DarkEspresso,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "সর্বশেষ আপডেট: $timeLabel",
            fontSize = 10.sp,
            color = Color(0xFF475569)
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White,
        border = BorderStroke(1.dp, borderCol),
        modifier = Modifier
          .heightIn(min = 48.dp)
          .clickable { onRefreshSync() }
          .testTag("firestore_refresh_tracking_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Firestore রিফ্রেশ",
            tint = dotColor,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "রিফ্রেশ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = dotColor
          )
        }
      }
    }
  }
}

private data class QuadStyle(
  val bg: Color,
  val border: Color,
  val accent: Color,
  val label: String
)

@Composable
private fun LiveOrderTrackingSpotlightCard(
  order: Order,
  syncState: OrderTrackingSyncState,
  onRefreshSync: () -> Unit,
  onOpenInvoice: () -> Unit,
  onViewAllOrders: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, AntiqueGold),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("live_order_tracking_spotlight")
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(RoyalMaroon.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LocalShipping,
              contentDescription = null,
              tint = RoyalMaroon,
              modifier = Modifier.size(20.dp)
            )
          }
          Column {
            Text(
              text = "লাইভ অর্ডার ট্র্যাকিং",
              fontSize = 14.5.sp,
              fontWeight = FontWeight.Black,
              color = RoyalMaroon
            )
            Text(
              text = "অর্ডার #${order.id} • ৳${order.total}",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF475569)
            )
          }
        }

        TextButton(
          onClick = onViewAllOrders,
          modifier = Modifier.heightIn(min = 48.dp)
        ) {
          Text("সব অর্ডার →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
        }
      }

      FirestoreSyncStatusBanner(
        syncState = syncState,
        onRefreshSync = onRefreshSync
      )

      RealTimeOrderTrackingStepper(order = order)

      OutlinedButton(
        onClick = onOpenInvoice,
        border = BorderStroke(1.dp, BorderGold),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 48.dp)
      ) {
        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = RoyalMaroon, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("অর্ডার ইনভয়েস ও বিস্তারিত দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
      }
    }
  }
}

@Composable
private fun RealTimeOrderTrackingStepper(
  order: Order,
  modifier: Modifier = Modifier
) {
  val progress = remember(order.status) { OrderTrackingHelper.resolveProgress(order.status) }
  val animatedFraction by animateFloatAsState(
    targetValue = if (progress.isTerminalException) 0f else (progress.currentStepIndex / 3f).coerceIn(0f, 1f),
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 240f),
    label = "order_step_progress"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "active_step_glow")
  val activeGlow by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 850),
      repeatMode = RepeatMode.Reverse
    ),
    label = "step_glow"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(CreamBackground)
      .border(1.dp, BorderGold.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
      .padding(12.dp)
      .testTag("order_tracking_stepper_${order.id}"),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    if (progress.isTerminalException) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFEF2F2),
        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = null,
            tint = AccentRed,
            modifier = Modifier.size(18.dp)
          )
          Column {
            Text(
              text = progress.statusLabelBn,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = AccentRed
            )
            Text(
              text = progress.statusHintBn,
              fontSize = 11.sp,
              color = Color(0xFF7F1D1D)
            )
          }
        }
      }
    } else {
      // Animated Horizontal Progress Track
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFE2E8F0))
        )
        Box(
          modifier = Modifier
            .fillMaxWidth(animatedFraction)
            .height(5.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(
              Brush.horizontalGradient(
                listOf(RoyalMaroon, AntiqueGoldDark, AccentGreen)
              )
            )
        )
      }

      // 4 Stage Step Nodes: Ordered -> Processing -> Shipped -> Delivered
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        progress.steps.forEachIndexed { idx, step ->
          val isCompleted = idx < progress.currentStepIndex ||
            (idx == progress.currentStepIndex && progress.stage == OrderTrackingStage.DELIVERED)
          val isActive = idx == progress.currentStepIndex && progress.stage != OrderTrackingStage.DELIVERED
          val nodeBg = when {
            isCompleted -> AccentGreen
            isActive -> RoyalMaroon
            else -> Color.White
          }
          val nodeBorder = when {
            isCompleted -> AccentGreen
            isActive -> AntiqueGold
            else -> Color(0xFFCBD5E1)
          }

          Column(
            modifier = Modifier
              .weight(1f)
              .testTag("tracking_step_${step.key.lowercase()}"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .alpha(if (isActive) activeGlow else 1f)
                .clip(CircleShape)
                .background(nodeBg)
                .border(if (isActive) 2.dp else 1.2.dp, nodeBorder, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              if (isCompleted) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = step.titleEn,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              } else {
                Text(
                  text = step.icon,
                  fontSize = 13.sp
                )
              }
            }

            Text(
              text = step.titleEn,
              fontSize = 10.5.sp,
              fontWeight = if (isCompleted || isActive) FontWeight.Black else FontWeight.Medium,
              color = when {
                isCompleted -> AccentGreen
                isActive -> RoyalMaroon
                else -> Color(0xFF64748B)
              },
              textAlign = TextAlign.Center,
              maxLines = 1
            )
            Text(
              text = step.titleBn,
              fontSize = 9.sp,
              color = if (isActive || isCompleted) DarkEspresso else Color(0xFF94A3B8),
              textAlign = TextAlign.Center,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      // Live Status Callout Message
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Text(
            text = "বর্তমান ধাপ: ${progress.statusLabelBn}",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          Text(
            text = progress.statusHintBn,
            fontSize = 11.sp,
            color = Color(0xFF475569)
          )
        }
      }
    }
  }
}

@Composable
private fun OrderHistoryItemCard(
  order: Order,
  onOpenInvoice: () -> Unit
) {
  val statusColors = when (order.status.trim().lowercase()) {
    "delivered" -> Color(0xFF047857) to Color(0xFFECFDF5)
    "shipped", "shipping" -> Color(0xFF0369A1) to Color(0xFFF0F9FF)
    "processing", "confirmed", "to ship" -> Color(0xFFB45309) to Color(0xFFFFFBEB)
    "cancelled", "cancellation", "failed delivered" -> AccentRed to Color(0xFFFEF2F2)
    else -> RoyalMaroon to CreamSurface
  }
  val totalQty = order.items.sumOf { it.quantity.coerceAtLeast(1) }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.75f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("order_history_card_${order.id}")
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "#${order.id}",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          Text(
            text = "${order.date.ifBlank { "আজ" }} • ${order.paymentMethod}",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }

        Surface(
          shape = RoundedCornerShape(999.dp),
          color = statusColors.second,
          border = BorderStroke(1.dp, statusColors.first.copy(alpha = 0.35f))
        ) {
          Text(
            text = order.status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = statusColors.first,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      // Real-Time Step Progress Indicator (Ordered -> Processing -> Shipped -> Delivered)
      RealTimeOrderTrackingStepper(order = order)

      Text(
        text = order.items.take(2).joinToString(", ") { "${it.name} (×${it.quantity})" } +
          if (order.items.size > 2) " + আরো ${order.items.size - 2} টি" else "",
        fontSize = 12.5.sp,
        fontWeight = FontWeight.Medium,
        color = DarkEspresso,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      if (order.trackingId.isNotBlank()) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFF0F9FF),
          border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "🚚 ট্র্যাকিং কোড: ${order.trackingId}" + if (order.deliveredBy.isNotBlank()) " (${order.deliveredBy})" else "",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0369A1),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }

      HorizontalDivider(color = BorderGold.copy(alpha = 0.45f))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "মোট: ৳${order.total}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          Text(
            text = "$totalQty টি পণ্য • ${order.thana}, ${order.district}",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }

        OutlinedButton(
          onClick = onOpenInvoice,
          border = BorderStroke(1.dp, BorderGold),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.heightIn(min = 48.dp)
        ) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = RoyalMaroon, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("ইনভয়েস দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
        }
      }
    }
  }
}

@Composable
private fun EmptyOrdersCard(onStartShopping: () -> Unit) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BorderGold),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(text = "🛍️", fontSize = 36.sp)
      Text(
        text = "এখনও কোনো অর্ডার পাওয়া যায়নি",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = RoyalMaroon
      )
      Text(
        text = "শপিং সেকশন থেকে আপনার পছন্দের শাড়ি, পাঞ্জাবি বা জুয়েলারি অর্ডার করুন।",
        fontSize = 12.sp,
        color = Color(0xFF64748B)
      )
      Button(
        onClick = onStartShopping,
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.heightIn(min = 48.dp)
      ) {
        Text("কেনাকাটা শুরু করুন →", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun HelplineCard(storeConfig: StoreConfig) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = CreamSurface,
    border = BorderStroke(1.dp, BorderGold),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "📞 কাস্টমার হেল্পলাইন",
        fontSize = 12.5.sp,
        fontWeight = FontWeight.Black,
        color = RoyalMaroon
      )
      Text(
        text = "ফোন: ${storeConfig.phone} • WhatsApp: ${storeConfig.whatsapp}",
        fontSize = 11.5.sp,
        color = DarkEspresso
      )
      Text(
        text = "ইমেইল: ${storeConfig.email}",
        fontSize = 11.5.sp,
        color = Color(0xFF475569)
      )
    }
  }
}

@Composable
private fun SavedAddressEditorDialog(
  initial: SavedAddress?,
  defaultRecipientName: String,
  defaultPhone: String,
  onDismiss: () -> Unit,
  onSave: (SavedAddress) -> Unit
) {
  var label by remember { mutableStateOf(initial?.label ?: "বাসা") }
  var category by remember { mutableStateOf(initial?.category ?: "Home") }
  var recipientName by remember { mutableStateOf(initial?.recipientName ?: defaultRecipientName) }
  var phone by remember { mutableStateOf(initial?.phone ?: defaultPhone) }
  var selectedDistrict by remember { mutableStateOf(initial?.district ?: "ঢাকা (Dhaka)") }
  var thana by remember { mutableStateOf(initial?.thana ?: "ধানমন্ডি") }
  var streetAddress by remember { mutableStateOf(initial?.streetAddress ?: "") }
  var landmark by remember { mutableStateOf(initial?.landmark ?: "") }
  var isDefault by remember { mutableStateOf(initial?.isDefault ?: true) }
  var errorText by remember { mutableStateOf("") }

  var showDistrictPicker by remember { mutableStateOf(false) }
  var districtQuery by remember { mutableStateOf("") }

  val thanasForDistrict = remember(selectedDistrict) {
    BangladeshLocations.getThanasForDistrict(selectedDistrict)
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = Color.White,
      border = BorderStroke(1.dp, BorderGold),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = if (initial != null) "📍 ঠিকানা আপডেট করুন" else "📍 নতুন ঠিকানা যোগ করুন",
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = RoyalMaroon
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("Home" to "🏠 বাসা", "Office" to "🏢 অফিস").forEach { (cat, chipText) ->
            val selected = category == cat
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (selected) RoyalMaroon else CreamBackground,
              border = BorderStroke(1.dp, if (selected) RoyalMaroon else BorderGold),
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .clickable {
                  category = cat
                  if (label.isBlank() || label == "বাসা" || label == "অফিস") {
                    label = if (cat == "Office") "অফিস" else "বাসা"
                  }
                }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = chipText,
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (selected) Color.White else DarkEspresso
                )
              }
            }
          }
        }

        val fieldColors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = RoyalMaroon,
          unfocusedBorderColor = BorderGold
        )

        OutlinedTextField(
          value = label,
          onValueChange = { label = it },
          label = { Text("ঠিকানার নাম (যেমন: বাসা / অফিস)", fontSize = 12.sp) },
          singleLine = true,
          colors = fieldColors,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = recipientName,
          onValueChange = { recipientName = it },
          label = { Text("প্রাপকের নাম *", fontSize = 12.sp) },
          singleLine = true,
          colors = fieldColors,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("মোবাইল নম্বর *", fontSize = 12.sp) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          colors = fieldColors,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = CreamBackground,
          border = BorderStroke(1.dp, BorderGold),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clickable { showDistrictPicker = !showDistrictPicker }
        ) {
          Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(text = "জেলা / থানা *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
            Text(
              text = "$selectedDistrict › $thana",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = DarkEspresso
            )
          }
        }

        if (showDistrictPicker) {
          OutlinedTextField(
            value = districtQuery,
            onValueChange = { districtQuery = it },
            placeholder = { Text("জেলা খুঁজুন...", fontSize = 12.sp) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
          val matchedDistricts = remember(districtQuery) {
            BangladeshLocations.searchDistricts(districtQuery).take(12)
          }
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            matchedDistricts.forEach { d ->
              val sel = selectedDistrict == d.displayLabel
              Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (sel) RoyalMaroon else CreamSurface,
                border = BorderStroke(1.dp, if (sel) RoyalMaroon else BorderGold),
                modifier = Modifier
                  .heightIn(min = 42.dp)
                  .clickable {
                    selectedDistrict = d.displayLabel
                    thana = d.thanas.firstOrNull() ?: "সদর"
                    showDistrictPicker = false
                  }
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                  Text(d.nameBn, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else DarkEspresso)
                }
              }
            }
          }
        }

        // Thana Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          thanasForDistrict.forEach { th ->
            val sel = thana == th
            Surface(
              shape = RoundedCornerShape(999.dp),
              color = if (sel) NavyBlue else Color.White,
              border = BorderStroke(1.dp, if (sel) NavyBlue else BorderGold),
              modifier = Modifier
                .heightIn(min = 42.dp)
                .clickable { thana = th }
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(th, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else DarkEspresso)
              }
            }
          }
        }

        OutlinedTextField(
          value = streetAddress,
          onValueChange = { streetAddress = it },
          label = { Text("বিস্তারিত ঠিকানা (বাসা/রোড/এলাকা) *", fontSize = 12.sp) },
          minLines = 2,
          colors = fieldColors,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = landmark,
          onValueChange = { landmark = it },
          label = { Text("ল্যান্ডমার্ক (ঐচ্ছিক)", fontSize = 12.sp) },
          singleLine = true,
          colors = fieldColors,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ডিফল্ট ঠিকানা হিসেবে সেট করুন",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = DarkEspresso
          )
          Switch(
            checked = isDefault,
            onCheckedChange = { isDefault = it },
            colors = SwitchDefaults.colors(checkedTrackColor = RoyalMaroon)
          )
        }

        if (errorText.isNotBlank()) {
          Text(text = errorText, fontSize = 12.sp, color = AccentRed, fontWeight = FontWeight.Bold)
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Text("বাতিল", fontWeight = FontWeight.Bold)
          }
          Button(
            onClick = {
              when {
                recipientName.isBlank() -> errorText = "অনুগ্রহ করে প্রাপকের নাম লিখুন"
                phone.isBlank() -> errorText = "অনুগ্রহ করে মোবাইল নম্বর লিখুন"
                streetAddress.isBlank() -> errorText = "অনুগ্রহ করে বিস্তারিত ঠিকানা লিখুন"
                else -> {
                  onSave(
                    SavedAddress(
                      id = initial?.id ?: "addr-${System.currentTimeMillis()}",
                      label = label.trim().ifBlank { if (category == "Office") "অফিস" else "বাসা" },
                      category = category,
                      recipientName = recipientName.trim(),
                      phone = phone.trim(),
                      district = selectedDistrict,
                      thana = thana,
                      streetAddress = streetAddress.trim(),
                      landmark = landmark.trim(),
                      isDefault = isDefault,
                      updatedAt = System.currentTimeMillis()
                    )
                  )
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Text("সেভ করুন", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
