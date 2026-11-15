package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
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
import com.example.ui.AppScreen
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

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
  onClearAllSearch: () -> Unit = { onSearchChange("") }
) {
  var isCategoryMenuExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.White)
  ) {
    // 1. Announcement Bar
    if (config.isAnnouncementEnabled && config.announcementText.isNotBlank()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(RoyalMaroon)
          .padding(vertical = 6.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = config.announcementText,
          color = CreamSurface,
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
            color = RoyalMaroon,
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

      // Actions: Switch Admin & Open Cart
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
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
            tint = RoyalMaroon
          )
        }

        // Cart Button
        if (currentScreen == AppScreen.STOREFRONT) {
          BadgedBox(
            badge = {
              if (cartItemCount > 0) {
                Badge(
                  containerColor = AntiqueGold,
                  contentColor = DarkEspresso,
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
              color = RoyalMaroon,
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
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "কার্ট",
                  color = Color.White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }

    // 3. Compact Search Strip (Storefront only):
    // [সকল ক্যাটাগরী ▾] [🏠] [🔍 Short Search Box + 🎙️ + 🖼️ + 📷 স্ক্যান]
    if (currentScreen == AppScreen.STOREFRONT) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        // 3A. "সকল ক্যাটাগরী" Menu Trigger + DropdownMenu
        Box {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFEF4444),
            border = BorderStroke(1.dp, Color.White),
            shadowElevation = 2.dp,
            modifier = Modifier
              .heightIn(min = 40.dp)
              .clip(RoundedCornerShape(20.dp))
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
                tint = Color.Black,
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = if (selectedCategory == "All") "সকল ক্যাটাগরী" else selectedCategory,
                color = Color.Black,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color.Black,
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
            categories.filter { it.type != "sub" }.forEach { cat ->
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

        // 3B. Home Icon Button
        Surface(
          shape = CircleShape,
          color = Color(0xFFEF4444),
          border = BorderStroke(1.dp, Color.White),
          shadowElevation = 2.dp,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable { onHomeReset() }
            .testTag("home_reset_button")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = "হোম পেজ ও সব ফিল্টার রিসেট",
              tint = Color.Black,
              modifier = Modifier.size(19.dp)
            )
          }
        }

        // 3C. Shorter Compact Search Box + Voice + Image + Scan
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = CreamBackground,
          border = BorderStroke(1.dp, BorderGold),
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
              tint = AntiqueGoldDark,
              modifier = Modifier.size(16.dp)
            )

            Box(
              modifier = Modifier.weight(1f),
              contentAlignment = Alignment.CenterStart
            ) {
              if (searchQuery.isEmpty()) {
                Text(
                  text = "খুঁজুন...",
                  fontSize = 11.5.sp,
                  color = Color.Gray,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              BasicTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                singleLine = true,
                textStyle = TextStyle(
                  color = DarkEspresso,
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

            // Voice Search Icon Button
            Surface(
              shape = CircleShape,
              color = Color(0xFFFFEBEE),
              border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
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
                  tint = Color.Black,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            // Image Search Icon Button (Photo Picker from Device)
            Surface(
              shape = CircleShape,
              color = if (isImageSearchActive) Color(0xFFEF4444) else Color(0xFFFFEBEE),
              border = BorderStroke(1.dp, if (isImageSearchActive) Color.Black else Color(0xFFFFCDD2)),
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
                  tint = Color.Black,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            // Scan Button
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = Color(0xFFEF4444),
              border = BorderStroke(1.dp, Color.White),
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
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
                  tint = Color.Black,
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = "স্ক্যান",
                  color = Color.Black,
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Black,
                  maxLines = 1
                )
              }
            }
          }
        }
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

        categories.forEach { cat ->
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
