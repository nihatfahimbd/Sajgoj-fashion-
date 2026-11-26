package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class HomePageConfig(
  // 1. Visibility Toggles
  val showSearchBar: Boolean = true,
  val showCatMenuBtn: Boolean = true,
  val showHomeBtn: Boolean = true,
  val showVoiceBtn: Boolean = true,
  val showImageSearchBtn: Boolean = true,
  val showScanBtn: Boolean = true,
  val showVoucherStrip: Boolean = true,
  val showVoucherBadge: Boolean = true,
  val showMoreVoucherBtn: Boolean = true,
  val showCollectAllBtn: Boolean = true,
  val showCategorySlider: Boolean = true,
  val showAllCatTile: Boolean = true,
  val showFooterMain: Boolean = true,
  val showTrustBadges: Boolean = true,
  val showCopyrightBar: Boolean = true,
  val showBottomNav: Boolean = true,
  val bottomNavShowLabels: Boolean = true,
  val bottomNavShowCartBadge: Boolean = true,
  val blockHeaderReversed: Boolean = false,
  // 2. Horizontal Ordering (Left/Right Moving)
  val searchBarOrder: List<String> = listOf("cat_menu", "home_btn", "search_input"),
  val searchToolsOrder: List<String> = listOf("voice", "image", "scan"),
  val voucherSlotsOrder: List<String> = listOf("v1", "v2", "cta"),
  val categoryOrder: List<String> = emptyList(),
  val trustBadgeOrder: List<String> = listOf("payment", "delivery", "return", "authentic"),
  val bottomNavOrder: List<String> = listOf("home", "shopping", "offers", "account", "cart"),
  // 3. Header Login & Search Strip
  val loginBtnLabel: String = "Login / Sign",
  val loginBtnMobileLabel: String = "লগইন",
  val loginBtnBg: String = "#0b0f19",
  val loginBtnBgEnd: String = "#1e293b",
  val loginBtnText: String = "#ffffff",
  val loginBtnBorder: String = "#ffffff",
  val loginBtnRadiusPx: Int = 24,
  val catMenuLabel: String = "সকল ক্যাটাগরী",
  val searchPlaceholder: String = "খুঁজুন...",
  val scanBtnLabel: String = "স্ক্যান",
  val searchBarContainerBg: String = "transparent",
  val searchBarContainerBorder: String = "transparent",
  val searchCatBtnBg: String = "#fca5a5",
  val searchCatBtnBgEnd: String = "#f87171",
  val searchCatBtnText: String = "#000000",
  val searchCatBtnBorder: String = "#dc2626",
  val searchHomeBtnBg: String = "#fca5a5",
  val searchHomeBtnBgEnd: String = "#f87171",
  val searchHomeBtnText: String = "#000000",
  val searchHomeBtnBorder: String = "#dc2626",
  val searchInputBg: String = "#ffffff",
  val searchInputText: String = "#000000",
  val searchInputBorder: String = "#ffffff",
  val searchToolBtnBg: String = "#fef2f2",
  val searchToolBtnText: String = "#000000",
  val searchToolBtnBorder: String = "#fecaca",
  val searchScanBtnBg: String = "#fca5a5",
  val searchScanBtnBgEnd: String = "#f87171",
  val searchScanBtnText: String = "#000000",
  val searchScanBtnBorder: String = "#dc2626",
  val searchBorderWidthPx: Int = 1,
  val searchRadiusPx: Int = 24,
  // 4. Voucher Strip Block
  val voucherTitle: String = "Claim Vouchers to Save More!",
  val voucherBadgeText: String = "11.11 / SAJGOJ",
  val voucherMoreLabel: String = "More Voucher",
  val voucher1Amount: String = "৳২,২৫০",
  val voucher1Sub: String = "Voucher Max",
  val voucher2Amount: String = "৳৪৫",
  val voucher2Sub: String = "Free shipping",
  val voucherCollectLabel: String = "Collect All",
  val voucherBlockBg: String = "#fca5a5",
  val voucherBlockBgEnd: String = "#f87171",
  val voucherBlockBorder: String = "#ffffff",
  val voucherBorderWidthPx: Int = 1,
  val voucherRadiusPx: Int = 16,
  val voucherTitleColor: String = "#000000",
  val voucherMoreColor: String = "#000000",
  val voucherTagBg: String = "#0b0f19",
  val voucherTagText: String = "#ffffff",
  val voucherTagBorder: String = "#ffffff",
  val voucherInnerBg: String = "#f97316",
  val voucherInnerBorder: String = "#ffffff",
  val voucherAmountColor: String = "#000000",
  val voucherSubColor: String = "#000000",
  val voucherCtaBg: String = "#0b0f19",
  val voucherCtaBgEnd: String = "#1e293b",
  val voucherCtaText: String = "#ffffff",
  val voucherCtaBorder: String = "#ffffff",
  val voucherCtaRadiusPx: Int = 24,
  // 5. Category Slider Block
  val allCatTileLabel: String = "সকল পণ্য",
  val allCatTileIcon: String = "🌟",
  val catSliderBlockBg: String = "#ffffff",
  val catSliderBlockBgEnd: String = "#e0f2fe",
  val catSliderBlockBorder: String = "#ffffff",
  val catSliderBlockRadiusPx: Int = 16,
  val catSliderBorderWidthPx: Int = 1,
  val catCardBg: String = "#ff7b7b",
  val catCardBgEnd: String = "#fca5a5",
  val catCardBorder: String = "#ffffff",
  val catCardRadiusPx: Int = 12,
  val catIconBoxBg: String = "#ffffff",
  val catIconBoxBorder: String = "#ffffff",
  val catLabelBg: String = "#ffffff",
  val catTextColor: String = "#000000",
  val catBadgeBg: String = "#0b0f19",
  val catBadgeText: String = "#ffffff",
  val catActiveBorderColor: String = "#000000",
  // 6. Product Section Layout Tuning
  val blockRadiusPx: Int = 20,
  val blockBorderWidthPx: Int = 1,
  val blockHeaderRadiusPx: Int = 24,
  // 7. Footer, 4 Trust Badges & Copyright
  val trust1Icon: String = "💳",
  val trust1Text: String = "নিরাপদ পেমেন্ট (bKash/COD)",
  val trust2Icon: String = "⚡",
  val trust2Text: String = "দ্রুততম হোম ডেলিভারি",
  val trust3Icon: String = "🔄",
  val trust3Text: String = "৭ দিনের সহজ রিটার্ন",
  val trust4Icon: String = "👑",
  val trust4Text: String = "১০০% খাঁটি ও আসল পণ্য",
  val footerCopyrightOverride: String = "",
  val footerBg: String = "#ffffff",
  val footerBgEnd: String = "#bae6fd",
  val footerBorderColor: String = "#ffffff",
  val footerBorderWidthPx: Int = 2,
  val footerRadiusPx: Int = 24,
  val footerTextColor: String = "#000000",
  val footerHeadingColor: String = "#000000",
  val footerLinkColor: String = "#7a1c2b",
  val trustCardBg: String = "#f87171",
  val trustCardBgEnd: String = "#ef4444",
  val trustCardBorder: String = "#ffffff",
  val trustCardBorderWidthPx: Int = 1,
  val trustCardTextColor: String = "#000000",
  val trustCardRadiusPx: Int = 16,
  val copyrightBg: String = "#f87171",
  val copyrightBgEnd: String = "#ef4444",
  val copyrightBorder: String = "#ffffff",
  val copyrightTextColor: String = "#000000",
  val copyrightRadiusPx: Int = 16,
  // 8. Bottom Navigation Bar
  val navHomeLabel: String = "হোম",
  val navShoppingLabel: String = "শপিং",
  val navOffersLabel: String = "অফার",
  val navAccountLabel: String = "একাউন্ট",
  val navCartLabel: String = "কার্ট",
  val bottomNavBg: String = "#ffffff",
  val bottomNavBorderColor: String = "#e5e7eb",
  val bottomNavBorderWidthPx: Int = 1,
  val bottomNavRadiusPx: Int = 0,
  val bottomNavActiveColor: String = "#7a1c2b",
  val bottomNavActivePillBg: String = "transparent",
  val bottomNavInactiveColor: String = "#4b5563",
  val bottomNavBadgeBg: String = "#7a1c2b",
  val bottomNavBadgeText: String = "#ffffff"
)

