package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ManagedUserHelper
import com.example.data.model.ManagedUserProfile
import com.example.data.model.ManagedUserWithStats
import com.example.data.model.Order
import com.example.util.UserExportHelper
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

private val VerifiedBlue = Color(0xFF2563EB)
private val VerifiedBlueBg = Color(0xFFEFF6FF)

private fun formatUserTime(ms: Long): String {
  if (ms <= 0L) return "লগইন তথ্য নেই"
  return try {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(ms))
  } catch (_: Exception) {
    "লগইন তথ্য নেই"
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminUserManagementHub(
  users: List<ManagedUserWithStats>,
  storeName: String,
  onToggleVerified: (String, ManagedUserProfile) -> Unit,
  onUpdateUserInfo: (String, String, String, String, String, String, String, ManagedUserProfile) -> Unit,
  onBlockUser: (String, String, ManagedUserProfile) -> Unit,
  onUnblockUser: (String, ManagedUserProfile) -> Unit,
  onUnlockLogin: (String, ManagedUserProfile) -> Unit,
  onResetPassword: (String, String, ManagedUserProfile) -> String,
  onAdjustWallet: (String, String, Int, String, ManagedUserProfile) -> Unit,
  onViewOrderInvoice: (Order) -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var filterKey by remember { mutableStateOf("ALL") } // ALL, VERIFIED, UNVERIFIED, BLOCKED, LOGIN_ISSUE, WALLET
  var districtFilter by remember { mutableStateOf("ALL") }
  var selectedUserKey by remember { mutableStateOf<String?>(null) }

  val districts = remember(users) {
    users.map { it.profile.district.trim() }
      .filter { it.isNotBlank() }
      .distinct()
      .sorted()
  }

  val filteredUsers = remember(users, searchQuery, filterKey, districtFilter) {
    val q = searchQuery.trim().lowercase()
    users.filter { item ->
      val u = item.profile
      val matchesFilter = when (filterKey) {
        "VERIFIED" -> u.isVerified
        "UNVERIFIED" -> !u.isVerified
        "BLOCKED" -> u.accountStatus == "blocked"
        "LOGIN_ISSUE" -> u.loginLocked || u.failedLoginAttempts > 0 || u.accountStatus == "blocked"
        "WALLET" -> u.walletBalance > 0
        else -> true
      }
      val matchesDistrict = districtFilter == "ALL" || u.district.equals(districtFilter, ignoreCase = true)
      val matchesQuery = q.isBlank() ||
        u.name.lowercase().contains(q) ||
        u.phone.lowercase().contains(q) ||
        u.email.lowercase().contains(q) ||
        u.district.lowercase().contains(q) ||
        u.thana.lowercase().contains(q) ||
        u.deviceInfo.lowercase().contains(q) ||
        u.blockReason.lowercase().contains(q)
      matchesFilter && matchesDistrict && matchesQuery
    }
  }

  val verifiedCount = remember(users) { users.count { it.profile.isVerified } }
  val blockedCount = remember(users) { users.count { it.profile.accountStatus == "blocked" } }
  val loginIssueCount = remember(users) { users.count { it.profile.loginLocked || it.profile.failedLoginAttempts > 0 } }
  val totalWalletBalance = remember(users) { users.sumOf { it.profile.walletBalance } }

  val selectedUser = remember(users, selectedUserKey) {
    selectedUserKey?.let { key -> users.find { it.key == key } }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_user_management_hub"),
    contentPadding = PaddingValues(bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Header + KPI Summary + Export Toolbar
    item {
      GlossyAdminCard(contentPadding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "🛡️ User Management & Control",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = AdminInkPrimary
              )
              Text(
                text = "ভেরিফাইড নীল টিক, ট্র্যাকিং, লগইন আনলক, পাসওয়ার্ড রিসেট, ব্লক লিস্ট ও Wallet",
                fontSize = 11.5.sp,
                color = AdminInkSecondary
              )
            }
          }

          // KPI Strip
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            UserKpiPill("মোট ইউজার", "${users.size}", AdminV2CyanDeep, AdminV2IceSurface)
            UserKpiPill("Verified ✓", "$verifiedCount", VerifiedBlue, VerifiedBlueBg)
            UserKpiPill("Block List", "$blockedCount", AdminV2Rose, Color(0xFFFEF2F2))
            UserKpiPill("Login Issue", "$loginIssueCount", AdminV2Amber, Color(0xFFFFFBEB))
            UserKpiPill("মোট Wallet", "৳$totalWalletBalance", AdminV2Emerald, Color(0xFFECFDF5))
          }

          // Export Buttons (CSV / Excel / PDF)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                UserExportHelper.exportUsersCsv(context, filteredUsers)
                  .onSuccess { onShowToast("📄 ${filteredUsers.size} জন ইউজারের CSV ডাউনলোড/শেয়ার প্রস্তুত!") }
                  .onFailure { onShowToast("⚠️ CSV তৈরি ব্যর্থ: ${it.localizedMessage}") }
              },
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, AdminV2CyanDeep),
              modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("export_users_csv_btn")
            ) {
              Icon(Icons.Default.Download, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("CSV (${filteredUsers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
            }

            OutlinedButton(
              onClick = {
                UserExportHelper.exportUsersExcel(context, filteredUsers)
                  .onSuccess { onShowToast("📊 ${filteredUsers.size} জন ইউজারের Excel (.xls) প্রস্তুত!") }
                  .onFailure { onShowToast("⚠️ Excel তৈরি ব্যর্থ: ${it.localizedMessage}") }
              },
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, AdminV2Emerald),
              modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("export_users_excel_btn")
            ) {
              Icon(Icons.Default.Download, contentDescription = null, tint = AdminV2Emerald, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Excel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2Emerald)
            }

            Button(
              onClick = {
                UserExportHelper.exportUsersPdf(context, filteredUsers)
                  .onSuccess { onShowToast("📕 ${filteredUsers.size} জন ইউজারের PDF রিপোর্ট প্রস্তুত!") }
                  .onFailure { onShowToast("⚠️ PDF তৈরি ব্যর্থ: ${it.localizedMessage}") }
              },
              colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("export_users_pdf_btn")
            ) {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2. Search & Filter Strip
    item {
      GlossyAdminCard(contentPadding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("নাম, ফোন, ইমেইল, জেলা, থানা বা ডিভাইস দিয়ে খুঁজুন...", fontSize = 12.5.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AdminV2CyanDeep) },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("user_mgmt_search_input")
          )

          val filterTabs = listOf(
            "ALL" to "সব (${users.size})",
            "VERIFIED" to "Verified ✓ ($verifiedCount)",
            "UNVERIFIED" to "Unverified (${users.size - verifiedCount})",
            "BLOCKED" to "Block List ($blockedCount)",
            "LOGIN_ISSUE" to "Login Issue ($loginIssueCount)",
            "WALLET" to "Wallet (${users.count { it.profile.walletBalance > 0 }})"
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            filterTabs.forEach { (key, label) ->
              val active = filterKey == key
              Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (active) AdminV2CyanDeep else Color.White,
                border = BorderStroke(1.dp, if (active) AdminV2CyanDeep else AdminCardBorderWarm),
                modifier = Modifier
                  .heightIn(min = 48.dp)
                  .clickable { filterKey = key }
                  .testTag("user_filter_${key.lowercase()}")
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                  Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (active) Color.White else AdminInkPrimary
                  )
                }
              }
            }
          }

          if (districts.isNotEmpty()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("জেলা:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AdminInkMuted)
              FilterDistrictChip("সব জেলা", districtFilter == "ALL") { districtFilter = "ALL" }
              districts.forEach { dist ->
                FilterDistrictChip(dist, districtFilter == dist) { districtFilter = dist }
              }
            }
          }
        }
      }
    }

    // 3. User Cards List
    if (filteredUsers.isEmpty()) {
      item {
        GlossyAdminCard(contentPadding = 24.dp) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text("🔍", fontSize = 32.sp)
            Text("কোনো ইউজার পাওয়া যায়নি", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
            Text("সার্চ বা ফিল্টার পরিবর্তন করে আবার চেষ্টা করুন।", fontSize = 12.sp, color = AdminInkMuted)
          }
        }
      }
    } else {
      items(filteredUsers, key = { it.key }) { item ->
        val u = item.profile
        val isBlocked = u.accountStatus == "blocked"
        GlossyAdminCard(
          accentBorderColor = when {
            isBlocked -> AdminV2Rose.copy(alpha = 0.55f)
            u.isVerified -> VerifiedBlue.copy(alpha = 0.45f)
            else -> AdminCardBorderWarm
          },
          contentPadding = 14.dp,
          modifier = Modifier.testTag("user_card_${item.key}")
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Avatar
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(
                    when {
                      isBlocked -> AdminV2Rose.copy(alpha = 0.14f)
                      u.isVerified -> VerifiedBlueBg
                      else -> AdminV2IceSurface
                    }
                  )
                  .border(1.2.dp, if (u.isVerified) VerifiedBlue else AdminCardBorderWarm, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = u.name.trim().firstOrNull()?.uppercase() ?: "U",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black,
                  color = if (isBlocked) AdminV2Rose else if (u.isVerified) VerifiedBlue else AdminV2CyanDeep
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                  Text(
                    text = u.name.ifBlank { "সম্মানিত গ্রাহক" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = AdminInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  if (u.isVerified) {
                    Icon(
                      imageVector = Icons.Default.Verified,
                      contentDescription = "Verified User",
                      tint = VerifiedBlue,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
                Text(
                  text = buildString {
                    if (u.phone.isNotBlank()) append("📞 ${u.phone}")
                    if (u.email.isNotBlank()) {
                      if (isNotEmpty()) append(" • ")
                      append("✉️ ${u.email}")
                    }
                    if (isEmpty()) append("যোগাযোগের তথ্য নেই")
                  },
                  fontSize = 11.5.sp,
                  color = AdminInkSecondary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              // Quick Verify Toggle
              IconButton(
                onClick = { onToggleVerified(item.key, u) },
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (u.isVerified) VerifiedBlueBg else Color(0xFFF8FAFC))
                  .testTag("verify_toggle_${item.key}")
              ) {
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = if (u.isVerified) "Unverify User" else "Verify User",
                  tint = if (u.isVerified) VerifiedBlue else Color(0xFF94A3B8),
                  modifier = Modifier.size(22.dp)
                )
              }
            }

            // Status / Block Reason / Login Issue Badges
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              if (isBlocked) {
                UserBadgePill("🚫 Blocked: ${u.blockReason.ifBlank { "No reason" }}", AdminV2Rose, Color(0xFFFEF2F2))
              } else {
                UserBadgePill("● Active", AdminV2Emerald, Color(0xFFECFDF5))
              }
              if (u.loginLocked) {
                UserBadgePill("🔐 Login Locked", AdminV2Amber, Color(0xFFFFFBEB))
              }
              if (u.district.isNotBlank() || u.thana.isNotBlank()) {
                UserBadgePill("📍 ${listOf(u.thana, u.district).filter { it.isNotBlank() }.joinToString(", ")}", AdminV2CyanDeep, AdminV2IceSurface)
              }
              UserBadgePill("👛 Wallet: ৳${u.walletBalance}", Color(0xFF047857), Color(0xFFECFDF5))
            }

            // Tracking Stats Grid
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("অর্ডার / বাতিল / রিটার্ন", fontSize = 10.sp, color = AdminInkMuted)
                Text(
                  "${item.totalOrders}টি  •  বাতিল ${item.cancelledOrders}  •  রিটার্ন ${item.returnCount}",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("মোট খরচ (Spend)", fontSize = 10.sp, color = AdminInkMuted)
                Text("৳${item.totalSpend}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
              }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = "🕒 সর্বশেষ লগইন: ${formatUserTime(u.lastLoginAtMs)}",
                fontSize = 11.sp,
                color = AdminInkSecondary
              )
              if (u.deviceInfo.isNotBlank()) {
                Text(
                  text = "📱 ডিভাইস: ${u.deviceInfo}",
                  fontSize = 11.sp,
                  color = AdminInkMuted,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            // Action Buttons Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { selectedUserKey = item.key },
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .heightIn(min = 48.dp)
                  .testTag("manage_user_${item.key}")
              ) {
                Text("কন্ট্রোল / অর্ডার / Wallet →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }

              if (isBlocked) {
                OutlinedButton(
                  onClick = { onUnblockUser(item.key, u) },
                  border = BorderStroke(1.dp, AdminV2Emerald),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("quick_unblock_${item.key}")
                ) {
                  Text("Unblock", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2Emerald)
                }
              } else if (u.loginLocked) {
                OutlinedButton(
                  onClick = { onUnlockLogin(item.key, u) },
                  border = BorderStroke(1.dp, AdminV2Amber),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.heightIn(min = 48.dp)
                ) {
                  Text("Unlock", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2Amber)
                }
              }
            }
          }
        }
      }
    }
  }

  // Full User Control Modal Dialog
  if (selectedUser != null) {
    UserControlDetailDialog(
      userStats = selectedUser,
      storeName = storeName,
      onDismiss = { selectedUserKey = null },
      onToggleVerified = { onToggleVerified(selectedUser.key, selectedUser.profile) },
      onUpdateInfo = { name, phone, email, address, district, thana ->
        onUpdateUserInfo(selectedUser.key, name, phone, email, address, district, thana, selectedUser.profile)
      },
      onBlock = { reason -> onBlockUser(selectedUser.key, reason, selectedUser.profile) },
      onUnblock = { onUnblockUser(selectedUser.key, selectedUser.profile) },
      onUnlockLogin = { onUnlockLogin(selectedUser.key, selectedUser.profile) },
      onResetPassword = { tempPass -> onResetPassword(selectedUser.key, tempPass, selectedUser.profile) },
      onAdjustWallet = { type, amt, note ->
        onAdjustWallet(selectedUser.key, type, amt, note, selectedUser.profile)
      },
      onViewOrderInvoice = onViewOrderInvoice,
      onShowToast = onShowToast
    )
  }
}

@Composable
private fun UserKpiPill(label: String, value: String, ink: Color, bg: Color) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bg,
    border = BorderStroke(1.dp, ink.copy(alpha = 0.25f))
  ) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
      Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AdminInkMuted)
      Text(value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = ink)
    }
  }
}

