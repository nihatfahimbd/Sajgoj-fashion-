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
import com.example.data.model.AddProductPageConfig

private enum class AddProdConfigGroup(val label: String) {
  COLORS_BG("Colors & BG"),
  INPUTS_TABLES("Inputs & Tables"),
  TYPOGRAPHY("Typography"),
  RENAME("Rename Labels"),
  ORDER_TOGGLES("Order & Toggles")
}

private val ADD_PROD_BLOCK_LABELS = mapOf(
  "media_identity" to "ছবি, নাম, ক্যাটাগরি ও ভিডিও (Media & Name)",
  "details_specs" to "ব্র্যান্ড ও অ্যাট্রিবিউটস (Brand & Attributes)",
  "variants_pricing" to "ভ্যারিয়েন্ট, মূল্য ও স্টক (Variants & Pricing)",
  "shipping_warranty" to "ডেলিভারি ও ওয়ারেন্টি (Shipping & Warranty)",
  "description_highlights" to "বিবরণ ও হাইলাইটস (Description & Highlights)"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddProductPageConfigControls(
  ap: AddProductPageConfig,
  onUpdate: (AddProductPageConfig) -> Unit
) {
  var activeGroup by remember { mutableStateOf(AddProdConfigGroup.COLORS_BG) }

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      AddProdConfigGroup.entries.forEach { grp ->
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
      AddProdConfigGroup.COLORS_BG -> {
        PageColorHexPickerRow("Page Canvas BG", ap.pageBgColor, "ap_page_bg") { onUpdate(ap.copy(pageBgColor = it)) }
        PageColorHexPickerRow("Top Bar BG", ap.topBarBgColor, "ap_topbar_bg") { onUpdate(ap.copy(topBarBgColor = it)) }
        PageColorHexPickerRow("Top Bar Title Color", ap.topBarTextColor, "ap_topbar_txt") { onUpdate(ap.copy(topBarTextColor = it)) }
        PageColorHexPickerRow("Block Card BG", ap.blockBgColor, "ap_blk_bg") { onUpdate(ap.copy(blockBgColor = it)) }
        PageColorHexPickerRow("Block Card Border", ap.blockBorderColor, "ap_blk_bdr") { onUpdate(ap.copy(blockBorderColor = it)) }
        PageColorHexPickerRow("Primary Accent Color", ap.accentColor, "ap_accent") { onUpdate(ap.copy(accentColor = it)) }
        PageColorHexPickerRow("Field Label Color", ap.labelTextColor, "ap_lbl_txt") { onUpdate(ap.copy(labelTextColor = it)) }
        PageColorHexPickerRow("Helper / Subtext Color", ap.subTextColor, "ap_sub_txt") { onUpdate(ap.copy(subTextColor = it)) }
      }

      AddProdConfigGroup.INPUTS_TABLES -> {
        PageColorHexPickerRow("Input Field BG", ap.inputBgColor, "ap_in_bg") { onUpdate(ap.copy(inputBgColor = it)) }
        PageColorHexPickerRow("Input Field Border", ap.inputBorderColor, "ap_in_bdr") { onUpdate(ap.copy(inputBorderColor = it)) }
        PageColorHexPickerRow("Input Text Color", ap.inputTextColor, "ap_in_txt") { onUpdate(ap.copy(inputTextColor = it)) }
        PageColorHexPickerRow("Variant Table Header BG", ap.tableHeaderBg, "ap_tbl_hdr_bg") { onUpdate(ap.copy(tableHeaderBg = it)) }
        PageColorHexPickerRow("Variant Table Header Text", ap.tableHeaderText, "ap_tbl_hdr_txt") { onUpdate(ap.copy(tableHeaderText = it)) }
        PageColorHexPickerRow("Variant Table BG", ap.tableBgColor, "ap_tbl_bg") { onUpdate(ap.copy(tableBgColor = it)) }
        PageColorHexPickerRow("Variant Table Text", ap.tableTextColor, "ap_tbl_txt") { onUpdate(ap.copy(tableTextColor = it)) }
        PageColorHexPickerRow("Variant Table Border", ap.tableBorderColor, "ap_tbl_bdr") { onUpdate(ap.copy(tableBorderColor = it)) }
        PageColorHexPickerRow("Publish Button BG", ap.publishBtnBg, "ap_pub_bg") { onUpdate(ap.copy(publishBtnBg = it)) }
        PageColorHexPickerRow("Publish Button Text", ap.publishBtnText, "ap_pub_txt") { onUpdate(ap.copy(publishBtnText = it)) }
        PageColorHexPickerRow("Draft Button BG", ap.draftBtnBg, "ap_drf_bg") { onUpdate(ap.copy(draftBtnBg = it)) }
        PageColorHexPickerRow("Draft Button Text", ap.draftBtnText, "ap_drf_txt") { onUpdate(ap.copy(draftBtnText = it)) }
        PageColorHexPickerRow("Draft Button Border", ap.draftBtnBorder, "ap_drf_bdr") { onUpdate(ap.copy(draftBtnBorder = it)) }
        PageIntSliderRow("Block Corner Radius", ap.cardRadiusPx, 0..32) { onUpdate(ap.copy(cardRadiusPx = it)) }
        PageIntSliderRow("Block Border Width", ap.borderWidthPx, 0..4) { onUpdate(ap.copy(borderWidthPx = it)) }
      }

      AddProdConfigGroup.TYPOGRAPHY -> {
        Text("ফন্ট ফ্যামিলি (Font Family):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          PAGE_FONT_CHOICES.forEach { (key, label) ->
            val sel = ap.fontFamily.equals(key, ignoreCase = true)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (sel) AdminV2CyanDeep else Color(0xFFF8FAFC),
              border = BorderStroke(1.dp, if (sel) AdminV2CyanDeep else Color(0xFFCBD5E1)),
              modifier = Modifier.clickable { onUpdate(ap.copy(fontFamily = key)) }
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
        PageIntSliderRow("Top Bar Heading Size", ap.headingSize, 12..26, "sp") { onUpdate(ap.copy(headingSize = it)) }
        PageTextStyleControlRow(
          title = "Field Labels",
          size = ap.labelSize,
          sizeRange = 10..22,
          isBold = ap.labelBold,
          isItalic = ap.labelItalic,
          onSizeChange = { onUpdate(ap.copy(labelSize = it)) },
          onBoldChange = { onUpdate(ap.copy(labelBold = it)) },
          onItalicChange = { onUpdate(ap.copy(labelItalic = it)) }
        )
        PageTextStyleControlRow(
          title = "Input Fields",
          size = ap.inputSize,
          sizeRange = 10..20,
          isBold = ap.inputBold,
          isItalic = ap.inputItalic,
          onSizeChange = { onUpdate(ap.copy(inputSize = it)) },
          onBoldChange = { onUpdate(ap.copy(inputBold = it)) },
          onItalicChange = { onUpdate(ap.copy(inputItalic = it)) }
        )
        PageIntSliderRow("Bottom Action Buttons Size", ap.buttonSize, 10..22, "sp") { onUpdate(ap.copy(buttonSize = it)) }
      }

      AddProdConfigGroup.RENAME -> {
        PageRenameInputRow("Add Mode Page Title", ap.addTitle) { onUpdate(ap.copy(addTitle = it)) }
        PageRenameInputRow("Edit Mode Page Title", ap.editTitle) { onUpdate(ap.copy(editTitle = it)) }
        PageRenameInputRow("Image Field Label", ap.imageLabel) { onUpdate(ap.copy(imageLabel = it)) }
        PageRenameInputRow("Product Name Label", ap.nameLabel) { onUpdate(ap.copy(nameLabel = it)) }
        PageRenameInputRow("Category Label", ap.categoryLabel) { onUpdate(ap.copy(categoryLabel = it)) }
        PageRenameInputRow("Video Label", ap.videoLabel) { onUpdate(ap.copy(videoLabel = it)) }
        PageRenameInputRow("Brand Label", ap.brandLabel) { onUpdate(ap.copy(brandLabel = it)) }
        PageRenameInputRow("Attributes Label", ap.attributesLabel) { onUpdate(ap.copy(attributesLabel = it)) }
        PageRenameInputRow("Variants Label", ap.variantsLabel) { onUpdate(ap.copy(variantsLabel = it)) }
        PageRenameInputRow("Price Label", ap.priceLabel) { onUpdate(ap.copy(priceLabel = it)) }
        PageRenameInputRow("Stock Label", ap.stockLabel) { onUpdate(ap.copy(stockLabel = it)) }
        PageRenameInputRow("Availability Label", ap.availabilityLabel) { onUpdate(ap.copy(availabilityLabel = it)) }
        PageRenameInputRow("Special Price Label", ap.specialPriceLabel) { onUpdate(ap.copy(specialPriceLabel = it)) }
        PageRenameInputRow("Shipping Info Label", ap.shippingLabel) { onUpdate(ap.copy(shippingLabel = it)) }
        PageRenameInputRow("Warranty Info Label", ap.warrantyLabel) { onUpdate(ap.copy(warrantyLabel = it)) }
        PageRenameInputRow("Description Label", ap.descriptionLabel) { onUpdate(ap.copy(descriptionLabel = it)) }
        PageRenameInputRow("Highlights Label", ap.highlightsLabel) { onUpdate(ap.copy(highlightsLabel = it)) }
        PageRenameInputRow("Publish Button Label", ap.publishBtnLabel) { onUpdate(ap.copy(publishBtnLabel = it)) }
        PageRenameInputRow("Draft Button Label", ap.draftBtnLabel) { onUpdate(ap.copy(draftBtnLabel = it)) }
      }

      AddProdConfigGroup.ORDER_TOGGLES -> {
        PageConfigSwitchRow(
          title = "AI Description Helper",
          subtitle = "Add Product বিবরণে AI সহায়তা বাটন চালু রাখুন",
          checked = ap.enableAiHelper,
          onCheckedChange = { onUpdate(ap.copy(enableAiHelper = it)) },
          testTag = "ap_enable_ai_switch"
        )
        PageConfigSwitchRow(
          title = "Show Channels & Merchandising Strip",
          subtitle = "Hot Sale, B2G1, Web ও POS চ্যানেল স্ট্রিপ দেখান",
          checked = ap.showChannelsStrip,
          onCheckedChange = { onUpdate(ap.copy(showChannelsStrip = it)) },
          testTag = "ap_show_channels_switch"
        )
        Text("ফর্ম ব্লক পজিশন (Reorder Blocks):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
        ap.blockOrder.forEachIndexed { idx, blkKey ->
          val isHidden = ap.hiddenBlocks.contains(blkKey)
          PageOrderItemRow(
            title = "${idx + 1}. ${ADD_PROD_BLOCK_LABELS[blkKey] ?: blkKey}",
            isHidden = isHidden,
            canMoveUp = idx > 0,
            canMoveDown = idx < ap.blockOrder.lastIndex,
            onMoveUp = {
              val next = ap.blockOrder.toMutableList()
              val tmp = next[idx - 1]
              next[idx - 1] = next[idx]
              next[idx] = tmp
              onUpdate(ap.copy(blockOrder = next))
            },
            onMoveDown = {
              val next = ap.blockOrder.toMutableList()
              val tmp = next[idx + 1]
              next[idx + 1] = next[idx]
              next[idx] = tmp
              onUpdate(ap.copy(blockOrder = next))
            },
            onToggleVisibility = {
              val nextHidden = if (isHidden) ap.hiddenBlocks - blkKey else (ap.hiddenBlocks + blkKey).distinct()
              onUpdate(ap.copy(hiddenBlocks = nextHidden))
            }
          )
        }
      }
    }
  }
}
