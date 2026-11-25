package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import com.example.util.ProductMediaHelper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.data.model.ProductVariantHelper
import com.example.data.model.StoreConfig
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex

@Composable
fun ProductDetailDialog(
  product: Product,
  reviews: List<ProductReview> = emptyList(),
  storeConfig: StoreConfig = StoreConfig(),
  allProducts: List<Product> = emptyList(),
  onDismiss: () -> Unit,
  onAddToCart: (Product, Int, String, String, String) -> Unit,
  onBuyNow: (Product, Int, String, String, String) -> Unit = { prod, qty, c, s, a ->
    onAddToCart(prod, qty, c, s, a)
    onDismiss()
  },
  onWhatsAppClick: ((Product, Int, String, String, String) -> Unit)? = null,
  onAddReview: (rating: Int, customerName: String, comment: String) -> Unit = { _, _, _ -> },
  onAskAiForProduct: (Product) -> Unit = {}
) {
  val context = LocalContext.current
  val initialVariation = remember(product.id) {
    product.variations.firstOrNull { it.enabled && it.stock > 0 }
      ?: product.variations.firstOrNull { it.enabled }
  }
  var quantity by remember { mutableIntStateOf(1) }
  var selectedColor by remember {
    mutableStateOf(initialVariation?.color?.takeIf { it.isNotBlank() } ?: product.colors.firstOrNull() ?: "")
  }
  var selectedSize by remember {
    mutableStateOf(initialVariation?.size?.takeIf { it.isNotBlank() } ?: product.sizes.firstOrNull() ?: "")
  }
  var selectedAgeGroup by remember {
    mutableStateOf(initialVariation?.ageGroup?.takeIf { it.isNotBlank() } ?: product.ageGroups.firstOrNull() ?: "")
  }
  var selectionError by remember { mutableStateOf<String?>(null) }
  var preferVariantHeroPhoto by remember { mutableStateOf(false) }

  val mediaGallery = remember(product) { ProductMediaHelper.buildMediaGallery(product) }
  var selectedMediaIdx by remember(product.id) { mutableIntStateOf(0) }
  var showOriginalMediaViewer by remember { mutableStateOf(false) }

  val (liveSellPrice, liveRegPrice) = remember(product, selectedColor, selectedSize, selectedAgeGroup) {
    ProductVariantHelper.resolvePricing(product, selectedColor, selectedSize, selectedAgeGroup)
  }
  val liveStock = remember(product, selectedColor, selectedSize, selectedAgeGroup) {
    ProductVariantHelper.resolveStock(product, selectedColor, selectedSize, selectedAgeGroup)
  }
  val activeVariantImg = remember(product, selectedColor, selectedSize) {
    ProductVariantHelper.resolveImage(product, selectedColor, selectedSize)
  }

  // Review Form States
  var isReviewFormOpen by remember { mutableStateOf(false) }
  var reviewRating by remember { mutableIntStateOf(5) }
  var reviewerName by remember { mutableStateOf("") }
  var reviewComment by remember { mutableStateOf("") }
  var reviewError by remember { mutableStateOf<String?>(null) }

  // Calculate review metrics
  val totalReviews = reviews.size
  val avgRating = if (totalReviews > 0) {
    (reviews.map { it.rating }.average() * 10).toInt() / 10f
  } else {
    product.rating
  }

  val dp = storeConfig.pageConfig.detailPage
  val masterCfg = storeConfig.pageConfig.master
  val dpFont = resolvePageFontFamily(dp.fontFamily)
  val cardRadius = dp.cardRadiusPx.coerceIn(0, 32).dp
  val btnRadius = dp.buttonRadiusPx.coerceIn(0, 50).dp
  val borderW = dp.borderWidthPx.coerceIn(0, 4).dp
  val modalBg = parseColorHex(dp.modalBgColor, Color.White)
  val headerBg = parseColorHex(dp.headerBgColor, Color.White)
  val headerTextCol = parseColorHex(dp.headerTextColor, RoyalMaroon)
  val skuCol = parseColorHex(dp.skuTextColor, AntiqueGoldDark)
  val priceCol = parseColorHex(dp.priceColor, RoyalMaroon)
  val regPriceCol = parseColorHex(dp.regularPriceColor, Color.Gray)
  val stockInCol = parseColorHex(dp.stockInColor, Color(0xFF2E7D32))
  val stockOutCol = parseColorHex(dp.stockOutColor, Color.Red)
  val descBgCol = parseColorHex(dp.descBlockBg, CreamBackground)
  val descBorderCol = parseColorHex(dp.descBlockBorder, BorderGold)
  val descHeadCol = if (masterCfg.highContrastMode) Color.Black else parseColorHex(dp.descHeadingColor, Color.Black)
  val descTxtCol = if (masterCfg.highContrastMode) Color.Black else parseColorHex(dp.descTextColor, Color.Black)
  val varBlkBg = parseColorHex(dp.variantBlockBg, CreamSurface)
  val varBlkBorder = parseColorHex(dp.variantBlockBorder, BorderGold)
  val varLabelCol = parseColorHex(dp.variantLabelColor, RoyalMaroon)
  val chipBg = parseColorHex(dp.variantChipBg, Color.White)
  val chipTxt = parseColorHex(dp.variantChipText, DarkEspresso)
  val chipBdr = parseColorHex(dp.variantChipBorder, BorderGold)
  val chipActiveBg = parseColorHex(dp.variantActiveBg, RoyalMaroon)
  val chipActiveTxt = parseColorHex(dp.variantActiveText, Color.White)
  val sizeActiveCol = parseColorHex(dp.variantSizeActiveBg, Color(0xFFF97316))
  val tblHdrBg = parseColorHex(dp.tableHeaderBg, Color(0xFF0F294A))
  val tblHdrTxt = parseColorHex(dp.tableHeaderText, Color.White)
  val tblBg = parseColorHex(dp.tableBgColor, Color.White)
  val tblAltBg = parseColorHex(dp.tableAltRowBg, CreamBackground)
  val tblTxt = if (masterCfg.highContrastMode) Color.Black else parseColorHex(dp.tableTextColor, Color.Black)
  val tblBdr = parseColorHex(dp.tableBorderColor, BorderGold)
  val qtyBlkBg = parseColorHex(dp.qtyBlockBg, Color.White)
  val qtyBlkBdr = parseColorHex(dp.qtyBlockBorder, BorderGold)
  val qtyBtnBgCol = parseColorHex(dp.qtyBtnBg, Color(0xFFF3F4F6))
  val qtyBtnTxtCol = parseColorHex(dp.qtyBtnText, Color(0xFF374151))
  val atcBgStart = parseColorHex(dp.addToCartBtnBg, RoyalMaroon)
  val atcBgEnd = parseColorHex(dp.addToCartBtnBgEnd, RoyalMaroon)
  val atcTxt = parseColorHex(dp.addToCartBtnText, Color.White)
  val revSecBg = parseColorHex(dp.reviewSectionBg, CreamBackground)
  val revSecBdr = parseColorHex(dp.reviewSectionBorder, BorderGold)
  val revCardBgCol = parseColorHex(dp.reviewCardBg, Color.White)
  val revCardBdrCol = parseColorHex(dp.reviewCardBorder, BorderGold)
  val revHeadCol = parseColorHex(dp.reviewHeadingColor, RoyalMaroon)
  val revTxtCol = if (masterCfg.highContrastMode) Color.Black else parseColorHex(dp.reviewTextColor, DarkEspresso)
  val revBtnBgCol = parseColorHex(dp.reviewBtnBg, Color.Transparent)
  val revBtnTxtCol = parseColorHex(dp.reviewBtnText, RoyalMaroon)
  val revBtnBdrCol = parseColorHex(dp.reviewBtnBorder, RoyalMaroon)
  val currencySym = masterCfg.currencySymbol.ifBlank { "৳" }

  fun validateSelection(): Boolean {
    if (product.colors.isNotEmpty() && selectedColor.isBlank()) {
      selectionError = "অনুগ্রহ করে একটি কালার নির্বাচন করুন!"
      return false
    }
    if (product.sizes.isNotEmpty() && selectedSize.isBlank()) {
      selectionError = "অনুগ্রহ করে একটি সাইজ নির্বাচন করুন!"
      return false
    }
    if (product.ageGroups.isNotEmpty() && selectedAgeGroup.isBlank()) {
      selectionError = "অনুগ্রহ করে বাচ্চার বয়স নির্বাচন করুন!"
      return false
    }
    if (liveStock <= 0) {
      selectionError = "নির্বাচিত ভ্যারিয়েন্টটি বর্তমানে স্টক আউট!"
      return false
    }
    return true
  }

  fun launchProductWhatsAppInquiry() {
    if (onWhatsAppClick != null) {
      onWhatsAppClick.invoke(product, quantity, selectedColor, selectedSize, selectedAgeGroup)
      return
    }
    val variantSummary = listOfNotNull(
      selectedColor.takeIf { it.isNotBlank() }?.let { "Color: $it" },
      selectedSize.takeIf { it.isNotBlank() }?.let { "Size: $it" },
      selectedAgeGroup.takeIf { it.isNotBlank() }?.let { "Age: $it" }
    ).joinToString(", ")
    val productDomain = storeConfig.websiteUrl.trim().trimEnd('/').ifBlank { "https://sajgoj.nfbd.workers.dev" }
    val inquiryMsg = buildString {
      append("আসসালামু আলাইকুম, আমি \"${product.name}\" (SKU: ${product.sku}) অর্ডার/তথ্য জানতে চাই।")
      if (variantSummary.isNotBlank()) append(" [$variantSummary]")
      append(" পরিমাণ: $quantity পিস, মোট: ${liveSellPrice * quantity} $currencySym")
      append("\nপ্রোডাক্ট লিংক: $productDomain/#product-${product.id}")
    }
    val rawWhatsapp = storeConfig.whatsapp.ifBlank { storeConfig.phone }
    val digitsOnly = rawWhatsapp.filter { it.isDigit() }
    val waPhone = when {
      digitsOnly.startsWith("880") -> digitsOnly
      digitsOnly.startsWith("0") -> "88$digitsOnly"
      else -> digitsOnly
    }
    try {
      val uri = Uri.parse("https://api.whatsapp.com/send?phone=$waPhone&text=${Uri.encode(inquiryMsg)}")
      context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (_: Exception) {
      try {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${storeConfig.phone}")))
      } catch (_: Exception) {
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = modalBg,
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 18.dp)
        .testTag("product_detail_dialog")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
        ) {
        // Top Header with Close
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(headerBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Text(
            text = "পণ্যের বিবরণ",
            fontFamily = dpFont,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = headerTextCol,
            modifier = Modifier.align(Alignment.CenterStart)
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .size(32.dp)
              .clip(CircleShape)
              .background(CreamSurface)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = DarkEspresso,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Product Large Video-First Media Gallery
        val safeMediaIdx = selectedMediaIdx.coerceIn(0, (mediaGallery.size - 1).coerceAtLeast(0))
        val activeMedia = mediaGallery.getOrNull(safeMediaIdx)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(1.1f)
              .clip(RoundedCornerShape(18.dp))
              .background(Color(0xFFF8FAFC))
              .clickable { showOriginalMediaViewer = true }
              .testTag("product_detail_media_box")
          ) {
            val heroImageModel = if (preferVariantHeroPhoto && activeVariantImg.isNotBlank()) {
              activeVariantImg
            } else {
              activeMedia?.thumbUrl ?: product.imageUrl
            }
            AppImage(
              model = heroImageModel,
              contentDescription = product.name,
              modifier = Modifier.fillMaxSize()
            )

            if (activeMedia?.isVideo == true) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color.Black.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDC2626).copy(alpha = 0.9f))
                    .border(2.dp, Color.White, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "ভিডিও প্লে করুন",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFEA580C),
                modifier = Modifier
                  .align(Alignment.TopStart)
                  .padding(10.dp)
              ) {
                Text(
                  text = "🎬 ${activeMedia.label}",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(50),
              color = Color.Black.copy(alpha = 0.72f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ZoomIn,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = "অরিজিনাল সাইজ",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          if (mediaGallery.size > 1) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              mediaGallery.forEachIndexed { idx, item ->
                val isSelected = idx == safeMediaIdx
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (!preferVariantHeroPhoto && isSelected) RoyalMaroon else BorderGold,
                      shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                      preferVariantHeroPhoto = false
                      selectedMediaIdx = idx
                    }
                ) {
                  AppImage(
                    model = item.thumbUrl,
                    contentDescription = item.label,
                    modifier = Modifier.fillMaxSize()
                  )
                  if (item.isVideo) {
                    Box(
                      modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                          imageVector = Icons.Default.PlayArrow,
                          contentDescription = "ভিডিও",
                          tint = Color.White,
                          modifier = Modifier.size(18.dp)
                        )
                        Text("ভিডিও", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        if (showOriginalMediaViewer) {
          ProductMediaViewerDialog(
            product = product,
            initialIndex = safeMediaIdx,
            onDismiss = { showOriginalMediaViewer = false },
            onOpenProductDetail = { showOriginalMediaViewer = false },
            onAddToCart = { prod -> onAddToCart(prod, 1, selectedColor, selectedSize, selectedAgeGroup) }
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Product Details (Dynamically ordered via PageConfiguration)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          dp.sectionOrder.filterNot { dp.hiddenSections.contains(it) }.forEach { sectionId ->
            when (sectionId) {
              "price" -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "$liveSellPrice $currencySym",
                fontFamily = dpFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = priceCol
              )
              if (liveRegPrice > liveSellPrice) {
                Text(
                  text = "$liveRegPrice $currencySym",
                  fontFamily = dpFont,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = regPriceCol,
                  textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(varBlkBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = AntiqueGold,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "$avgRating (${if (totalReviews > 0) totalReviews else product.reviewsCount} রিভিউ)",
                fontFamily = dpFont,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = descTxtCol
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = product.name,
            fontFamily = dpFont,
            fontSize = dp.titleSize.coerceIn(12, 32).sp,
            fontWeight = if (dp.titleBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (dp.titleItalic) FontStyle.Italic else FontStyle.Normal,
            color = headerTextCol
          )

          Text(
            text = "SKU: ${product.sku}  |  ক্যাটাগরি: ${product.category}",
            fontFamily = dpFont,
            fontSize = 11.sp,
            color = skuCol,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(8.dp))
              }

              "offers" -> {
          // Show Buy 2 Get 1 Free banner ONLY when Admin explicitly selected this product
          val isEligibleB2G1 = storeConfig.b2g1.enabled && storeConfig.b2g1.eligibleProductIds.contains(product.id)
          if (isEligibleB2G1) {
            val giftProd = allProducts.find { it.id == storeConfig.b2g1.freeProductId }
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(cardRadius),
              color = Color(0xFF047857),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("product_detail_b2g1_banner")
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "🎁 ${storeConfig.b2g1.title} প্রযোজ্য",
                    fontFamily = dpFont,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                  Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White
                  ) {
                    Text(
                      text = "FREE GIFT",
                      fontSize = 9.5.sp,
                      fontWeight = FontWeight.Black,
                      color = Color(0xFF047857),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = if (giftProd != null) {
                    "নির্বাচিত ${storeConfig.b2g1.minItems.coerceAtLeast(1)}টি পণ্য কার্টে নিলে পাচ্ছেন \"${giftProd.name}\" একদম ফ্রি!"
                  } else {
                    storeConfig.b2g1.subtitle
                  },
                  fontFamily = dpFont,
                  fontSize = 11.sp,
                  color = Color(0xFFECFDF5)
                )
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
              }

              "description" -> {
          // Description Card
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(cardRadius))
              .background(descBgCol)
              .border(borderW, descBorderCol, RoundedCornerShape(cardRadius))
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = dp.descTitle.ifBlank { "বিবরণ" },
              fontFamily = dpFont,
              fontSize = dp.headingSize.coerceIn(10, 24).sp,
              fontWeight = if (dp.headingBold) FontWeight.ExtraBold else FontWeight.Normal,
              fontStyle = if (dp.headingItalic) FontStyle.Italic else FontStyle.Normal,
              color = descHeadCol
            )
            Text(
              text = product.description,
              fontFamily = dpFont,
              fontSize = dp.descSize.coerceIn(10, 24).sp,
              fontWeight = if (dp.descBold) FontWeight.Bold else FontWeight.Medium,
              fontStyle = if (dp.descItalic) FontStyle.Italic else FontStyle.Normal,
              color = descTxtCol,
              lineHeight = (dp.descSize + 6).sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // AI Styling & Matching Assistant trigger for this item
          OutlinedButton(
            onClick = { onAskAiForProduct(product) },
            border = BorderStroke(1.dp, priceCol.copy(alpha = 0.65f)),
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = varBlkBg,
              contentColor = priceCol
            ),
            shape = RoundedCornerShape(btnRadius),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("product_detail_ai_tip_button")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = AntiqueGoldDark,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "এই পণ্যের সাথে স্টাইলিং ও ম্যাচিং টিপস দেখুন",
              fontFamily = dpFont,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
              }

              "specs_table" -> if (masterCfg.showSpecsTableOnDetails) {
          val specRows = buildList {
            add("SKU" to product.sku)
            add("ক্যাটাগরি" to product.category)
            if (product.brand.isNotBlank() && product.brand != "No Brand") {
              add("ব্র্যান্ড" to product.brand)
            }
            product.attributes.forEach { (k, v) -> if (v.isNotBlank()) add(k to v) }
            if (product.colors.isNotEmpty()) add("কালার অপশন" to product.colors.joinToString(", "))
            if (product.sizes.isNotEmpty()) add("সাইজ অপশন" to product.sizes.joinToString(", "))
            if (product.variations.isNotEmpty()) {
              val varSummary = product.variations.take(4).joinToString(" | ") { v ->
                listOf(v.color, v.size).filter { it.isNotBlank() }.joinToString("/") + ": ${v.stock} পিস"
              }
              add("ভ্যারিয়েন্ট স্টক" to varSummary)
            } else {
              add("মোট স্টক" to "${product.stock} পিস")
            }
          }
          Surface(
            shape = RoundedCornerShape(cardRadius),
            color = tblBg,
            border = BorderStroke(borderW, tblBdr),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 10.dp)
              .testTag("product_detail_specs_table")
          ) {
            Column {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(tblHdrBg)
                  .padding(horizontal = 12.dp, vertical = 8.dp)
              ) {
                Text(
                  text = dp.specsTableTitle.ifBlank { "পণ্যের স্পেসিফিকেশন ও স্টক টেবিল" },
                  fontFamily = dpFont,
                  fontSize = (dp.tableSize + 1).coerceIn(10, 22).sp,
                  fontWeight = FontWeight.Bold,
                  color = tblHdrTxt
                )
              }
              specRows.forEachIndexed { idx, (k, v) ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(if (idx % 2 == 1) tblAltBg else tblBg)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = k,
                    fontFamily = dpFont,
                    fontSize = dp.tableSize.coerceIn(9, 20).sp,
                    fontWeight = if (dp.tableBold) FontWeight.Bold else FontWeight.SemiBold,
                    fontStyle = if (dp.tableItalic) FontStyle.Italic else FontStyle.Normal,
                    color = tblTxt,
                    modifier = Modifier.weight(0.42f)
                  )
                  Text(
                    text = v,
                    fontFamily = dpFont,
                    fontSize = dp.tableSize.coerceIn(9, 20).sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = if (dp.tableItalic) FontStyle.Italic else FontStyle.Normal,
                    color = tblTxt,
                    modifier = Modifier.weight(0.58f)
                  )
                }
                if (idx < specRows.lastIndex) {
                  HorizontalDivider(color = tblBdr)
                }
              }
            }
          }
              }

              "variants" -> {

          // ------------------------------------------
          // COLOR FAMILY SELECTION WITH VARIANT PHOTOS ABOVE SIZE (Screenshot 2.1)
          // ------------------------------------------
          if (product.colors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(cardRadius),
              color = varBlkBg,
              border = BorderStroke(borderW, varBlkBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = dp.colorLabel.ifBlank { "কালার নির্বাচন করুন (Color Family):" },
                  fontFamily = dpFont,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = varLabelCol
                )
                if (selectedColor.isNotBlank()) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CreamSurface,
                    border = BorderStroke(1.dp, BorderGold)
                  ) {
                    Text(
                      text = selectedColor,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = DarkEspresso,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              val hasColorImages = product.colors.any { !product.variantImages[it].isNullOrBlank() }
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                product.colors.forEach { col ->
                  val isSelected = selectedColor == col
                  val thumb = product.variantImages[col]?.takeIf { it.isNotBlank() }
                  if (hasColorImages || !thumb.isNullOrBlank()) {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = Color.White,
                      border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFFF97316) else BorderGold
                      ),
                      modifier = Modifier
                        .width(86.dp)
                        .clickable {
                          selectedColor = col
                          preferVariantHeroPhoto = true
                          selectionError = null
                          val newMax = ProductVariantHelper.resolveStock(product, col, selectedSize, selectedAgeGroup)
                          if (newMax > 0 && quantity > newMax) quantity = newMax
                        }
                        .testTag("color_option_$col")
                    ) {
                      Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                          modifier = Modifier
                            .fillMaxWidth()
                            .height(74.dp)
                            .background(Color(0xFFF8FAFC))
                        ) {
                          AppImage(
                            model = thumb ?: product.imageUrl,
                            contentDescription = col,
                            modifier = Modifier.fillMaxSize()
                          )
                        }
                        Box(
                          modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) Color(0xFFFFF7ED) else CreamSurface)
                            .padding(horizontal = 4.dp, vertical = 5.dp),
                          contentAlignment = Alignment.Center
                        ) {
                          Text(
                            text = col,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFFEA580C) else DarkEspresso,
                            maxLines = 1
                          )
                        }
                      }
                    }
                  } else {
                    Surface(
                      shape = RoundedCornerShape(btnRadius),
                      color = if (isSelected) chipActiveBg else chipBg,
                      border = BorderStroke(1.dp, if (isSelected) chipActiveBg else chipBdr),
                      modifier = Modifier
                        .clickable {
                          selectedColor = col
                          selectionError = null
                          val newMax = ProductVariantHelper.resolveStock(product, col, selectedSize, selectedAgeGroup)
                          if (newMax > 0 && quantity > newMax) quantity = newMax
                        }
                        .testTag("color_option_$col")
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                      ) {
                        if (isSelected) {
                          Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = chipActiveTxt,
                            modifier = Modifier.size(13.dp)
                          )
                        }
                        Text(
                          text = col,
                          fontFamily = dpFont,
                          fontSize = 12.sp,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                          color = if (isSelected) chipActiveTxt else chipTxt
                        )
                      }
                    }
                  }
                }
              }
            }
            }
          }

          // ------------------------------------------
          // SIZE SELECTION (যদি সাইজ অপশন থাকে)
          // ------------------------------------------
          if (product.sizes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = dp.sizeLabel.ifBlank { "সাইজ নির্বাচন করুন (Size):" },
                  fontFamily = dpFont,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = varLabelCol
                )
                if (selectedSize.isNotBlank()) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CreamSurface,
                    border = BorderStroke(1.dp, BorderGold)
                  ) {
                    Text(
                      text = selectedSize,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = DarkEspresso,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
                  .padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                product.sizes.forEach { sz ->
                  val isSelected = selectedSize == sz
                  val sizeStock = ProductVariantHelper.resolveStock(product, selectedColor, sz, selectedAgeGroup)
                  val inStock = sizeStock > 0
                  Surface(
                    shape = RoundedCornerShape(btnRadius),
                    color = when {
                      isSelected -> sizeActiveCol.copy(alpha = 0.14f)
                      !inStock -> Color(0xFFF1F5F9)
                      else -> chipBg
                    },
                    border = BorderStroke(
                      width = if (isSelected) 1.5.dp else 1.dp,
                      color = if (isSelected) sizeActiveCol else if (inStock) chipBdr else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                      .clickable {
                        selectedSize = sz
                        selectionError = null
                        if (sizeStock > 0 && quantity > sizeStock) quantity = sizeStock
                      }
                      .testTag("size_option_$sz")
                  ) {
                    Box(
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = if (inStock || product.variations.isEmpty()) sz else "$sz (শেষ)",
                        fontFamily = dpFont,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                          isSelected -> sizeActiveCol
                          !inStock && product.variations.isNotEmpty() -> Color.Gray
                          else -> chipTxt
                        }
                      )
                    }
                  }
                }
              }
            }
          }

          // ------------------------------------------
          // KIDS AGE SELECTION (বাচ্চাদের বয়সের মাপ)
          // ------------------------------------------
          if (product.ageGroups.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(text = "👶", fontSize = 14.sp)
                  Text(
                    text = "বাচ্চার বয়স নির্বাচন করুন (Kids Age):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalMaroon
                  )
                }
                if (selectedAgeGroup.isNotBlank()) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AntiqueGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, BorderGold)
                  ) {
                    Text(
                      text = selectedAgeGroup,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMaroon,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                product.ageGroups.forEach { age ->
                  val isSelected = selectedAgeGroup == age
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AntiqueGoldDark else CreamSurface,
                    border = BorderStroke(1.dp, if (isSelected) AntiqueGoldDark else BorderGold),
                    modifier = Modifier
                      .clickable {
                        selectedAgeGroup = age
                        selectionError = null
                      }
                      .testTag("age_option_$age")
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      if (isSelected) {
                        Icon(
                          imageVector = Icons.Default.CheckCircle,
                          contentDescription = null,
                          tint = Color.White,
                          modifier = Modifier.size(12.dp)
                        )
                      }
                      Text(
                        text = age,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else DarkEspresso
                      )
                    }
                  }
                }
              }
            }
          }
              }

              "quantity" -> {
          Spacer(modifier = Modifier.height(10.dp))

          // Stock Info & Quantity Selector
          Surface(
            shape = RoundedCornerShape(cardRadius),
            color = qtyBlkBg,
            border = BorderStroke(borderW, qtyBlkBdr),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              if (masterCfg.showStockOnDetails) {
                Text(
                  text = when {
                    liveStock <= 0 -> dp.stockOutLabel.ifBlank { "স্টক আউট" }
                    liveStock <= 5 -> "⏰ প্রায় শেষ! মাত্র $liveStock পিস বাকি"
                    else -> "${dp.stockInLabel.ifBlank { "স্টক আছে" }} ($liveStock পিস)"
                  },
                  fontFamily = dpFont,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = when {
                    liveStock <= 0 -> stockOutCol
                    liveStock <= 5 -> Color(0xFFEA580C)
                    else -> stockInCol
                  }
                )
              } else {
                Text(dp.qtyLabel, fontFamily = dpFont, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = varLabelCol)
              }

              if (liveStock > 0) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier
                    .clip(RoundedCornerShape(btnRadius))
                    .background(qtyBtnBgCol)
                    .border(1.dp, qtyBlkBdr, RoundedCornerShape(btnRadius))
                    .padding(4.dp)
                ) {
                  IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = qtyBtnTxtCol, modifier = Modifier.size(16.dp))
                  }

                  Text(
                    text = quantity.toString(),
                    fontFamily = dpFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = qtyBtnTxtCol,
                    modifier = Modifier.padding(horizontal = 6.dp)
                  )

                  IconButton(
                    onClick = { if (quantity < liveStock) quantity++ },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = qtyBtnTxtCol, modifier = Modifier.size(16.dp))
                  }
                }
              }
            }
          }
              }

              "actions" -> {
                // Inline Add-to-Cart button moved to the sticky bottom action bar (WhatsApp, Buy Now, Add To Cart)
              }

              "reviews" -> if (masterCfg.showReviewsOnDetails) {
          Spacer(modifier = Modifier.height(18.dp))
          HorizontalDivider(color = revSecBdr)
          Spacer(modifier = Modifier.height(14.dp))

          // ==========================================
          // CUSTOMER REVIEWS & RATINGS SECTION
          // ==========================================
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
              Text(
                text = dp.reviewsTitle.ifBlank { "গ্রাহক রিভিউ ও রেটিং" },
                fontFamily = dpFont,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = revHeadCol
              )
              Text(
                text = "${totalReviews} জন ক্রেতার মূল্যায়ন",
                fontFamily = dpFont,
                fontSize = 11.sp,
                color = revTxtCol.copy(alpha = 0.7f)
              )
            }

            OutlinedButton(
              onClick = { isReviewFormOpen = !isReviewFormOpen },
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = revBtnBgCol,
                contentColor = revBtnTxtCol
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, revBtnBdrCol),
              shape = RoundedCornerShape(btnRadius),
              modifier = Modifier.testTag("write_review_button")
            ) {
              Icon(
                imageVector = Icons.Default.RateReview,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isReviewFormOpen) "বন্ধ করুন" else dp.writeReviewBtnLabel.ifBlank { "রিভিউ দিন" },
                fontFamily = dpFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Overall Rating Box & Breakdown
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(cardRadius))
              .background(revSecBg)
              .border(borderW, revSecBdr, RoundedCornerShape(cardRadius))
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              // Score column
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(90.dp)
              ) {
                Text(
                  text = "%.1f".format(avgRating),
                  fontSize = 32.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = RoyalMaroon
                )
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                  val rounded = avgRating.toInt()
                  for (i in 1..5) {
                    Icon(
                      imageVector = if (i <= rounded) Icons.Default.Star else Icons.Outlined.StarBorder,
                      contentDescription = null,
                      tint = AntiqueGold,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "সর্বমোট $totalReviews রিভিউ",
                  fontSize = 10.sp,
                  color = DarkEspresso.copy(alpha = 0.7f),
                  fontWeight = FontWeight.Medium
                )
              }

              // Rating distribution bars
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                for (star in 5 downTo 1) {
                  val starCount = reviews.count { it.rating == star }
                  val progress = if (totalReviews > 0) starCount.toFloat() / totalReviews else 0f
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = "${star}★",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = DarkEspresso,
                      modifier = Modifier.width(18.dp)
                    )
                    LinearProgressIndicator(
                      progress = { progress },
                      modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                      color = AntiqueGold,
                      trackColor = Color.White
                    )
                    Text(
                      text = starCount.toString(),
                      fontSize = 10.sp,
                      color = DarkEspresso.copy(alpha = 0.6f),
                      modifier = Modifier.width(18.dp)
                    )
                  }
                }
              }
            }
          }

          // Expandable Review Submission Form
          AnimatedVisibility(
            visible = isReviewFormOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(CreamSurface)
                .border(1.dp, BorderGold, RoundedCornerShape(18.dp))
                .padding(16.dp)
                .testTag("review_submission_form")
            ) {
              Text(
                text = "আপনার মতামত ও রেটিং দিন",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Star Picker (1-5)
              Text(
                text = "রেটিং নির্বাচন করুন:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DarkEspresso
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
              ) {
                for (star in 1..5) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(if (star <= reviewRating) AntiqueGold.copy(alpha = 0.15f) else Color.Transparent)
                      .clickable { reviewRating = star }
                      .testTag("star_rate_$star"),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (star <= reviewRating) Icons.Default.Star else Icons.Outlined.StarBorder,
                      contentDescription = "$star Stars",
                      tint = if (star <= reviewRating) AntiqueGold else Color.Gray,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = when (reviewRating) {
                    5 -> "অসাধারণ! (৫/৫)"
                    4 -> "খুব ভালো (৪/৫)"
                    3 -> "ভালো (৩/৫)"
                    2 -> "সন্তোষজনক নয় (২/৫)"
                    else -> "খারাপ (১/৫)"
                  },
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AntiqueGoldDark
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Name Input
              OutlinedTextField(
                value = reviewerName,
                onValueChange = { 
                  reviewerName = it
                  reviewError = null 
                },
                label = { Text("আপনার নাম (ঐচ্ছিক)") },
                placeholder = { Text("উদাঃ ফাতেমা বেগম") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = RoyalMaroon,
                  unfocusedBorderColor = BorderGold,
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("review_name_input")
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Comment Input
              OutlinedTextField(
                value = reviewComment,
                onValueChange = { 
                  reviewComment = it
                  reviewError = null
                },
                label = { Text("মতামত বা মন্তব্য লিখুন *") },
                placeholder = { Text("প্রোডাক্টের কাপড়ের কোয়ালিটি, ফিনিশিং কেমন লেগেছে?") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = RoyalMaroon,
                  unfocusedBorderColor = BorderGold,
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("review_comment_input")
              )

              if (reviewError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = reviewError!!,
                  color = Color.Red,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = {
                  if (reviewComment.isBlank()) {
                    reviewError = "অনুগ্রহ করে আপনার মন্তব্য বা রিভিউ লিখুন!"
                    return@Button
                  }
                  onAddReview(reviewRating, reviewerName.trim(), reviewComment.trim())
                  // Reset form
                  reviewerName = ""
                  reviewComment = ""
                  reviewRating = 5
                  reviewError = null
                  isReviewFormOpen = false
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = RoyalMaroon,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .testTag("submit_review_button")
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "রিভিউ জমা দিন",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Customer Reviews List
          if (reviews.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CreamBackground)
                .padding(20.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "💬",
                  fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "এখনও কোনো রিভিউ যুক্ত হয়নি",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = DarkEspresso
                )
                Text(
                  text = "প্রথম রিভিউটি দিয়ে অন্যদের সিদ্ধান্ত নিতে সাহায্য করুন!",
                  fontSize = 11.sp,
                  color = DarkEspresso.copy(alpha = 0.6f)
                )
              }
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              reviews.forEach { rev ->
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(cardRadius))
                    .border(borderW, revCardBdrCol, RoundedCornerShape(cardRadius))
                    .background(revCardBgCol)
                    .padding(12.dp)
                    .testTag("review_item_${rev.id}")
                ) {
                  Column {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        // Avatar Initial
                        Box(
                          modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(RoyalMaroon),
                          contentAlignment = Alignment.Center
                        ) {
                          Text(
                            text = rev.customerName.firstOrNull()?.toString() ?: "ক",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                          )
                        }

                        Column {
                          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                              text = rev.customerName.ifBlank { "ভেরিফাইড ক্রেতা" },
                              fontSize = 13.sp,
                              fontWeight = FontWeight.Bold,
                              color = DarkEspresso
                            )
                            if (rev.verified) {
                              Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(12.dp)
                              )
                            }
                          }
                          Text(
                            text = if (rev.verified) "ভেরিফাইড ক্রেতা" else "সাধারণ গ্রাহক",
                            fontSize = 10.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                          )
                        }
                      }

                      // Date & Stars
                      Column(horizontalAlignment = Alignment.End) {
                        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                          for (i in 1..5) {
                            Icon(
                              imageVector = if (i <= rev.rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                              contentDescription = null,
                              tint = AntiqueGold,
                              modifier = Modifier.size(13.dp)
                            )
                          }
                        }
                        if (rev.date.isNotBlank()) {
                          Text(
                            text = rev.date,
                            fontSize = 10.sp,
                            color = DarkEspresso.copy(alpha = 0.5f)
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = rev.comment,
                      fontSize = 12.sp,
                      color = DarkEspresso,
                      lineHeight = 17.sp
                    )

                    if (rev.image.isNotBlank()) {
                      Spacer(modifier = Modifier.height(8.dp))
                      AppImage(
                        model = rev.image,
                        contentDescription = "Review attachment photo",
                        modifier = Modifier
                          .height(110.dp)
                          .clip(RoundedCornerShape(10.dp))
                          .border(1.dp, BorderGold.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
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

          Spacer(modifier = Modifier.height(16.dp))
        }

        if (selectionError != null) {
          Surface(
            color = Color(0xFFFEF2F2),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("detail_selection_error_banner")
          ) {
            Text(
              text = selectionError!!,
              color = Color(0xFFB91C1C),
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
          }
        }

        // Fixed / Non-Scrolling Bottom Action Bar (Exclusive to Product Detail Dialog)
        ProductDetailBottomBar(
          isInStock = liveStock > 0,
          stockOutLabel = dp.stockOutLabel.ifBlank { "Stock Out" },
          onWhatsAppClick = { launchProductWhatsAppInquiry() },
          onBuyNowClick = {
            if (validateSelection()) {
              onBuyNow(product, quantity, selectedColor, selectedSize, selectedAgeGroup)
            }
          },
          onAddToCartClick = {
            if (validateSelection()) {
              onAddToCart(product, quantity, selectedColor, selectedSize, selectedAgeGroup)
              onDismiss()
            }
          }
        )
      }
    }
  }
}

@Composable
fun ProductDetailBottomBar(
  isInStock: Boolean,
  stockOutLabel: String = "Stock Out",
  onWhatsAppClick: () -> Unit,
  onBuyNowClick: () -> Unit,
  onAddToCartClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = Color.White,
    shadowElevation = 10.dp,
    modifier = modifier
      .fillMaxWidth()
      .testTag("product_detail_bottom_bar")
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // 1. WhatsApp Logo Button
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF25D366),
          border = BorderStroke(1.dp, Color(0xFF1EBE5D)),
          shadowElevation = 2.dp,
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onWhatsAppClick() }
            .testTag("detail_bottom_whatsapp_button")
        ) {
          Box(contentAlignment = Alignment.Center) {
            WhatsAppLogoIcon(modifier = Modifier.size(28.dp))
          }
        }

        // 2. Buy Now Button (Black)
        Button(
          onClick = onBuyNowClick,
          enabled = isInStock,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0A0A0A),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF94A3B8),
            disabledContentColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("detail_bottom_buy_now_button")
        ) {
          Text(
            text = "Buy Now",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
          )
        }

        // 3. Add To Cart Button (Red)
        Button(
          onClick = onAddToCartClick,
          enabled = isInStock,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEF1C25),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF94A3B8),
            disabledContentColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1.1f)
            .heightIn(min = 48.dp)
            .testTag("detail_bottom_add_to_cart_button")
        ) {
          Text(
            text = if (isInStock) "Add To Cart" else stockOutLabel,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

@Composable
private fun WhatsAppLogoIcon(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val strokeW = w * 0.095f
      val radius = w * 0.39f
      val cx = w * 0.52f
      val cy = h * 0.47f

      drawCircle(
        color = Color.White,
        radius = radius,
        center = androidx.compose.ui.geometry.Offset(cx, cy),
        style = Stroke(width = strokeW)
      )

      val tailPath = Path().apply {
        moveTo(w * 0.24f, h * 0.72f)
        lineTo(w * 0.14f, h * 0.89f)
        lineTo(w * 0.34f, h * 0.82f)
        close()
      }
      drawPath(
        path = tailPath,
        color = Color.White
      )
    }
    Icon(
      imageVector = Icons.Default.Call,
      contentDescription = "WhatsApp এ মেসেজ করুন",
      tint = Color.White,
      modifier = Modifier
        .size(14.dp)
        .padding(start = 1.dp, bottom = 1.dp)
    )
  }
}