object HomePageConfigJson {
  private fun parseList(arr: JSONArray?, fallback: List<String>): List<String> {
    if (arr == null) return fallback
    val out = mutableListOf<String>()
    for (i in 0 until arr.length()) {
      val item = arr.optString(i).trim()
      if (item.isNotBlank() && !out.contains(item)) out.add(item)
    }
    return out
  }

  private fun mergeOrder(raw: JSONArray?, defaults: List<String>): List<String> {
    val parsed = parseList(raw, defaults)
    return if (defaults.isEmpty()) parsed else (parsed + defaults.filterNot { parsed.contains(it) }).distinct()
  }

  fun toJsonObject(h: HomePageConfig): JSONObject = JSONObject().apply {
    put("showSearchBar", h.showSearchBar)
    put("showCatMenuBtn", h.showCatMenuBtn)
    put("showHomeBtn", h.showHomeBtn)
    put("showVoiceBtn", h.showVoiceBtn)
    put("showImageSearchBtn", h.showImageSearchBtn)
    put("showScanBtn", h.showScanBtn)
    put("showVoucherStrip", h.showVoucherStrip)
    put("showVoucherBadge", h.showVoucherBadge)
    put("showMoreVoucherBtn", h.showMoreVoucherBtn)
    put("showCollectAllBtn", h.showCollectAllBtn)
    put("showCategorySlider", h.showCategorySlider)
    put("showAllCatTile", h.showAllCatTile)
    put("showFooterMain", h.showFooterMain)
    put("showTrustBadges", h.showTrustBadges)
    put("showCopyrightBar", h.showCopyrightBar)
    put("showBottomNav", h.showBottomNav)
    put("bottomNavShowLabels", h.bottomNavShowLabels)
    put("bottomNavShowCartBadge", h.bottomNavShowCartBadge)
    put("blockHeaderReversed", h.blockHeaderReversed)

    put("searchBarOrder", JSONArray(h.searchBarOrder))
    put("searchToolsOrder", JSONArray(h.searchToolsOrder))
    put("voucherSlotsOrder", JSONArray(h.voucherSlotsOrder))
    put("categoryOrder", JSONArray(h.categoryOrder))
    put("trustBadgeOrder", JSONArray(h.trustBadgeOrder))
    put("bottomNavOrder", JSONArray(h.bottomNavOrder))

    put("loginBtnLabel", h.loginBtnLabel)
    put("loginBtnMobileLabel", h.loginBtnMobileLabel)
    put("loginBtnBg", h.loginBtnBg)
    put("loginBtnBgEnd", h.loginBtnBgEnd)
    put("loginBtnText", h.loginBtnText)
    put("loginBtnBorder", h.loginBtnBorder)
    put("loginBtnRadiusPx", h.loginBtnRadiusPx)
    put("catMenuLabel", h.catMenuLabel)
    put("searchPlaceholder", h.searchPlaceholder)
    put("scanBtnLabel", h.scanBtnLabel)
    put("searchBarContainerBg", h.searchBarContainerBg)
    put("searchBarContainerBorder", h.searchBarContainerBorder)
    put("searchCatBtnBg", h.searchCatBtnBg)
    put("searchCatBtnBgEnd", h.searchCatBtnBgEnd)
    put("searchCatBtnText", h.searchCatBtnText)
    put("searchCatBtnBorder", h.searchCatBtnBorder)
    put("searchHomeBtnBg", h.searchHomeBtnBg)
    put("searchHomeBtnBgEnd", h.searchHomeBtnBgEnd)
    put("searchHomeBtnText", h.searchHomeBtnText)
    put("searchHomeBtnBorder", h.searchHomeBtnBorder)
    put("searchInputBg", h.searchInputBg)
    put("searchInputText", h.searchInputText)
    put("searchInputBorder", h.searchInputBorder)
    put("searchToolBtnBg", h.searchToolBtnBg)
    put("searchToolBtnText", h.searchToolBtnText)
    put("searchToolBtnBorder", h.searchToolBtnBorder)
    put("searchScanBtnBg", h.searchScanBtnBg)
    put("searchScanBtnBgEnd", h.searchScanBtnBgEnd)
    put("searchScanBtnText", h.searchScanBtnText)
    put("searchScanBtnBorder", h.searchScanBtnBorder)
    put("searchBorderWidthPx", h.searchBorderWidthPx)
    put("searchRadiusPx", h.searchRadiusPx)

    put("voucherTitle", h.voucherTitle)
    put("voucherBadgeText", h.voucherBadgeText)
    put("voucherMoreLabel", h.voucherMoreLabel)
    put("voucher1Amount", h.voucher1Amount)
    put("voucher1Sub", h.voucher1Sub)
    put("voucher2Amount", h.voucher2Amount)
    put("voucher2Sub", h.voucher2Sub)
    put("voucherCollectLabel", h.voucherCollectLabel)
    put("voucherBlockBg", h.voucherBlockBg)
    put("voucherBlockBgEnd", h.voucherBlockBgEnd)
    put("voucherBlockBorder", h.voucherBlockBorder)
    put("voucherBorderWidthPx", h.voucherBorderWidthPx)
    put("voucherRadiusPx", h.voucherRadiusPx)
    put("voucherTitleColor", h.voucherTitleColor)
    put("voucherMoreColor", h.voucherMoreColor)
    put("voucherTagBg", h.voucherTagBg)
    put("voucherTagText", h.voucherTagText)
    put("voucherTagBorder", h.voucherTagBorder)
    put("voucherInnerBg", h.voucherInnerBg)
    put("voucherInnerBorder", h.voucherInnerBorder)
    put("voucherAmountColor", h.voucherAmountColor)
    put("voucherSubColor", h.voucherSubColor)
    put("voucherCtaBg", h.voucherCtaBg)
    put("voucherCtaBgEnd", h.voucherCtaBgEnd)
    put("voucherCtaText", h.voucherCtaText)
    put("voucherCtaBorder", h.voucherCtaBorder)
    put("voucherCtaRadiusPx", h.voucherCtaRadiusPx)

    put("allCatTileLabel", h.allCatTileLabel)
    put("allCatTileIcon", h.allCatTileIcon)
    put("catSliderBlockBg", h.catSliderBlockBg)
    put("catSliderBlockBgEnd", h.catSliderBlockBgEnd)
    put("catSliderBlockBorder", h.catSliderBlockBorder)
    put("catSliderBlockRadiusPx", h.catSliderBlockRadiusPx)
    put("catSliderBorderWidthPx", h.catSliderBorderWidthPx)
    put("catCardBg", h.catCardBg)
    put("catCardBgEnd", h.catCardBgEnd)
    put("catCardBorder", h.catCardBorder)
    put("catCardRadiusPx", h.catCardRadiusPx)
    put("catIconBoxBg", h.catIconBoxBg)
    put("catIconBoxBorder", h.catIconBoxBorder)
    put("catLabelBg", h.catLabelBg)
    put("catTextColor", h.catTextColor)
    put("catBadgeBg", h.catBadgeBg)
    put("catBadgeText", h.catBadgeText)
    put("catActiveBorderColor", h.catActiveBorderColor)

    put("blockRadiusPx", h.blockRadiusPx)
    put("blockBorderWidthPx", h.blockBorderWidthPx)
    put("blockHeaderRadiusPx", h.blockHeaderRadiusPx)

    put("trust1Icon", h.trust1Icon)
    put("trust1Text", h.trust1Text)
    put("trust2Icon", h.trust2Icon)
    put("trust2Text", h.trust2Text)
    put("trust3Icon", h.trust3Icon)
    put("trust3Text", h.trust3Text)
    put("trust4Icon", h.trust4Icon)
    put("trust4Text", h.trust4Text)
    put("footerCopyrightOverride", h.footerCopyrightOverride)
    put("footerBg", h.footerBg)
    put("footerBgEnd", h.footerBgEnd)
    put("footerBorderColor", h.footerBorderColor)
    put("footerBorderWidthPx", h.footerBorderWidthPx)
    put("footerRadiusPx", h.footerRadiusPx)
    put("footerTextColor", h.footerTextColor)
    put("footerHeadingColor", h.footerHeadingColor)
    put("footerLinkColor", h.footerLinkColor)
    put("trustCardBg", h.trustCardBg)
    put("trustCardBgEnd", h.trustCardBgEnd)
    put("trustCardBorder", h.trustCardBorder)
    put("trustCardBorderWidthPx", h.trustCardBorderWidthPx)
    put("trustCardTextColor", h.trustCardTextColor)
    put("trustCardRadiusPx", h.trustCardRadiusPx)
    put("copyrightBg", h.copyrightBg)
    put("copyrightBgEnd", h.copyrightBgEnd)
    put("copyrightBorder", h.copyrightBorder)
    put("copyrightTextColor", h.copyrightTextColor)
    put("copyrightRadiusPx", h.copyrightRadiusPx)

    put("navHomeLabel", h.navHomeLabel)
    put("navShoppingLabel", h.navShoppingLabel)
    put("navOffersLabel", h.navOffersLabel)
    put("navAccountLabel", h.navAccountLabel)
    put("navCartLabel", h.navCartLabel)
    put("bottomNavBg", h.bottomNavBg)
    put("bottomNavBorderColor", h.bottomNavBorderColor)
    put("bottomNavBorderWidthPx", h.bottomNavBorderWidthPx)
    put("bottomNavRadiusPx", h.bottomNavRadiusPx)
    put("bottomNavActiveColor", h.bottomNavActiveColor)
    put("bottomNavActivePillBg", h.bottomNavActivePillBg)
    put("bottomNavInactiveColor", h.bottomNavInactiveColor)
    put("bottomNavBadgeBg", h.bottomNavBadgeBg)
    put("bottomNavBadgeText", h.bottomNavBadgeText)
  }

