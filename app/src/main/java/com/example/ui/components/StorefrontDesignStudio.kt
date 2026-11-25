package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.WebAsset
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoreConfig
import com.example.data.model.StoreDesignConfig
import com.example.ui.theme.AccentGreen
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
import kotlinx.coroutines.delay

private data class DesignPreset(
  val id: String,
  val label: String,
  val design: StoreDesignConfig
)

private val designPresets = listOf(
  DesignPreset(
    id = "sky_white_gloss",
    label = "গ্লসি স্কাই + হোয়াইট",
    design = StoreDesignConfig(
      useCustomPageBg = true,
      pageBgStart = "#F0F9FF",
      pageBgEnd = "#FFFFFF",
      useCustomBlockBg = true,
      blockBgColor = "#FFFFFF",
      blockBorderColor = "#BAE6FD",
      blockTitleBg = "#0284C7",
      blockTitleText = "#FFFFFF",
      seeMoreBg = "#0EA5E9",
      seeMoreText = "#FFFFFF",
      cardBodyBg = "#0284C7",
      cardBodyBgEnd = "#0EA5E9",
      cardTitleBg = "#2563EB",
      cardTitleBgEnd = "#3B82F6",
      cardPriceBg = "#FFFFFF",
      cardPriceBorder = "#BAE6FD",
      cardRegularPriceColor = "#475569",
      cardOfferPriceColor = "#0284C7",
      cardBtnStyle = "pill",
      cardBtnBg = "#10B981",
      cardBtnBgEnd = "#059669"
    )
  ),
  DesignPreset(
    id = "indigo_cyan_aurora",
    label = "ইন্ডিগো + অরোরা স্কাই",
    design = StoreDesignConfig(
      useCustomPageBg = true,
      pageBgStart = "#EEF2FF",
      pageBgEnd = "#F0F9FF",
      useCustomBlockBg = true,
      blockBgColor = "#FFFFFF",
      blockBorderColor = "#C7D2FE",
      blockTitleBg = "#4F46E5",
      blockTitleText = "#FFFFFF",
      seeMoreBg = "#0284C7",
      seeMoreText = "#FFFFFF",
      cardBodyBg = "#4F46E5",
      cardBodyBgEnd = "#0EA5E9",
      cardBorderColor = "#FFFFFF",
      cardTitleBg = "#2563EB",
      cardTitleBgEnd = "#06B6D4",
      cardPriceBg = "#FFFFFF",
      cardPriceBorder = "#C7D2FE",
      cardRegularPriceColor = "#475569",
      cardOfferPriceColor = "#4F46E5",
      cardBtnStyle = "pill",
      cardBtnBg = "#0EA5E9",
      cardBtnBgEnd = "#0284C7",
      cardBtnText = "#FFFFFF",
      useCustomHeaderColors = true,
      announcementBg = "#2563EB",
      announcementTextColor = "#FFFFFF",
      headerBg = "#FFFFFF",
      headerTextColor = "#0F2430",
      cartBg = "#4F46E5",
      cartTextColor = "#FFFFFF"
    )
  ),
  DesignPreset(
    id = "emerald_rose",
    label = "এমেরাল্ড ও অ্যাকুয়া",
    design = StoreDesignConfig(
      useCustomPageBg = true,
      pageBgStart = "#ECFDF5",
      pageBgEnd = "#F0FDF4",
      useCustomBlockBg = true,
      blockBgColor = "#FFFFFF",
      blockBorderColor = "#A7F3D0",
      blockTitleBg = "#059669",
      blockTitleText = "#FFFFFF",
      seeMoreBg = "#0D9488",
      seeMoreText = "#FFFFFF",
      cardBodyBg = "#059669",
      cardBodyBgEnd = "#10B981",
      cardTitleBg = "#0D9488",
      cardTitleBgEnd = "#0284C7",
      cardPriceBg = "#FFFFFF",
      cardPriceBorder = "#A7F3D0",
      cardRegularPriceColor = "#475569",
      cardOfferPriceColor = "#059669",
      cardBtnStyle = "pill",
      cardBtnBg = "#F43F5E",
      cardBtnBgEnd = "#E11D48"
    )
  ),
  DesignPreset(
    id = "royal_maroon_gold",
    label = "রয়েল রোজ ও গোল্ড",
    design = StoreDesignConfig(
      useCustomPageBg = true,
      pageBgStart = "#FFFBF5",
      pageBgEnd = "#FFF1F2",
      useCustomBlockBg = true,
      blockBgColor = "#FFFFFF",
      blockBorderColor = "#FCD34D",
      blockTitleBg = "#E11D48",
      blockTitleText = "#FFFFFF",
      seeMoreBg = "#D97706",
      seeMoreText = "#FFFFFF",
      cardBodyBg = "#E11D48",
      cardBodyBgEnd = "#F43F5E",
      cardTitleBg = "#D97706",
      cardTitleBgEnd = "#F59E0B",
      cardPriceBg = "#FFFBEB",
      cardPriceBorder = "#FDE68A",
      cardRegularPriceColor = "#475569",
      cardOfferPriceColor = "#E11D48",
      cardBtnStyle = "pill",
      cardBtnBg = "#F59E0B",
      cardBtnBgEnd = "#EA580C"
    )
  ),
  DesignPreset(
    id = "default_navy_cream",
    label = "ডিফল্ট ক্লাসিক ক্রিম",
    design = StoreDesignConfig()
  )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentMethodsToggleSection(
  config: StoreConfig,
  onSaveConfig: (StoreConfig) -> Unit,
  modifier: Modifier = Modifier
) {
  val enabledGateways = listOf(
    config.bkashEnabled,
    config.nagadEnabled,
    config.rocketEnabled,
    config.codEnabled,
    config.visaEnabled
  ).count { it }

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color.White,
    border = BorderStroke(1.2.dp, Color(0xFFA7F3D0)),
    shadowElevation = 4.dp,
    modifier = modifier
      .fillMaxWidth()
      .testTag("payment_methods_toggle_section")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Vibrant Emerald-Teal Header Strip with White Text
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(adminEmeraldTealBrush())
          .padding(horizontal = 14.dp, vertical = 12.dp)
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
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color.White.copy(alpha = 0.22f))
                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(11.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.CreditCard,
                contentDescription = "পেমেন্ট মেথড কন্ট্রোল",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "পেমেন্ট মেথড সেটিংস",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
              Text(
                text = "টগল করলেই স্টোরফ্রন্ট ফুটারে লাইভ আপডেট",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f)
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.24f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
          ) {
            Text(
              text = "$enabledGateways/5 সক্রিয়",
              fontSize = 10.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
      }

      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Quick Presets Row (48.dp min touch targets)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFECFDF5),
            border = BorderStroke(1.dp, Color(0xFF6EE7B7)),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                onSaveConfig(
                  config.copy(
                    bkashEnabled = true,
                    nagadEnabled = true,
                    rocketEnabled = true,
                    visaEnabled = true,
                    codEnabled = true
                  )
                )
              }
              .testTag("payment_enable_all_btn")
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
              Text(
                text = "✓ সব চালু করুন",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF047857)
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                onSaveConfig(
                  config.copy(
                    bkashEnabled = false,
                    nagadEnabled = false,
                    rocketEnabled = false,
                    visaEnabled = false,
                    codEnabled = true
                  )
                )
              }
              .testTag("payment_cod_only_btn")
          ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
              Text(
                text = "শুধু COD রাখুন",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkPrimary
              )
            }
          }
        }

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
          label = "Visa / Mastercard",
          subtitle = "কার্ড পেমেন্ট সাপোর্ট ব্যাজ",
          badgeColor = Color(0xFF1E3A8A),
          checked = config.visaEnabled,
          testTag = "payment_toggle_visa",
          onToggle = { enabled -> onSaveConfig(config.copy(visaEnabled = enabled)) }
        )
        PaymentMethodToggleRow(
          label = "Cash on Delivery",
          subtitle = "হাতে পেয়ে মূল্য পরিশোধ (COD)",
          badgeColor = Color(0xFF0D9488),
          checked = config.codEnabled,
          testTag = "payment_toggle_cod",
          onToggle = { enabled -> onSaveConfig(config.copy(codEnabled = enabled)) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Instant Live Store Footer Preview
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = CreamSurface,
          border = BorderStroke(1.2.dp, BorderGold),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("payment_footer_live_preview")
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "লাইভ স্টোর ফুটার প্রিভিউ (পেমেন্ট মেথড)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RoyalMaroon
              )
              Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFDCFCE7)
              ) {
                Text(
                  text = "লাইভ সিঙ্ক",
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF166534),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            if (enabledGateways == 0) {
              Text(
                text = "⚠️ সব পেমেন্ট মেথড বন্ধ আছে। ফুটারে প্রদর্শনের জন্য অন্তত ১টি চালু করুন।",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFBE123C)
              )
            } else {
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                if (config.bkashEnabled) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFE2136E))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(text = "bKash", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
                if (config.nagadEnabled) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFF6921E))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(text = "Nagad", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
                if (config.rocketEnabled) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFF8C3494))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(text = "Rocket", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
                if (config.codEnabled) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderGold)
                  ) {
                    Text(
                      text = "Cash on Delivery",
                      color = DarkEspresso,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }
                }
                if (config.visaEnabled) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFF1E3A8A))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(text = "Visa / Mastercard", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
    color = if (checked) Color(0xFFF0F9FF) else Color(0xFFFAFBFD),
    border = BorderStroke(1.dp, if (checked) Color(0xFF7DD3FC) else Color(0xFFE2E8F0)),
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 52.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable { onToggle(!checked) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 11.dp, vertical = 7.dp),
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
            color = AdminInkPrimary
          )
          Text(
            text = subtitle,
            fontSize = 10.5.sp,
            color = AdminInkMuted
          )
        }
      }

      Switch(
        checked = checked,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.White,
          checkedTrackColor = AccentGreen,
          uncheckedThumbColor = Color.White,
          uncheckedTrackColor = Color(0xFFCBD5E1)
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
  var openGroup by remember { mutableStateOf("CARD") } // PAGE, CARD, HEADER
  var savedSuccess by remember { mutableStateOf(false) }

  LaunchedEffect(savedSuccess) {
    if (savedSuccess) {
      delay(2500)
      savedSuccess = false
    }
  }

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = Color.White,
    border = BorderStroke(1.2.dp, Color(0xFFBAE6FD)),
    shadowElevation = 5.dp,
    modifier = modifier
      .fillMaxWidth()
      .testTag("storefront_design_studio_panel")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Vibrant Violet-Cobalt-Sky Gradient Studio Banner with White Text
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(adminVioletCyanBrush())
          .padding(15.dp)
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
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.22f))
                .border(1.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.Palette,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(21.dp)
              )
            }
            Column {
              Text(
                text = "থিম ও ডিজাইন স্টুডিও",
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
              Text(
                text = "প্রিসেট বা কাস্টম কালারে স্টোরফ্রন্ট সাজান",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.90f)
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(11.dp),
            color = Color.White.copy(alpha = 0.22f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f)),
            modifier = Modifier
              .heightIn(min = 40.dp)
              .clip(RoundedCornerShape(11.dp))
              .clickable {
                val reset = StoreDesignConfig()
                draft = reset
                onSaveConfig(config.copy(design = reset))
              }
              .testTag("reset_design_studio_btn")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "রিসেট",
                tint = Color.White,
                modifier = Modifier.size(15.dp)
              )
              Text(text = "রিসেট", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
          }
        }
      }

      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // 1. One-Click Bright Theme Presets
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "রেডিমেড থিম প্রিসেট",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AdminInkPrimary
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            designPresets.forEach { preset ->
              val pPrimary = parseColorHex(preset.design.cardBodyBg, AdminV2CyanDeep)
              val pAccent = parseColorHex(preset.design.cardBtnBg, AdminV2Emerald)
              val isActivePreset = draft == preset.design
              val chipShape = RoundedCornerShape(12.dp)
              Box(
                modifier = Modifier
                  .heightIn(min = 44.dp)
                  .then(
                    if (isActivePreset) {
                      Modifier.shadow(4.dp, chipShape, spotColor = AdminV2CyanDeep.copy(alpha = 0.24f))
                    } else {
                      Modifier
                    }
                  )
                  .clip(chipShape)
                  .then(
                    if (isActivePreset) {
                      Modifier
                        .background(adminRoyalGlossBrush())
                        .border(1.dp, Color(0xFF7DD3FC), chipShape)
                    } else {
                      Modifier
                        .background(Color(0xFFF5FAFF))
                        .border(1.dp, Color(0xFFBAE6FD), chipShape)
                    }
                  )
                  .clickable {
                    draft = preset.design
                    onSaveConfig(config.copy(design = preset.design))
                  }
                  .testTag("design_preset_${preset.id}")
                  .padding(horizontal = 11.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                  Row(horizontalArrangement = Arrangement.spacedBy((-5).dp)) {
                    Box(
                      modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(pPrimary)
                        .border(1.2.dp, Color.White, CircleShape)
                    )
                    Box(
                      modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(pAccent)
                        .border(1.2.dp, Color.White, CircleShape)
                    )
                  }
                  Text(
                    text = preset.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActivePreset) Color.White else AdminInkPrimary
                  )
                }
              }
            }
          }
        }

        // 2. Live Card Preview
        DesignLivePreviewBox(design = draft)

        // 3. Clean Collapsible Accordion Sections
        DesignAccordionGroup(
          title = "পেজ ও সেকশন ব্লক কালার",
          subtitle = "ব্যাকগ্রাউন্ড, বর্ডার ও রিলস লেবেল",
          icon = Icons.Outlined.ColorLens,
          iconBrush = adminRoyalGlossBrush(),
          expanded = openGroup == "PAGE",
          onToggle = { openGroup = if (openGroup == "PAGE") "" else "PAGE" }
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "কাস্টম পেজ ব্যাকগ্রাউন্ড", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = AdminInkPrimary)
            Switch(
              checked = draft.useCustomPageBg,
              onCheckedChange = { draft = draft.copy(useCustomPageBg = it) },
              colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
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
            Text(text = "কাস্টম ব্লক ব্যাকগ্রাউন্ড", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = AdminInkPrimary)
            Switch(
              checked = draft.useCustomBlockBg,
              onCheckedChange = { draft = draft.copy(useCustomBlockBg = it) },
              colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
            )
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ColorHexField(
              label = "ব্লক Bg",
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
            label = { Text("রিলস ক্যাপশন টেক্সট", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )
        }

        DesignAccordionGroup(
          title = "প্রোডাক্ট কার্ড, প্রাইস ও বাটন",
          subtitle = "কার্ড কালার, বাটন শেপ ও উইশলিস্ট",
          icon = Icons.Outlined.Style,
          iconBrush = adminVioletCyanBrush(),
          expanded = openGroup == "CARD",
          onToggle = { openGroup = if (openGroup == "CARD") "" else "CARD" }
        ) {
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
              label = "প্রাইস বক্স",
              value = draft.cardPriceBg,
              onValueChange = { draft = draft.copy(cardPriceBg = it) },
              modifier = Modifier.weight(1f)
            )
            ColorHexField(
              label = "রেগুলার মূল্য",
              value = draft.cardRegularPriceColor,
              onValueChange = { draft = draft.copy(cardRegularPriceColor = it) },
              modifier = Modifier.weight(1f)
            )
            ColorHexField(
              label = "অফার মূল্য",
              value = draft.cardOfferPriceColor,
              onValueChange = { draft = draft.copy(cardOfferPriceColor = it) },
              modifier = Modifier.weight(1f)
            )
          }

          Text(text = "প্রাইস ডিসকাউন্ট ডিসপ্লে:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              "auto" to "প্রোডাক্ট অনুযায়ী",
              "percent" to "% ডিসকাউন্ট (৳490 -45%)",
              "crossed" to "রেগুলার + অফার (৳450 ৳400)"
            ).forEach { (styleKey, styleLabel) ->
              val selected = draft.cardPriceDiscountStyle == styleKey
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (selected) AdminV2CyanDeep else Color.White,
                border = BorderStroke(1.dp, if (selected) AdminV2CyanDeep else Color(0xFFBAE6FD)),
                modifier = Modifier
                  .heightIn(min = 38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { draft = draft.copy(cardPriceDiscountStyle = styleKey) }
                  .testTag("price_discount_style_$styleKey")
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                  Text(
                    text = styleLabel,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else AdminInkPrimary
                  )
                }
              }
            }
          }

          Text(text = "বাটন স্টাইল:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              "pill" to "Pill",
              "rounded" to "Rounded",
              "square" to "Square",
              "outline" to "Outline"
            ).forEach { (styleKey, styleLabel) ->
              val selected = draft.cardBtnStyle == styleKey
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (selected) AdminV2CyanDeep else Color.White,
                border = BorderStroke(1.dp, if (selected) AdminV2CyanDeep else Color(0xFFBAE6FD)),
                modifier = Modifier
                  .heightIn(min = 38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { draft = draft.copy(cardBtnStyle = styleKey) }
                  .testTag("btn_style_$styleKey")
              ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                  Text(
                    text = styleLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else AdminInkPrimary
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
              colors = adminOutlinedTextFieldColors(),
              modifier = Modifier.weight(1.4f)
            )
            OutlinedTextField(
              value = draft.cardWishlistIcon,
              onValueChange = { draft = draft.copy(cardWishlistIcon = it) },
              label = { Text("উইশলিস্ট", fontSize = 10.5.sp) },
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              colors = adminOutlinedTextFieldColors(),
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
        }

        DesignAccordionGroup(
          title = "হেডার, নোটিশ ও কার্ট কালার",
          subtitle = "টপ বার, অ্যানাউন্সমেন্ট ও কার্ট বাটন",
          icon = Icons.Outlined.WebAsset,
          iconBrush = adminEmeraldTealBrush(),
          expanded = openGroup == "HEADER",
          onToggle = { openGroup = if (openGroup == "HEADER") "" else "HEADER" }
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "কাস্টম হেডার কালার চালু করুন", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = AdminInkPrimary)
            Switch(
              checked = draft.useCustomHeaderColors,
              onCheckedChange = { draft = draft.copy(useCustomHeaderColors = it) },
              colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
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
        }

        GlossyGradientButton(
          text = if (savedSuccess) "থিম ও ডিজাইন সেভ হয়েছে" else "ডিজাইন ও কালার সেভ করুন",
          icon = if (savedSuccess) Icons.Default.Check else Icons.Outlined.Palette,
          brush = if (savedSuccess) adminEmeraldTealBrush() else adminVioletCyanBrush(),
          onClick = {
            onSaveConfig(config.copy(design = draft))
            savedSuccess = true
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("save_design_studio_btn")
        )
      }
    }
  }
}

