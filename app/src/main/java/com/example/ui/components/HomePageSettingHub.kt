package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BannerItem
import com.example.data.model.Category
import com.example.data.model.HomeSectionConfig
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@Composable
fun HomePageSettingHub(
  homeSections: List<HomeSectionConfig>,
  products: List<Product>,
  categories: List<Category>,
  banners: List<BannerItem>,
  storeConfig: StoreConfig,
  onToggleSection: (String) -> Unit,
  onMoveSection: (String, Int) -> Unit,
  onUpdateSection: (String, (HomeSectionConfig) -> HomeSectionConfig) -> Unit,
  onToggleProductInSection: (String, String) -> Unit,
  onAddCustomSection: (HomeSectionConfig) -> Unit,
  onDeleteSection: (String) -> Unit,
  onResetSectionsToDefault: () -> Unit,
  onAddBannerClick: () -> Unit,
  onToggleBanner: (String) -> Unit,
  onDeleteBanner: (String) -> Unit,
  onSaveStoreConfig: (StoreConfig) -> Unit,
  modifier: Modifier = Modifier
) {
  var subTab by remember { mutableStateOf("BLOCKS") } // BLOCKS, BANNERS, HEADER
  var expandedEditorSectionId by remember { mutableStateOf<String?>("best_deals") }
  var sectionProductQueries by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
  var creatorType by remember { mutableStateOf<String?>(null) } // category_products, promo_banner, custom_products

  // Add New Creator States
  var newTitle by remember { mutableStateOf("") }
  var newSubtitle by remember { mutableStateOf("") }
  var newCategory by remember { mutableStateOf(categories.firstOrNull { it.type == "main" }?.name ?: categories.firstOrNull()?.name ?: "শাড়ি") }
  var newColumnsPerRow by remember { mutableStateOf(3) }
  var newRowCount by remember { mutableStateOf(1) }
  var newDisplayStyle by remember { mutableStateOf("slider") }
  var newBannerButtonText by remember { mutableStateOf("অফার দেখুন") }

  Column(modifier = modifier.fillMaxSize()) {
    // Master Hub Title Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "🏠 Home Page Setting (মাস্টার হাব)",
          fontSize = 15.5.sp,
          fontWeight = FontWeight.Black,
          color = RoyalMaroon
        )
        Text(
          text = "ব্লক রি-অর্ডার, ON/OFF, + Add New, ব্যানার ও প্রোডাক্ট ডিসপ্লে",
          fontSize = 10.5.sp,
          color = AntiqueGoldDark
        )
      }

      OutlinedButton(
        onClick = onResetSectionsToDefault,
        border = BorderStroke(1.dp, BorderGold),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier
          .height(34.dp)
          .testTag("reset_home_layout_btn")
      ) {
        Text(text = "রিসেট", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Sub-tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(
        "BLOCKS" to "🧱 ব্লক ও প্রোডাক্ট (${homeSections.size})",
        "BANNERS" to "🖼️ হিরো ব্যানার (${banners.size})",
        "HEADER" to "📢 হেডার ও নোটিশ"
      ).forEach { (key, label) ->
        val selected = subTab == key
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (selected) RoyalMaroon else Color.White,
          border = BorderStroke(1.dp, if (selected) RoyalMaroon else BorderGold),
          modifier = Modifier
            .clickable { subTab = key }
            .testTag("home_setting_subtab_$key")
        ) {
          Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else DarkEspresso,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    when (subTab) {
      "BLOCKS" -> {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 24.dp)
        ) {
          // + Add New Preset Creator Card
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = BorderStroke(1.5.dp, BorderGold),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = "➕ Add New — হোম পেজে নতুন ব্লক / ব্যানার যুক্ত করুন",
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Black,
                  color = RoyalMaroon
                )

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  listOf(
                    Triple("category_products", "🏷️ + Add ক্যাটাগরি প্রোডাক্ট", "নির্বাচিত ক্যাটাগরির প্রোডাক্ট"),
                    Triple("promo_banner", "🖼️ + Add ব্যানার / বিজ্ঞাপন", "অফার বা প্রমোশন ব্যানার"),
                    Triple("custom_products", "✨ + Add কাস্টম প্রোডাক্ট ব্লক", "নিজের পছন্দমতো কালেকশন")
                  ).forEach { (typeKey, btnLabel, defaultSub) ->
                    val activeCreator = creatorType == typeKey
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = if (activeCreator) RoyalMaroon else CreamSurface,
                      border = BorderStroke(1.dp, if (activeCreator) RoyalMaroon else BorderGold),
                      modifier = Modifier
                        .clickable {
                          if (activeCreator) {
                            creatorType = null
                          } else {
                            creatorType = typeKey
                            newTitle = when (typeKey) {
                              "category_products" -> "$newCategory স্পেশাল"
                              "promo_banner" -> "ঈদ ধামাকা বিজ্ঞাপন অফার"
                              else -> "স্পেশাল সিলেকশন"
                            }
                            newSubtitle = defaultSub
                          }
                        }
                        .testTag("add_new_preset_$typeKey")
                    ) {
                      Text(
                        text = btnLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeCreator) Color.White else DarkEspresso,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                      )
                    }
                  }
                }

                if (creatorType != null) {
                  HorizontalDivider(color = BorderGold)
                  OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("ব্লক / ব্যানারের নাম *", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                  )

                  OutlinedTextField(
                    value = newSubtitle,
                    onValueChange = { newSubtitle = it },
                    label = { Text("বিবরণ / সাবটাইটেল", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                  )

                  if (creatorType == "category_products" || creatorType == "promo_banner") {
                    Text(
                      text = if (creatorType == "category_products") "মেইন ক্যাটাগরি নির্বাচন করুন:" else "ব্যানারে ক্লিক করলে কোন ক্যাটাগরি খুলবে:",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = DarkEspresso
                    )
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      categories.filter { it.type != "parent" }.forEach { cat ->
                        val chosen = newCategory == cat.name
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (chosen) RoyalMaroon else CreamBackground,
                          border = BorderStroke(1.dp, if (chosen) RoyalMaroon else BorderGold),
                          modifier = Modifier.clickable {
                            newCategory = cat.name
                            if (creatorType == "category_products") {
                              newTitle = "${cat.name} কালেকশন"
                            }
                          }
                        ) {
                          Text(
                            text = "${cat.icon} ${cat.name}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (chosen) Color.White else DarkEspresso,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                          )
                        }
                      }
                    }
                  }

                  if (creatorType == "promo_banner") {
                    OutlinedTextField(
                      value = newBannerButtonText,
                      onValueChange = { newBannerButtonText = it },
                      label = { Text("বাটন টেক্সট (যেমন: অফার দেখুন)", fontSize = 11.sp) },
                      singleLine = true,
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.fillMaxWidth()
                    )
                  } else {
                    Text(text = "প্রতি লাইনে কয়টি প্রোডাক্ট ও কয় লাইন:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      listOf(2 to "২টি", 3 to "৩টি", 4 to "৪টি").forEach { (cols, label) ->
                        val chosen = newColumnsPerRow == cols
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (chosen) RoyalMaroon else CreamSurface,
                          border = BorderStroke(1.dp, if (chosen) RoyalMaroon else BorderGold),
                          modifier = Modifier.clickable { newColumnsPerRow = cols }
                        ) {
                          Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (chosen) Color.White else DarkEspresso,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                          )
                        }
                      }
                      listOf("slider" to "স্লাইডার", "grid" to "গ্রিড").forEach { (styleKey, styleLabel) ->
                        val chosen = newDisplayStyle == styleKey
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (chosen) DarkEspresso else CreamSurface,
                          border = BorderStroke(1.dp, BorderGold),
                          modifier = Modifier.clickable { newDisplayStyle = styleKey }
                        ) {
                          Text(
                            text = styleLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (chosen) Color.White else DarkEspresso,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                          )
                        }
                      }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      (1..4).forEach { r ->
                        val chosen = newRowCount == r
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (chosen) RoyalMaroon else CreamSurface,
                          border = BorderStroke(1.dp, if (chosen) RoyalMaroon else BorderGold),
                          modifier = Modifier.clickable {
                            newRowCount = r
                            if (r > 1) newDisplayStyle = "grid"
                          }
                        ) {
                          Text(
                            text = "${r} লাইন",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (chosen) Color.White else DarkEspresso,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                          )
                        }
                      }
                    }
                  }

                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                      onClick = {
                        if (newTitle.isNotBlank()) {
                          val secId = "custom_${System.currentTimeMillis()}"
                          val created = HomeSectionConfig(
                            id = secId,
                            type = creatorType!!,
                            title = newTitle.trim(),
                            subtitle = newSubtitle.trim(),
                            icon = when (creatorType) {
                              "promo_banner" -> "📢"
                              "category_products" -> "🏷️"
                              else -> "✨"
                            },
                            badge = if (creatorType == "promo_banner") "AD / OFFER" else newCategory,
                            layout = if (creatorType == "promo_banner") "banner" else if (newDisplayStyle == "slider") "slider3" else "grid3x2",
                            columnsPerRow = newColumnsPerRow,
                            rowCount = newRowCount,
                            displayStyle = if (creatorType == "promo_banner") "banner" else newDisplayStyle,
                            enabled = true,
                            isCustom = true,
                            categoryName = newCategory,
                            bannerButtonText = newBannerButtonText.ifBlank { "অফার দেখুন" },
                            bannerLinkCategory = newCategory,
                            maxItems = if (newDisplayStyle == "slider") 12 else (newColumnsPerRow * newRowCount)
                          )
                          onAddCustomSection(created)
                          expandedEditorSectionId = secId
                          creatorType = null
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier
                        .weight(1f)
                        .testTag("save_new_home_block_btn")
                    ) {
                      Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("হোম পেজে যুক্ত করুন", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                      onClick = { creatorType = null },
                      shape = RoundedCornerShape(12.dp)
                    ) {
                      Text("বাতিল", fontSize = 11.5.sp)
                    }
                  }
                }
              }
            }
          }

          // Ordered Blocks with Up/Down + ON/OFF + Inline Search & Product Picker + Columns/Rows Controls
          itemsIndexed(homeSections, key = { _, s -> s.id }) { index, section ->
            val isProductBlock = section.type in setOf(
              "new_products", "hot_sale", "b2g1_offers", "best_deals", "all_products", "category_products", "custom_products"
            )
            val isLayoutConfigurable = isProductBlock || section.type == "reels_showcase" || section.type == "combo_packs"
            val gridCfg = remember(section) { resolveBlockGridConfig(section) }
            val layoutSummary = "${gridCfg.cols}টি × ${gridCfg.rows} লাইন (${if (gridCfg.isSlider) "স্লাইডার" else "গ্রিড"})"
            val isExpanded = expandedEditorSectionId == section.id
            val blockSearch = sectionProductQueries[section.id].orEmpty()

            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (section.enabled) Color.White else Color(0xFFF3F4F6)
              ),
              border = BorderStroke(
                width = if (isExpanded) 1.5.dp else 1.dp,
                color = if (isExpanded) RoyalMaroon else BorderGold
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("home_setting_block_${section.id}")
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = RoyalMaroon
                    ) {
                      Text(
                        text = "#${index + 1}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                      )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${section.icon} ${section.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (section.enabled) DarkEspresso else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = when (section.type) {
                          "new_products" -> "$layoutSummary • নতুন আপলোড + ${section.productIds.size} পিন্ড"
                          "hot_sale" -> "$layoutSummary • ${products.count { it.isHotSale || section.productIds.contains(it.id) }} টি পণ্য"
                          "combo_packs" -> "$layoutSummary • স্পেশাল বান্ডেল অফার"
                          "b2g1_offers" -> "$layoutSummary • ${products.count { it.isB2G1 || section.productIds.contains(it.id) }} টি অফার পণ্য"
                          "best_deals" -> "$layoutSummary • ${section.productIds.size} টি সিলেক্টেড"
                          "reels_showcase" -> "$layoutSummary • শপেবল রিলস ভিডিও"
                          "all_products" -> "$layoutSummary • সব ক্যাটাগরির প্রোডাক্ট"
                          "category_products" -> "$layoutSummary • ক্যাটাগরি: ${section.categoryName}"
                          "promo_banner" -> "প্রমোশন ব্যানার (${section.bannerLinkCategory})"
                          else -> section.subtitle.ifBlank { section.type }
                        },
                        fontSize = 10.sp,
                        color = AntiqueGoldDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                  }

                  // Reorder Arrows + ON/OFF Switch
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                  ) {
                    IconButton(
                      onClick = { onMoveSection(section.id, -1) },
                      enabled = index > 0,
                      modifier = Modifier
                        .size(32.dp)
                        .testTag("move_up_${section.id}")
                    ) {
                      Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "উপরে নিন",
                        tint = if (index > 0) RoyalMaroon else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                      )
                    }

                    IconButton(
                      onClick = { onMoveSection(section.id, 1) },
                      enabled = index < homeSections.lastIndex,
                      modifier = Modifier
                        .size(32.dp)
                        .testTag("move_down_${section.id}")
                    ) {
                      Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "নিচে নিন",
                        tint = if (index < homeSections.lastIndex) RoyalMaroon else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                      )
                    }

                    Switch(
                      checked = section.enabled,
                      onCheckedChange = { onToggleSection(section.id) },
                      colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentGreen
                      ),
                      modifier = Modifier.testTag("toggle_section_${section.id}")
                    )
                  }
                }

                // Action Strip: Manage Products / Customize / Delete Custom Block
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  if (isLayoutConfigurable || section.type == "promo_banner") {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = if (isExpanded) RoyalMaroon else CreamSurface,
                      border = BorderStroke(1.dp, if (isExpanded) RoyalMaroon else BorderGold),
                      modifier = Modifier
                        .clickable {
                          expandedEditorSectionId = if (isExpanded) null else section.id
                        }
                        .testTag("manage_block_${section.id}")
                    ) {
                      Text(
                        text = if (isExpanded) {
                          "▾ এডিটর বন্ধ করুন"
                        } else if (isProductBlock) {
                          "⚙️ কলাম/লাইন ও প্রোডাক্ট সেটিং"
                        } else if (section.type == "reels_showcase" || section.type == "combo_packs") {
                          "📐 কলাম/লাইন ও ডিসপ্লে সেটিং"
                        } else {
                          "✏️ ব্যানার এডিট করুন"
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpanded) Color.White else RoyalMaroon,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                      )
                    }
                  } else {
                    Text(
                      text = if (section.enabled) "✅ হোম পেজে চালু আছে" else "⛔ বন্ধ আছে",
                      fontSize = 10.sp,
                      color = if (section.enabled) AccentGreen else Color.Gray,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  if (section.isCustom) {
                    IconButton(
                      onClick = { onDeleteSection(section.id) },
                      modifier = Modifier.size(30.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "ব্লক মুছুন",
                        tint = AccentRed,
                        modifier = Modifier.size(17.dp)
                      )
                    }
                  }
                }

                // Expanded Editor Panel: Block Title + Columns (2/3/4) & Rows (1-4) & Slider/Grid + Product Picker
                if (isExpanded) {
                  HorizontalDivider(color = BorderGold)

                  var editTitle by remember(section.id, section.title) { mutableStateOf(section.title) }
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    OutlinedTextField(
                      value = editTitle,
                      onValueChange = { editTitle = it },
                      label = { Text("ব্লকের শিরোনাম", fontSize = 10.sp) },
                      singleLine = true,
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.weight(1f)
                    )
                    Button(
                      onClick = {
                        if (editTitle.isNotBlank()) {
                          onUpdateSection(section.id) { it.copy(title = editTitle.trim()) }
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = DarkEspresso),
                      shape = RoundedCornerShape(10.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                      Text("সেভ", fontSize = 10.5.sp)
                    }
                  }

                  if (section.type == "category_products") {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      categories.filter { it.type != "parent" }.forEach { cat ->
                        val chosen = section.categoryName == cat.name
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (chosen) RoyalMaroon else CreamBackground,
                          border = BorderStroke(1.dp, BorderGold),
                          modifier = Modifier.clickable {
                            onUpdateSection(section.id) {
                              it.copy(categoryName = cat.name, badge = cat.name)
                            }
                          }
                        ) {
                          Text(
                            text = "${cat.icon} ${cat.name}",
                            fontSize = 10.sp,
                            color = if (chosen) Color.White else DarkEspresso,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                          )
                        }
                      }
                    }
                  }

                  if (isLayoutConfigurable) {
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = CreamBackground,
                      border = BorderStroke(1.dp, BorderGold),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = "📐 ডিসপ্লে কলাম ও লাইন সেটিং",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = RoyalMaroon
                          )
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, BorderGold)
                          ) {
                            Text(
                              text = layoutSummary,
                              fontSize = 9.5.sp,
                              fontWeight = FontWeight.Bold,
                              color = DarkEspresso,
                              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                          }
                        }

                        // Columns per row (2 / 3 / 4) & Display Style (Slider / Grid)
                        Row(
                          modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                          horizontalArrangement = Arrangement.spacedBy(6.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = "প্রতি লাইন:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkEspresso
                          )
                          listOf(2 to "২টি", 3 to "৩টি", 4 to "৪টি").forEach { (cols, label) ->
                            val chosen = gridCfg.cols == cols
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (chosen) RoyalMaroon else Color.White,
                              border = BorderStroke(1.dp, if (chosen) RoyalMaroon else BorderGold),
                              modifier = Modifier
                                .clickable {
                                  onUpdateSection(section.id) { sec ->
                                    val targetMax = if (sec.displayStyle == "grid") cols * gridCfg.rows else if (sec.type == "reels_showcase") cols else maxOf(sec.maxItems, cols * 2)
                                    sec.copy(columnsPerRow = cols, maxItems = targetMax)
                                  }
                                }
                                .testTag("cols_${section.id}_$cols")
                            ) {
                              Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chosen) Color.White else DarkEspresso,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                              )
                            }
                          }

                          Spacer(modifier = Modifier.width(4.dp))

                          listOf("slider" to "স্লাইডার", "grid" to "গ্রিড").forEach { (styleKey, styleLabel) ->
                            val chosen = (if (gridCfg.isSlider) "slider" else "grid") == styleKey
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (chosen) DarkEspresso else Color.White,
                              border = BorderStroke(1.dp, BorderGold),
                              modifier = Modifier
                                .clickable {
                                  onUpdateSection(section.id) { sec ->
                                    val nextRows = if (styleKey == "slider") 1 else maxOf(gridCfg.rows, 2)
                                    sec.copy(
                                      displayStyle = styleKey,
                                      rowCount = nextRows,
                                      layout = if (styleKey == "slider") "slider3" else "grid3x2",
                                      maxItems = if (styleKey == "slider") {
                                        if (sec.type == "reels_showcase") gridCfg.cols else 12
                                      } else {
                                        gridCfg.cols * nextRows
                                      }
                                    )
                                  }
                                }
                                .testTag("style_${section.id}_$styleKey")
                            ) {
                              Text(
                                text = styleLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chosen) Color.White else DarkEspresso,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                              )
                            }
                          }
                        }

                        // Row/Line Count (1..4)
                        Row(
                          modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                          horizontalArrangement = Arrangement.spacedBy(6.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = "লাইন সংখ্যা:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkEspresso
                          )
                          (1..4).forEach { r ->
                            val chosen = gridCfg.rows == r
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (chosen) RoyalMaroon else Color.White,
                              border = BorderStroke(1.dp, if (chosen) RoyalMaroon else BorderGold),
                              modifier = Modifier
                                .clickable {
                                  onUpdateSection(section.id) { sec ->
                                    val nextStyle = if (r > 1) "grid" else sec.displayStyle
                                    sec.copy(
                                      rowCount = r,
                                      displayStyle = nextStyle,
                                      layout = if (nextStyle == "grid") "grid3x2" else "slider3",
                                      maxItems = if (nextStyle == "grid") (gridCfg.cols * r) else if (sec.type == "reels_showcase") gridCfg.cols else 12
                                    )
                                  }
                                }
                                .testTag("rows_${section.id}_$r")
                            ) {
                              Text(
                                text = "${r} লাইন",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chosen) Color.White else DarkEspresso,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                              )
                            }
                          }
                        }
                      }
                    }
                  }

                  if (isProductBlock) {
                    // Inline Product Search Box directly above Product Selection for this Block
                    OutlinedTextField(
                      value = blockSearch,
                      onValueChange = { q ->
                        sectionProductQueries = sectionProductQueries + (section.id to q)
                      },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Default.Search,
                          contentDescription = "Search",
                          tint = RoyalMaroon,
                          modifier = Modifier.size(18.dp)
                        )
                      },
                      placeholder = {
                        Text("🔍 ${section.title}-এর প্রোডাক্ট খুঁজুন (নাম, SKU, ক্যাটাগরি)...", fontSize = 11.sp)
                      },
                      singleLine = true,
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier
                        .fillMaxWidth()
                        .testTag("block_search_${section.id}")
                    )

                    val filteredBlockProducts = remember(products, blockSearch) {
                      val q = blockSearch.trim().lowercase()
                      if (q.isEmpty()) products
                      else products.filter {
                        "${it.name} ${it.sku} ${it.category}".lowercase().contains(q)
                      }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                      filteredBlockProducts.take(15).forEach { prod ->
                        val isSelected = when (section.type) {
                          "hot_sale" -> prod.isHotSale || section.productIds.contains(prod.id)
                          "b2g1_offers" -> prod.isB2G1 || section.productIds.contains(prod.id)
                          "best_deals" -> section.productIds.contains(prod.id) || prod.isBestDeal
                          else -> section.productIds.contains(prod.id)
                        }
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (isSelected) Color(0xFFFFF8E1) else CreamBackground,
                          border = BorderStroke(
                            1.dp,
                            if (isSelected) RoyalMaroon else BorderGold.copy(alpha = 0.6f)
                          ),
                          modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleProductInSection(section.id, prod.id) }
                            .testTag("picker_${section.id}_${prod.id}")
                        ) {
                          Row(
                            modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                          ) {
                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.spacedBy(8.dp),
                              modifier = Modifier.weight(1f)
                            ) {
                              AppImage(
                                model = prod.imageUrl,
                                contentDescription = prod.name,
                                modifier = Modifier
                                  .size(34.dp)
                                  .clip(RoundedCornerShape(8.dp))
                              )
                              Column(modifier = Modifier.weight(1f)) {
                                Text(
                                  text = prod.name,
                                  fontSize = 11.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = DarkEspresso,
                                  maxLines = 1,
                                  overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                  text = "${prod.category} • SKU: ${prod.sku} • ৳${prod.price}",
                                  fontSize = 9.5.sp,
                                  color = Color.Gray,
                                  maxLines = 1
                                )
                              }
                            }

                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (isSelected) AccentGreen else RoyalMaroon
                            ) {
                              Text(
                                text = if (isSelected) "✓ যুক্ত" else "+ যোগ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
      }

      "BANNERS" -> {
        Column(modifier = Modifier.fillMaxSize()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "হোম পেজ ব্যানার স্লাইডার (${banners.size})",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
            Button(
              onClick = onAddBannerClick,
              colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
              shape = RoundedCornerShape(14.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("নতুন ব্যানার", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(banners) { _, b ->
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderGold),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  AppImage(
                    model = b.imageUrl,
                    contentDescription = b.title,
                    modifier = Modifier
                      .size(56.dp)
                      .clip(RoundedCornerShape(8.dp))
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(text = b.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkEspresso, maxLines = 1)
                    Text(text = b.subtitle, fontSize = 10.5.sp, color = Color.Gray, maxLines = 1)
                    Text(text = "বাটন: ${b.buttonText}", fontSize = 10.sp, color = RoyalMaroon, fontWeight = FontWeight.SemiBold)
                  }
                  Switch(
                    checked = b.isActive,
                    onCheckedChange = { onToggleBanner(b.id) }
                  )
                  IconButton(onClick = { onDeleteBanner(b.id) }, modifier = Modifier.size(30.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "মুছুন", tint = AccentRed, modifier = Modifier.size(17.dp))
                  }
                }
              }
            }
          }
        }
      }

      "HEADER" -> {
        var storeName by remember(storeConfig.storeName) { mutableStateOf(storeConfig.storeName) }
        var tagline by remember(storeConfig.tagline) { mutableStateOf(storeConfig.tagline) }
        var announcementText by remember(storeConfig.announcementText) { mutableStateOf(storeConfig.announcementText) }
        var isAnnouncementEnabled by remember(storeConfig.isAnnouncementEnabled) { mutableStateOf(storeConfig.isAnnouncementEnabled) }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderGold),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(
                text = "হোম পেজ হেডার ও অ্যানাউন্সমেন্ট বার",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "অ্যানাউন্সমেন্ট বার প্রদর্শন করুন", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkEspresso)
                Switch(
                  checked = isAnnouncementEnabled,
                  onCheckedChange = { isAnnouncementEnabled = it }
                )
              }

              OutlinedTextField(
                value = announcementText,
                onValueChange = { announcementText = it },
                label = { Text("অ্যানাউন্সমেন্ট নোটিশ টেক্সট") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("স্টোর ব্র্যান্ড নাম") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = { Text("ট্যাগলাইন / স্লোগান") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              )

              Button(
                onClick = {
                  onSaveStoreConfig(
                    storeConfig.copy(
                      storeName = storeName.trim().ifBlank { storeConfig.storeName },
                      tagline = tagline.trim().ifBlank { storeConfig.tagline },
                      announcementText = announcementText.trim(),
                      isAnnouncementEnabled = isAnnouncementEnabled
                    )
                  )
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(46.dp)
              ) {
                Text("হেডার সেটিংস সেভ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
