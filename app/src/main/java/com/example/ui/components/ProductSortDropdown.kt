package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.example.ui.ProductSortOption
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@Composable
fun ProductSortDropdown(
  selectedOption: ProductSortOption,
  onOptionSelected: (ProductSortOption) -> Unit,
  modifier: Modifier = Modifier,
  testTagPrefix: String = "product_sort"
) {
  var expanded by remember { mutableStateOf(false) }
  val isCustomSort = selectedOption != ProductSortOption.DEFAULT

  Box(modifier = modifier) {
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = if (isCustomSort) RoyalMaroon else Color.White,
      border = BorderStroke(1.dp, if (isCustomSort) RoyalMaroon else BorderGold),
      shadowElevation = 2.dp,
      modifier = Modifier
        .minimumInteractiveComponentSize()
        .heightIn(min = 48.dp)
        .clip(RoundedCornerShape(14.dp))
        .clickable { expanded = true }
        .testTag("${testTagPrefix}_dropdown")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Sort,
          contentDescription = "Sort products",
          tint = if (isCustomSort) Color.White else RoyalMaroon,
          modifier = Modifier.size(18.dp)
        )
        Text(
          text = if (isCustomSort) selectedOption.label else "Sort by",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = if (isCustomSort) Color.White else DarkEspresso,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.widthIn(max = 170.dp)
        )
        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = null,
          tint = if (isCustomSort) Color.White else DarkEspresso,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      containerColor = Color.White,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .widthIn(min = 230.dp)
        .testTag("${testTagPrefix}_menu")
    ) {
      ProductSortOption.entries.forEachIndexed { index, option ->
        val isSelected = selectedOption == option
        val optionIcon = when (option) {
          ProductSortOption.DEFAULT -> Icons.Default.AutoAwesome
          ProductSortOption.PRICE_LOW_TO_HIGH -> Icons.Default.ArrowUpward
          ProductSortOption.PRICE_HIGH_TO_LOW -> Icons.Default.ArrowDownward
          ProductSortOption.NEWEST_ARRIVALS -> Icons.Default.NewReleases
        }

        DropdownMenuItem(
          text = {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) CreamSurface else Color.Transparent)
                .padding(horizontal = 6.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(
                  imageVector = optionIcon,
                  contentDescription = null,
                  tint = if (isSelected) RoyalMaroon else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
                Column {
                  Text(
                    text = option.label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) RoyalMaroon else DarkEspresso
                  )
                  Text(
                    text = option.subtitleBn,
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                  )
                }
              }

              if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = RoyalMaroon,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          },
          onClick = {
            onOptionSelected(option)
            expanded = false
          },
          modifier = Modifier
            .heightIn(min = 48.dp)
            .testTag("${testTagPrefix}_option_${option.name}")
        )

        if (index == 0) {
          HorizontalDivider(color = BorderGold.copy(alpha = 0.35f))
        }
      }
    }
  }
}