@Composable
private fun DesignAccordionGroup(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconBrush: Brush,
  expanded: Boolean,
  onToggle: () -> Unit,
  content: @Composable () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (expanded) Color(0xFFF4FAFF) else Color.White,
    border = BorderStroke(1.2.dp, if (expanded) Color(0xFF7DD3FC) else Color(0xFFD6E8F5)),
    shadowElevation = if (expanded) 3.dp else 1.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 54.dp)
          .clickable(onClick = onToggle)
          .padding(horizontal = 12.dp, vertical = 10.dp),
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
              .size(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(iconBrush),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
          Column {
            Text(
              text = title,
              fontSize = 12.5.sp,
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
        Icon(
          imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
          contentDescription = null,
          tint = AdminV2CyanDeep,
          modifier = Modifier.size(20.dp)
        )
      }

      AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          HorizontalDivider(color = Color(0xFFD6E8F5))
          content()
        }
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
  val previewColor = remember(value) { parseColorHex(value, AdminV2CyanDeep) }
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
          .border(1.2.dp, Color(0xFFBAE6FD), CircleShape)
      )
    },
    singleLine = true,
    shape = RoundedCornerShape(10.dp),
    colors = adminOutlinedTextFieldColors(),
    modifier = modifier
  )
}

@Composable
private fun DesignLivePreviewBox(design: StoreDesignConfig) {
  val pageStart = if (design.useCustomPageBg) parseColorHex(design.pageBgStart, CreamBackground) else Color(0xFFF0F9FF)
  val pageEnd = if (design.useCustomPageBg) parseColorHex(design.pageBgEnd, CreamSurface) else Color.White
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
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.2.dp, Color(0xFF7DD3FC)),
    shadowElevation = 2.dp,
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
          color = parseColorHex(design.blockTitleBg, AdminV2CyanDeep)
        ) {
          Text(
            text = "লাইভ প্রিভিউ",
            color = parseColorHex(design.blockTitleText, Color.White),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(50),
          color = parseColorHex(design.seeMoreBg, AdminV2Cyan)
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
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            modifier = Modifier.width(148.dp)
          ) {
            Column(
              modifier = Modifier
                .background(Color.White)
                .padding(8.dp),
              verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(parseColorHex(design.cardWishlistCircleBg, Color.White))
                    .border(0.8.dp, Color(0xFFE5E7EB), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = design.cardWishlistIcon.ifBlank { "❤️" }, fontSize = 10.sp)
                }
              }

              Text(
                text = "রয়েল জামদানি শাড়ি",
                color = Color(0xFF1F2937),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              CleanCardPriceRow(
                sellingPrice = 3500,
                regularPrice = 4200,
                isPercentDiscount = design.cardPriceDiscountStyle == "percent",
                discountPercent = 17,
                design = design
              )
            }
          }
        }
      }
    }
  }
}
