package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AdminTab
import java.util.Calendar

fun adminV2GreetingBn(): String {
  val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  return when {
    hour in 5..11 -> "শুভ সকাল, অ্যাডমিন"
    hour in 12..17 -> "শুভ অপরাহ্ন, অ্যাডমিন"
    else -> "শুভ সন্ধ্যা, অ্যাডমিন"
  }
}

private fun adminTabIcon(tab: AdminTab): ImageVector = when (tab) {
  AdminTab.DASHBOARD -> Icons.Default.Dashboard
  AdminTab.HOME_PAGE_SETTING -> Icons.Default.Home
  AdminTab.PRODUCTS -> Icons.Default.Inventory2
  AdminTab.ORDERS -> Icons.Default.LocalShipping
  AdminTab.MARKETING -> Icons.Default.Campaign
  AdminTab.SETTINGS -> Icons.Default.Settings
}

@Composable
fun AdminV2SidebarContent(
  currentTab: AdminTab,
  pendingOrdersCount: Int,
  productsCount: Int,
  onSelectTab: (AdminTab) -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val overviewTabs = listOf(AdminTab.DASHBOARD, AdminTab.HOME_PAGE_SETTING)
  val managementTabs = listOf(
    AdminTab.PRODUCTS,
    AdminTab.ORDERS,
    AdminTab.MARKETING,
    AdminTab.SETTINGS
  )

  Column(
    modifier = modifier
      .fillMaxHeight()
      .background(adminSidebarBrush())
      .padding(horizontal = 14.dp, vertical = 16.dp)
  ) {
    // Brand Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = AdminV2Cyan.copy(alpha = 0.4f))
          .clip(RoundedCornerShape(12.dp))
          .background(
            Brush.linearGradient(listOf(AdminV2Cyan, AdminV2CyanDeep))
          )
          .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "S",
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
      }
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "SAJGOJ FASHION",
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          letterSpacing = 0.6.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "অ্যাডমিন প্যানেল V2",
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF93C5FD)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))
    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
    Spacer(modifier = Modifier.height(12.dp))

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "OVERVIEW",
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF7DD3FC).copy(alpha = 0.78f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )

      overviewTabs.forEach { tab ->
        AdminV2SidebarNavItem(
          tab = tab,
          selected = currentTab == tab,
          badgeCount = null,
          onClick = { onSelectTab(tab) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "MANAGEMENT",
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF7DD3FC).copy(alpha = 0.78f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )

      managementTabs.forEach { tab ->
        val badge = when (tab) {
          AdminTab.ORDERS -> if (pendingOrdersCount > 0) pendingOrdersCount else null
          AdminTab.PRODUCTS -> productsCount
          else -> null
        }
        AdminV2SidebarNavItem(
          tab = tab,
          selected = currentTab == tab,
          badgeCount = badge,
          onClick = { onSelectTab(tab) }
        )
      }
    }

    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
    Spacer(modifier = Modifier.height(10.dp))

    // Admin Profile Pill + Logout
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = Color.White.copy(alpha = 0.08f),
      border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(AdminV2Cyan.copy(alpha = 0.28f))
            .border(1.dp, Color(0xFF7DD3FC).copy(alpha = 0.55f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "NF",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Store Owner",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
          )
          Text(
            text = "nihatfahimbd@gmail.com",
            fontSize = 9.5.sp,
            color = Color.White.copy(alpha = 0.72f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
        IconButton(
          onClick = onLogout,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "লগআউট",
            tint = Color(0xFFFCA5A5),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun AdminV2SidebarNavItem(
  tab: AdminTab,
  selected: Boolean,
  badgeCount: Int?,
  onClick: () -> Unit
) {
  val shape = RoundedCornerShape(14.dp)
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 48.dp)
      .clip(shape)
      .then(
        if (selected) {
          Modifier
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0xFF1E5B78),
                  Color(0xFF174A63)
                )
              )
            )
            .border(1.dp, Color(0xFF7DD3FC).copy(alpha = 0.45f), shape)
        } else {
          Modifier.background(Color.Transparent)
        }
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 10.dp)
      .testTag("admin_sidebar_${tab.name}"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Icon(
      imageVector = adminTabIcon(tab),
      contentDescription = tab.title,
      tint = if (selected) Color(0xFF7DD3FC) else Color.White.copy(alpha = 0.75f),
      modifier = Modifier.size(18.dp)
    )
    Text(
      text = tab.title,
      fontSize = 12.5.sp,
      fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
      color = if (selected) Color.White else Color.White.copy(alpha = 0.84f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f)
    )
    if (badgeCount != null) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (tab == AdminTab.ORDERS) Color(0xFFDC2626) else Color.White.copy(alpha = 0.16f)
      ) {
        Text(
          text = badgeCount.toString(),
          fontSize = 10.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }
  }
}

@Composable
fun AdminV2TopBar(
  productsCount: Int,
  ordersCount: Int,
  activeBlocksCount: Int,
  showMenuButton: Boolean,
  onOpenDrawer: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(adminRoyalGlossBrush())
      .padding(horizontal = 12.dp, vertical = 9.dp)
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
        if (showMenuButton) {
          IconButton(
            onClick = onOpenDrawer,
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White.copy(alpha = 0.14f))
              .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
              .testTag("admin_drawer_toggle")
          ) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = "মেনু খুলুন",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        } else {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Brush.linearGradient(listOf(AdminV2Cyan, AdminV2CyanDeep)))
              .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "S", fontSize = 17.sp, fontWeight = FontWeight.Black, color = Color.White)
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "সাজগোজ অ্যাডমিন কন্ট্রোল",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "$productsCount পণ্য • $ordersCount অর্ডার • $activeBlocksCount ব্লক সক্রিয়",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFBAE6FD),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
        modifier = Modifier
          .heightIn(min = 40.dp)
          .clip(RoundedCornerShape(12.dp))
          .clickable(onClick = onLogout)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PowerSettingsNew,
            contentDescription = "Logout",
            tint = Color.White,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "লগআউট",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}
