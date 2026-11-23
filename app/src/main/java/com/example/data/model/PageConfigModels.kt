package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class DetailPageConfig(
  val backdropColor: String = "#000000",
  val modalBgColor: String = "#FFFFFF",
  val headerBgColor: String = "#FFFFFF",
  val headerTextColor: String = "#7A1C2B",
  val skuTextColor: String = "#B8956A",
  val priceColor: String = "#7A1C2B",
  val regularPriceColor: String = "#94A3B8",
  val stockInColor: String = "#15803D",
  val stockOutColor: String = "#DC2626",
  val descBlockBg: String = "#FFFAF5",
  val descBlockBorder: String = "#F3E2D0",
  val descHeadingColor: String = "#000000",
  val descTextColor: String = "#000000",
  val variantBlockBg: String = "#FFFAF5",
  val variantBlockBorder: String = "#F3E2D0",
  val variantLabelColor: String = "#1F2937",
  val variantChipBg: String = "#FFFFFF",
  val variantChipText: String = "#374151",
  val variantChipBorder: String = "#F3E2D0",
  val variantActiveBg: String = "#7A1C2B",
  val variantActiveText: String = "#FFFFFF",
  val variantSizeActiveBg: String = "#D97706",
  val tableHeaderBg: String = "#0F294A",
  val tableHeaderText: String = "#FFFFFF",
  val tableBgColor: String = "#FFFFFF",
  val tableAltRowBg: String = "#FFFAF5",
  val tableTextColor: String = "#000000",
  val tableBorderColor: String = "#E2D2C0",
  val qtyBlockBg: String = "#FFFFFF",
  val qtyBlockBorder: String = "#F3E2D0",
  val qtyBtnBg: String = "#F3F4F6",
  val qtyBtnText: String = "#374151",
  val wishlistBtnBg: String = "#FFFAF5",
  val wishlistBtnText: String = "#E11D48",
  val wishlistBtnBorder: String = "#FECDD3",
  val sizeGuideBtnBg: String = "#FFFBEB",
  val sizeGuideBtnText: String = "#78350F",
  val sizeGuideBtnBorder: String = "#FCD34D",
  val qrBtnBg: String = "#FFF8F0",
  val qrBtnText: String = "#7A1C2B",
  val qrBtnBorder: String = "#D4A574",
  val addToCartBtnBg: String = "#7A1C2B",
  val addToCartBtnBgEnd: String = "#5A1220",
  val addToCartBtnText: String = "#FFFFFF",
  val reviewSectionBg: String = "#FFFDFA",
  val reviewSectionBorder: String = "#F3E2D0",
  val reviewCardBg: String = "#FFFFFF",
  val reviewCardBorder: String = "#F3E2D0",
  val reviewHeadingColor: String = "#7A1C2B",
  val reviewTextColor: String = "#1F2937",
  val reviewBtnBg: String = "#FFF8F0",
  val reviewBtnText: String = "#7A1C2B",
  val reviewBtnBorder: String = "#7A1C2B",
  val cardRadiusPx: Int = 16,
  val buttonRadiusPx: Int = 24,
  val borderWidthPx: Int = 1,
  val cardShadow: String = "md",
  val fontFamily: String = "Sans Serif",
  val titleSize: Int = 20,
  val titleBold: Boolean = true,
  val titleItalic: Boolean = false,
  val descSize: Int = 14,
  val descBold: Boolean = false,
  val descItalic: Boolean = false,
  val headingSize: Int = 13,
  val headingBold: Boolean = true,
  val headingItalic: Boolean = false,
  val buttonSize: Int = 13,
  val buttonBold: Boolean = true,
  val buttonItalic: Boolean = false,
  val tableSize: Int = 12,
  val tableBold: Boolean = false,
  val tableItalic: Boolean = false,
  val descTitle: String = "বিবরণ",
  val specsTableTitle: String = "পণ্যের স্পেসিফিকেশন ও স্টক টেবিল",
  val stockInLabel: String = "স্টক আছে",
  val stockOutLabel: String = "এই ভ্যারিয়েশনটি স্টক আউট",
  val colorLabel: String = "কালার নির্বাচন করুন (Color Family):",
  val sizeLabel: String = "সাইজ:",
  val sizeChartLabel: String = "চার্ট ↗",
  val qtyLabel: String = "পরিমাণ (Quantity):",
  val wishlistSaveLabel: String = "পরে কেনার জন্য সেভ করুন",
  val wishlistSavedLabel: String = "পছন্দের তালিকায় সংরক্ষিত",
  val sizeGuideBtnLabel: String = "📏 সাইজ গাইড",
  val qrBtnLabel: String = "📷 QR কোড",
  val addToCartLabel: String = "কার্টে যোগ করুন",
  val reviewsTitle: String = "গ্রাহক রিভিউ ও রেটিং (Customer Reviews)",
  val writeReviewBtnLabel: String = "একটি রিভিউ লিখুন",
  val sectionOrder: List<String> = listOf(
    "price", "offers", "description", "specs_table", "variants", "quantity", "actions", "reviews"
  ),
  val hiddenSections: List<String> = emptyList()
)

