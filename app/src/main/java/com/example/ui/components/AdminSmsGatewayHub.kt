package com.example.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Sms
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.SmsQueueItem
import com.example.data.model.StoreConfig
import com.example.sms.SimSmsGatewayManager
import com.example.sms.SmsHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminSmsGatewayHub(
  storeConfig: StoreConfig,
  orders: List<Order>,
  smsQueue: List<SmsQueueItem>,
  isProcessingSmsQueue: Boolean,
  smsGatewayLastLog: String,
  onUpdateStoreConfig: (StoreConfig) -> Unit,
  onEnqueueSingleSms: (phone: String, customerName: String, message: String, orderId: String, type: String) -> Unit,
  onEnqueueBulkSms: (recipients: List<Pair<String, String>>, messageTemplate: String, type: String) -> Unit,
  onProcessPendingSmsNow: () -> Unit,
  onRetrySmsItem: (String) -> Unit,
  onMarkSmsSentViaIntent: (String) -> Unit,
  onDeleteSmsItem: (String) -> Unit,
  onClearCompletedSms: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current
  var hasSmsPermission by remember {
    mutableStateOf(SimSmsGatewayManager.hasSendSmsPermission(context))
  }

  val smsPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasSmsPermission = granted || SimSmsGatewayManager.hasSendSmsPermission(context)
    if (hasSmsPermission) {
      onShowToast("✅ SIM থেকে SMS পাঠানোর অনুমতি চালু হয়েছে!")
    } else {
      onShowToast("⚠️ অনুমতি না দিলে প্লে-স্টোর সেফ ১-ক্লিক SMS মোড ব্যবহার করতে পারবেন।")
    }
  }

  var activeSubTab by remember { mutableStateOf("SEND_COMPOSE") } // SEND_COMPOSE | LIVE_QUEUE
  var manualPhonesInput by remember { mutableStateOf("") }
  var messageInput by remember {
    mutableStateOf(SmsHelper.defaultTemplates(storeConfig.storeName).firstOrNull()?.body ?: "")
  }
  var selectedCustomerPhones by remember { mutableStateOf(setOf<String>()) }
  var queueStatusFilter by remember { mutableStateOf("All") }

  val pendingCount = remember(smsQueue) { smsQueue.count { it.status == "pending" || it.status == "sending" } }
  val sentCount = remember(smsQueue) { smsQueue.count { it.status == "sent" } }
  val failedCount = remember(smsQueue) { smsQueue.count { it.status == "failed" } }

  // Extract unique CRM customer phone/name pairs from recent orders
  val customerDirectory = remember(orders) {
    val map = LinkedHashMap<String, Pair<String, String>>() // normalizedPhone -> (name, latestOrderStatus)
    orders.forEach { order ->
      val normalized = SmsHelper.normalizePhone(order.phone)
      if (SmsHelper.isValidPhone(normalized) && !map.containsKey(normalized)) {
        map[normalized] = order.customerName.ifBlank { "সম্মানিত গ্রাহক" } to order.status
      }
    }
    map.entries.map { (phone, meta) -> Triple(phone, meta.first, meta.second) }
  }

  val templates = remember(storeConfig.storeName) {
    SmsHelper.defaultTemplates(storeConfig.storeName)
  }

  val parsedManualRecipients = remember(manualPhonesInput) {
    manualPhonesInput
      .split(",", "\n", ";")
      .map { SmsHelper.normalizePhone(it) }
      .filter { SmsHelper.isValidPhone(it) }
      .distinct()
      .map { it to "" }
  }

  val selectedCrmRecipients = remember(selectedCustomerPhones, customerDirectory) {
    customerDirectory
      .filter { selectedCustomerPhones.contains(it.first) }
      .map { it.first to it.second }
  }

  val combinedRecipients = remember(selectedCrmRecipients, parsedManualRecipients) {
    val merged = LinkedHashMap<String, String>()
    selectedCrmRecipients.forEach { (p, n) -> merged[p] = n }
    parsedManualRecipients.forEach { (p, n) -> if (!merged.containsKey(p)) merged[p] = n }
    merged.entries.map { it.key to it.value }
  }

  val charCount = messageInput.length
  val isUnicode = remember(messageInput) { SmsHelper.containsNonAscii(messageInput) }
  val estimatedParts = remember(messageInput) { SmsHelper.estimateSmsParts(messageInput) }

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Vibrant Header Card (Dual-Mode Summary)
    AdminVibrantHeroCard(
      title = "SIM SMS গেটওয়ে ও বাল্ক SMS হাব",
      subtitle = "মোড ১: পার্সোনাল SIM অটো-গেটওয়ে • মোড ২: Play Store সেফ ১-ক্লিক SMS",
      icon = Icons.Default.Sms,
      brush = adminIndigoSkyBrush(),
      trailingContent = {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (storeConfig.smsGatewayDeviceEnabled && hasSmsPermission) Color(0xFF10B981) else Color.White.copy(alpha = 0.22f),
          border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
        ) {
          Text(
            text = if (storeConfig.smsGatewayDeviceEnabled && hasSmsPermission) "🟢 SIM লাইভ" else "⚪ স্ট্যান্ডবাই",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      },
      bottomContent = {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AdminHeroGlassChip(text = "কিউ পেন্ডিং: $pendingCount", dotColor = Color(0xFFFDE68A))
          AdminHeroGlassChip(text = "সফল: $sentCount", dotColor = Color(0xFF6EE7B7))
          if (failedCount > 0) {
            AdminHeroGlassChip(text = "ব্যর্থ: $failedCount", dotColor = Color(0xFFFCA5A5))
          }
        }
      }
    )

    // 2. Sub-navigation Pills
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      GlossySegmentPill(
        label = "✍️ কম্পোজ ও বাল্ক SMS",
        selected = activeSubTab == "SEND_COMPOSE",
        onClick = { activeSubTab = "SEND_COMPOSE" },
        modifier = Modifier
          .weight(1f)
          .testTag("sms_subtab_compose")
      )
      GlossySegmentPill(
        label = "📋 লাইভ কিউ ও লগ",
        selected = activeSubTab == "LIVE_QUEUE",
        badgeText = smsQueue.size.toString(),
        onClick = { activeSubTab = "LIVE_QUEUE" },
        modifier = Modifier
          .weight(1f)
          .testTag("sms_subtab_queue")
      )
    }

    if (activeSubTab == "SEND_COMPOSE") {
      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // MODE 1 CARD: Personal Admin Mobile SIM Gateway
        GlossyAdminCard(
          accentBorderColor = if (storeConfig.smsGatewayDeviceEnabled) AdminV2Emerald else AdminCardBorderWarm,
          contentPadding = 14.dp
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                Icon(
                  imageVector = Icons.Default.SimCard,
                  contentDescription = "সিম গেটওয়ে",
                  tint = AdminV2CyanDeep,
                  modifier = Modifier.size(22.dp)
                )
                Column {
                  Text(
                    text = "মোড ১: পার্সোনাল SIM SMS গেটওয়ে",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = AdminInkPrimary
                  )
                  Text(
                    text = "ডিভাইস: ${SimSmsGatewayManager.deviceModelLabel()}",
                    fontSize = 11.sp,
                    color = AdminInkMuted
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (hasSmsPermission) AdminV2EmeraldSoft else AdminV2AmberSoft,
                border = BorderStroke(1.dp, if (hasSmsPermission) AdminV2Emerald else AdminV2Amber)
              ) {
                Text(
                  text = if (hasSmsPermission) "✅ অনুমতি আছে" else "⚠️ অনুমতি নেই",
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (hasSmsPermission) AdminV2Emerald else AdminV2Amber,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            if (!hasSmsPermission) {
              Button(
                onClick = { smsPermissionLauncher.launch(Manifest.permission.SEND_SMS) },
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 48.dp)
                  .testTag("sms_permission_grant_btn")
              ) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("আপনার ফোনের সিম থেকে SMS পাঠাতে অনুমতি দিন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Switch 1: Live SIM Gateway Host
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(AdminV2IceSurface, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "এই ফোনকে লাইভ SMS গেটওয়ে হিসেবে রাখুন",
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary
                )
                Text(
                  text = "চালু থাকলে ওয়েব/অ্যাপের পেন্ডিং কিউ স্বয়ংক্রিয়ভাবে সিম থেকে যাবে",
                  fontSize = 10.5.sp,
                  color = AdminInkMuted
                )
              }
              Switch(
                checked = storeConfig.smsGatewayDeviceEnabled,
                onCheckedChange = { enabled ->
                  if (enabled && !hasSmsPermission) {
                    smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                  }
                  onUpdateStoreConfig(storeConfig.copy(smsGatewayDeviceEnabled = enabled))
                },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AdminV2Emerald),
                modifier = Modifier.testTag("sms_gateway_switch")
              )
            }

            // Switch 2: Auto-queue on Order Status Change
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(AdminV2IceSurface, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "অর্ডার কনফার্ম / কুরিয়ারে অটো SMS কিউ",
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary
                )
                Text(
                  text = "অর্ডার স্ট্যাটাস আপডেট বা Steadfast/Pathao ডিসপ্যাচে স্বয়ংক্রিয় SMS",
                  fontSize = 10.5.sp,
                  color = AdminInkMuted
                )
              }
              Switch(
                checked = storeConfig.smsAutoSendOnOrderStatus,
                onCheckedChange = { enabled ->
                  onUpdateStoreConfig(storeConfig.copy(smsAutoSendOnOrderStatus = enabled))
                },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AdminV2CyanDeep),
                modifier = Modifier.testTag("sms_auto_order_switch")
              )
            }

            // Rate-limit Delay Selector
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "প্রতি বাল্ক SMS-এর বিরতি (সিম ব্লক সুরক্ষা):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkSecondary
              )
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(3, 4, 6, 10).forEach { sec ->
                  val isSel = storeConfig.smsDelaySeconds == sec
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSel) AdminV2CyanDeep else Color.White,
                    border = BorderStroke(1.dp, if (isSel) AdminV2CyanDeep else AdminCardBorderWarm),
                    modifier = Modifier
                      .heightIn(min = 36.dp)
                      .clickable { onUpdateStoreConfig(storeConfig.copy(smsDelaySeconds = sec)) }
                  ) {
                    Text(
                      text = "${sec}s",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = if (isSel) Color.White else AdminInkPrimary,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                  }
                }
              }
            }

            // Live Engine Log + Manual Trigger
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFF8FAFC),
              border = BorderStroke(1.dp, AdminCardBorderSoft),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                if (isProcessingSmsQueue) {
                  CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = AdminV2CyanDeep)
                }
                Text(
                  text = "স্ট্যাটাস: $smsGatewayLastLog",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = AdminInkSecondary,
                  modifier = Modifier.weight(1f),
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            if (pendingCount > 0) {
              GlossyGradientButton(
                text = if (isProcessingSmsQueue) "SIM দিয়ে পাঠানো চলছে..." else "🚀 পেন্ডিং ($pendingCount টি) এখনই SIM দিয়ে পাঠান",
                icon = Icons.AutoMirrored.Filled.Send,
                brush = adminEmeraldTealBrush(),
                onClick = {
                  if (!isProcessingSmsQueue) {
                    if (!hasSmsPermission) {
                      smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                    } else {
                      onProcessPendingSmsNow()
                    }
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("sms_process_now_btn")
              )
            }
          }
        }

        // MODE 2 GUIDE CARD: Play Store Compliance
        GlossyAdminCard(
          accentBorderColor = Color(0xFF93C5FD),
          containerTintTop = Color(0xFFF0F9FF),
          containerTintBottom = Color.White,
          contentPadding = 12.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = "Google Play Policy Mode",
              tint = AdminV2CyanDeep,
              modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
              Text(
                text = "মোড ২: Play Store পাবলিশ মোড (১০০% পলিসি-সেফ)",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AdminInkPrimary
              )
              Text(
                text = "কাস্টমার ভার্সন Play Store-এ আপলোড করার আগে শুধু Manifest থেকে SEND_SMS পারমিশন লাইনটি সরালেই হবে — তখন '📩 ১-ক্লিক SMS' বাটনগুলো কোনো পারমিশন ছাড়াই ফোনের ডিফল্ট মেসেজ অ্যাপ খুলে দেবে।",
                fontSize = 11.sp,
                color = AdminInkSecondary,
                lineHeight = 16.sp
              )
            }
          }
        }

        // COMPOSE & BULK CAMPAIGN CARD
        GlossyAdminCard(
          accentBorderColor = AdminCardBorderWarm,
          contentPadding = 14.dp
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "✉️ সিঙ্গেল বা বাল্ক SMS পাঠান",
              fontSize = 14.5.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary
            )

            // Quick Templates
            Text(text = "কুইক টেমপ্লেট ({name} স্বয়ংক্রিয় বসবে):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminInkSecondary)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              templates.forEach { tpl ->
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = AdminV2SkySoft,
                  border = BorderStroke(1.dp, AdminV2Cyan.copy(alpha = 0.45f)),
                  modifier = Modifier
                    .heightIn(min = 40.dp)
                    .clickable { messageInput = tpl.body }
                ) {
                  Text(
                    text = tpl.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminV2CyanDeep,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)
                  )
                }
              }
            }

            // CRM Customer Selector
            if (customerDirectory.isNotEmpty()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "গ্রাহক তালিকা (${selectedCustomerPhones.size}/${customerDirectory.size} নির্বাচিত):",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AdminV2IceSurface,
                    border = BorderStroke(1.dp, AdminCardBorderWarm),
                    modifier = Modifier.clickable {
                      selectedCustomerPhones = customerDirectory.map { it.first }.toSet()
                    }
                  ) {
                    Text(
                      text = "সকল সিলেক্ট",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = AdminV2CyanDeep,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AdminV2IceSurface,
                    border = BorderStroke(1.dp, AdminCardBorderWarm),
                    modifier = Modifier.clickable {
                      selectedCustomerPhones = customerDirectory
                        .filter { it.third.equals("Pending", ignoreCase = true) || it.third.equals("Confirmed", ignoreCase = true) }
                        .map { it.first }
                        .toSet()
                    }
                  ) {
                    Text(
                      text = "পেন্ডিং অর্ডার",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = AdminV2Amber,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                  }
                  if (selectedCustomerPhones.isNotEmpty()) {
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color(0xFFFEF2F2),
                      border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                      modifier = Modifier.clickable { selectedCustomerPhones = emptySet() }
                    ) {
                      Text(
                        text = "ক্লিয়ার",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminV2Rose,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                      )
                    }
                  }
                }
              }

              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                customerDirectory.take(24).forEach { (phone, name, _) ->
                  val checked = selectedCustomerPhones.contains(phone)
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (checked) AdminV2CyanDeep else Color.White,
                    border = BorderStroke(1.dp, if (checked) AdminV2CyanDeep else AdminCardBorderWarm),
                    modifier = Modifier
                      .heightIn(min = 38.dp)
                      .clickable {
                        selectedCustomerPhones = if (checked) selectedCustomerPhones - phone else selectedCustomerPhones + phone
                      }
                  ) {
                    Text(
                      text = "${if (checked) "✓ " else ""}$name (${phone.takeLast(8)})",
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (checked) Color.White else AdminInkPrimary,
                      modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp)
                    )
                  }
                }
              }
            }

            // Manual Phone Entry
            OutlinedTextField(
              value = manualPhonesInput,
              onValueChange = { manualPhonesInput = it },
              label = { Text("ম্যানুয়াল নম্বর (কমা বা নতুন লাইন দিয়ে একাধিক)", fontSize = 11.5.sp) },
              placeholder = { Text("017XXXXXXXX, 018XXXXXXXX") },
              minLines = 2,
              maxLines = 4,
              shape = RoundedCornerShape(12.dp),
              colors = adminOutlinedTextFieldColors(),
              modifier = Modifier.fillMaxWidth()
            )

            // Message Body
            OutlinedTextField(
              value = messageInput,
              onValueChange = { messageInput = it },
              label = { Text("SMS মেসেজ লিখুন", fontSize = 11.5.sp) },
              minLines = 3,
              maxLines = 6,
              shape = RoundedCornerShape(12.dp),
              colors = adminOutlinedTextFieldColors(),
              modifier = Modifier.fillMaxWidth()
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "মোট প্রাপক: ${combinedRecipients.size} জন",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AdminV2CyanDeep
              )
              Text(
                text = "$charCount অক্ষর • ${if (isUnicode) "বাংলা/Unicode" else "ASCII"} • ≈$estimatedParts SMS",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminInkMuted
              )
            }

            // Dual Action Buttons (Mode 1 Queue vs Mode 2 Play-Safe Intent)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              GlossyGradientButton(
                text = if (combinedRecipients.size > 1) {
                  "📲 ${combinedRecipients.size} জনে SIM Gateway কিউতে পাঠান (Bulk)"
                } else {
                  "📲 SIM Gateway কিউতে যোগ করুন"
                },
                icon = Icons.Default.SimCard,
                brush = adminRoyalGlossBrush(),
                onClick = {
                  if (combinedRecipients.isEmpty()) {
                    onShowToast("⚠️ অন্তত ১টি সঠিক মোবাইল নম্বর সিলেক্ট বা টাইপ করুন।")
                  } else if (messageInput.isBlank()) {
                    onShowToast("⚠️ SMS মেসেজ লিখুন।")
                  } else if (combinedRecipients.size == 1) {
                    val (phone, name) = combinedRecipients.first()
                    val formatted = SmsHelper.formatTemplate(
                      template = messageInput,
                      customerName = name,
                      storeName = storeConfig.storeName
                    )
                    onEnqueueSingleSms(phone, name, formatted, "", "custom")
                  } else {
                    onEnqueueBulkSms(combinedRecipients, messageInput, "bulk_promo")
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("sms_queue_send_btn")
              )

              OutlinedButton(
                onClick = {
                  if (combinedRecipients.isEmpty()) {
                    onShowToast("⚠️ ১-ক্লিক SMS-এর জন্য ১টি নম্বর সিলেক্ট বা লিখুন।")
                  } else if (messageInput.isBlank()) {
                    onShowToast("⚠️ SMS মেসেজ লিখুন।")
                  } else {
                    val (phone, name) = combinedRecipients.first()
                    val formatted = SmsHelper.formatTemplate(
                      template = messageInput,
                      customerName = name,
                      storeName = storeConfig.storeName
                    )
                    SimSmsGatewayManager.launchPlaySafeSmsIntent(context, phone, formatted)
                  }
                },
                border = BorderStroke(1.2.dp, AdminV2CyanDeep),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 48.dp)
                  .testTag("sms_play_safe_intent_btn")
              ) {
                Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "✉️ ১-ক্লিক ডিফল্ট SMS অ্যাপ খুলুন (Play-Safe মোড)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminV2CyanDeep
                )
              }
            }
          }
        }
      }
    } else {
      // LIVE QUEUE & LOGS SUB-TAB
      val filteredQueue = remember(smsQueue, queueStatusFilter) {
        if (queueStatusFilter == "All") smsQueue else smsQueue.filter { it.status.equals(queueStatusFilter, ignoreCase = true) }
      }

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier
              .weight(1f)
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("All", "pending", "sent", "failed").forEach { st ->
              val sel = queueStatusFilter == st
              val label = when (st) {
                "All" -> "সব (${smsQueue.size})"
                "pending" -> "পেন্ডিং ($pendingCount)"
                "sent" -> "সফল ($sentCount)"
                else -> "ব্যর্থ ($failedCount)"
              }
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (sel) AdminV2CyanDeep else Color.White,
                border = BorderStroke(1.dp, if (sel) AdminV2CyanDeep else AdminCardBorderWarm),
                modifier = Modifier
                  .heightIn(min = 38.dp)
                  .clickable { queueStatusFilter = st }
              ) {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (sel) Color.White else AdminInkPrimary,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = onClearCompletedSms,
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White)
              .border(1.dp, AdminCardBorderWarm, RoundedCornerShape(12.dp))
              .testTag("sms_clear_completed_btn")
          ) {
            Icon(
              imageVector = Icons.Default.CleaningServices,
              contentDescription = "সম্পন্ন SMS লগ পরিষ্কার করুন",
              tint = AdminV2CyanDeep,
              modifier = Modifier.size(19.dp)
            )
          }
        }

        if (filteredQueue.isEmpty()) {
          GlossyAdminCard(contentPadding = 22.dp) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AdminV2CyanDeep,
                modifier = Modifier.size(34.dp)
              )
              Text(
                text = "এই ফিল্টারে কোনো SMS নেই",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AdminInkPrimary
              )
              Text(
                text = "কম্পোজ ট্যাব বা ওয়েব অ্যাডমিন থেকে SMS পাঠালে এখানে লাইভ সিঙ্ক দেখাবে।",
                fontSize = 11.5.sp,
                color = AdminInkMuted
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
          ) {
            items(filteredQueue, key = { it.id }) { item ->
              val (badgeColor, badgeBg, badgeText) = when (item.status) {
                "sent" -> Triple(AdminV2Emerald, AdminV2EmeraldSoft, "✅ পাঠানো হয়েছে")
                "sending" -> Triple(AdminV2CyanDeep, AdminV2SkySoft, "📤 পাঠানো হচ্ছে...")
                "failed" -> Triple(AdminV2Rose, Color(0xFFFEE2E2), "❌ ব্যর্থ")
                else -> Triple(AdminV2Amber, AdminV2AmberSoft, "⏳ পেন্ডিং")
              }

              GlossyAdminCard(
                cornerRadius = 16.dp,
                accentBorderColor = badgeColor.copy(alpha = 0.45f),
                contentPadding = 12.dp
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${item.phone}${if (item.customerName.isNotBlank()) " • ${item.customerName}" else ""}",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Black,
                        color = AdminInkPrimary
                      )
                      if (item.orderId.isNotBlank()) {
                        Text(
                          text = "অর্ডার #${item.orderId}",
                          fontSize = 10.5.sp,
                          fontWeight = FontWeight.Bold,
                          color = AdminV2CyanDeep
                        )
                      }
                    }
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = badgeBg,
                      border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                    ) {
                      Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }

                  Text(
                    text = item.message,
                    fontSize = 11.5.sp,
                    color = AdminInkSecondary
                  )

                  if (item.errorReason.isNotBlank()) {
                    Text(
                      text = "কারণ: ${item.errorReason}",
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = AdminV2Rose
                    )
                  }
                  if (item.sentByDevice.isNotBlank()) {
                    Text(
                      text = "ডিভাইস: ${item.sentByDevice}",
                      fontSize = 10.sp,
                      color = AdminInkMuted
                    )
                  }

                  HorizontalDivider(color = AdminCardBorderSoft)

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    OutlinedButton(
                      onClick = {
                        if (SimSmsGatewayManager.launchPlaySafeSmsIntent(context, item.phone, item.message)) {
                          onMarkSmsSentViaIntent(item.id)
                        }
                      },
                      border = BorderStroke(1.dp, AdminV2CyanDeep),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                    ) {
                      Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(15.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("১-ক্লিক Send", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
                    }

                    if (item.status == "failed" || item.status == "pending") {
                      Button(
                        onClick = { onRetrySmsItem(item.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                          .weight(1f)
                          .heightIn(min = 48.dp)
                      ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পুনরায় কিউ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }
                    }

                    IconButton(
                      onClick = { onDeleteSmsItem(item.id) },
                      modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEF2F2))
                    ) {
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "SMS মুছুন",
                        tint = AdminV2Rose,
                        modifier = Modifier.size(18.dp)
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
