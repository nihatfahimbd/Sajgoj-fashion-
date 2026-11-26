package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HomePageConfig
import com.example.data.model.Order
import com.example.data.model.StoreConfig
import com.example.firebase.AuthUserSummary
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex

enum class StorefrontBottomTab {
  HOME,
  SHOPPING,
  OFFERS,
  ACCOUNT,
  CART
}

@Composable
fun StorefrontBottomNavBar(
  activeTab: StorefrontBottomTab,
  cartItemCount: Int,
  onSelectHome: () -> Unit,
  onSelectShopping: () -> Unit,
  onSelectOffers: () -> Unit,
  onSelectAccount: () -> Unit,
  onSelectCart: () -> Unit,
  homePage: HomePageConfig = HomePageConfig(),
  modifier: Modifier = Modifier
) {
  if (!homePage.showBottomNav) return

  val navBg = parseColorHex(homePage.bottomNavBg, Color.White)
  val navBorder = parseColorHex(homePage.bottomNavBorderColor, Color(0xFFE5E7EB))
  val navBorderW = homePage.bottomNavBorderWidthPx.coerceIn(0, 4).dp
  val navRadius = homePage.bottomNavRadiusPx.coerceIn(0, 32).dp
  val activeCol = parseColorHex(homePage.bottomNavActiveColor, RoyalMaroon)
  val inactiveCol = parseColorHex(homePage.bottomNavInactiveColor, Color(0xFF4B5563))
  val pillBg = if (homePage.bottomNavActivePillBg.equals("transparent", ignoreCase = true)) {
    activeCol.copy(alpha = 0.12f)
  } else {
    parseColorHex(homePage.bottomNavActivePillBg, activeCol.copy(alpha = 0.12f))
  }
  val badgeBg = parseColorHex(homePage.bottomNavBadgeBg, RoyalMaroon)
  val badgeText = parseColorHex(homePage.bottomNavBadgeText, Color.White)

  val itemColors = NavigationBarItemDefaults.colors(
    selectedIconColor = activeCol,
    selectedTextColor = activeCol,
    indicatorColor = pillBg,
    unselectedIconColor = inactiveCol,
    unselectedTextColor = inactiveCol
  )

  val tabOrder = homePage.bottomNavOrder.ifEmpty { listOf("home", "shopping", "offers", "account", "cart") }

  Surface(
    color = navBg,
    shape = RoundedCornerShape(topStart = navRadius, topEnd = navRadius),
    border = if (navBorderW > 0.dp) BorderStroke(navBorderW, navBorder) else null,
    shadowElevation = 10.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      NavigationBar(
        containerColor = navBg,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars
      ) {
        tabOrder.forEach { key ->
          when (key) {
            "home" -> NavigationBarItem(
              selected = activeTab == StorefrontBottomTab.HOME,
              onClick = onSelectHome,
              colors = itemColors,
              alwaysShowLabel = homePage.bottomNavShowLabels,
              modifier = Modifier.testTag("bottom_nav_home"),
              icon = {
                Icon(
                  imageVector = if (activeTab == StorefrontBottomTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                  contentDescription = homePage.navHomeLabel.ifBlank { "হোম" }
                )
              },
              label = if (homePage.bottomNavShowLabels) {
                {
                  Text(
                    text = homePage.navHomeLabel.ifBlank { "হোম" },
                    fontSize = 11.sp,
                    fontWeight = if (activeTab == StorefrontBottomTab.HOME) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              } else null
            )

            "shopping" -> NavigationBarItem(
              selected = activeTab == StorefrontBottomTab.SHOPPING,
              onClick = onSelectShopping,
              colors = itemColors,
              alwaysShowLabel = homePage.bottomNavShowLabels,
              modifier = Modifier.testTag("bottom_nav_shopping"),
              icon = {
                Icon(
                  imageVector = if (activeTab == StorefrontBottomTab.SHOPPING) Icons.Filled.GridView else Icons.Outlined.GridView,
                  contentDescription = homePage.navShoppingLabel.ifBlank { "শপিং" }
                )
              },
              label = if (homePage.bottomNavShowLabels) {
                {
                  Text(
                    text = homePage.navShoppingLabel.ifBlank { "শপিং" },
                    fontSize = 11.sp,
                    fontWeight = if (activeTab == StorefrontBottomTab.SHOPPING) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              } else null
            )

            "offers" -> NavigationBarItem(
              selected = activeTab == StorefrontBottomTab.OFFERS,
              onClick = onSelectOffers,
              colors = itemColors,
              alwaysShowLabel = homePage.bottomNavShowLabels,
              modifier = Modifier.testTag("bottom_nav_offers"),
              icon = {
                Icon(
                  imageVector = if (activeTab == StorefrontBottomTab.OFFERS) Icons.Filled.CardGiftcard else Icons.Outlined.CardGiftcard,
                  contentDescription = homePage.navOffersLabel.ifBlank { "অফার" }
                )
              },
              label = if (homePage.bottomNavShowLabels) {
                {
                  Text(
                    text = homePage.navOffersLabel.ifBlank { "অফার" },
                    fontSize = 11.sp,
                    fontWeight = if (activeTab == StorefrontBottomTab.OFFERS) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              } else null
            )

            "account" -> NavigationBarItem(
              selected = activeTab == StorefrontBottomTab.ACCOUNT,
              onClick = onSelectAccount,
              colors = itemColors,
              alwaysShowLabel = homePage.bottomNavShowLabels,
              modifier = Modifier.testTag("bottom_nav_account"),
              icon = {
                Icon(
                  imageVector = if (activeTab == StorefrontBottomTab.ACCOUNT) Icons.Filled.Person else Icons.Outlined.Person,
                  contentDescription = homePage.navAccountLabel.ifBlank { "একাউন্ট" }
                )
              },
              label = if (homePage.bottomNavShowLabels) {
                {
                  Text(
                    text = homePage.navAccountLabel.ifBlank { "একাউন্ট" },
                    fontSize = 11.sp,
                    fontWeight = if (activeTab == StorefrontBottomTab.ACCOUNT) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              } else null
            )

            "cart" -> NavigationBarItem(
              selected = activeTab == StorefrontBottomTab.CART,
              onClick = onSelectCart,
              colors = itemColors,
              alwaysShowLabel = homePage.bottomNavShowLabels,
              modifier = Modifier.testTag("bottom_nav_cart"),
              icon = {
                BadgedBox(
                  badge = {
                    if (homePage.bottomNavShowCartBadge && cartItemCount > 0) {
                      Badge(
                        containerColor = badgeBg,
                        contentColor = badgeText
                      ) {
                        Text(
                          text = cartItemCount.toString(),
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }
                ) {
                  Icon(
                    imageVector = if (activeTab == StorefrontBottomTab.CART) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                    contentDescription = homePage.navCartLabel.ifBlank { "কার্ট" }
                  )
                }
              },
              label = if (homePage.bottomNavShowLabels) {
                {
                  val cartBase = homePage.navCartLabel.ifBlank { "কার্ট" }
                  Text(
                    text = if (homePage.bottomNavShowCartBadge && cartItemCount > 0) "$cartBase ($cartItemCount)" else cartBase,
                    fontSize = 11.sp,
                    fontWeight = if (activeTab == StorefrontBottomTab.CART) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              } else null
            )
          }
        }
      }
    }
  }
}

@Composable
fun CustomerAccountDialog(
  authUser: AuthUserSummary?,
  isAuthBusy: Boolean,
  orders: List<Order>,
  storeConfig: StoreConfig,
  unreadSupportCount: Int = 0,
  unreadNotificationCount: Int = 0,
  onGoogleAuthClick: () -> Unit,
  onOpenOrderInvoice: (Order) -> Unit,
  onOpenLiveSupport: () -> Unit = {},
  onOpenNotifications: () -> Unit = {},
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CreamBackground,
      border = BorderStroke(1.dp, BorderGold),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "👤 আমার একাউন্ট",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = RoyalMaroon
            )
            Text(
              text = "${storeConfig.storeName} কাস্টমার পোর্টাল",
              fontSize = 11.sp,
              color = AntiqueGoldDark
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "বন্ধ করুন",
              tint = RoyalMaroon
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = CreamSurface,
          border = BorderStroke(1.dp, BorderGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (authUser != null) {
              Text(
                text = authUser.displayName.ifBlank { "সাজগোজ গ্রাহক" },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
              if (authUser.email.isNotBlank()) {
                Text(
                  text = authUser.email,
                  fontSize = 12.sp,
                  color = Color.DarkGray
                )
              }
              OutlinedButton(
                onClick = onGoogleAuthClick,
                enabled = !isAuthBusy,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(if (isAuthBusy) "অপেক্ষা করুন..." else "লগআউট করুন", color = RoyalMaroon, fontWeight = FontWeight.Bold)
              }
            } else {
              Text(
                text = "সহজে অর্ডার ও ভাউচার সিঙ্ক করতে সাইন-ইন করুন",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
              )
              Button(
                onClick = onGoogleAuthClick,
                enabled = !isAuthBusy,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                if (isAuthBusy) {
                  CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                  )
                } else {
                  Text("Google দিয়ে সাইন-ইন করুন", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        CustomerOrderStatisticsCard(orders = orders)

        Text(
          text = "📦 সাম্প্রতিক অর্ডার (${orders.size})",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = RoyalMaroon
        )

        if (orders.isEmpty()) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "এখনো কোনো অর্ডার পাওয়া যায়নি। শপিং ট্যাব থেকে পছন্দের পণ্য অর্ডার করুন!",
              fontSize = 12.sp,
              color = Color.DarkGray,
              modifier = Modifier.padding(12.dp)
            )
          }
        } else {
          orders.take(5).forEach { order ->
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = Color.White,
              border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.6f)),
              onClick = {
                onDismiss()
                onOpenOrderInvoice(order)
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "#${order.id.takeLast(8)} • ${order.customerName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "৳${order.total} • ${order.status}",
                    fontSize = 11.sp,
                    color = RoyalMaroon,
                    fontWeight = FontWeight.SemiBold
                  )
                }
                Text(
                  text = "ইনভয়েস ›",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AntiqueGoldDark
                )
              }
            }
          }
        }

        OutlinedButton(
          onClick = {
            onDismiss()
            onOpenNotifications()
          },
          border = BorderStroke(1.dp, BorderGold),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("account_notifications_button")
        ) {
          Text(
            text = if (unreadNotificationCount > 0) {
              "🔔 অর্ডার নোটিফিকেশন ($unreadNotificationCount টি নতুন)"
            } else {
              "🔔 অর্ডার নোটিফিকেশন ইনবক্স"
            },
            color = RoyalMaroon,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Button(
          onClick = {
            onDismiss()
            onOpenLiveSupport()
          },
          colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("account_live_support_btn")
        ) {
          Text(
            text = if (unreadSupportCount > 0) {
              "💬 লাইভ সাপোর্ট চ্যাট ($unreadSupportCount টি নতুন উত্তর)"
            } else {
              "💬 অ্যাডমিনের সাথে লাইভ সাপোর্ট চ্যাট"
            },
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = CreamSurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Text(
              text = "কাস্টমার হেল্পলাইন",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
            Text(
              text = "কল: ${storeConfig.phone} | WhatsApp: ${storeConfig.whatsapp}",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
          }
        }
      }
    }
  }
}
