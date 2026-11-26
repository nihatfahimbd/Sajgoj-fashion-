package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.HomePageConfig
import com.example.data.model.HomeSectionConfig
import com.example.data.model.StoreConfig
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex

private fun previewOptColor(hex: String, fallback: Color): Color {
  val t = hex.trim()
  return if (t.equals("transparent", ignoreCase = true)) Color.Transparent else parseColorHex(t, fallback)
}

@Composable
fun HomePageLivePreview(
  hp: HomePageConfig,
  storeConfig: StoreConfig,
  homeSections: List<HomeSectionConfig>,
  categories: List<Category>,
  modifier: Modifier = Modifier
) {
  val enabledSections = remember(homeSections) { homeSections.filter { it.enabled } }
  val orderedCats = remember(categories, hp.categoryOrder) {
    orderCategoriesByHomePage(
      categories.filter { it.type != "sub" && it.type != "parent" }.ifEmpty { categories },
      hp.categoryOrder
    ).take(4)
  }

  Surface(
    shape = RoundedCornerShape(22.dp),
    color = Color(0xFFF8FAFC),
    border = BorderStroke(2.dp, Color(0xFF0F172A)),
    shadowElevation = 6.dp,
    modifier = modifier
      .fillMaxWidth()
      .testTag("home_page_live_preview")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Phone Top Notch Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0F172A))
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("📱 LIVE HOME VIEW", fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF7DD3FC))
        Text("Live Sync", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7))
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Header + Login Button Preview
        val loginStart = previewOptColor(hp.loginBtnBg, Color(0xFF0B0F19))
        val loginEnd = previewOptColor(hp.loginBtnBgEnd, loginStart)
        val loginTxt = previewOptColor(hp.loginBtnText, Color.White)
        val loginBrd = previewOptColor(hp.loginBtnBorder, Color.White)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(storeConfig.storeName, fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalMaroon, maxLines = 1)
            Text(storeConfig.tagline, fontSize = 8.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(hp.loginBtnRadiusPx.coerceIn(0, 32).dp))
              .background(Brush.horizontalGradient(listOf(loginStart, loginEnd)))
              .border(1.dp, loginBrd, RoundedCornerShape(hp.loginBtnRadiusPx.coerceIn(0, 32).dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(hp.loginBtnMobileLabel.ifBlank { "লগইন" }, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = loginTxt)
          }
        }

        // Ordered Sections Preview (matches vertical ↑↓ order)
        val previewSections = if (enabledSections.isNotEmpty()) {
          enabledSections
        } else {
          listOf(
            HomeSectionConfig(id = "search_bar", type = "search_bar", title = "সার্চ বার"),
            HomeSectionConfig(id = "top_banner", type = "top_banner", title = "হিরো ব্যানার"),
            HomeSectionConfig(id = "voucher_strip", type = "voucher_strip", title = "ভাউচার"),
            HomeSectionConfig(id = "category_slider", type = "category_slider", title = "ক্যাটাগরি"),
            HomeSectionConfig(id = "new_products", type = "new_products", title = "✨ নতুন প্রোডাক্ট"),
            HomeSectionConfig(id = "footer_block", type = "footer_block", title = "ফুটার")
          )
        }

        var shownProductSample = false
        previewSections.forEach { sec ->
          when (sec.type) {
            "search_bar" -> if (hp.showSearchBar) PreviewSearchStrip(hp)
            "top_banner" -> Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF3E1C1A), Color(0xFF7A1C2B))))
                .padding(horizontal = 10.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              Text("🖼️ ${sec.title}", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            "voucher_strip" -> if (hp.showVoucherStrip) PreviewVoucherCard(hp)
            "category_slider" -> if (hp.showCategorySlider) PreviewCategoryStrip(hp, orderedCats)
            "google_map" -> Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFECFDF5))
                .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text("📍 স্টোর লোকেশন (Google Map)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
            }
            "footer_block" -> PreviewFooter2x2(hp, storeConfig)
            else -> if (!shownProductSample) {
              shownProductSample = true
              val blkR = hp.blockRadiusPx.coerceIn(0, 32).dp
              val blkBW = hp.blockBorderWidthPx.coerceIn(0, 4).dp
              val hdrR = hp.blockHeaderRadiusPx.coerceIn(0, 32).dp
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(blkR))
                  .background(Color.White)
                  .then(if (blkBW > 0.dp) Modifier.border(blkBW, Color(0xFFE2E8F0), RoundedCornerShape(blkR)) else Modifier)
                  .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  val tChip: @Composable () -> Unit = {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(hdrR))
                        .background(NavyBlue)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                      Text("${sec.icon} ${sec.title}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                    }
                  }
                  val mChip: @Composable () -> Unit = {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(hdrR))
                        .background(NavyBlue)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                      Text("আরো দেখুন ›", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                  }
                  if (hp.blockHeaderReversed) { mChip(); tChip() } else { tChip(); mChip() }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                  repeat(3) {
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .height(22.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                    )
                  }
                }
              }
            } else {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.White)
                  .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("${sec.icon} ${sec.title}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155), maxLines = 1)
              }
            }
          }
        }

        // Bottom Nav Preview
        if (hp.showBottomNav) {
          val navBg = previewOptColor(hp.bottomNavBg, Color.White)
          val navBrd = previewOptColor(hp.bottomNavBorderColor, Color(0xFFE5E7EB))
          val actCol = previewOptColor(hp.bottomNavActiveColor, RoyalMaroon)
          val inactCol = previewOptColor(hp.bottomNavInactiveColor, Color(0xFF4B5563))
          val labelsMap = mapOf(
            "home" to hp.navHomeLabel.ifBlank { "হোম" },
            "shopping" to hp.navShoppingLabel.ifBlank { "শপিং" },
            "offers" to hp.navOffersLabel.ifBlank { "অফার" },
            "account" to hp.navAccountLabel.ifBlank { "একাউন্ট" },
            "cart" to hp.navCartLabel.ifBlank { "কার্ট" }
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(hp.bottomNavRadiusPx.coerceIn(0, 24).dp))
              .background(navBg)
              .border(hp.bottomNavBorderWidthPx.coerceIn(0, 4).dp, navBrd, RoundedCornerShape(hp.bottomNavRadiusPx.coerceIn(0, 24).dp))
              .padding(vertical = 5.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            hp.bottomNavOrder.forEachIndexed { idx, key ->
              Text(
                text = if (hp.bottomNavShowLabels) (labelsMap[key] ?: key) else "•",
                fontSize = 8.sp,
                fontWeight = if (idx == 0) FontWeight.Black else FontWeight.SemiBold,
                color = if (idx == 0) actCol else inactCol,
                maxLines = 1
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PreviewSearchStrip(hp: HomePageConfig) {
  val radius = hp.searchRadiusPx.coerceIn(0, 32).dp
  val bw = hp.searchBorderWidthPx.coerceIn(0, 4).dp
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(radius))
      .background(previewOptColor(hp.searchBarContainerBg, Color.Transparent))
      .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    hp.searchBarOrder.forEach { slot ->
      when (slot) {
        "cat_menu" -> if (hp.showCatMenuBtn) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(radius))
              .background(Brush.horizontalGradient(listOf(previewOptColor(hp.searchCatBtnBg, Color(0xFFFCA5A5)), previewOptColor(hp.searchCatBtnBgEnd, Color(0xFFF87171)))))
              .border(bw, previewOptColor(hp.searchCatBtnBorder, Color(0xFFDC2626)), RoundedCornerShape(radius))
              .padding(horizontal = 6.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
              Icon(Icons.Default.Menu, null, tint = previewOptColor(hp.searchCatBtnText, Color.Black), modifier = Modifier.size(10.dp))
              Text(hp.catMenuLabel.ifBlank { "সকল" }, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = previewOptColor(hp.searchCatBtnText, Color.Black), maxLines = 1)
            }
          }
        }
        "home_btn" -> if (hp.showHomeBtn) {
          Box(
            modifier = Modifier
              .size(22.dp)
              .clip(CircleShape)
              .background(Brush.horizontalGradient(listOf(previewOptColor(hp.searchHomeBtnBg, Color(0xFFFCA5A5)), previewOptColor(hp.searchHomeBtnBgEnd, Color(0xFFF87171)))))
              .border(bw, previewOptColor(hp.searchHomeBtnBorder, Color(0xFFDC2626)), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Home, null, tint = previewOptColor(hp.searchHomeBtnText, Color.Black), modifier = Modifier.size(11.dp))
          }
        }
        "search_input" -> Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(radius))
            .background(previewOptColor(hp.searchInputBg, Color.White))
            .border(bw, previewOptColor(hp.searchInputBorder, Color.White), RoundedCornerShape(radius))
            .padding(horizontal = 6.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Icon(Icons.Default.Search, null, tint = Color.Gray, modifier = Modifier.size(10.dp))
          Text(hp.searchPlaceholder.ifBlank { "খুঁজুন..." }, fontSize = 7.5.sp, color = previewOptColor(hp.searchInputText, Color.Black).copy(alpha = 0.6f), modifier = Modifier.weight(1f), maxLines = 1)
          hp.searchToolsOrder.forEach { t ->
            when (t) {
              "voice" -> if (hp.showVoiceBtn) Icon(Icons.Default.Mic, null, tint = previewOptColor(hp.searchToolBtnText, Color.Black), modifier = Modifier.size(10.dp))
              "image" -> if (hp.showImageSearchBtn) Icon(Icons.Default.ImageSearch, null, tint = previewOptColor(hp.searchToolBtnText, Color.Black), modifier = Modifier.size(10.dp))
              "scan" -> if (hp.showScanBtn) Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(radius))
                  .background(Brush.horizontalGradient(listOf(previewOptColor(hp.searchScanBtnBg, Color(0xFFFCA5A5)), previewOptColor(hp.searchScanBtnBgEnd, Color(0xFFF87171)))))
                  .padding(horizontal = 4.dp, vertical = 2.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                  Icon(Icons.Default.QrCodeScanner, null, tint = previewOptColor(hp.searchScanBtnText, Color.Black), modifier = Modifier.size(8.dp))
                  Text(hp.scanBtnLabel.ifBlank { "স্ক্যান" }, fontSize = 6.5.sp, fontWeight = FontWeight.Black, color = previewOptColor(hp.searchScanBtnText, Color.Black))
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
private fun PreviewVoucherCard(hp: HomePageConfig) {
  val r = hp.voucherRadiusPx.coerceIn(0, 32).dp
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(r))
      .background(previewOptColor(hp.voucherInnerBg, Color(0xFFF97316)))
      .border(hp.voucherBorderWidthPx.coerceIn(0, 4).dp, previewOptColor(hp.voucherInnerBorder, Color.White), RoundedCornerShape(r))
      .padding(horizontal = 8.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    hp.voucherSlotsOrder.forEach { slot ->
      when (slot) {
        "v1" -> Column {
          Text(hp.voucher1Amount, fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = previewOptColor(hp.voucherAmountColor, Color.Black))
          Text(hp.voucher1Sub, fontSize = 6.5.sp, color = previewOptColor(hp.voucherSubColor, Color.Black))
        }
        "v2" -> Column {
          Text(hp.voucher2Amount, fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = previewOptColor(hp.voucherAmountColor, Color.Black))
          Text(hp.voucher2Sub, fontSize = 6.5.sp, color = previewOptColor(hp.voucherSubColor, Color.Black))
        }
        "cta" -> if (hp.showCollectAllBtn) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(hp.voucherCtaRadiusPx.coerceIn(0, 24).dp))
              .background(Brush.horizontalGradient(listOf(previewOptColor(hp.voucherCtaBg, Color(0xFF0B0F19)), previewOptColor(hp.voucherCtaBgEnd, Color(0xFF1E293B)))))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text("${hp.voucherCollectLabel} ›", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = previewOptColor(hp.voucherCtaText, Color.White))
          }
        }
      }
    }
  }
}

@Composable
private fun PreviewCategoryStrip(hp: HomePageConfig, cats: List<Category>) {
  val blkR = hp.catSliderBlockRadiusPx.coerceIn(0, 32).dp
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(blkR))
      .background(Brush.horizontalGradient(listOf(previewOptColor(hp.catSliderBlockBg, Color.White), previewOptColor(hp.catSliderBlockBgEnd, Color(0xFFE0F2FE)))))
      .border(hp.catSliderBorderWidthPx.coerceIn(0, 4).dp, previewOptColor(hp.catSliderBlockBorder, Color.White), RoundedCornerShape(blkR))
      .padding(5.dp),
    horizontalArrangement = Arrangement.spacedBy(5.dp)
  ) {
    if (hp.showAllCatTile) {
      PreviewCatTile(hp, hp.allCatTileIcon.ifBlank { "🌟" }, hp.allCatTileLabel.ifBlank { "সকল পণ্য" })
    }
    cats.forEach { cat ->
      PreviewCatTile(hp, cat.icon.ifBlank { "✨" }, cat.name)
    }
  }
}

