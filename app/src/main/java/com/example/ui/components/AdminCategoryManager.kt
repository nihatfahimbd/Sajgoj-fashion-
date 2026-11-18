package com.example.ui.components

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Category
import com.example.data.model.Product

@Composable
fun AdminCategoriesTab(
  categories: List<Category>,
  products: List<Product>,
  onAddClick: () -> Unit,
  onDeleteCategory: (String) -> Unit,
  onEditCategory: ((Category) -> Unit)? = null,
  onToggleSlider: ((Category) -> Unit)? = null
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedTypeFilter by remember { mutableStateOf("ALL") } // ALL, main, sub, parent
  var pendingDeleteId by remember { mutableStateOf<String?>(null) }

  val categoryNameById = remember(categories) {
    categories.associate { it.id to it.name }
  }
  val productCountByCategory = remember(products) {
    products.groupingBy { it.category }.eachCount()
  }

  val mainCount = remember(categories) { categories.count { it.type == "main" } }
  val subCount = remember(categories) { categories.count { it.type == "sub" } }
  val parentCount = remember(categories) { categories.count { it.type == "parent" } }
  val sliderActiveCount = remember(categories) { categories.count { it.type == "main" && it.showInSlider } }

  val filteredCategories = remember(categories, searchQuery, selectedTypeFilter, categoryNameById) {
    val q = searchQuery.trim().lowercase()
    categories.filter { cat ->
      val typeMatches = selectedTypeFilter == "ALL" || cat.type.equals(selectedTypeFilter, ignoreCase = true)
      val parentName = categoryNameById[cat.parentId].orEmpty().lowercase()
      val queryMatches = q.isEmpty() ||
        cat.name.lowercase().contains(q) ||
        cat.badge.lowercase().contains(q) ||
        parentName.contains(q)
      typeMatches && queryMatches
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_categories_tab"),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Clean White Dynamic Hero Card
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
      shadowElevation = 4.dp,
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
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .shadow(3.dp, RoundedCornerShape(14.dp))
              .clip(RoundedCornerShape(14.dp))
              .background(Brush.linearGradient(listOf(AdminV2Cyan, AdminV2CyanDeep))),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.Category,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "ক্যাটাগরি ম্যানেজার",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "মেইন, সাব-ক্যাটাগরি, প্যারেন্ট ও হোম স্লাইডার সেটআপ",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = AdminInkMuted,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            modifier = Modifier
              .heightIn(min = 44.dp)
              .testTag("add_category_button")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "নতুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Live Telemetry Pills
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CategoryStatChip(
            dotColor = AdminV2CyanDeep,
            label = "মোট ${categories.size} টি",
            bgColor = Color(0xFFF0F9FF),
            borderColor = Color(0xFFBAE6FD),
            textColor = AdminV2CyanDeep
          )
          CategoryStatChip(
            dotColor = AdminV2Emerald,
            label = "স্লাইডার চালু $sliderActiveCount",
            bgColor = Color(0xFFECFDF5),
            borderColor = Color(0xFFA7F3D0),
            textColor = Color(0xFF065F46)
          )
          CategoryStatChip(
            dotColor = AdminV2Amber,
            label = "পণ্য লিঙ্কড ${products.size}",
            bgColor = Color(0xFFFFFBEB),
            borderColor = Color(0xFFFDE68A),
            textColor = Color(0xFF92400E)
          )
        }
      }
    }

    // 2. Search + Dynamic Type Filter Strip
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color.White,
      border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("category_search_input"),
          placeholder = { Text("ক্যাটাগরি বা প্যারেন্ট নাম দিয়ে খুঁজুন...", fontSize = 12.sp) },
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
              IconButton(
                onClick = { searchQuery = "" },
                modifier = Modifier.size(48.dp)
              ) {
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
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFFEAF4FB),
          border = BorderStroke(1.dp, Color(0xFFD0E5F4)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(4.dp)
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            listOf(
              Triple("ALL", "সকল (${categories.size})", "✨"),
              Triple("main", "মেইন ($mainCount)", "⭐"),
              Triple("sub", "সাব ($subCount)", "↳"),
              Triple("parent", "প্যারেন্ট ($parentCount)", "🗂️")
            ).forEach { (key, label, emoji) ->
              CategoryFilterPill(
                label = "$emoji $label",
                selected = selectedTypeFilter == key,
                onClick = { selectedTypeFilter = key },
                modifier = Modifier.testTag("category_filter_$key")
              )
            }
          }
        }
      }
    }

    // 3. Filtered Category Cards
    if (filteredCategories.isEmpty()) {
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
            imageVector = Icons.Outlined.Layers,
            contentDescription = null,
            tint = AdminV2CyanDeep,
            modifier = Modifier.size(34.dp)
          )
          Text(
            text = "কোনো ক্যাটাগরি পাওয়া যায়নি",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Text(
            text = "ফিল্টার পরিবর্তন করুন অথবা নতুন ক্যাটাগরি যোগ করুন",
            fontSize = 11.5.sp,
            color = AdminInkMuted
          )
          if (searchQuery.isNotEmpty() || selectedTypeFilter != "ALL") {
            OutlinedButton(
              onClick = {
                searchQuery = ""
                selectedTypeFilter = "ALL"
              },
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, AdminV2CyanDeep),
              modifier = Modifier.heightIn(min = 48.dp)
            ) {
              Text("ফিল্টার রিসেট করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
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
        items(filteredCategories, key = { it.id }) { cat ->
          val linkedCount = productCountByCategory[cat.name] ?: 0
          val parentName = categoryNameById[cat.parentId]
          val isConfirmingDelete = pendingDeleteId == cat.id

          CategoryItemCard(
            category = cat,
            parentName = parentName,
            linkedProductCount = linkedCount,
            isConfirmingDelete = isConfirmingDelete,
            onEditClick = onEditCategory?.let { cb -> { cb(cat) } },
            onToggleSlider = if (cat.type == "main" && onToggleSlider != null) {
              { onToggleSlider(cat.copy(showInSlider = !cat.showInSlider)) }
            } else null,
            onRequestDelete = {
              pendingDeleteId = if (isConfirmingDelete) null else cat.id
            },
            onCancelDelete = { pendingDeleteId = null },
            onConfirmDelete = {
              pendingDeleteId = null
              onDeleteCategory(cat.id)
            }
          )
        }
      }
    }
  }
}

