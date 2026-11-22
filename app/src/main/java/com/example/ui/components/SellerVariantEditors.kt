package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ProductVariation
import com.example.util.ProductMediaHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

private val DarazOrange = Color(0xFFF97316)
private val DarazOrangeDeep = Color(0xFFEA580C)
private val DarazBg = Color(0xFFF4F6F9)
private val DarazBorder = Color(0xFFE7ECF2)
private val DarazInk = Color(0xFF1E293B)
private val DarazMuted = Color(0xFF94A3B8)
private val DarazReqRed = Color(0xFFEF4444)

private val PRESET_SIZE_OPTIONS = listOf(
  "3-4yrs", "4-5 yrs", "5-6yrs", "6-7yrs",
  "0-3 years", "3-12 months", "0-12 months",
  "Free Size", "XS", "S", "M", "L", "XL", "XXL", "3XL",
  "22", "24", "26", "28", "30", "32", "34", "36", "38", "40", "42", "44"
)

private val PRESET_COLOR_OPTIONS = listOf(
  "Black", "Blue", "Brown", "Green", "White", "Grey",
  "Navy Blue", "Olive", "Red", "Maroon", "Pink", "Yellow",
  "Purple", "Golden", "Orange", "Beige",
  "লাল", "মেরুন", "কালো", "সবুজ", "নীল", "সাদা", "গোলাপী", "গোল্ডেন"
)

private val PRESET_AGE_OPTIONS = listOf(
  "0-6 months", "6-12 months", "1-2 yrs", "2-3 yrs",
  "3-4 yrs", "4-5 yrs", "5-6 yrs", "6-7 yrs", "7-8 yrs", "9-10 yrs"
)

