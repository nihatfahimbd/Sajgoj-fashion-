package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
  onCategorySelected: (String) -> Unit
) {
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
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .weight(1f)
          .clickable { /* Brand click */ }
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(CreamSurface),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "👑", fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = config.storeName,
            color = RoyalMaroon,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 0.5.sp
          )
          Text(
            text = config.tagline,
            color = AntiqueGoldDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
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
            .size(40.dp)
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

    // 3. Search Bar (Storefront only)
    if (currentScreen == AppScreen.STOREFRONT) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          placeholder = {
            Text(
              text = "শাড়ি বা জুয়েলারির নাম দিয়ে খুঁজুন...",
              fontSize = 13.sp,
              color = Color.Gray
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = AntiqueGoldDark,
              modifier = Modifier.size(20.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchChange("") }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Clear",
                  tint = Color.Gray,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CreamBackground,
            unfocusedContainerColor = CreamBackground,
            focusedBorderColor = RoyalMaroon,
            unfocusedBorderColor = BorderGold,
            cursorColor = RoyalMaroon
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("search_input")
        )
      }

      // 4. Horizontal Category Scroll Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 8.dp),
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