@Composable
private fun CategoryStatChip(
  dotColor: Color,
  label: String,
  bgColor: Color,
  borderColor: Color,
  textColor: Color
) {
  Surface(
    shape = RoundedCornerShape(50),
    color = bgColor,
    border = BorderStroke(1.dp, borderColor)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Box(
        modifier = Modifier
          .size(7.dp)
          .clip(CircleShape)
          .background(dotColor)
      )
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun CategoryFilterPill(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg by animateColorAsState(
    targetValue = if (selected) Color.White else Color.Transparent,
    animationSpec = tween(220, easing = FastOutSlowInEasing),
    label = "cat_filter_bg"
  )
  Surface(
    shape = RoundedCornerShape(11.dp),
    color = bg,
    shadowElevation = if (selected) 2.dp else 0.dp,
    modifier = modifier
      .heightIn(min = 40.dp)
      .clip(RoundedCornerShape(11.dp))
      .clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = label,
        fontSize = 11.5.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        color = if (selected) AdminV2CyanDeep else AdminInkSecondary,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun CategoryItemCard(
  category: Category,
  parentName: String?,
  linkedProductCount: Int,
  isConfirmingDelete: Boolean,
  onEditClick: (() -> Unit)?,
  onToggleSlider: (() -> Unit)?,
  onRequestDelete: () -> Unit,
  onCancelDelete: () -> Unit,
  onConfirmDelete: () -> Unit
) {
  val (typeLabel, accentColor, softBg) = when (category.type.lowercase()) {
    "parent" -> Triple("প্যারেন্ট গ্রুপ", Color(0xFF4F46E5), Color(0xFFEEF2FF))
    "sub" -> Triple("সাব-ক্যাটাগরি", AdminV2Emerald, Color(0xFFECFDF5))
    else -> Triple("মেইন ক্যাটাগরি", AdminV2CyanDeep, Color(0xFFF0F9FF))
  }

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
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Left Type Accent Strip
        Box(
          modifier = Modifier
            .width(4.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(accentColor)
        )

        // Emoji Avatar
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(softBg)
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(13.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(text = category.icon.ifBlank { "✨" }, fontSize = 22.sp)
        }

        // Info Column
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = category.name,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false)
            )
            if (category.badge.isNotBlank()) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = AdminV2AmberSoft,
                border = BorderStroke(0.5.dp, Color(0xFFFDE68A))
              ) {
                Text(
                  text = category.badge,
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF92400E),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = softBg
            ) {
              Text(
                text = typeLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            if (!parentName.isNullOrBlank()) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF1F5F9)
              ) {
                Text(
                  text = "↳ $parentName",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = AdminInkSecondary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFF8FAFC),
              border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
            ) {
              Text(
                text = "$linkedProductCount টি পণ্য",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminInkMuted,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            if (category.type == "main") {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (category.showInSlider) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                modifier = if (onToggleSlider != null) {
                  Modifier.clickable(onClick = onToggleSlider)
                } else Modifier
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                  Icon(
                    imageVector = if (category.showInSlider) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    tint = if (category.showInSlider) AdminV2Emerald else AdminInkMuted,
                    modifier = Modifier.size(11.dp)
                  )
                  Text(
                    text = if (category.showInSlider) "স্লাইডার চালু" else "স্লাইডার বন্ধ",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (category.showInSlider) AdminV2Emerald else AdminInkMuted
                  )
                }
              }
            }
          }
        }

        // Action Buttons (48.dp touch targets)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          if (onEditClick != null) {
            IconButton(
              onClick = onEditClick,
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F9FF))
            ) {
              Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "এডিট ${category.name}",
                tint = AdminV2CyanDeep,
                modifier = Modifier.size(18.dp)
              )
            }
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
              contentDescription = "মুছুন ${category.name}",
              tint = AdminV2Rose,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Safe 2-Step Inline Delete Confirmation
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
              text = if (linkedProductCount > 0) {
                "$linkedProductCount টি পণ্য এই ক্যাটাগরিতে আছে! মুছবেন?"
              } else {
                "\"${category.name}\" মুছে ফেলবেন?"
              },
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF991B1B),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryFormDialog(
  category: Category? = null,
  categories: List<Category> = emptyList(),
  onDismiss: () -> Unit,
  onSave: (Category) -> Unit
) {
  var name by remember(category) { mutableStateOf(category?.name ?: "") }
  var icon by remember(category) { mutableStateOf(category?.icon ?: "✨") }
  var type by remember(category) { mutableStateOf(category?.type ?: "main") }
  var parentId by remember(category) { mutableStateOf(category?.parentId ?: "") }
  var badge by remember(category) { mutableStateOf(category?.badge ?: "") }
  var showInSlider by remember(category) { mutableStateOf(category?.showInSlider ?: true) }
  var formError by remember { mutableStateOf<String?>(null) }

  val emojiPresets = remember {
    listOf("👗", "🥻", "👑", "💍", "📿", "👶", "👕", "👖", "👠", "👜", "💄", "🎀", "🎁", "✨", "🔥", "🍼")
  }
  val badgePresets = remember {
    listOf("হট 🔥", "নতুন", "বেস্ট", "অফার", "স্পেশাল", "কিউট")
  }

  val eligibleParents = remember(categories, type, category) {
    categories.filter { candidate ->
      candidate.id != category?.id && when (type) {
        "main" -> candidate.type == "parent"
        "sub" -> candidate.type == "main" || candidate.type == "parent"
        else -> false
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFFF4F9FE),
      border = BorderStroke(1.5.dp, Color(0xFFC7E3F4)),
      shadowElevation = 12.dp,
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .widthIn(max = 560.dp)
        .heightIn(max = 720.dp)
        .testTag("category_form_dialog")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Sticky Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(AdminV2Cyan, AdminV2CyanDeep))),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.Category,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = if (category != null) "ক্যাটাগরি এডিট করুন" else "নতুন ক্যাটাগরি তৈরি করুন",
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Black,
                color = AdminInkPrimary
              )
              Text(
                text = "আইকন, টাইপ, ব্যাজ ও স্লাইডার ভিজিবিলিটি সেট করুন",
                fontSize = 11.sp,
                color = AdminInkMuted
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "বন্ধ করুন",
              tint = AdminInkPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        HorizontalDivider(color = Color(0xFFE2EFF8))

        // Scrollable Body
        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Live Preview Chip Card
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF0F9FF),
            border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color.White)
                  .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = icon.ifBlank { "✨" }, fontSize = 24.sp)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "লাইভ প্রিভিউ",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminV2CyanDeep
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = name.ifBlank { "ক্যাটাগরির নাম" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = AdminInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  if (badge.isNotBlank()) {
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = AdminV2AmberSoft
                    ) {
                      Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = AdminV2CyanDeep
              ) {
                Text(
                  text = when (type) {
                    "parent" -> "প্যারেন্ট"
                    "sub" -> "সাব"
                    else -> "মেইন"
                  },
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          // White Card: Category Name & Icon Selection
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(13.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Category Name
              OutlinedTextField(
                value = name,
                onValueChange = {
                  name = it
                  formError = null
                },
                label = { Text("ক্যাটাগরির নাম *") },
                placeholder = { Text("যেমন: ব্রাইডাল সেট বা ডিজাইনার শাড়ি") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = adminOutlinedTextFieldColors(),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("category_form_name_input")
              )

              // Icon Selection Grid
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = "আইকন নির্বাচন করুন",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary
                )
                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  emojiPresets.forEach { preset ->
                    val isSelected = icon == preset
                    Box(
                      modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFFE0F2FE) else Color(0xFFF8FCFF))
                        .border(
                          if (isSelected) 2.dp else 1.dp,
                          if (isSelected) AdminV2CyanDeep else Color(0xFFD0E5F4),
                          RoundedCornerShape(12.dp)
                        )
                        .clickable { icon = preset },
                      contentAlignment = Alignment.Center
                    ) {
                      Text(text = preset, fontSize = 20.sp)
                    }
                  }
                }

                OutlinedTextField(
                  value = icon,
                  onValueChange = { icon = it },
                  label = { Text("অথবা কাস্টম ইমোজি আইকন") },
                  singleLine = true,
                  shape = RoundedCornerShape(12.dp),
                  colors = adminOutlinedTextFieldColors(),
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }

          // Type Selection
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(13.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "ক্যাটাগরি ধরন (Type)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkPrimary
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                  "main" to "⭐ মেইন",
                  "sub" to "↳ সাব",
                  "parent" to "🗂️ প্যারেন্ট"
                ).forEach { (key, label) ->
                  val isSelected = type == key
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AdminV2CyanDeep else Color(0xFFF8FCFF),
                    border = BorderStroke(1.dp, if (isSelected) AdminV2CyanDeep else Color(0xFFD0E5F4)),
                    modifier = Modifier
                      .weight(1f)
                      .heightIn(min = 44.dp)
                      .clickable {
                        type = key
                        if (key == "parent") parentId = ""
                      }
                  ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                      Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else AdminInkPrimary
                      )
                    }
                  }
                }
              }
            }
          }

          // Parent Category Picker (when main or sub)
          AnimatedVisibility(visible = type != "parent" && eligibleParents.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "প্যারেন্ট ক্যাটাগরি (ঐচ্ছিক)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkPrimary
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (parentId.isBlank()) AdminV2CyanDeep else Color.White,
                  border = BorderStroke(1.dp, if (parentId.isBlank()) AdminV2CyanDeep else Color(0xFFD0E5F4)),
                  modifier = Modifier
                    .heightIn(min = 40.dp)
                    .clickable { parentId = "" }
                ) {
                  Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text(
                      text = "স্বতন্ত্র (None)",
                      fontSize = 11.5.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = if (parentId.isBlank()) Color.White else AdminInkPrimary
                    )
                  }
                }

                eligibleParents.forEach { candidate ->
                  val isSelected = parentId == candidate.id
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) AdminV2CyanDeep else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) AdminV2CyanDeep else Color(0xFFD0E5F4)),
                    modifier = Modifier
                      .heightIn(min = 40.dp)
                      .clickable { parentId = candidate.id }
                  ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                      Text(
                        text = "${candidate.icon} ${candidate.name}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else AdminInkPrimary
                      )
                    }
                  }
                }
              }
            }
          }

          // Badge Section
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "প্রোমো ব্যাজ (ঐচ্ছিক)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = AdminInkPrimary
            )
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              badgePresets.forEach { presetBadge ->
                val isSelected = badge == presetBadge
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) AdminV2AmberSoft else Color(0xFFF8FCFF),
                  border = BorderStroke(1.dp, if (isSelected) AdminV2Amber else Color(0xFFD0E5F4)),
                  modifier = Modifier
                    .heightIn(min = 38.dp)
                    .clickable { badge = if (isSelected) "" else presetBadge }
                ) {
                  Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp)) {
                    Text(
                      text = presetBadge,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) Color(0xFF92400E) else AdminInkPrimary
                    )
                  }
                }
              }
            }

            OutlinedTextField(
              value = badge,
              onValueChange = { badge = it },
              label = { Text("কাস্টম ব্যাজ (যেমন: নতুন কালেকশন)") },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = adminOutlinedTextFieldColors(),
              modifier = Modifier.fillMaxWidth()
            )
          }

          // Slider Toggle
          if (type == "main") {
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = Color(0xFFF8FCFF),
              border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "হোম পেজ স্লাইডারে দেখান",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminInkPrimary
                  )
                  Text(
                    text = "চালু রাখলে স্টোরফ্রন্টের উপরের ক্যাটাগরি স্লাইডারে দেখাবে",
                    fontSize = 10.5.sp,
                    color = AdminInkMuted
                  )
                }
                Switch(
                  checked = showInSlider,
                  onCheckedChange = { showInSlider = it },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AdminV2Emerald
                  )
                )
              }
            }
          }

          if (formError != null) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFFEF2F2),
              border = BorderStroke(1.dp, Color(0xFFFECACA)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = formError!!,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AdminV2Rose,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }

        HorizontalDivider(color = Color(0xFFE2EFF8))

        // Sticky Footer Actions
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFBFD9EA)),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
          ) {
            Text("বাতিল", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
          }
          Button(
            onClick = {
              val trimmed = name.trim()
              if (trimmed.isBlank()) {
                formError = "অনুগ্রহ করে ক্যাটাগরির নাম লিখুন!"
                return@Button
              }
              onSave(
                Category(
                  id = category?.id ?: "c_${System.currentTimeMillis()}",
                  name = trimmed,
                  icon = icon.trim().ifBlank { "✨" },
                  imageUrl = category?.imageUrl.orEmpty(),
                  type = type,
                  parentId = if (type == "parent") "" else parentId,
                  badge = badge.trim(),
                  showInSlider = if (type == "main") showInSlider else false
                )
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1.4f)
              .height(48.dp)
              .testTag("category_form_save_button")
          ) {
            Text(
              text = if (category != null) "আপডেট করুন" else "যুক্ত করুন",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
