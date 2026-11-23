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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AddProductPageConfig
import com.example.data.model.DetailPageConfig
import com.example.data.model.MasterPageConfig
import com.example.data.model.PageConfiguration
import com.example.data.model.Product
import com.example.ui.theme.parseColorHex

@Composable
fun DetailPageLivePreview(
  dp: DetailPageConfig,
  master: MasterPageConfig,
  sampleProduct: Product?,
  modifier: Modifier = Modifier
) {
  val font = resolvePageFontFamily(dp.fontFamily)
  val cardRadius = dp.cardRadiusPx.coerceIn(0, 28).dp
  val btnRadius = dp.buttonRadiusPx.coerceIn(0, 40).dp
  val borderW = dp.borderWidthPx.coerceIn(0, 4).dp
  val modalBg = parseColorHex(dp.modalBgColor, Color.White)
  val headerBg = parseColorHex(dp.headerBgColor, Color.White)
  val headerText = parseColorHex(dp.headerTextColor, Color(0xFF7A1C2B))
  val descText = if (master.highContrastMode) Color.Black else parseColorHex(dp.descTextColor, Color.Black)
  val descHead = if (master.highContrastMode) Color.Black else parseColorHex(dp.descHeadingColor, Color.Black)
  val tableText = if (master.highContrastMode) Color.Black else parseColorHex(dp.tableTextColor, Color.Black)
  val currency = master.currencySymbol.ifBlank { "৳" }

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = parseColorHex(dp.backdropColor, Color.Black).copy(alpha = 0.08f),
    border = BorderStroke(1.2.dp, Color(0xFF7DD3FC)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("detail_page_live_preview")
  ) {
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("লাইভ ভিউ — Product Details Page", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
        Surface(shape = RoundedCornerShape(50), color = Color(0xFFDCFCE7)) {
          Text("LIVE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
        }
      }

      Surface(
        shape = RoundedCornerShape(cardRadius),
        color = modalBg,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Header Preview
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(headerBg)
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Column {
              Text(
                text = sampleProduct?.name ?: "রয়েল জামদানি শাড়ি",
                fontFamily = font,
                fontSize = (dp.titleSize * 0.78f).coerceIn(12f, 22f).sp,
                fontWeight = if (dp.titleBold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (dp.titleItalic) FontStyle.Italic else FontStyle.Normal,
                color = headerText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "SKU: ${sampleProduct?.sku ?: "SAJ-101"}",
                fontFamily = font,
                fontSize = 9.5.sp,
                color = parseColorHex(dp.skuTextColor, Color(0xFFB8956A))
              )
            }
          }

          // Dynamically ordered sections preview
          dp.sectionOrder.filterNot { dp.hiddenSections.contains(it) }.forEach { sec ->
            when (sec) {
              "price" -> {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                      text = "${sampleProduct?.price ?: 3500} $currency",
                      fontFamily = font,
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Black,
                      color = parseColorHex(dp.priceColor, Color(0xFF7A1C2B))
                    )
                    Text(
                      text = "${sampleProduct?.regularPrice ?: 4200} $currency",
                      fontFamily = font,
                      fontSize = 10.5.sp,
                      color = parseColorHex(dp.regularPriceColor, Color(0xFF94A3B8)),
                      textDecoration = TextDecoration.LineThrough
                    )
                  }
                  if (master.showStockOnDetails) {
                    Text(
                      text = "${dp.stockInLabel} (12 পিস)",
                      fontFamily = font,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = parseColorHex(dp.stockInColor, Color(0xFF15803D))
                    )
                  }
                }
              }

              "description" -> {
                Surface(
                  shape = RoundedCornerShape(cardRadius),
                  color = parseColorHex(dp.descBlockBg, Color(0xFFFFFAF5)),
                  border = BorderStroke(borderW, parseColorHex(dp.descBlockBorder, Color(0xFFF3E2D0))),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                      text = dp.descTitle,
                      fontFamily = font,
                      fontSize = (dp.headingSize * 0.85f).sp,
                      fontWeight = if (dp.headingBold) FontWeight.Bold else FontWeight.Normal,
                      fontStyle = if (dp.headingItalic) FontStyle.Italic else FontStyle.Normal,
                      color = descHead
                    )
                    Text(
                      text = "প্রিমিয়াম হ্যান্ডলুম ফেব্রিক এবং নিখুঁত কারুকার্য।",
                      fontFamily = font,
                      fontSize = (dp.descSize * 0.82f).sp,
                      fontWeight = if (dp.descBold) FontWeight.Bold else FontWeight.Normal,
                      fontStyle = if (dp.descItalic) FontStyle.Italic else FontStyle.Normal,
                      color = descText
                    )
                  }
                }
              }

              "specs_table" -> if (master.showSpecsTableOnDetails) {
                Surface(
                  shape = RoundedCornerShape((cardRadius.value * 0.7f).dp),
                  color = parseColorHex(dp.tableBgColor, Color.White),
                  border = BorderStroke(borderW, parseColorHex(dp.tableBorderColor, Color(0xFFE2D2C0))),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .background(parseColorHex(dp.tableHeaderBg, Color(0xFF0F294A)))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                      Text(
                        text = dp.specsTableTitle,
                        fontFamily = font,
                        fontSize = (dp.tableSize * 0.85f).sp,
                        fontWeight = FontWeight.Bold,
                        color = parseColorHex(dp.tableHeaderText, Color.White)
                      )
                    }
                    listOf("ক্যাটাগরি" to "শাড়ি", "স্টক স্ট্যাটাস" to dp.stockInLabel).forEachIndexed { idx, (k, v) ->
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .background(if (idx % 2 == 1) parseColorHex(dp.tableAltRowBg, Color(0xFFFFFAF5)) else parseColorHex(dp.tableBgColor, Color.White))
                          .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(k, fontFamily = font, fontSize = (dp.tableSize * 0.82f).sp, fontWeight = if (dp.tableBold) FontWeight.Bold else FontWeight.Medium, fontStyle = if (dp.tableItalic) FontStyle.Italic else FontStyle.Normal, color = tableText)
                        Text(v, fontFamily = font, fontSize = (dp.tableSize * 0.82f).sp, fontWeight = FontWeight.Bold, color = tableText)
                      }
                      if (idx == 0) HorizontalDivider(color = parseColorHex(dp.tableBorderColor, Color(0xFFE2D2C0)))
                    }
                  }
                }
              }

              "variants" -> {
                Surface(
                  shape = RoundedCornerShape(cardRadius),
                  color = parseColorHex(dp.variantBlockBg, Color(0xFFFFFAF5)),
                  border = BorderStroke(borderW, parseColorHex(dp.variantBlockBorder, Color(0xFFF3E2D0))),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                      text = dp.colorLabel,
                      fontFamily = font,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = parseColorHex(dp.variantLabelColor, Color(0xFF1F2937))
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      Surface(
                        shape = RoundedCornerShape(btnRadius),
                        color = parseColorHex(dp.variantActiveBg, Color(0xFF7A1C2B))
                      ) {
                        Text("✓ মেরুন", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = parseColorHex(dp.variantActiveText, Color.White), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                      }
                      Surface(
                        shape = RoundedCornerShape(btnRadius),
                        color = parseColorHex(dp.variantChipBg, Color.White),
                        border = BorderStroke(1.dp, parseColorHex(dp.variantChipBorder, Color(0xFFF3E2D0)))
                      ) {
                        Text("গোল্ডেন", fontSize = 9.5.sp, color = parseColorHex(dp.variantChipText, Color(0xFF374151)), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                      }
                      Surface(
                        shape = RoundedCornerShape(btnRadius),
                        color = parseColorHex(dp.variantSizeActiveBg, Color(0xFFD97706))
                      ) {
                        Text("${dp.sizeLabel} M", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                      }
                    }
                  }
                }
              }

              "quantity" -> {
                Surface(
                  shape = RoundedCornerShape(cardRadius),
                  color = parseColorHex(dp.qtyBlockBg, Color.White),
                  border = BorderStroke(borderW, parseColorHex(dp.qtyBlockBorder, Color(0xFFF3E2D0))),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(dp.qtyLabel, fontFamily = font, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = parseColorHex(dp.variantLabelColor, Color(0xFF1F2937)))
                    Surface(shape = RoundedCornerShape(8.dp), color = parseColorHex(dp.qtyBtnBg, Color(0xFFF3F4F6))) {
                      Text("-  1  +", fontSize = 10.sp, fontWeight = FontWeight.Black, color = parseColorHex(dp.qtyBtnText, Color(0xFF374151)), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                  }
                }
              }

              "actions" -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  if (master.showQrOnDetails) {
                    Surface(
                      shape = RoundedCornerShape(btnRadius),
                      color = parseColorHex(dp.qrBtnBg, Color(0xFFFFF8F0)),
                      border = BorderStroke(1.dp, parseColorHex(dp.qrBtnBorder, Color(0xFFD4A574)))
                    ) {
                      Text(dp.qrBtnLabel, fontFamily = font, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = parseColorHex(dp.qrBtnText, Color(0xFF7A1C2B)), modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp))
                    }
                  }
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(btnRadius))
                      .background(
                        Brush.horizontalGradient(
                          listOf(
                            parseColorHex(dp.addToCartBtnBg, Color(0xFF7A1C2B)),
                            parseColorHex(dp.addToCartBtnBgEnd, Color(0xFF5A1220))
                          )
                        )
                      )
                      .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                      Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = parseColorHex(dp.addToCartBtnText, Color.White), modifier = Modifier.size(12.dp))
                      Text(
                        text = "${dp.addToCartLabel} (3500 $currency)",
                        fontFamily = font,
                        fontSize = (dp.buttonSize * 0.8f).sp,
                        fontWeight = if (dp.buttonBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (dp.buttonItalic) FontStyle.Italic else FontStyle.Normal,
                        color = parseColorHex(dp.addToCartBtnText, Color.White),
                        maxLines = 1
                      )
                    }
                  }
                }
              }

              "reviews" -> if (master.showReviewsOnDetails) {
                Surface(
                  shape = RoundedCornerShape(cardRadius),
                  color = parseColorHex(dp.reviewSectionBg, Color(0xFFFFFDFA)),
                  border = BorderStroke(borderW, parseColorHex(dp.reviewSectionBorder, Color(0xFFF3E2D0))),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                      Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(13.dp))
                      Text(
                        text = dp.reviewsTitle,
                        fontFamily = font,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = parseColorHex(dp.reviewHeadingColor, Color(0xFF7A1C2B)),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(145.dp)
                      )
                    }
                    Surface(
                      shape = RoundedCornerShape(btnRadius),
                      color = parseColorHex(dp.reviewBtnBg, Color(0xFFFFF8F0)),
                      border = BorderStroke(1.dp, parseColorHex(dp.reviewBtnBorder, Color(0xFF7A1C2B)))
                    ) {
                      Text(
                        text = dp.writeReviewBtnLabel,
                        fontFamily = font,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = parseColorHex(dp.reviewBtnText, Color(0xFF7A1C2B)),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
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

@Composable
fun AddProductPageLivePreview(
  ap: AddProductPageConfig,
  master: MasterPageConfig,
  modifier: Modifier = Modifier
) {
  val font = resolvePageFontFamily(ap.fontFamily)
  val cardRadius = ap.cardRadiusPx.coerceIn(0, 28).dp
  val borderW = ap.borderWidthPx.coerceIn(0, 4).dp
  val labelColor = if (master.highContrastMode) Color.Black else parseColorHex(ap.labelTextColor, Color(0xFF0F172A))
  val subColor = parseColorHex(ap.subTextColor, Color(0xFF64748B))
  val accent = parseColorHex(ap.accentColor, Color(0xFFF97316))

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = parseColorHex(ap.pageBgColor, Color(0xFFF4F6F9)),
    border = BorderStroke(1.2.dp, Color(0xFF7DD3FC)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("add_product_live_preview")
  ) {
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
      // Top bar preview
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = parseColorHex(ap.topBarBgColor, Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "‹  ${ap.addTitle}",
            fontFamily = font,
            fontSize = (ap.headingSize * 0.78f).sp,
            fontWeight = FontWeight.Black,
            color = parseColorHex(ap.topBarTextColor, Color(0xFF0F172A))
          )
          Text("লাইভ প্রিভিউ", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accent)
        }
      }

      // Dynamic Blocks
      ap.blockOrder.filterNot { ap.hiddenBlocks.contains(it) }.forEach { blockId ->
        Surface(
          shape = RoundedCornerShape(cardRadius),
          color = parseColorHex(ap.blockBgColor, Color.White),
          border = BorderStroke(borderW, parseColorHex(ap.blockBorderColor, Color(0xFFE2E8F0))),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            when (blockId) {
              "media_identity" -> {
                Text(
                  text = "${ap.nameLabel} *",
                  fontFamily = font,
                  fontSize = (ap.labelSize * 0.78f).sp,
                  fontWeight = if (ap.labelBold) FontWeight.Bold else FontWeight.Normal,
                  fontStyle = if (ap.labelItalic) FontStyle.Italic else FontStyle.Normal,
                  color = labelColor
                )
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(7.dp))
                    .background(parseColorHex(ap.inputBgColor, Color(0xFFF8FAFC)))
                    .border(1.dp, parseColorHex(ap.inputBorderColor, Color(0xFFCBD5E1)), RoundedCornerShape(7.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                  Text(
                    text = "জামদানি শাড়ি (${ap.categoryLabel} / ${ap.videoLabel})",
                    fontFamily = font,
                    fontSize = (ap.inputSize * 0.8f).sp,
                    fontWeight = if (ap.inputBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (ap.inputItalic) FontStyle.Italic else FontStyle.Normal,
                    color = parseColorHex(ap.inputTextColor, Color(0xFF0F172A))
                  )
                }
              }

              "details_specs" -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(ap.brandLabel, fontFamily = font, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = labelColor)
                  Text("SAJGOJ", fontSize = 10.sp, color = accent, fontWeight = FontWeight.Bold)
                }
                Text(ap.attributesLabel, fontFamily = font, fontSize = 10.sp, color = subColor)
              }

              "variants_pricing" -> {
                Text("${ap.variantsLabel} • ${ap.priceLabel} & ${ap.stockLabel}", fontFamily = font, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = labelColor)
                // Variation table style preview
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = parseColorHex(ap.tableBgColor, Color.White),
                  border = BorderStroke(1.dp, parseColorHex(ap.tableBorderColor, Color(0xFFCBD5E1)))
                ) {
                  Column {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .background(parseColorHex(ap.tableHeaderBg, Color(0xFF0F172A)))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text("Variant", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = parseColorHex(ap.tableHeaderText, Color.White))
                      Text("${ap.priceLabel} / ${ap.stockLabel}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = parseColorHex(ap.tableHeaderText, Color.White))
                    }
                    Row(
                      modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 3.dp),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text("Red • M", fontSize = 9.sp, color = parseColorHex(ap.tableTextColor, Color(0xFF0F172A)))
                      Text("2500 / 10", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = parseColorHex(ap.tableTextColor, Color(0xFF0F172A)))
                    }
                  }
                }
              }

              "shipping_warranty" -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(ap.shippingLabel, fontFamily = font, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = labelColor)
                  Text(ap.warrantyLabel, fontFamily = font, fontSize = 10.sp, color = subColor)
                }
              }

              "description_highlights" -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(ap.descriptionLabel, fontFamily = font, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = labelColor)
                  if (ap.enableAiHelper) {
                    Text("✨ AI Helper On", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accent)
                  }
                }
              }
            }
          }
        }
      }

      if (ap.showChannelsStrip) {
        Text("✓ Hot Sale • ⭐ সেরা ডিল • 🌐 Web • 🛒 POS", fontSize = 9.sp, color = subColor, fontWeight = FontWeight.SemiBold)
      }

      // Action Buttons Preview
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
          shape = RoundedCornerShape(50),
          color = parseColorHex(ap.draftBtnBg, Color.White),
          border = BorderStroke(1.2.dp, parseColorHex(ap.draftBtnBorder, accent)),
          modifier = Modifier.weight(1f)
        ) {
          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
            Text(ap.draftBtnLabel, fontFamily = font, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = parseColorHex(ap.draftBtnText, accent))
          }
        }
        Surface(
          shape = RoundedCornerShape(50),
          color = parseColorHex(ap.publishBtnBg, accent),
          modifier = Modifier.weight(1f)
        ) {
          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
            Text(ap.publishBtnLabel, fontFamily = font, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = parseColorHex(ap.publishBtnText, Color.White))
          }
        }
      }
    }
  }
}

@Composable
fun MasterConfigLivePreview(pc: PageConfiguration) {
  val m = pc.master
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    border = BorderStroke(1.2.dp, Color(0xFF7DD3FC)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("master_config_live_preview")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("লাইভ সিস্টেম স্ট্যাটাস (Master Config Preview)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MasterStatPill("ফন্ট: ${m.globalFontFamily}", Modifier.weight(1f))
        MasterStatPill("কারেন্সি: ${m.currencySymbol}", Modifier.weight(1f))
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MasterStatPill(if (m.highContrastMode) "🖤 High Contrast: ON" else "Contrast: Custom", Modifier.weight(1f))
        MasterStatPill(if (m.showSpecsTableOnDetails) "📊 Specs Table: ON" else "📊 Specs Table: OFF", Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun MasterStatPill(text: String, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFFF0F9FF),
    border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
    modifier = modifier
  ) {
    Text(text, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
  }
}
