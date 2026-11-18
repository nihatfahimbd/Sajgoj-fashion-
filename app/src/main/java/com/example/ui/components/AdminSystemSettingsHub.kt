package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.RestorePage
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PixelConfig
import com.example.data.model.StoreConfig
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import kotlinx.coroutines.delay

@Composable
fun AdminSystemSettingsHub(
  config: StoreConfig,
  pixelConfig: PixelConfig,
  productsCount: Int,
  ordersCount: Int,
  onSaveConfig: (StoreConfig) -> Unit,
  onSavePixel: (PixelConfig) -> Unit,
  onResetDefaults: () -> Unit,
  onShowToast: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedSubTab by remember { mutableStateOf("GENERAL") } // GENERAL, INTEGRATIONS, APPEARANCE, BACKUP
  val activePayments = remember(config) {
    listOf(
      config.bkashEnabled,
      config.nagadEnabled,
      config.rocketEnabled,
      config.codEnabled,
      config.visaEnabled
    ).count { it }
  }

  Column(modifier = modifier.fillMaxSize()) {
    // 1. Clean White Dynamic Hero Card
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
      shadowElevation = 4.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("system_settings_hero_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .shadow(3.dp, RoundedCornerShape(14.dp))
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.linearGradient(listOf(AdminV2Cyan, AdminV2CyanDeep))
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.Settings,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "সিস্টেম সেটিংস ও কনফিগারেশন",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "স্টোর প্রোফাইল, পেমেন্ট গেটওয়ে, পিক্সেল ও ডাটা ব্যাকআপ",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium,
              color = AdminInkMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          SettingsTelemetryChip(
            dotColor = AdminV2Emerald,
            label = "স্টোর লাইভ",
            bgColor = Color(0xFFECFDF5),
            borderColor = Color(0xFFA7F3D0),
            textColor = Color(0xFF065F46)
          )
          SettingsTelemetryChip(
            dotColor = AdminV2CyanDeep,
            label = "$activePayments/5 পেমেন্ট চালু",
            bgColor = Color(0xFFF0F9FF),
            borderColor = Color(0xFFBAE6FD),
            textColor = AdminV2CyanDeep
          )
          SettingsTelemetryChip(
            dotColor = if (pixelConfig.isEnabled) AdminV2Emerald else Color(0xFF94A3B8),
            label = if (pixelConfig.isEnabled) "Meta Pixel সক্রিয়" else "Pixel বন্ধ",
            bgColor = if (pixelConfig.isEnabled) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
            borderColor = if (pixelConfig.isEnabled) Color(0xFFA7F3D0) else Color(0xFFE2E8F0),
            textColor = if (pixelConfig.isEnabled) Color(0xFF065F46) else AdminInkMuted
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Clean Segmented Tab Track
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color(0xFFEAF4FB),
      border = BorderStroke(1.dp, Color(0xFFD0E5F4)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(4.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        SettingsSubTabPill(
          label = "স্টোর ও কন্ট্যাক্ট",
          icon = Icons.Outlined.Storefront,
          selected = selectedSubTab == "GENERAL",
          onClick = { selectedSubTab = "GENERAL" },
          modifier = Modifier.testTag("settings_subtab_GENERAL")
        )
        SettingsSubTabPill(
          label = "পেমেন্ট ও পিক্সেল",
          icon = Icons.Outlined.Payments,
          selected = selectedSubTab == "INTEGRATIONS",
          onClick = { selectedSubTab = "INTEGRATIONS" },
          modifier = Modifier.testTag("settings_subtab_INTEGRATIONS")
        )
        SettingsSubTabPill(
          label = "থিম ও ডিজাইন",
          icon = Icons.Outlined.Palette,
          selected = selectedSubTab == "APPEARANCE",
          onClick = { selectedSubTab = "APPEARANCE" },
          modifier = Modifier.testTag("settings_subtab_APPEARANCE")
        )
        SettingsSubTabPill(
          label = "ব্যাকআপ",
          icon = Icons.Outlined.CloudDone,
          selected = selectedSubTab == "BACKUP",
          onClick = { selectedSubTab = "BACKUP" },
          modifier = Modifier.testTag("settings_subtab_BACKUP")
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Animated Sub-Tab Content
    AnimatedContent(
      targetState = selectedSubTab,
      transitionSpec = {
        (fadeIn(animationSpec = tween(180)) + expandVertically(animationSpec = tween(180)))
          .togetherWith(fadeOut(animationSpec = tween(140)))
      },
      label = "system_settings_tab_transition",
      modifier = Modifier.weight(1f)
    ) { tab ->
      when (tab) {
        "GENERAL" -> GeneralStoreSettingsSection(
          config = config,
          onSaveConfig = onSaveConfig
        )

        "INTEGRATIONS" -> PaymentAndPixelSettingsSection(
          config = config,
          pixelConfig = pixelConfig,
          onSaveConfig = onSaveConfig,
          onSavePixel = onSavePixel
        )

        "APPEARANCE" -> {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            StorefrontDesignStudioPanel(
              config = config,
              onSaveConfig = onSaveConfig
            )
            Spacer(modifier = Modifier.height(12.dp))
          }
        }

        "BACKUP" -> SystemBackupAndResetSection(
          productsCount = productsCount,
          ordersCount = ordersCount,
          onReset = onResetDefaults,
          onShowToast = onShowToast
        )
      }
    }
  }
}

@Composable
private fun GeneralStoreSettingsSection(
  config: StoreConfig,
  onSaveConfig: (StoreConfig) -> Unit
) {
  var storeName by remember(config.storeName) { mutableStateOf(config.storeName) }
  var tagline by remember(config.tagline) { mutableStateOf(config.tagline) }
  var announcement by remember(config.announcementText) { mutableStateOf(config.announcementText) }
  var isAnnouncementEnabled by remember(config.isAnnouncementEnabled) { mutableStateOf(config.isAnnouncementEnabled) }
  var phone by remember(config.phone) { mutableStateOf(config.phone) }
  var whatsapp by remember(config.whatsapp) { mutableStateOf(config.whatsapp) }
  var email by remember(config.email) { mutableStateOf(config.email) }
  var address by remember(config.address) { mutableStateOf(config.address) }
  var savedSuccess by remember { mutableStateOf(false) }

  LaunchedEffect(savedSuccess) {
    if (savedSuccess) {
      delay(2500)
      savedSuccess = false
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Card 1: Brand Identity & Announcement
    SettingsSectionCard(
      title = "ব্র্যান্ড ও অ্যানাউন্সমেন্ট",
      subtitle = "স্টোরের নাম, স্লোগান ও টপ নোটিশ বার",
      icon = Icons.Outlined.Store,
      iconTint = AdminV2CyanDeep,
      iconBg = Color(0xFFE0F2FE)
    ) {
      OutlinedTextField(
        value = storeName,
        onValueChange = { storeName = it },
        label = { Text("স্টোরের নাম") },
        leadingIcon = {
          Icon(imageVector = Icons.Outlined.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = adminOutlinedTextFieldColors(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("settings_store_name_input")
      )

      OutlinedTextField(
        value = tagline,
        onValueChange = { tagline = it },
        label = { Text("ট্যাগলাইন / স্লোগান") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = adminOutlinedTextFieldColors(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("settings_tagline_input")
      )

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isAnnouncementEnabled) Color(0xFFF0F9FF) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isAnnouncementEnabled) Color(0xFFBAE6FD) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Outlined.Campaign,
              contentDescription = null,
              tint = if (isAnnouncementEnabled) AdminV2CyanDeep else AdminInkMuted,
              modifier = Modifier.size(18.dp)
            )
            Column {
              Text(
                text = "অ্যানাউন্সমেন্ট বার চালু",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkPrimary
              )
              Text(
                text = "হোম পেজের একদম উপরে নোটিশ দেখাবে",
                fontSize = 10.sp,
                color = AdminInkMuted
              )
            }
          }
          Switch(
            checked = isAnnouncementEnabled,
            onCheckedChange = { isAnnouncementEnabled = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
          )
        }
      }

      AnimatedVisibility(
        visible = isAnnouncementEnabled,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        OutlinedTextField(
          value = announcement,
          onValueChange = { announcement = it },
          label = { Text("অ্যানাউন্সমেন্ট নোটিশ টেক্সট") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // Card 2: Contact Channels & Showroom
    SettingsSectionCard(
      title = "যোগাযোগ ও শোরুম তথ্য",
      subtitle = "কাস্টমার হেল্পলাইন, WhatsApp ও ইনভয়েস ঠিকানা",
      icon = Icons.Outlined.Call,
      iconTint = AdminV2Emerald,
      iconBg = Color(0xFFECFDF5)
    ) {
      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it },
        label = { Text("হটলাইন ফোন নম্বর") },
        leadingIcon = {
          Icon(imageVector = Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = adminOutlinedTextFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = whatsapp,
        onValueChange = { whatsapp = it },
        label = { Text("WhatsApp হেল্পলাইন নম্বর") },
        leadingIcon = {
          Icon(imageVector = Icons.AutoMirrored.Outlined.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = adminOutlinedTextFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("ইমেইল ঠিকানা") },
        leadingIcon = {
          Icon(imageVector = Icons.Outlined.Email, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = adminOutlinedTextFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = address,
        onValueChange = { address = it },
        label = { Text("শপ / শোরুম ঠিকানা") },
        leadingIcon = {
          Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = adminOutlinedTextFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Unified Save CTA
    Button(
      onClick = {
        onSaveConfig(
          config.copy(
            storeName = storeName.trim().ifBlank { config.storeName },
            tagline = tagline.trim().ifBlank { config.tagline },
            announcementText = announcement.trim(),
            isAnnouncementEnabled = isAnnouncementEnabled,
            phone = phone.trim(),
            whatsapp = whatsapp.trim(),
            email = email.trim(),
            address = address.trim()
          )
        )
        savedSuccess = true
      },
      colors = ButtonDefaults.buttonColors(
        containerColor = if (savedSuccess) AdminV2Emerald else AdminV2CyanDeep,
        contentColor = Color.White
      ),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("save_general_store_settings_btn")
    ) {
      if (savedSuccess) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
      }
      Text(
        text = if (savedSuccess) "স্টোর সেটিংস সংরক্ষিত হয়েছে" else "স্টোর সেটিংস সেভ করুন",
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(14.dp))
  }
}

@Composable
private fun PaymentAndPixelSettingsSection(
  config: StoreConfig,
  pixelConfig: PixelConfig,
  onSaveConfig: (StoreConfig) -> Unit,
  onSavePixel: (PixelConfig) -> Unit
) {
  var pixelId by remember(pixelConfig.pixelId) { mutableStateOf(pixelConfig.pixelId) }
  var isPixelEnabled by remember(pixelConfig.isEnabled) { mutableStateOf(pixelConfig.isEnabled) }
  var pixelSaved by remember { mutableStateOf(false) }

  LaunchedEffect(pixelSaved) {
    if (pixelSaved) {
      delay(2500)
      pixelSaved = false
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    PaymentMethodsToggleSection(
      config = config,
      onSaveConfig = onSaveConfig
    )

    SettingsSectionCard(
      title = "Meta / Facebook Pixel ট্র্যাকিং",
      subtitle = "ইভেন্ট কনভার্সন ও এডস ট্র্যাকিং কনফিগারেশন",
      icon = Icons.Outlined.Analytics,
      iconTint = AdminV2CyanDeep,
      iconBg = Color(0xFFE0F2FE),
      trailingBadge = {
        Surface(
          shape = RoundedCornerShape(50),
          color = if (isPixelEnabled) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
          border = BorderStroke(1.dp, if (isPixelEnabled) Color(0xFFA7F3D0) else Color(0xFFE2E8F0))
        ) {
          Text(
            text = if (isPixelEnabled) "Connected" else "Paused",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPixelEnabled) Color(0xFF065F46) else AdminInkMuted,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }
    ) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isPixelEnabled) Color(0xFFF0F9FF) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isPixelEnabled) Color(0xFFBAE6FD) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "পিক্সেল ইভেন্ট ট্র্যাকিং চালু রাখুন",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Switch(
            checked = isPixelEnabled,
            onCheckedChange = { isPixelEnabled = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
          )
        }
      }

      OutlinedTextField(
        value = pixelId,
        onValueChange = { pixelId = it },
        label = { Text("Meta Pixel ID (Numeric)") },
        leadingIcon = {
          Icon(imageVector = Icons.Outlined.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = adminOutlinedTextFieldColors(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("settings_pixel_id_input")
      )

      Button(
        onClick = {
          onSavePixel(pixelConfig.copy(isEnabled = isPixelEnabled, pixelId = pixelId.trim()))
          pixelSaved = true
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (pixelSaved) AdminV2Emerald else AdminV2CyanDeep,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("save_pixel_settings_btn")
      ) {
        if (pixelSaved) {
          Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(17.dp))
          Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
          text = if (pixelSaved) "পিক্সেল সেটিংস সেভ হয়েছে" else "পিক্সেল কনফিগারেশন সেভ করুন",
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))
  }
}

@Composable
private fun SystemBackupAndResetSection(
  productsCount: Int,
  ordersCount: Int,
  onReset: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current
  var copiedRecently by remember { mutableStateOf(false) }
  var confirmResetExpanded by remember { mutableStateOf(false) }

  LaunchedEffect(copiedRecently) {
    if (copiedRecently) {
      delay(2500)
      copiedRecently = false
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Live Snapshot Metric Tiles
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      BackupStatCard(
        icon = Icons.Outlined.Inventory2,
        label = "মোট ক্যাটালগ পণ্য",
        value = "$productsCount টি",
        accentColor = AdminV2CyanDeep,
        modifier = Modifier.weight(1f)
      )
      BackupStatCard(
        icon = Icons.Outlined.ShoppingBag,
        label = "মোট সংরক্ষিত অর্ডার",
        value = "$ordersCount টি",
        accentColor = AdminV2Emerald,
        modifier = Modifier.weight(1f)
      )
    }

    // 2. Clean Export Backup Card
    SettingsSectionCard(
      title = "ক্লিপবোর্ড ডাটা ব্যাকআপ",
      subtitle = "আপনার ক্যাটালগ ও অর্ডারের সামারি JSON আকারে এক্সপোর্ট করুন",
      icon = Icons.Outlined.CloudDone,
      iconTint = AdminV2CyanDeep,
      iconBg = Color(0xFFE0F2FE)
    ) {
      Button(
        onClick = {
          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
          val clip = ClipData.newPlainText(
            "SAJGOJ_BACKUP",
            "{\"app\":\"SAJGOJ_FASHION\",\"products\":$productsCount,\"orders\":$ordersCount,\"status\":\"SUCCESS\"}"
          )
          clipboard.setPrimaryClip(clip)
          copiedRecently = true
          onShowToast("ব্যাকআপ ডেটা সফলভাবে ক্লিপবোর্ডে কপি হয়েছে!")
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (copiedRecently) AdminV2Emerald else AdminV2CyanDeep,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("copy_json_backup_btn")
      ) {
        Icon(
          imageVector = if (copiedRecently) Icons.Default.Check else Icons.Default.ContentCopy,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (copiedRecently) "ক্লিপবোর্ডে কপি হয়েছে" else "JSON ব্যাকআপ কপি করুন",
          fontSize = 12.5.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // 3. Two-Step Danger Zone Card
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = Color(0xFFFFFBFB),
      border = BorderStroke(1.dp, Color(0xFFFECACA)),
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFEE2E2)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.WarningAmber,
              contentDescription = null,
              tint = AccentRed,
              modifier = Modifier.size(20.dp)
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "ফ্যাক্টরি রিসেট (Danger Zone)",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AccentRed
            )
            Text(
              text = "কাস্টম পরিবর্তন মুছে প্রাথমিক ক্যাটালগ ও ডিফল্ট অর্ডারে ফিরে যান।",
              fontSize = 11.sp,
              color = AdminInkSecondary
            )
          }
        }

        AnimatedContent(
          targetState = confirmResetExpanded,
          label = "reset_confirm_toggle"
        ) { confirming ->
          if (!confirming) {
            OutlinedButton(
              onClick = { confirmResetExpanded = true },
              border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.55f)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("trigger_reset_defaults_btn")
            ) {
              Icon(
                imageVector = Icons.Outlined.RestorePage,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(17.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "ডিফল্ট ক্যাটালগ রিস্টোর করুন",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AccentRed
              )
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "আপনি কি নিশ্চিত? লোকাল ও কাস্টম সেটিংস ডিফল্টে ফিরে যাবে।",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = AccentRed
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    confirmResetExpanded = false
                    onReset()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = AccentRed, contentColor = Color.White),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("confirm_reset_defaults_btn")
                ) {
                  Text("হ্যাঁ, রিস্টোর করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                  onClick = { confirmResetExpanded = false },
                  border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.height(46.dp)
                ) {
                  Text("বাতিল", fontSize = 12.sp, color = AdminInkPrimary, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))
  }
}

@Composable
private fun SettingsTelemetryChip(
  dotColor: Color,
  label: String,
  bgColor: Color,
  borderColor: Color,
  textColor: Color
) {
  Surface(
    shape = RoundedCornerShape(50),
    color = bgColor,
    border = BorderStroke(1.dp, borderColor)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(dotColor)
      )
      Text(
        text = label,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun SettingsSubTabPill(
  label: String,
  icon: ImageVector,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg by animateColorAsState(
    targetValue = if (selected) Color.White else Color.Transparent,
    animationSpec = tween(180, easing = FastOutSlowInEasing),
    label = "settings_tab_bg"
  )
  val contentColor by animateColorAsState(
    targetValue = if (selected) AdminV2CyanDeep else AdminInkSecondary,
    label = "settings_tab_ink"
  )
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bg,
    shadowElevation = if (selected) 2.dp else 0.dp,
    border = if (selected) BorderStroke(1.dp, Color(0xFFBAE6FD)) else null,
    modifier = modifier
      .heightIn(min = 40.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (selected) AdminV2CyanDeep else AdminInkMuted,
        modifier = Modifier.size(15.dp)
      )
      Text(
        text = label,
        fontSize = 11.5.sp,
        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
        color = contentColor,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun SettingsSectionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  iconBg: Color,
  trailingBadge: (@Composable () -> Unit)? = null,
  content: @Composable () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color.White,
    border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
    shadowElevation = 3.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(15.dp),
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
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(iconBg),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = iconTint,
              modifier = Modifier.size(19.dp)
            )
          }
          Column {
            Text(
              text = title,
              fontSize = 13.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AdminInkPrimary
            )
            Text(
              text = subtitle,
              fontSize = 10.5.sp,
              color = AdminInkMuted
            )
          }
        }
        trailingBadge?.invoke()
      }

      content()
    }
  }
}

@Composable
private fun BackupStatCard(
  icon: ImageVector,
  label: String,
  value: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
    shadowElevation = 2.dp,
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(13.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(accentColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(18.dp)
        )
      }
      Column {
        Text(
          text = value,
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary
        )
        Text(
          text = label,
          fontSize = 10.5.sp,
          color = AdminInkMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