fun buildCartesianVariations(
  sizes: List<String>,
  colors: List<String>,
  ageGroups: List<String>,
  existing: List<ProductVariation>,
  defaultPrice: Int,
  defaultStock: Int,
  defaultSpecialPrice: Int = 0,
  defaultSpecialStart: String = "",
  defaultSpecialEnd: String = "",
  skuPrefix: String = "SAJ"
): List<ProductVariation> {
  val sizeList = sizes.ifEmpty { listOf("") }
  val colorList = colors.ifEmpty { listOf("") }
  val ageList = ageGroups.ifEmpty { listOf("") }

  if (sizes.isEmpty() && colors.isEmpty() && ageGroups.isEmpty()) {
    return emptyList()
  }

  val result = mutableListOf<ProductVariation>()
  var index = 1
  for (sz in sizeList) {
    for (col in colorList) {
      for (age in ageList) {
        val prev = existing.firstOrNull { v ->
          v.size.equals(sz, ignoreCase = true) &&
            v.color.equals(col, ignoreCase = true) &&
            v.ageGroup.equals(age, ignoreCase = true)
        }
        val cleanId = "var_${sz}_${col}_${age}_${index}".replace(Regex("[^a-zA-Z0-9_-]"), "_")
        result.add(
          prev?.copy(id = prev.id.ifBlank { cleanId }) ?: ProductVariation(
            id = cleanId,
            size = sz,
            color = col,
            ageGroup = age,
            price = defaultPrice.coerceAtLeast(0),
            specialPrice = if (defaultSpecialPrice in 1 until defaultPrice) defaultSpecialPrice else 0,
            specialPriceStart = defaultSpecialStart,
            specialPriceEnd = defaultSpecialEnd,
            stock = defaultStock.coerceAtLeast(0),
            enabled = true,
            sku = if (skuPrefix.isNotBlank()) "$skuPrefix-$index" else "VAR-$index"
          )
        )
        index++
      }
    }
  }
  return result
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SellerVariantsFlowDialog(
  initialSizes: List<String>,
  initialColors: List<String>,
  initialAgeGroups: List<String>,
  initialVariations: List<ProductVariation>,
  initialVariantImages: Map<String, String>,
  initialVariantOrder: List<String>,
  initialVariantImageType: String,
  initialSku: String,
  defaultPrice: Int,
  defaultStock: Int,
  defaultSpecialPrice: Int,
  defaultSpecialStart: String,
  defaultSpecialEnd: String,
  startInDetails: Boolean = false,
  startWithSpecialSettings: Boolean = false,
  onDismiss: () -> Unit,
  onCommitAll: (
    sizes: List<String>,
    colors: List<String>,
    ageGroups: List<String>,
    variations: List<ProductVariation>,
    variantImages: Map<String, String>,
    variantOrder: List<String>,
    variantImageType: String
  ) -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var sizes by remember { mutableStateOf(initialSizes) }
  var colors by remember { mutableStateOf(initialColors) }
  var ageGroups by remember { mutableStateOf(initialAgeGroups) }
  var variantImages by remember { mutableStateOf(initialVariantImages) }
  var variantOrder by remember {
    mutableStateOf(
      initialVariantOrder.filter { it == "Size" || it == "Color Family" }.ifEmpty {
        listOf("Size", "Color Family")
      }
    )
  }
  var showColorTypeCard by remember {
    mutableStateOf(initialColors.isNotEmpty() || initialVariantOrder.contains("Color Family") || initialVariations.any { it.color.isNotBlank() })
  }
  var showAgeTypeCard by remember {
    mutableStateOf(initialAgeGroups.isNotEmpty() || initialVariations.any { it.ageGroup.isNotBlank() })
  }
  var variantImageType by remember {
    mutableStateOf(
      if (initialVariantImageType.isNotBlank()) initialVariantImageType
      else if (initialVariantImages.isNotEmpty()) "Color Family"
      else ""
    )
  }
  var variations by remember { mutableStateOf(initialVariations) }
  var inDetailsScreen by remember { mutableStateOf(startInDetails && initialVariations.isNotEmpty()) }
  var showSpecialSettings by remember { mutableStateOf(startWithSpecialSettings) }
  var activePickerType by remember { mutableStateOf<String?>(null) }
  var pendingImageOptionKey by remember { mutableStateOf<String?>(null) }
  var urlEditOptionKey by remember { mutableStateOf<String?>(null) }
  var showBulkEditModal by remember { mutableStateOf(false) }

  val singleVariantImagePicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    val targetKey = pendingImageOptionKey
    pendingImageOptionKey = null
    if (uri != null && !targetKey.isNullOrBlank()) {
      scope.launch {
        val savedPath = withContext(Dispatchers.IO) {
          ProductMediaHelper.copyPickedImageToInternalStorage(context, uri)
        }
        if (!savedPath.isNullOrBlank()) {
          variantImages = variantImages + (targetKey to savedPath)
        }
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = DarazBg,
      shadowElevation = 18.dp,
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .widthIn(max = 600.dp)
        .heightIn(max = 820.dp)
        .testTag(if (inDetailsScreen) "variation_details_dialog" else "variants_editor_dialog")
    ) {
      if (!inDetailsScreen) {
        // ============================================================
        // SCREEN A: VARIANTS BUILDER (Screenshots 2/1, 2, 4)
        // ============================================================
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.White)
              .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarazInk)
            }
            Text("Variants", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarazInk)
            IconButton(
              onClick = {
                if (!showColorTypeCard) showColorTypeCard = true
                else showAgeTypeCard = !showAgeTypeCard
              },
              modifier = Modifier.size(48.dp)
            ) {
              Icon(Icons.Outlined.EditNote, contentDescription = "Toggle Variant Type", tint = DarazInk)
            }
          }
          HorizontalDivider(color = Color(0xFFF1F4F8))

          Column(
            modifier = Modifier
              .weight(1f, fill = false)
              .verticalScroll(rememberScrollState())
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            val orderedTypes = buildList {
              variantOrder.forEach { t ->
                if (t == "Size") add("Size")
                else if (t == "Color Family" && showColorTypeCard) add("Color Family")
              }
              if (!contains("Size")) add(0, "Size")
              if (showColorTypeCard && !contains("Color Family")) add("Color Family")
            }

            if (orderedTypes.size >= 2) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    variantOrder = orderedTypes.reversed()
                  }
                  .padding(horizontal = 4.dp, vertical = 6.dp)
                  .testTag("switch_variant_type_order_button")
              ) {
                Icon(Icons.Default.SwapVert, contentDescription = null, tint = DarazOrangeDeep, modifier = Modifier.size(18.dp))
                Text(
                  text = "Switch Variant Type Order",
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = DarazOrangeDeep
                )
              }
            }

            orderedTypes.forEach { typeName ->
              val selectedOptions = if (typeName == "Size") sizes else colors
              val isAddImageActive = variantImageType == typeName
              VariantTypeCard(
                typeName = typeName,
                selectedOptions = selectedOptions,
                showAddImageRadio = orderedTypes.size >= 2 || typeName == "Color Family",
                isAddImageSelected = isAddImageActive,
                variantImages = variantImages,
                onToggleAddImage = {
                  variantImageType = if (isAddImageActive) "" else typeName
                },
                onRemoveOption = { opt ->
                  if (typeName == "Size") sizes = sizes.filterNot { it == opt }
                  else colors = colors.filterNot { it == opt }
                },
                onPickImageForOption = { opt ->
                  pendingImageOptionKey = opt
                  singleVariantImagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                onUrlEditForOption = { opt -> urlEditOptionKey = opt },
                onClearImageForOption = { opt -> variantImages = variantImages - opt },
                onOpenOptions = { activePickerType = typeName }
              )
            }

            if (showAgeTypeCard) {
              VariantTypeCard(
                typeName = "Kids Age",
                selectedOptions = ageGroups,
                showAddImageRadio = false,
                isAddImageSelected = false,
                variantImages = variantImages,
                onToggleAddImage = {},
                onRemoveOption = { opt -> ageGroups = ageGroups.filterNot { it == opt } },
                onPickImageForOption = {},
                onUrlEditForOption = {},
                onClearImageForOption = {},
                onOpenOptions = { activePickerType = "Kids Age" }
              )
            }

            if (!showColorTypeCard || !showAgeTypeCard) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .drawBehind {
                      drawRoundRect(
                        color = DarazOrange,
                        style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)),
                        cornerRadius = CornerRadius(10.dp.toPx())
                      )
                    }
                    .clickable {
                      if (!showColorTypeCard) showColorTypeCard = true
                      else showAgeTypeCard = true
                    }
                    .testTag("add_variant_type_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = DarazOrangeDeep, modifier = Modifier.size(18.dp))
                    Text(
                      text = if (!showColorTypeCard) "Variant Type" else "Add Kids Age Variant",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = DarazInk
                    )
                  }
                }
              }
            }
          }

          HorizontalDivider(color = Color(0xFFF1F4F8))
          val hasAnyOption = sizes.isNotEmpty() || colors.isNotEmpty() || ageGroups.isNotEmpty()
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.White)
              .padding(horizontal = 16.dp, vertical = 12.dp)
          ) {
            Button(
              onClick = {
                variations = buildCartesianVariations(
                  sizes = sizes,
                  colors = colors,
                  ageGroups = ageGroups,
                  existing = variations,
                  defaultPrice = defaultPrice,
                  defaultStock = defaultStock,
                  defaultSpecialPrice = defaultSpecialPrice,
                  defaultSpecialStart = defaultSpecialStart,
                  defaultSpecialEnd = defaultSpecialEnd,
                  skuPrefix = initialSku.ifBlank { "SAJ" }
                )
                inDetailsScreen = true
              },
              enabled = hasAnyOption,
              colors = ButtonDefaults.buttonColors(
                containerColor = DarazOrange,
                disabledContainerColor = Color(0xFFCBD5E1),
                contentColor = Color.White,
                disabledContentColor = Color.White
              ),
              shape = RoundedCornerShape(50),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("variants_next_price_stock_button")
            ) {
              Text("Next: Set Price & Stock", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      } else {
        // ============================================================
        // SCREEN B: VARIATION DETAILS (Screenshots 5 & 7)
        // ============================================================
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.White)
              .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            IconButton(onClick = { inDetailsScreen = false }, modifier = Modifier.size(48.dp)) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Variants", tint = DarazInk)
            }
            Text("Variation Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarazInk)
            IconButton(onClick = { showBulkEditModal = true }, modifier = Modifier.size(48.dp)) {
              Icon(Icons.Outlined.EditNote, contentDescription = "Bulk Edit", tint = DarazInk)
            }
          }
          HorizontalDivider(color = Color(0xFFF1F4F8))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(DarazBg)
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { showSpecialSettings = !showSpecialSettings }
                .padding(vertical = 4.dp)
                .testTag("toggle_special_price_settings_button")
            ) {
              Icon(
                imageVector = if (showSpecialSettings) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = DarazInk,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = if (showSpecialSettings) "Hide Special Price Settings" else "View Special Price Settings",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = DarazInk
              )
            }

            Text(
              text = "Bulk Edit",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Bold,
              color = DarazOrangeDeep,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { showBulkEditModal = true }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("variation_bulk_edit_button")
            )
          }

          Column(
            modifier = Modifier
              .weight(1f, fill = false)
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            variations.forEachIndexed { idx, v ->
              val titleLabel = buildList {
                variantOrder.forEach { t ->
                  if (t == "Size" && v.size.isNotBlank()) add(v.size)
                  else if (t == "Color Family" && v.color.isNotBlank()) add(v.color)
                }
                if (v.size.isNotBlank() && !variantOrder.contains("Size")) add(v.size)
                if (v.color.isNotBlank() && !variantOrder.contains("Color Family")) add(v.color)
                if (v.ageGroup.isNotBlank()) add(v.ageGroup)
              }.joinToString(", ").ifBlank { "Variation #${idx + 1}" }

              val thumbUrl = variantImages[v.color]?.takeIf { it.isNotBlank() }
                ?: variantImages[v.size]?.takeIf { it.isNotBlank() }

              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (v.enabled) Color.White else Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, DarazBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      modifier = Modifier.weight(1f)
                    ) {
                      if (!thumbUrl.isNullOrBlank()) {
                        Box(
                          modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, DarazBorder, RoundedCornerShape(6.dp))
                        ) {
                          AppImage(model = thumbUrl, contentDescription = titleLabel, modifier = Modifier.fillMaxSize())
                        }
                      }
                      Text(
                        text = titleLabel,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (v.enabled) DarazInk else DarazMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }

                    Switch(
                      checked = v.enabled,
                      onCheckedChange = { checked ->
                        variations = variations.toMutableList().also { it[idx] = v.copy(enabled = checked) }
                      },
                      colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DarazOrange,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD5DCE4),
                        uncheckedBorderColor = Color.Transparent
                      )
                    )
                  }

                  if (!showSpecialSettings) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Price", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                        DarazBdtBox(
                          value = if (v.price > 0) v.price.toString() else "",
                          placeholder = "199",
                          onValueChange = { txt ->
                            val parsed = txt.filter { it.isDigit() }.toIntOrNull() ?: 0
                            variations = variations.toMutableList().also { it[idx] = v.copy(price = parsed) }
                          },
                          testTag = "var_price_$idx"
                        )
                      }
                      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Stock", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                        DarazStockBox(
                          value = v.stock.toString(),
                          placeholder = "10",
                          onValueChange = { txt ->
                            val parsed = txt.filter { it.isDigit() }.toIntOrNull() ?: 0
                            variations = variations.toMutableList().also { it[idx] = v.copy(stock = parsed) }
                          },
                          testTag = "var_stock_$idx"
                        )
                      }
                    }
                  } else {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Price", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                        DarazBdtBox(
                          value = if (v.price > 0) v.price.toString() else "",
                          placeholder = "50",
                          onValueChange = { txt ->
                            val parsed = txt.filter { it.isDigit() }.toIntOrNull() ?: 0
                            variations = variations.toMutableList().also { it[idx] = v.copy(price = parsed) }
                          },
                          testTag = "var_price_$idx"
                        )
                      }
                      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Special Price", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                        DarazBdtBox(
                          value = if (v.specialPrice > 0) v.specialPrice.toString() else "",
                          placeholder = if (v.price > 0) v.price.toString() else "Offer",
                          onValueChange = { txt ->
                            val parsed = txt.filter { it.isDigit() }.toIntOrNull() ?: 0
                            variations = variations.toMutableList().also {
                              val today = LocalDate.now().toString()
                              val weekLater = LocalDate.now().plusDays(7).toString()
                              it[idx] = v.copy(
                                specialPrice = parsed,
                                specialPriceStart = v.specialPriceStart.ifBlank { if (parsed > 0) today else "" },
                                specialPriceEnd = v.specialPriceEnd.ifBlank { if (parsed > 0) weekLater else "" }
                              )
                            }
                          },
                          testTag = "var_special_price_$idx"
                        )
                      }
                    }

                    Text("Special Price Periods", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(14.dp)
                          .clip(CircleShape)
                          .border(3.dp, DarazOrange, CircleShape)
                      )
                      DarazDateBox(
                        value = v.specialPriceStart,
                        placeholder = LocalDate.now().toString(),
                        onValueChange = { s ->
                          variations = variations.toMutableList().also { it[idx] = v.copy(specialPriceStart = s) }
                        },
                        modifier = Modifier.weight(1f)
                      )
                      Text("-", fontWeight = FontWeight.Bold, color = DarazInk)
                      DarazDateBox(
                        value = v.specialPriceEnd,
                        placeholder = LocalDate.now().plusDays(7).toString(),
                        onValueChange = { e ->
                          variations = variations.toMutableList().also { it[idx] = v.copy(specialPriceEnd = e) }
                        },
                        modifier = Modifier.weight(1f)
                      )
                    }

                    Column(
                      modifier = Modifier.fillMaxWidth(0.48f),
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text("Stock", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                      DarazStockBox(
                        value = v.stock.toString(),
                        placeholder = "50",
                        onValueChange = { txt ->
                          val parsed = txt.filter { it.isDigit() }.toIntOrNull() ?: 0
                          variations = variations.toMutableList().also { it[idx] = v.copy(stock = parsed) }
                        },
                        testTag = "var_stock_$idx"
                      )
                    }
                  }
                }
              }
            }
          }

          HorizontalDivider(color = Color(0xFFF1F4F8))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.White)
              .padding(horizontal = 16.dp, vertical = 12.dp)
          ) {
            Button(
              onClick = {
                onCommitAll(
                  sizes,
                  colors,
                  ageGroups,
                  variations,
                  variantImages,
                  variantOrder,
                  variantImageType
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = DarazOrange, contentColor = Color.White),
              shape = RoundedCornerShape(50),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("variation_details_save_button")
            ) {
              Text("Save", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Options Picker Modal (Screenshot 3)
  val pickerType = activePickerType
  if (pickerType != null) {
    val presetList = when (pickerType) {
      "Size" -> PRESET_SIZE_OPTIONS
      "Color Family" -> PRESET_COLOR_OPTIONS
      else -> PRESET_AGE_OPTIONS
    }
    val initialSelected = when (pickerType) {
      "Size" -> sizes
      "Color Family" -> colors
      else -> ageGroups
    }
    SellerVariantOptionsPickerDialog(
      typeName = pickerType,
      presets = presetList,
      initialSelected = initialSelected,
      onDismiss = { activePickerType = null },
      onSave = { picked ->
        when (pickerType) {
          "Size" -> sizes = picked
          "Color Family" -> colors = picked
          else -> ageGroups = picked
        }
        activePickerType = null
      }
    )
  }

  // URL Input dialog for a specific variant option image
  val targetUrlOpt = urlEditOptionKey
  if (targetUrlOpt != null) {
    var urlInput by remember(targetUrlOpt) { mutableStateOf(variantImages[targetUrlOpt] ?: "") }
    Dialog(onDismissRequest = { urlEditOptionKey = null }) {
      Surface(shape = RoundedCornerShape(18.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.9f)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("'$targetUrlOpt' এর জন্য ছবির লিংক", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarazInk)
          OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            label = { Text("Image URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { urlEditOptionKey = null }, modifier = Modifier.weight(1f)) {
              Text("Cancel")
            }
            Button(
              onClick = {
                val t = urlInput.trim()
                variantImages = if (t.isNotBlank()) variantImages + (targetUrlOpt to t) else variantImages - targetUrlOpt
                urlEditOptionKey = null
              },
              colors = ButtonDefaults.buttonColors(containerColor = DarazOrange, contentColor = Color.White),
              modifier = Modifier.weight(1f)
            ) {
              Text("Save")
            }
          }
        }
      }
    }
  }

  // Bulk Edit Modal
  if (showBulkEditModal) {
    var bulkPrice by remember { mutableStateOf(if (defaultPrice > 0) defaultPrice.toString() else "") }
    var bulkStock by remember { mutableStateOf("") }
    var bulkSpecial by remember { mutableStateOf("") }
    var bulkStart by remember { mutableStateOf(LocalDate.now().toString()) }
    var bulkEnd by remember { mutableStateOf(LocalDate.now().plusDays(7).toString()) }
    Dialog(onDismissRequest = { showBulkEditModal = false }) {
      Surface(shape = RoundedCornerShape(18.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.92f)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Bulk Edit Variations", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarazInk)
          OutlinedTextField(
            value = bulkPrice,
            onValueChange = { bulkPrice = it.filter { ch -> ch.isDigit() } },
            label = { Text("Price (BDT)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = bulkStock,
            onValueChange = { bulkStock = it.filter { ch -> ch.isDigit() } },
            label = { Text("Stock (per variation)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = bulkSpecial,
            onValueChange = { bulkSpecial = it.filter { ch -> ch.isDigit() } },
            label = { Text("Special Price (BDT, Optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          if (bulkSpecial.isNotBlank()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = bulkStart,
                onValueChange = { bulkStart = it },
                label = { Text("Start Date") },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = bulkEnd,
                onValueChange = { bulkEnd = it },
                label = { Text("End Date") },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )
            }
          }
          Button(
            onClick = {
              val p = bulkPrice.toIntOrNull()
              val s = bulkStock.toIntOrNull()
              val sp = bulkSpecial.toIntOrNull()
              variations = variations.map { v ->
                if (!v.enabled) v
                else v.copy(
                  price = p ?: v.price,
                  stock = s ?: v.stock,
                  specialPrice = sp ?: v.specialPrice,
                  specialPriceStart = if (sp != null && sp > 0) bulkStart else v.specialPriceStart,
                  specialPriceEnd = if (sp != null && sp > 0) bulkEnd else v.specialPriceEnd
                )
              }
              showBulkEditModal = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = DarazOrange, contentColor = Color.White),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(46.dp)
          ) {
            Text("Apply to All Active Variations", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VariantTypeCard(
  typeName: String,
  selectedOptions: List<String>,
  showAddImageRadio: Boolean,
  isAddImageSelected: Boolean,
  variantImages: Map<String, String>,
  onToggleAddImage: () -> Unit,
  onRemoveOption: (String) -> Unit,
  onPickImageForOption: (String) -> Unit,
  onUrlEditForOption: (String) -> Unit,
  onClearImageForOption: (String) -> Unit,
  onOpenOptions: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = BorderStroke(1.dp, DarazBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(typeName, fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = DarazInk)
        Text("*", fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = DarazReqRed)
      }

      if (showAddImageRadio) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggleAddImage)
            .padding(vertical = 2.dp)
            .testTag("variant_add_image_toggle_$typeName")
        ) {
          Box(
            modifier = Modifier
              .size(18.dp)
              .clip(CircleShape)
              .border(
                width = if (isAddImageSelected) 5.dp else 1.5.dp,
                color = if (isAddImageSelected) DarazOrange else Color(0xFFCBD5E1),
                shape = CircleShape
              )
          )
          Text("Add Image", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarazInk)
        }
      }

      if (selectedOptions.isNotEmpty()) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          selectedOptions.forEach { opt ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFF1F5F9)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(opt, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DarazInk)
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Remove $opt",
                  tint = Color(0xFF94A3B8),
                  modifier = Modifier
                    .size(14.dp)
                    .clickable { onRemoveOption(opt) }
                )
              }
            }
          }
        }

        if (isAddImageSelected) {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            selectedOptions.forEach { opt ->
              val img = variantImages[opt]
              Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                  modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFAFAFC))
                    .border(
                      width = if (!img.isNullOrBlank()) 1.5.dp else 1.dp,
                      color = if (!img.isNullOrBlank()) DarazOrange else Color(0xFFCBD5E1),
                      shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onPickImageForOption(opt) }
                    .testTag("variant_image_tile_$opt"),
                  contentAlignment = Alignment.Center
                ) {
                  if (!img.isNullOrBlank()) {
                    AppImage(model = img, contentDescription = opt, modifier = Modifier.fillMaxSize())
                    Box(
                      modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.68f))
                        .clickable { onClearImageForOption(opt) },
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Close, contentDescription = "Clear image", tint = Color.White, modifier = Modifier.size(10.dp))
                    }
                  } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = "Add $opt photo", tint = DarazMuted, modifier = Modifier.size(20.dp))
                      Text("+ Add", fontSize = 9.sp, color = DarazMuted, fontWeight = FontWeight.SemiBold)
                    }
                  }
                }
                Text(
                  text = opt,
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = DarazInk,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.widthIn(max = 68.dp)
                )
                Text(
                  text = "URL",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = DarazOrangeDeep,
                  modifier = Modifier.clickable { onUrlEditForOption(opt) }
                )
              }
            }
          }
        }
      }

      OutlinedButton(
        onClick = onOpenOptions,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, DarazOrange),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        modifier = Modifier
          .height(36.dp)
          .testTag("variant_add_options_$typeName")
      ) {
        Text("+ Options", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarazOrange)
      }
    }
  }
}

