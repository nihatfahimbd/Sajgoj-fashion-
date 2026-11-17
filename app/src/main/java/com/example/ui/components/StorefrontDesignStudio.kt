package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoreConfig
import com.example.data.model.StoreDesignConfig
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamPriceBorder
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.NavyBlueCardEnd
import com.example.ui.theme.ReelsRedBadge
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex

private data class DesignPreset(
  val id: String,
  val label: String,
  val design: StoreDesignConfig
)

private val designPresets = listOf(
  DesignPreset(
    id = "default_navy_cream",
    label = "✨ ডিফল্ট নেভি + ক্রিম",
    design = StoreDesignConfig()
  ),
  DesignPreset(
    id = "royal_maroon_gold",
    label = "👑 রয়েল মেরুন ও গোল্ড",
    design = StoreDesignConfig(
      useCustomPageBg = true,
      pageBgStart = "#FFFBF5",
      pageBgEnd = "#F8EBD9",
      useCustomBlockBg = true,
      blockBgColor = "#FFFFFF",
      blockBorderColor = "#D4AF37",
      blockTitleBg = "#7A1C2B",
      blockTitleText = "#FFFFFF",
      seeMoreBg = "#7A1C2B",
      seeMoreText = "#FFFFFF",
      cardBodyBg = "#53111C",
      cardBodyBgEnd = "#7A1C2B",
      cardTitleBg = "#D97706",
      cardTitleBgEnd = "#B45309",
      cardPriceBg = "#FFF8F0",
      cardPriceBorder = "#F3D5B5",
      cardRegularPriceColor = "#0F294A",
      cardOfferPriceColor = "#DC2626",
      cardBtnStyle = "pill",
      cardBtnBg = "#EF4444",
      cardBtnBgEnd = "#B91C1C"
    )
  ),
  DesignPreset(
    id = "midnight_dark_luxury",
    label = "🌙 মিডনাইট ডার্ক লাক্সারি",
    design = StoreDesignConfig(
      useCustomPageBg = true,
      pageBgStart = "#0F172A",
      pageBgEnd = "#1E293B",
      useCustomBlockBg = true,
      blockBgColor = "#1E293B",
      blockBorderColor = "#334155",
      blockTitleBg = "#F59E0B",
      blockTitleText = "#0F172A",
      seeMoreBg = "#F59E0B",
      seeMoreText = "#0F172A",
      cardBodyBg = "#0F172A",
      cardBodyBgEnd = "#1E293B",
      cardBorderColor = "#475569",
      cardTitleBg = "#DC2626",
      cardTitleBgEnd = "#991B1B",
      cardPriceBg = "#FFF8F0",
      cardPriceBorder = "#F3D5B5",
      cardRegularPriceColor = "#0F294A",
      cardOfferPriceColor = "#DC2626",
      cardBtnStyle = "rounded",
      cardBtnBg = "#F59E0B",
      cardBtnBgEnd = "#D97706",
      cardBtnText = "#0F172A",
      useCustomHeaderColors = true,
      announcementBg = "#0F172A",
      announcementTextColor = "#FDE68A",
      headerBg = "#1E293B",
      headerTextColor = "#FFFFFF",
      cartBg = "#F59E0B",
      cartTextColor = "#0F172A"
    )
  ),
  DesignPreset(
    id = "emerald_rose",
    label = "💎 এমেরাল্ড ও রোজ",
    design = StoreDesignConfig(
      useCustomPageBg = true,
      pageBgStart = "#ECFDF5",
      pageBgEnd = "#F0FDF4",
      useCustomBlockBg = true,
      blockBgColor = "#FFFFFF",
      blockBorderColor = "#A7F3D0",
      blockTitleBg = "#065F46",
      blockTitleText = "#FFFFFF",
      seeMoreBg = "#065F46",
      seeMoreText = "#FFFFFF",
      cardBodyBg = "#064E3B",
      cardBodyBgEnd = "#065F46",
      cardTitleBg = "#E11D48",
      cardTitleBgEnd = "#BE123C",
      cardPriceBg = "#FFF8F0",
      cardPriceBorder = "#F3D5B5",
      cardRegularPriceColor = "#0F294A",
      cardOfferPriceColor = "#DC2626",
      cardBtnStyle = "pill",
      cardBtnBg = "#10B981",
      cardBtnBgEnd = "#059669"
    )
  )
)

