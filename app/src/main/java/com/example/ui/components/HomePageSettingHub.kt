package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.DashboardCustomize
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material.icons.outlined.WebAsset
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import kotlinx.coroutines.delay

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
  val activeBannerCount = remember(banners) { banners.count { it.isActive } }

  Column(modifier = modifier.fillMaxSize()) {
    // 1. Clean White Dynamic Hero Header Card
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
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .shadow(3.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(
                  Brush.linearGradient(listOf(AdminV2DeepOcean, AdminV2CyanDeep))
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.DashboardCustomize,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "হোম পেজ বিল্ডার",
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Black,
                color = AdminInkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "সেকশন অর্ডার, গ্রিড লেআউট, ব্যানার ও লাইভ থিম",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = AdminInkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF1F8FE),
            border = BorderStroke(1.dp, Color(0xFFBAE0F5)),
            modifier = Modifier
              .heightIn(min = 42.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable(onClick = onResetSectionsToDefault)
              .testTag("reset_home_layout_btn")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "ডিফল্ট রিসেট",
                tint = AdminV2CyanDeep,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "রিসেট",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = AdminV2DeepOcean
              )
            }
          }
        }

        // Sleek Live Telemetry Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          HomeStatusChip(
            dotColor = AdminV2Emerald,
            text = "$enabledCount/${homeSections.size} সেকশন চালু",
            bgColor = Color(0xFFECFDF5),
            borderColor = Color(0xFFA7F3D0),
            textColor = Color(0xFF065F46)
          )
          HomeStatusChip(
            dotColor = AdminV2CyanDeep,
            text = "$activeBannerCount ব্যানার লাইভ",
            bgColor = Color(0xFFF0F9FF),
            borderColor = Color(0xFFBAE6FD),
            textColor = AdminV2DeepOcean
          )
          if (storeConfig.isAnnouncementEnabled) {
            HomeStatusChip(
              dotColor = AdminV2Amber,
              text = "নোটিশ চালু",
              bgColor = Color(0xFFFFFBEB),
              borderColor = Color(0xFFFDE68A),
              textColor = Color(0xFF92400E)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Single-Row Sleek Segmented Tab Track (No clipping, crisp M3 icons)
    Surface(
      shape = RoundedCornerShape(16.dp),
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
        HomeSubTabPill(
          label = "সেকশন (${homeSections.size})",
          icon = Icons.Outlined.GridView,
          selected = subTab == "BLOCKS",
          onClick = { subTab = "BLOCKS" },
          modifier = Modifier.testTag("home_setting_subtab_BLOCKS")
        )
        HomeSubTabPill(
          label = "ব্যানার (${banners.size})",
          icon = Icons.Outlined.Collections,
          selected = subTab == "BANNERS",
          onClick = { subTab = "BANNERS" },
          modifier = Modifier.testTag("home_setting_subtab_BANNERS")
        )
        HomeSubTabPill(
          label = "হেডার",
          icon = Icons.Outlined.WebAsset,
          selected = subTab == "HEADER",
          onClick = { subTab = "HEADER" },
          modifier = Modifier.testTag("home_setting_subtab_HEADER")
        )
        HomeSubTabPill(
          label = "ডিজাইন",
          icon = Icons.Outlined.Palette,
          selected = subTab == "DESIGN",
          onClick = { subTab = "DESIGN" },
          modifier = Modifier.testTag("home_setting_subtab_DESIGN")
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Smooth Animated Sub-Tab Switching
    AnimatedContent(
      targetState = subTab,
      transitionSpec = {
        (fadeIn(animationSpec = tween(180)) + expandVertically(animationSpec = tween(180)))
          .togetherWith(fadeOut(animationSpec = tween(140)))
      },
      label = "home_setting_subtab_content",
      modifier = Modifier.weight(1f)
    ) { activeSubTab ->
      when (activeSubTab) {
        "BLOCKS" -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 28.dp)
          ) {
            // Quick-Action Block Creator Card
            item(key = "block_creator_card") {
              Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(
                  1.dp,
                  if (creatorType != null) AdminV2CyanDeep else Color(0xFFD6E8F5)
                ),
                shadowElevation = if (creatorType != null) 5.dp else 2.dp,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "নতুন সেকশন বা ব্যানার যোগ করুন",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AdminInkPrimary
                      )
                      Text(
                        text = "যেকোনো কার্ডে ট্যাপ করে হোম পেজে নতুন ব্লক যুক্ত করুন",
                        fontSize = 11.sp,
                        color = AdminInkMuted
                      )
                    }
                    if (creatorType != null) {
                      IconButton(
                        onClick = { creatorType = null },
                        modifier = Modifier.size(36.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Close,
                          contentDescription = "বন্ধ করুন",
                          tint = AdminInkMuted,
                          modifier = Modifier.size(18.dp)
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    CreatorQuickTile(
                      icon = Icons.Outlined.Category,
                      title = "ক্যাটাগরি",
                      subtitle = "ব্লক যোগ",
                      accentColor = AdminV2CyanDeep,
                      selected = creatorType == "category_products",
                      onClick = {
                        if (creatorType == "category_products") {
                          creatorType = null
                        } else {
                          creatorType = "category_products"
                          newTitle = "$newCategory স্পেশাল"
                          newSubtitle = "নির্বাচিত ক্যাটাগরির প্রোডাক্ট"
                        }
                      },
                      modifier = Modifier
                        .weight(1f)
                        .testTag("add_new_preset_category_products")
                    )
                    CreatorQuickTile(
                      icon = Icons.Outlined.Campaign,
                      title = "প্রোমো ব্যানার",
                      subtitle = "অফার বিজ্ঞাপন",
                      accentColor = AdminV2Amber,
                      selected = creatorType == "promo_banner",
                      onClick = {
                        if (creatorType == "promo_banner") {
                          creatorType = null
                        } else {
                          creatorType = "promo_banner"
                          newTitle = "ঈদ ধামাকা বিজ্ঞাপন অফার"
                          newSubtitle = "অফার বা প্রমোশন ব্যানার"
                        }
                      },
                      modifier = Modifier
                        .weight(1f)
                        .testTag("add_new_preset_promo_banner")
                    )
                    CreatorQuickTile(
                      icon = Icons.Outlined.AutoAwesome,
                      title = "কাস্টম ব্লক",
                      subtitle = "নিজস্ব সংগ্রহ",
                      accentColor = AdminV2Emerald,
                      selected = creatorType == "custom_products",
                      onClick = {
                        if (creatorType == "custom_products") {
                          creatorType = null
                        } else {
                          creatorType = "custom_products"
                          newTitle = "স্পেশাল সিলেকশন"
                          newSubtitle = "নিজের পছন্দমতো কালেকশন"
                        }
                      },
                      modifier = Modifier
                        .weight(1f)
                        .testTag("add_new_preset_custom_products")
                    )
                  }

                  AnimatedVisibility(
                    visible = creatorType != null,
                    enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                  ) {
                    Column(
                      modifier = Modifier.padding(top = 12.dp),
                      verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      HorizontalDivider(color = Color(0xFFE2EFF8))

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
                          text = if (creatorType == "category_products") "মেইন ক্যাটাগরি নির্বাচন করুন:" else "ব্যানার লিংক ক্যাটাগরি:",
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
                            ChoiceChipPill(
                              label = "${cat.icon} ${cat.name}",
                              selected = chosen,
                              onClick = {
                                newCategory = cat.name
                                if (creatorType == "category_products") {
                                  newTitle = "${cat.name} কালেকশন"
                                }
                              }
                            )
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
                          text = "ডিসপ্লে গ্রিড সেটআপ:",
                          fontSize = 11.5.sp,
                          fontWeight = FontWeight.Bold,
                          color = AdminInkPrimary
                        )
                        FlowRow(
                          horizontalArrangement = Arrangement.spacedBy(6.dp),
                          verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                          listOf(2 to "২ কলাম", 3 to "৩ কলাম", 4 to "৪ কলাম").forEach { (cols, label) ->
                            ChoiceChipPill(
                              label = label,
                              selected = newColumnsPerRow == cols,
                              onClick = { newColumnsPerRow = cols }
                            )
                          }
                          listOf("slider" to "স্লাইডার", "grid" to "গ্রিড").forEach { (styleKey, styleLabel) ->
                            ChoiceChipPill(
                              label = styleLabel,
                              selected = newDisplayStyle == styleKey,
                              accentColor = AdminV2CyanDeep,
                              onClick = { newDisplayStyle = styleKey }
                            )
                          }
                          (1..4).forEach { r ->
                            ChoiceChipPill(
                              label = "${r} লাইন",
                              selected = newRowCount == r,
                              onClick = {
                                newRowCount = r
                                if (r > 1) newDisplayStyle = "grid"
                              }
                            )
                          }
                        }
                      }

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Button(
                          onClick = {
                            if (newTitle.isNotBlank() && creatorType != null) {
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
                            .heightIn(min = 48.dp)
                            .testTag("save_new_home_block_btn")
                        ) {
                          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(6.dp))
                          Text("হোম পেজে যুক্ত করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                          onClick = { creatorType = null },
                          border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                          shape = RoundedCornerShape(12.dp),
                          modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                          Text("বাতিল", fontSize = 12.sp, color = AdminInkPrimary, fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                }
              }
            }

            // Dynamic Ordered Section Cards
            itemsIndexed(homeSections, key = { _, s -> s.id }) { index, section ->
              val isProductBlock = section.type in setOf(
                "new_products", "hot_sale", "b2g1_offers", "best_deals", "all_products", "category_products", "custom_products"
              )
              val isLayoutConfigurable = isProductBlock || section.type == "reels_showcase" || section.type == "combo_packs"
              val gridCfg = remember(section) { resolveBlockGridConfig(section) }
              val layoutSummary = "${gridCfg.cols} কলাম × ${gridCfg.rows} লাইন • ${if (gridCfg.isSlider) "স্লাইডার" else "গ্রিড"}"
              val isExpanded = expandedEditorSectionId == section.id
              val blockSearch = sectionProductQueries[section.id].orEmpty()

              val borderColor by animateColorAsState(
                targetValue = when {
                  isExpanded -> AdminV2CyanDeep
                  section.enabled -> Color(0xFFD6E8F5)
                  else -> Color(0xFFE2E8F0)
                },
                label = "block_border_color"
              )
              val cardBg by animateColorAsState(
                targetValue = if (section.enabled) Color.White else Color(0xFFF8FAFC),
                label = "block_bg_color"
              )

              Surface(
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(if (isExpanded) 1.5.dp else 1.dp, borderColor),
                shadowElevation = if (isExpanded) 6.dp else 2.dp,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("home_setting_block_${section.id}")
              ) {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                  // Left Semantic Status Accent Strip
                  Box(
                    modifier = Modifier
                      .width(4.dp)
                      .fillMaxHeight()
                      .background(
                        if (section.enabled) {
                          Brush.verticalGradient(listOf(AdminV2Cyan, AdminV2Emerald))
                        } else {
                          Brush.verticalGradient(listOf(Color(0xFFCBD5E1), Color(0xFFE2E8F0)))
                        }
                      )
                  )

                  Column(
                    modifier = Modifier
                      .weight(1f)
                      .padding(13.dp)
                  ) {
                    // Header Row: Icon + Rank + Title + Spec + Switch
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
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
                            .background(
                              if (section.enabled) Color(0xFFEBF6FD) else Color(0xFFF1F5F9)
                            ),
                          contentAlignment = Alignment.Center
                        ) {
                          Icon(
                            imageVector = sectionVectorIcon(section.type),
                            contentDescription = null,
                            tint = if (section.enabled) AdminV2CyanDeep else AdminInkMuted,
                            modifier = Modifier.size(20.dp)
                          )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                          Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                          ) {
                            Surface(
                              shape = RoundedCornerShape(6.dp),
                              color = if (section.enabled) AdminV2DeepOcean else Color(0xFF94A3B8)
                            ) {
                              Text(
                                text = (index + 1).toString().padStart(2, '0'),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                              )
                            }
                            Text(
                              text = section.title,
                              fontSize = 13.5.sp,
                              fontWeight = FontWeight.ExtraBold,
                              color = if (section.enabled) AdminInkPrimary else AdminInkMuted,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                            )
                          }

                          Spacer(modifier = Modifier.height(3.dp))

                          Text(
                            text = when (section.type) {
                              "new_products" -> "$layoutSummary • নতুন + ${section.productIds.size} পিন্ড"
                              "hot_sale" -> "$layoutSummary • ${products.count { it.isHotSale || section.productIds.contains(it.id) }} টি পণ্য"
                              "combo_packs" -> "$layoutSummary • বান্ডেল অফার"
                              "b2g1_offers" -> "$layoutSummary • ${products.count { it.isB2G1 || section.productIds.contains(it.id) }} টি পণ্য"
                              "best_deals" -> "$layoutSummary • ${section.productIds.size} সিলেক্টেড"
                              "reels_showcase" -> "$layoutSummary • শপেবল রিলস"
                              "all_products" -> "$layoutSummary • সম্পূর্ণ ক্যাটালগ"
                              "category_products" -> "$layoutSummary • ${section.categoryName}"
                              "promo_banner" -> "প্রমোশন ব্যানার • ${section.bannerLinkCategory}"
                              else -> section.subtitle.ifBlank { section.type }
                            },
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = AdminInkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                          )
                        }
                      }

                      Spacer(modifier = Modifier.width(6.dp))

                      Switch(
                        checked = section.enabled,
                        onCheckedChange = { onToggleSection(section.id) },
                        colors = SwitchDefaults.colors(
                          checkedThumbColor = Color.White,
                          checkedTrackColor = AccentGreen,
                          uncheckedThumbColor = Color.White,
                          uncheckedTrackColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.testTag("toggle_section_${section.id}")
                      )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Clean Action Footer Rail: Reorder Stepper | Customize Pill | Delete
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        // Grouped Reorder Stepper
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = Color(0xFFF4F9FD),
                          border = BorderStroke(1.dp, Color(0xFFD6E8F5))
                        ) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
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
                                tint = if (index > 0) AdminV2DeepOcean else Color(0xFFCBD5E1),
                                modifier = Modifier.size(16.dp)
                              )
                            }
                            Box(
                              modifier = Modifier
                                .width(1.dp)
                                .height(18.dp)
                                .background(Color(0xFFD6E8F5))
                            )
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
                                tint = if (index < homeSections.lastIndex) AdminV2DeepOcean else Color(0xFFCBD5E1),
                                modifier = Modifier.size(16.dp)
                              )
                            }
                          }
                        }

                        if (isLayoutConfigurable || section.type == "promo_banner") {
                          Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isExpanded) AdminV2DeepOcean else Color(0xFFF0F9FF),
                            border = BorderStroke(
                              1.dp,
                              if (isExpanded) AdminV2DeepOcean else Color(0xFFBAE6FD)
                            ),
                            modifier = Modifier
                              .heightIn(min = 38.dp)
                              .clip(RoundedCornerShape(10.dp))
                              .clickable {
                                expandedEditorSectionId = if (isExpanded) null else section.id
                              }
                              .testTag("manage_block_${section.id}")
                          ) {
                            Row(
                              modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                              Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = null,
                                tint = if (isExpanded) Color.White else AdminV2CyanDeep,
                                modifier = Modifier.size(14.dp)
                              )
                              Text(
                                text = when {
                                  isExpanded -> "বন্ধ করুন"
                                  isProductBlock -> "কাস্টমাইজ ও প্রোডাক্ট"
                                  section.type == "reels_showcase" || section.type == "combo_packs" -> "গ্রিড ও কলাম"
                                  else -> "ব্যানার এডিট"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpanded) Color.White else AdminV2DeepOcean
                              )
                              Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (isExpanded) Color.White else AdminV2CyanDeep,
                                modifier = Modifier.size(15.dp)
                              )
                            }
                          }
                        } else {
                          Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (section.enabled) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                            border = BorderStroke(
                              1.dp,
                              if (section.enabled) Color(0xFFA7F3D0) else Color(0xFFE2E8F0)
                            )
                          ) {
                            Text(
                              text = if (section.enabled) "সক্রিয়" else "নিষ্ক্রিয়",
                              fontSize = 10.5.sp,
                              color = if (section.enabled) Color(0xFF065F46) else AdminInkMuted,
                              fontWeight = FontWeight.Bold,
                              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                          }
                        }
                      }

                      Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFF1F2),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3))
                      ) {
                        IconButton(
                          onClick = { onDeleteSection(section.id) },
                          modifier = Modifier
                            .size(36.dp)
                            .testTag("delete_section_${section.id}")
                        ) {
                          Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "ব্লক মুছুন",
                            tint = AccentRed,
                            modifier = Modifier.size(17.dp)
                          )
                        }
                      }
                    }

                    // Smooth Animated Inline Editor Drawer
                    AnimatedVisibility(
                      visible = isExpanded,
                      enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                      exit = shrinkVertically() + fadeOut()
                    ) {
                      Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                      ) {
                        HorizontalDivider(color = Color(0xFFE2EFF8))

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
                            modifier = Modifier.width(66.dp)
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
                              ChoiceChipPill(
                                label = "${cat.icon} ${cat.name}",
                                selected = section.categoryName == cat.name,
                                onClick = {
                                  onUpdateSection(section.id) {
                                    it.copy(categoryName = cat.name, badge = cat.name)
                                  }
                                }
                              )
                            }
                          }
                        }

                        if (isLayoutConfigurable) {
                          Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF4FAFF),
                            border = BorderStroke(1.dp, Color(0xFFD0E7F7)),
                            modifier = Modifier.fillMaxWidth()
                          ) {
                            Column(
                              modifier = Modifier.padding(12.dp),
                              verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                              Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                Text(
                                  text = "কলাম ও গ্রিড ডিসপ্লে",
                                  fontSize = 12.sp,
                                  fontWeight = FontWeight.ExtraBold,
                                  color = AdminInkPrimary
                                )
                                Surface(
                                  shape = RoundedCornerShape(8.dp),
                                  color = Color.White,
                                  border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                                ) {
                                  Text(
                                    text = layoutSummary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminV2CyanDeep,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                  )
                                }
                              }

                              // Dynamic Live Mini-Grid Preview Diagram
                              MiniLayoutPreviewDiagram(
                                cols = gridCfg.cols,
                                rows = if (gridCfg.isSlider) 1 else gridCfg.rows,
                                isSlider = gridCfg.isSlider
                              )

                              // Columns per row (2 / 3 / 4) & Mode (Slider / Grid)
                              Row(
                                modifier = Modifier
                                  .fillMaxWidth()
                                  .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                Text(
                                  text = "কলাম:",
                                  fontSize = 10.5.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = AdminInkSecondary
                                )
                                listOf(2 to "২টি", 3 to "৩টি", 4 to "৪টি").forEach { (cols, label) ->
                                  ChoiceChipPill(
                                    label = label,
                                    selected = gridCfg.cols == cols,
                                    onClick = {
                                      onUpdateSection(section.id) { sec ->
                                        val targetMax = if (sec.displayStyle == "grid") {
                                          cols * gridCfg.rows
                                        } else if (sec.type == "reels_showcase") {
                                          cols
                                        } else {
                                          maxOf(sec.maxItems, cols * 2)
                                        }
                                        sec.copy(columnsPerRow = cols, maxItems = targetMax)
                                      }
                                    },
                                    modifier = Modifier.testTag("cols_${section.id}_$cols")
                                  )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                listOf("slider" to "স্লাইডার", "grid" to "গ্রিড").forEach { (styleKey, styleLabel) ->
                                  ChoiceChipPill(
                                    label = styleLabel,
                                    selected = (if (gridCfg.isSlider) "slider" else "grid") == styleKey,
                                    accentColor = AdminV2CyanDeep,
                                    onClick = {
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
                                    },
                                    modifier = Modifier.testTag("style_${section.id}_$styleKey")
                                  )
                                }
                              }

                              // Row Count (1..4)
                              Row(
                                modifier = Modifier
                                  .fillMaxWidth()
                                  .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                Text(
                                  text = "লাইন:",
                                  fontSize = 10.5.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = AdminInkSecondary
                                )
                                (1..4).forEach { r ->
                                  ChoiceChipPill(
                                    label = "${r} লাইন",
                                    selected = gridCfg.rows == r,
                                    onClick = {
                                      onUpdateSection(section.id) { sec ->
                                        val nextStyle = if (r > 1) "grid" else sec.displayStyle
                                        sec.copy(
                                          rowCount = r,
                                          displayStyle = nextStyle,
                                          layout = if (nextStyle == "grid") "grid3x2" else "slider3",
                                          maxItems = if (nextStyle == "grid") {
                                            gridCfg.cols * r
                                          } else if (sec.type == "reels_showcase") {
                                            gridCfg.cols
                                          } else {
                                            12
                                          }
                                        )
                                      }
                                    },
                                    modifier = Modifier.testTag("rows_${section.id}_$r")
                                  )
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
                                "${section.title}-এর প্রোডাক্ট খুঁজুন (নাম, SKU)...",
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
                                color = if (isSelected) Color(0xFFF0F9FF) else Color(0xFFFAFAFC),
                                border = BorderStroke(
                                  1.dp,
                                  if (isSelected) AdminV2CyanDeep else Color(0xFFE2E8F0)
                                ),
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
                                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                                    modifier = Modifier.weight(1f)
                                  ) {
                                    AppImage(
                                      model = prod.imageUrl,
                                      contentDescription = prod.name,
                                      modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(9.dp))
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
                                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
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
          }
        }

        "BANNERS" -> {
          Column(modifier = Modifier.fillMaxSize()) {
            Surface(
              shape = RoundedCornerShape(18.dp),
              color = Color.White,
              border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
              shadowElevation = 2.dp,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "হিরো ব্যানার স্লাইডার (${banners.size})",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AdminInkPrimary
                  )
                  Text(
                    text = "স্টোরফ্রন্টের শীর্ষ ব্যানার চালু/বন্ধ ও নতুন ব্যানার যোগ করুন",
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
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = Color.White,
                  border = BorderStroke(
                    1.dp,
                    if (b.isActive) Color(0xFFBAE6FD) else Color(0xFFE2E8F0)
                  ),
                  shadowElevation = 2.dp,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    AppImage(
                      model = b.imageUrl,
                      contentDescription = b.title,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier
                        .width(74.dp)
                        .height(54.dp)
                        .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = b.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AdminInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = b.subtitle,
                        fontSize = 11.sp,
                        color = AdminInkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF0F9FF),
                        border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                      ) {
                        Text(
                          text = b.buttonText,
                          fontSize = 10.sp,
                          color = AdminV2CyanDeep,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                      }
                    }
                    Switch(
                      checked = b.isActive,
                      onCheckedChange = { onToggleBanner(b.id) },
                      colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFCBD5E1)
                      )
                    )
                    IconButton(
                      onClick = { onDeleteBanner(b.id) },
                      modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                      Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "মুছুন",
                        tint = AccentRed,
                        modifier = Modifier.size(18.dp)
                      )
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
          var headerSavedBanner by remember { mutableStateOf(false) }

          LaunchedEffect(headerSavedBanner) {
            if (headerSavedBanner) {
              delay(2500)
              headerSavedBanner = false
            }
          }

          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Live Mini Storefront Header Preview
            Surface(
              shape = RoundedCornerShape(18.dp),
              color = Color.White,
              border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
              shadowElevation = 2.dp,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = "লাইভ হেডার প্রিভিউ",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkMuted
                )
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column {
                    if (isAnnouncementEnabled) {
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .background(Color(0xFF0F294A))
                          .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = announcementText.ifBlank { "ঈদ স্পেশাল অফার চলছে!" },
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFFFDE68A),
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                      }
                    }
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBF5))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = storeName.ifBlank { "SAJGOJ FASHION" },
                          fontSize = 13.5.sp,
                          fontWeight = FontWeight.Black,
                          color = Color(0xFF7A1C2B),
                          maxLines = 1
                        )
                        Text(
                          text = tagline.ifBlank { "রয়েল বুটিক ও জুয়েলারি কালেকশন" },
                          fontSize = 10.sp,
                          color = Color(0xFF6B4F4B),
                          maxLines = 1
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF7A1C2B)
                      ) {
                        Text(
                          text = "কার্ট (0)",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White,
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                      }
                    }
                  }
                }
              }
            }

            Surface(
              shape = RoundedCornerShape(18.dp),
              color = Color.White,
              border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
              shadowElevation = 2.dp,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Text(
                  text = "ব্র্যান্ড ও অ্যানাউন্সমেন্ট সেটিংস",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = AdminInkPrimary
                )

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (isAnnouncementEnabled) Color(0xFFF0F9FF) else Color(0xFFF8FAFC),
                  border = BorderStroke(1.dp, if (isAnnouncementEnabled) Color(0xFFBAE6FD) else Color(0xFFE2E8F0)),
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
                    headerSavedBanner = true
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = AdminV2DeepOcean, contentColor = Color.White),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                ) {
                  Icon(
                    imageVector = if (headerSavedBanner) Icons.Default.Check else Icons.Outlined.Storefront,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (headerSavedBanner) "হেডার সেটিংস সেভ হয়েছে" else "হেডার সেটিংস সেভ করুন",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
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
}

@Composable
private fun HomeStatusChip(
  dotColor: Color,
  text: String,
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
      modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(dotColor)
      )
      Text(
        text = text,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun HomeSubTabPill(
  label: String,
  icon: ImageVector,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg by animateColorAsState(
    targetValue = if (selected) Color.White else Color.Transparent,
    animationSpec = tween(180, easing = FastOutSlowInEasing),
    label = "subtab_bg"
  )
  val contentColor by animateColorAsState(
    targetValue = if (selected) AdminV2DeepOcean else AdminInkSecondary,
    label = "subtab_ink"
  )
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bg,
    shadowElevation = if (selected) 2.dp else 0.dp,
    border = if (selected) BorderStroke(1.dp, Color(0xFFBAE6FD)) else null,
    modifier = modifier
      .heightIn(min = 40.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (selected) AdminV2CyanDeep else AdminInkMuted,
        modifier = Modifier.size(15.dp)
      )
      Text(
        text = label,
        fontSize = 11.5.sp,
        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
        color = contentColor,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun CreatorQuickTile(
  icon: ImageVector,
  title: String,
  subtitle: String,
  accentColor: Color,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (selected) accentColor.copy(alpha = 0.12f) else Color(0xFFF7FBFE),
    border = BorderStroke(
      width = if (selected) 1.5.dp else 1.dp,
      color = if (selected) accentColor else Color(0xFFD6E8F5)
    ),
    modifier = modifier
      .heightIn(min = 74.dp)
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(30.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(16.dp)
        )
      }
      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = AdminInkPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = subtitle,
        fontSize = 9.5.sp,
        color = AdminInkMuted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun ChoiceChipPill(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = AdminV2DeepOcean
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (selected) accentColor else Color.White,
    border = BorderStroke(1.dp, if (selected) accentColor else Color(0xFFCBD5E1)),
    modifier = modifier
      .heightIn(min = 36.dp)
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      Text(
        text = label,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) Color.White else AdminInkPrimary
      )
    }
  }
}

@Composable
private fun MiniLayoutPreviewDiagram(
  cols: Int,
  rows: Int,
  isSlider: Boolean
) {
  val safeCols = cols.coerceIn(2, 4)
  val safeRows = rows.coerceIn(1, 3)
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color.White,
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      repeat(safeRows) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          repeat(safeCols) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(if (isSlider) 26.dp else 20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE0F2FE))
                .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(6.dp))
            )
          }
          if (isSlider) {
            Box(
              modifier = Modifier
                .width(12.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                .background(Color(0xFFF1F5F9))
            )
          }
        }
      }
    }
  }
}

private fun sectionVectorIcon(type: String): ImageVector = when (type) {
  "hero_banner", "promo_banner" -> Icons.Outlined.ViewCarousel
  "new_products" -> Icons.Outlined.NewReleases
  "hot_sale" -> Icons.Outlined.LocalFireDepartment
  "combo_packs" -> Icons.Outlined.ShoppingBag
  "b2g1_offers" -> Icons.Outlined.LocalOffer
  "reels_showcase" -> Icons.Outlined.Movie
  "category_products" -> Icons.Outlined.Category
  "custom_products" -> Icons.Outlined.AutoAwesome
  else -> Icons.Outlined.GridView
}