data class AddProductPageConfig(
  val pageBgColor: String = "#F4F6F9",
  val topBarBgColor: String = "#FFFFFF",
  val topBarTextColor: String = "#0F172A",
  val blockBgColor: String = "#FFFFFF",
  val blockBorderColor: String = "#E2E8F0",
  val accentColor: String = "#F97316",
  val labelTextColor: String = "#0F172A",
  val subTextColor: String = "#64748B",
  val inputBgColor: String = "#F8FAFC",
  val inputBorderColor: String = "#CBD5E1",
  val inputTextColor: String = "#0F172A",
  val tableHeaderBg: String = "#0F172A",
  val tableHeaderText: String = "#FFFFFF",
  val tableBgColor: String = "#FFFFFF",
  val tableTextColor: String = "#0F172A",
  val tableBorderColor: String = "#CBD5E1",
  val publishBtnBg: String = "#F97316",
  val publishBtnText: String = "#FFFFFF",
  val draftBtnBg: String = "#FFFFFF",
  val draftBtnText: String = "#F97316",
  val draftBtnBorder: String = "#F97316",
  val fontFamily: String = "Sans Serif",
  val headingSize: Int = 17,
  val labelSize: Int = 14,
  val labelBold: Boolean = true,
  val labelItalic: Boolean = false,
  val inputSize: Int = 13,
  val inputBold: Boolean = false,
  val inputItalic: Boolean = false,
  val buttonSize: Int = 14,
  val buttonBold: Boolean = true,
  val cardRadiusPx: Int = 16,
  val borderWidthPx: Int = 1,
  val addTitle: String = "Add Product",
  val editTitle: String = "Edit Product",
  val imageLabel: String = "Product Image",
  val nameLabel: String = "প্রোডাক্টের নাম / Product Name",
  val categoryLabel: String = "Category",
  val videoLabel: String = "Video",
  val brandLabel: String = "Brand",
  val attributesLabel: String = "Attributes",
  val variantsLabel: String = "Variants",
  val priceLabel: String = "Price(BDT)",
  val stockLabel: String = "Stock",
  val availabilityLabel: String = "Availability",
  val specialPriceLabel: String = "Special Price",
  val shippingLabel: String = "Shipping Information",
  val warrantyLabel: String = "Warranty Information",
  val descriptionLabel: String = "পণ্যের বিবরণ (Description)",
  val highlightsLabel: String = "Highlights",
  val publishBtnLabel: String = "Publish Product",
  val draftBtnLabel: String = "Save as Draft",
  val blockOrder: List<String> = listOf(
    "media_identity", "details_specs", "variants_pricing", "shipping_warranty", "description_highlights"
  ),
  val hiddenBlocks: List<String> = emptyList(),
  val enableAiHelper: Boolean = true,
  val showChannelsStrip: Boolean = true
)

data class MasterPageConfig(
  val globalFontFamily: String = "Sans Serif",
  val highContrastMode: Boolean = false,
  val showSpecsTableOnDetails: Boolean = true,
  val showStockOnDetails: Boolean = true,
  val showWishlistOnDetails: Boolean = true,
  val showSizeGuideOnDetails: Boolean = true,
  val showQrOnDetails: Boolean = true,
  val showReviewsOnDetails: Boolean = true,
  val cardHoverScaleEnabled: Boolean = true,
  val currencySymbol: String = "৳"
)

