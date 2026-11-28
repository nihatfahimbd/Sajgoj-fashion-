package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import com.example.data.model.Category
import com.example.data.model.FooterPageContentOverride
import com.example.data.model.HomePageConfig
import com.example.data.model.HomeSectionConfig
import com.example.data.model.StoreConfig
import com.example.ui.screens.FOOTER_HUB_TOPICS
import com.example.ui.screens.defaultFooterPageContent
import com.example.ui.screens.resolvedFooterPageContent
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex

private enum class HomeStudioGroup(val label: String) {
  ORDERING("সাজানো (↑↓←→)"),
  LOGIN_SEARCH("লগইন ও সার্চ"),
  VOUCHER("ভাউচার কার্ড"),
  CATEGORY_BLOCK("ক্যাটাগরি ও ব্লক"),
  FOOTER_TRUST("ফুটার ও ট্রাস্ট"),
  FOOTER_PAGES("ফুটার পেজ ও কনটেন্ট"),
  BOTTOM_NAV("বটম মেনু")
}

private fun <T> moveListItem(list: List<T>, idx: Int, delta: Int): List<T> {
  val target = idx + delta
  if (idx !in list.indices || target !in list.indices) return list
  return list.toMutableList().apply {
    val item = removeAt(idx)
    add(target, item)
  }
}

@Composable
fun HomePageDeveloperStudio(
  hp: HomePageConfig,
  homeSections: List<HomeSectionConfig>,
  categories: List<Category>,
  onUpdateHomePage: (HomePageConfig) -> Unit,
  onToggleSection: (String) -> Unit,
  onMoveSection: (String, Int) -> Unit,
  onResetHomePage: () -> Unit = { onUpdateHomePage(HomePageConfig()) },
  storeConfig: StoreConfig = StoreConfig(),
  modifier: Modifier = Modifier
) {
  var activeGroup by remember { mutableStateOf(HomeStudioGroup.ORDERING) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("home_page_developer_studio"),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text("হোম পেজ ডেভেলপার স্টুডিও", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
        Text("সব কন্টেন্ট, কালার, বর্ডার, রেডিয়াস ও ৪-দিকে মুভিং কন্ট্রোল (↑↓←→)", fontSize = 10.5.sp, color = AdminInkMuted)
      }
      OutlinedButton(
        onClick = onResetHomePage,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFEF4444)),
        modifier = Modifier.heightIn(min = 40.dp).testTag("home_studio_reset_btn")
      ) {
        Text("রিসেট", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
      }
    }

    // Group Selector Pills
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      HomeStudioGroup.entries.forEach { group ->
        val sel = group == activeGroup
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (sel) AdminV2CyanDeep else Color(0xFFF8FAFC),
          border = BorderStroke(1.dp, if (sel) AdminV2CyanDeep else Color(0xFFCBD5E1)),
          modifier = Modifier
            .heightIn(min = 42.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { activeGroup = group }
            .testTag("home_studio_group_${group.name.lowercase()}")
        ) {
          Text(
            text = group.label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (sel) Color.White else AdminInkPrimary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
          )
        }
      }
    }

    HorizontalDivider(color = Color(0xFFE2E8F0))

    when (activeGroup) {
      HomeStudioGroup.ORDERING -> OrderingStudioControls(
        hp = hp,
        homeSections = homeSections,
        categories = categories,
        onUpdate = onUpdateHomePage,
        onToggleSection = onToggleSection,
        onMoveSection = onMoveSection
      )
      HomeStudioGroup.LOGIN_SEARCH -> LoginAndSearchControls(hp = hp, onUpdate = onUpdateHomePage)
      HomeStudioGroup.VOUCHER -> VoucherStudioControls(hp = hp, onUpdate = onUpdateHomePage)
      HomeStudioGroup.CATEGORY_BLOCK -> CategoryAndBlockControls(hp = hp, onUpdate = onUpdateHomePage)
      HomeStudioGroup.FOOTER_TRUST -> FooterAndTrustControls(hp = hp, onUpdate = onUpdateHomePage)
      HomeStudioGroup.FOOTER_PAGES -> FooterPagesEditorControls(hp = hp, storeConfig = storeConfig, onUpdate = onUpdateHomePage)
      HomeStudioGroup.BOTTOM_NAV -> BottomNavStudioControls(hp = hp, onUpdate = onUpdateHomePage)
    }
  }
}

