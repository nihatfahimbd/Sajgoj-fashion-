package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.SajgojAiService
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.data.model.ProductVariantHelper
import com.example.data.model.ProductVariation
import com.example.util.ProductMediaHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

private val SellerOrange = Color(0xFFF97316)
private val SellerOrangeDeep = Color(0xFFEA580C)
private val SellerPageBg = Color(0xFFF4F6F9)
private val SellerCardBorder = Color(0xFFE7ECF2)
private val SellerDivider = Color(0xFFF1F4F8)
private val SellerLabelInk = Color(0xFF1E293B)
private val SellerMutedInk = Color(0xFF94A3B8)
private val SellerReqRed = Color(0xFFEF4444)

private enum class SellerSubEditor {
  NONE,
  URL_PICKER,
  CATEGORY,
  BRAND,
  ATTRIBUTES,
  VARIANTS,
  SPECIAL_PERIOD,
  SHIPPING,
  WARRANTY,
  DESCRIPTION,
  HIGHLIGHTS
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SellerCenterProductFormDialog(
  product: Product?,
  categories: List<Category>,
  onDismiss: () -> Unit,
  onSave: (Product) -> Unit
) {
  val context = LocalContext.current
  val formScope = rememberCoroutineScope()

  // 1. Images (unlimited: Main imageUrl + additionalImages gallery)
  val initialImages = remember(product) {
    if (product != null) {
      (listOf(product.imageUrl) + product.additionalImages).filter { it.isNotBlank() }.distinct()
    } else {
      emptyList()
    }
  }
  var imageList by remember(product) { mutableStateOf(initialImages) }
  var isPickingImage by remember { mutableStateOf(false) }
  var customUrlInput by remember { mutableStateOf("") }

  // 2. Product Name (English)
  var name by remember(product) { mutableStateOf(product?.name ?: "") }

  // 3. Category
  var category by remember(product) { mutableStateOf(product?.category ?: (categories.firstOrNull()?.name ?: "শাড়ি")) }
  var subCategoryText by remember(product) { mutableStateOf("") }

  // 4. Video
  var videoEnabled by remember(product) { mutableStateOf(product?.videoEnabled ?: false) }
  var videoType by remember(product) { mutableStateOf(product?.videoType ?: "upload") }
  var videoUrl by remember(product) { mutableStateOf(product?.videoUrl ?: "") }
  var videoThumbUrl by remember(product) { mutableStateOf(product?.videoThumbUrl ?: "") }

  // 5. Brand
  var brand by remember(product) { mutableStateOf(product?.brand?.ifBlank { "No Brand" } ?: "No Brand") }

  // 6. Attributes
  var attributes by remember(product) { mutableStateOf(product?.attributes ?: emptyMap()) }

  // 7. Variants + SKU + Home Display + Channels
  var colors by remember(product) { mutableStateOf(product?.colors ?: emptyList()) }
  var sizes by remember(product) { mutableStateOf(product?.sizes ?: emptyList()) }
  var ageGroups by remember(product) { mutableStateOf(product?.ageGroups ?: emptyList()) }
  var variations by remember(product) { mutableStateOf(product?.variations ?: emptyList()) }
  var variantImages by remember(product) { mutableStateOf(product?.variantImages ?: emptyMap()) }
  var variantOrder by remember(product) { mutableStateOf(product?.variantOrder ?: listOf("Size", "Color Family")) }
  var variantImageType by remember(product) { mutableStateOf(product?.variantImageType ?: "Color Family") }
  var startInVariantDetails by remember { mutableStateOf(false) }
  var startWithSpecialSettings by remember { mutableStateOf(false) }
  var sku by remember(product) { mutableStateOf(product?.sku ?: "SAJ-${(100..999).random()}") }
  var isHotSale by remember(product) { mutableStateOf(product?.isHotSale ?: false) }
  var isBestDeal by remember(product) { mutableStateOf(product?.isBestDeal ?: false) }
  var isB2G1 by remember(product) { mutableStateOf(product?.isB2G1 ?: false) }
  var enabledForWeb by remember(product) { mutableStateOf(product?.enabledForWeb ?: true) }
  var enabledForPos by remember(product) { mutableStateOf(product?.enabledForPos ?: true) }

  // 8-11. Price, Stock, Availability, Special Price & Periods
  var priceStr by remember(product) {
    mutableStateOf(
      if (product != null) {
        (if (product.regularPrice > product.price) product.regularPrice else product.price).toString()
      } else ""
    )
  }
  var stockStr by remember(product) { mutableStateOf(product?.stock?.toString() ?: "") }
  var isAvailable by remember(product) { mutableStateOf(product?.isAvailable ?: true) }

  val initialOfferStr = remember(product) {
    if (product != null) {
      val basePrice = if (product.regularPrice > 0) product.regularPrice else product.price
      when {
        product.offerPrice > 0 && product.offerPrice < basePrice -> product.offerPrice.toString()
        product.regularPrice > product.price -> product.price.toString()
        else -> ""
      }
    } else ""
  }
  var specialPriceEnabled by remember(product) {
    mutableStateOf(product?.specialPriceEnabled ?: initialOfferStr.isNotBlank())
  }
  var offerPriceStr by remember(product) { mutableStateOf(initialOfferStr) }
  var specialPriceStart by remember(product) { mutableStateOf(product?.specialPriceStart ?: "") }
  var specialPriceEnd by remember(product) { mutableStateOf(product?.specialPriceEnd ?: "") }

  // 12. Shipping Information
  var shippingWeightKg by remember(product) { mutableStateOf(product?.shippingWeightKg ?: "") }
  var shippingDimensions by remember(product) { mutableStateOf(product?.shippingDimensions ?: "") }
  var shippingDangerousGoods by remember(product) { mutableStateOf(product?.shippingDangerousGoods ?: "None") }
  var shippingExpressChandpur by remember(product) { mutableStateOf(product?.shippingExpressChandpur ?: true) }
  var shippingNote by remember(product) { mutableStateOf(product?.shippingNote ?: "") }
  var sourceUrl by remember(product) { mutableStateOf(product?.sourceUrl ?: "") }

  // 13. Warranty Information
  var warrantyType by remember(product) { mutableStateOf(product?.warrantyType ?: "No Warranty") }
  var warrantyPeriod by remember(product) { mutableStateOf(product?.warrantyPeriod ?: "") }
  var warrantyPolicy by remember(product) { mutableStateOf(product?.warrantyPolicy ?: "") }

  // 14. Description
  var description by remember(product) { mutableStateOf(product?.description ?: "") }
  var isGeneratingAiDesc by remember { mutableStateOf(false) }
  var aiDescError by remember { mutableStateOf<String?>(null) }

  // 15. Highlights
  var highlights by remember(product) { mutableStateOf(product?.highlights ?: emptyList()) }

  // UI Control States
  var activeSubEditor by remember { mutableStateOf(SellerSubEditor.NONE) }
  var validationError by remember { mutableStateOf<String?>(null) }

  // Android Photo Picker (Zero Storage Permissions - Google Play Compliant, unlimited picks)
  val multiImagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia()
  ) { uris ->
    if (uris.isNotEmpty()) {
      isPickingImage = true
      formScope.launch {
        val copied = withContext(Dispatchers.IO) {
          uris.mapNotNull { ProductMediaHelper.copyPickedImageToInternalStorage(context, it) }
        }
        isPickingImage = false
        if (copied.isNotEmpty()) {
          imageList = (imageList + copied).distinct()
        }
      }
    }
  }