  fun fromJsonObject(obj: JSONObject?): HomePageConfig {
    val d = HomePageConfig()
    if (obj == null) return d
    return HomePageConfig(
      showSearchBar = obj.optBoolean("showSearchBar", d.showSearchBar),
      showCatMenuBtn = obj.optBoolean("showCatMenuBtn", d.showCatMenuBtn),
      showHomeBtn = obj.optBoolean("showHomeBtn", d.showHomeBtn),
      showVoiceBtn = obj.optBoolean("showVoiceBtn", d.showVoiceBtn),
      showImageSearchBtn = obj.optBoolean("showImageSearchBtn", d.showImageSearchBtn),
      showScanBtn = obj.optBoolean("showScanBtn", d.showScanBtn),
      showVoucherStrip = obj.optBoolean("showVoucherStrip", d.showVoucherStrip),
      showVoucherBadge = obj.optBoolean("showVoucherBadge", d.showVoucherBadge),
      showMoreVoucherBtn = obj.optBoolean("showMoreVoucherBtn", d.showMoreVoucherBtn),
      showCollectAllBtn = obj.optBoolean("showCollectAllBtn", d.showCollectAllBtn),
      showCategorySlider = obj.optBoolean("showCategorySlider", d.showCategorySlider),
      showAllCatTile = obj.optBoolean("showAllCatTile", d.showAllCatTile),
      showFooterMain = obj.optBoolean("showFooterMain", d.showFooterMain),
      showTrustBadges = obj.optBoolean("showTrustBadges", d.showTrustBadges),
      showCopyrightBar = obj.optBoolean("showCopyrightBar", d.showCopyrightBar),
      showBottomNav = obj.optBoolean("showBottomNav", d.showBottomNav),
      bottomNavShowLabels = obj.optBoolean("bottomNavShowLabels", d.bottomNavShowLabels),
      bottomNavShowCartBadge = obj.optBoolean("bottomNavShowCartBadge", d.bottomNavShowCartBadge),
      blockHeaderReversed = obj.optBoolean("blockHeaderReversed", d.blockHeaderReversed),
      searchBarOrder = mergeOrder(obj.optJSONArray("searchBarOrder"), d.searchBarOrder),
      searchToolsOrder = mergeOrder(obj.optJSONArray("searchToolsOrder"), d.searchToolsOrder),
      voucherSlotsOrder = mergeOrder(obj.optJSONArray("voucherSlotsOrder"), d.voucherSlotsOrder),
      categoryOrder = parseList(obj.optJSONArray("categoryOrder"), d.categoryOrder),
      trustBadgeOrder = mergeOrder(obj.optJSONArray("trustBadgeOrder"), d.trustBadgeOrder),
      bottomNavOrder = mergeOrder(obj.optJSONArray("bottomNavOrder"), d.bottomNavOrder),
      loginBtnLabel = obj.optString("loginBtnLabel", d.loginBtnLabel),
      loginBtnMobileLabel = obj.optString("loginBtnMobileLabel", d.loginBtnMobileLabel),
      loginBtnBg = obj.optString("loginBtnBg", d.loginBtnBg),
      loginBtnBgEnd = obj.optString("loginBtnBgEnd", d.loginBtnBgEnd),
      loginBtnText = obj.optString("loginBtnText", d.loginBtnText),
      loginBtnBorder = obj.optString("loginBtnBorder", d.loginBtnBorder),
      loginBtnRadiusPx = obj.optInt("loginBtnRadiusPx", d.loginBtnRadiusPx).coerceIn(0, 32),
      catMenuLabel = obj.optString("catMenuLabel", d.catMenuLabel),
      searchPlaceholder = obj.optString("searchPlaceholder", d.searchPlaceholder),
      scanBtnLabel = obj.optString("scanBtnLabel", d.scanBtnLabel),
      searchBarContainerBg = obj.optString("searchBarContainerBg", d.searchBarContainerBg),
      searchBarContainerBorder = obj.optString("searchBarContainerBorder", d.searchBarContainerBorder),
      searchCatBtnBg = obj.optString("searchCatBtnBg", d.searchCatBtnBg),
      searchCatBtnBgEnd = obj.optString("searchCatBtnBgEnd", d.searchCatBtnBgEnd),
      searchCatBtnText = obj.optString("searchCatBtnText", d.searchCatBtnText),
      searchCatBtnBorder = obj.optString("searchCatBtnBorder", d.searchCatBtnBorder),
      searchHomeBtnBg = obj.optString("searchHomeBtnBg", d.searchHomeBtnBg),
      searchHomeBtnBgEnd = obj.optString("searchHomeBtnBgEnd", d.searchHomeBtnBgEnd),
      searchHomeBtnText = obj.optString("searchHomeBtnText", d.searchHomeBtnText),
      searchHomeBtnBorder = obj.optString("searchHomeBtnBorder", d.searchHomeBtnBorder),
      searchInputBg = obj.optString("searchInputBg", d.searchInputBg),
      searchInputText = obj.optString("searchInputText", d.searchInputText),
      searchInputBorder = obj.optString("searchInputBorder", d.searchInputBorder),
      searchToolBtnBg = obj.optString("searchToolBtnBg", d.searchToolBtnBg),
      searchToolBtnText = obj.optString("searchToolBtnText", d.searchToolBtnText),
      searchToolBtnBorder = obj.optString("searchToolBtnBorder", d.searchToolBtnBorder),
      searchScanBtnBg = obj.optString("searchScanBtnBg", d.searchScanBtnBg),
      searchScanBtnBgEnd = obj.optString("searchScanBtnBgEnd", d.searchScanBtnBgEnd),
      searchScanBtnText = obj.optString("searchScanBtnText", d.searchScanBtnText),
      searchScanBtnBorder = obj.optString("searchScanBtnBorder", d.searchScanBtnBorder),
      searchBorderWidthPx = obj.optInt("searchBorderWidthPx", d.searchBorderWidthPx).coerceIn(0, 4),
      searchRadiusPx = obj.optInt("searchRadiusPx", d.searchRadiusPx).coerceIn(0, 32),
      voucherTitle = obj.optString("voucherTitle", d.voucherTitle),
      voucherBadgeText = obj.optString("voucherBadgeText", d.voucherBadgeText),
      voucherMoreLabel = obj.optString("voucherMoreLabel", d.voucherMoreLabel),
      voucher1Amount = obj.optString("voucher1Amount", d.voucher1Amount),
      voucher1Sub = obj.optString("voucher1Sub", d.voucher1Sub),
      voucher2Amount = obj.optString("voucher2Amount", d.voucher2Amount),
      voucher2Sub = obj.optString("voucher2Sub", d.voucher2Sub),
      voucherCollectLabel = obj.optString("voucherCollectLabel", d.voucherCollectLabel),
      voucherBlockBg = obj.optString("voucherBlockBg", d.voucherBlockBg),
      voucherBlockBgEnd = obj.optString("voucherBlockBgEnd", d.voucherBlockBgEnd),
      voucherBlockBorder = obj.optString("voucherBlockBorder", d.voucherBlockBorder),
      voucherBorderWidthPx = obj.optInt("voucherBorderWidthPx", d.voucherBorderWidthPx).coerceIn(0, 4),
      voucherRadiusPx = obj.optInt("voucherRadiusPx", d.voucherRadiusPx).coerceIn(0, 32),
      voucherTitleColor = obj.optString("voucherTitleColor", d.voucherTitleColor),
      voucherMoreColor = obj.optString("voucherMoreColor", d.voucherMoreColor),
      voucherTagBg = obj.optString("voucherTagBg", d.voucherTagBg),
      voucherTagText = obj.optString("voucherTagText", d.voucherTagText),
      voucherTagBorder = obj.optString("voucherTagBorder", d.voucherTagBorder),
      voucherInnerBg = obj.optString("voucherInnerBg", d.voucherInnerBg),
      voucherInnerBorder = obj.optString("voucherInnerBorder", d.voucherInnerBorder),
      voucherAmountColor = obj.optString("voucherAmountColor", d.voucherAmountColor),
      voucherSubColor = obj.optString("voucherSubColor", d.voucherSubColor),
      voucherCtaBg = obj.optString("voucherCtaBg", d.voucherCtaBg),
      voucherCtaBgEnd = obj.optString("voucherCtaBgEnd", d.voucherCtaBgEnd),
      voucherCtaText = obj.optString("voucherCtaText", d.voucherCtaText),
      voucherCtaBorder = obj.optString("voucherCtaBorder", d.voucherCtaBorder),
      voucherCtaRadiusPx = obj.optInt("voucherCtaRadiusPx", d.voucherCtaRadiusPx).coerceIn(0, 32),
      allCatTileLabel = obj.optString("allCatTileLabel", d.allCatTileLabel),
      allCatTileIcon = obj.optString("allCatTileIcon", d.allCatTileIcon),
      catSliderBlockBg = obj.optString("catSliderBlockBg", d.catSliderBlockBg),
      catSliderBlockBgEnd = obj.optString("catSliderBlockBgEnd", d.catSliderBlockBgEnd),
      catSliderBlockBorder = obj.optString("catSliderBlockBorder", d.catSliderBlockBorder),
      catSliderBlockRadiusPx = obj.optInt("catSliderBlockRadiusPx", d.catSliderBlockRadiusPx).coerceIn(0, 32),
      catSliderBorderWidthPx = obj.optInt("catSliderBorderWidthPx", d.catSliderBorderWidthPx).coerceIn(0, 4),
      catCardBg = obj.optString("catCardBg", d.catCardBg),
      catCardBgEnd = obj.optString("catCardBgEnd", d.catCardBgEnd),
      catCardBorder = obj.optString("catCardBorder", d.catCardBorder),
      catCardRadiusPx = obj.optInt("catCardRadiusPx", d.catCardRadiusPx).coerceIn(0, 32),
      catIconBoxBg = obj.optString("catIconBoxBg", d.catIconBoxBg),
      catIconBoxBorder = obj.optString("catIconBoxBorder", d.catIconBoxBorder),
      catLabelBg = obj.optString("catLabelBg", d.catLabelBg),
      catTextColor = obj.optString("catTextColor", d.catTextColor),
      catBadgeBg = obj.optString("catBadgeBg", d.catBadgeBg),
      catBadgeText = obj.optString("catBadgeText", d.catBadgeText),
      catActiveBorderColor = obj.optString("catActiveBorderColor", d.catActiveBorderColor),
      blockRadiusPx = obj.optInt("blockRadiusPx", d.blockRadiusPx).coerceIn(0, 32),
      blockBorderWidthPx = obj.optInt("blockBorderWidthPx", d.blockBorderWidthPx).coerceIn(0, 4),
      blockHeaderRadiusPx = obj.optInt("blockHeaderRadiusPx", d.blockHeaderRadiusPx).coerceIn(0, 32),
      trust1Icon = obj.optString("trust1Icon", d.trust1Icon),
      trust1Text = obj.optString("trust1Text", d.trust1Text),
      trust2Icon = obj.optString("trust2Icon", d.trust2Icon),
      trust2Text = obj.optString("trust2Text", d.trust2Text),
      trust3Icon = obj.optString("trust3Icon", d.trust3Icon),
      trust3Text = obj.optString("trust3Text", d.trust3Text),
      trust4Icon = obj.optString("trust4Icon", d.trust4Icon),
      trust4Text = obj.optString("trust4Text", d.trust4Text),
      footerCopyrightOverride = obj.optString("footerCopyrightOverride", d.footerCopyrightOverride),
      footerBg = obj.optString("footerBg", d.footerBg),
      footerBgEnd = obj.optString("footerBgEnd", d.footerBgEnd),
      footerBorderColor = obj.optString("footerBorderColor", d.footerBorderColor),
      footerBorderWidthPx = obj.optInt("footerBorderWidthPx", d.footerBorderWidthPx).coerceIn(0, 4),
      footerRadiusPx = obj.optInt("footerRadiusPx", d.footerRadiusPx).coerceIn(0, 32),
      footerTextColor = obj.optString("footerTextColor", d.footerTextColor),
      footerHeadingColor = obj.optString("footerHeadingColor", d.footerHeadingColor),
      footerLinkColor = obj.optString("footerLinkColor", d.footerLinkColor),
      trustCardBg = obj.optString("trustCardBg", d.trustCardBg),
      trustCardBgEnd = obj.optString("trustCardBgEnd", d.trustCardBgEnd),
      trustCardBorder = obj.optString("trustCardBorder", d.trustCardBorder),
      trustCardBorderWidthPx = obj.optInt("trustCardBorderWidthPx", d.trustCardBorderWidthPx).coerceIn(0, 4),
      trustCardTextColor = obj.optString("trustCardTextColor", d.trustCardTextColor),
      trustCardRadiusPx = obj.optInt("trustCardRadiusPx", d.trustCardRadiusPx).coerceIn(0, 32),
      copyrightBg = obj.optString("copyrightBg", d.copyrightBg),
      copyrightBgEnd = obj.optString("copyrightBgEnd", d.copyrightBgEnd),
      copyrightBorder = obj.optString("copyrightBorder", d.copyrightBorder),
      copyrightTextColor = obj.optString("copyrightTextColor", d.copyrightTextColor),
      copyrightRadiusPx = obj.optInt("copyrightRadiusPx", d.copyrightRadiusPx).coerceIn(0, 32),
      navHomeLabel = obj.optString("navHomeLabel", d.navHomeLabel),
      navShoppingLabel = obj.optString("navShoppingLabel", d.navShoppingLabel),
      navOffersLabel = obj.optString("navOffersLabel", d.navOffersLabel),
      navAccountLabel = obj.optString("navAccountLabel", d.navAccountLabel),
      navCartLabel = obj.optString("navCartLabel", d.navCartLabel),
      bottomNavBg = obj.optString("bottomNavBg", d.bottomNavBg),
      bottomNavBorderColor = obj.optString("bottomNavBorderColor", d.bottomNavBorderColor),
      bottomNavBorderWidthPx = obj.optInt("bottomNavBorderWidthPx", d.bottomNavBorderWidthPx).coerceIn(0, 4),
      bottomNavRadiusPx = obj.optInt("bottomNavRadiusPx", d.bottomNavRadiusPx).coerceIn(0, 32),
      bottomNavActiveColor = obj.optString("bottomNavActiveColor", d.bottomNavActiveColor),
      bottomNavActivePillBg = obj.optString("bottomNavActivePillBg", d.bottomNavActivePillBg),
      bottomNavInactiveColor = obj.optString("bottomNavInactiveColor", d.bottomNavInactiveColor),
      bottomNavBadgeBg = obj.optString("bottomNavBadgeBg", d.bottomNavBadgeBg),
      bottomNavBadgeText = obj.optString("bottomNavBadgeText", d.bottomNavBadgeText)
    )
  }
}