@Composable
private fun PreviewCatTile(hp: HomePageConfig, icon: String, label: String) {
  val cardR = hp.catCardRadiusPx.coerceIn(0, 24).dp
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .width(46.dp)
      .clip(RoundedCornerShape(cardR))
      .background(Brush.verticalGradient(listOf(previewOptColor(hp.catCardBg, Color(0xFFFF7B7B)), previewOptColor(hp.catCardBgEnd, Color(0xFFFCA5A5)))))
      .border(1.dp, previewOptColor(hp.catCardBorder, Color.White), RoundedCornerShape(cardR))
      .padding(3.dp)
  ) {
    Box(
      modifier = Modifier
        .size(22.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(previewOptColor(hp.catIconBoxBg, Color.White)),
      contentAlignment = Alignment.Center
    ) {
      Text(icon, fontSize = 11.sp)
    }
    Spacer(modifier = Modifier.height(2.dp))
    Box(
      modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(previewOptColor(hp.catLabelBg, Color.White)).padding(horizontal = 2.dp, vertical = 1.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(label, fontSize = 6.5.sp, fontWeight = FontWeight.Bold, color = previewOptColor(hp.catTextColor, Color.Black), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
  }
}

@Composable
private fun PreviewFooter2x2(hp: HomePageConfig, storeConfig: StoreConfig) {
  Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
    if (hp.showFooterMain) {
      val fR = hp.footerRadiusPx.coerceIn(0, 24).dp
      val hCol = previewOptColor(hp.footerHeadingColor, Color.Black)
      val lCol = previewOptColor(hp.footerLinkColor, RoyalMaroon)
      val tCol = previewOptColor(hp.footerTextColor, Color.Black)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(fR))
          .background(Brush.verticalGradient(listOf(previewOptColor(hp.footerBg, Color.White), previewOptColor(hp.footerBgEnd, Color(0xFFBAE6FD)))))
          .border(hp.footerBorderWidthPx.coerceIn(0, 4).dp, previewOptColor(hp.footerBorderColor, Color.White), RoundedCornerShape(fR))
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Column(modifier = Modifier.weight(1f)) {
            Text("INFORMATION", fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = hCol)
            Text("▪ About • Contact", fontSize = 6.5.sp, color = lCol, maxLines = 1)
          }
          Column(modifier = Modifier.weight(1f)) {
            Text("SUPPORT", fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = hCol)
            Text("▪ Tracking • FAQ", fontSize = 6.5.sp, color = lCol, maxLines = 1)
          }
        }
        HorizontalDivider(color = hCol.copy(alpha = 0.14f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Column(modifier = Modifier.weight(1f)) {
            Text("CONSUMER POLICY", fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = hCol)
            Text("▪ Refund • Exchange", fontSize = 6.5.sp, color = lCol, maxLines = 1)
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(storeConfig.storeName, fontSize = 7.5.sp, fontWeight = FontWeight.Black, color = hCol, maxLines = 1)
            Text("📞 ${storeConfig.phone}", fontSize = 6.5.sp, color = tCol, maxLines = 1)
          }
        }
      }
    }

    if (hp.showTrustBadges) {
      val map = mapOf(
        "payment" to "${hp.trust1Icon} ${hp.trust1Text}",
        "delivery" to "${hp.trust2Icon} ${hp.trust2Text}",
        "return" to "${hp.trust3Icon} ${hp.trust3Text}",
        "authentic" to "${hp.trust4Icon} ${hp.trust4Text}"
      )
      val tR = hp.trustCardRadiusPx.coerceIn(0, 24).dp
      hp.trustBadgeOrder.mapNotNull { map[it] }.chunked(2).forEach { row ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          row.forEach { txt ->
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(tR))
                .background(Brush.horizontalGradient(listOf(previewOptColor(hp.trustCardBg, Color(0xFFF87171)), previewOptColor(hp.trustCardBgEnd, Color(0xFFEF4444)))))
                .border(1.dp, previewOptColor(hp.trustCardBorder, Color.White), RoundedCornerShape(tR))
                .padding(horizontal = 5.dp, vertical = 4.dp)
            ) {
              Text(txt, fontSize = 7.sp, fontWeight = FontWeight.Bold, color = previewOptColor(hp.trustCardTextColor, Color.Black), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          }
        }
      }
    }

    if (hp.showCopyrightBar) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(hp.copyrightRadiusPx.coerceIn(0, 24).dp))
          .background(Brush.horizontalGradient(listOf(previewOptColor(hp.copyrightBg, Color(0xFFF87171)), previewOptColor(hp.copyrightBgEnd, Color(0xFFEF4444)))))
          .border(1.dp, previewOptColor(hp.copyrightBorder, Color.White), RoundedCornerShape(hp.copyrightRadiusPx.coerceIn(0, 24).dp))
          .padding(vertical = 4.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          hp.footerCopyrightOverride.ifBlank { "© 2026 ${storeConfig.storeName}. All Rights Reserved." },
          fontSize = 7.sp,
          fontWeight = FontWeight.Bold,
          color = previewOptColor(hp.copyrightTextColor, Color.Black),
          textAlign = TextAlign.Center,
          maxLines = 1
        )
      }
    }
  }
}