data class PageConfiguration(
  val detailPage: DetailPageConfig = DetailPageConfig(),
  val addProductPage: AddProductPageConfig = AddProductPageConfig(),
  val master: MasterPageConfig = MasterPageConfig()
)

object PageConfigJson {
  fun toJsonObject(pc: PageConfiguration): JSONObject {
    val d = pc.detailPage
    val dObj = JSONObject().apply {
      put("backdropColor", d.backdropColor)
      put("modalBgColor", d.modalBgColor)
      put("headerBgColor", d.headerBgColor)
      put("headerTextColor", d.headerTextColor)
      put("skuTextColor", d.skuTextColor)
      put("priceColor", d.priceColor)
      put("regularPriceColor", d.regularPriceColor)
      put("stockInColor", d.stockInColor)
      put("stockOutColor", d.stockOutColor)
      put("descBlockBg", d.descBlockBg)
      put("descBlockBorder", d.descBlockBorder)
      put("descHeadingColor", d.descHeadingColor)
      put("descTextColor", d.descTextColor)
      put("variantBlockBg", d.variantBlockBg)
      put("variantBlockBorder", d.variantBlockBorder)
      put("variantLabelColor", d.variantLabelColor)
      put("variantChipBg", d.variantChipBg)
      put("variantChipText", d.variantChipText)
      put("variantChipBorder", d.variantChipBorder)
      put("variantActiveBg", d.variantActiveBg)
      put("variantActiveText", d.variantActiveText)
      put("variantSizeActiveBg", d.variantSizeActiveBg)
      put("tableHeaderBg", d.tableHeaderBg)
      put("tableHeaderText", d.tableHeaderText)
      put("tableBgColor", d.tableBgColor)
      put("tableAltRowBg", d.tableAltRowBg)
      put("tableTextColor", d.tableTextColor)
      put("tableBorderColor", d.tableBorderColor)
      put("qtyBlockBg", d.qtyBlockBg)
      put("qtyBlockBorder", d.qtyBlockBorder)
      put("qtyBtnBg", d.qtyBtnBg)
      put("qtyBtnText", d.qtyBtnText)
      put("wishlistBtnBg", d.wishlistBtnBg)
      put("wishlistBtnText", d.wishlistBtnText)
      put("wishlistBtnBorder", d.wishlistBtnBorder)
      put("sizeGuideBtnBg", d.sizeGuideBtnBg)
      put("sizeGuideBtnText", d.sizeGuideBtnText)
      put("sizeGuideBtnBorder", d.sizeGuideBtnBorder)
      put("qrBtnBg", d.qrBtnBg)
      put("qrBtnText", d.qrBtnText)
      put("qrBtnBorder", d.qrBtnBorder)
      put("addToCartBtnBg", d.addToCartBtnBg)
      put("addToCartBtnBgEnd", d.addToCartBtnBgEnd)
      put("addToCartBtnText", d.addToCartBtnText)
      put("reviewSectionBg", d.reviewSectionBg)
      put("reviewSectionBorder", d.reviewSectionBorder)
      put("reviewCardBg", d.reviewCardBg)
      put("reviewCardBorder", d.reviewCardBorder)
      put("reviewHeadingColor", d.reviewHeadingColor)
      put("reviewTextColor", d.reviewTextColor)
      put("reviewBtnBg", d.reviewBtnBg)
      put("reviewBtnText", d.reviewBtnText)
      put("reviewBtnBorder", d.reviewBtnBorder)
      put("cardRadiusPx", d.cardRadiusPx)
      put("buttonRadiusPx", d.buttonRadiusPx)
      put("borderWidthPx", d.borderWidthPx)
      put("cardShadow", d.cardShadow)
      put("fontFamily", d.fontFamily)
      put("titleSize", d.titleSize)
      put("titleBold", d.titleBold)
      put("titleItalic", d.titleItalic)
      put("descSize", d.descSize)
      put("descBold", d.descBold)
      put("descItalic", d.descItalic)
      put("headingSize", d.headingSize)
      put("headingBold", d.headingBold)
      put("headingItalic", d.headingItalic)
      put("buttonSize", d.buttonSize)
      put("buttonBold", d.buttonBold)
      put("buttonItalic", d.buttonItalic)
      put("tableSize", d.tableSize)
      put("tableBold", d.tableBold)
      put("tableItalic", d.tableItalic)
      put("descTitle", d.descTitle)
      put("specsTableTitle", d.specsTableTitle)
      put("stockInLabel", d.stockInLabel)
      put("stockOutLabel", d.stockOutLabel)
      put("colorLabel", d.colorLabel)
      put("sizeLabel", d.sizeLabel)
      put("sizeChartLabel", d.sizeChartLabel)
      put("qtyLabel", d.qtyLabel)
      put("wishlistSaveLabel", d.wishlistSaveLabel)
      put("wishlistSavedLabel", d.wishlistSavedLabel)
      put("sizeGuideBtnLabel", d.sizeGuideBtnLabel)
      put("qrBtnLabel", d.qrBtnLabel)
      put("addToCartLabel", d.addToCartLabel)
      put("reviewsTitle", d.reviewsTitle)
      put("writeReviewBtnLabel", d.writeReviewBtnLabel)
      put("sectionOrder", JSONArray(d.sectionOrder))
      put("hiddenSections", JSONArray(d.hiddenSections))
    }

    val a = pc.addProductPage
    val aObj = JSONObject().apply {
      put("pageBgColor", a.pageBgColor)
      put("topBarBgColor", a.topBarBgColor)
      put("topBarTextColor", a.topBarTextColor)
      put("blockBgColor", a.blockBgColor)
      put("blockBorderColor", a.blockBorderColor)
      put("accentColor", a.accentColor)
      put("labelTextColor", a.labelTextColor)
      put("subTextColor", a.subTextColor)
      put("inputBgColor", a.inputBgColor)
      put("inputBorderColor", a.inputBorderColor)
      put("inputTextColor", a.inputTextColor)
      put("tableHeaderBg", a.tableHeaderBg)
      put("tableHeaderText", a.tableHeaderText)
      put("tableBgColor", a.tableBgColor)
      put("tableTextColor", a.tableTextColor)
      put("tableBorderColor", a.tableBorderColor)
      put("publishBtnBg", a.publishBtnBg)
      put("publishBtnText", a.publishBtnText)
      put("draftBtnBg", a.draftBtnBg)
      put("draftBtnText", a.draftBtnText)
      put("draftBtnBorder", a.draftBtnBorder)
      put("fontFamily", a.fontFamily)
      put("headingSize", a.headingSize)
      put("labelSize", a.labelSize)
      put("labelBold", a.labelBold)
      put("labelItalic", a.labelItalic)
      put("inputSize", a.inputSize)
      put("inputBold", a.inputBold)
      put("inputItalic", a.inputItalic)
      put("buttonSize", a.buttonSize)
      put("buttonBold", a.buttonBold)
      put("cardRadiusPx", a.cardRadiusPx)
      put("borderWidthPx", a.borderWidthPx)
      put("addTitle", a.addTitle)
      put("editTitle", a.editTitle)
      put("imageLabel", a.imageLabel)
      put("nameLabel", a.nameLabel)
      put("categoryLabel", a.categoryLabel)
      put("videoLabel", a.videoLabel)
      put("brandLabel", a.brandLabel)
      put("attributesLabel", a.attributesLabel)
      put("variantsLabel", a.variantsLabel)
      put("priceLabel", a.priceLabel)
      put("stockLabel", a.stockLabel)
      put("availabilityLabel", a.availabilityLabel)
      put("specialPriceLabel", a.specialPriceLabel)
      put("shippingLabel", a.shippingLabel)
      put("warrantyLabel", a.warrantyLabel)
      put("descriptionLabel", a.descriptionLabel)
      put("highlightsLabel", a.highlightsLabel)
      put("publishBtnLabel", a.publishBtnLabel)
      put("draftBtnLabel", a.draftBtnLabel)
      put("blockOrder", JSONArray(a.blockOrder))
      put("hiddenBlocks", JSONArray(a.hiddenBlocks))
      put("enableAiHelper", a.enableAiHelper)
      put("showChannelsStrip", a.showChannelsStrip)
    }

    val m = pc.master
    val mObj = JSONObject().apply {
      put("globalFontFamily", m.globalFontFamily)
      put("highContrastMode", m.highContrastMode)
      put("showSpecsTableOnDetails", m.showSpecsTableOnDetails)
      put("showStockOnDetails", m.showStockOnDetails)
      put("showWishlistOnDetails", m.showWishlistOnDetails)
      put("showSizeGuideOnDetails", m.showSizeGuideOnDetails)
      put("showQrOnDetails", m.showQrOnDetails)
      put("showReviewsOnDetails", m.showReviewsOnDetails)
      put("cardHoverScaleEnabled", m.cardHoverScaleEnabled)
      put("currencySymbol", m.currencySymbol)
    }

    return JSONObject().apply {
      put("detailPage", dObj)
      put("addProductPage", aObj)
      put("master", mObj)
    }
  }

