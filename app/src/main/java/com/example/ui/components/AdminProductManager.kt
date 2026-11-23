package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.SajgojAiService
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.util.ProductMediaHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdminProductsTab(
  products: List<Product>,
  categories: List<Category> = emptyList(),
  onAddClick: () -> Unit,
  onEditClick: (Product) -> Unit,
  onDeleteClick: (String) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }
  var selectedHealthFilter by remember { mutableStateOf("ALL") } // ALL, LOW, OUT, HOT, VIDEO
  var sortMode by remember { mutableStateOf("NEWEST") } // NEWEST, PRICE_ASC, PRICE_DESC, STOCK_ASC
  var pendingDeleteId by remember { mutableStateOf<String?>(null) }

  val lowStockCount = remember(products) { products.count { it.stock in 1..5 } }
  val outOfStockCount = remember(products) { products.count { it.stock <= 0 } }
  val hotSaleCount = remember(products) { products.count { it.isHotSale } }
  val videoCount = remember(products) { products.count { ProductMediaHelper.hasActiveVideo(it) } }

  val categoryOptions = remember(categories, products) {
    listOf("All") + (categories.map { it.name } + products.map { it.category }).filter { it.isNotBlank() }.distinct()
  }

  val filteredProducts = remember(
    products,
    searchQuery,
    selectedCategory,
    selectedHealthFilter,
    sortMode
  ) {
    val q = searchQuery.trim().lowercase()
    val filtered = products.filter { p ->
      val catOk = selectedCategory == "All" || p.category == selectedCategory
      val healthOk = when (selectedHealthFilter) {
        "LOW" -> p.stock in 1..5
        "OUT" -> p.stock <= 0
        "HOT" -> p.isHotSale
        "VIDEO" -> ProductMediaHelper.hasActiveVideo(p)
        else -> true
      }
      val queryOk = q.isEmpty() ||
        p.name.lowercase().contains(q) ||
        p.sku.lowercase().contains(q) ||
        p.category.lowercase().contains(q)
      catOk && healthOk && queryOk
    }
    when (sortMode) {
      "PRICE_ASC" -> filtered.sortedBy { it.price }
      "PRICE_DESC" -> filtered.sortedByDescending { it.price }
      "STOCK_ASC" -> filtered.sortedBy { it.stock }
      else -> filtered.sortedByDescending { it.createdAt }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_products_tab"),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Clean White Search + Dynamic Filter Card
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = Color.White,
      border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
      shadowElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Search + Sort Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
              .weight(1f)
              .testTag("product_search_input"),
            placeholder = { Text("নাম, SKU বা ক্যাটাগরি...", fontSize = 12.sp) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = AdminV2CyanDeep,
                modifier = Modifier.size(18.dp)
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(48.dp)) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "সার্চ মুছুন",
                    tint = AdminInkMuted,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors()
          )

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF0F9FF),
            border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
            modifier = Modifier
              .heightIn(min = 48.dp)
              .clickable {
                sortMode = when (sortMode) {
                  "NEWEST" -> "STOCK_ASC"
                  "STOCK_ASC" -> "PRICE_ASC"
                  "PRICE_ASC" -> "PRICE_DESC"
                  else -> "NEWEST"
                }
              }
          ) {
            Box(
              modifier = Modifier.padding(horizontal = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = when (sortMode) {
                  "STOCK_ASC" -> "স্টক ↑"
                  "PRICE_ASC" -> "দাম ↑"
                  "PRICE_DESC" -> "দাম ↓"
                  else -> "নতুন"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AdminV2CyanDeep
              )
            }
          }
        }

        // Quick Inventory Health Filters
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          HealthFilterPill(
            label = "সকল (${products.size})",
            selected = selectedHealthFilter == "ALL",
            accent = AdminV2CyanDeep,
            onClick = { selectedHealthFilter = "ALL" }
          )
          HealthFilterPill(
            label = "স্টক কম ($lowStockCount)",
            selected = selectedHealthFilter == "LOW",
            accent = AdminV2Amber,
            onClick = { selectedHealthFilter = if (selectedHealthFilter == "LOW") "ALL" else "LOW" }
          )
          HealthFilterPill(
            label = "স্টক শেষ ($outOfStockCount)",
            selected = selectedHealthFilter == "OUT",
            accent = AdminV2Rose,
            onClick = { selectedHealthFilter = if (selectedHealthFilter == "OUT") "ALL" else "OUT" }
          )
          HealthFilterPill(
            label = "🔥 Hot ($hotSaleCount)",
            selected = selectedHealthFilter == "HOT",
            accent = Color(0xFFEA580C),
            onClick = { selectedHealthFilter = if (selectedHealthFilter == "HOT") "ALL" else "HOT" }
          )
          HealthFilterPill(
            label = "🎬 ভিডিও ($videoCount)",
            selected = selectedHealthFilter == "VIDEO",
            accent = AdminV2CyanDeep,
            onClick = { selectedHealthFilter = if (selectedHealthFilter == "VIDEO") "ALL" else "VIDEO" }
          )
        }

        // Category Chips Row (only if > 1 categories)
        if (categoryOptions.size > 1) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            categoryOptions.forEach { cat ->
              val isChosen = selectedCategory == cat
              val count = if (cat == "All") products.size else products.count { it.category == cat }
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isChosen) AdminV2CyanDeep else Color(0xFFF8FCFF),
                border = BorderStroke(1.dp, if (isChosen) AdminV2CyanDeep else Color(0xFFD6E8F5)),
                modifier = Modifier
                  .heightIn(min = 36.dp)
                  .clickable { selectedCategory = cat }
              ) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = if (cat == "All") "সব ক্যাটাগরি ($count)" else "$cat ($count)",
                    fontSize = 11.sp,
                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Medium,
                    color = if (isChosen) Color.White else AdminInkSecondary
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. Product Cards List or Empty State
    if (filteredProducts.isEmpty()) {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Inventory2,
            contentDescription = null,
            tint = AdminV2CyanDeep,
            modifier = Modifier.size(34.dp)
          )
          Text(
            text = "কোনো পণ্য পাওয়া যায়নি",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Text(
            text = "ফিল্টার পরিবর্তন করুন অথবা নতুন পণ্য যোগ করুন",
            fontSize = 11.5.sp,
            color = AdminInkMuted
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (searchQuery.isNotEmpty() || selectedCategory != "All" || selectedHealthFilter != "ALL") {
              OutlinedButton(
                onClick = {
                  searchQuery = ""
                  selectedCategory = "All"
                  selectedHealthFilter = "ALL"
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.heightIn(min = 48.dp)
              ) {
                Text("রিসেট", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
              }
            }
            Button(
              onClick = onAddClick,
              colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.heightIn(min = 48.dp)
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("নতুন পণ্য যোগ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredProducts, key = { it.id }) { product ->
          val isConfirming = pendingDeleteId == product.id
          ProductAdminRowCard(
            product = product,
            isConfirmingDelete = isConfirming,
            onEditClick = { onEditClick(product) },
            onRequestDelete = {
              pendingDeleteId = if (isConfirming) null else product.id
            },
            onCancelDelete = { pendingDeleteId = null },
            onConfirmDelete = {
              pendingDeleteId = null
              onDeleteClick(product.id)
            }
          )
        }
      }
    }
  }
}

@Composable
private fun HealthFilterPill(
  label: String,
  selected: Boolean,
  accent: Color,
  onClick: () -> Unit
) {
  val bg by animateColorAsState(
    targetValue = if (selected) accent else Color(0xFFF8FCFF),
    animationSpec = tween(200, easing = FastOutSlowInEasing),
    label = "health_pill_bg"
  )
  Surface(
    shape = RoundedCornerShape(50),
    color = bg,
    border = BorderStroke(1.dp, if (selected) accent else Color(0xFFD6E8F5)),
    modifier = Modifier
      .heightIn(min = 38.dp)
      .clip(RoundedCornerShape(50))
      .clickable(onClick = onClick)
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) Color.White else AdminInkPrimary
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductAdminRowCard(
  product: Product,
  isConfirmingDelete: Boolean,
  onEditClick: () -> Unit,
  onRequestDelete: () -> Unit,
  onCancelDelete: () -> Unit,
  onConfirmDelete: () -> Unit
) {
  val stockAccent = when {
    product.stock <= 0 -> AdminV2Rose
    product.stock <= 5 -> AdminV2Amber
    else -> AdminV2Emerald
  }
  val hasDiscount = product.offerPrice in 1 until product.regularPrice
  val discountPct = if (hasDiscount && product.regularPrice > 0) {
    ((product.regularPrice - product.offerPrice) * 100) / product.regularPrice
  } else 0
  val hasVideo = ProductMediaHelper.hasActiveVideo(product)

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color.White,
    border = BorderStroke(1.dp, if (isConfirmingDelete) Color(0xFFFCA5A5) else Color(0xFFD6E8F5)),
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(11.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Left Stock Health Accent Bar
        Box(
          modifier = Modifier
            .width(4.dp)
            .height(54.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(stockAccent)
        )

        // Product Thumbnail with Video Indicator
        Box(
          modifier = Modifier
            .size(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE2EFF8), RoundedCornerShape(12.dp))
        ) {
          AppImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier.fillMaxSize()
          )
          if (hasVideo) {
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(3.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(AdminV2CyanDeep.copy(alpha = 0.9f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "ভিডিও আছে",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
              )
            }
          }
        }

        // Info Column
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Text(
            text = product.name,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "৳${product.price}",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black,
              color = AdminV2CyanDeep
            )
            if (hasDiscount) {
              Text(
                text = "৳${product.regularPrice}",
                fontSize = 11.sp,
                color = AdminInkMuted,
                textDecoration = TextDecoration.LineThrough
              )
              if (discountPct > 0) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFFFEF2F2)
                ) {
                  Text(
                    text = "-$discountPct%",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AdminV2Rose,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
            }
            if (product.sku.isNotBlank()) {
              Text(
                text = "• ${product.sku}",
                fontSize = 10.sp,
                color = AdminInkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Stock Pill
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = stockAccent.copy(alpha = 0.12f)
            ) {
              Text(
                text = when {
                  product.stock <= 0 -> "স্টক শেষ (0)"
                  product.stock <= 5 -> "কম স্টক (${product.stock})"
                  else -> "স্টক: ${product.stock}"
                },
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = stockAccent,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            // Category Pill
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFF0F9FF)
            ) {
              Text(
                text = product.category,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminV2CyanDeep,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            if (product.isDraft) {
              MiniTagChip("📝 Draft", Color(0xFFFFF7ED), Color(0xFFEA580C))
            }
            if (!product.isAvailable) {
              MiniTagChip("⏸️ Unavailable", Color(0xFFFEF2F2), AdminV2Rose)
            }
            if (product.brand.isNotBlank() && product.brand != "No Brand") {
              MiniTagChip("🏷️ ${product.brand}", Color(0xFFF3E8FF), Color(0xFF7E22CE))
            }
            if (product.isHotSale) {
              MiniTagChip("🔥 Hot", Color(0xFFFFEDD5), Color(0xFFC2410C))
            }
            if (product.isBestDeal) {
              MiniTagChip("⭐ ডিল", Color(0xFFE0F2FE), AdminV2CyanDeep)
            }
            if (product.isB2G1) {
              MiniTagChip("🎁 B2G1", Color(0xFFECFDF5), AdminV2Emerald)
            }
            if (product.colors.isNotEmpty()) {
              MiniTagChip("🎨 ${product.colors.size}", Color(0xFFF1F5F9), AdminInkSecondary)
            }
            if (product.sizes.isNotEmpty()) {
              MiniTagChip("📏 ${product.sizes.size}", Color(0xFFF1F5F9), AdminInkSecondary)
            }
            if (product.ageGroups.isNotEmpty()) {
              MiniTagChip("👶 ${product.ageGroups.size}", Color(0xFFFEF3C7), AdminV2Amber)
            }
          }
        }

        // Action Buttons (48.dp touch targets)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onEditClick,
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFF0F9FF))
          ) {
            Icon(
              imageVector = Icons.Outlined.Edit,
              contentDescription = "এডিট ${product.name}",
              tint = AdminV2CyanDeep,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = onRequestDelete,
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFFEF2F2))
          ) {
            Icon(
              imageVector = Icons.Outlined.DeleteOutline,
              contentDescription = "মুছুন ${product.name}",
              tint = AdminV2Rose,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      AnimatedVisibility(
        visible = isConfirmingDelete,
        enter = expandVertically(tween(180)) + fadeIn(tween(180)),
        exit = shrinkVertically(tween(150)) + fadeOut(tween(150))
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFFEF2F2),
          border = BorderStroke(1.dp, Color(0xFFFECACA)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "\"${product.name}\" ডিলিট করবেন?",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF991B1B),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              OutlinedButton(
                onClick = onCancelDelete,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                modifier = Modifier.heightIn(min = 40.dp)
              ) {
                Text("না", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
              }
              Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2Rose, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.heightIn(min = 40.dp)
              ) {
                Text("মুছুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MiniTagChip(text: String, bg: Color, fg: Color) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = bg
  ) {
    Text(
      text = text,
      fontSize = 9.5.sp,
      fontWeight = FontWeight.Bold,
      color = fg,
      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
    )
  }
}

@Composable
fun ProductFormDialog(
  product: Product?,
  categories: List<Category>,
  storeConfig: com.example.data.model.StoreConfig = com.example.data.model.StoreConfig(),
  onDismiss: () -> Unit,
  onSave: (Product) -> Unit
) {
  SellerCenterProductFormDialog(
    product = product,
    categories = categories,
    storeConfig = storeConfig,
    onDismiss = onDismiss,
    onSave = onSave
  )
}

@Composable
internal fun VariantAccordionCard(
  title: String,
  count: Int,
  expanded: Boolean,
  accentColor: Color,
  onToggleExpand: () -> Unit,
  onClear: (() -> Unit)?,
  content: @Composable () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFFF8FCFF),
    border = BorderStroke(1.dp, Color(0xFFD0E5F4)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onToggleExpand)
          .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = title,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = accentColor.copy(alpha = 0.14f)
          ) {
            Text(
              text = "$count",
              fontSize = 10.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = accentColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (onClear != null) {
            Text(
              text = "ক্লিয়ার",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AdminV2Rose,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onClear)
                .padding(horizontal = 6.dp, vertical = 4.dp)
            )
          }
          Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) "সংকুচিত করুন" else "বিস্তারিত দেখুন",
            tint = AdminInkSecondary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(tween(200)) + fadeIn(tween(200)),
        exit = shrinkVertically(tween(160)) + fadeOut(tween(160))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          content()
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun VariantTagSectionContent(
  selected: List<String>,
  presets: List<String>,
  customInput: String,
  onCustomInputChange: (String) -> Unit,
  placeholder: String,
  addButtonText: String,
  activeBg: Color,
  presetSelectedBg: Color,
  presetSelectedFg: Color,
  onAddCustom: () -> Unit,
  onToggleItem: (String) -> Unit
) {
  if (selected.isNotEmpty()) {
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      selected.forEach { item ->
        Surface(
          shape = RoundedCornerShape(50),
          color = activeBg,
          modifier = Modifier.clickable { onToggleItem(item) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = item, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "বাদ দিন $item",
              tint = Color.White,
              modifier = Modifier.size(13.dp)
            )
          }
        }
      }
    }
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    OutlinedTextField(
      value = customInput,
      onValueChange = onCustomInputChange,
      placeholder = { Text(placeholder, fontSize = 11.sp) },
      singleLine = true,
      shape = RoundedCornerShape(12.dp),
      colors = adminOutlinedTextFieldColors(),
      modifier = Modifier
        .weight(1f)
        .heightIn(min = 48.dp)
    )

    Button(
      onClick = onAddCustom,
      colors = ButtonDefaults.buttonColors(containerColor = activeBg, contentColor = Color.White),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.heightIn(min = 48.dp)
    ) {
      Text(addButtonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
  }

  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    presets.forEach { option ->
      val isAdded = selected.contains(option)
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isAdded) presetSelectedBg else Color.White,
        border = BorderStroke(1.dp, if (isAdded) presetSelectedFg else Color(0xFFD0E5F4)),
        modifier = Modifier
          .heightIn(min = 34.dp)
          .clickable { onToggleItem(option) }
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)) {
          Text(
            text = if (isAdded) "✓ $option" else "+ $option",
            fontSize = 11.sp,
            fontWeight = if (isAdded) FontWeight.Bold else FontWeight.Normal,
            color = if (isAdded) presetSelectedFg else AdminInkPrimary
          )
        }
      }
    }
  }
}
