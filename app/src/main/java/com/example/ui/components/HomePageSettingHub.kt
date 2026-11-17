package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import com.example.data.model.BannerItem
import com.example.data.model.Category
import com.example.data.model.HomeSectionConfig
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed

@OptIn(ExperimentalLayoutApi::class)
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
  var subTab by remember { mutableStateOf("BLOCKS") } // BLOCKS, BANNERS, HEADER, DESIGN
  var expandedEditorSectionId by remember { mutableStateOf<String?>("best_deals") }
  var sectionProductQueries by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
  var creatorType by remember { mutableStateOf<String?>(null) }

  // Add New Creator States
  var newTitle by remember { mutableStateOf("") }
  var newSubtitle by remember { mutableStateOf("") }
  var newCategory by remember {
    mutableStateOf(
      categories.firstOrNull { it.type == "main" }?.name
        ?: categories.firstOrNull()?.name
        ?: "শাড়ি"
    )
  }
  var newColumnsPerRow by remember { mutableStateOf(3) }
  var newRowCount by remember { mutableStateOf(1) }
  var newDisplayStyle by remember { mutableStateOf("slider") }
  var newBannerButtonText by remember { mutableStateOf("অফার দেখুন") }

  val enabledCount = remember(homeSections) { homeSections.count { it.enabled } }

  Column(modifier = modifier.fillMaxSize()) {
    // 1. Glossy Master Hub Header Card (Frosted V2 White-Ice Glass + Crisp Deep Ocean Ink)
    GlossyAdminCard(
      cornerRadius = 20.dp,
      accentBorderColor = AdminCardBorderWarm,
      containerTintTop = Color(0xFAFFFFFF),
      containerTintBottom = Color(0xEDF1F9FE),
      elevation = 7.dp,
      contentPadding = 14.dp
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .shadow(4.dp, RoundedCornerShape(14.dp))
              .clip(RoundedCornerShape(14.dp))
              .background(adminAccentGlossBrush(AdminV2DeepOcean, AdminV2CyanDeep))
              .border(1.dp, Color.White.copy(alpha = 0.65f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🏠", fontSize = 20.sp)
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Home Page Setting",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "মাস্টার হাব • ব্লক, ব্যানার, ON/OFF ও কালার স্টুডিও",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = AdminInkSecondary,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Crisp Reset Pill Button (min 44dp touch target, clear ice-surface pill)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = AdminV2SkySoft,
          border = BorderStroke(1.dp, AdminV2Cyan.copy(alpha = 0.45f)),
          shadowElevation = 2.dp,
          modifier = Modifier
            .heightIn(min = 42.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onResetSectionsToDefault)
            .testTag("reset_home_layout_btn")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "ডিফল্ট রিসেট",
              tint = AdminV2CyanDeep,
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = "রিসেট",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AdminV2DeepOcean
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Live Status Strip inside Header Card
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = AdminV2EmeraldSoft,
          border = BorderStroke(1.dp, Color(0xFFA7F3D0))
        ) {
          Text(
            text = "● $enabledCount/${homeSections.size} ব্লক চালু",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF065F46),
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = AdminV2SkySoft,
          border = BorderStroke(1.dp, AdminCardBorderWarm)
        ) {
          Text(
            text = "🖼️ ${banners.count { it.isActive }} ব্যানার সক্রিয়",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = AdminV2CyanDeep,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Clean 2x2 Glossy Sub-Tab Grid (Never clips or truncates labels!)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GlossySegmentPill(
          label = "🧱 ব্লক ও প্রোডাক্ট (${homeSections.size})",
          selected = subTab == "BLOCKS",
          onClick = { subTab = "BLOCKS" },
          modifier = Modifier
            .weight(1f)
            .testTag("home_setting_subtab_BLOCKS")
        )
        GlossySegmentPill(
          label = "🖼️ হিরো ব্যানার (${banners.size})",
          selected = subTab == "BANNERS",
          onClick = { subTab = "BANNERS" },
          modifier = Modifier
            .weight(1f)
            .testTag("home_setting_subtab_BANNERS")
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GlossySegmentPill(
          label = "📢 হেডার ও নোটিশ",
          selected = subTab == "HEADER",
          onClick = { subTab = "HEADER" },
          modifier = Modifier
            .weight(1f)
            .testTag("home_setting_subtab_HEADER")
        )
        GlossySegmentPill(
          label = "🎨 ডিজাইন স্টুডিও",
          selected = subTab == "DESIGN",
          onClick = { subTab = "DESIGN" },
          selectedBrush = adminAccentGlossBrush(AdminNavyGradStart, AdminNavyGradEnd),
          modifier = Modifier
            .weight(1f)
            .testTag("home_setting_subtab_DESIGN")
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (subTab) {
      "BLOCKS" -> {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 28.dp)
        ) {
          // + Add New Preset Creator Card
          item {
            GlossyAdminCard(
              cornerRadius = 18.dp,
              accentBorderColor = AdminCardBorderWarm,
              containerTintTop = Color(0xFAFFFFFF),
              containerTintBottom = Color(0xEDF1F9FE),
              elevation = 5.dp,
              contentPadding = 14.dp
            ) {
              Text(
                text = "➕ Add New — হোম পেজে নতুন ব্লক / ব্যানার",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                color = AdminInkPrimary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "নিচের যেকোনো অপশনে ট্যাপ করে ক্যাটাগরি, ব্যানার বা কাস্টম প্রোডাক্ট ব্লক তৈরি করুন:",
                fontSize = 11.sp,
                color = AdminInkMuted,
                lineHeight = 15.sp
              )
              Spacer(modifier = Modifier.height(10.dp))

              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                  Triple("category_products", "🏷️ + Add ক্যাটাগরি প্রোডাক্ট", "নির্বাচিত ক্যাটাগরির প্রোডাক্ট"),
                  Triple("promo_banner", "🖼️ + Add ব্যানার / বিজ্ঞাপন", "অফার বা প্রমোশন ব্যানার"),
                  Triple("custom_products", "✨ + Add কাস্টম প্রোডাক্ট ব্লক", "নিজের পছন্দমতো কালেকশন")
                ).forEach { (typeKey, btnLabel, defaultSub) ->
                  val activeCreator = creatorType == typeKey
                  GlossySegmentPill(
                    label = btnLabel,
                    selected = activeCreator,
                    onClick = {
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
                    },
                    modifier = Modifier.testTag("add_new_preset_$typeKey")
                  )
                }
              }

              if (creatorType != null) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = AdminCardBorderWarm)
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("ব্লক / ব্যানারের নাম *", fontSize = 11.5.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = adminOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                  )

                  OutlinedTextField(
                    value = newSubtitle,
                    onValueChange = { newSubtitle = it },
                    label = { Text("বিবরণ / সাবটাইটেল", fontSize = 11.5.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = adminOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                  )

                  if (creatorType == "category_products" || creatorType == "promo_banner") {
                    Text(
                      text = if (creatorType == "category_products") "মেইন ক্যাটাগরি নির্বাচন করুন:" else "ব্যানারে ক্লিক করলে কোন ক্যাটাগরি খুলবে:",
                      fontSize = 11.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = AdminInkPrimary
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
                          color = if (chosen) AdminV2DeepOcean else Color.White,
                          border = BorderStroke(1.dp, if (chosen) AdminV2CyanDeep else AdminCardBorderWarm),
                          modifier = Modifier
                            .heightIn(min = 38.dp)
                            .clickable {
                              newCategory = cat.name
                              if (creatorType == "category_products") {
                                newTitle = "${cat.name} কালেকশন"
                              }
                            }
                        ) {
                          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(
                              text = "${cat.icon} ${cat.name}",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              color = if (chosen) Color.White else AdminInkPrimary
                            )
                          }
                        }
                      }
                    }
                  }

                  if (creatorType == "promo_banner") {
                    OutlinedTextField(
                      value = newBannerButtonText,
                      onValueChange = { newBannerButtonText = it },
                      label = { Text("বাটন টেক্সট (যেমন: অফার দেখুন)", fontSize = 11.5.sp) },
                      singleLine = true,
                      shape = RoundedCornerShape(12.dp),
                      colors = adminOutlinedTextFieldColors(),
                      modifier = Modifier.fillMaxWidth()
                    )
                  } else {
                    Text(
                      text = "প্রতি লাইনে কয়টি প্রোডাক্ট ও কয় লাইন:",
                      fontSize = 11.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = AdminInkPrimary
                    )
                    FlowRow(
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                      verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      listOf(2 to "২টি", 3 to "৩টি", 4 to "৪টি").forEach { (cols, label) ->
                        val chosen = newColumnsPerRow == cols
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (chosen) AdminV2DeepOcean else Color.White,
                          border = BorderStroke(1.dp, if (chosen) AdminV2CyanDeep else AdminCardBorderWarm),
                          modifier = Modifier
                            .heightIn(min = 36.dp)
                            .clickable { newColumnsPerRow = cols }
                        ) {
                          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)) {
                            Text(
                              text = label,
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              color = if (chosen) Color.White else AdminInkPrimary
                            )
                          }
                        }
                      }
                      listOf("slider" to "স্লাইডার", "grid" to "গ্রিড").forEach { (styleKey, styleLabel) ->
                        val chosen = newDisplayStyle == styleKey
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (chosen) AdminV2CyanDeep else Color.White,
                          border = BorderStroke(1.dp, if (chosen) AdminV2CyanDeep else AdminCardBorderWarm),
                          modifier = Modifier
                            .heightIn(min = 36.dp)
                            .clickable { newDisplayStyle = styleKey }
                        ) {
                          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)) {
                            Text(
                              text = styleLabel,
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              color = if (chosen) Color.White else AdminInkPrimary
                            )
                          }
                        }
                      }
                      (1..4).forEach { r ->
                        val chosen = newRowCount == r
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = if (chosen) AdminV2DeepOcean else Color.White,
                          border = BorderStroke(1.dp, if (chosen) AdminV2CyanDeep else AdminCardBorderWarm),
                          modifier = Modifier
                            .heightIn(min = 36.dp)
                            .clickable {
                              newRowCount = r
                              if (r > 1) newDisplayStyle = "grid"
                            }
                        ) {
                          Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)) {
                            Text(
                              text = "${r} লাইন",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              color = if (chosen) Color.White else AdminInkPrimary
                            )
                          }
                        }
                      }
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
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
                      colors = ButtonDefaults.buttonColors(containerColor = AdminV2DeepOcean, contentColor = Color.White),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 46.dp)
                        .testTag("save_new_home_block_btn")
                    ) {
                      Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("হোম পেজে যুক্ত করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                      onClick = { creatorType = null },
                      border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier.heightIn(min = 46.dp)
                    ) {
                      Text("বাতিল", fontSize = 12.sp, color = AdminInkPrimary, fontWeight = FontWeight.Bold)
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

            GlossyAdminCard(
              cornerRadius = 18.dp,
              accentBorderColor = when {
                isExpanded -> AdminV2CyanDeep
                section.enabled -> AdminCardBorderWarm
                else -> Color(0xFFCBD5E1)
              },
              containerTintTop = if (section.enabled) Color(0xFAFFFFFF) else Color(0xFFF8FAFC),
              containerTintBottom = if (section.enabled) Color(0xEDF1F9FE) else Color(0xFFF1F5F9),
              elevation = if (isExpanded) 8.dp else 4.dp,
              contentPadding = 13.dp,
              modifier = Modifier.testTag("home_setting_block_${section.id}")
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
                  // Glossy Rank Pill
                  Box(
                    modifier = Modifier
                      .shadow(2.dp, RoundedCornerShape(9.dp))
                      .clip(RoundedCornerShape(9.dp))
                      .background(
                        if (section.enabled) adminAccentGlossBrush()
                        else Brush.horizontalGradient(listOf(Color(0xFF475569), Color(0xFF64748B)))
                      )
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = "#${index + 1}",
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Black,
                      color = Color.White
                    )
                  }

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "${section.icon} ${section.title}",
                      fontSize = 13.5.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = if (section.enabled) AdminInkPrimary else AdminInkMuted,
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
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = if (section.enabled) AdminInkSecondary else AdminInkMuted,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }

                // Reorder Arrows + ON/OFF Switch
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (index > 0) AdminV2SkySoft else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (index > 0) AdminCardBorderWarm else Color(0xFFE2E8F0))
                  ) {
                    IconButton(
                      onClick = { onMoveSection(section.id, -1) },
                      enabled = index > 0,
                      modifier = Modifier
                        .size(36.dp)
                        .testTag("move_up_${section.id}")
                    ) {
                      Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "উপরে নিন",
                        tint = if (index > 0) AdminV2DeepOcean else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (index < homeSections.lastIndex) AdminV2SkySoft else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (index < homeSections.lastIndex) AdminCardBorderWarm else Color(0xFFE2E8F0))
                  ) {
                    IconButton(
                      onClick = { onMoveSection(section.id, 1) },
                      enabled = index < homeSections.lastIndex,
                      modifier = Modifier
                        .size(36.dp)
                        .testTag("move_down_${section.id}")
                    ) {
                      Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "নিচে নিন",
                        tint = if (index < homeSections.lastIndex) AdminV2DeepOcean else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }

                  Switch(
                    checked = section.enabled,
                    onCheckedChange = { onToggleSection(section.id) },
                    colors = SwitchDefaults.colors(
                      checkedThumbColor = Color.White,
                      checkedTrackColor = AccentGreen,
                      uncheckedThumbColor = Color.White,
                      uncheckedTrackColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("toggle_section_${section.id}")
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Action Strip: Manage Products / Customize / Delete Block
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (isLayoutConfigurable || section.type == "promo_banner") {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isExpanded) AdminV2DeepOcean else AdminV2SkySoft,
                    border = BorderStroke(1.dp, if (isExpanded) AdminV2CyanDeep else AdminCardBorderWarm),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                      .heightIn(min = 38.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .clickable {
                        expandedEditorSectionId = if (isExpanded) null else section.id
                      }
                      .testTag("manage_block_${section.id}")
                  ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)) {
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
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpanded) Color.White else AdminV2DeepOcean
                      )
                    }
                  }
                } else {
                  Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (section.enabled) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (section.enabled) Color(0xFFA7F3D0) else Color(0xFFCBD5E1))
                  ) {
                    Text(
                      text = if (section.enabled) "✅ হোম পেজে চালু আছে" else "⛔ বন্ধ আছে",
                      fontSize = 10.5.sp,
                      color = if (section.enabled) Color(0xFF065F46) else AdminInkMuted,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = Color(0xFFFEF2F2),
                  border = BorderStroke(1.dp, Color(0xFFFECACA))
                ) {
                  IconButton(
                    onClick = { onDeleteSection(section.id) },
                    modifier = Modifier
                      .size(36.dp)
                      .testTag("delete_section_${section.id}")
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

              // Expanded Editor Panel
              if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = AdminCardBorderWarm)
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  var editTitle by remember(section.id, section.title) { mutableStateOf(section.title) }
                  var editSubtitle by remember(section.id, section.subtitle) { mutableStateOf(section.subtitle) }
                  var editIcon by remember(section.id, section.icon) { mutableStateOf(section.icon) }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    OutlinedTextField(
                      value = editIcon,
                      onValueChange = { editIcon = it },
                      label = { Text("আইকন", fontSize = 10.sp) },
                      singleLine = true,
                      shape = RoundedCornerShape(10.dp),
                      colors = adminOutlinedTextFieldColors(),
                      modifier = Modifier.width(68.dp)
                    )
                    OutlinedTextField(
                      value = editTitle,
                      onValueChange = { editTitle = it },
                      label = { Text("ব্লকের শিরোনাম", fontSize = 10.sp) },
                      singleLine = true,
                      shape = RoundedCornerShape(10.dp),
                      colors = adminOutlinedTextFieldColors(),
                      modifier = Modifier.weight(1f)
                    )
                      Button(
                      onClick = {
                        if (editTitle.isNotBlank()) {
                          onUpdateSection(section.id) {
                            it.copy(
                              title = editTitle.trim(),
                              subtitle = editSubtitle.trim(),
                              icon = editIcon.trim().ifBlank { it.icon }
                            )
                          }
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = AdminV2DeepOcean, contentColor = Color.White),
                      shape = RoundedCornerShape(10.dp),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                      modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                      Text("সেভ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  OutlinedTextField(
                    value = editSubtitle,
                    onValueChange = { editSubtitle = it },
                    label = { Text("সাবটাইটেল / বিবরণ (ঐচ্ছিক)", fontSize = 10.5.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = adminOutlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                  )

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
                          color = if (chosen) AdminV2DeepOcean else Color.White,
                          border = BorderStroke(1.dp, if (chosen) AdminV2CyanDeep else AdminCardBorderWarm),
                          modifier = Modifier.clickable {
                            onUpdateSection(section.id) {
                              it.copy(categoryName = cat.name, badge = cat.name)
                            }
                          }
                        ) {
                          Text(
                            text = "${cat.icon} ${cat.name}",
                            fontSize = 10.5.sp,
                            color = if (chosen) Color.White else AdminInkPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                          )
                        }
                      }
                    }
                  }

                  if (isLayoutConfigurable) {
                    Surface(
                      shape = RoundedCornerShape(14.dp),
                      color = AdminV2IceSurface,
                      border = BorderStroke(1.dp, AdminCardBorderWarm),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(
                        modifier = Modifier.padding(11.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = "📐 ডিসপ্লে কলাম ও লাইন সেটিং",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            color = AdminInkPrimary
                          )
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AdminV2SkySoft,
                            border = BorderStroke(1.dp, AdminCardBorderWarm)
                          ) {
                            Text(
                              text = layoutSummary,
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold,
                              color = AdminV2CyanDeep,
                              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
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
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminInkSecondary
                          )
                          listOf(2 to "২টি", 3 to "৩টি", 4 to "৪টি").forEach { (cols, label) ->
                            val chosen = gridCfg.cols == cols
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (chosen) AdminV2DeepOcean else Color.White,
                              border = BorderStroke(1.dp, if (chosen) AdminV2CyanDeep else AdminCardBorderWarm),
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
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chosen) Color.White else AdminInkPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                              )
                            }
                          }

                          Spacer(modifier = Modifier.width(4.dp))

                          listOf("slider" to "স্লাইডার", "grid" to "গ্রিড").forEach { (styleKey, styleLabel) ->
                            val chosen = (if (gridCfg.isSlider) "slider" else "grid") == styleKey
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (chosen) AdminV2CyanDeep else Color.White,
                              border = BorderStroke(1.dp, if (chosen) AdminV2CyanDeep else AdminCardBorderWarm),
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
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chosen) Color.White else AdminInkPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminInkSecondary
                          )
                          (1..4).forEach { r ->
                            val chosen = gridCfg.rows == r
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (chosen) AdminV2DeepOcean else Color.White,
                              border = BorderStroke(1.dp, if (chosen) AdminV2CyanDeep else AdminCardBorderWarm),
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
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chosen) Color.White else AdminInkPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                              )
                            }
                          }
                        }
                      }
                    }
                  }

                  if (isProductBlock) {
                    OutlinedTextField(
                      value = blockSearch,
                      onValueChange = { q ->
                        sectionProductQueries = sectionProductQueries + (section.id to q)
                      },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Default.Search,
                          contentDescription = "Search",
                          tint = AdminV2CyanDeep,
                          modifier = Modifier.size(18.dp)
                        )
                      },
                      placeholder = {
                        Text(
                          "🔍 ${section.title}-এর প্রোডাক্ট খুঁজুন (নাম, SKU, ক্যাটাগরি)...",
                          fontSize = 11.sp
                        )
                      },
                      singleLine = true,
                      shape = RoundedCornerShape(12.dp),
                      colors = adminOutlinedTextFieldColors(),
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
                          shape = RoundedCornerShape(12.dp),
                          color = if (isSelected) AdminV2IceSurface else Color.White,
                          border = BorderStroke(
                            1.dp,
                            if (isSelected) AdminV2CyanDeep else AdminCardBorderSoft
                          ),
                          shadowElevation = 1.dp,
                          modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleProductInSection(section.id, prod.id) }
                            .testTag("picker_${section.id}_${prod.id}")
                        ) {
                          Row(
                            modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 10.dp, vertical = 8.dp),
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
                                  .size(36.dp)
                                  .clip(RoundedCornerShape(8.dp))
                              )
                              Column(modifier = Modifier.weight(1f)) {
                                Text(
                                  text = prod.name,
                                  fontSize = 11.5.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = AdminInkPrimary,
                                  maxLines = 1,
                                  overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                  text = "${prod.category} • SKU: ${prod.sku} • ৳${prod.price}",
                                  fontSize = 10.sp,
                                  fontWeight = FontWeight.Medium,
                                  color = AdminInkMuted,
                                  maxLines = 1
                                )
                              }
                            }

                            Surface(
                              shape = RoundedCornerShape(9.dp),
                              color = if (isSelected) AdminV2Emerald else AdminV2DeepOcean
                            ) {
                              Text(
                                text = if (isSelected) "✓ যুক্ত" else "+ যোগ",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
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
          GlossyAdminCard(
            cornerRadius = 16.dp,
            contentPadding = 12.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "হোম পেজ ব্যানার স্লাইডার (${banners.size})",
                  fontSize = 14.5.sp,
                  fontWeight = FontWeight.Black,
                  color = AdminInkPrimary
                )
                Text(
                  text = "প্রোমোশনাল স্লাইডার ব্যানার চালু/বন্ধ ও যোগ করুন",
                  fontSize = 11.sp,
                  color = AdminInkMuted
                )
              }
              Button(
                onClick = onAddBannerClick,
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2DeepOcean, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.heightIn(min = 44.dp)
              ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন ব্যানার", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
          ) {
            itemsIndexed(banners) { _, b ->
              GlossyAdminCard(
                cornerRadius = 16.dp,
                accentBorderColor = if (b.isActive) AdminCardBorderWarm else Color(0xFFCBD5E1),
                contentPadding = 11.dp
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  AppImage(
                    model = b.imageUrl,
                    contentDescription = b.title,
                    modifier = Modifier
                      .size(58.dp)
                      .clip(RoundedCornerShape(10.dp))
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(text = b.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary, maxLines = 1)
                    Text(text = b.subtitle, fontSize = 11.sp, color = AdminInkMuted, maxLines = 1)
                    Text(text = "বাটন: ${b.buttonText}", fontSize = 10.5.sp, color = AdminV2CyanDeep, fontWeight = FontWeight.Bold)
                  }
                  Switch(
                    checked = b.isActive,
                    onCheckedChange = { onToggleBanner(b.id) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
                  )
                  IconButton(onClick = { onDeleteBanner(b.id) }, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "মুছুন", tint = AccentRed, modifier = Modifier.size(18.dp))
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
          GlossyAdminCard(
            cornerRadius = 18.dp,
            contentPadding = 16.dp
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Text(
                text = "📢 হোম পেজ হেডার ও অ্যানাউন্সমেন্ট বার",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Black,
                color = AdminInkPrimary
              )

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "অ্যানাউন্সমেন্ট বার প্রদর্শন করুন",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminInkPrimary
                  )
                  Switch(
                    checked = isAnnouncementEnabled,
                    onCheckedChange = { isAnnouncementEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
                  )
                }
              }

              OutlinedTextField(
                value = announcementText,
                onValueChange = { announcementText = it },
                label = { Text("অ্যানাউন্সমেন্ট নোটিশ টেক্সট") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = adminOutlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("স্টোর ব্র্যান্ড নাম") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = adminOutlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
              )

              OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = { Text("ট্যাগলাইন / স্লোগান") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = adminOutlinedTextFieldColors(),
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
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2DeepOcean, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
              ) {
                Text("💾 হেডার সেটিংস সেভ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      "DESIGN" -> {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          StorefrontDesignStudioPanel(
            config = storeConfig,
            onSaveConfig = onSaveStoreConfig
          )
        }
      }
    }
  }
}