  private fun parseStringList(arr: JSONArray?, fallback: List<String>): List<String> {
    if (arr == null) return fallback
    val out = mutableListOf<String>()
    for (i in 0 until arr.length()) {
      val item = arr.optString(i).trim()
      if (item.isNotBlank() && !out.contains(item)) out.add(item)
    }
    return out
  }

  fun fromJsonObject(obj: JSONObject?): PageConfiguration {
    if (obj == null) return PageConfiguration()
    val defD = DetailPageConfig()
    val dObj = obj.optJSONObject("detailPage")
    val detail = if (dObj != null) {
      val rawOrder = parseStringList(dObj.optJSONArray("sectionOrder"), defD.sectionOrder)
      val mergedOrder = (rawOrder + defD.sectionOrder.filterNot { rawOrder.contains(it) }).distinct()
      DetailPageConfig(
        backdropColor = dObj.optString("backdropColor", defD.backdropColor),
        modalBgColor = dObj.optString("modalBgColor", defD.modalBgColor),
        headerBgColor = dObj.optString("headerBgColor", defD.headerBgColor),
        headerTextColor = dObj.optString("headerTextColor", defD.headerTextColor),
        skuTextColor = dObj.optString("skuTextColor", defD.skuTextColor),
        priceColor = dObj.optString("priceColor", defD.priceColor),
        regularPriceColor = dObj.optString("regularPriceColor", defD.regularPriceColor),
        stockInColor = dObj.optString("stockInColor", defD.stockInColor),
        stockOutColor = dObj.optString("stockOutColor", defD.stockOutColor),
        descBlockBg = dObj.optString("descBlockBg", defD.descBlockBg),
        descBlockBorder = dObj.optString("descBlockBorder", defD.descBlockBorder),
        descHeadingColor = dObj.optString("descHeadingColor", defD.descHeadingColor),
        descTextColor = dObj.optString("descTextColor", defD.descTextColor),
        variantBlockBg = dObj.optString("variantBlockBg", defD.variantBlockBg),
        variantBlockBorder = dObj.optString("variantBlockBorder", defD.variantBlockBorder),
        variantLabelColor = dObj.optString("variantLabelColor", defD.variantLabelColor),
        variantChipBg = dObj.optString("variantChipBg", defD.variantChipBg),
        variantChipText = dObj.optString("variantChipText", defD.variantChipText),
        variantChipBorder = dObj.optString("variantChipBorder", defD.variantChipBorder),
        variantActiveBg = dObj.optString("variantActiveBg", defD.variantActiveBg),
        variantActiveText = dObj.optString("variantActiveText", defD.variantActiveText),
        variantSizeActiveBg = dObj.optString("variantSizeActiveBg", defD.variantSizeActiveBg),
        tableHeaderBg = dObj.optString("tableHeaderBg", defD.tableHeaderBg),
        tableHeaderText = dObj.optString("tableHeaderText", defD.tableHeaderText),
        tableBgColor = dObj.optString("tableBgColor", defD.tableBgColor),
        tableAltRowBg = dObj.optString("tableAltRowBg", defD.tableAltRowBg),
        tableTextColor = dObj.optString("tableTextColor", defD.tableTextColor),
        tableBorderColor = dObj.optString("tableBorderColor", defD.tableBorderColor),
        qtyBlockBg = dObj.optString("qtyBlockBg", defD.qtyBlockBg),
        qtyBlockBorder = dObj.optString("qtyBlockBorder", defD.qtyBlockBorder),
        qtyBtnBg = dObj.optString("qtyBtnBg", defD.qtyBtnBg),
        qtyBtnText = dObj.optString("qtyBtnText", defD.qtyBtnText),
        wishlistBtnBg = dObj.optString("wishlistBtnBg", defD.wishlistBtnBg),
        wishlistBtnText = dObj.optString("wishlistBtnText", defD.wishlistBtnText),
        wishlistBtnBorder = dObj.optString("wishlistBtnBorder", defD.wishlistBtnBorder),
        sizeGuideBtnBg = dObj.optString("sizeGuideBtnBg", defD.sizeGuideBtnBg),
        sizeGuideBtnText = dObj.optString("sizeGuideBtnText", defD.sizeGuideBtnText),
        sizeGuideBtnBorder = dObj.optString("sizeGuideBtnBorder", defD.sizeGuideBtnBorder),
        qrBtnBg = dObj.optString("qrBtnBg", defD.qrBtnBg),
        qrBtnText = dObj.optString("qrBtnText", defD.qrBtnText),
        qrBtnBorder = dObj.optString("qrBtnBorder", defD.qrBtnBorder),
        addToCartBtnBg = dObj.optString("addToCartBtnBg", defD.addToCartBtnBg),
        addToCartBtnBgEnd = dObj.optString("addToCartBtnBgEnd", defD.addToCartBtnBgEnd),
        addToCartBtnText = dObj.optString("addToCartBtnText", defD.addToCartBtnText),
        reviewSectionBg = dObj.optString("reviewSectionBg", defD.reviewSectionBg),
        reviewSectionBorder = dObj.optString("reviewSectionBorder", defD.reviewSectionBorder),
        reviewCardBg = dObj.optString("reviewCardBg", defD.reviewCardBg),
        reviewCardBorder = dObj.optString("reviewCardBorder", defD.reviewCardBorder),
        reviewHeadingColor = dObj.optString("reviewHeadingColor", defD.reviewHeadingColor),
        reviewTextColor = dObj.optString("reviewTextColor", defD.reviewTextColor),
        reviewBtnBg = dObj.optString("reviewBtnBg", defD.reviewBtnBg),
        reviewBtnText = dObj.optString("reviewBtnText", defD.reviewBtnText),
        reviewBtnBorder = dObj.optString("reviewBtnBorder", defD.reviewBtnBorder),
        cardRadiusPx = dObj.optInt("cardRadiusPx", defD.cardRadiusPx).coerceIn(0, 32),
        buttonRadiusPx = dObj.optInt("buttonRadiusPx", defD.buttonRadiusPx).coerceIn(0, 50),
        borderWidthPx = dObj.optInt("borderWidthPx", defD.borderWidthPx).coerceIn(0, 4),
        cardShadow = dObj.optString("cardShadow", defD.cardShadow),
        fontFamily = dObj.optString("fontFamily", defD.fontFamily),
        titleSize = dObj.optInt("titleSize", defD.titleSize).coerceIn(12, 32),
        titleBold = dObj.optBoolean("titleBold", defD.titleBold),
        titleItalic = dObj.optBoolean("titleItalic", defD.titleItalic),
        descSize = dObj.optInt("descSize", defD.descSize).coerceIn(10, 24),
        descBold = dObj.optBoolean("descBold", defD.descBold),
        descItalic = dObj.optBoolean("descItalic", defD.descItalic),
        headingSize = dObj.optInt("headingSize", defD.headingSize).coerceIn(10, 24),
        headingBold = dObj.optBoolean("headingBold", defD.headingBold),
        headingItalic = dObj.optBoolean("headingItalic", defD.headingItalic),
        buttonSize = dObj.optInt("buttonSize", defD.buttonSize).coerceIn(10, 22),
        buttonBold = dObj.optBoolean("buttonBold", defD.buttonBold),
        buttonItalic = dObj.optBoolean("buttonItalic", defD.buttonItalic),
        tableSize = dObj.optInt("tableSize", defD.tableSize).coerceIn(9, 20),
        tableBold = dObj.optBoolean("tableBold", defD.tableBold),
        tableItalic = dObj.optBoolean("tableItalic", defD.tableItalic),
        descTitle = dObj.optString("descTitle", defD.descTitle),
        specsTableTitle = dObj.optString("specsTableTitle", defD.specsTableTitle),
        stockInLabel = dObj.optString("stockInLabel", defD.stockInLabel),
        stockOutLabel = dObj.optString("stockOutLabel", defD.stockOutLabel),
        colorLabel = dObj.optString("colorLabel", defD.colorLabel),
        sizeLabel = dObj.optString("sizeLabel", defD.sizeLabel),
        sizeChartLabel = dObj.optString("sizeChartLabel", defD.sizeChartLabel),
        qtyLabel = dObj.optString("qtyLabel", defD.qtyLabel),
        wishlistSaveLabel = dObj.optString("wishlistSaveLabel", defD.wishlistSaveLabel),
        wishlistSavedLabel = dObj.optString("wishlistSavedLabel", defD.wishlistSavedLabel),
        sizeGuideBtnLabel = dObj.optString("sizeGuideBtnLabel", defD.sizeGuideBtnLabel),
        qrBtnLabel = dObj.optString("qrBtnLabel", defD.qrBtnLabel),
        addToCartLabel = dObj.optString("addToCartLabel", defD.addToCartLabel),
        reviewsTitle = dObj.optString("reviewsTitle", defD.reviewsTitle),
        writeReviewBtnLabel = dObj.optString("writeReviewBtnLabel", defD.writeReviewBtnLabel),
        sectionOrder = mergedOrder,
        hiddenSections = parseStringList(dObj.optJSONArray("hiddenSections"), defD.hiddenSections)
      )
    } else defD

    val defA = AddProductPageConfig()
    val aObj = obj.optJSONObject("addProductPage")
    val addProd = if (aObj != null) {
      val rawBlocks = parseStringList(aObj.optJSONArray("blockOrder"), defA.blockOrder)
      val mergedBlocks = (rawBlocks + defA.blockOrder.filterNot { rawBlocks.contains(it) }).distinct()
      AddProductPageConfig(
        pageBgColor = aObj.optString("pageBgColor", defA.pageBgColor),
        topBarBgColor = aObj.optString("topBarBgColor", defA.topBarBgColor),
        topBarTextColor = aObj.optString("topBarTextColor", defA.topBarTextColor),
        blockBgColor = aObj.optString("blockBgColor", defA.blockBgColor),
        blockBorderColor = aObj.optString("blockBorderColor", defA.blockBorderColor),
        accentColor = aObj.optString("accentColor", defA.accentColor),
        labelTextColor = aObj.optString("labelTextColor", defA.labelTextColor),
        subTextColor = aObj.optString("subTextColor", defA.subTextColor),
        inputBgColor = aObj.optString("inputBgColor", defA.inputBgColor),
        inputBorderColor = aObj.optString("inputBorderColor", defA.inputBorderColor),
        inputTextColor = aObj.optString("inputTextColor", defA.inputTextColor),
        tableHeaderBg = aObj.optString("tableHeaderBg", defA.tableHeaderBg),
        tableHeaderText = aObj.optString("tableHeaderText", defA.tableHeaderText),
        tableBgColor = aObj.optString("tableBgColor", defA.tableBgColor),
        tableTextColor = aObj.optString("tableTextColor", defA.tableTextColor),
        tableBorderColor = aObj.optString("tableBorderColor", defA.tableBorderColor),
        publishBtnBg = aObj.optString("publishBtnBg", defA.publishBtnBg),
        publishBtnText = aObj.optString("publishBtnText", defA.publishBtnText),
        draftBtnBg = aObj.optString("draftBtnBg", defA.draftBtnBg),
        draftBtnText = aObj.optString("draftBtnText", defA.draftBtnText),
        draftBtnBorder = aObj.optString("draftBtnBorder", defA.draftBtnBorder),
        fontFamily = aObj.optString("fontFamily", defA.fontFamily),
        headingSize = aObj.optInt("headingSize", defA.headingSize).coerceIn(12, 26),
        labelSize = aObj.optInt("labelSize", defA.labelSize).coerceIn(10, 22),
        labelBold = aObj.optBoolean("labelBold", defA.labelBold),
        labelItalic = aObj.optBoolean("labelItalic", defA.labelItalic),
        inputSize = aObj.optInt("inputSize", defA.inputSize).coerceIn(10, 20),
        inputBold = aObj.optBoolean("inputBold", defA.inputBold),
        inputItalic = aObj.optBoolean("inputItalic", defA.inputItalic),
        buttonSize = aObj.optInt("buttonSize", defA.buttonSize).coerceIn(10, 22),
        buttonBold = aObj.optBoolean("buttonBold", defA.buttonBold),
        cardRadiusPx = aObj.optInt("cardRadiusPx", defA.cardRadiusPx).coerceIn(0, 32),
        borderWidthPx = aObj.optInt("borderWidthPx", defA.borderWidthPx).coerceIn(0, 4),
        addTitle = aObj.optString("addTitle", defA.addTitle),
        editTitle = aObj.optString("editTitle", defA.editTitle),
        imageLabel = aObj.optString("imageLabel", defA.imageLabel),
        nameLabel = aObj.optString("nameLabel", defA.nameLabel),
        categoryLabel = aObj.optString("categoryLabel", defA.categoryLabel),
        videoLabel = aObj.optString("videoLabel", defA.videoLabel),
        brandLabel = aObj.optString("brandLabel", defA.brandLabel),
        attributesLabel = aObj.optString("attributesLabel", defA.attributesLabel),
        variantsLabel = aObj.optString("variantsLabel", defA.variantsLabel),
        priceLabel = aObj.optString("priceLabel", defA.priceLabel),
        stockLabel = aObj.optString("stockLabel", defA.stockLabel),
        availabilityLabel = aObj.optString("availabilityLabel", defA.availabilityLabel),
        specialPriceLabel = aObj.optString("specialPriceLabel", defA.specialPriceLabel),
        shippingLabel = aObj.optString("shippingLabel", defA.shippingLabel),
        warrantyLabel = aObj.optString("warrantyLabel", defA.warrantyLabel),
        descriptionLabel = aObj.optString("descriptionLabel", defA.descriptionLabel),
        highlightsLabel = aObj.optString("highlightsLabel", defA.highlightsLabel),
        publishBtnLabel = aObj.optString("publishBtnLabel", defA.publishBtnLabel),
        draftBtnLabel = aObj.optString("draftBtnLabel", defA.draftBtnLabel),
        blockOrder = mergedBlocks,
        hiddenBlocks = parseStringList(aObj.optJSONArray("hiddenBlocks"), defA.hiddenBlocks),
        enableAiHelper = aObj.optBoolean("enableAiHelper", defA.enableAiHelper),
        showChannelsStrip = aObj.optBoolean("showChannelsStrip", defA.showChannelsStrip)
      )
    } else defA

    val defM = MasterPageConfig()
    val mObj = obj.optJSONObject("master")
    val master = if (mObj != null) {
      MasterPageConfig(
        globalFontFamily = mObj.optString("globalFontFamily", defM.globalFontFamily),
        highContrastMode = mObj.optBoolean("highContrastMode", defM.highContrastMode),
        showSpecsTableOnDetails = mObj.optBoolean("showSpecsTableOnDetails", defM.showSpecsTableOnDetails),
        showStockOnDetails = mObj.optBoolean("showStockOnDetails", defM.showStockOnDetails),
        showWishlistOnDetails = mObj.optBoolean("showWishlistOnDetails", defM.showWishlistOnDetails),
        showSizeGuideOnDetails = mObj.optBoolean("showSizeGuideOnDetails", defM.showSizeGuideOnDetails),
        showQrOnDetails = mObj.optBoolean("showQrOnDetails", defM.showQrOnDetails),
        showReviewsOnDetails = mObj.optBoolean("showReviewsOnDetails", defM.showReviewsOnDetails),
        cardHoverScaleEnabled = mObj.optBoolean("cardHoverScaleEnabled", defM.cardHoverScaleEnabled),
        currencySymbol = mObj.optString("currencySymbol", defM.currencySymbol).ifBlank { "৳" }
      )
    } else defM

    return PageConfiguration(
      detailPage = detail,
      addProductPage = addProd,
      master = master
    )
  }
}