  val regularParsed = priceStr.toIntOrNull() ?: 0
  val offerParsed = offerPriceStr.toIntOrNull() ?: 0
  val hasValidOffer = specialPriceEnabled && regularParsed > 0 && offerParsed in 1 until regularParsed
  val hasShippingSet = shippingWeightKg.isNotBlank() || shippingDimensions.isNotBlank() || shippingNote.isNotBlank()
  val hasWarrantySet = warrantyType != "No Warranty" || warrantyPeriod.isNotBlank() || warrantyPolicy.isNotBlank()

  fun buildAndSave(saveAsDraft: Boolean) {
    if (!saveAsDraft) {
      if (name.isBlank()) {
        validationError = "প্রোডাক্টের নাম * পূরণ করুন।"
        return
      }
      if (regularParsed <= 0) {
        validationError = "Price(BDT) * অবশ্যই ০-এর বেশি হতে হবে।"
        return
      }
      if (stockStr.toIntOrNull() == null) {
        validationError = "Stock * পরিমাণ দিন।"
        return
      }
    } else if (name.isBlank()) {
      validationError = "Draft সংরক্ষণের জন্য কমপক্ষে পণ্যের নাম লিখুন।"
      return
    }

    val variantSummary = if (variations.isNotEmpty()) ProductVariantHelper.summarizeVariations(variations) else null
    val finalRegular = (variantSummary?.minRegularPrice?.takeIf { it > 0 } ?: regularParsed).coerceAtLeast(0)
    val finalOffer = when {
      variantSummary != null && variantSummary.minOfferPrice in 1 until finalRegular -> variantSummary.minOfferPrice
      hasValidOffer -> offerParsed
      else -> 0
    }
    val effectivePrice = if (finalOffer in 1 until finalRegular) finalOffer else finalRegular
    val cleanStock = (variantSummary?.totalStock ?: (stockStr.toIntOrNull() ?: 0)).coerceAtLeast(0)
    val finalImages = if (imageList.isNotEmpty()) imageList else listOf("res://banner_eid_special_1792530116809")

    val cleanVideoUrl = videoUrl.trim()
    val finalVideoEnabled = videoEnabled && cleanVideoUrl.isNotBlank()
    val parsedVideo = ProductMediaHelper.parseVideoSource(cleanVideoUrl, videoType)
    val resolvedThumb = if (finalVideoEnabled) {
      videoThumbUrl.trim().ifBlank { parsedVideo.thumbUrl }
    } else ""

    val resolvedCategory = if (subCategoryText.isNotBlank()) "$category > $subCategoryText" else category.trim().ifBlank { "শাড়ি" }

    val built = Product(
      id = product?.id ?: "p_${System.currentTimeMillis()}",
      name = name.trim(),
      price = effectivePrice,
      regularPrice = finalRegular,
      offerPrice = finalOffer,
      stock = cleanStock,
      category = resolvedCategory,
      imageUrl = finalImages.first(),
      additionalImages = finalImages.drop(1),
      sku = sku.trim().ifBlank { "SAJ-${(100..999).random()}" },
      description = description.trim().ifBlank {
        if (highlights.isNotEmpty()) highlights.joinToString("\n• ", prefix = "• ")
        else "সাজগোজ ফ্যাশনের প্রিমিয়াম কোয়ালিটি পণ্য।"
      },
      sourceUrl = sourceUrl.trim(),
      colors = colors,
      sizes = sizes,
      ageGroups = ageGroups,
      isHotSale = isHotSale,
      isBestDeal = isBestDeal,
      isB2G1 = isB2G1,
      createdAt = product?.createdAt ?: System.currentTimeMillis(),
      videoEnabled = finalVideoEnabled,
      videoType = videoType,
      videoUrl = cleanVideoUrl,
      videoThumbUrl = resolvedThumb,
      brand = brand.ifBlank { "No Brand" },
      attributes = attributes,
      isAvailable = isAvailable,
      isDraft = saveAsDraft,
      specialPriceEnabled = (variantSummary?.specialPriceEnabled ?: specialPriceEnabled),
      specialPriceStart = (variantSummary?.specialPriceStart?.ifBlank { specialPriceStart } ?: specialPriceStart).trim(),
      specialPriceEnd = (variantSummary?.specialPriceEnd?.ifBlank { specialPriceEnd } ?: specialPriceEnd).trim(),
      shippingWeightKg = shippingWeightKg.trim(),
      shippingDimensions = shippingDimensions.trim(),
      shippingDangerousGoods = shippingDangerousGoods,
      shippingExpressChandpur = shippingExpressChandpur,
      shippingNote = shippingNote.trim(),
      warrantyType = warrantyType,
      warrantyPeriod = warrantyPeriod.trim(),
      warrantyPolicy = warrantyPolicy.trim(),
      highlights = highlights,
      enabledForWeb = enabledForWeb,
      enabledForPos = enabledForPos,
      variations = variations,
      variantImages = variantImages,
      variantOrder = variantOrder,
      variantImageType = variantImageType
    )
    onSave(built)
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = SellerPageBg,
      shadowElevation = 16.dp,
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .widthIn(max = 600.dp)
        .heightIn(max = 820.dp)
        .testTag("product_form_dialog")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // TOP BAR (< Back | Add Product | Draft/Quick Icon)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(48.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = SellerLabelInk
            )
          }

          Text(
            text = if (product != null) "Edit Product" else "Add Product",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SellerLabelInk
          )

          IconButton(
            onClick = {
              // Quick preset / URL importer shortcut
              activeSubEditor = SellerSubEditor.URL_PICKER
            },
            modifier = Modifier.size(48.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.EditNote,
              contentDescription = "Image URL & Presets",
              tint = SellerLabelInk
            )
          }
        }

        HorizontalDivider(color = SellerDivider)

        // SERIALIZED SELLER-CENTER BODY
        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // =========================================================
          // CARD 1: Product Image (Unlimited) *, Product Name *, Category *, Video
          // =========================================================
          SellerCard {
            // 1. Product Image (Main Photo + Small Gallery Strip Below) *
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                RequiredLabel(text = "Product Image (${imageList.size}টি)", required = true)
                Text(
                  text = "+ URL / Preset",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = SellerOrange,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { activeSubEditor = SellerSubEditor.URL_PICKER }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              if (imageList.isNotEmpty()) {
                // Selected Main Image on Top
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Box(
                    modifier = Modifier
                      .size(132.dp)
                      .clip(RoundedCornerShape(14.dp))
                      .border(2.dp, SellerOrange, RoundedCornerShape(14.dp))
                  ) {
                    AppImage(
                      model = imageList.first(),
                      contentDescription = "মেইন ছবি",
                      modifier = Modifier.fillMaxSize()
                    )
                    Box(
                      modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SellerOrange)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                      Text("★ মেইন ছবি", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Box(
                      modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.68f))
                        .clickable { imageList = imageList.drop(1) },
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Close, contentDescription = "মেইন ছবি মুছুন", tint = Color.White, modifier = Modifier.size(13.dp))
                    }
                  }

                  Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text("মেইন ছবি নির্বাচিত", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SellerLabelInk)
                    Text(
                      "নিচের গ্যালারির যেকোনো ছোট ছবিতে ট্যাপ করে মেইন ছবি হিসেবে সেট করুন।",
                      fontSize = 11.sp,
                      color = SellerMutedInk,
                      lineHeight = 15.sp
                    )
                    OutlinedButton(
                      onClick = {
                        multiImagePickerLauncher.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                      },
                      enabled = !isPickingImage,
                      border = BorderStroke(1.dp, SellerOrange),
                      shape = RoundedCornerShape(50),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                      modifier = Modifier.height(34.dp)
                    ) {
                      Text("+ আরও ছবি যোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SellerOrange)
                    }
                  }
                }

                // Small Gallery Strip Below Main Image
                Text(
                  text = "গ্যালারি (${imageList.size}টি) — ট্যাপ করে মেইন ছবি নির্বাচন করুন",
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = SellerMutedInk
                )
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  imageList.forEachIndexed { idx, img ->
                    val isMain = idx == 0
                    Box(
                      modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                          width = if (isMain) 2.dp else 1.dp,
                          color = if (isMain) SellerOrange else SellerCardBorder,
                          shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                          if (!isMain) {
                            val mutable = imageList.toMutableList()
                            val picked = mutable.removeAt(idx)
                            mutable.add(0, picked)
                            imageList = mutable
                          }
                        }
                    ) {
                      AppImage(
                        model = img,
                        contentDescription = "Gallery image ${idx + 1}",
                        modifier = Modifier.fillMaxSize()
                      )
                      if (isMain) {
                        Box(
                          modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(SellerOrange.copy(alpha = 0.92f))
                            .padding(vertical = 1.dp),
                          contentAlignment = Alignment.Center
                        ) {
                          Text("মেইন", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                      }
                      Box(
                        modifier = Modifier
                          .align(Alignment.TopEnd)
                          .padding(2.dp)
                          .size(16.dp)
                          .clip(CircleShape)
                          .background(Color.Black.copy(alpha = 0.65f))
                          .clickable { imageList = imageList.filterIndexed { i, _ -> i != idx } },
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(10.dp))
                      }
                    }
                  }

                  Box(
                    modifier = Modifier
                      .size(56.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .background(Color(0xFFFAFAFC))
                      .drawBehind {
                        drawRoundRect(
                          color = Color(0xFFCBD5E1),
                          style = Stroke(width = 2.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f), 0f)),
                          cornerRadius = CornerRadius(10.dp.toPx())
                        )
                      }
                      .clickable(enabled = !isPickingImage) {
                        multiImagePickerLauncher.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    if (isPickingImage) {
                      CircularProgressIndicator(strokeWidth = 2.dp, color = SellerOrange, modifier = Modifier.size(18.dp))
                    } else {
                      Icon(
                        imageVector = Icons.Outlined.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        tint = SellerMutedInk,
                        modifier = Modifier.size(22.dp)
                      )
                    }
                  }
                }
              } else {
                // Empty state dashed '+' upload tile
                Box(
                  modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFC))
                    .drawBehind {
                      drawRoundRect(
                        color = Color(0xFFCBD5E1),
                        style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)),
                        cornerRadius = CornerRadius(12.dp.toPx())
                      )
                    }
                    .clickable(enabled = !isPickingImage) {
                      multiImagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                  contentAlignment = Alignment.Center
                ) {
                  if (isPickingImage) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = SellerOrange, modifier = Modifier.size(20.dp))
                  } else {
                    Icon(
                      imageVector = Icons.Outlined.AddPhotoAlternate,
                      contentDescription = "Add Photo",
                      tint = SellerMutedInk,
                      modifier = Modifier.size(26.dp)
                    )
                  }
                }
              }
            }

            SellerRowDivider()

            // 2. Product Name (Any Language: Bangla / English) *
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                RequiredLabel(text = "প্রোডাক্টের নাম / Product Name", required = true)
                Text("বাংলা / English", fontSize = 10.5.sp, color = SellerMutedInk, fontWeight = FontWeight.Medium)
              }
              BasicTextField(
                value = name,
                onValueChange = {
                  name = it
                  validationError = null
                },
                textStyle = TextStyle(fontSize = 14.sp, color = SellerLabelInk, fontWeight = FontWeight.Medium),
                cursorBrush = SolidColor(SellerOrange),
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("product_form_name_input"),
                decorationBox = { inner ->
                  if (name.isEmpty()) {
                    Text(
                      text = "যেমন: এক্সক্লুসিভ জামদানি শাড়ি / Bridal Choker Set",
                      fontSize = 13.5.sp,
                      color = Color(0xFFC0C8D2)
                    )
                  }
                  inner()
                }
              )
            }

            SellerRowDivider()

            // 3. Category *
            SellerNavRow(
              label = "Category",
              required = true,
              value = if (subCategoryText.isNotBlank()) "$category > $subCategoryText" else category.ifBlank { "Select Category" },
              valueColor = SellerLabelInk,
              onClick = { activeSubEditor = SellerSubEditor.CATEGORY }
            )

            SellerRowDivider()

            // 4. Video (Toggle Switch)
            SellerSwitchRow(
              label = "Video",
              checked = videoEnabled,
              onCheckedChange = { videoEnabled = it },
              testTag = "product_form_video_switch"
            )
            AnimatedVisibility(
              visible = videoEnabled,
              enter = expandVertically() + fadeIn(),
              exit = shrinkVertically() + fadeOut()
            ) {
              Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                ProductVideoFormSection(
                  videoEnabled = videoEnabled,
                  videoType = videoType,
                  videoUrl = videoUrl,
                  videoThumbUrl = videoThumbUrl,
                  onVideoChanged = { enabled, type, url, thumb ->
                    videoEnabled = enabled
                    videoType = type
                    videoUrl = url
                    videoThumbUrl = thumb
                  }
                )
              }
            }
          }

          // =========================================================
          // CARD 2: Brand & Attributes
          // =========================================================
          SellerCard {
            // 5. Brand
            SellerNavRow(
              label = "Brand",
              required = false,
              value = brand.ifBlank { "No Brand" },
              valueColor = SellerLabelInk,
              onClick = { activeSubEditor = SellerSubEditor.BRAND }
            )
            SellerRowDivider()
            // 6. Attributes
            SellerNavRow(
              label = "Attributes",
              required = false,
              value = if (attributes.isEmpty()) "Set" else "${attributes.size} set",
              valueColor = if (attributes.isEmpty()) SellerMutedInk else SellerOrangeDeep,
              onClick = { activeSubEditor = SellerSubEditor.ATTRIBUTES }
            )
          }

          // =========================================================
          // CARD 3: Variants *
          // =========================================================
          SellerCard {
            // 7. Variants *
            val enabledVarCount = variations.count { it.enabled }.takeIf { it > 0 } ?: variations.size
            val variantCount = colors.size + sizes.size + ageGroups.size
            val variantSummary = when {
              variations.isNotEmpty() -> "$enabledVarCount Variations"
              else -> buildList {
                if (sizes.isNotEmpty()) add("${sizes.size} Sizes")
                if (colors.isNotEmpty()) add("${colors.size} Colors")
                if (ageGroups.isNotEmpty()) add("${ageGroups.size} Ages")
              }.joinToString(" • ")
            }

            SellerNavRow(
              label = "Variants",
              required = true,
              helperWarning = if (variantCount == 0 && variations.isEmpty()) "This field is required" else null,
              value = if (variantSummary.isNotBlank()) variantSummary else "Add Variations",
              valueColor = if (variations.isNotEmpty()) SellerLabelInk else if (variantSummary.isNotBlank()) SellerOrangeDeep else SellerMutedInk,
              onClick = {
                startInVariantDetails = false
                startWithSpecialSettings = false
                activeSubEditor = SellerSubEditor.VARIANTS
              }
            )
          }

          // =========================================================
          // CARD 4: Price(BDT) *, Stock *, Availability, Special Price, Special Price(BDT), Special Price Periods
          // =========================================================
          SellerCard {
            if (variations.isNotEmpty()) {
              // Auto-synced summary rows (Screenshots 6 & 8) -> tap jumps to Variation Details
              SellerNavRow(
                label = "Price(BDT)",
                required = true,
                helperWarning = if (regularParsed <= 0) "This field is required" else null,
                value = priceStr.ifBlank { "BDT" },
                valueColor = if (regularParsed > 0) SellerLabelInk else SellerMutedInk,
                onClick = {
                  startInVariantDetails = true
                  startWithSpecialSettings = false
                  activeSubEditor = SellerSubEditor.VARIANTS
                }
              )

              SellerRowDivider()

              SellerNavRow(
                label = "Stock",
                required = true,
                helperWarning = if (stockStr.isBlank()) "This field is required" else null,
                value = stockStr.ifBlank { "0" },
                valueColor = if (stockStr.isNotBlank()) SellerLabelInk else SellerMutedInk,
                onClick = {
                  startInVariantDetails = true
                  startWithSpecialSettings = false
                  activeSubEditor = SellerSubEditor.VARIANTS
                }
              )

              SellerRowDivider()

              SellerSwitchRow(
                label = "Availability",
                checked = isAvailable,
                onCheckedChange = { isAvailable = it },
                testTag = "product_form_availability_switch"
              )

              SellerRowDivider()

              SellerNavRow(
                label = "Special Price(BDT)",
                required = false,
                value = if (hasValidOffer) "$offerParsed BDT" else "Set",
                valueColor = if (hasValidOffer) Color(0xFF059669) else SellerMutedInk,
                onClick = {
                  startInVariantDetails = true
                  startWithSpecialSettings = true
                  activeSubEditor = SellerSubEditor.VARIANTS
                }
              )
            } else {
              // Single / Non-variant Pricing & Stock rows
              SellerInlineNumberRow(
                label = "Price(BDT)",
                required = true,
                helperWarning = if (regularParsed <= 0) "This field is required" else null,
                value = priceStr,
                placeholder = "BDT",
                onValueChange = {
                  priceStr = it.filter { ch -> ch.isDigit() }
                  validationError = null
                },
                testTag = "product_form_price_input"
              )

              SellerRowDivider()

              SellerInlineNumberRow(
                label = "Stock",
                required = true,
                helperWarning = if (stockStr.isBlank()) "This field is required" else null,
                value = stockStr,
                placeholder = "0",
                onValueChange = {
                  stockStr = it.filter { ch -> ch.isDigit() }
                  validationError = null
                },
                testTag = "product_form_stock_input"
              )

              SellerRowDivider()

              SellerSwitchRow(
                label = "Availability",
                checked = isAvailable,
                onCheckedChange = { isAvailable = it },
                testTag = "product_form_availability_switch"
              )

              SellerRowDivider()

              SellerSwitchRow(
                label = "Special Price",
                checked = specialPriceEnabled,
                onCheckedChange = { enabled ->
                  specialPriceEnabled = enabled
                  if (enabled) {
                    if (specialPriceStart.isBlank()) specialPriceStart = LocalDate.now().toString()
                    if (specialPriceEnd.isBlank()) specialPriceEnd = LocalDate.now().plusDays(7).toString()
                  }
                },
                testTag = "product_form_special_price_switch"
              )

              AnimatedVisibility(visible = specialPriceEnabled) {
                Column {
                  SellerRowDivider()
                  SellerInlineNumberRow(
                    label = "Special Price(BDT)",
                    required = false,
                    helperWarning = when {
                      hasValidOffer -> {
                        val pct = ((regularParsed - offerParsed) * 100) / regularParsed
                        "-$pct% ছাড় (গ্রাহক দেখবে ৳$offerParsed)"
                      }
                      regularParsed > 0 -> "বর্তমান দাম ৳$regularParsed — শুধু অফার প্রাইস লিখুন"
                      else -> null
                    },
                    helperColor = if (hasValidOffer) Color(0xFF059669) else SellerOrangeDeep,
                    value = offerPriceStr,
                    placeholder = if (regularParsed > 0) "$regularParsed (Offer)" else "Input",
                    onValueChange = {
                      offerPriceStr = it.filter { ch -> ch.isDigit() }
                      if (offerPriceStr.isNotBlank()) specialPriceEnabled = true
                    },
                    testTag = "product_form_offer_price_input"
                  )

                  SellerRowDivider()

                  val periodText = if (specialPriceStart.isNotBlank() || specialPriceEnd.isNotBlank()) {
                    "${specialPriceStart.ifBlank { "Start" }} - ${specialPriceEnd.ifBlank { "End" }}"
                  } else {
                    "Start Date - End Date"
                  }
                  SellerNavRow(
                    label = "Special Price Periods",
                    required = false,
                    value = periodText,
                    valueColor = if (specialPriceStart.isNotBlank() || specialPriceEnd.isNotBlank()) SellerLabelInk else SellerMutedInk,
                    onClick = { activeSubEditor = SellerSubEditor.SPECIAL_PERIOD }
                  )
                }
              }
            }
          }

          // =========================================================
          // CARD 5: Shipping Information *, Warranty Information
          // =========================================================
          SellerCard {
            // 12. Shipping Information *
            val shipValue = if (hasShippingSet) {
              listOfNotNull(
                shippingWeightKg.takeIf { it.isNotBlank() }?.let { "${it}kg" },
                if (shippingExpressChandpur) "Express✓" else null
              ).joinToString(" · ").ifBlank { "Configured" }
            } else "Set"

            SellerNavRow(
              label = "Shipping Information",
              required = true,
              value = shipValue,
              valueColor = if (hasShippingSet) Color(0xFF059669) else SellerMutedInk,
              onClick = { activeSubEditor = SellerSubEditor.SHIPPING }
            )

            SellerRowDivider()

            // 13. Warranty Information
            val warValue = if (hasWarrantySet) {
              listOf(warrantyType, warrantyPeriod).filter { it.isNotBlank() }.joinToString(" · ")
            } else "Set"

            SellerNavRow(
              label = "Warranty Information",
              required = false,
              value = warValue,
              valueColor = if (hasWarrantySet) SellerOrangeDeep else SellerMutedInk,
              onClick = { activeSubEditor = SellerSubEditor.WARRANTY }
            )
          }

          // =========================================================
          // CARD 6: Description *, Highlights *
          // =========================================================
          SellerCard {
            // 14. Description * (বাংলায়)
            SellerNavRow(
              label = "পণ্যের বিবরণ (Description)",
              required = true,
              value = if (description.isBlank()) "লিখুন" else "${description.length} অক্ষর",
              valueColor = if (description.isBlank()) SellerMutedInk else Color(0xFF059669),
              onClick = { activeSubEditor = SellerSubEditor.DESCRIPTION }
            )

            SellerRowDivider()

            // 15. Highlights *
            SellerNavRow(
              label = "Highlights",
              required = true,
              value = if (highlights.isEmpty()) "Set" else "${highlights.size} points",
              valueColor = if (highlights.isEmpty()) SellerMutedInk else Color(0xFF059669),
              onClick = { activeSubEditor = SellerSubEditor.HIGHLIGHTS }
            )
          }

          // Compact Sajgoj Merchandising & Channel Strip
          SellerCard {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "হোম পেজ ডিসপ্লে ও SKU ($sku)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = SellerLabelInk
                )
                Text(
                  text = "এডিট ›",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = SellerOrange,
                  modifier = Modifier.clickable { activeSubEditor = SellerSubEditor.VARIANTS }
                )
              }
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                DisplayQuickPill("🔥 Hot Sale", isHotSale, "product_form_hot_sale_switch") { isHotSale = !isHotSale }
                DisplayQuickPill("⭐ সেরা ডিল", isBestDeal, "product_form_best_deal_switch") { isBestDeal = !isBestDeal }
                DisplayQuickPill("🎁 B2G1", isB2G1, "product_form_b2g1_switch") { isB2G1 = !isB2G1 }
                DisplayQuickPill("🌐 Web", enabledForWeb, "product_form_web_switch") { enabledForWeb = !enabledForWeb }
                DisplayQuickPill("🛒 POS", enabledForPos, "product_form_pos_switch") { enabledForPos = !enabledForPos }
              }
            }
          }

          if (validationError != null) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFFEF2F2),
              border = BorderStroke(1.dp, Color(0xFFFECACA)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = validationError!!,
                color = SellerReqRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
              )
            }
          }
        }

        HorizontalDivider(color = SellerDivider)

        // =========================================================
        // STICKY BOTTOM ACTION BAR: Save as Draft | Publish Product
        // =========================================================
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { buildAndSave(saveAsDraft = true) },
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.5.dp, SellerOrange),
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = Color.White,
              contentColor = SellerOrange
            ),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("save_draft_button")
          ) {
            Text(
              text = "Save as Draft",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = SellerOrange
            )
          }

          Button(
            onClick = { buildAndSave(saveAsDraft = false) },
            colors = ButtonDefaults.buttonColors(
              containerColor = SellerOrange,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
              .weight(1.15f)
              .height(48.dp)
              .testTag("publish_product_button")
          ) {
            Text(
              text = "Publish Product",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }

  // =========================================================
  // SUB-EDITORS FOR SERIAL ROWS
  // =========================================================
  when (activeSubEditor) {
    SellerSubEditor.URL_PICKER -> {
      SellerSubModal(title = "Image URL & Presets (যত খুশি)", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        OutlinedTextField(
          value = customUrlInput,
          onValueChange = { customUrlInput = it },
          label = { Text("Image URL (https://...)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Button(
          onClick = {
            val trimmed = customUrlInput.trim()
            if (trimmed.isNotBlank()) {
              imageList = (imageList + trimmed).distinct()
              customUrlInput = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("+ ইমেজ লিস্টে যোগ করুন (${imageList.size}টি)", fontWeight = FontWeight.Bold)
        }
        Text("দ্রুত প্রিসেট:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SellerLabelInk)
        listOf(
          "res://banner_eid_special_1792530116809" to "👗 ঈদ স্পেশাল শাড়ি ব্যানার",
          "res://banner_jewelry_royal_1792530130914" to "💎 রয়েল জুয়েলারি ব্যানার",
          "res://ic_app_icon_1792530102214" to "✨ সাজগোজ ব্র্যান্ড লোগো"
        ).forEach { (url, label) ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (imageList.contains(url)) Color(0xFFFFF7ED) else Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, if (imageList.contains(url)) SellerOrange else SellerCardBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                imageList = (imageList + url).distinct()
              }
          ) {
            Text(label, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(12.dp))
          }
        }
      }
    }

    SellerSubEditor.CATEGORY -> {
      SellerSubModal(title = "Select Category", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        val catNames = remember(categories) {
          (categories.map { it.name } + listOf("শাড়ি", "জুয়েলারি", "থ্রি পিস", "পাঞ্জাবি", "বাচ্চাদের পোশাক", "কম্বো প্যাক", "কসমেটিক্স"))
            .filter { it.isNotBlank() }.distinct()
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          catNames.forEach { catName ->
            val selected = category.equals(catName, ignoreCase = true)
            Surface(
              shape = RoundedCornerShape(50),
              color = if (selected) SellerOrange else Color(0xFFF1F5F9),
              modifier = Modifier.clickable {
                category = catName
                activeSubEditor = SellerSubEditor.NONE
              }
            ) {
              Text(
                text = catName,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else SellerLabelInk,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              )
            }
          }
        }
        OutlinedTextField(
          value = subCategoryText,
          onValueChange = { subCategoryText = it },
          label = { Text("Sub-Category (ঐচ্ছিক, যেমন: Jamdani / Baby Boy Shorts)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    SellerSubEditor.BRAND -> {
      SellerSubModal(title = "Brand Selection", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        var customBrand by remember { mutableStateOf(brand) }
        val presets = listOf("No Brand", "SAJGOJ Exclusive", "Aarong", "Kay Kraft", "Anjan's", "Le Reve", "Sailor", "Yellow")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          presets.forEach { preset ->
            val chosen = brand.equals(preset, ignoreCase = true)
            Surface(
              shape = RoundedCornerShape(50),
              color = if (chosen) SellerOrange else Color(0xFFF1F5F9),
              modifier = Modifier.clickable {
                brand = preset
                activeSubEditor = SellerSubEditor.NONE
              }
            ) {
              Text(
                text = preset,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (chosen) Color.White else SellerLabelInk,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              )
            }
          }
        }
        OutlinedTextField(
          value = customBrand,
          onValueChange = { customBrand = it },
          label = { Text("কাস্টম ব্র্যান্ড নাম") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Button(
          onClick = {
            brand = customBrand.trim().ifBlank { "No Brand" }
            activeSubEditor = SellerSubEditor.NONE
          },
          colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Save Brand", fontWeight = FontWeight.Bold)
        }
      }
    }

    SellerSubEditor.ATTRIBUTES -> {
      SellerSubModal(title = "Product Attributes", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        var fabric by remember { mutableStateOf(attributes["Material"] ?: "") }
        var pattern by remember { mutableStateOf(attributes["Pattern"] ?: "") }
        var origin by remember { mutableStateOf(attributes["Origin"] ?: "Bangladesh") }
        var gender by remember { mutableStateOf(attributes["Gender"] ?: "") }
        var customKey by remember { mutableStateOf("") }
        var customVal by remember { mutableStateOf("") }
        var extraMap by remember {
          mutableStateOf(attributes.filterKeys { it !in listOf("Material", "Pattern", "Origin", "Gender") })
        }

        OutlinedTextField(
          value = fabric,
          onValueChange = { fabric = it },
          label = { Text("Material / Fabric (যেমন: Cotton, Silk, Jamdani)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = pattern,
          onValueChange = { pattern = it },
          label = { Text("Pattern / Style (যেমন: Bridal, Embroidered, Casual)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = origin,
          onValueChange = { origin = it },
          label = { Text("Country of Origin (যেমন: Bangladesh / India)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = gender,
          onValueChange = { gender = it },
          label = { Text("Target / Occasion (যেমন: Women, Eid Special)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        if (extraMap.isNotEmpty()) {
          extraMap.forEach { (k, v) ->
            Row(
              modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("$k: $v", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SellerLabelInk)
              IconButton(onClick = { extraMap = extraMap - k }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = SellerReqRed, modifier = Modifier.size(16.dp))
              }
            }
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
            value = customKey,
            onValueChange = { customKey = it },
            label = { Text("Key") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = customVal,
            onValueChange = { customVal = it },
            label = { Text("Value") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          Button(
            onClick = {
              if (customKey.isNotBlank() && customVal.isNotBlank()) {
                extraMap = extraMap + (customKey.trim() to customVal.trim())
                customKey = ""
                customVal = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep),
            contentPadding = PaddingValues(horizontal = 10.dp)
          ) {
            Text("+")
          }
        }

        Button(
          onClick = {
            val combined = buildMap {
              if (fabric.isNotBlank()) put("Material", fabric.trim())
              if (pattern.isNotBlank()) put("Pattern", pattern.trim())
              if (origin.isNotBlank()) put("Origin", origin.trim())
              if (gender.isNotBlank()) put("Gender", gender.trim())
              putAll(extraMap)
            }
            attributes = combined
            activeSubEditor = SellerSubEditor.NONE
          },
          colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Save Attributes", fontWeight = FontWeight.Bold)
        }
      }
    }

    SellerSubEditor.VARIANTS -> {
      SellerVariantsFlowDialog(
        initialSizes = sizes,
        initialColors = colors,
        initialAgeGroups = ageGroups,
        initialVariations = variations,
        initialVariantImages = variantImages,
        initialVariantOrder = variantOrder,
        initialVariantImageType = variantImageType,
        initialSku = sku,
        defaultPrice = regularParsed,
        defaultStock = stockStr.toIntOrNull() ?: 0,
        defaultSpecialPrice = offerParsed,
        defaultSpecialStart = specialPriceStart,
        defaultSpecialEnd = specialPriceEnd,
        startInDetails = startInVariantDetails,
        startWithSpecialSettings = startWithSpecialSettings,
        onDismiss = { activeSubEditor = SellerSubEditor.NONE },
        onCommitAll = { nextSizes, nextColors, nextAges, nextVariations, nextVariantImages, nextOrder, nextImgType ->
          sizes = nextSizes
          colors = nextColors
          ageGroups = nextAges
          variations = nextVariations
          variantImages = nextVariantImages
          variantOrder = nextOrder
          variantImageType = nextImgType
          val summary = ProductVariantHelper.summarizeVariations(nextVariations)
          if (summary.minRegularPrice > 0) {
            priceStr = summary.minRegularPrice.toString()
          }
          stockStr = summary.totalStock.toString()
          if (summary.specialPriceEnabled && summary.minOfferPrice > 0) {
            specialPriceEnabled = true
            offerPriceStr = summary.minOfferPrice.toString()
            if (summary.specialPriceStart.isNotBlank()) specialPriceStart = summary.specialPriceStart
            if (summary.specialPriceEnd.isNotBlank()) specialPriceEnd = summary.specialPriceEnd
          } else {
            offerPriceStr = ""
          }
          validationError = null
          activeSubEditor = SellerSubEditor.NONE
        }
      )
    }

    SellerSubEditor.SPECIAL_PERIOD -> {
      SellerSubModal(title = "Special Price Periods", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        val today = remember { LocalDate.now().toString() }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf(7 to "৭ দিন", 15 to "১৫ দিন", 30 to "৩০ দিন").forEach { (days, label) ->
            Surface(
              shape = RoundedCornerShape(50),
              color = Color(0xFFFFF7ED),
              border = BorderStroke(1.dp, SellerOrange),
              modifier = Modifier.clickable {
                specialPriceStart = today
                specialPriceEnd = LocalDate.now().plusDays(days.toLong()).toString()
              }
            ) {
              Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SellerOrangeDeep, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
          }
        }
        OutlinedTextField(
          value = specialPriceStart,
          onValueChange = { specialPriceStart = it },
          label = { Text("Start Date (YYYY-MM-DD)") },
          placeholder = { Text(today) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = specialPriceEnd,
          onValueChange = { specialPriceEnd = it },
          label = { Text("End Date (YYYY-MM-DD)") },
          placeholder = { Text(LocalDate.now().plusDays(7).toString()) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Button(
          onClick = { activeSubEditor = SellerSubEditor.NONE },
          colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Save Special Price Period", fontWeight = FontWeight.Bold)
        }
      }
    }

    SellerSubEditor.SHIPPING -> {
      SellerSubModal(title = "Shipping Information *", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        OutlinedTextField(
          value = shippingWeightKg,
          onValueChange = { shippingWeightKg = it },
          label = { Text("Package Weight (kg) (যেমন: 0.5)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = shippingDimensions,
          onValueChange = { shippingDimensions = it },
          label = { Text("Dimensions L × W × H cm (যেমন: 30 x 25 x 5 cm)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Text("Dangerous Goods / Handling:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SellerLabelInk)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("None", "Fragile", "Liquid", "Battery").forEach { option ->
            val sel = shippingDangerousGoods == option
            Surface(
              shape = RoundedCornerShape(50),
              color = if (sel) SellerOrange else Color(0xFFF1F5F9),
              modifier = Modifier.clickable { shippingDangerousGoods = option }
            ) {
              Text(
                text = option,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (sel) Color.White else SellerLabelInk,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }
        SellerSwitchRow(
          label = "চাঁদপুর Express ডেলিভারি প্রযোজ্য",
          checked = shippingExpressChandpur,
          onCheckedChange = { shippingExpressChandpur = it },
          testTag = "shipping_express_chandpur_switch"
        )
        OutlinedTextField(
          value = shippingNote,
          onValueChange = { shippingNote = it },
          label = { Text("Delivery / Packaging Note") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = sourceUrl,
          onValueChange = { sourceUrl = it },
          label = { Text("Supplier / Source Link (অপশনাল)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Button(
          onClick = { activeSubEditor = SellerSubEditor.NONE },
          colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Save Shipping Info", fontWeight = FontWeight.Bold)
        }
      }
    }

    SellerSubEditor.WARRANTY -> {
      SellerSubModal(title = "Warranty Information", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        Text("Warranty Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SellerLabelInk)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("No Warranty", "Seller Warranty", "Brand Warranty", "Replacement Guarantee").forEach { t ->
            val sel = warrantyType == t
            Surface(
              shape = RoundedCornerShape(50),
              color = if (sel) SellerOrange else Color(0xFFF1F5F9),
              modifier = Modifier.clickable { warrantyType = t }
            ) {
              Text(
                text = t,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (sel) Color.White else SellerLabelInk,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }
        Text("Warranty Period:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SellerLabelInk)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("7 Days", "15 Days", "1 Month", "3 Months", "6 Months", "1 Year").forEach { p ->
            val sel = warrantyPeriod == p
            Surface(
              shape = RoundedCornerShape(50),
              color = if (sel) Color(0xFFFFF7ED) else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (sel) SellerOrange else Color.Transparent),
              modifier = Modifier.clickable { warrantyPeriod = p }
            ) {
              Text(
                text = p,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (sel) SellerOrangeDeep else SellerLabelInk,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
        OutlinedTextField(
          value = warrantyPolicy,
          onValueChange = { warrantyPolicy = it },
          label = { Text("Warranty Policy / Terms") },
          minLines = 2,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Button(
          onClick = { activeSubEditor = SellerSubEditor.NONE },
          colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Save Warranty Info", fontWeight = FontWeight.Bold)
        }
      }
    }

    SellerSubEditor.DESCRIPTION -> {
      SellerSubModal(title = "পণ্যের বিস্তারিত বিবরণ *", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        OutlinedButton(
          onClick = {
            if (name.isBlank()) {
              aiDescError = "আগে প্রোডাক্টের নাম লিখুন।"
              return@OutlinedButton
            }
            aiDescError = null
            isGeneratingAiDesc = true
            formScope.launch {
              val eff = if (hasValidOffer) offerParsed else regularParsed
              val res = SajgojAiService.generateProductDescription(
                productName = name.trim(),
                category = category.trim(),
                price = eff,
                colors = colors,
                sizes = sizes,
                ageGroups = ageGroups
              )
              isGeneratingAiDesc = false
              res.onSuccess { generated -> description = generated }
                .onFailure { err -> aiDescError = err.localizedMessage ?: "AI বিবরণ তৈরি করতে পারেনি।" }
            }
          },
          enabled = !isGeneratingAiDesc,
          border = BorderStroke(1.dp, AdminV2CyanDeep),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(44.dp).testTag("product_form_ai_desc_button")
        ) {
          if (isGeneratingAiDesc) {
            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = AdminV2CyanDeep)
            Spacer(modifier = Modifier.width(6.dp))
            Text("তৈরি হচ্ছে...", color = AdminV2CyanDeep, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("✨ AI দিয়ে বাংলা বিবরণ লিখুন", color = AdminV2CyanDeep, fontWeight = FontWeight.Bold)
          }
        }
        if (aiDescError != null) {
          Text(aiDescError!!, color = SellerReqRed, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
        }
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("পণ্যের বিস্তারিত বিবরণ (বাংলায়) *") },
          placeholder = { Text("পণ্যের ফেব্রিক, সাইজ, ডিজাইন ও যত্নের নির্দেশনা বাংলায় লিখুন...") },
          minLines = 5,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
        Button(
          onClick = { activeSubEditor = SellerSubEditor.NONE },
          colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("বিবরণ সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
        }
      }
    }

    SellerSubEditor.HIGHLIGHTS -> {
      SellerSubModal(title = "Product Highlights *", onDismiss = { activeSubEditor = SellerSubEditor.NONE }) {
        var newHighlight by remember { mutableStateOf("") }
        val quickBullets = listOf(
          "১০০% অরিজিনাল ও প্রিমিয়াম কোয়ালিটি",
          "আরামদায়ক ও টেকসই ফেব্রিক ফিনিশিং",
          "দ্রুত হোম ডেলিভারি (চাঁদপুরে Express ২৪ ঘন্টায়)",
          "সহজ ৭ দিনের এক্সচেঞ্জ ও রিটার্ন সুবিধা"
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          quickBullets.forEach { qb ->
            val added = highlights.contains(qb)
            Surface(
              shape = RoundedCornerShape(50),
              color = if (added) Color(0xFFFFF7ED) else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (added) SellerOrange else Color.Transparent),
              modifier = Modifier.clickable {
                highlights = if (added) highlights - qb else highlights + qb
              }
            ) {
              Text(
                text = if (added) "✓ $qb" else "+ $qb",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (added) SellerOrangeDeep else SellerLabelInk,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
            value = newHighlight,
            onValueChange = { newHighlight = it },
            placeholder = { Text("নতুন হাইলাইট লিখুন...", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          Button(
            onClick = {
              val t = newHighlight.trim()
              if (t.isNotBlank() && !highlights.contains(t)) {
                highlights = highlights + t
                newHighlight = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White)
          ) {
            Text("+ Add")
          }
        }
        highlights.forEachIndexed { idx, h ->
          Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("• $h", fontSize = 12.sp, color = SellerLabelInk, modifier = Modifier.weight(1f))
            IconButton(
              onClick = { highlights = highlights.filterIndexed { i, _ -> i != idx } },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Outlined.DeleteOutline, contentDescription = "Remove highlight", tint = SellerReqRed, modifier = Modifier.size(16.dp))
            }
          }
        }
        Button(
          onClick = { activeSubEditor = SellerSubEditor.NONE },
          colors = ButtonDefaults.buttonColors(containerColor = SellerOrange, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Save Highlights", fontWeight = FontWeight.Bold)
        }
      }
    }

    SellerSubEditor.NONE -> Unit
  }
}

@Composable
private fun SellerCard(content: @Composable () -> Unit) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = BorderStroke(1.dp, SellerCardBorder),
    shadowElevation = 1.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      content()
    }
  }
}

@Composable
private fun SellerRowDivider() {
  HorizontalDivider(color = SellerDivider, thickness = 1.dp, modifier = Modifier.padding(horizontal = 14.dp))
}

@Composable
private fun RequiredLabel(text: String, required: Boolean) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
    Text(
      text = text,
      fontSize = 14.5.sp,
      fontWeight = FontWeight.Bold,
      color = SellerLabelInk
    )
    if (required) {
      Text(
        text = "*",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = SellerReqRed
      )
    }
  }
}

@Composable
private fun SellerNavRow(
  label: String,
  required: Boolean,
  helperWarning: String? = null,
  value: String,
  valueColor: Color,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      RequiredLabel(text = label, required = required)
      if (!helperWarning.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = helperWarning,
          fontSize = 11.5.sp,
          color = SellerReqRed,
          fontWeight = FontWeight.Medium
        )
      }
    }
    Spacer(modifier = Modifier.width(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = value,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium,
        color = valueColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.widthIn(max = 210.dp)
      )
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = SellerMutedInk,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
private fun SellerSwitchRow(
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontSize = 14.5.sp,
      fontWeight = FontWeight.Bold,
      color = SellerLabelInk
    )
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = SellerOrange,
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = Color(0xFFD5DCE4),
        uncheckedBorderColor = Color.Transparent
      ),
      modifier = Modifier.testTag(testTag)
    )
  }
}

@Composable
private fun SellerInlineNumberRow(
  label: String,
  required: Boolean,
  helperWarning: String? = null,
  helperColor: Color = SellerReqRed,
  value: String,
  placeholder: String,
  onValueChange: (String) -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      RequiredLabel(text = label, required = required)
      if (!helperWarning.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = helperWarning,
          fontSize = 11.5.sp,
          color = helperColor,
          fontWeight = FontWeight.Medium
        )
      }
    }
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      singleLine = true,
      textStyle = TextStyle(
        fontSize = 14.5.sp,
        fontWeight = FontWeight.Bold,
        color = SellerLabelInk,
        textAlign = TextAlign.End
      ),
      cursorBrush = SolidColor(SellerOrange),
      modifier = Modifier
        .width(120.dp)
        .testTag(testTag),
      decorationBox = { inner ->
        Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()) {
          if (value.isEmpty()) {
            Text(
              text = placeholder,
              fontSize = 14.sp,
              color = Color(0xFFC0C8D2),
              fontWeight = FontWeight.Medium
            )
          }
          inner()
        }
      }
    )
  }
}

@Composable
private fun DisplayQuickPill(
  text: String,
  selected: Boolean,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(50),
    color = if (selected) Color(0xFFFFF7ED) else Color(0xFFF8FAFC),
    border = BorderStroke(1.dp, if (selected) SellerOrange else SellerCardBorder),
    modifier = Modifier
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Text(
      text = if (selected) "✓ $text" else text,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = if (selected) SellerOrangeDeep else Color(0xFF475569),
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
    )
  }
}

@Composable
private fun SellerSubModal(
  title: String,
  onDismiss: () -> Unit,
  content: @Composable () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      shadowElevation = 18.dp,
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .widthIn(max = 520.dp)
        .heightIn(max = 680.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFC))
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(title, fontSize = 15.sp, fontWeight = FontWeight.Black, color = SellerLabelInk)
          IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = SellerLabelInk)
          }
        }
        HorizontalDivider(color = SellerDivider)
        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          content()
        }
      }
    }
  }
}