@Composable
fun PaymentMethodsToggleSection(
  config: StoreConfig,
  onSaveConfig: (StoreConfig) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("payment_methods_toggle_section"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, BorderGold)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "💳 পেমেন্ট মেথড কন্ট্রোল (Storefront Footer)",
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        color = RoyalMaroon
      )
      Text(
        text = "এখান থেকে bKash, Nagad, Rocket, COD এবং Visa/Mastercard অন/অফ করলে সাথে সাথে Storefront ফুটারে আপডেট হবে।",
        fontSize = 11.sp,
        color = AntiqueGoldDark,
        lineHeight = 15.sp
      )

      PaymentMethodToggleRow(
        label = "bKash",
        subtitle = "বিকাশ মোবাইল পেমেন্ট ব্যাজ",
        badgeColor = Color(0xFFE2136E),
        checked = config.bkashEnabled,
        testTag = "payment_toggle_bkash",
        onToggle = { enabled -> onSaveConfig(config.copy(bkashEnabled = enabled)) }
      )
      PaymentMethodToggleRow(
        label = "Nagad",
        subtitle = "নগদ মোবাইল পেমেন্ট ব্যাজ",
        badgeColor = Color(0xFFF6921E),
        checked = config.nagadEnabled,
        testTag = "payment_toggle_nagad",
        onToggle = { enabled -> onSaveConfig(config.copy(nagadEnabled = enabled)) }
      )
      PaymentMethodToggleRow(
        label = "Rocket",
        subtitle = "রকেট মোবাইল ব্যাংকিং ব্যাজ",
        badgeColor = Color(0xFF8C3494),
        checked = config.rocketEnabled,
        testTag = "payment_toggle_rocket",
        onToggle = { enabled -> onSaveConfig(config.copy(rocketEnabled = enabled)) }
      )
      PaymentMethodToggleRow(
        label = "Cash on Delivery",
        subtitle = "হাতে পেয়ে মূল্য পরিশোধ (COD)",
        badgeColor = DarkEspresso,
        checked = config.codEnabled,
        testTag = "payment_toggle_cod",
        onToggle = { enabled -> onSaveConfig(config.copy(codEnabled = enabled)) }
      )
      PaymentMethodToggleRow(
        label = "Visa / Mastercard",
        subtitle = "কার্ড পেমেন্ট সাপোর্ট ব্যাজ",
        badgeColor = NavyBlue,
        checked = config.visaEnabled,
        testTag = "payment_toggle_visa",
        onToggle = { enabled -> onSaveConfig(config.copy(visaEnabled = enabled)) }
      )
    }
  }
}

