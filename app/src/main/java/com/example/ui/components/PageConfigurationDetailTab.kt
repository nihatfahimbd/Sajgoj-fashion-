package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DetailPageConfig

private enum class DetailConfigGroup(val label: String) {
  COLORS_BG("Colors & BG"),
  BLOCKS_TABLES("Blocks & Tables"),
  TYPOGRAPHY("Typography"),
  RENAME("Rename Titles"),
  ORDER_VIS("Order & Visibility")
}

private val DETAIL_SECTION_LABELS = mapOf(
  "price" to "মূল্য ও স্টক (Price & Stock)",
  "offers" to "স্পেশাল অফার (B2G1 Banner)",
  "description" to "পণ্যের বিবরণ ব্লক (Description)",
  "specs_table" to "স্পেসিফিকেশন ও স্টক টেবিল (Table)",
  "variants" to "কালার, সাইজ ও বয়স (Variants)",
  "quantity" to "পরিমাণ ও স্টক (Quantity)",
  "actions" to "বাটন ও QR (Actions & Cart)",
  "reviews" to "গ্রাহক রিভিউ (Customer Reviews)"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailPageConfigControls(
  dp: DetailPageConfig,
  onUpdate: (DetailPageConfig) -> Unit
) {
  var activeGroup by remember { mutableStateOf(DetailConfigGroup.COLORS_BG) }

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    // Sub-group Filter Strip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      DetailConfigGroup.entries.forEach { grp ->
        val sel = grp == activeGroup
        Surface(
          shape = RoundedCornerShape(50),
          color = if (sel) AdminV2CyanDeep else Color(0xFFF1F5F9),
          border = BorderStroke(1.dp, if (sel) AdminV2CyanDeep else Color(0xFFCBD5E1)),
          modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { activeGroup = grp }
        ) {
          Text(
            text = grp.label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (sel) Color.White else AdminInkPrimary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
          )
        }
      }
    }

    when (activeGroup) {
      DetailConfigGroup.COLORS_BG -> {
        PageColorHexPickerRow("Modal Backdrop Color", dp.backdropColor, "dp_backdrop") { onUpdate(dp.copy(backdropColor = it)) }
        PageColorHexPickerRow("Modal Background", dp.modalBgColor, "dp_modal_bg") { onUpdate(dp.copy(modalBgColor = it)) }
        PageColorHexPickerRow("Header Background", dp.headerBgColor, "dp_header_bg") { onUpdate(dp.copy(headerBgColor = it)) }
        PageColorHexPickerRow("Header / Title Text", dp.headerTextColor, "dp_header_txt") { onUpdate(dp.copy(headerTextColor = it)) }
        PageColorHexPickerRow("SKU & Category Ink", dp.skuTextColor, "dp_sku_txt") { onUpdate(dp.copy(skuTextColor = it)) }
        PageColorHexPickerRow("Sell Price Color", dp.priceColor, "dp_price_color") { onUpdate(dp.copy(priceColor = it)) }
        PageColorHexPickerRow("Regular Price Strike", dp.regularPriceColor, "dp_reg_price") { onUpdate(dp.copy(regularPriceColor = it)) }
        PageColorHexPickerRow("In-Stock Text Color", dp.stockInColor, "dp_stock_in") { onUpdate(dp.copy(stockInColor = it)) }
        PageColorHexPickerRow("Out-of-Stock Color", dp.stockOutColor, "dp_stock_out") { onUpdate(dp.copy(stockOutColor = it)) }
        PageColorHexPickerRow("Description Block BG", dp.descBlockBg, "dp_desc_bg") { onUpdate(dp.copy(descBlockBg = it)) }
        PageColorHexPickerRow("Description Border", dp.descBlockBorder, "dp_desc_bdr") { onUpdate(dp.copy(descBlockBorder = it)) }
        PageColorHexPickerRow("Description Heading", dp.descHeadingColor, "dp_desc_head") { onUpdate(dp.copy(descHeadingColor = it)) }
        PageColorHexPickerRow("Description Text Color", dp.descTextColor, "dp_desc_txt") { onUpdate(dp.copy(descTextColor = it)) }
      }

      DetailConfigGroup.BLOCKS_TABLES -> {
        PageColorHexPickerRow("Variant Block BG", dp.variantBlockBg, "dp_var_bg") { onUpdate(dp.copy(variantBlockBg = it)) }
        PageColorHexPickerRow("Variant Block Border", dp.variantBlockBorder, "dp_var_bdr") { onUpdate(dp.copy(variantBlockBorder = it)) }
        PageColorHexPickerRow("Variant Label Color", dp.variantLabelColor, "dp_var_lbl") { onUpdate(dp.copy(variantLabelColor = it)) }
        PageColorHexPickerRow("Variant Chip BG", dp.variantChipBg, "dp_chip_bg") { onUpdate(dp.copy(variantChipBg = it)) }
        PageColorHexPickerRow("Variant Chip Text", dp.variantChipText, "dp_chip_txt") { onUpdate(dp.copy(variantChipText = it)) }
        PageColorHexPickerRow("Variant Chip Border", dp.variantChipBorder, "dp_chip_bdr") { onUpdate(dp.copy(variantChipBorder = it)) }
        PageColorHexPickerRow("Variant Active BG", dp.variantActiveBg, "dp_var_act_bg") { onUpdate(dp.copy(variantActiveBg = it)) }
        PageColorHexPickerRow("Variant Active Text", dp.variantActiveText, "dp_var_act_txt") { onUpdate(dp.copy(variantActiveText = it)) }
        PageColorHexPickerRow("Size Active Accent", dp.variantSizeActiveBg, "dp_size_act") { onUpdate(dp.copy(variantSizeActiveBg = it)) }

        PageColorHexPickerRow("Table Header BG", dp.tableHeaderBg, "dp_tbl_hdr_bg") { onUpdate(dp.copy(tableHeaderBg = it)) }
        PageColorHexPickerRow("Table Header Text", dp.tableHeaderText, "dp_tbl_hdr_txt") { onUpdate(dp.copy(tableHeaderText = it)) }
        PageColorHexPickerRow("Table Row BG", dp.tableBgColor, "dp_tbl_bg") { onUpdate(dp.copy(tableBgColor = it)) }
        PageColorHexPickerRow("Table Alt Row BG", dp.tableAltRowBg, "dp_tbl_alt") { onUpdate(dp.copy(tableAltRowBg = it)) }
        PageColorHexPickerRow("Table Text Color", dp.tableTextColor, "dp_tbl_txt") { onUpdate(dp.copy(tableTextColor = it)) }
        PageColorHexPickerRow("Table Border Color", dp.tableBorderColor, "dp_tbl_bdr") { onUpdate(dp.copy(tableBorderColor = it)) }

        PageColorHexPickerRow("Qty Box BG", dp.qtyBlockBg, "dp_qty_bg") { onUpdate(dp.copy(qtyBlockBg = it)) }
        PageColorHexPickerRow("Qty Box Border", dp.qtyBlockBorder, "dp_qty_bdr") { onUpdate(dp.copy(qtyBlockBorder = it)) }
        PageColorHexPickerRow("Qty Stepper Button BG", dp.qtyBtnBg, "dp_qty_btn_bg") { onUpdate(dp.copy(qtyBtnBg = it)) }
        PageColorHexPickerRow("Qty Stepper Text", dp.qtyBtnText, "dp_qty_btn_txt") { onUpdate(dp.copy(qtyBtnText = it)) }

        PageColorHexPickerRow("QR Button BG", dp.qrBtnBg, "dp_qr_bg") { onUpdate(dp.copy(qrBtnBg = it)) }
        PageColorHexPickerRow("QR Button Text", dp.qrBtnText, "dp_qr_txt") { onUpdate(dp.copy(qrBtnText = it)) }
        PageColorHexPickerRow("QR Button Border", dp.qrBtnBorder, "dp_qr_bdr") { onUpdate(dp.copy(qrBtnBorder = it)) }

        PageColorHexPickerRow("Add To Cart BG Start", dp.addToCartBtnBg, "dp_atc_bg1") { onUpdate(dp.copy(addToCartBtnBg = it)) }
        PageColorHexPickerRow("Add To Cart BG End", dp.addToCartBtnBgEnd, "dp_atc_bg2") { onUpdate(dp.copy(addToCartBtnBgEnd = it)) }
        PageColorHexPickerRow("Add To Cart Text", dp.addToCartBtnText, "dp_atc_txt") { onUpdate(dp.copy(addToCartBtnText = it)) }

        PageColorHexPickerRow("Review Section BG", dp.reviewSectionBg, "dp_rev_sec_bg") { onUpdate(dp.copy(reviewSectionBg = it)) }
        PageColorHexPickerRow("Review Section Border", dp.reviewSectionBorder, "dp_rev_sec_bdr") { onUpdate(dp.copy(reviewSectionBorder = it)) }
        PageColorHexPickerRow("Review Card BG", dp.reviewCardBg, "dp_rev_card_bg") { onUpdate(dp.copy(reviewCardBg = it)) }
        PageColorHexPickerRow("Review Card Border", dp.reviewCardBorder, "dp_rev_card_bdr") { onUpdate(dp.copy(reviewCardBorder = it)) }
        PageColorHexPickerRow("Review Heading Color", dp.reviewHeadingColor, "dp_rev_head") { onUpdate(dp.copy(reviewHeadingColor = it)) }
        PageColorHexPickerRow("Review Text Color", dp.reviewTextColor, "dp_rev_txt") { onUpdate(dp.copy(reviewTextColor = it)) }
        PageColorHexPickerRow("Review Button BG", dp.reviewBtnBg, "dp_rev_btn_bg") { onUpdate(dp.copy(reviewBtnBg = it)) }
        PageColorHexPickerRow("Review Button Text", dp.reviewBtnText, "dp_rev_btn_txt") { onUpdate(dp.copy(reviewBtnText = it)) }
        PageColorHexPickerRow("Review Button Border", dp.reviewBtnBorder, "dp_rev_btn_bdr") { onUpdate(dp.copy(reviewBtnBorder = it)) }

        PageIntSliderRow("Card Corner Radius", dp.cardRadiusPx, 0..32) { onUpdate(dp.copy(cardRadiusPx = it)) }
        PageIntSliderRow("Button Corner Radius", dp.buttonRadiusPx, 0..40) { onUpdate(dp.copy(buttonRadiusPx = it)) }
        PageIntSliderRow("Block Border Width", dp.borderWidthPx, 0..4) { onUpdate(dp.copy(borderWidthPx = it)) }
      }

      DetailConfigGroup.TYPOGRAPHY -> {
        Text("ফন্ট ফ্যামিলি (Font Family):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          PAGE_FONT_CHOICES.forEach { (key, label) ->
            val sel = dp.fontFamily.equals(key, ignoreCase = true)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (sel) AdminV2CyanDeep else Color(0xFFF8FAFC),
              border = BorderStroke(1.dp, if (sel) AdminV2CyanDeep else Color(0xFFCBD5E1)),
              modifier = Modifier.clickable { onUpdate(dp.copy(fontFamily = key)) }
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (sel) Color.White else AdminInkPrimary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
        PageTextStyleControlRow(
          title = "Title Font",
          size = dp.titleSize,
          sizeRange = 12..30,
          isBold = dp.titleBold,
          isItalic = dp.titleItalic,
          onSizeChange = { onUpdate(dp.copy(titleSize = it)) },
          onBoldChange = { onUpdate(dp.copy(titleBold = it)) },
          onItalicChange = { onUpdate(dp.copy(titleItalic = it)) }
        )
        PageTextStyleControlRow(
          title = "Section Headings",
          size = dp.headingSize,
          sizeRange = 10..24,
          isBold = dp.headingBold,
          isItalic = dp.headingItalic,
          onSizeChange = { onUpdate(dp.copy(headingSize = it)) },
          onBoldChange = { onUpdate(dp.copy(headingBold = it)) },
          onItalicChange = { onUpdate(dp.copy(headingItalic = it)) }
        )
        PageTextStyleControlRow(
          title = "Description Text",
          size = dp.descSize,
          sizeRange = 10..24,
          isBold = dp.descBold,
          isItalic = dp.descItalic,
          onSizeChange = { onUpdate(dp.copy(descSize = it)) },
          onBoldChange = { onUpdate(dp.copy(descBold = it)) },
          onItalicChange = { onUpdate(dp.copy(descItalic = it)) }
        )
        PageTextStyleControlRow(
          title = "Buttons Label",
          size = dp.buttonSize,
          sizeRange = 10..22,
          isBold = dp.buttonBold,
          isItalic = dp.buttonItalic,
          onSizeChange = { onUpdate(dp.copy(buttonSize = it)) },
          onBoldChange = { onUpdate(dp.copy(buttonBold = it)) },
          onItalicChange = { onUpdate(dp.copy(buttonItalic = it)) }
        )
        PageTextStyleControlRow(
          title = "Specs Table Text",
          size = dp.tableSize,
          sizeRange = 9..20,
          isBold = dp.tableBold,
          isItalic = dp.tableItalic,
          onSizeChange = { onUpdate(dp.copy(tableSize = it)) },
          onBoldChange = { onUpdate(dp.copy(tableBold = it)) },
          onItalicChange = { onUpdate(dp.copy(tableItalic = it)) }
        )
      }

      DetailConfigGroup.RENAME -> {
        PageRenameInputRow("Description Heading", dp.descTitle) { onUpdate(dp.copy(descTitle = it)) }
        PageRenameInputRow("Specs Table Title", dp.specsTableTitle) { onUpdate(dp.copy(specsTableTitle = it)) }
        PageRenameInputRow("In-Stock Label", dp.stockInLabel) { onUpdate(dp.copy(stockInLabel = it)) }
        PageRenameInputRow("Out-of-Stock Label", dp.stockOutLabel) { onUpdate(dp.copy(stockOutLabel = it)) }
        PageRenameInputRow("Color Picker Heading", dp.colorLabel) { onUpdate(dp.copy(colorLabel = it)) }
        PageRenameInputRow("Size Picker Heading", dp.sizeLabel) { onUpdate(dp.copy(sizeLabel = it)) }
        PageRenameInputRow("Quantity Label", dp.qtyLabel) { onUpdate(dp.copy(qtyLabel = it)) }
        PageRenameInputRow("QR Button Text", dp.qrBtnLabel) { onUpdate(dp.copy(qrBtnLabel = it)) }
        PageRenameInputRow("Add To Cart Button Text", dp.addToCartLabel) { onUpdate(dp.copy(addToCartLabel = it)) }
        PageRenameInputRow("Customer Reviews Title", dp.reviewsTitle) { onUpdate(dp.copy(reviewsTitle = it)) }
        PageRenameInputRow("Write Review Button Text", dp.writeReviewBtnLabel) { onUpdate(dp.copy(writeReviewBtnLabel = it)) }
      }

      DetailConfigGroup.ORDER_VIS -> {
        Text("সেকশন পজিশন পরিবর্তন ও দৃশ্যমানতা (Reorder & Toggle):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
        dp.sectionOrder.forEachIndexed { idx, secKey ->
          val isHidden = dp.hiddenSections.contains(secKey)
          PageOrderItemRow(
            title = "${idx + 1}. ${DETAIL_SECTION_LABELS[secKey] ?: secKey}",
            isHidden = isHidden,
            canMoveUp = idx > 0,
            canMoveDown = idx < dp.sectionOrder.lastIndex,
            onMoveUp = {
              val next = dp.sectionOrder.toMutableList()
              val tmp = next[idx - 1]
              next[idx - 1] = next[idx]
              next[idx] = tmp
              onUpdate(dp.copy(sectionOrder = next))
            },
            onMoveDown = {
              val next = dp.sectionOrder.toMutableList()
              val tmp = next[idx + 1]
              next[idx + 1] = next[idx]
              next[idx] = tmp
              onUpdate(dp.copy(sectionOrder = next))
            },
            onToggleVisibility = {
              val nextHidden = if (isHidden) dp.hiddenSections - secKey else (dp.hiddenSections + secKey).distinct()
              onUpdate(dp.copy(hiddenSections = nextHidden))
            }
          )
        }
      }
    }
  }
}
