package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.StoreConfig
import com.example.firebase.AuthUserSummary
import com.example.ui.AppScreen
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex

private fun parseOptColor(hex: String, fallback: Color): Color {
  val trimmed = hex.trim()
  if (trimmed.equals("transparent", ignoreCase = true)) return Color.Transparent
  return parseColorHex(trimmed, fallback)
}

fun orderCategoriesByHomePage(categories: List<Category>, categoryOrder: List<String>): List<Category> {
  if (categoryOrder.isEmpty()) return categories
  val orderMap = categoryOrder.withIndex().associate { it.value to it.index }
  return categories.sortedWith(compareBy { cat ->
    orderMap[cat.id] ?: orderMap[cat.name] ?: Int.MAX_VALUE
  })
}

@Composable
fun StoreHeader(
  config: StoreConfig,
  currentScreen: AppScreen,
  cartItemCount: Int,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onToggleScreen: () -> Unit,
  onOpenCart: () -> Unit,
  categories: List<Category>,
  selectedCategory: String,
  onCategorySelected: (String) -> Unit,
  onHomeReset: () -> Unit = {},
  onVoiceSearchClick: () -> Unit = {},
  onImageSearchClick: () -> Unit = {},
  onScanClick: () -> Unit = {},
  isImageSearchActive: Boolean = false,
  onClearAllSearch: () -> Unit = { onSearchChange("") },
  authUser: AuthUserSummary? = null,
  isAuthBusy: Boolean = false,
  onGoogleAuthClick: () -> Unit = {},
  unreadNotificationCount: Int = 0,
  onOpenNotifications: () -> Unit = {},
  showSearchStripInHeader: Boolean = true
) {
  val hp = config.pageConfig.homePage
  val orderedCategories = remember(categories, hp.categoryOrder) {
    orderCategoriesByHomePage(categories, hp.categoryOrder)
  }
  val d = config.design
  val customHdr = d.useCustomHeaderColors
  val headerSurfaceColor = if (customHdr) parseColorHex(d.headerBg, Color.White) else Color.White
  val announcementBgColor = if (customHdr) parseColorHex(d.announcementBg, RoyalMaroon) else RoyalMaroon
  val announcementText = if (customHdr) parseColorHex(d.announcementTextColor, CreamSurface) else CreamSurface
  val brandTitleColor = if (customHdr) parseColorHex(d.headerTextColor, RoyalMaroon) else RoyalMaroon
  val cartBtnBg = if (customHdr) parseColorHex(d.cartBg, RoyalMaroon) else RoyalMaroon
  val cartBtnText = if (customHdr) parseColorHex(d.cartTextColor, Color.White) else Color.White
  val cartBadgeBg = if (customHdr) parseColorHex(d.cartBadgeBg, AntiqueGold) else AntiqueGold
  val cartBadgeText = if (customHdr) parseColorHex(d.cartBadgeText, DarkEspresso) else DarkEspresso

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(headerSurfaceColor)
  ) {
    // 1. Announcement Bar
    if (config.isAnnouncementEnabled && config.announcementText.isNotBlank()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(announcementBgColor)
          .padding(vertical = 6.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = config.announcementText,
          color = announcementText,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    // 2. Main Title Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .weight(1f)
          .clickable { onHomeReset() }
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(CreamSurface),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "👑", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = config.storeName,
            color = brandTitleColor,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = config.tagline,
            color = AntiqueGoldDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Actions: Google Account, Notifications, Switch Admin & Open Cart
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        // Google Sign-In / Signed-in Logout Pill (styled via HomePageConfig)
        val loginStart = parseOptColor(hp.loginBtnBg, Color(0xFF0B0F19))
        val loginEnd = parseOptColor(hp.loginBtnBgEnd, loginStart)
        val loginTxt = parseOptColor(hp.loginBtnText, Color.White)
        val loginBrd = parseOptColor(hp.loginBtnBorder, Color.White)
        val loginShape = RoundedCornerShape(hp.loginBtnRadiusPx.coerceIn(0, 32).dp)
        Surface(
          shape = loginShape,
          color = Color.Transparent,
          border = BorderStroke(
            1.dp,
            if (authUser != null) Color(0xFFF43F5E) else loginBrd
          ),
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(loginShape)
            .background(
              if (authUser != null) {
                Brush.horizontalGradient(listOf(Color(0xFFFFF1F2), Color(0xFFFFE4E6)))
              } else {
                Brush.horizontalGradient(listOf(loginStart, loginEnd))
              }
            )
            .clickable(enabled = !isAuthBusy) { onGoogleAuthClick() }
            .testTag("header_google_auth_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = if (authUser != null) Icons.Default.PowerSettingsNew else Icons.Default.AccountCircle,
              contentDescription = if (authUser != null) "লগআউট করুন (${authUser.displayName})" else "Google সাইন-ইন",
              tint = if (authUser != null) Color(0xFFBE123C) else loginTxt,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = when {
                isAuthBusy -> "..."
                authUser != null -> "লগআউট"
                else -> hp.loginBtnMobileLabel.ifBlank { "লগইন" }
              },
              color = if (authUser != null) Color(0xFFBE123C) else loginTxt,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Order Status Notifications Bell Button
        if (currentScreen == AppScreen.STOREFRONT) {
          IconButton(
            onClick = onOpenNotifications,
            modifier = Modifier
              .size(48.dp)
              .testTag("header_notifications_button")
          ) {
            BadgedBox(
              badge = {
                if (unreadNotificationCount > 0) {
                  Badge(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                  ) {
                    Text(
                      text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(CreamSurface),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (unreadNotificationCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                  contentDescription = "অর্ডার নোটিফিকেশন",
                  tint = brandTitleColor,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }

        // Toggle Admin / Store Button
        IconButton(
          onClick = onToggleScreen,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(CreamSurface)
            .testTag("admin_toggle_button")
        ) {
          Icon(
            imageVector = if (currentScreen == AppScreen.STOREFRONT) Icons.Default.AdminPanelSettings else Icons.Default.Storefront,
            contentDescription = "Switch Panel",
            tint = brandTitleColor
          )
        }

        // Cart Button
        if (currentScreen == AppScreen.STOREFRONT) {
          BadgedBox(
            badge = {
              if (cartItemCount > 0) {
                Badge(
                  containerColor = cartBadgeBg,
                  contentColor = cartBadgeText,
                  modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                ) {
                  Text(
                    text = cartItemCount.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }
            }
          ) {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = cartBtnBg,
              shadowElevation = 2.dp,
              modifier = Modifier
                .clickable { onOpenCart() }
                .testTag("cart_open_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ShoppingCart,
                  contentDescription = "Cart",
                  tint = cartBtnText,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "কার্ট",
                  color = cartBtnText,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }

    // 3. Compact Search Strip (when pinned in Header) + Horizontal Category Scroll Bar
    if (currentScreen == AppScreen.STOREFRONT) {
      if (showSearchStripInHeader && hp.showSearchBar) {
        StorefrontCompactSearchStrip(
          config = config,
          categories = orderedCategories,
          selectedCategory = selectedCategory,
          searchQuery = searchQuery,
          isImageSearchActive = isImageSearchActive,
          onSearchChange = onSearchChange,
          onCategorySelected = onCategorySelected,
          onHomeReset = onHomeReset,
          onVoiceSearchClick = onVoiceSearchClick,
          onImageSearchClick = onImageSearchClick,
          onScanClick = onScanClick,
          onClearAllSearch = onClearAllSearch
        )
      }

      // 4. Horizontal Category Scroll Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // "All" Pill
        CategoryPill(
          label = "সকল কালেকশন",
          icon = "✨",
          isSelected = selectedCategory == "All",
          onClick = { onCategorySelected("All") }
        )

        orderedCategories.forEach { cat ->
          CategoryPill(
            label = cat.name,
            icon = cat.icon,
            isSelected = selectedCategory == cat.name,
            onClick = { onCategorySelected(cat.name) }
          )
        }
      }
    }

    // Divider line
    Spacer(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(BorderGold)
    )
  }
}

@Composable
fun StorefrontCompactSearchStrip(
  config: StoreConfig,
  categories: List<Category>,
  selectedCategory: String,
  searchQuery: String,
  isImageSearchActive: Boolean,
  onSearchChange: (String) -> Unit,
  onCategorySelected: (String) -> Unit,
  onHomeReset: () -> Unit,
  onVoiceSearchClick: () -> Unit,
  onImageSearchClick: () -> Unit,
  onScanClick: () -> Unit,
  onClearAllSearch: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isCategoryMenuExpanded by remember { mutableStateOf(false) }
  val hp = config.pageConfig.homePage
  if (!hp.showSearchBar) return

  val orderedCats = remember(categories, hp.categoryOrder) {
    orderCategoriesByHomePage(categories, hp.categoryOrder)
  }
  val containerBg = parseOptColor(hp.searchBarContainerBg, Color.Transparent)
  val containerBorder = parseOptColor(hp.searchBarContainerBorder, Color.Transparent)
  val radiusDp = hp.searchRadiusPx.coerceIn(0, 32).dp
  val borderW = hp.searchBorderWidthPx.coerceIn(0, 4).dp
  val barOrder = hp.searchBarOrder.ifEmpty { listOf("cat_menu", "home_btn", "search_input") }
  val toolsOrder = hp.searchToolsOrder.ifEmpty { listOf("voice", "image", "scan") }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 10.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(radiusDp))
      .background(containerBg)
      .then(
        if (containerBorder != Color.Transparent && borderW > 0.dp) {
          Modifier.border(borderW, containerBorder, RoundedCornerShape(radiusDp))
        } else {
          Modifier
        }
      )
      .padding(horizontal = if (containerBg != Color.Transparent) 6.dp else 0.dp, vertical = if (containerBg != Color.Transparent) 4.dp else 0.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(5.dp)
  ) {
    barOrder.forEach { slot ->
      when (slot) {
        "cat_menu" -> if (hp.showCatMenuBtn) {
          val catStart = parseOptColor(hp.searchCatBtnBg, Color(0xFFFCA5A5))
          val catEnd = parseOptColor(hp.searchCatBtnBgEnd, catStart)
          val catTxt = parseOptColor(hp.searchCatBtnText, Color.Black)
          val catBrd = parseOptColor(hp.searchCatBtnBorder, Color(0xFFDC2626))
          Box {
            Surface(
              shape = RoundedCornerShape(radiusDp),
              color = Color.Transparent,
              border = if (borderW > 0.dp) BorderStroke(borderW, catBrd) else null,
              shadowElevation = 2.dp,
              modifier = Modifier
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(radiusDp))
                .background(Brush.horizontalGradient(listOf(catStart, catEnd)))
                .clickable { isCategoryMenuExpanded = true }
                .testTag("all_categories_menu_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Menu,
                  contentDescription = "সকল ক্যাটাগরী মেনু",
                  tint = catTxt,
                  modifier = Modifier.size(15.dp)
                )
                Text(
                  text = if (selectedCategory == "All") hp.catMenuLabel.ifBlank { "সকল ক্যাটাগরী" } else selectedCategory,
                  color = catTxt,
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.ExtraBold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Icon(
                  imageVector = Icons.Default.ArrowDropDown,
                  contentDescription = null,
                  tint = catTxt,
                  modifier = Modifier.size(15.dp)
                )
              }
            }

            DropdownMenu(
              expanded = isCategoryMenuExpanded,
              onDismissRequest = { isCategoryMenuExpanded = false },
              modifier = Modifier
                .background(Color.White)
                .width(220.dp)
            ) {
              DropdownMenuItem(
                text = {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Text(text = "🌟", fontSize = 14.sp)
                    Text(
                      text = "সকল পণ্য (All)",
                      fontWeight = if (selectedCategory == "All") FontWeight.Black else FontWeight.SemiBold,
                      color = if (selectedCategory == "All") RoyalMaroon else DarkEspresso,
                      fontSize = 13.sp
                    )
                  }
                },
                onClick = {
                  onCategorySelected("All")
                  isCategoryMenuExpanded = false
                }
              )
              HorizontalDivider(color = BorderGold.copy(alpha = 0.4f))
              orderedCats.filter { it.type != "sub" }.forEach { cat ->
                DropdownMenuItem(
                  text = {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Text(text = cat.icon.ifBlank { "✨" }, fontSize = 14.sp)
                      Text(
                        text = cat.name,
                        fontWeight = if (selectedCategory == cat.name) FontWeight.Black else FontWeight.Medium,
                        color = if (selectedCategory == cat.name) RoyalMaroon else DarkEspresso,
                        fontSize = 13.sp
                      )
                    }
                  },
                  onClick = {
                    onCategorySelected(cat.name)
                    isCategoryMenuExpanded = false
                  }
                )
              }
            }
          }
        }

        "home_btn" -> if (hp.showHomeBtn) {
          val homeStart = parseOptColor(hp.searchHomeBtnBg, Color(0xFFFCA5A5))
          val homeEnd = parseOptColor(hp.searchHomeBtnBgEnd, homeStart)
          val homeTxt = parseOptColor(hp.searchHomeBtnText, Color.Black)
          val homeBrd = parseOptColor(hp.searchHomeBtnBorder, Color(0xFFDC2626))
          Surface(
            shape = CircleShape,
            color = Color.Transparent,
            border = if (borderW > 0.dp) BorderStroke(borderW, homeBrd) else null,
            shadowElevation = 2.dp,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Brush.horizontalGradient(listOf(homeStart, homeEnd)))
              .clickable { onHomeReset() }
              .testTag("home_reset_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "হোম পেজ ও সব ফিল্টার রিসেট",
                tint = homeTxt,
                modifier = Modifier.size(19.dp)
              )
            }
          }
        }

        "search_input" -> {
          val inputBg = parseOptColor(hp.searchInputBg, CreamBackground)
          val inputTxt = parseOptColor(hp.searchInputText, DarkEspresso)
          val inputBrd = parseOptColor(hp.searchInputBorder, BorderGold)
          val toolBg = parseOptColor(hp.searchToolBtnBg, Color(0xFFFEF2F2))
          val toolTxt = parseOptColor(hp.searchToolBtnText, Color.Black)
          val toolBrd = parseOptColor(hp.searchToolBtnBorder, Color(0xFFFECACA))
          val scanStart = parseOptColor(hp.searchScanBtnBg, Color(0xFFFCA5A5))
          val scanEnd = parseOptColor(hp.searchScanBtnBgEnd, scanStart)
          val scanTxt = parseOptColor(hp.searchScanBtnText, Color.Black)
          val scanBrd = parseOptColor(hp.searchScanBtnBorder, Color(0xFFDC2626))

          Surface(
            shape = RoundedCornerShape(radiusDp),
            color = inputBg,
            border = if (borderW > 0.dp) BorderStroke(borderW, inputBrd) else null,
            shadowElevation = 1.dp,
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 42.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp, top = 3.dp, bottom = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = inputTxt.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
              )

              Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
              ) {
                if (searchQuery.isEmpty()) {
                  Text(
                    text = hp.searchPlaceholder.ifBlank { "খুঁজুন..." },
                    fontSize = 11.5.sp,
                    color = inputTxt.copy(alpha = 0.55f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
                BasicTextField(
                  value = searchQuery,
                  onValueChange = onSearchChange,
                  singleLine = true,
                  textStyle = TextStyle(
                    color = inputTxt,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                  ),
                  cursorBrush = SolidColor(RoyalMaroon),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input")
                )
              }

              if (searchQuery.isNotEmpty() || isImageSearchActive) {
                Box(
                  modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .clickable { onClearAllSearch() }
                    .testTag("search_clear_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }

              toolsOrder.forEach { tool ->
                when (tool) {
                  "voice" -> if (hp.showVoiceBtn) {
                    Surface(
                      shape = CircleShape,
                      color = toolBg,
                      border = if (borderW > 0.dp) BorderStroke(borderW, toolBrd) else null,
                      modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable { onVoiceSearchClick() }
                        .testTag("voice_search_button")
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Icon(
                          imageVector = Icons.Default.Mic,
                          contentDescription = "ভয়েস সার্চ",
                          tint = toolTxt,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                  }

                  "image" -> if (hp.showImageSearchBtn) {
                    Surface(
                      shape = CircleShape,
                      color = if (isImageSearchActive) Color(0xFFEF4444) else toolBg,
                      border = if (borderW > 0.dp) BorderStroke(borderW, if (isImageSearchActive) Color.Black else toolBrd) else null,
                      modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable { onImageSearchClick() }
                        .testTag("image_search_button")
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Icon(
                          imageVector = Icons.Default.ImageSearch,
                          contentDescription = "ছবি দিয়ে প্রোডাক্ট সার্চ",
                          tint = toolTxt,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                  }

                  "scan" -> if (hp.showScanBtn) {
                    Surface(
                      shape = RoundedCornerShape(radiusDp),
                      color = Color.Transparent,
                      border = if (borderW > 0.dp) BorderStroke(borderW, scanBrd) else null,
                      modifier = Modifier
                        .clip(RoundedCornerShape(radiusDp))
                        .background(Brush.horizontalGradient(listOf(scanStart, scanEnd)))
                        .clickable { onScanClick() }
                        .testTag("scan_search_button")
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.QrCodeScanner,
                          contentDescription = "স্ক্যান করুন",
                          tint = scanTxt,
                          modifier = Modifier.size(13.dp)
                        )
                        Text(
                          text = hp.scanBtnLabel.ifBlank { "স্ক্যান" },
                          color = scanTxt,
                          fontSize = 9.5.sp,
                          fontWeight = FontWeight.Black,
                          maxLines = 1
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

@Composable
fun CategoryPill(
  label: String,
  icon: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = if (isSelected) RoyalMaroon else Color.White,
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isSelected) RoyalMaroon else BorderGold
    ),
    shadowElevation = if (isSelected) 2.dp else 0.dp,
    modifier = Modifier
      .clickable { onClick() }
      .testTag("cat_pill_$label")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(text = icon, fontSize = 13.sp)
      Text(
        text = label,
        color = if (isSelected) Color.White else DarkEspresso,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}