@Composable
private fun SellerVariantOptionsPickerDialog(
  typeName: String,
  presets: List<String>,
  initialSelected: List<String>,
  onDismiss: () -> Unit,
  onSave: (List<String>) -> Unit
) {
  var selected by remember { mutableStateOf(initialSelected) }
  var allOptions by remember(presets, initialSelected) {
    mutableStateOf((initialSelected + presets).distinct())
  }
  var showCustomInput by remember { mutableStateOf(false) }
  var customValue by remember { mutableStateOf("") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
      color = Color.White,
      shadowElevation = 20.dp,
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .widthIn(max = 560.dp)
        .heightIn(max = 720.dp)
        .testTag("variant_options_picker_dialog")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
          Text(
            text = "$typeName(${selected.size}/${allOptions.size})",
            fontSize = 16.5.sp,
            fontWeight = FontWeight.Bold,
            color = DarazInk,
            modifier = Modifier.align(Alignment.Center)
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .size(32.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
          }
        }
        HorizontalDivider(color = Color(0xFFF1F4F8))

        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
        ) {
          allOptions.forEach { option ->
            val isChecked = selected.contains(option)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  selected = if (isChecked) selected.filterNot { it == option } else selected + option
                }
                .padding(horizontal = 18.dp, vertical = 14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = option,
                fontSize = 15.sp,
                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                color = DarazInk
              )
              if (isChecked) {
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(DarazOrange),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(14.dp))
                }
              }
            }
            HorizontalDivider(color = Color(0xFFF8FAFC), modifier = Modifier.padding(horizontal = 16.dp))
          }
        }

        HorizontalDivider(color = Color(0xFFF1F4F8))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (showCustomInput) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = customValue,
                onValueChange = { customValue = it },
                placeholder = { Text("নতুন $typeName লিখুন (বাংলা / English)...", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .testTag("variant_custom_option_input")
              )
              Button(
                onClick = {
                  val t = customValue.trim()
                  if (t.isNotBlank()) {
                    if (!allOptions.contains(t)) allOptions = listOf(t) + allOptions
                    if (!selected.contains(t)) selected = selected + t
                    customValue = ""
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarazOrange, contentColor = Color.White)
              ) {
                Text("+ Add")
              }
            }
          } else {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showCustomInput = true }
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.Center
            ) {
              Text("Spot a missing attribute value? ", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
              Text("click me", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
            }
          }

          Button(
            onClick = { onSave(selected) },
            colors = ButtonDefaults.buttonColors(containerColor = DarazOrange, contentColor = Color.White),
            shape = RoundedCornerShape(50),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("variant_options_save_button")
          ) {
            Text("Save", fontSize = 15.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun DarazBdtBox(
  value: String,
  placeholder: String,
  onValueChange: (String) -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(42.dp)
      .clip(RoundedCornerShape(8.dp))
      .border(1.dp, DarazBorder, RoundedCornerShape(8.dp)),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .height(42.dp)
        .background(Color(0xFFF1F5F9))
        .padding(horizontal = 10.dp),
      contentAlignment = Alignment.Center
    ) {
      Text("BDT", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
    }
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      singleLine = true,
      textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarazInk),
      cursorBrush = SolidColor(DarazOrange),
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 10.dp)
        .testTag(testTag),
      decorationBox = { inner ->
        if (value.isEmpty()) {
          Text(placeholder, fontSize = 13.5.sp, color = Color(0xFFCBD5E1))
        }
        inner()
      }
    )
  }
}