@Composable
private fun UserBadgePill(text: String, ink: Color, bg: Color) {
  Surface(
    shape = RoundedCornerShape(999.dp),
    color = bg,
    border = BorderStroke(0.8.dp, ink.copy(alpha = 0.3f))
  ) {
    Text(
      text = text,
      fontSize = 10.5.sp,
      fontWeight = FontWeight.Bold,
      color = ink,
      modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
    )
  }
}

@Composable
private fun FilterDistrictChip(label: String, active: Boolean, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (active) AdminV2CyanDeep else Color.White,
    border = BorderStroke(1.dp, if (active) AdminV2CyanDeep else AdminCardBorderWarm),
    modifier = Modifier
      .heightIn(min = 48.dp)
      .clickable(onClick = onClick)
  ) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
      Text(
        text = label,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        color = if (active) Color.White else AdminInkSecondary
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UserControlDetailDialog(
  userStats: ManagedUserWithStats,
  storeName: String,
  onDismiss: () -> Unit,
  onToggleVerified: () -> Unit,
  onUpdateInfo: (String, String, String, String, String, String) -> Unit,
  onBlock: (String) -> Unit,
  onUnblock: () -> Unit,
  onUnlockLogin: () -> Unit,
  onResetPassword: (String) -> String,
  onAdjustWallet: (String, Int, String) -> Unit,
  onViewOrderInvoice: (Order) -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current
  val u = userStats.profile
  var activeTab by remember { mutableStateOf("CONTROL") } // CONTROL, ORDERS, WALLET, SECURITY, EDIT

  var blockReasonInput by remember(userStats.key) { mutableStateOf(u.blockReason) }
  var tempPasswordInput by remember(userStats.key) {
    mutableStateOf(u.tempPassword.ifBlank { "SJG-${Random.nextInt(1000, 9999)}" })
  }
  var lastIssuedTempPass by remember(userStats.key) { mutableStateOf(u.tempPassword) }

  var walletMode by remember { mutableStateOf("credit") }
  var walletAmountInput by remember { mutableStateOf("") }
  var walletNoteInput by remember { mutableStateOf("") }

  var editName by remember(userStats.key) { mutableStateOf(u.name) }
  var editPhone by remember(userStats.key) { mutableStateOf(u.phone) }
  var editEmail by remember(userStats.key) { mutableStateOf(u.email) }
  var editDistrict by remember(userStats.key) { mutableStateOf(u.district) }
  var editThana by remember(userStats.key) { mutableStateOf(u.thana) }
  var editAddress by remember(userStats.key) { mutableStateOf(u.address) }

  LaunchedEffect(u.tempPassword) {
    if (u.tempPassword.isNotBlank()) {
      lastIssuedTempPass = u.tempPassword
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .heightIn(max = 760.dp)
        .testTag("user_control_dialog")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(AdminV2IceSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = u.name.ifBlank { "সম্মানিত গ্রাহক" },
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = AdminInkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (u.isVerified) {
                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = VerifiedBlue, modifier = Modifier.size(19.dp))
              }
            }
            Text(
              text = "${u.phone.ifBlank { "ফোন নেই" }} • Wallet: ৳${u.walletBalance}",
              fontSize = 11.5.sp,
              color = AdminInkSecondary
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = AdminInkPrimary)
          }
        }

        // Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "CONTROL" to "🛡️ কন্ট্রোল",
            "ORDERS" to "📦 অর্ডার (${userStats.totalOrders})",
            "WALLET" to "👛 Wallet (৳${u.walletBalance})",
            "SECURITY" to "🔑 লগইন/পাসওয়ার্ড",
            "EDIT" to "✏️ তথ্য আপডেট"
          ).forEach { (key, label) ->
            val selected = activeTab == key
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (selected) AdminV2CyanDeep else Color(0xFFF1F5F9),
              modifier = Modifier
                .heightIn(min = 48.dp)
                .clickable { activeTab = key }
                .testTag("user_detail_tab_${key.lowercase()}")
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                  text = label,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (selected) Color.White else AdminInkPrimary
                )
              }
            }
          }
        }

        HorizontalDivider(color = AdminCardBorderSoft)

        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          when (activeTab) {
            "CONTROL" -> {
              // Verify / Unverify Blue Tick Card
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (u.isVerified) VerifiedBlueBg else Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, if (u.isVerified) VerifiedBlue else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (u.isVerified) "✓ Verified ইউজার (Blue Tick সক্রিয়)" else "Unverified ইউজার",
                      fontSize = 13.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (u.isVerified) VerifiedBlue else AdminInkPrimary
                    )
                    Text(
                      text = "Verify করলে ইউজারের নামের পাশে নীল টিকমার্ক ✓ দেখাবে।",
                      fontSize = 11.sp,
                      color = AdminInkMuted
                    )
                  }
                  Button(
                    onClick = onToggleVerified,
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (u.isVerified) Color(0xFF64748B) else VerifiedBlue
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.heightIn(min = 48.dp)
                  ) {
                    Text(if (u.isVerified) "Unverify" else "Verify ✓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              // Tracking Summary
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                  Text("📊 কাস্টমার ট্র্যাকিং তথ্য", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
                  Text("• সর্বশেষ লগইন: ${formatUserTime(u.lastLoginAtMs)}", fontSize = 12.sp, color = AdminInkSecondary)
                  Text("• ডিভাইস: ${u.deviceInfo.ifBlank { "অজানা ডিভাইস" }}", fontSize = 12.sp, color = AdminInkSecondary)
                  Text("• জেলা / থানা: ${u.district.ifBlank { "-" }} / ${u.thana.ifBlank { "-" }}", fontSize = 12.sp, color = AdminInkSecondary)
                  Text("• ঠিকানা: ${u.address.ifBlank { "-" }}", fontSize = 12.sp, color = AdminInkSecondary)
                  Text("• মোট অর্ডার: ${userStats.totalOrders} • বাতিল: ${userStats.cancelledOrders} • রিটার্ন: ${userStats.returnCount}", fontSize = 12.sp, color = AdminInkSecondary)
                  Text("• মোট খরচ: ৳${userStats.totalSpend}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
                }
              }

              // Block / Unblock with Required Reason
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (u.accountStatus == "blocked") Color(0xFFFEF2F2) else Color.White,
                border = BorderStroke(1.dp, if (u.accountStatus == "blocked") AdminV2Rose else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = if (u.accountStatus == "blocked") "🚫 ইউজার বর্তমানে ব্লকড আছে" else "🛡️ অ্যাকাউন্ট ব্লক / কন্ট্রোল",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black,
                    color = if (u.accountStatus == "blocked") AdminV2Rose else AdminInkPrimary
                  )
                  if (u.accountStatus == "blocked") {
                    Text("কারণ: ${u.blockReason.ifBlank { "-" }}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2Rose)
                    Button(
                      onClick = onUnblock,
                      colors = ButtonDefaults.buttonColors(containerColor = AdminV2Emerald),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                    ) {
                      Text("✅ Unblock করে Active করুন", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    }
                  } else {
                    Text("ব্লকের কারণ নির্বাচন করুন বা লিখুন:", fontSize = 11.5.sp, color = AdminInkSecondary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                      listOf("Fake Order", "খারাপ ব্যবহার", "Fraud", "Multiple Return Abuse").forEach { reason ->
                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = if (blockReasonInput == reason) AdminV2Rose else Color(0xFFF8FAFC),
                          border = BorderStroke(1.dp, if (blockReasonInput == reason) AdminV2Rose else Color(0xFFCBD5E1)),
                          modifier = Modifier.clickable { blockReasonInput = reason }
                        ) {
                          Text(
                            text = reason,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (blockReasonInput == reason) Color.White else AdminInkPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                          )
                        }
                      }
                    }
                    OutlinedTextField(
                      value = blockReasonInput,
                      onValueChange = { blockReasonInput = it },
                      label = { Text("Block Reason *") },
                      placeholder = { Text("যেমন: Fake Order / খারাপ ব্যবহার / Fraud") },
                      singleLine = true,
                      modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                      onClick = {
                        if (blockReasonInput.isBlank()) {
                          onShowToast("⚠️ ইউজারকে ব্লক করার জন্য কারণ লিখুন!")
                        } else {
                          onBlock(blockReasonInput.trim())
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = AdminV2Rose),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                    ) {
                      Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(17.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("ইউজার Block করুন", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            "ORDERS" -> {
              if (userStats.orders.isEmpty()) {
                Text("এই ইউজারের এখনও কোনো অর্ডার নেই।", fontSize = 13.sp, color = AdminInkMuted)
              } else {
                userStats.orders.forEach { ord ->
                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text("#${ord.id} • ${ord.date}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
                        Surface(
                          shape = RoundedCornerShape(999.dp),
                          color = if (ord.status.equals("Cancelled", true)) Color(0xFFFEF2F2) else Color(0xFFECFDF5)
                        ) {
                          Text(
                            text = ord.status,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ord.status.equals("Cancelled", true)) AdminV2Rose else AdminV2Emerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                          )
                        }
                      }
                      ord.items.forEach { itm ->
                        Text(
                          text = "• ${itm.name} × ${itm.quantity} (৳${itm.price * itm.quantity})",
                          fontSize = 11.5.sp,
                          color = AdminInkSecondary
                        )
                      }
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "মোট: ৳${ord.total}" + (if (ord.walletPaidAmount > 0) " (+Wallet ৳${ord.walletPaidAmount})" else "") + " • ${ord.paymentMethod}",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = AdminV2CyanDeep
                        )
                        TextButton(
                          onClick = { onViewOrderInvoice(ord) },
                          modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                          Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("মেমো", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                }
              }
              if (userStats.returns.isNotEmpty()) {
                Text("🔄 রিটার্ন / এক্সচেঞ্জ রেকর্ড (${userStats.returns.size})", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
                userStats.returns.forEach { ret ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, AdminV2Amber.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text("${ret.id} (${ret.orderId}) • ${ret.date}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                      Text("কারণ: ${ret.reason} • নিষ্পত্তি: ${ret.settlementMethod}", fontSize = 11.sp, color = AdminInkSecondary)
                    }
                  }
                }
              }
            }

            "WALLET" -> {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFECFDF5),
                border = BorderStroke(1.2.dp, AdminV2Emerald.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text("বর্তমান Wallet ব্যালেন্স", fontSize = 11.5.sp, color = Color(0xFF065F46))
                    Text("৳${u.walletBalance}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = AdminV2Emerald)
                  }
                  Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AdminV2Emerald, modifier = Modifier.size(34.dp))
                }
              }

              // Credit / Debit Form
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text("Wallet ব্যালেন্স যোগ / বাদ দিন", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                      onClick = { walletMode = "credit" },
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (walletMode == "credit") AdminV2Emerald else Color(0xFFF1F5F9),
                        contentColor = if (walletMode == "credit") Color.White else AdminInkPrimary
                      ),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                    ) {
                      Text("+ টাকা যোগ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                      onClick = { walletMode = "debit" },
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (walletMode == "debit") AdminV2Rose else Color(0xFFF1F5F9),
                        contentColor = if (walletMode == "debit") Color.White else AdminInkPrimary
                      ),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                    ) {
                      Text("- টাকা বাদ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                  OutlinedTextField(
                    value = walletAmountInput,
                    onValueChange = { walletAmountInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("টাকার পরিমাণ (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  OutlinedTextField(
                    value = walletNoteInput,
                    onValueChange = { walletNoteInput = it },
                    label = { Text("নোট / কারণ (ঐচ্ছিক)") },
                    placeholder = { Text("যেমন: কাস্টমার বোনাস / রিটার্ন রিফান্ড") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  Button(
                    onClick = {
                      val amt = walletAmountInput.toIntOrNull() ?: 0
                      if (amt <= 0) {
                        onShowToast("⚠️ সঠিক টাকার পরিমাণ দিন!")
                      } else {
                        onAdjustWallet(walletMode, amt, walletNoteInput.trim())
                        walletAmountInput = ""
                        walletNoteInput = ""
                      }
                    },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (walletMode == "debit") AdminV2Rose else AdminV2Emerald
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(min = 48.dp)
                  ) {
                    Text(
                      text = if (walletMode == "debit") "Wallet থেকে টাকা বাদ দিন" else "Wallet-এ টাকা যোগ করুন",
                      fontSize = 12.5.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }

              Text("📜 Wallet লেনদেনের ইতিহাস (${u.walletHistory.size})", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
              if (u.walletHistory.isEmpty()) {
                Text("এখনও কোনো Wallet লেনদেন হয়নি।", fontSize = 12.sp, color = AdminInkMuted)
              } else {
                u.walletHistory.forEach { tx ->
                  val isDebit = tx.type == "debit"
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(tx.note.ifBlank { tx.type }, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
                        Text(formatUserTime(tx.timestampMs), fontSize = 10.5.sp, color = AdminInkMuted)
                      }
                      Text(
                        text = if (isDebit) "-৳${tx.amount}" else "+৳${tx.amount}",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDebit) AdminV2Rose else AdminV2Emerald
                      )
                    }
                  }
                }
              }
            }

            "SECURITY" -> {
              // Login Issue & Unlock Card
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (u.loginLocked) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, if (u.loginLocked) AdminV2Amber else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text("🔓 লগইন সমস্যা সমাধান", fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
                  Text(
                    text = "অবস্থা: ${if (u.loginLocked) "লগইন লকড" else "স্বাভাবিক"} • ব্যর্থ চেষ্টা: ${u.failedLoginAttempts} বার",
                    fontSize = 12.sp,
                    color = AdminInkSecondary
                  )
                  Button(
                    onClick = onUnlockLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(min = 48.dp)
                  ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Login Issue সমাধান ও Unlock করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              // Temporary Password Reset + SMS / WhatsApp Send
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text("🔑 Password Reset (Temporary Password)", fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                      value = tempPasswordInput,
                      onValueChange = { tempPasswordInput = it },
                      label = { Text("Temporary Password") },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                    IconButton(
                      onClick = { tempPasswordInput = "SJG-${Random.nextInt(1000, 9999)}" },
                      modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AdminV2IceSurface)
                    ) {
                      Icon(Icons.Default.Refresh, contentDescription = "নতুন পাসওয়ার্ড জেনারেট", tint = AdminV2CyanDeep)
                    }
                  }
                  Button(
                    onClick = { lastIssuedTempPass = onResetPassword(tempPasswordInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(min = 48.dp)
                  ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Temporary Password সেট করুন", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                  }

                  val activePass = lastIssuedTempPass.ifBlank { tempPasswordInput }
                  val shareText = "$storeName অ্যাকাউন্ট লগইন Temporary Password: $activePass — দয়া করে লগইন করে প্রোফাইল থেকে পাসওয়ার্ড পরিবর্তন করুন।"

                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                      onClick = {
                        val normPhone = ManagedUserHelper.normalizePhone(u.phone)
                        if (normPhone.isBlank()) {
                          onShowToast("⚠️ ইউজারের মোবাইল নম্বর দেওয়া নেই!")
                        } else {
                          val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$normPhone")).apply {
                            putExtra("sms_body", shareText)
                          }
                          runCatching { context.startActivity(smsIntent) }
                            .onFailure { onShowToast("⚠️ SMS অ্যাপ ওপেন করা যায়নি।") }
                        }
                      },
                      border = BorderStroke(1.dp, AdminV2CyanDeep),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                    ) {
                      Icon(Icons.Default.Sms, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("SMS-এ পাঠান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
                    }

                    Button(
                      onClick = {
                        val normPhone = ManagedUserHelper.normalizePhone(u.phone)
                        if (normPhone.isBlank()) {
                          onShowToast("⚠️ ইউজারের মোবাইল নম্বর দেওয়া নেই!")
                        } else {
                          val waPhone = if (normPhone.startsWith("0")) "88$normPhone" else normPhone
                          val encoded = URLEncoder.encode(shareText, "UTF-8")
                          val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$waPhone?text=$encoded"))
                          runCatching { context.startActivity(waIntent) }
                            .onFailure { onShowToast("⚠️ WhatsApp ওপেন করা যায়নি।") }
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = AdminV2Emerald),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                    ) {
                      Text("WhatsApp-এ পাঠান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            "EDIT" -> {
              Text("✏️ কাস্টমারের তথ্য আপডেট করুন", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
              OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("নাম") }, singleLine = true, modifier = Modifier.fillMaxWidth())
              OutlinedTextField(value = editPhone, onValueChange = { editPhone = it }, label = { Text("ফোন নম্বর") }, singleLine = true, modifier = Modifier.fillMaxWidth())
              OutlinedTextField(value = editEmail, onValueChange = { editEmail = it }, label = { Text("ইমেইল") }, singleLine = true, modifier = Modifier.fillMaxWidth())
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = editDistrict, onValueChange = { editDistrict = it }, label = { Text("জেলা (District)") }, singleLine = true, modifier = Modifier.weight(1f))
                OutlinedTextField(value = editThana, onValueChange = { editThana = it }, label = { Text("থানা (Thana)") }, singleLine = true, modifier = Modifier.weight(1f))
              }
              OutlinedTextField(value = editAddress, onValueChange = { editAddress = it }, label = { Text("বিস্তারিত ঠিকানা (Address)") }, minLines = 2, modifier = Modifier.fillMaxWidth())
              Button(
                onClick = { onUpdateInfo(editName, editPhone, editEmail, editAddress, editDistrict, editThana) },
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 48.dp)
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("তথ্য সেভ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
