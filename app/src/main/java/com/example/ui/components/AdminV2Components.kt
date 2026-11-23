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
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Tune
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
  AdminTab.PAGE_CONFIG -> Icons.Default.Tune
  AdminTab.PRODUCTS -> Icons.Default.Inventory2
  AdminTab.ORDERS -> Icons.Default.LocalShipping
  AdminTab.SHIPPING_INFO -> Icons.Default.LocalShipping
  AdminTab.MARKETING -> Icons.Default.Campaign
  AdminTab.SMS_GATEWAY -> Icons.Default.Sms
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
  val overviewTabs = listOf(AdminTab.DASHBOARD, AdminTab.HOME_PAGE_SETTING, AdminTab.PAGE_CONFIG)
  val managementTabs = listOf(
    AdminTab.PRODUCTS,
    AdminTab.ORDERS,
    AdminTab.SHIPPING_INFO,
    AdminTab.MARKETING,
    AdminTab.SMS_GATEWAY,
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
          .size(40.dp)
          .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = AdminV2Cyan.copy(alpha = 0.35f))
          .clip(RoundedCornerShape(12.dp))
          .background(
            Brush.linearGradient(listOf(AdminV2Cyan, AdminV2CyanDeep))
          )
          .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
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
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary,
          letterSpacing = 0.6.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "অ্যাডমিন প্যানেল V2",
          fontSize = 10.5.sp,
          fontWeight = FontWeight.Bold,
          color = AdminV2CyanDeep
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))
    HorizontalDivider(color = AdminCardBorderSoft)
    Spacer(modifier = Modifier.height(12.dp))

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Text(
        text = "OVERVIEW",
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        color = AdminV2CyanDeep,
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
        color = AdminV2CyanDeep,
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

    HorizontalDivider(color = AdminCardBorderSoft)
    Spacer(modifier = Modifier.height(10.dp))

    // Admin Profile Pill + Logout on Bright White Surface
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = Color.White,
      border = BorderStroke(1.dp, AdminCardBorderWarm),
      shadowElevation = 2.dp,
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
            .size(34.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(AdminV2Cyan, AdminV2CyanDeep))),
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
            color = AdminInkPrimary,
            maxLines = 1
          )
          Text(
            text = "nihatfahimbd@gmail.com",
            fontSize = 9.5.sp,
            color = AdminInkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
        IconButton(
          onClick = onLogout,
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFEF2F2))
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "লগআউট",
            tint = AdminV2Rose,
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
      .then(
        if (selected) {
          Modifier.shadow(4.dp, shape, spotColor = AdminV2CyanDeep.copy(alpha = 0.25f))
        } else {
          Modifier
        }
      )
      .clip(shape)
      .then(
        if (selected) {
          Modifier
            .background(
              Brush.horizontalGradient(
                listOf(
                  AdminV2CyanDeep,
                  AdminV2Cyan
                )
              )
            )
            .border(1.dp, Color.White.copy(alpha = 0.55f), shape)
        } else {
          Modifier
            .background(Color.White.copy(alpha = 0.72f))
            .border(1.dp, AdminCardBorderSoft, shape)
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
      tint = if (selected) Color.White else AdminV2CyanDeep,
      modifier = Modifier.size(18.dp)
    )
    Text(
      text = tab.title,
      fontSize = 12.5.sp,
      fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
      color = if (selected) Color.White else AdminInkPrimary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f)
    )
    if (badgeCount != null) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = when {
          tab == AdminTab.ORDERS -> AdminV2Rose
          selected -> Color.White.copy(alpha = 0.24f)
          else -> AdminV2SkySoft
        }
      ) {
        Text(
          text = badgeCount.toString(),
          fontSize = 10.sp,
          fontWeight = FontWeight.ExtraBold,
          color = if (tab == AdminTab.ORDERS || selected) Color.White else AdminV2CyanDeep,
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
  onOpenQrScanner: () -> Unit = {},
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(adminRoyalGlossBrush())
      .padding(horizontal = 12.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        modifier = Modifier.weight(1f)
      ) {
        if (showMenuButton) {
          IconButton(
            onClick = onOpenDrawer,
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(13.dp))
              .background(Color.White.copy(alpha = 0.24f))
              .border(1.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(13.dp))
              .testTag("admin_drawer_toggle")
          ) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = "মেনু খুলুন",
              tint = Color.White,
              modifier = Modifier.size(21.dp)
            )
          }
        } else {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White.copy(alpha = 0.25f))
              .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "S", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "সাজগোজ অ্যাডমিন কন্ট্রোল",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "$productsCount পণ্য • $ordersCount অর্ডার • $activeBlocksCount ব্লক সক্রিয়",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.92f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(13.dp),
          color = Color.White,
          border = BorderStroke(1.dp, Color.White),
          shadowElevation = 2.dp,
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(13.dp))
            .clickable(onClick = onOpenQrScanner)
            .testTag("admin_topbar_qr_scanner_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.QrCodeScanner,
              contentDescription = "QR ও বারকোড স্ক্যানার খুলুন",
              tint = AdminV2CyanDeep,
              modifier = Modifier.size(17.dp)
            )
            Text(
              text = "QR",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AdminV2CyanDeep,
              maxLines = 1
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(13.dp),
          color = Color.White.copy(alpha = 0.24f),
          border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f)),
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(13.dp))
            .clickable(onClick = onLogout)
            .testTag("admin_logout_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PowerSettingsNew,
              contentDescription = "অ্যাডমিন থেকে লগআউট করুন",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "লগআউট",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              maxLines = 1
            )
          }
        }
      }
    }
  }
}