@Composable
private fun DarazStockBox(
  value: String,
  placeholder: String,
  onValueChange: (String) -> Unit,
  testTag: String
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(42.dp)
      .clip(RoundedCornerShape(8.dp))
      .border(1.dp, DarazBorder, RoundedCornerShape(8.dp))
      .padding(horizontal = 12.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      singleLine = true,
      textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarazInk),
      cursorBrush = SolidColor(DarazOrange),
      modifier = Modifier
        .fillMaxWidth()
        .testTag(testTag),
      decorationBox = { inner ->
        if (value.isEmpty()) {
          Text(placeholder, fontSize = 13.5.sp, color = Color(0xFFCBD5E1))
        }
        inner()
      }
    )
  }
}

@Composable
private fun DarazDateBox(
  value: String,
  placeholder: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .height(38.dp)
      .clip(RoundedCornerShape(8.dp))
      .border(1.dp, DarazBorder, RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      singleLine = true,
      textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DarazInk),
      cursorBrush = SolidColor(DarazOrange),
      modifier = Modifier.weight(1f),
      decorationBox = { inner ->
        if (value.isEmpty()) {
          Text(placeholder, fontSize = 11.5.sp, color = Color(0xFFCBD5E1), maxLines = 1)
        }
        inner()
      }
    )
    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = DarazMuted, modifier = Modifier.size(14.dp))
  }
}