@Composable
private fun OrderingStudioControls(
  hp: HomePageConfig,
  homeSections: List<HomeSectionConfig>,
  categories: List<Category>,
  onUpdate: (HomePageConfig) -> Unit,
  onToggleSection: (String) -> Unit,
  onMoveSection: (String, Int) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text("১. ভার্টিক্যাল সেকশন অর্ডার (উপরে ↑ / নিচে ↓):", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
    homeSections.forEachIndexed { idx, sec ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(if (sec.enabled) Color(0xFFF8FAFC) else Color(0xFFF1F5F9))
          .border(1.dp, if (sec.enabled) Color(0xFFBAE6FD) else Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "${idx + 1}. ${sec.icon} ${sec.title}",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Bold,
          color = if (sec.enabled) AdminInkPrimary else AdminInkMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { onMoveSection(sec.id, -1) }, enabled = idx > 0, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.ArrowUpward, "উপরে নিন", tint = if (idx > 0) AdminV2CyanDeep else Color(0xFFCBD5E1), modifier = Modifier.size(16.dp))
          }
          IconButton(onClick = { onMoveSection(sec.id, 1) }, enabled = idx < homeSections.lastIndex, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.ArrowDownward, "নিচে নিন", tint = if (idx < homeSections.lastIndex) AdminV2CyanDeep else Color(0xFFCBD5E1), modifier = Modifier.size(16.dp))
          }
          IconButton(onClick = { onToggleSection(sec.id) }, modifier = Modifier.size(40.dp)) {
            Icon(
              imageVector = if (sec.enabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = "দৃশ্যমানতা",
              tint = if (sec.enabled) Color(0xFF059669) else Color(0xFFDC2626),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    HorizontalDivider(color = Color(0xFFE2E8F0))
    Text("২. ডানে/বামে হরিজন্টাল আইটেম মুভিং (← / →):", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)

    HorizontalReorderChips(
      title = "সার্চ বার এলিমেন্ট (ক্যাটাগরী / হোম / সার্চ বক্স)",
      items = hp.searchBarOrder,
      labelMap = mapOf("cat_menu" to "☰ ক্যাটাগরী", "home_btn" to "🏠 হোম", "search_input" to "🔍 সার্চ বক্স"),
      onReorder = { onUpdate(hp.copy(searchBarOrder = it)) }
    )
    HorizontalReorderChips(
      title = "সার্চ টুলস (ভয়েস / ইমেজ / স্ক্যান)",
      items = hp.searchToolsOrder,
      labelMap = mapOf("voice" to "🎙️ ভয়েস", "image" to "🖼️ ছবি", "scan" to "📷 স্ক্যান"),
      onReorder = { onUpdate(hp.copy(searchToolsOrder = it)) }
    )
    HorizontalReorderChips(
      title = "ভাউচার স্লট (ভাউচার ১ / ভাউচার ২ / বাটন)",
      items = hp.voucherSlotsOrder,
      labelMap = mapOf("v1" to hp.voucher1Amount, "v2" to hp.voucher2Amount, "cta" to hp.voucherCollectLabel),
      onReorder = { onUpdate(hp.copy(voucherSlotsOrder = it)) }
    )
    val baseCats = categories.filter { it.type != "sub" && it.type != "parent" }.ifEmpty { categories }
    val catKeys = orderCategoriesByHomePage(baseCats, hp.categoryOrder).map { it.id.ifBlank { it.name } }
    val catLabels = baseCats.associate { (it.id.ifBlank { it.name }) to "${it.icon} ${it.name}" }
    if (catKeys.isNotEmpty()) {
      HorizontalReorderChips(
        title = "ক্যাটাগরি কার্ড অর্ডার (← / →)",
        items = catKeys,
        labelMap = catLabels,
        onReorder = { onUpdate(hp.copy(categoryOrder = it)) }
      )
    }
    HorizontalReorderChips(
      title = "৪টি ট্রাস্ট ব্যাজ কার্ড (← / →)",
      items = hp.trustBadgeOrder,
      labelMap = mapOf(
        "payment" to "${hp.trust1Icon} পেমেন্ট",
        "delivery" to "${hp.trust2Icon} ডেলিভারি",
        "return" to "${hp.trust3Icon} রিটার্ন",
        "authentic" to "${hp.trust4Icon} আসল পণ্য"
      ),
      onReorder = { onUpdate(hp.copy(trustBadgeOrder = it)) }
    )
    HorizontalReorderChips(
      title = "বটম নেভিগেশন ৫ ট্যাব (← / →)",
      items = hp.bottomNavOrder,
      labelMap = mapOf(
        "home" to hp.navHomeLabel,
        "shopping" to hp.navShoppingLabel,
        "offers" to hp.navOffersLabel,
        "account" to hp.navAccountLabel,
        "cart" to hp.navCartLabel
      ),
      onReorder = { onUpdate(hp.copy(bottomNavOrder = it)) }
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HorizontalReorderChips(
  title: String,
  items: List<String>,
  labelMap: Map<String, String>,
  onReorder: (List<String>) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFFF8FAFC))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
      .padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminInkSecondary)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      items.forEachIndexed { idx, key ->
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color.White,
          border = BorderStroke(1.dp, Color(0xFFBAE6FD))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            IconButton(
              onClick = { onReorder(moveListItem(items, idx, -1)) },
              enabled = idx > 0,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "বামে নিন", tint = if (idx > 0) AdminV2CyanDeep else Color(0xFFCBD5E1), modifier = Modifier.size(16.dp))
            }
            Text(
              text = labelMap[key] ?: key,
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Bold,
              color = AdminInkPrimary,
              modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(
              onClick = { onReorder(moveListItem(items, idx, 1)) },
              enabled = idx < items.lastIndex,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "ডানে নিন", tint = if (idx < items.lastIndex) AdminV2CyanDeep else Color(0xFFCBD5E1), modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LoginAndSearchControls(hp: HomePageConfig, onUpdate: (HomePageConfig) -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PageRenameInputRow("Login Button Label (Mobile)", hp.loginBtnMobileLabel) { onUpdate(hp.copy(loginBtnMobileLabel = it)) }
    PageRenameInputRow("Login Button Label (Desktop)", hp.loginBtnLabel) { onUpdate(hp.copy(loginBtnLabel = it)) }
    PageColorHexPickerRow("Login বাটন ব্যাকগ্রাউন্ড", hp.loginBtnBg, "hp_login_bg") { onUpdate(hp.copy(loginBtnBg = it)) }
    PageColorHexPickerRow("Login বাটন গ্রেডিয়েন্ট শেষ", hp.loginBtnBgEnd, "hp_login_bg_end") { onUpdate(hp.copy(loginBtnBgEnd = it)) }
    PageColorHexPickerRow("Login বাটন টেক্সট কালার", hp.loginBtnText, "hp_login_txt") { onUpdate(hp.copy(loginBtnText = it)) }
    PageColorHexPickerRow("Login বাটন বর্ডার কালার", hp.loginBtnBorder, "hp_login_brd") { onUpdate(hp.copy(loginBtnBorder = it)) }
    PageIntSliderRow("Login বাটন Radius", hp.loginBtnRadiusPx, 0..32) { onUpdate(hp.copy(loginBtnRadiusPx = it)) }

    HorizontalDivider(color = Color(0xFFE2E8F0))
    PageConfigSwitchRow("সার্চ স্ট্রিপ দেখান", "হোম পেজে সার্চ ও ক্যাটাগরী স্ট্রিপ চালু রাখুন", hp.showSearchBar, { onUpdate(hp.copy(showSearchBar = it)) }, "hp_show_search")
    PageConfigSwitchRow("সকল ক্যাটাগরী বাটন", "বামের ক্যাটাগরী মেনু বাটন দেখান", hp.showCatMenuBtn, { onUpdate(hp.copy(showCatMenuBtn = it)) }, "hp_show_cat_btn")
    PageConfigSwitchRow("হোম বাটন (🏠)", "রিসেট হোম বাটন দেখান", hp.showHomeBtn, { onUpdate(hp.copy(showHomeBtn = it)) }, "hp_show_home_btn")
    PageConfigSwitchRow("ভয়েস সার্চ বাটন (🎙️)", "মাইক্রোফোন সার্চ আইকন দেখান", hp.showVoiceBtn, { onUpdate(hp.copy(showVoiceBtn = it)) }, "hp_show_voice")
    PageConfigSwitchRow("ইমেজ সার্চ বাটন (🖼️)", "ছবি সার্চ আইকন দেখান", hp.showImageSearchBtn, { onUpdate(hp.copy(showImageSearchBtn = it)) }, "hp_show_img")
    PageConfigSwitchRow("স্ক্যান বাটন (📷)", "বারকোড/QR স্ক্যান বাটন দেখান", hp.showScanBtn, { onUpdate(hp.copy(showScanBtn = it)) }, "hp_show_scan")

    PageRenameInputRow("ক্যাটাগরী বাটন টেক্সট", hp.catMenuLabel) { onUpdate(hp.copy(catMenuLabel = it)) }
    PageRenameInputRow("সার্চ বক্স Placeholder", hp.searchPlaceholder) { onUpdate(hp.copy(searchPlaceholder = it)) }
    PageRenameInputRow("স্ক্যান বাটন টেক্সট", hp.scanBtnLabel) { onUpdate(hp.copy(scanBtnLabel = it)) }
    PageColorHexPickerRow("সার্চ বার কন্টেইনার ব্যাকগ্রাউন্ড", hp.searchBarContainerBg, "hp_srch_cont_bg") { onUpdate(hp.copy(searchBarContainerBg = it)) }
    PageColorHexPickerRow("সার্চ বার কন্টেইনার বর্ডার", hp.searchBarContainerBorder, "hp_srch_cont_brd") { onUpdate(hp.copy(searchBarContainerBorder = it)) }
    PageColorHexPickerRow("ক্যাটাগরী বাটন ব্যাকগ্রাউন্ড", hp.searchCatBtnBg, "hp_srch_cat_bg") { onUpdate(hp.copy(searchCatBtnBg = it)) }
    PageColorHexPickerRow("ক্যাটাগরী বাটন টেক্সট", hp.searchCatBtnText, "hp_srch_cat_txt") { onUpdate(hp.copy(searchCatBtnText = it)) }
    PageColorHexPickerRow("ক্যাটাগরী বাটন বর্ডার", hp.searchCatBtnBorder, "hp_srch_cat_brd") { onUpdate(hp.copy(searchCatBtnBorder = it)) }
    PageColorHexPickerRow("হোম বাটন ব্যাকগ্রাউন্ড", hp.searchHomeBtnBg, "hp_srch_home_bg") { onUpdate(hp.copy(searchHomeBtnBg = it)) }
    PageColorHexPickerRow("হোম বাটন আইকন/বর্ডার", hp.searchHomeBtnText, "hp_srch_home_txt") { onUpdate(hp.copy(searchHomeBtnText = it)) }
    PageColorHexPickerRow("সার্চ ইনপুট ব্যাকগ্রাউন্ড", hp.searchInputBg, "hp_srch_in_bg") { onUpdate(hp.copy(searchInputBg = it)) }
    PageColorHexPickerRow("সার্চ ইনপুট টেক্সট", hp.searchInputText, "hp_srch_in_txt") { onUpdate(hp.copy(searchInputText = it)) }
    PageColorHexPickerRow("সার্চ ইনপুট বর্ডার", hp.searchInputBorder, "hp_srch_in_brd") { onUpdate(hp.copy(searchInputBorder = it)) }
    PageColorHexPickerRow("স্ক্যান বাটন ব্যাকগ্রাউন্ড", hp.searchScanBtnBg, "hp_srch_scan_bg") { onUpdate(hp.copy(searchScanBtnBg = it)) }
    PageColorHexPickerRow("স্ক্যান বাটন টেক্সট", hp.searchScanBtnText, "hp_srch_scan_txt") { onUpdate(hp.copy(searchScanBtnText = it)) }
    PageIntSliderRow("Search Strip Radius", hp.searchRadiusPx, 0..32) { onUpdate(hp.copy(searchRadiusPx = it)) }
    PageIntSliderRow("Search Border Width", hp.searchBorderWidthPx, 0..4) { onUpdate(hp.copy(searchBorderWidthPx = it)) }
  }
}

@Composable
private fun VoucherStudioControls(hp: HomePageConfig, onUpdate: (HomePageConfig) -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PageConfigSwitchRow("ভাউচার কার্ড দেখান", "হোম পেজে Claim Vouchers স্ট্রিপ দেখান", hp.showVoucherStrip, { onUpdate(hp.copy(showVoucherStrip = it)) }, "hp_show_voucher")
    PageConfigSwitchRow("অফার ট্যাগ ব্যাজ", "11.11 / SAJGOJ ব্যাজ দেখান", hp.showVoucherBadge, { onUpdate(hp.copy(showVoucherBadge = it)) }, "hp_show_vbadge")
    PageConfigSwitchRow("More Voucher লিংক", "ডান পাশে More Voucher বাটন দেখান", hp.showMoreVoucherBtn, { onUpdate(hp.copy(showMoreVoucherBtn = it)) }, "hp_show_vmore")
    PageConfigSwitchRow("Collect All বাটন", "ভাউচারের Collect All বাটন দেখান", hp.showCollectAllBtn, { onUpdate(hp.copy(showCollectAllBtn = it)) }, "hp_show_vcta")

    PageRenameInputRow("শিরোনাম", hp.voucherTitle) { onUpdate(hp.copy(voucherTitle = it)) }
    PageRenameInputRow("ট্যাগ ব্যাজ টেক্সট", hp.voucherBadgeText) { onUpdate(hp.copy(voucherBadgeText = it)) }
    PageRenameInputRow("More Voucher লেবেল", hp.voucherMoreLabel) { onUpdate(hp.copy(voucherMoreLabel = it)) }
    PageRenameInputRow("ভাউচার ১ এমাউন্ট", hp.voucher1Amount) { onUpdate(hp.copy(voucher1Amount = it)) }
    PageRenameInputRow("ভাউচার ১ সাবটাইটেল", hp.voucher1Sub) { onUpdate(hp.copy(voucher1Sub = it)) }
    PageRenameInputRow("ভাউচার ২ এমাউন্ট", hp.voucher2Amount) { onUpdate(hp.copy(voucher2Amount = it)) }
    PageRenameInputRow("ভাউচার ২ সাবটাইটেল", hp.voucher2Sub) { onUpdate(hp.copy(voucher2Sub = it)) }
    PageRenameInputRow("Collect বাটন টেক্সট", hp.voucherCollectLabel) { onUpdate(hp.copy(voucherCollectLabel = it)) }

    PageColorHexPickerRow("ব্লক ব্যাকগ্রাউন্ড", hp.voucherBlockBg, "hp_v_blk_bg") { onUpdate(hp.copy(voucherBlockBg = it)) }
    PageColorHexPickerRow("ব্লক গ্রেডিয়েন্ট শেষ", hp.voucherBlockBgEnd, "hp_v_blk_end") { onUpdate(hp.copy(voucherBlockBgEnd = it)) }
    PageColorHexPickerRow("ব্লক বর্ডার কালার", hp.voucherBlockBorder, "hp_v_blk_brd") { onUpdate(hp.copy(voucherBlockBorder = it)) }
    PageColorHexPickerRow("শিরোনাম টেক্সট কালার", hp.voucherTitleColor, "hp_v_title_col") { onUpdate(hp.copy(voucherTitleColor = it)) }
    PageColorHexPickerRow("ভেতরের কার্ড ব্যাকগ্রাউন্ড", hp.voucherInnerBg, "hp_v_in_bg") { onUpdate(hp.copy(voucherInnerBg = it)) }
    PageColorHexPickerRow("ভেতরের কার্ড বর্ডার", hp.voucherInnerBorder, "hp_v_in_brd") { onUpdate(hp.copy(voucherInnerBorder = it)) }
    PageColorHexPickerRow("এমাউন্ট কালার", hp.voucherAmountColor, "hp_v_amt_col") { onUpdate(hp.copy(voucherAmountColor = it)) }
    PageColorHexPickerRow("CTA বাটন ব্যাকগ্রাউন্ড", hp.voucherCtaBg, "hp_v_cta_bg") { onUpdate(hp.copy(voucherCtaBg = it)) }
    PageColorHexPickerRow("CTA বাটন টেক্সট", hp.voucherCtaText, "hp_v_cta_txt") { onUpdate(hp.copy(voucherCtaText = it)) }
    PageColorHexPickerRow("CTA বাটন বর্ডার", hp.voucherCtaBorder, "hp_v_cta_brd") { onUpdate(hp.copy(voucherCtaBorder = it)) }
    PageIntSliderRow("Voucher Block Radius", hp.voucherRadiusPx, 0..32) { onUpdate(hp.copy(voucherRadiusPx = it)) }
    PageIntSliderRow("CTA Button Radius", hp.voucherCtaRadiusPx, 0..32) { onUpdate(hp.copy(voucherCtaRadiusPx = it)) }
  }
}

@Composable
private fun CategoryAndBlockControls(hp: HomePageConfig, onUpdate: (HomePageConfig) -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PageConfigSwitchRow("ক্যাটাগরী স্লাইডার দেখান", "হোম পেজে স্লাইডিং ক্যাটাগরি ব্লক প্রদর্শন করুন", hp.showCategorySlider, { onUpdate(hp.copy(showCategorySlider = it)) }, "hp_show_catslider")
    PageConfigSwitchRow("সকল পণ্য টাইল দেখান", "প্রথম কার্ডে সকল পণ্য টাইল দেখান", hp.showAllCatTile, { onUpdate(hp.copy(showAllCatTile = it)) }, "hp_show_allcat")
    PageRenameInputRow("সকল পণ্য লেবেল", hp.allCatTileLabel) { onUpdate(hp.copy(allCatTileLabel = it)) }
    PageRenameInputRow("সকল পণ্য আইকন", hp.allCatTileIcon) { onUpdate(hp.copy(allCatTileIcon = it)) }

    PageColorHexPickerRow("স্লাইডার ব্লক ব্যাকগ্রাউন্ড", hp.catSliderBlockBg, "hp_cat_blk_bg") { onUpdate(hp.copy(catSliderBlockBg = it)) }
    PageColorHexPickerRow("স্লাইডার ব্লক বর্ডার", hp.catSliderBlockBorder, "hp_cat_blk_brd") { onUpdate(hp.copy(catSliderBlockBorder = it)) }
    PageColorHexPickerRow("ক্যাটাগরী কার্ড ব্যাকগ্রাউন্ড", hp.catCardBg, "hp_cat_card_bg") { onUpdate(hp.copy(catCardBg = it)) }
    PageColorHexPickerRow("ক্যাটাগরী কার্ড বর্ডার", hp.catCardBorder, "hp_cat_card_brd") { onUpdate(hp.copy(catCardBorder = it)) }
    PageColorHexPickerRow("আইকন বক্স ব্যাকগ্রাউন্ড", hp.catIconBoxBg, "hp_cat_icon_bg") { onUpdate(hp.copy(catIconBoxBg = it)) }
    PageColorHexPickerRow("নাম লেবেল ব্যাকগ্রাউন্ড", hp.catLabelBg, "hp_cat_lbl_bg") { onUpdate(hp.copy(catLabelBg = it)) }
    PageColorHexPickerRow("নাম টেক্সট কালার", hp.catTextColor, "hp_cat_txt_col") { onUpdate(hp.copy(catTextColor = it)) }
    PageColorHexPickerRow("ব্যাজ ব্যাকগ্রাউন্ড", hp.catBadgeBg, "hp_cat_bdg_bg") { onUpdate(hp.copy(catBadgeBg = it)) }
    PageColorHexPickerRow("এক্টিভ কার্ড বর্ডার", hp.catActiveBorderColor, "hp_cat_act_brd") { onUpdate(hp.copy(catActiveBorderColor = it)) }
    PageIntSliderRow("Category Block Radius", hp.catSliderBlockRadiusPx, 0..32) { onUpdate(hp.copy(catSliderBlockRadiusPx = it)) }
    PageIntSliderRow("Category Card Radius", hp.catCardRadiusPx, 0..32) { onUpdate(hp.copy(catCardRadiusPx = it)) }

    HorizontalDivider(color = Color(0xFFE2E8F0))
    PageConfigSwitchRow("ব্লক হেডার বাটন রিভার্স (ডানে-বামে)", "শিরোনাম ও 'আরো দেখুন' বাটনের অবস্থান অদল-বদল করুন", hp.blockHeaderReversed, { onUpdate(hp.copy(blockHeaderReversed = it)) }, "hp_block_hdr_rev")
    PageIntSliderRow("Product Block Radius", hp.blockRadiusPx, 0..32) { onUpdate(hp.copy(blockRadiusPx = it)) }
    PageIntSliderRow("Product Block Border", hp.blockBorderWidthPx, 0..4) { onUpdate(hp.copy(blockBorderWidthPx = it)) }
    PageIntSliderRow("Header Pill Radius", hp.blockHeaderRadiusPx, 0..32) { onUpdate(hp.copy(blockHeaderRadiusPx = it)) }
  }
}

@Composable
private fun FooterAndTrustControls(hp: HomePageConfig, onUpdate: (HomePageConfig) -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PageConfigSwitchRow("মেইন ফুটার কার্ড দেখান", "Information, Support, Policy, Contact ও Payment ব্লক", hp.showFooterMain, { onUpdate(hp.copy(showFooterMain = it)) }, "hp_show_footer")
    PageConfigSwitchRow("৪টি ট্রাস্ট ব্যাজ কার্ড", "পেমেন্ট, ডেলিভারি, রিটার্ন ও আসল পণ্যের কার্ড", hp.showTrustBadges, { onUpdate(hp.copy(showTrustBadges = it)) }, "hp_show_trust")
    PageConfigSwitchRow("কপিরাইট বার দেখান", "একদম নিচের কপিরাইট বার", hp.showCopyrightBar, { onUpdate(hp.copy(showCopyrightBar = it)) }, "hp_show_copy")

    PageColorHexPickerRow("ফুটার ব্যাকগ্রাউন্ড", hp.footerBg, "hp_ftr_bg") { onUpdate(hp.copy(footerBg = it)) }
    PageColorHexPickerRow("ফুটার গ্রেডিয়েন্ট শেষ", hp.footerBgEnd, "hp_ftr_end") { onUpdate(hp.copy(footerBgEnd = it)) }
    PageColorHexPickerRow("ফুটার বর্ডার কালার", hp.footerBorderColor, "hp_ftr_brd") { onUpdate(hp.copy(footerBorderColor = it)) }
    PageColorHexPickerRow("ফুটার হেডিং কালার", hp.footerHeadingColor, "hp_ftr_hd") { onUpdate(hp.copy(footerHeadingColor = it)) }
    PageColorHexPickerRow("ফুটার লিংক কালার", hp.footerLinkColor, "hp_ftr_lnk") { onUpdate(hp.copy(footerLinkColor = it)) }
    PageColorHexPickerRow("ফুটার টেক্সট কালার", hp.footerTextColor, "hp_ftr_txt") { onUpdate(hp.copy(footerTextColor = it)) }
    PageIntSliderRow("Footer Radius", hp.footerRadiusPx, 0..32) { onUpdate(hp.copy(footerRadiusPx = it)) }

    HorizontalDivider(color = Color(0xFFE2E8F0))
    PageRenameInputRow("ট্রাস্ট ১ টেক্সট", hp.trust1Text) { onUpdate(hp.copy(trust1Text = it)) }
    PageRenameInputRow("ট্রাস্ট ২ টেক্সট", hp.trust2Text) { onUpdate(hp.copy(trust2Text = it)) }
    PageRenameInputRow("ট্রাস্ট ৩ টেক্সট", hp.trust3Text) { onUpdate(hp.copy(trust3Text = it)) }
    PageRenameInputRow("ট্রাস্ট ৪ টেক্সট", hp.trust4Text) { onUpdate(hp.copy(trust4Text = it)) }
    PageColorHexPickerRow("ট্রাস্ট কার্ড ব্যাকগ্রাউন্ড", hp.trustCardBg, "hp_tr_bg") { onUpdate(hp.copy(trustCardBg = it)) }
    PageColorHexPickerRow("ট্রাস্ট কার্ড টেক্সট", hp.trustCardTextColor, "hp_tr_txt") { onUpdate(hp.copy(trustCardTextColor = it)) }
    PageColorHexPickerRow("ট্রাস্ট কার্ড বর্ডার", hp.trustCardBorder, "hp_tr_brd") { onUpdate(hp.copy(trustCardBorder = it)) }

    HorizontalDivider(color = Color(0xFFE2E8F0))
    PageRenameInputRow("কপিরাইট কাস্টম টেক্সট", hp.footerCopyrightOverride) { onUpdate(hp.copy(footerCopyrightOverride = it)) }
    PageColorHexPickerRow("কপিরাইট বার ব্যাকগ্রাউন্ড", hp.copyrightBg, "hp_cp_bg") { onUpdate(hp.copy(copyrightBg = it)) }
    PageColorHexPickerRow("কপিরাইট বার টেক্সট", hp.copyrightTextColor, "hp_cp_txt") { onUpdate(hp.copy(copyrightTextColor = it)) }
    PageColorHexPickerRow("কপিরাইট বার বর্ডার", hp.copyrightBorder, "hp_cp_brd") { onUpdate(hp.copy(copyrightBorder = it)) }
  }
}

@Composable
private fun BottomNavStudioControls(hp: HomePageConfig, onUpdate: (HomePageConfig) -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PageConfigSwitchRow("বটম নেভিগেশন বার দেখান", "নিচের ৫ বাটনের মেনু বার চালু/বন্ধ করুন", hp.showBottomNav, { onUpdate(hp.copy(showBottomNav = it)) }, "hp_show_bnav")
    PageConfigSwitchRow("বাটন টেক্সট লেবেল দেখান", "আইকনের নিচে টেক্সট দেখান", hp.bottomNavShowLabels, { onUpdate(hp.copy(bottomNavShowLabels = it)) }, "hp_bnav_lbl")
    PageConfigSwitchRow("কার্ট সংখ্যা ব্যাজ দেখান", "কার্ট ট্যাবে পণ্যের সংখ্যা ব্যাজ দেখান", hp.bottomNavShowCartBadge, { onUpdate(hp.copy(bottomNavShowCartBadge = it)) }, "hp_bnav_bdg")

    PageRenameInputRow("১. হোম লেবেল", hp.navHomeLabel) { onUpdate(hp.copy(navHomeLabel = it)) }
    PageRenameInputRow("২. শপিং লেবেল", hp.navShoppingLabel) { onUpdate(hp.copy(navShoppingLabel = it)) }
    PageRenameInputRow("৩. অফার লেবেল", hp.navOffersLabel) { onUpdate(hp.copy(navOffersLabel = it)) }
    PageRenameInputRow("৪. একাউন্ট লেবেল", hp.navAccountLabel) { onUpdate(hp.copy(navAccountLabel = it)) }
    PageRenameInputRow("৫. কার্ট লেবেল", hp.navCartLabel) { onUpdate(hp.copy(navCartLabel = it)) }

    PageColorHexPickerRow("বটম বার ব্যাকগ্রাউন্ড", hp.bottomNavBg, "hp_bn_bg") { onUpdate(hp.copy(bottomNavBg = it)) }
    PageColorHexPickerRow("বটম বার বর্ডার কালার", hp.bottomNavBorderColor, "hp_bn_brd") { onUpdate(hp.copy(bottomNavBorderColor = it)) }
    PageColorHexPickerRow("এক্টিভ আইকন ও টেক্সট", hp.bottomNavActiveColor, "hp_bn_act") { onUpdate(hp.copy(bottomNavActiveColor = it)) }
    PageColorHexPickerRow("এক্টিভ পিল ব্যাকগ্রাউন্ড", hp.bottomNavActivePillBg, "hp_bn_pill") { onUpdate(hp.copy(bottomNavActivePillBg = it)) }
    PageColorHexPickerRow("ইন-এক্টিভ আইকন ও টেক্সট", hp.bottomNavInactiveColor, "hp_bn_inact") { onUpdate(hp.copy(bottomNavInactiveColor = it)) }
    PageColorHexPickerRow("কার্ট ব্যাজ ব্যাকগ্রাউন্ড", hp.bottomNavBadgeBg, "hp_bn_bdg_bg") { onUpdate(hp.copy(bottomNavBadgeBg = it)) }
    PageColorHexPickerRow("কার্ট ব্যাজ টেক্সট", hp.bottomNavBadgeText, "hp_bn_bdg_txt") { onUpdate(hp.copy(bottomNavBadgeText = it)) }
    PageIntSliderRow("Bottom Nav Top Radius", hp.bottomNavRadiusPx, 0..32) { onUpdate(hp.copy(bottomNavRadiusPx = it)) }
    PageIntSliderRow("Bottom Nav Border Width", hp.bottomNavBorderWidthPx, 0..4) { onUpdate(hp.copy(bottomNavBorderWidthPx = it)) }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FooterPagesEditorControls(
  hp: HomePageConfig,
  storeConfig: StoreConfig,
  onUpdate: (HomePageConfig) -> Unit
) {
  var selectedTopicKey by remember { mutableStateOf("privacy") }
  val defContent = remember(selectedTopicKey, storeConfig) {
    defaultFooterPageContent(selectedTopicKey, storeConfig)
  }
  val resolvedContent = remember(selectedTopicKey, hp, storeConfig) {
    resolvedFooterPageContent(selectedTopicKey, hp, storeConfig)
  }
  val currentOverride = hp.footerPageContents[selectedTopicKey]

  val pageBgCol = parseColorHex(hp.footerPageBg, Color(0xFFFFFAF5))
  val cardBgCol = parseColorHex(hp.footerPageCardBg, Color.White)
  val borderCol = parseColorHex(hp.footerPageBorderColor, Color(0xFFF3E2D0))
  val headingCol = parseColorHex(hp.footerPageHeadingColor, RoyalMaroon)
  val textCol = parseColorHex(hp.footerPageTextColor, DarkEspresso)
  val btnBgCol = parseColorHex(hp.footerPageBtnBg, RoyalMaroon)
  val btnTxtCol = parseColorHex(hp.footerPageBtnText, Color.White)
  val cardRadius = hp.footerPageRadiusPx.coerceIn(0, 32).dp

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text(
      text = "ফুটার হাব ক্লিন পেজ কাস্টমাইজেশন ও কনটেন্ট এডিটর",
      fontSize = 13.sp,
      fontWeight = FontWeight.Black,
      color = AdminInkPrimary
    )
    Text(
      text = "Information, Support ও Consumer Policy-র প্রতিটি লিংকের সরাসরি ক্লিন পেজের ডিজাইন এবং লেখা এখান থেকে পরিবর্তন করুন।",
      fontSize = 11.sp,
      color = AdminInkMuted
    )

    // Live Mini Preview of Selected Clean Footer Page
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = pageBgCol,
      border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("footer_page_live_preview")
    ) {
      Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = btnBgCol
        ) {
          Text(
            text = "← হোমে ফিরুন",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = btnTxtCol,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(cardRadius),
          color = cardBgCol,
          border = BorderStroke(1.dp, borderCol),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = resolvedContent.title,
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = headingCol,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (resolvedContent.subtitle.isNotBlank()) {
              Text(
                text = resolvedContent.subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = textCol.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
            Text(
              text = resolvedContent.body,
              fontSize = 9.5.sp,
              color = textCol,
              maxLines = 3,
              overflow = TextOverflow.Ellipsis,
              lineHeight = 13.sp
            )
          }
        }
      }
    }

    // Topic Selector Chips
    Text("১. কোন পেজের কনটেন্ট এডিট করবেন:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      FOOTER_HUB_TOPICS.forEach { meta ->
        val isSel = meta.key == selectedTopicKey
        val hasCustom = hp.footerPageContents[meta.key]?.let {
          it.title.isNotBlank() || it.subtitle.isNotBlank() || it.body.isNotBlank()
        } == true
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isSel) AdminV2CyanDeep else if (hasCustom) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
          border = BorderStroke(1.dp, if (isSel) AdminV2CyanDeep else if (hasCustom) Color(0xFF34D399) else Color(0xFFCBD5E1)),
          modifier = Modifier
            .heightIn(min = 38.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { selectedTopicKey = meta.key }
            .testTag("footer_editor_topic_${meta.key}")
        ) {
          Text(
            text = meta.defaultLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSel) Color.White else AdminInkPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
          )
        }
      }
    }

    // Content Editor Fields for Selected Topic
    val titleVal = currentOverride?.title ?: defContent.title
    val subtitleVal = currentOverride?.subtitle ?: defContent.subtitle
    val bodyVal = currentOverride?.body ?: defContent.body

    fun updateTopicOverride(nextTitle: String, nextSubtitle: String, nextBody: String) {
      val nextMap = hp.footerPageContents.toMutableMap()
      nextMap[selectedTopicKey] = FooterPageContentOverride(
        title = nextTitle,
        subtitle = nextSubtitle,
        body = nextBody
      )
      onUpdate(hp.copy(footerPageContents = nextMap))
    }

    OutlinedTextField(
      value = titleVal,
      onValueChange = { updateTopicOverride(it, subtitleVal, bodyVal) },
      label = { Text("পেজ শিরোনাম (Title)") },
      singleLine = true,
      shape = RoundedCornerShape(10.dp),
      colors = adminOutlinedTextFieldColors(),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("footer_editor_title_input")
    )

    OutlinedTextField(
      value = subtitleVal,
      onValueChange = { updateTopicOverride(titleVal, it, bodyVal) },
      label = { Text("সাবটাইটেল (Subtitle)") },
      singleLine = true,
      shape = RoundedCornerShape(10.dp),
      colors = adminOutlinedTextFieldColors(),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("footer_editor_subtitle_input")
    )

    OutlinedTextField(
      value = bodyVal,
      onValueChange = { updateTopicOverride(titleVal, subtitleVal, it) },
      label = { Text("মূল কনটেন্ট (Body Text)") },
      minLines = 4,
      maxLines = 8,
      shape = RoundedCornerShape(10.dp),
      colors = adminOutlinedTextFieldColors(),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("footer_editor_body_input")
    )

    OutlinedButton(
      onClick = {
        val nextMap = hp.footerPageContents.toMutableMap().apply { remove(selectedTopicKey) }
        onUpdate(hp.copy(footerPageContents = nextMap))
      },
      shape = RoundedCornerShape(10.dp),
      border = BorderStroke(1.dp, Color(0xFF94A3B8)),
      modifier = Modifier.heightIn(min = 42.dp)
    ) {
      Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
      Text(" এই পেজ ডিফল্টে ফিরিয়ে আনুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }

    HorizontalDivider(color = Color(0xFFE2E8F0))
    Text("২. ক্লিন পেজের কালার ও বর্ডার সেটিংস:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)

    PageColorHexPickerRow("পেজ ব্যাকগ্রাউন্ড", hp.footerPageBg, "hp_fp_bg") { onUpdate(hp.copy(footerPageBg = it)) }
    PageColorHexPickerRow("কার্ড ব্যাকগ্রাউন্ড", hp.footerPageCardBg, "hp_fp_card_bg") { onUpdate(hp.copy(footerPageCardBg = it)) }
    PageColorHexPickerRow("কার্ড বর্ডার কালার", hp.footerPageBorderColor, "hp_fp_brd") { onUpdate(hp.copy(footerPageBorderColor = it)) }
    PageColorHexPickerRow("শিরোনাম কালার", hp.footerPageHeadingColor, "hp_fp_hd") { onUpdate(hp.copy(footerPageHeadingColor = it)) }
    PageColorHexPickerRow("মূল টেক্সট কালার", hp.footerPageTextColor, "hp_fp_txt") { onUpdate(hp.copy(footerPageTextColor = it)) }
    PageColorHexPickerRow("ব্যাক বাটন ব্যাকগ্রাউন্ড", hp.footerPageBtnBg, "hp_fp_btn_bg") { onUpdate(hp.copy(footerPageBtnBg = it)) }
    PageColorHexPickerRow("ব্যাক বাটন টেক্সট", hp.footerPageBtnText, "hp_fp_btn_txt") { onUpdate(hp.copy(footerPageBtnText = it)) }
    PageIntSliderRow("Card Corner Radius", hp.footerPageRadiusPx, 0..32) { onUpdate(hp.copy(footerPageRadiusPx = it)) }
  }
}