@Composable
private fun PaymentMethodToggleRow(
  label: String,
  subtitle: String,
  badgeColor: Color,
  checked: Boolean,
  testTag: String,
  onToggle: (Boolean) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (checked) CreamSurface else Color(0xFFF8FAFC),
    border = BorderStroke(1.dp, if (checked) BorderGold else Color(0xFFE2E8F0)),
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 52.dp)
      .clickable { onToggle(!checked) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = badgeColor
        ) {
          Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Column {
          Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = DarkEspresso
          )
          Text(
            text = subtitle,
            fontSize = 10.sp,
            color = Color.Gray
          )
        }
      }

      Switch(
        checked = checked,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.White,
          checkedTrackColor = AccentGreen
        ),
        modifier = Modifier.testTag(testTag)
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StorefrontDesignStudioPanel(
  config: StoreConfig,
  onSaveConfig: (StoreConfig) -> Unit,
  modifier: Modifier = Modifier
) {
  var draft by remember(config.design) { mutableStateOf(config.design) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("storefront_design_studio_panel"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, BorderGold)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "🎨 স্টোরফ্রন্ট ডিজাইন ও কালার স্টুডিও",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          Text(
            text = "পেজ ব্যাকগ্রাউন্ড, ব্লক, কার্ড, প্রাইস বক্স, বাটন স্টাইল ও উইশলিস্ট নিজেই সাজান",
            fontSize = 10.5.sp,
            color = AntiqueGoldDark
          )
        }

        OutlinedButton(
          onClick = {
            val reset = StoreDesignConfig()
            draft = reset
            onSaveConfig(config.copy(design = reset))
          },
          border = BorderStroke(1.dp, BorderGold),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier
            .height(34.dp)
            .testTag("reset_design_studio_btn")
        ) {
          Text(text = "↺ রিসেট", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
        }
      }

      // 1. One-Click Presets
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "১. রেডিমেড ডিজাইন প্রিসেট", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          designPresets.forEach { preset ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CreamSurface,
              border = BorderStroke(1.dp, BorderGold),
              modifier = Modifier
                .heightIn(min = 40.dp)
                .clickable {
                  draft = preset.design
                  onSaveConfig(config.copy(design = preset.design))
                }
                .testTag("design_preset_${preset.id}")
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                Text(text = preset.label, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
              }
            }
          }
        }
      }

      // 2. Live Card Preview
      DesignLivePreviewBox(design = draft)

      HorizontalDivider(color = BorderGold)

      // 3. Page & Block Background Section
      Text(text = "২. ওয়েবসাইট পেজ ও ব্লক ডিজাইন", fontSize = 12.sp, fontWeight = FontWeight.Black, color = RoyalMaroon)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "কাস্টম পেজ ব্যাকগ্রাউন্ড চালু করুন", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = DarkEspresso)
        Switch(
          checked = draft.useCustomPageBg,
          onCheckedChange = { draft = draft.copy(useCustomPageBg = it) }
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "পেজ Bg ১",
          value = draft.pageBgStart,
          onValueChange = { draft = draft.copy(pageBgStart = it, useCustomPageBg = true) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "পেজ Bg ২",
          value = draft.pageBgEnd,
          onValueChange = { draft = draft.copy(pageBgEnd = it, useCustomPageBg = true) },
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "কাস্টম প্রোডাক্ট ব্লক ব্যাকগ্রাউন্ড", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = DarkEspresso)
        Switch(
          checked = draft.useCustomBlockBg,
          onCheckedChange = { draft = draft.copy(useCustomBlockBg = it) }
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "ব্লক ব্যাকগ্রাউন্ড",
          value = draft.blockBgColor,
          onValueChange = { draft = draft.copy(blockBgColor = it, useCustomBlockBg = true) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "ব্লক বর্ডার",
          value = draft.blockBorderColor,
          onValueChange = { draft = draft.copy(blockBorderColor = it, useCustomBlockBg = true) },
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "ব্লক টাইটেল পিল",
          value = draft.blockTitleBg,
          onValueChange = { draft = draft.copy(blockTitleBg = it) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "টাইটেল টেক্সট",
          value = draft.blockTitleText,
          onValueChange = { draft = draft.copy(blockTitleText = it) },
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "আরো দেখুন পিল",
          value = draft.seeMoreBg,
          onValueChange = { draft = draft.copy(seeMoreBg = it) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "আরো দেখুন টেক্সট",
          value = draft.seeMoreText,
          onValueChange = { draft = draft.copy(seeMoreText = it) },
          modifier = Modifier.weight(1f)
        )
      }

      OutlinedTextField(
        value = draft.reelsCaptionLabel,
        onValueChange = { draft = draft.copy(reelsCaptionLabel = it) },
        label = { Text("রিলস মাঝের ক্যাপশন টেক্সট", fontSize = 11.sp) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      )

      HorizontalDivider(color = BorderGold)

      // 4. Product Card, Cream Price Box, Button Style & Wishlist
      Text(text = "৩. প্রোডাক্ট কার্ড, ক্রিম প্রাইস বক্স, বাটন ও উইশলিস্ট", fontSize = 12.sp, fontWeight = FontWeight.Black, color = RoyalMaroon)

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "কার্ড বডি Bg",
          value = draft.cardBodyBg,
          onValueChange = { draft = draft.copy(cardBodyBg = it) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "কার্ড টাইটেল পিল",
          value = draft.cardTitleBg,
          onValueChange = { draft = draft.copy(cardTitleBg = it) },
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "প্রাইস বক্স Bg",
          value = draft.cardPriceBg,
          onValueChange = { draft = draft.copy(cardPriceBg = it) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "রেগুলার (ক্রস)",
          value = draft.cardRegularPriceColor,
          onValueChange = { draft = draft.copy(cardRegularPriceColor = it) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "অফার মূল্য (লাল)",
          value = draft.cardOfferPriceColor,
          onValueChange = { draft = draft.copy(cardOfferPriceColor = it) },
          modifier = Modifier.weight(1f)
        )
      }

      Text(text = "অ্যাকশন বাটন স্টাইল:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(
          "pill" to "Pill (গোল)",
          "rounded" to "Rounded",
          "square" to "Square",
          "outline" to "Outline"
        ).forEach { (styleKey, styleLabel) ->
          val selected = draft.cardBtnStyle == styleKey
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (selected) RoyalMaroon else CreamBackground,
            border = BorderStroke(1.dp, if (selected) RoyalMaroon else BorderGold),
            modifier = Modifier
              .heightIn(min = 38.dp)
              .clickable { draft = draft.copy(cardBtnStyle = styleKey) }
              .testTag("btn_style_$styleKey")
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
              Text(
                text = styleLabel,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else DarkEspresso
              )
            }
          }
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "বাটন কালার",
          value = draft.cardBtnBg,
          onValueChange = { draft = draft.copy(cardBtnBg = it) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "বাটন টেক্সট",
          value = draft.cardBtnText,
          onValueChange = { draft = draft.copy(cardBtnText = it) },
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = draft.cardActionLabel,
          onValueChange = { draft = draft.copy(cardActionLabel = it) },
          label = { Text("বাটন লেখা", fontSize = 10.5.sp) },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1.4f)
        )
        OutlinedTextField(
          value = draft.cardWishlistIcon,
          onValueChange = { draft = draft.copy(cardWishlistIcon = it) },
          label = { Text("উইশলিস্ট", fontSize = 10.5.sp) },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(0.8f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "উইশলিস্ট সার্কেল",
          value = draft.cardWishlistCircleBg,
          onValueChange = { draft = draft.copy(cardWishlistCircleBg = it) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "অ্যাক্টিভ উইশলিস্ট",
          value = draft.cardWishlistActiveBg,
          onValueChange = { draft = draft.copy(cardWishlistActiveBg = it) },
          modifier = Modifier.weight(1f)
        )
      }

      HorizontalDivider(color = BorderGold)

      // 5. Header, Announcement & Cart Overrides
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "৪. কাস্টম হেডার, নোটিশ ও কার্ট কালার", fontSize = 12.sp, fontWeight = FontWeight.Black, color = RoyalMaroon)
        Switch(
          checked = draft.useCustomHeaderColors,
          onCheckedChange = { draft = draft.copy(useCustomHeaderColors = it) }
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "নোটিশ বার Bg",
          value = draft.announcementBg,
          onValueChange = { draft = draft.copy(announcementBg = it, useCustomHeaderColors = true) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "হেডার Bg",
          value = draft.headerBg,
          onValueChange = { draft = draft.copy(headerBg = it, useCustomHeaderColors = true) },
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorHexField(
          label = "কার্ট বাটন Bg",
          value = draft.cartBg,
          onValueChange = { draft = draft.copy(cartBg = it, useCustomHeaderColors = true) },
          modifier = Modifier.weight(1f)
        )
        ColorHexField(
          label = "কার্ট টেক্সট",
          value = draft.cartTextColor,
          onValueChange = { draft = draft.copy(cartTextColor = it, useCustomHeaderColors = true) },
          modifier = Modifier.weight(1f)
        )
      }

      Button(
        onClick = { onSaveConfig(config.copy(design = draft)) },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("save_design_studio_btn")
      ) {
        Text(
          text = "💾 ডিজাইন ও কালার সেভ করুন (Storefront আপডেট হবে)",
          fontSize = 12.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}

@Composable
private fun ColorHexField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val previewColor = remember(value) { parseColorHex(value, NavyBlue) }
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, fontSize = 9.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    leadingIcon = {
      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .background(previewColor)
          .border(1.dp, DarkEspresso.copy(alpha = 0.25f), CircleShape)
      )
    },
    singleLine = true,
    shape = RoundedCornerShape(10.dp),
    modifier = modifier
  )
}

@Composable
private fun DesignLivePreviewBox(design: StoreDesignConfig) {
  val pageStart = if (design.useCustomPageBg) parseColorHex(design.pageBgStart, CreamBackground) else CreamBackground
  val pageEnd = if (design.useCustomPageBg) parseColorHex(design.pageBgEnd, CreamSurface) else CreamSurface
  val blockBg = if (design.useCustomBlockBg) parseColorHex(design.blockBgColor, Color.White) else Color.White
  val bodyStart = parseColorHex(design.cardBodyBg, NavyBlue)
  val bodyEnd = parseColorHex(design.cardBodyBgEnd, NavyBlueCardEnd)
  val titleBg = parseColorHex(design.cardTitleBg, Color(0xFFEF4444))
  val titleEnd = parseColorHex(design.cardTitleBgEnd, ReelsRedBadge)
  val titleTxt = parseColorHex(design.cardTitleText, Color.White)
  val priceBg = parseColorHex(design.cardPriceBg, CreamSurface)
  val priceBorder = parseColorHex(design.cardPriceBorder, CreamPriceBorder)
  val regColor = parseColorHex(design.cardRegularPriceColor, NavyBlue)
  val offerColor = parseColorHex(design.cardOfferPriceColor, ReelsRedBadge)
  val btnBg = parseColorHex(design.cardBtnBg, Color(0xFFFB7185))
  val btnEnd = parseColorHex(design.cardBtnBgEnd, Color(0xFFE11D48))
  val btnText = parseColorHex(design.cardBtnText, Color.White)
  val btnShape = when (design.cardBtnStyle) {
    "rounded" -> RoundedCornerShape(10.dp)
    "square" -> RoundedCornerShape(4.dp)
    else -> RoundedCornerShape(50)
  }

  Surface(
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, BorderGold),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .background(Brush.verticalGradient(listOf(pageStart, pageEnd)))
        .padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(50),
          color = parseColorHex(design.blockTitleBg, NavyBlue)
        ) {
          Text(
            text = "✨ লাইভ প্রিভিউ",
            color = parseColorHex(design.blockTitleText, Color.White),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(50),
          color = parseColorHex(design.seeMoreBg, NavyBlue)
        ) {
          Text(
            text = "আরো দেখুন ›",
            color = parseColorHex(design.seeMoreText, Color.White),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = blockBg,
        border = BorderStroke(1.dp, parseColorHex(design.blockBorderColor, BorderGold)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          horizontalArrangement = Arrangement.Center
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, parseColorHex(design.cardBorderColor, Color.White)),
            modifier = Modifier.width(142.dp)
          ) {
            Column(
              modifier = Modifier
                .background(Brush.verticalGradient(listOf(bodyStart, bodyEnd)))
                .padding(7.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(parseColorHex(design.cardWishlistCircleBg, Color.White)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = design.cardWishlistIcon.ifBlank { "❤️" }, fontSize = 10.sp)
                }
              }

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(7.dp))
                  .background(Brush.horizontalGradient(listOf(titleBg, titleEnd)))
                  .padding(horizontal = 6.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "রয়েল জামদানি শাড়ি",
                  color = titleTxt,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1
                )
              }

              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(priceBg)
                  .border(1.dp, priceBorder, RoundedCornerShape(8.dp))
                  .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "৳4,200",
                  color = regColor,
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold,
                  textDecoration = TextDecoration.LineThrough
                )
                Text(
                  text = "৳3,500",
                  color = offerColor,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(btnShape)
                  .then(
                    if (design.cardBtnStyle == "outline") {
                      Modifier
                        .background(Color.Transparent)
                        .border(1.5.dp, btnBg, btnShape)
                    } else {
                      Modifier
                        .background(Brush.horizontalGradient(listOf(btnBg, btnEnd)))
                        .border(1.dp, Color.White.copy(alpha = 0.7f), btnShape)
                    }
                  )
                  .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = design.cardActionLabel.ifBlank { "পন্যটি দেখুন" },
                  color = if (design.cardBtnStyle == "outline") btnBg else btnText,
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }
        }
      }
    }
  }
}
