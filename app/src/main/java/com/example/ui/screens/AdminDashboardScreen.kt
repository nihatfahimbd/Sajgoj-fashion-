package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ai.AiTaskState
import com.example.ai.SajgojAiService
import com.example.data.model.BannerItem
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.CourierConfig
import com.example.data.model.HomeSectionConfig
import com.example.data.model.InventorySyncEvent
import com.example.data.model.Order
import com.example.data.model.PixelConfig
import com.example.data.model.PosInventoryItem
import com.example.data.model.Product
import com.example.data.model.ReturnRecord
import com.example.data.model.SmsQueueItem
import com.example.data.model.StoreConfig
import com.example.data.model.SupportChatFeedState
import com.example.data.model.SupportChatMessage
import com.example.data.model.SupportChatThread
import com.example.data.model.SyncErrorLog
import com.example.data.model.SyncHealthState
import com.example.firebase.AuthUserSummary
import com.example.firebase.BOOTSTRAPPED_ADMIN_EMAIL
import com.example.sms.SimSmsGatewayManager
import com.example.sms.SmsHelper
import com.example.ui.AdminTab
import com.example.ui.components.AdminAmberGradEnd
import com.example.ui.components.AdminAmberGradStart
import com.example.ui.components.AdminCardBorderSoft
import com.example.ui.components.AdminCardBorderWarm
import com.example.ui.components.AdminCategoriesTab
import com.example.ui.components.AdminDeliveryTrackingTab
import com.example.ui.components.AdminEmeraldGradEnd
import com.example.ui.components.AdminEmeraldGradStart
import com.example.ui.components.AdminHeroGlassChip
import com.example.ui.components.AdminInkMuted
import com.example.ui.components.AdminInkPrimary
import com.example.ui.components.AdminInkSecondary
import com.example.ui.components.AdminNavyGradStart
import com.example.ui.components.AdminProductsTab
import com.example.ui.components.AdminRoyalGradEnd
import com.example.ui.components.AdminRoyalGradStart
import com.example.ui.components.CategoryFormDialog
import com.example.ui.components.ProductFormDialog
import com.example.ui.components.AdminV2Amber
import com.example.ui.components.AdminV2AmberSoft
import com.example.ui.components.AdminV2BentoStatCard
import com.example.ui.components.AdminV2Cyan
import com.example.ui.components.AdminV2CyanDeep
import com.example.ui.components.AdminV2Emerald
import com.example.ui.components.AdminV2EmeraldSoft
import com.example.ui.components.AdminV2FulfillmentRingCard
import com.example.ui.components.AdminV2IceSurface
import com.example.ui.components.AdminV2Rose
import com.example.ui.components.AdminV2SidebarContent
import com.example.ui.components.AdminV2SkySoft
import com.example.ui.components.AdminV2TopBar
import com.example.ui.components.AdminVibrantHeroCard
import com.example.ui.components.AdminQrScannerDialog
import com.example.ui.components.AdminSellerOrdersHub
import com.example.ui.components.AdminShippingInfoHub
import com.example.ui.components.AdminSmsGatewayHub
import com.example.ui.components.AdminSupportChatHub
import com.example.ui.components.AppImage
import com.example.ui.components.PageConfigurationHub
import com.example.ui.components.DailyRevenueTrendChartCard
import com.example.ui.components.GlossyAdminCard
import com.example.ui.components.GlossyGradientButton
import com.example.ui.components.GlossySegmentPill
import com.example.ui.components.HomePageSettingHub
import com.example.ui.components.InventorySyncPanel
import com.example.ui.components.PaymentMethodsToggleSection
import com.example.ui.components.ProductVideoFormSection
import com.example.ui.components.ReturnExchangeDialog
import com.example.ui.components.ReturnSlipDialog
import com.example.ui.components.ShippingLabelPrintFormat
import com.example.ui.components.StorefrontDesignStudioPanel
import com.example.ui.components.adminAccentGlossBrush
import com.example.ui.components.adminCanvasBrush
import com.example.ui.components.adminEmeraldTealBrush
import com.example.ui.components.adminIndigoSkyBrush
import com.example.ui.components.adminOutlinedTextFieldColors
import com.example.ui.components.adminRoyalGlossBrush
import com.example.ui.components.adminV2GreetingBn
import com.example.ui.components.adminVioletCyanBrush
import com.example.ui.components.adminWarmCoralAmberBrush
import com.example.ui.components.PageConfigSwitchRow
import com.example.ui.components.printShippingLabels
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.util.ProductMediaHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  currentTab: AdminTab,
  onSelectTab: (AdminTab) -> Unit,
  isAdminLoggedIn: Boolean,
  onLogin: (String) -> Boolean,
  onLogout: () -> Unit,
  products: List<Product>,
  categories: List<Category>,
  orders: List<Order>,
  returns: List<ReturnRecord> = emptyList(),
  coupons: List<Coupon>,
  banners: List<BannerItem>,
  storeConfig: StoreConfig,
  courierConfig: CourierConfig,
  pixelConfig: PixelConfig,
  onAddProduct: (Product) -> Unit,
  onUpdateProduct: (Product) -> Unit,
  onDeleteProduct: (String) -> Unit,
  onUpdateOrderStatus: (String, String) -> Unit,
  onDeleteOrder: (String) -> Unit = {},
  onDispatchCourier: (String, String) -> Unit,
  onUpdateDeliveredBy: (String, String) -> Unit = { _, _ -> },
  onViewInvoice: (Order) -> Unit,
  onOpenShippingLabel: (Order) -> Unit = {},
  onAddReturnRecord: (ReturnRecord) -> Unit = {},
  onAddCategory: (Category) -> Unit,
  onDeleteCategory: (String) -> Unit,
  onAddCoupon: (Coupon) -> Unit,
  onToggleCoupon: (String) -> Unit,
  onDeleteCoupon: (String) -> Unit,
  onAddBanner: (BannerItem) -> Unit,
  onUpdateBanner: (BannerItem) -> Unit = {},
  onToggleBanner: (String) -> Unit,
  onToggleBannerOverlay: (String) -> Unit = {},
  onDeleteBanner: (String) -> Unit,
  onUpdateStoreConfig: (StoreConfig) -> Unit,
  onUpdateCourierConfig: (CourierConfig) -> Unit,
  onUpdatePixelConfig: (PixelConfig) -> Unit,
  onResetDefaults: () -> Unit,
  onShowToast: (String) -> Unit,
  posInventory: Map<String, PosInventoryItem> = emptyMap(),
  syncEvents: List<InventorySyncEvent> = emptyList(),
  syncHealth: SyncHealthState = SyncHealthState(),
  syncErrorLogs: List<SyncErrorLog> = emptyList(),
  onRecordPosSale: (Product, Int, String, String) -> Unit = { _, _, _, _ -> },
  onReconcileInventory: () -> Unit = {},
  onClearSyncErrors: () -> Unit = {},
  homeSections: List<HomeSectionConfig> = emptyList(),
  onToggleHomeSection: (String) -> Unit = {},
  onMoveHomeSection: (String, Int) -> Unit = { _, _ -> },
  onUpdateHomeSection: (String, (HomeSectionConfig) -> HomeSectionConfig) -> Unit = { _, _ -> },
  onToggleProductInHomeSection: (String, String) -> Unit = { _, _ -> },
  onAddCustomHomeSection: (HomeSectionConfig) -> Unit = {},
  onDeleteHomeSection: (String) -> Unit = {},
  onResetHomeSectionsToDefault: () -> Unit = {},
  authUser: AuthUserSummary? = null,
  isAuthBusy: Boolean = false,
  onGoogleSignInAdmin: () -> Unit = {},
  onGoogleSignOut: () -> Unit = {},
  aiInsightsState: AiTaskState<String> = AiTaskState.Idle,
  onGenerateAiInsights: () -> Unit = {},
  smsQueue: List<SmsQueueItem> = emptyList(),
  isProcessingSmsQueue: Boolean = false,
  smsGatewayLastLog: String = "স্ট্যান্ডবাই",
  onEnqueueSingleSms: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
  onEnqueueBulkSms: (List<Pair<String, String>>, String, String) -> Unit = { _, _, _ -> },
  onProcessPendingSmsNow: () -> Unit = {},
  onRetrySmsItem: (String) -> Unit = {},
  onMarkSmsSentViaIntent: (String) -> Unit = {},
  onDeleteSmsItem: (String) -> Unit = {},
  onClearCompletedSms: () -> Unit = {},
  unreadSupportCount: Int = 0,
  supportThreadsState: SupportChatFeedState<List<SupportChatThread>> = SupportChatFeedState.Idle,
  selectedSupportChatId: String? = null,
  supportMessagesState: SupportChatFeedState<List<SupportChatMessage>> = SupportChatFeedState.Idle,
  isSendingSupportReply: Boolean = false,
  onSelectSupportThread: (String?) -> Unit = {},
  onSendSupportReply: (String, String) -> Unit = { _, _ -> },
  onToggleSupportThreadStatus: (String, String) -> Unit = { _, _ -> },
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Admin login dialog state if not logged in
  var loginPasswordInput by remember { mutableStateOf("admin123") }

  // Modals for CRUD & Returns
  var showProductDialog by remember { mutableStateOf(false) }
  var editingProduct by remember { mutableStateOf<Product?>(null) }
  var showCategoryDialog by remember { mutableStateOf(false) }
  var editingCategory by remember { mutableStateOf<Category?>(null) }
  var showCouponDialog by remember { mutableStateOf(false) }
  var showBannerDialog by remember { mutableStateOf(false) }
  var editingBanner by remember { mutableStateOf<BannerItem?>(null) }
  var showReturnDialog by remember { mutableStateOf(false) }
  var returnTargetOrder by remember { mutableStateOf<Order?>(null) }
  var viewingReturnSlip by remember { mutableStateOf<ReturnRecord?>(null) }
  var showQrScannerDialog by remember { mutableStateOf(false) }

  if (!isAdminLoggedIn) {
    // V2 Frosted Ocean & Ice-White Admin Login Screen
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(adminCanvasBrush())
        .padding(20.dp),
      contentAlignment = Alignment.Center
    ) {
      GlossyAdminCard(
        modifier = Modifier
          .width(420.dp)
          .padding(horizontal = 8.dp),
        cornerRadius = 24.dp,
        accentBorderColor = AdminCardBorderWarm,
        elevation = 10.dp,
        contentPadding = 24.dp
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(66.dp)
              .shadow(8.dp, CircleShape, spotColor = AdminV2CyanDeep.copy(alpha = 0.32f))
              .clip(CircleShape)
              .background(adminRoyalGlossBrush())
              .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Admin Security",
              tint = Color.White,
              modifier = Modifier.size(30.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "অ্যাডমিন প্যানেলে প্রবেশ",
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            color = AdminInkPrimary
          )

          Spacer(modifier = Modifier.height(6.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = AdminV2IceSurface,
            border = BorderStroke(1.dp, AdminCardBorderWarm)
          ) {
            Text(
              text = "ইমেইল: nihatfahimbd@gmail.com",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = AdminInkSecondary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          OutlinedTextField(
            value = loginPasswordInput,
            onValueChange = { loginPasswordInput = it },
            label = { Text("অ্যাডমিন পাসওয়ার্ড", fontSize = 12.sp) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_password_input")
          )

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = { onLogin(loginPasswordInput) },
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("admin_login_submit")
          ) {
            Text("লগইন করুন", fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold)
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedButton(
            onClick = onGoogleSignInAdmin,
            enabled = !isAuthBusy,
            border = BorderStroke(1.dp, AdminV2CyanDeep),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 48.dp)
              .testTag("admin_google_signin_button")
          ) {
            if (isAuthBusy) {
              CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = AdminV2CyanDeep)
              Spacer(modifier = Modifier.width(8.dp))
              Text("সাইন-ইন হচ্ছে...", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
            } else {
              Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(17.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Google দিয়ে অ্যাডমিন সাইন-ইন ($BOOTSTRAPPED_ADMIN_EMAIL)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "ডিফল্ট পাসওয়ার্ড: admin123",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkMuted
          )
        }
      }
    }
    return
  }

  val pendingCountForNav = remember(orders) {
    orders.count { it.status.equals("Pending", ignoreCase = true) }
  }
  val activeBlocksCount = remember(homeSections) {
    homeSections.count { it.enabled }
  }
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  // Responsive V2 Shell: Persistent Left Sidebar on Wide Screens, ModalNavigationDrawer + 1-tap Segment Strip on Compact
  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(adminCanvasBrush())
  ) {
    val isWideLayout = maxWidth >= 600.dp

    val workspaceContent: @Composable () -> Unit = {
      Column(modifier = Modifier.fillMaxSize()) {
        AdminV2TopBar(
          productsCount = products.size,
          ordersCount = orders.size,
          activeBlocksCount = activeBlocksCount,
          showMenuButton = !isWideLayout,
          onOpenDrawer = { scope.launch { drawerState.open() } },
          onOpenQrScanner = { showQrScannerDialog = true },
          onLogout = onLogout
        )

        if (!isWideLayout) {
          // Frosted Ice Sub Navigation Strip for 1-tap mobile switching
          Surface(
            color = Color.White.copy(alpha = 0.88f),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              AdminTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val labelText = if (tab == AdminTab.SUPPORT_CHAT && unreadSupportCount > 0) {
                  "${tab.icon} ${tab.title} ($unreadSupportCount)"
                } else {
                  "${tab.icon} ${tab.title}"
                }
                GlossySegmentPill(
                  label = labelText,
                  selected = isSelected,
                  onClick = { onSelectTab(tab) },
                  modifier = Modifier.testTag("admin_tab_${tab.name}")
                )
              }
            }
          }
          HorizontalDivider(color = AdminCardBorderWarm)
        }

        // Main Workspace Content
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
        ) {
      when (currentTab) {
        AdminTab.DASHBOARD -> {
          AdminOverviewTab(
            products = products,
            orders = orders,
            categories = categories,
            returnsCount = returns.size,
            authUser = authUser,
            isAuthBusy = isAuthBusy,
            onGoogleSignInAdmin = onGoogleSignInAdmin,
            onGoogleSignOut = onGoogleSignOut,
            aiInsightsState = aiInsightsState,
            onGenerateAiInsights = onGenerateAiInsights,
            onGoToTab = onSelectTab,
            onAddProductClick = {
              editingProduct = null
              showProductDialog = true
            },
            onOpenNewReturn = {
              returnTargetOrder = null
              showReturnDialog = true
            },
            onOpenQrScanner = { showQrScannerDialog = true }
          )
        }

        AdminTab.HOME_PAGE_SETTING -> {
          HomePageSettingHub(
            homeSections = homeSections,
            products = products,
            categories = categories,
            banners = banners,
            storeConfig = storeConfig,
            onToggleSection = onToggleHomeSection,
            onMoveSection = onMoveHomeSection,
            onUpdateSection = onUpdateHomeSection,
            onToggleProductInSection = onToggleProductInHomeSection,
            onAddCustomSection = onAddCustomHomeSection,
            onDeleteSection = onDeleteHomeSection,
            onResetSectionsToDefault = onResetHomeSectionsToDefault,
            onAddBannerClick = {
              editingBanner = null
              showBannerDialog = true
            },
            onEditBannerClick = { b ->
              editingBanner = b
              showBannerDialog = true
            },
            onToggleBanner = onToggleBanner,
            onToggleBannerOverlay = onToggleBannerOverlay,
            onDeleteBanner = onDeleteBanner,
            onSaveStoreConfig = onUpdateStoreConfig
          )
        }

        AdminTab.PAGE_CONFIG -> {
          PageConfigurationHub(
            storeConfig = storeConfig,
            products = products,
            onSaveStoreConfig = onUpdateStoreConfig,
            onShowToast = onShowToast,
            homeSections = homeSections,
            categories = categories,
            onToggleSection = onToggleHomeSection,
            onMoveSection = onMoveHomeSection
          )
        }

        AdminTab.PRODUCTS -> {
          AdminProductsHub(
            products = products,
            categories = categories,
            orders = orders,
            returns = returns,
            posInventory = posInventory,
            syncEvents = syncEvents,
            syncHealth = syncHealth,
            syncErrorLogs = syncErrorLogs,
            onRecordPosSale = onRecordPosSale,
            onReconcileInventory = onReconcileInventory,
            onClearSyncErrors = onClearSyncErrors,
            onAddClick = {
              editingProduct = null
              showProductDialog = true
            },
            onEditClick = { product ->
              editingProduct = product
              showProductDialog = true
            },
            onDeleteClick = onDeleteProduct,
            onAddCategory = {
              editingCategory = null
              showCategoryDialog = true
            },
            onEditCategory = { cat ->
              editingCategory = cat
              showCategoryDialog = true
            },
            onToggleCategorySlider = { updated -> onAddCategory(updated) },
            onDeleteCategory = onDeleteCategory
          )
        }

        AdminTab.ORDERS -> {
          AdminSellerOrdersHub(
            orders = orders,
            products = products,
            returns = returns,
            storeConfig = storeConfig,
            courierConfig = courierConfig,
            onUpdateStatus = onUpdateOrderStatus,
            onDispatchCourier = onDispatchCourier,
            onUpdateDeliveredBy = onUpdateDeliveredBy,
            onViewInvoice = onViewInvoice,
            onOpenShippingLabel = onOpenShippingLabel,
            onOpenReturn = { order ->
              returnTargetOrder = order
              showReturnDialog = true
            },
            onViewReturnSlip = { record ->
              viewingReturnSlip = record
            },
            onUpdateCourierConfig = onUpdateCourierConfig,
            onOpenQrScanner = { showQrScannerDialog = true },
            onShowToast = onShowToast,
            onEnqueueOrderSms = { order ->
              val msg = SmsHelper.buildOrderStatusSms(order, storeConfig.storeName)
              onEnqueueSingleSms(order.phone, order.customerName, msg, order.id, "order_alert")
            },
            onDeleteOrder = onDeleteOrder
          )
        }

        AdminTab.SUPPORT_CHAT -> {
          AdminSupportChatHub(
            authUser = authUser,
            isAuthBusy = isAuthBusy,
            threadsState = supportThreadsState,
            selectedChatId = selectedSupportChatId,
            messagesState = supportMessagesState,
            isSendingReply = isSendingSupportReply,
            onGoogleSignInAdmin = onGoogleSignInAdmin,
            onSelectThread = onSelectSupportThread,
            onSendReply = onSendSupportReply,
            onToggleThreadStatus = onToggleSupportThreadStatus
          )
        }

        AdminTab.SHIPPING_INFO -> {
          AdminShippingInfoHub(
            storeConfig = storeConfig,
            onSaveStoreConfig = onUpdateStoreConfig,
            onShowToast = onShowToast
          )
        }

        AdminTab.MARKETING -> {
          AdminMarketingHub(
            coupons = coupons,
            banners = banners,
            storeConfig = storeConfig,
            products = products,
            onUpdateStoreConfig = onUpdateStoreConfig,
            onShowToast = onShowToast,
            onAddCouponClick = { showCouponDialog = true },
            onToggleCoupon = onToggleCoupon,
            onDeleteCoupon = onDeleteCoupon,
            onAddBannerClick = {
              editingBanner = null
              showBannerDialog = true
            },
            onEditBannerClick = { b ->
              editingBanner = b
              showBannerDialog = true
            },
            onToggleBanner = onToggleBanner,
            onToggleBannerOverlay = onToggleBannerOverlay,
            onDeleteBanner = onDeleteBanner
          )
        }

        AdminTab.SMS_GATEWAY -> {
          AdminSmsGatewayHub(
            storeConfig = storeConfig,
            orders = orders,
            smsQueue = smsQueue,
            isProcessingSmsQueue = isProcessingSmsQueue,
            smsGatewayLastLog = smsGatewayLastLog,
            onUpdateStoreConfig = onUpdateStoreConfig,
            onEnqueueSingleSms = onEnqueueSingleSms,
            onEnqueueBulkSms = onEnqueueBulkSms,
            onProcessPendingSmsNow = onProcessPendingSmsNow,
            onRetrySmsItem = onRetrySmsItem,
            onMarkSmsSentViaIntent = onMarkSmsSentViaIntent,
            onDeleteSmsItem = onDeleteSmsItem,
            onClearCompletedSms = onClearCompletedSms,
            onShowToast = onShowToast
          )
        }

        AdminTab.SETTINGS -> {
          AdminSettingsHub(
            config = storeConfig,
            pixelConfig = pixelConfig,
            productsCount = products.size,
            ordersCount = orders.size,
            onSaveConfig = onUpdateStoreConfig,
            onSavePixel = onUpdatePixelConfig,
            onResetDefaults = onResetDefaults,
            onShowToast = onShowToast
          )
        }
      }
    }
    }
    }

    if (isWideLayout) {
      Row(modifier = Modifier.fillMaxSize()) {
        AdminV2SidebarContent(
          currentTab = currentTab,
          pendingOrdersCount = pendingCountForNav,
          productsCount = products.size,
          unreadSupportCount = unreadSupportCount,
          onSelectTab = onSelectTab,
          onLogout = onLogout,
          modifier = Modifier
            .width(236.dp)
            .fillMaxHeight()
        )
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
          workspaceContent()
        }
      }
    } else {
      ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
          ModalDrawerSheet(
            drawerContainerColor = Color.Transparent,
            modifier = Modifier.width(268.dp)
          ) {
            AdminV2SidebarContent(
              currentTab = currentTab,
              pendingOrdersCount = pendingCountForNav,
              productsCount = products.size,
              unreadSupportCount = unreadSupportCount,
              onSelectTab = { tab ->
                onSelectTab(tab)
                scope.launch { drawerState.close() }
              },
              onLogout = {
                scope.launch { drawerState.close() }
                onLogout()
              },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      ) {
        workspaceContent()
      }
    }
  }

  // Product Add / Edit Dialog
  if (showProductDialog) {
    ProductFormDialog(
      product = editingProduct,
      categories = categories,
      storeConfig = storeConfig,
      onDismiss = {
        showProductDialog = false
        editingProduct = null
      },
      onSave = { p ->
        if (editingProduct != null) {
          onUpdateProduct(p)
        } else {
          onAddProduct(p)
        }
        showProductDialog = false
        editingProduct = null
      }
    )
  }

  // Category Add / Edit Dialog
  if (showCategoryDialog) {
    CategoryFormDialog(
      category = editingCategory,
      categories = categories,
      onDismiss = {
        showCategoryDialog = false
        editingCategory = null
      },
      onSave = { c ->
        onAddCategory(c)
        showCategoryDialog = false
        editingCategory = null
      }
    )
  }

  // Coupon Add Dialog
  if (showCouponDialog) {
    CouponFormDialog(
      onDismiss = { showCouponDialog = false },
      onSave = { cp ->
        onAddCoupon(cp)
        showCouponDialog = false
      }
    )
  }

  // Banner Add / Edit Dialog
  if (showBannerDialog) {
    BannerFormDialog(
      initialBanner = editingBanner,
      onDismiss = {
        showBannerDialog = false
        editingBanner = null
      },
      onSave = { b ->
        if (editingBanner != null) {
          onUpdateBanner(b)
        } else {
          onAddBanner(b)
        }
        showBannerDialog = false
        editingBanner = null
      }
    )
  }

  // Return & Exchange Dialog
  if (showReturnDialog) {
    ReturnExchangeDialog(
      orders = orders,
      products = products,
      initialOrder = returnTargetOrder,
      onDismiss = { showReturnDialog = false },
      onConfirm = { record ->
        showReturnDialog = false
        onAddReturnRecord(record)
        viewingReturnSlip = record
        onShowToast("রিটার্ন ও এক্সচেঞ্জ মেমো #${record.id} সম্পন্ন হয়েছে! 📄")
      }
    )
  }

  // Return Slip Memo Dialog
  if (viewingReturnSlip != null) {
    ReturnSlipDialog(
      record = viewingReturnSlip!!,
      onDismiss = { viewingReturnSlip = null },
      onShowToast = onShowToast
    )
  }

  // Dedicated Admin QR & Barcode Lookup Scanner Dialog
  if (showQrScannerDialog) {
    AdminQrScannerDialog(
      products = products,
      orders = orders,
      posInventory = posInventory,
      onDismiss = { showQrScannerDialog = false },
      onEditProduct = { product ->
        showQrScannerDialog = false
        editingProduct = product
        showProductDialog = true
      },
      onViewInvoice = { order ->
        showQrScannerDialog = false
        onViewInvoice(order)
      },
      onOpenShippingLabel = { order ->
        showQrScannerDialog = false
        onOpenShippingLabel(order)
      },
      onUpdateOrderStatus = onUpdateOrderStatus,
      onShowToast = onShowToast
    )
  }
}

// ----------------- SUB-TABS -----------------

@Composable
fun AdminOverviewTab(
  products: List<Product>,
  orders: List<Order>,
  categories: List<Category>,
  returnsCount: Int = 0,
  authUser: AuthUserSummary? = null,
  isAuthBusy: Boolean = false,
  onGoogleSignInAdmin: () -> Unit = {},
  onGoogleSignOut: () -> Unit = {},
  aiInsightsState: AiTaskState<String> = AiTaskState.Idle,
  onGenerateAiInsights: () -> Unit = {},
  onGoToTab: (AdminTab) -> Unit,
  onAddProductClick: () -> Unit,
  onOpenNewReturn: () -> Unit = {},
  onOpenQrScanner: () -> Unit = {}
) {
  val totalRevenue = orders.sumOf { it.total }
  val pendingOrders = orders.filter { it.status.equals("Pending", ignoreCase = true) }
  val deliveredOrders = orders.filter { it.status.equals("Delivered", ignoreCase = true) }
  val lowStockProducts = products.filter { it.stock <= 5 }
  val totalStockUnits = products.sumOf { it.stock }
  val totalStockValuation = products.sumOf { it.price * it.stock }

  val deliveredRatio = if (orders.isNotEmpty()) {
    deliveredOrders.size.toFloat() / orders.size.toFloat()
  } else {
    0f
  }
  val deliveredPercent = (deliveredRatio * 100f).toInt()
  val greeting = remember { adminV2GreetingBn() }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val isWideOverview = maxWidth >= 600.dp

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // V2 Greeting Header + Deep Ocean "+ নতুন পণ্য" Pill CTA
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = greeting,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = AdminInkPrimary
          )
          Text(
            text = "সাজগোজ ফ্যাশন • লাইভ সেলস ও অপারেশন ট্র্যাকিং",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = AdminInkMuted
          )
        }
        Button(
          onClick = onAddProductClick,
          colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
          shape = RoundedCornerShape(50),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
          modifier = Modifier.heightIn(min = 48.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text(text = "নতুন পণ্য", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
      }

      // Dedicated Quick QR & Barcode Lookup Strip
      GlossyAdminCard(
        modifier = Modifier
          .clickable(onClick = onOpenQrScanner)
          .testTag("admin_overview_qr_scanner_button"),
        cornerRadius = 18.dp,
        accentBorderColor = AdminV2Cyan.copy(alpha = 0.65f),
        contentPadding = 12.dp
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
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(adminRoyalGlossBrush()),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "QR ও বারকোড স্ক্যানার",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "কুইক QR ও বারকোড স্ক্যানার",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                color = AdminInkPrimary
              )
              Text(
                text = "ক্যামেরা দিয়ে পণ্যের SKU বা পার্সেল ট্র্যাকিং QR মুহূর্তেই দেখুন",
                fontSize = 11.sp,
                color = AdminInkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = AdminV2SkySoft,
            border = BorderStroke(1.dp, AdminCardBorderWarm)
          ) {
            Text(
              text = "স্ক্যান করুন",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AdminV2CyanDeep,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      // 2x2 Minimal Bento KPI Cards
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AdminV2BentoStatCard(
          title = "মোট বিক্রয়",
          value = "৳$totalRevenue",
          trendText = "↑ লাইভ হিসাব • ${deliveredOrders.size} ডেলিভার্ড",
          trendColor = AdminV2Emerald,
          icon = Icons.Default.AttachMoney,
          iconTint = AdminV2CyanDeep,
          iconBg = AdminV2SkySoft,
          accentBorderColor = AdminCardBorderWarm,
          modifier = Modifier.weight(1f)
        )

        AdminV2BentoStatCard(
          title = "মোট অর্ডার",
          value = "${orders.size} টি",
          trendText = "${pendingOrders.size} টি পেন্ডিং অনুমোদন",
          trendColor = if (pendingOrders.isNotEmpty()) AdminV2Amber else AdminV2Emerald,
          icon = Icons.Default.ShoppingCart,
          iconTint = AdminV2Amber,
          iconBg = AdminV2AmberSoft,
          accentBorderColor = AdminCardBorderWarm,
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AdminV2BentoStatCard(
          title = "ইনভেন্টরি স্টক",
          value = "$totalStockUnits পিস",
          trendText = "মূল্য: ৳$totalStockValuation",
          trendColor = AdminV2Emerald,
          icon = Icons.Default.Inventory2,
          iconTint = AdminV2Emerald,
          iconBg = AdminV2EmeraldSoft,
          accentBorderColor = AdminCardBorderWarm,
          modifier = Modifier.weight(1f)
        )

        AdminV2BentoStatCard(
          title = "কম স্টক সতর্কতা",
          value = "${lowStockProducts.size} টি",
          trendText = if (lowStockProducts.isNotEmpty()) "রিস্টক প্রয়োজন • $returnsCount রিটার্ন" else "স্টক পর্যাপ্ত • $returnsCount রিটার্ন",
          trendColor = if (lowStockProducts.isNotEmpty()) AdminV2Rose else AdminV2Emerald,
          icon = Icons.Default.Warning,
          iconTint = if (lowStockProducts.isNotEmpty()) AdminV2Rose else AdminV2CyanDeep,
          iconBg = if (lowStockProducts.isNotEmpty()) Color(0xFFFEE2E2) else AdminV2SkySoft,
          accentBorderColor = if (lowStockProducts.isNotEmpty()) Color(0xFFFCA5A5) else AdminCardBorderWarm,
          modifier = Modifier.weight(1f)
        )
      }

      // 7-Day Smooth Sky-Cyan Area Chart + Circular Fulfillment Ring Card
      if (isWideOverview) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          Box(modifier = Modifier.weight(1.35f)) {
            DailyRevenueTrendChartCard(orders = orders)
          }
          Box(modifier = Modifier.weight(1f)) {
            AdminV2FulfillmentRingCard(
              title = "ডেলিভারি লক্ষ্যমাত্রা",
              subtitle = "সম্পন্ন অর্ডার অনুপাত",
              progressRatio = deliveredRatio,
              centerText = "$deliveredPercent%",
              centerSubText = "সম্পন্ন",
              leftLabel = "পেন্ডিং",
              leftValue = "${pendingOrders.size} টি",
              rightLabel = "ডেলিভার্ড",
              rightValue = "${deliveredOrders.size} টি"
            )
          }
        }
      } else {
        DailyRevenueTrendChartCard(orders = orders)
        AdminV2FulfillmentRingCard(
          title = "ডেলিভারি লক্ষ্যমাত্রা",
          subtitle = "সম্পন্ন অর্ডার অনুপাত",
          progressRatio = deliveredRatio,
          centerText = "$deliveredPercent%",
          centerSubText = "সম্পন্ন",
          leftLabel = "পেন্ডিং",
          leftValue = "${pendingOrders.size} টি",
          rightLabel = "ডেলিভার্ড",
          rightValue = "${deliveredOrders.size} টি"
        )
      }

      // Cloud Sync Google Account Status + Admin AI Insights Card
      GlossyAdminCard(
        modifier = Modifier.testTag("admin_cloud_auth_status_card"),
        cornerRadius = 16.dp,
        accentBorderColor = if (authUser?.isAdmin == true) Color(0xFFA7F3D0) else AdminCardBorderWarm,
        contentPadding = 12.dp
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
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = if (authUser?.isAdmin == true) AdminV2Emerald else AdminInkMuted,
              modifier = Modifier.size(18.dp)
            )
            Column {
              Text(
                text = when {
                  authUser?.isAdmin == true -> "ক্লাউড অ্যাডমিন সিঙ্ক সক্রিয়"
                  authUser != null -> "সাইন-ইন: ${authUser.email}"
                  else -> "অ্যাডমিন ক্লাউড সিঙ্ক (Google সাইন-ইন)"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AdminInkPrimary
              )
              Text(
                text = if (authUser != null) authUser.email else "অর্ডার ও POS ক্লাউডে সেভ করতে Google দিয়ে সাইন-ইন করুন",
                fontSize = 10.5.sp,
                color = AdminInkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
          OutlinedButton(
            onClick = { if (authUser != null) onGoogleSignOut() else onGoogleSignInAdmin() },
            enabled = !isAuthBusy,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.heightIn(min = 36.dp)
          ) {
            Text(
              text = if (authUser != null) "সাইন-আউট" else "Google লগইন",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AdminV2CyanDeep
            )
          }
        }
      }

      GlossyAdminCard(
        modifier = Modifier.testTag("admin_ai_insights_card"),
        cornerRadius = 18.dp,
        accentBorderColor = AdminV2Cyan.copy(alpha = 0.55f),
        contentPadding = 14.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(adminRoyalGlossBrush()),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = "AI Insights",
                  tint = Color.White,
                  modifier = Modifier.size(19.dp)
                )
              }
              Column {
                Text(
                  text = "Gemini AI স্টোর ইনসাইট ও প্রোমো ক্যাপশন",
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Black,
                  color = AdminInkPrimary
                )
                Text(
                  text = "বিক্রয়, লো-স্টক ও সোশ্যাল মিডিয়া মার্কেটিং পরামর্শ",
                  fontSize = 10.5.sp,
                  color = AdminInkMuted
                )
              }
            }

            Button(
              onClick = onGenerateAiInsights,
              enabled = aiInsightsState !is AiTaskState.Loading,
              colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier
                .heightIn(min = 40.dp)
                .testTag("admin_ai_insights_button")
            ) {
              Text(
                text = if (aiInsightsState is AiTaskState.Loading) "তৈরি হচ্ছে..." else "✨ বিশ্লেষণ করুন",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          when (aiInsightsState) {
            is AiTaskState.Idle -> {
              Text(
                text = "বাটনে ট্যাপ করে লাইভ স্টক, অর্ডার ট্রেন্ড এবং ফেসবুক/ইদ প্রোমো ক্যাপশন এক ক্লিকে তৈরি করুন।",
                fontSize = 11.5.sp,
                color = AdminInkSecondary,
                lineHeight = 16.sp
              )
            }
            is AiTaskState.Loading -> {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                CircularProgressIndicator(strokeWidth = 2.dp, color = AdminV2CyanDeep, modifier = Modifier.size(18.dp))
                Text(
                  text = "স্টোরের ডাটা বিশ্লেষণ করে ইনসাইট তৈরি হচ্ছে...",
                  fontSize = 12.sp,
                  color = AdminInkPrimary
                )
              }
            }
            is AiTaskState.Error -> {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = aiInsightsState.message,
                    fontSize = 11.5.sp,
                    color = Color(0xFF991B1B),
                    modifier = Modifier.weight(1f)
                  )
                  OutlinedButton(onClick = onGenerateAiInsights) {
                    Text("পুনরায়", fontSize = 11.sp)
                  }
                }
              }
            }
            is AiTaskState.Success -> {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = AdminV2IceSurface,
                border = BorderStroke(1.dp, AdminCardBorderWarm),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = aiInsightsState.data,
                  fontSize = 12.sp,
                  lineHeight = 18.sp,
                  color = AdminInkPrimary,
                  modifier = Modifier.padding(12.dp)
                )
              }
            }
          }
        }
      }

      // Quick Shortcuts Header
      Text(
        text = "মূল প্রশাসনিক হাবসমূহ (Master Function Hubs):",
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Black,
        color = AdminInkPrimary
      )

      // Master Hub 1: Product Hub Card
      GlossyAdminCard(
        modifier = Modifier
          .clickable { onGoToTab(AdminTab.PRODUCTS) }
          .testTag("dashboard_product_hub_card"),
        cornerRadius = 18.dp,
        accentBorderColor = AdminCardBorderWarm,
        contentPadding = 14.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(adminAccentGlossBrush(AdminV2Cyan, AdminV2CyanDeep)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "👗", fontSize = 19.sp)
              }
              Column {
                Text(text = "১. প্রোডাক্ট ও ইনভেন্টরি হাব", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AdminInkPrimary)
                Text(text = "${products.size} টি পণ্য • ${categories.size} টি ক্যাটাগরি", fontSize = 11.sp, color = AdminInkSecondary, fontWeight = FontWeight.SemiBold)
              }
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = AdminV2SkySoft,
              border = BorderStroke(1.dp, AdminCardBorderWarm)
            ) {
              Text(
                text = "পণ্য সংক্রান্ত সব",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AdminV2CyanDeep,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Text(
            text = "নতুন পণ্য যোগ, স্টক ও টাকার ভ্যালুয়েশন, ক্যাটাগরিভিত্তিক স্টক ব্রেকডাউন, POS সিঙ্ক এবং সেলস ও রিটার্ন রিপোর্ট।",
            fontSize = 11.5.sp,
            color = AdminInkMuted,
            lineHeight = 16.sp
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(text = "প্রোডাক্ট হাবে প্রবেশ করুন →", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = AdminV2CyanDeep)
          }
        }
      }

      // Master Hub 2: Order Hub Card
      GlossyAdminCard(
        modifier = Modifier
          .clickable { onGoToTab(AdminTab.ORDERS) }
          .testTag("dashboard_order_hub_card"),
        cornerRadius = 18.dp,
        accentBorderColor = Color(0xFFA7F3D0),
        contentPadding = 14.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(adminAccentGlossBrush(AdminEmeraldGradStart, AdminEmeraldGradEnd)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "📦", fontSize = 19.sp)
              }
              Column {
                Text(text = "২. অর্ডার ম্যানেজমেন্ট হাব", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AdminInkPrimary)
                Text(text = "${orders.size} টি মোট অর্ডার • ${pendingOrders.size} টি পেন্ডিং", fontSize = 11.sp, color = AdminV2Emerald, fontWeight = FontWeight.Bold)
              }
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (pendingOrders.isNotEmpty()) Color(0xFFFEE2E2) else AdminV2EmeraldSoft
            ) {
              Text(
                text = if (pendingOrders.isNotEmpty()) "${pendingOrders.size} পেন্ডিং" else "সব সম্পন্ন",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (pendingOrders.isNotEmpty()) AdminV2Rose else AdminV2Emerald,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Text(
            text = "নতুন অর্ডার অনুমোদন, Steadfast/Pathao কুরিয়ার বুকিং, চালান মেমো প্রিন্ট, ডেলিভারি ট্র্যাকিং ও রিটার্ন এক্সচেঞ্জ।",
            fontSize = 11.5.sp,
            color = AdminInkMuted,
            lineHeight = 16.sp
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(text = "অর্ডার হাবে প্রবেশ করুন →", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = AdminV2Emerald)
          }
        }
      }

      // Master Hub 3 & 4: Marketing & Settings Cards
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GlossyAdminCard(
          modifier = Modifier
            .weight(1f)
            .clickable { onGoToTab(AdminTab.MARKETING) },
          cornerRadius = 16.dp,
          accentBorderColor = AdminCardBorderWarm,
          contentPadding = 12.dp
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(text = "🎁", fontSize = 20.sp)
            Text(text = "৩. মার্কেটিং হাব", fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold, color = AdminInkPrimary)
            Text(text = "কুপন, ভাউচার, কম্বো, B2G1 ও ব্যানার", fontSize = 10.5.sp, color = AdminInkMuted)
            Text(text = "ম্যানেজ করুন →", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
          }
        }

        GlossyAdminCard(
          modifier = Modifier
            .weight(1f)
            .clickable { onGoToTab(AdminTab.SETTINGS) },
          cornerRadius = 16.dp,
          accentBorderColor = AdminCardBorderWarm,
          contentPadding = 12.dp
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(text = "⚙️", fontSize = 20.sp)
            Text(text = "৪. সেটিংস হাব", fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold, color = AdminInkPrimary)
            Text(text = "শপ ইনফো, পেমেন্ট, কুরিয়ার ও ব্যাকআপ", fontSize = 10.5.sp, color = AdminInkMuted)
            Text(text = "কনফিগ করুন →", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
          }
        }
      }

      // Master Hub 5: Home Page Setting Card
      GlossyAdminCard(
        modifier = Modifier
          .clickable { onGoToTab(AdminTab.HOME_PAGE_SETTING) }
          .testTag("dashboard_home_page_setting_card"),
        cornerRadius = 18.dp,
        accentBorderColor = AdminV2Cyan.copy(alpha = 0.5f),
        contentPadding = 14.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(adminAccentGlossBrush(AdminRoyalGradStart, AdminRoyalGradEnd)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🏠", fontSize = 19.sp)
              }
              Column {
                Text(text = "৫. Home Page Setting (মাস্টার হাব)", fontSize = 14.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
                Text(text = "ব্লক লেআউট, ON/OFF, ব্যানার ও কালার স্টুডিও", fontSize = 11.sp, color = AdminInkSecondary, fontWeight = FontWeight.SemiBold)
              }
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = AdminV2SkySoft,
              border = BorderStroke(1.dp, AdminV2Cyan.copy(alpha = 0.35f))
            ) {
              Text(
                text = "হোম কন্ট্রোল",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AdminV2CyanDeep,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Text(
            text = "হোম পেজের প্রতিটি ব্লকের প্রোডাক্ট সার্চ করে যুক্ত/বাদ দিন, ON/OFF করুন, উপরে-নিচে মুভ করুন এবং নতুন ক্যাটাগরি বা ডিজাইন স্টুডিও কাস্টমাইজ করুন।",
            fontSize = 11.5.sp,
            color = AdminInkMuted,
            lineHeight = 16.sp
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(text = "Home Page Setting খুলুন →", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = AdminV2CyanDeep)
          }
        }
      }
    }
  }
}

@Composable
fun AdminProductsHub(
  products: List<Product>,
  categories: List<Category>,
  orders: List<Order>,
  returns: List<ReturnRecord>,
  posInventory: Map<String, PosInventoryItem> = emptyMap(),
  syncEvents: List<InventorySyncEvent> = emptyList(),
  syncHealth: SyncHealthState = SyncHealthState(),
  syncErrorLogs: List<SyncErrorLog> = emptyList(),
  onRecordPosSale: (Product, Int, String, String) -> Unit = { _, _, _, _ -> },
  onReconcileInventory: () -> Unit = {},
  onClearSyncErrors: () -> Unit = {},
  onAddClick: () -> Unit,
  onEditClick: (Product) -> Unit,
  onDeleteClick: (String) -> Unit,
  onAddCategory: () -> Unit,
  onEditCategory: (Category) -> Unit = {},
  onToggleCategorySlider: (Category) -> Unit = {},
  onDeleteCategory: (String) -> Unit
) {
  var selectedSubTab by remember { mutableStateOf("LIST") }

  val totalStockUnits = remember(products) { products.sumOf { it.stock } }
  val totalStockValuation = remember(products) { products.sumOf { it.price * it.stock } }
  val lowStockProducts = remember(products) { products.filter { it.stock <= 5 } }

  Column(modifier = Modifier.fillMaxSize()) {
    // Top Hub Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "প্রোডাক্ট ও ইনভেন্টরি হাব",
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary
        )
        Text(
          text = "মোট স্টক: $totalStockUnits পিস • ভ্যালু: ৳$totalStockValuation",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.SemiBold,
          color = AdminInkSecondary
        )
      }

      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = Modifier.heightIn(min = 44.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "নতুন পণ্য যোগ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Sub Navigation Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val subTabs = listOf(
        Triple("LIST", "পণ্য তালিকা", "📋"),
        Triple("POS_SYNC", if (syncErrorLogs.isNotEmpty()) "POS সিঙ্ক (${syncErrorLogs.size})" else "POS সিঙ্ক", "🔄"),
        Triple("STOCK", "স্টক ও ভ্যালুয়েশন", "💰"),
        Triple("SALES", "সেলস ও রিটার্ন", "📈"),
        Triple("CATEGORIES", "ক্যাটাগরি", "🏷️")
      )

      subTabs.forEach { (key, label, icon) ->
        GlossySegmentPill(
          label = "$icon $label",
          selected = selectedSubTab == key,
          onClick = { selectedSubTab = key }
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    when (selectedSubTab) {
      "LIST" -> {
        AdminProductsTab(
          products = products,
          categories = categories,
          onAddClick = onAddClick,
          onEditClick = onEditClick,
          onDeleteClick = onDeleteClick
        )
      }

      "STOCK" -> {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              GlossyAdminCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 16.dp,
                contentPadding = 12.dp
              ) {
                Column {
                  Text(text = "মোট স্টক সংখ্যা", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AdminInkMuted)
                  Text(text = "$totalStockUnits পিস", fontSize = 17.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
                  Text(text = "${products.size} টি আলাদা পণ্য", fontSize = 10.sp, color = AdminInkSecondary)
                }
              }

              GlossyAdminCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 16.dp,
                accentBorderColor = Color(0xFFA7F3D0),
                containerTintBottom = Color(0xFFF0FDF4),
                contentPadding = 12.dp
              ) {
                Column {
                  Text(text = "মোট স্টক মূল্য (Valuation)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AdminInkMuted)
                  Text(text = "৳$totalStockValuation", fontSize = 17.sp, fontWeight = FontWeight.Black, color = AdminEmeraldGradStart)
                  Text(text = "ইনভেন্টরি অ্যাসেট", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AdminInkSecondary)
                }
              }
            }
          }

          item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              GlossyAdminCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 16.dp,
                contentPadding = 12.dp
              ) {
                val avgPrice = if (products.isNotEmpty()) products.sumOf { it.price } / products.size else 0
                Column {
                  Text(text = "গড় পণ্যের মূল্য", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AdminInkMuted)
                  Text(text = "৳$avgPrice", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
                  Text(text = "আইটেম প্রতি গড়", fontSize = 10.sp, color = AdminInkMuted)
                }
              }

              GlossyAdminCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 16.dp,
                accentBorderColor = if (lowStockProducts.isNotEmpty()) Color(0xFFFECACA) else AdminCardBorderWarm,
                containerTintBottom = if (lowStockProducts.isNotEmpty()) Color(0xFFFEF2F2) else AdminV2IceSurface,
                contentPadding = 12.dp
              ) {
                Column {
                  Text(text = "কম স্টক সতর্কতা (≤৫)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AccentRed)
                  Text(text = "${lowStockProducts.size} টি পণ্য", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AccentRed)
                  Text(text = "রিস্টক প্রয়োজন", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = AccentRed)
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "ক্যাটাগরি অনুযায়ী স্টক ও টাকার হিসাব (Category Breakdown):",
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary
            )
          }

          val catList = (categories.map { it.name } + products.map { it.category }).distinct()
          items(catList) { catName ->
            val catProducts = products.filter { it.category == catName }
            val catPieces = catProducts.sumOf { it.stock }
            val catValue = catProducts.sumOf { it.price * it.stock }
            val pct = if (totalStockValuation > 0) (catValue * 100) / totalStockValuation else 0

            GlossyAdminCard(
              cornerRadius = 16.dp,
              contentPadding = 12.dp
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "🏷️", fontSize = 14.sp)
                    Text(text = catName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = AdminV2IceSurface,
                      border = BorderStroke(0.5.dp, AdminCardBorderWarm)
                    ) {
                      Text(
                        text = "${catProducts.size} টি পণ্য",
                        fontSize = 10.sp,
                        color = AdminInkPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Text(
                    text = "$pct% ইনভেন্টরি",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminInkSecondary
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text(text = "মোট স্টক পরিমাণ:", fontSize = 10.sp, color = AdminInkMuted)
                    Text(text = "$catPieces পিস", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
                  }
                  Column(horizontalAlignment = Alignment.End) {
                    Text(text = "মোট স্টক মূল্য:", fontSize = 10.sp, color = AdminInkMuted)
                    Text(text = "৳$catValue", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AdminEmeraldGradStart)
                  }
                }

                if (catProducts.isNotEmpty()) {
                  HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AdminCardBorderSoft)
                  catProducts.forEach { cp ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "• ${cp.name}",
                        fontSize = 11.sp,
                        color = AdminInkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                      )
                      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "${cp.stock} পিস × ৳${cp.price}", fontSize = 10.sp, color = AdminInkMuted)
                        Text(text = "= ৳${cp.stock * cp.price}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      "SALES" -> {
        val completedOrders = orders.filter { it.status.equals("Delivered", ignoreCase = true) }
        val completedRevenue = completedOrders.sumOf { it.total }

        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              GlossyAdminCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 16.dp,
                accentBorderColor = Color(0xFFA7F3D0),
                containerTintBottom = Color(0xFFF0FDF4),
                contentPadding = 12.dp
              ) {
                Column {
                  Text(text = "মোট সম্পন্ন অর্ডার", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AdminInkMuted)
                  Text(text = "${completedOrders.size} টি", fontSize = 17.sp, fontWeight = FontWeight.Black, color = AdminEmeraldGradStart)
                  Text(text = "সফল ডেলিভারি", fontSize = 10.sp, color = AdminInkSecondary)
                }
              }

              GlossyAdminCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 16.dp,
                contentPadding = 12.dp
              ) {
                Column {
                  Text(text = "মোট সম্পন্ন বিক্রয় আয়", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AdminInkMuted)
                  Text(text = "৳$completedRevenue", fontSize = 17.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
                  Text(text = "ক্যাশ ও বিকাশ ক্যাশড", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AdminInkSecondary)
                }
              }
            }
          }

          item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              GlossyAdminCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 16.dp,
                contentPadding = 12.dp
              ) {
                Column {
                  Text(text = "মোট রিটার্ন রেকর্ড", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AdminInkMuted)
                  Text(text = "${returns.size} টি রেকর্ড", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AdminAmberGradStart)
                  val retPieces = returns.sumOf { r -> r.returnedItems.sumOf { it.quantity } }
                  Text(text = "মোট $retPieces পিস ফেরত", fontSize = 10.sp, color = AdminInkMuted)
                }
              }

              GlossyAdminCard(
                modifier = Modifier.weight(1f),
                cornerRadius = 16.dp,
                contentPadding = 12.dp
              ) {
                Column {
                  Text(text = "পণ্য এক্সচেঞ্জ রিপোর্ট", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = AdminInkMuted)
                  val exchangeCount = returns.count { it.exchangeItems.isNotEmpty() }
                  Text(text = "$exchangeCount টি এক্সচেঞ্জ", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
                  val diffSum = returns.sumOf { it.priceDifference }
                  Text(text = "নিষ্পত্তি: ৳$diffSum", fontSize = 10.sp, color = AdminInkSecondary)
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "সর্বশেষ সম্পন্ন অর্ডার তালিকা (${completedOrders.size}):",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = AdminInkPrimary
            )
          }

          if (completedOrders.isEmpty()) {
            item {
              GlossyAdminCard(cornerRadius = 14.dp, contentPadding = 16.dp) {
                Text(
                  text = "এখনও কোনো সম্পন্ন (Delivered) অর্ডার নেই।",
                  fontSize = 12.sp,
                  color = AdminInkMuted
                )
              }
            }
          } else {
            items(completedOrders.take(15)) { co ->
              GlossyAdminCard(cornerRadius = 14.dp, contentPadding = 12.dp) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(text = "অর্ডার #${co.id}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
                    Text(text = "গ্রাহক: ${co.customerName} (${co.phone})", fontSize = 11.sp, color = AdminInkPrimary)
                    Text(text = "পণ্য: ${co.items.joinToString { "${it.name} ×${it.quantity}" }}", fontSize = 10.sp, color = AdminInkMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                  }
                  Column(horizontalAlignment = Alignment.End) {
                    Text(text = "৳${co.total}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AdminEmeraldGradStart)
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color(0xFFDCFCE7)
                    ) {
                      Text(text = "Delivered", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                  }
                }
              }
            }
          }
        }
      }

      "POS_SYNC" -> {
        InventorySyncPanel(
          products = products,
          posInventory = posInventory,
          syncEvents = syncEvents,
          syncHealth = syncHealth,
          syncErrorLogs = syncErrorLogs,
          onRecordPosSale = onRecordPosSale,
          onReconcileNow = onReconcileInventory,
          onClearSyncErrors = onClearSyncErrors
        )
      }

      "CATEGORIES" -> {
        AdminCategoriesTab(
          categories = categories,
          products = products,
          onAddClick = onAddCategory,
          onDeleteCategory = onDeleteCategory,
          onEditCategory = onEditCategory,
          onToggleSlider = onToggleCategorySlider
        )
      }
    }
  }
}

@Composable
fun AdminOrdersHub(
  orders: List<Order>,
  products: List<Product>,
  returns: List<ReturnRecord>,
  storeConfig: StoreConfig,
  courierConfig: CourierConfig,
  onUpdateStatus: (String, String) -> Unit,
  onDispatchCourier: (String, String) -> Unit,
  onViewInvoice: (Order) -> Unit,
  onOpenShippingLabel: (Order) -> Unit,
  onOpenReturn: (Order?) -> Unit,
  onViewReturnSlip: (ReturnRecord) -> Unit,
  onUpdateCourierConfig: (CourierConfig) -> Unit,
  onOpenQrScanner: () -> Unit = {},
  onShowToast: (String) -> Unit,
  onEnqueueOrderSms: (Order) -> Unit = {}
) {
  val context = LocalContext.current
  var selectedOrderSubTab by remember { mutableStateOf("NEW_ORDERS") }
  val pendingOrders = remember(orders) { orders.filter { it.status.equals("Pending", ignoreCase = true) } }
  val shippedOrders = remember(orders) { orders.filter { it.status.equals("Shipped", ignoreCase = true) } }

  Column(modifier = Modifier.fillMaxSize()) {
    // Vibrant Royal-Azure-Cyan Hero Card for Orders & Courier Hub with White Text
    AdminVibrantHeroCard(
      title = "অর্ডার ও কুরিয়ার হাব",
      subtitle = "অর্ডার অনুমোদন, কুরিয়ার বুকিং ও ডেলিভারি ট্র্যাকিং",
      icon = Icons.Default.LocalShipping,
      brush = adminIndigoSkyBrush(),
      trailingContent = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
              .heightIn(min = 48.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable(onClick = onOpenQrScanner)
              .testTag("admin_orders_qr_scanner_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "পার্সেল QR স্ক্যান",
                tint = AdminV2CyanDeep,
                modifier = Modifier.size(17.dp)
              )
              Text(
                text = "QR",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AdminV2CyanDeep
              )
            }
          }

          if (pendingOrders.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = AdminV2Rose,
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
            ) {
              Text(
                text = "🔔 ${pendingOrders.size} টি নতুন",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      },
      bottomContent = {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AdminHeroGlassChip(
            text = "মোট অর্ডার: ${orders.size}",
            dotColor = Color(0xFFBAE6FD)
          )
          AdminHeroGlassChip(
            text = "পেন্ডিং: ${pendingOrders.size}",
            dotColor = Color(0xFFFDE68A)
          )
          AdminHeroGlassChip(
            text = "Shipped: ${shippedOrders.size}",
            dotColor = Color(0xFF6EE7B7)
          )
        }
      }
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val trackedOrdersCount = remember(orders) {
        orders.count {
          it.status.equals("Pending", ignoreCase = true) ||
            it.status.equals("Shipped", ignoreCase = true) ||
            it.status.equals("Delivered", ignoreCase = true)
        }
      }
      val orderSubTabs = listOf(
        Triple("NEW_ORDERS", "নতুন অর্ডার অনুমোদন", "🔔"),
        Triple("DELIVERY_TRACKING", "ডেলিভারি ট্র্যাকিং", "📊"),
        Triple("ALL_ORDERS", "সকল অর্ডার ও ফিল্টার", "📋"),
        Triple("SHIPPING_LABELS", "শিপিং লেবেল", "🏷️"),
        Triple("COURIER", "কুরিয়ার ট্র্যাকিং", "🚚"),
        Triple("RETURNS", "রিটার্ন ও এক্সচেঞ্জ", "🔄")
      )

      orderSubTabs.forEach { (key, label, icon) ->
        val isSelected = selectedOrderSubTab == key
        val badgeText = when (key) {
          "NEW_ORDERS" -> if (pendingOrders.isNotEmpty()) "${pendingOrders.size}" else null
          "DELIVERY_TRACKING" -> "$trackedOrdersCount"
          "ALL_ORDERS" -> "${orders.size}"
          "SHIPPING_LABELS" -> "${shippedOrders.size}"
          "RETURNS" -> "${returns.size}"
          else -> null
        }

        GlossySegmentPill(
          label = "$icon $label",
          selected = isSelected,
          badgeText = badgeText,
          onClick = { selectedOrderSubTab = key },
          modifier = Modifier.testTag("order_hub_tab_$key")
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    when (selectedOrderSubTab) {
      "NEW_ORDERS" -> {
        if (pendingOrders.isEmpty()) {
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.2.dp, Color(0xFFA7F3D0)),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.fillMaxWidth()) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(adminEmeraldTealBrush())
                  .padding(horizontal = 16.dp, vertical = 12.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(Color.White.copy(alpha = 0.24f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                  Column {
                    Text(
                      text = "অর্ডার অনুমোদন ডেস্ক আপ-টু-ডেট",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Black,
                      color = Color.White
                    )
                    Text(
                      text = "কোনো নতুন পেন্ডিং অর্ডার অপেক্ষমান নেই",
                      fontSize = 11.sp,
                      color = Color.White.copy(alpha = 0.90f)
                    )
                  }
                }
              }

              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(
                    Brush.verticalGradient(listOf(Color.White, Color(0xFFF0FDF4)))
                  )
                  .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "সব নতুন অর্ডার পর্যালোচনা সম্পন্ন হয়েছে!",
                  fontSize = 14.5.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = AdminInkPrimary
                )
                Text(
                  text = "বর্তমানে কোনো নতুন পেন্ডিং অর্ডার নেই। পূর্ববর্তী অর্ডার দেখতে 'সকল অর্ডার ও ফিল্টার' ট্যাবে যান।",
                  fontSize = 11.5.sp,
                  color = AdminInkSecondary,
                  modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                GlossyGradientButton(
                  text = "সকল অর্ডার দেখুন (${orders.size})",
                  brush = adminIndigoSkyBrush(),
                  onClick = { selectedOrderSubTab = "ALL_ORDERS" }
                )
              }
            }
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "অপেক্ষমান নতুন অর্ডার (${pendingOrders.size} টি):",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black,
                  color = AdminInkPrimary
                )
                Text(
                  text = "১-ক্লিকে অনুমোদন করুন",
                  fontSize = 11.sp,
                  color = AdminAmberGradStart,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            items(pendingOrders, key = { it.id }) { order ->
              GlossyAdminCard(
                cornerRadius = 16.dp,
                accentBorderColor = Color(0xFFFBBF24),
                contentPadding = 14.dp
              ) {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = "অর্ডার #${order.id}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminV2CyanDeep
                      )
                      Text(text = order.date, fontSize = 10.sp, color = AdminInkMuted)
                    }

                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = Color(0xFFFEF3C7),
                      border = BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                      Text(
                        text = "⏳ পেন্ডিং রিভিউ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  Text(text = "গ্রাহক: ${order.customerName} • 📞 ${order.phone}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
                  Text(text = "ঠিকানা: ${order.address}", fontSize = 11.sp, color = AdminInkMuted)
                  Text(text = "পেমেন্ট মেথড: ${order.paymentMethod} • সর্বমোট: ৳${order.total}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)

                  Spacer(modifier = Modifier.height(6.dp))
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .background(AdminV2IceSurface, RoundedCornerShape(8.dp))
                      .padding(8.dp)
                  ) {
                    order.items.forEach { item ->
                      Text(
                        text = "• ${item.name} × ${item.quantity} (৳${item.price})",
                        fontSize = 11.sp,
                        color = AdminInkPrimary
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Text(text = "দ্রুত অ্যাকশন (Quick Approval):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminInkSecondary)
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Button(
                      onClick = { onUpdateStatus(order.id, "Confirmed") },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.weight(1f),
                      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                      Text(text = "✅ কনফার্ম", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                      onClick = { onUpdateStatus(order.id, "Processing") },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.weight(1f),
                      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                      Text(text = "⚙️ প্রসেসিং", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                      onClick = { onDispatchCourier(order.id, "Steadfast") },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.weight(1f),
                      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                      Text(text = "🚚 কুরিয়ার", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                      onClick = { onUpdateStatus(order.id, "Cancelled") },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.weight(1f),
                      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                      Text(text = "❌ বাতিল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Button(
                    onClick = { onViewInvoice(order) },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminV2IceSurface, contentColor = AdminV2CyanDeep),
                    border = BorderStroke(1.dp, AdminCardBorderWarm),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(min = 48.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                  ) {
                    Text(text = "📄 ইনভয়েস মেমো দেখুন ও প্রিন্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    OutlinedButton(
                      onClick = {
                        val msg = SmsHelper.buildOrderStatusSms(order, storeConfig.storeName)
                        SimSmsGatewayManager.launchPlaySafeSmsIntent(context, order.phone, msg)
                      },
                      border = BorderStroke(1.dp, AdminV2CyanDeep),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("order_sms_intent_${order.id}")
                    ) {
                      Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(text = "📩 ১-ক্লিক SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
                    }

                    Button(
                      onClick = { onEnqueueOrderSms(order) },
                      colors = ButtonDefaults.buttonColors(containerColor = AdminV2Emerald, contentColor = Color.White),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("order_sms_queue_${order.id}")
                    ) {
                      Text(text = "📲 SIM কিউ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }
        }
      }

      "DELIVERY_TRACKING" -> {
        AdminDeliveryTrackingTab(
          orders = orders,
          onUpdateStatus = onUpdateStatus,
          onViewInvoice = onViewInvoice,
          onOpenShippingLabel = onOpenShippingLabel
        )
      }

      "ALL_ORDERS" -> {
        AdminOrdersTab(
          orders = orders,
          storeName = storeConfig.storeName,
          onUpdateStatus = onUpdateStatus,
          onDispatchCourier = onDispatchCourier,
          onViewInvoice = onViewInvoice,
          onOpenShippingLabel = onOpenShippingLabel,
          onOpenReturn = { onOpenReturn(it) },
          onEnqueueOrderSms = onEnqueueOrderSms
        )
      }

      "SHIPPING_LABELS" -> {
        AdminShippingLabelsTab(
          shippedOrders = shippedOrders,
          storeConfig = storeConfig,
          onOpenShippingLabel = onOpenShippingLabel,
          onGoToAllOrders = { selectedOrderSubTab = "ALL_ORDERS" },
          onShowToast = onShowToast
        )
      }

      "COURIER" -> {
        AdminCourierTab(
          config = courierConfig,
          onSave = onUpdateCourierConfig,
          onTest = onShowToast
        )
      }

      "RETURNS" -> {
        AdminReturnsTab(
          returns = returns,
          orders = orders,
          products = products,
          onOpenNewReturn = { onOpenReturn(null) },
          onViewReturnSlip = onViewReturnSlip
        )
      }
    }
  }
}

@Composable
fun AdminShippingLabelsTab(
  shippedOrders: List<Order>,
  storeConfig: StoreConfig,
  onOpenShippingLabel: (Order) -> Unit,
  onGoToAllOrders: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current

  Column(modifier = Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(5.dp, RoundedCornerShape(20.dp), spotColor = AdminV2CyanDeep.copy(alpha = 0.25f))
        .clip(RoundedCornerShape(20.dp))
        .background(adminVioletCyanBrush())
        .border(1.2.dp, Color.White.copy(alpha = 0.48f), RoundedCornerShape(20.dp))
    ) {
      Column(
        modifier = Modifier.padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "পার্সেল শিপিং লেবেল ডেস্ক (${shippedOrders.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
          Text(
            text = "Shipped স্ট্যাটাসের অর্ডারগুলোর QR কোডসহ পার্সেল স্টিকার প্রিন্ট করুন",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.90f)
          )
        }

        if (shippedOrders.isNotEmpty()) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 48.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                printShippingLabels(
                  context = context,
                  orders = shippedOrders,
                  config = storeConfig,
                  format = ShippingLabelPrintFormat.THERMAL_4X6,
                  onShowToast = onShowToast
                )
              }
              .testTag("batch_print_shipping_labels_btn")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Default.Print, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "সব Shipped লেবেল একসাথে প্রিন্ট (${shippedOrders.size} টি)",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AdminV2CyanDeep
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (shippedOrders.isEmpty()) {
      GlossyAdminCard(
        cornerRadius = 18.dp,
        contentPadding = 24.dp
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(text = "🏷️", fontSize = 36.sp)
          Text(
            text = "কোনো Shipped অর্ডার পাওয়া যায়নি",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Text(
            text = "যেকোনো অর্ডারের স্ট্যাটাস 'Shipped' করলে এখানে পার্সেল শিপিং লেবেল তৈরি ও প্রিন্ট করা যাবে।",
            fontSize = 11.5.sp,
            color = AdminInkMuted
          )
          Button(
            onClick = onGoToAllOrders,
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.heightIn(min = 48.dp)
          ) {
            Text(text = "সকল অর্ডারে যান", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(shippedOrders, key = { it.id }) { order ->
          GlossyAdminCard(
            modifier = Modifier.testTag("shipping_label_card_${order.id}"),
            cornerRadius = 16.dp,
            contentPadding = 14.dp
          ) {
            Column(
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "অর্ডার #${order.id}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminV2CyanDeep
                  )
                  Text(
                    text = if (order.trackingId.isNotBlank()) "Tracking: ${order.trackingId}" else "তারিখ: ${order.date}",
                    fontSize = 11.sp,
                    color = AdminInkMuted
                  )
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFE0F2FE)
                ) {
                  Text(
                    text = "🚚 Shipped",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminV2CyanDeep,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              Text(
                text = "${order.customerName} • 📞 ${order.phone}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminInkPrimary
              )
              Text(
                text = "📍 ${order.address}, ${order.thana}, ${order.district}",
                fontSize = 11.sp,
                color = AdminInkMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "COD/মোট: ৳${order.total} (${order.items.sumOf { it.quantity }} পিস)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary
                )
                Button(
                  onClick = { onOpenShippingLabel(order) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB), contentColor = Color.White),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("shipping_label_tab_print_${order.id}"),
                  contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                  Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = "লেবেল তৈরি ও প্রিন্ট", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
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
fun AdminMarketingHub(
  coupons: List<Coupon>,
  banners: List<BannerItem>,
  storeConfig: StoreConfig = StoreConfig(),
  products: List<Product> = emptyList(),
  onUpdateStoreConfig: (StoreConfig) -> Unit = {},
  onShowToast: (String) -> Unit = {},
  onAddCouponClick: () -> Unit,
  onToggleCoupon: (String) -> Unit,
  onDeleteCoupon: (String) -> Unit,
  onAddBannerClick: () -> Unit,
  onEditBannerClick: (BannerItem) -> Unit = {},
  onToggleBanner: (String) -> Unit,
  onToggleBannerOverlay: (String) -> Unit = {},
  onDeleteBanner: (String) -> Unit
) {
  var selectedSubTab by remember { mutableStateOf("B2G1") }

  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(text = "মার্কেটিং ও অফার হাব", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
        Text(text = "Buy 2 Get 1 অফার, কুপন কোড ও প্রমোশনাল ব্যানার", fontSize = 11.5.sp, color = AdminInkMuted)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val tabs = listOf(
        Pair("B2G1", "🎁 Buy 2 Get 1 (${storeConfig.b2g1.eligibleProductIds.size})"),
        Pair("COUPONS", "🎟️ কুপন (${coupons.size})"),
        Pair("BANNERS", "🖼️ ব্যানার (${banners.size})")
      )
      tabs.forEach { (key, label) ->
        GlossySegmentPill(
          label = label,
          selected = selectedSubTab == key,
          onClick = { selectedSubTab = key }
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    when (selectedSubTab) {
      "B2G1" -> {
        com.example.ui.components.AdminB2G1Hub(
          storeConfig = storeConfig,
          products = products,
          onSaveStoreConfig = onUpdateStoreConfig,
          onShowToast = onShowToast
        )
      }
      "COUPONS" -> {
        AdminCouponsTab(
          coupons = coupons,
          onAddClick = onAddCouponClick,
          onToggle = onToggleCoupon,
          onDelete = onDeleteCoupon
        )
      }
      "BANNERS" -> {
        AdminBannersTab(
          banners = banners,
          bannerOverlayEnabled = storeConfig.pageConfig.homePage.bannerOverlayEnabled,
          onToggleGlobalOverlay = { enabled ->
            onUpdateStoreConfig(
              storeConfig.copy(
                pageConfig = storeConfig.pageConfig.copy(
                  homePage = storeConfig.pageConfig.homePage.copy(bannerOverlayEnabled = enabled)
                )
              )
            )
          },
          onAddClick = onAddBannerClick,
          onEditClick = onEditBannerClick,
          onToggle = onToggleBanner,
          onToggleOverlay = onToggleBannerOverlay,
          onDelete = onDeleteBanner
        )
      }
    }
  }
}

@Composable
fun AdminSettingsHub(
  config: StoreConfig,
  pixelConfig: PixelConfig,
  productsCount: Int,
  ordersCount: Int,
  onSaveConfig: (StoreConfig) -> Unit,
  onSavePixel: (PixelConfig) -> Unit,
  onResetDefaults: () -> Unit,
  onShowToast: (String) -> Unit
) {
  com.example.ui.components.AdminSystemSettingsHub(
    config = config,
    pixelConfig = pixelConfig,
    productsCount = productsCount,
    ordersCount = ordersCount,
    onSaveConfig = onSaveConfig,
    onSavePixel = onSavePixel,
    onResetDefaults = onResetDefaults,
    onShowToast = onShowToast
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersTab(
  orders: List<Order>,
  storeName: String = "SAJGOJ FASHION",
  onUpdateStatus: (String, String) -> Unit,
  onDispatchCourier: (String, String) -> Unit,
  onViewInvoice: (Order) -> Unit,
  onOpenShippingLabel: (Order) -> Unit = {},
  onOpenReturn: (Order) -> Unit,
  onEnqueueOrderSms: (Order) -> Unit = {}
) {
  val context = LocalContext.current
  val statuses = listOf("Pending", "Confirmed", "Shipped", "Delivered", "Cancelled")
  var selectedStatusFilter by remember { mutableStateOf("All") }
  var phoneSearchQuery by remember { mutableStateOf("") }

  val filteredOrders = remember(orders, selectedStatusFilter, phoneSearchQuery) {
    orders.filter { order ->
      val matchesStatus = selectedStatusFilter == "All" || order.status.equals(selectedStatusFilter, ignoreCase = true)
      val cleanQuery = phoneSearchQuery.trim().replace(Regex("[\\s-+()]"), "")
      val cleanPhone = order.phone.replace(Regex("[\\s-+()]"), "")
      val matchesPhone = cleanQuery.isEmpty() || cleanPhone.contains(cleanQuery)
      matchesStatus && matchesPhone
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color.White,
      border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "কাস্টমার অর্ডার তালিকা (${orders.size})",
          fontSize = 15.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary
        )
        if (selectedStatusFilter != "All" || phoneSearchQuery.isNotBlank()) {
          Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFFFEF3C7),
            border = BorderStroke(1.dp, Color(0xFFFCD34D))
          ) {
            Text(
              text = "ফলাফল: ${filteredOrders.size} টি",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFFB45309),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }
    }
    Spacer(modifier = Modifier.height(8.dp))

    // 1. Customer Phone Number Filter Bar
    OutlinedTextField(
      value = phoneSearchQuery,
      onValueChange = { phoneSearchQuery = it },
      modifier = Modifier.fillMaxWidth(),
      placeholder = { Text("গ্রাহকের ফোন নম্বর দিয়ে খুঁজুন (যেমন: 017...)", fontSize = 12.sp) },
      leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = AdminV2CyanDeep, modifier = Modifier.size(18.dp))
      },
      trailingIcon = {
        if (phoneSearchQuery.isNotEmpty()) {
          IconButton(onClick = { phoneSearchQuery = "" }, modifier = Modifier.size(28.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = AdminInkMuted, modifier = Modifier.size(16.dp))
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(12.dp),
      colors = adminOutlinedTextFieldColors()
    )

    Spacer(modifier = Modifier.height(8.dp))

    // 2. Status Filter Chips (All, Pending, Confirmed, Shipped, Delivered, Cancelled)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val filterOptions = listOf("All") + statuses
      filterOptions.forEach { statusKey ->
        val count = if (statusKey == "All") orders.size else orders.count { it.status.equals(statusKey, ignoreCase = true) }
        val isSelected = selectedStatusFilter == statusKey
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (isSelected) AdminV2CyanDeep else Color.White,
          border = BorderStroke(1.dp, if (isSelected) AdminV2CyanDeep else AdminCardBorderWarm),
          modifier = Modifier.clickable { selectedStatusFilter = statusKey }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = if (statusKey == "All") "সকল ($count)" else "$statusKey ($count)",
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else AdminInkPrimary
            )
          }
        }
      }

      if (selectedStatusFilter != "All" || phoneSearchQuery.isNotBlank()) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color(0xFFFFEBEE),
          border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
          modifier = Modifier.clickable {
            selectedStatusFilter = "All"
            phoneSearchQuery = ""
          }
        ) {
          Text(
            text = "রিসেট ✕",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC62828),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (filteredOrders.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = "🔍", fontSize = 36.sp)
          Text(
            text = "কোন অর্ডার খুঁজে পাওয়া যায়নি!",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Text(
            text = "অন্য কোনো ফোন নম্বর বা স্ট্যাটাস দিয়ে চেষ্টা করুন।",
            fontSize = 11.sp,
            color = AdminInkMuted
          )
          Button(
            onClick = {
              selectedStatusFilter = "All"
              phoneSearchQuery = ""
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White)
          ) {
            Text("ফিল্টার রিসেট করুন", fontSize = 12.sp)
          }
        }
      }
    } else {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
        items(filteredOrders, key = { it.id }) { order ->
        var isDropdownExpanded by remember { mutableStateOf(false) }

        GlossyAdminCard(
          cornerRadius = 16.dp,
          contentPadding = 12.dp
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "#${order.id}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
                Text(text = order.date, fontSize = 10.sp, color = AdminInkMuted)
              }

              // Status Selector Dropdown
              ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = AdminV2IceSurface,
                  border = BorderStroke(1.dp, AdminCardBorderWarm),
                  modifier = Modifier.menuAnchor()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(text = order.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                  }
                }
                ExposedDropdownMenu(
                  expanded = isDropdownExpanded,
                  onDismissRequest = { isDropdownExpanded = false },
                  containerColor = Color.White
                ) {
                  statuses.forEach { st ->
                    DropdownMenuItem(
                      text = { Text(st, fontSize = 12.sp) },
                      onClick = {
                        onUpdateStatus(order.id, st)
                        isDropdownExpanded = false
                      }
                    )
                  }
                }
              }
            }

            HorizontalDivider(color = AdminCardBorderSoft)

            Text(text = "গ্রাহক: ${order.customerName} (${order.phone})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
            Text(text = "ঠিকানা: ${order.address}, ${order.thana}, ${order.district}", fontSize = 11.sp, color = AdminInkSecondary)
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(text = "অর্ডারকৃত পণ্যসমূহ (${order.items.size}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
              order.items.forEach { itm ->
                val v = listOfNotNull(
                  if (itm.color.isNotBlank()) "রং: ${itm.color}" else null,
                  if (itm.size.isNotBlank()) "সাইজ: ${itm.size}" else null,
                  if (itm.ageGroup.isNotBlank()) "👶 বয়স: ${itm.ageGroup}" else null
                ).joinToString(" | ")
                Text(
                  text = "• ${itm.name} x${itm.quantity}${if (v.isNotBlank()) " [$v]" else ""}",
                  fontSize = 11.sp,
                  color = AdminInkPrimary,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "মোট: ${order.total} ৳ (${order.paymentMethod})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkPrimary
              )

              if (order.trackingId.isNotBlank()) {
                Text(
                  text = "Track: ${order.trackingId}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AccentGreen
                )
              }
            }

            // Order Action Buttons
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { onViewInvoice(order) },
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2IceSurface, contentColor = AdminV2CyanDeep),
                border = BorderStroke(1.dp, AdminCardBorderWarm),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(34.dp)
              ) {
                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "চালান", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { onDispatchCourier(order.id, "Steadfast") },
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(34.dp)
              ) {
                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Steadfast", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { onOpenReturn(order) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C), contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(34.dp)
              ) {
                Text(text = "🔄 রিটার্ন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            if (order.status.equals("Shipped", ignoreCase = true)) {
              Button(
                onClick = { onOpenShippingLabel(order) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB), contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 48.dp)
                  .testTag("shipping_label_btn_${order.id}")
              ) {
                Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "🏷️ শিপিং লেবেল তৈরি ও প্রিন্ট (QR সহ)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  val msg = SmsHelper.buildOrderStatusSms(order, storeName)
                  SimSmsGatewayManager.launchPlaySafeSmsIntent(context, order.phone, msg)
                },
                border = BorderStroke(1.dp, AdminV2CyanDeep),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .heightIn(min = 48.dp)
                  .testTag("all_order_sms_intent_${order.id}")
              ) {
                Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "📩 ১-ক্লিক SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
              }

              Button(
                onClick = { onEnqueueOrderSms(order) },
                colors = ButtonDefaults.buttonColors(containerColor = AdminV2Emerald, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .heightIn(min = 48.dp)
                  .testTag("all_order_sms_queue_${order.id}")
              ) {
                Text(text = "📲 SIM কিউ SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
fun AdminReturnsTab(
  returns: List<ReturnRecord>,
  orders: List<Order>,
  products: List<Product>,
  onOpenNewReturn: () -> Unit,
  onViewReturnSlip: (ReturnRecord) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredReturns = returns.filter {
    searchQuery.isBlank() ||
      it.id.contains(searchQuery, ignoreCase = true) ||
      it.orderId.contains(searchQuery, ignoreCase = true) ||
      it.customerName.contains(searchQuery, ignoreCase = true) ||
      it.phone.contains(searchQuery)
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Vibrant Coral-Amber Gradient Header for Returns & Exchanges with White Text
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(5.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFEA580C).copy(alpha = 0.25f))
        .clip(RoundedCornerShape(20.dp))
        .background(adminWarmCoralAmberBrush())
        .border(1.2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
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
            text = "সেলস রিটার্ন ও এক্সচেঞ্জ (${returns.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
          Text(
            text = "মূল্য সমন্বয় ও ব্যালেন্স নিষ্পত্তি তালিকা",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.92f)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color.White,
          shadowElevation = 2.dp,
          modifier = Modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onOpenNewReturn)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
            Text(text = "নতুন রিটার্ন", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEA580C))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 3 Summary Cards
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      val extraCollected = returns.filter { it.priceDifference > 0 }.sumOf { it.priceDifference }
      val balanceRefunded = returns.filter { it.priceDifference < 0 }.sumOf { kotlin.math.abs(it.priceDifference) }

      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF0F9FF),
        border = BorderStroke(1.dp, Color(0xFFBAE6FD))
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(text = "মোট মেমো", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AdminInkSecondary)
          Text(text = "${returns.size} টি", fontSize = 14.5.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
        }
      }

      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFF7ED),
        border = BorderStroke(1.dp, Color(0xFFFDBA74))
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(text = "অতিরিক্ত আদায়", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFC2410C))
          Text(text = "+ ৳$extraCollected", fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = Color(0xFFEA580C))
        }
      }

      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFECFDF5),
        border = BorderStroke(1.dp, Color(0xFFA7F3D0))
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(text = "গ্রাহককে ফেরত", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF065F46))
          Text(text = "- ৳$balanceRefunded", fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = AdminV2Emerald)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("রিটার্ন ID, অর্ডার ID বা ফোন দিয়ে খুঁজুন...", fontSize = 11.5.sp) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = adminOutlinedTextFieldColors()
    )

    Spacer(modifier = Modifier.height(10.dp))

    if (filteredReturns.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("কোনো রিটার্ন বা এক্সচেঞ্জ রেকর্ড নেই", fontSize = 13.sp, color = AdminInkMuted)
      }
    } else {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
        items(filteredReturns, key = { it.id }) { ret ->
          GlossyAdminCard(
            cornerRadius = 16.dp,
            contentPadding = 12.dp
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                  ) {
                    Text(
                      text = "#${ret.id}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFE65100),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Text(text = "অর্ডার: #${ret.orderId}", fontSize = 11.sp, color = AdminInkMuted)
                }

                Text(text = ret.date, fontSize = 10.sp, color = AdminInkMuted)
              }

              HorizontalDivider(color = AdminCardBorderSoft)

              Text(text = "${ret.customerName} • 📞 ${ret.phone}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
              Text(
                text = "ফেরত পণ্য (${ret.returnedItems.size}টি): ${ret.returnedItems.joinToString { "${it.name} (${it.quantity}টি)" }}",
                fontSize = 10.5.sp,
                color = AdminInkSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )

              if (ret.isExchange && ret.exchangeItems.isNotEmpty()) {
                Text(
                  text = "নতুন এক্সচেঞ্জ (${ret.exchangeItems.size}টি): ${ret.exchangeItems.joinToString { "${it.name} (${it.quantity}টি)" }}",
                  fontSize = 10.sp,
                  color = Color(0xFFE65100),
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }

              // Settlement Badge
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = when (ret.differenceType) {
                    "customer_paid_extra" -> Color(0xFFFFF3E0)
                    "customer_refunded_balance" -> Color(0xFFE8F5E9)
                    else -> Color(0xFFE3F2FD)
                  },
                  border = BorderStroke(
                    1.dp,
                    when (ret.differenceType) {
                      "customer_paid_extra" -> Color(0xFFFF9800)
                      "customer_refunded_balance" -> AccentGreen
                      else -> Color(0xFF2196F3)
                    }
                  )
                ) {
                  Text(
                    text = when (ret.differenceType) {
                      "customer_paid_extra" -> "+ ৳${ret.priceDifference} অতিরিক্ত আদায় (${ret.settlementMethod})"
                      "customer_refunded_balance" -> "- ৳${kotlin.math.abs(ret.priceDifference)} ফেরত প্রদান (${ret.settlementMethod})"
                      else -> "সমমূল্য বিনিময় (০ ৳)"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (ret.differenceType) {
                      "customer_paid_extra" -> Color(0xFFB26A00)
                      "customer_refunded_balance" -> AccentGreen
                      else -> Color(0xFF0D47A1)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }

                Button(
                  onClick = { onViewReturnSlip(ret) },
                  colors = ButtonDefaults.buttonColors(containerColor = AdminV2IceSurface, contentColor = AdminV2CyanDeep),
                  border = BorderStroke(1.dp, AdminCardBorderWarm),
                  shape = RoundedCornerShape(10.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                  modifier = Modifier.height(28.dp)
                ) {
                  Text("মেমো ভিউ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
fun AdminCouponsTab(
  coupons: List<Coupon>,
  onAddClick: () -> Unit,
  onToggle: (String) -> Unit,
  onDelete: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "কুপন কোড ও অফার (${coupons.size})", fontSize = 15.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "কুপন তৈরি", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(coupons, key = { it.id }) { cp ->
        GlossyAdminCard(
          cornerRadius = 14.dp,
          contentPadding = 12.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = cp.code, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
              Text(
                text = "ছাড়: ${if (cp.type == "%") "${cp.value}%" else "${cp.value} ৳"}  |  মিনিমাম: ${cp.minOrder} ৳",
                fontSize = 11.sp,
                color = AdminInkPrimary
              )
              Text(text = "মেয়াদ: ${cp.expiry}", fontSize = 10.sp, color = AdminInkMuted)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Switch(
                checked = cp.isActive,
                onCheckedChange = { onToggle(cp.id) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
              )
              IconButton(onClick = { onDelete(cp.id) }, modifier = Modifier.size(36.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminCourierTab(
  config: CourierConfig,
  onSave: (CourierConfig) -> Unit,
  onTest: (String) -> Unit
) {
  var steadfastKey by remember { mutableStateOf(config.steadfastApiKey) }
  var steadfastSec by remember { mutableStateOf(config.steadfastSecret) }
  var pathaoId by remember { mutableStateOf(config.pathaoClientId) }
  var pathaoSec by remember { mutableStateOf(config.pathaoClientSecret) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Vibrant Emerald-Teal Courier Logistics Hero Card
    AdminVibrantHeroCard(
      title = "কুরিয়ার লজিস্টিকস ইন্টিগ্রেশন",
      subtitle = "Steadfast ও Pathao কুরিয়ার API এবং অটো-বুকিং",
      icon = Icons.Default.LocalShipping,
      brush = adminEmeraldTealBrush(),
      bottomContent = {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AdminHeroGlassChip(text = "Steadfast সক্রিয়", dotColor = Color(0xFF6EE7B7))
          AdminHeroGlassChip(text = "Pathao প্রস্তুত", dotColor = Color(0xFFBAE6FD))
        }
      }
    )

    // Steadfast Card with Vibrant Sky-Azure Header Strip
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = Color.White,
      border = BorderStroke(1.2.dp, Color(0xFFBAE6FD)),
      shadowElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(adminRoyalGlossBrush())
            .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Steadfast Courier API",
              fontSize = 14.5.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Surface(
              shape = RoundedCornerShape(50),
              color = Color.White.copy(alpha = 0.24f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
            ) {
              Text(
                text = "সক্রিয়",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
              )
            }
          }
        }

        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = steadfastKey,
            onValueChange = { steadfastKey = it },
            label = { Text("Steadfast API Key", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = steadfastSec,
            onValueChange = { steadfastSec = it },
            label = { Text("Secret Key", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          GlossyGradientButton(
            text = "Steadfast সংযোগ টেস্ট করুন",
            brush = adminRoyalGlossBrush(),
            onClick = { onTest("Steadfast API সংযোগ সফল! টেস্ট পিং কার্যকর হয়েছে। ✅") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // Pathao Card with Vibrant Violet-Cobalt Header Strip
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = Color.White,
      border = BorderStroke(1.2.dp, Color(0xFFC7D2FE)),
      shadowElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(adminVioletCyanBrush())
            .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Pathao Logistics API",
              fontSize = 14.5.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Surface(
              shape = RoundedCornerShape(50),
              color = Color.White.copy(alpha = 0.24f),
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
            ) {
              Text(
                text = "প্রস্তুত",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
              )
            }
          }
        }

        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = pathaoId,
            onValueChange = { pathaoId = it },
            label = { Text("Client ID", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = pathaoSec,
            onValueChange = { pathaoSec = it },
            label = { Text("Client Secret", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          GlossyGradientButton(
            text = "Pathao সংযোগ টেস্ট করুন",
            brush = adminVioletCyanBrush(),
            onClick = { onTest("Pathao API সংযোগ সফল! ✅") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    GlossyGradientButton(
      text = "কুরিয়ার সেটিংস সংরক্ষণ করুন",
      icon = Icons.Default.Check,
      brush = adminEmeraldTealBrush(),
      onClick = {
        onSave(config.copy(steadfastApiKey = steadfastKey, steadfastSecret = steadfastSec, pathaoClientId = pathaoId, pathaoClientSecret = pathaoSec))
      },
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))
  }
}

@Composable
fun AdminBannersTab(
  banners: List<BannerItem>,
  bannerOverlayEnabled: Boolean = true,
  onToggleGlobalOverlay: (Boolean) -> Unit = {},
  onAddClick: () -> Unit,
  onEditClick: (BannerItem) -> Unit = {},
  onToggle: (String) -> Unit,
  onToggleOverlay: (String) -> Unit = {},
  onDelete: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "হোম ব্যানার ব্যবস্থাপনা (${banners.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.heightIn(min = 42.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "ব্যানার যোগ", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Banner Settings Global Overlay On/Off Switch
    PageConfigSwitchRow(
      title = "Banner Settings: Overlay On/Off",
      subtitle = if (bannerOverlayEnabled) {
        "চালু: ব্যানারের টাইটেল/সাবটাইটেল/বাটন দেখাবে (যদি লেখা থাকে)"
      } else {
        "বন্ধ (Off): কোনো লেখা দেখাবে না, শুধু পরিষ্কার ছবি দেখাবে"
      },
      checked = bannerOverlayEnabled,
      onCheckedChange = onToggleGlobalOverlay,
      testTag = "admin_banner_global_overlay_switch"
    )

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(banners, key = { it.id }) { b ->
        GlossyAdminCard(
          cornerRadius = 14.dp,
          contentPadding = 10.dp
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              AppImage(
                model = b.imageUrl,
                contentDescription = b.title.ifBlank { "ব্যানার ছবি" },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(64.dp)
                  .clip(RoundedCornerShape(8.dp))
              )

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = b.title.ifBlank { "(শুধু ছবি — টাইটেল নেই)" },
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (b.title.isBlank()) AdminInkMuted else AdminInkPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                if (b.subtitle.isNotBlank()) {
                  Text(text = b.subtitle, fontSize = 11.sp, color = AdminInkMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (b.buttonText.isNotBlank()) {
                  Text(text = "বাটন: ${b.buttonText}", fontSize = 10.sp, color = AdminV2CyanDeep, fontWeight = FontWeight.SemiBold)
                }
                Text(
                  text = if (b.link.isNotBlank()) "🔗 ${b.link}" else "লিংক নেই (ক্লিক অকার্যকর)",
                  fontSize = 10.sp,
                  color = if (b.link.isNotBlank()) Color(0xFF059669) else AdminInkMuted,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Switch(
                checked = b.isActive,
                onCheckedChange = { onToggle(b.id) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentGreen)
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = { onToggleOverlay(b.id) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (b.showOverlay) Color(0xFF94A3B8) else Color(0xFFF59E0B)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                  .weight(1f)
                  .heightIn(min = 42.dp)
                  .testTag("admin_banner_overlay_btn_${b.id}")
              ) {
                Icon(
                  imageVector = if (b.showOverlay) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null,
                  tint = if (b.showOverlay) AdminInkPrimary else Color(0xFFD97706),
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (b.showOverlay) "Hide Overlay" else "Show Overlay",
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (b.showOverlay) AdminInkPrimary else Color(0xFFD97706),
                  maxLines = 1
                )
              }

              OutlinedButton(
                onClick = { onEditClick(b) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                  .heightIn(min = 42.dp)
                  .testTag("admin_banner_edit_btn_${b.id}")
              ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = AdminV2CyanDeep, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("এডিট", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
              }

              OutlinedButton(
                onClick = { onDelete(b.id) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                  .heightIn(min = 42.dp)
                  .testTag("admin_banner_delete_btn_${b.id}")
              ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ডিলিট", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = AccentRed)
              }
            }
          }
        }
      }
    }
  }
}

// System Settings & Backup UI moved to modular AdminSystemSettingsHub.kt

// ----------------- FORM DIALOGS -----------------
@Composable
fun CouponFormDialog(
  onDismiss: () -> Unit,
  onSave: (Coupon) -> Unit
) {
  var code by remember { mutableStateOf("") }
  var type by remember { mutableStateOf("%") }
  var valueStr by remember { mutableStateOf("10") }
  var minOrderStr by remember { mutableStateOf("1500") }
  var expiry by remember { mutableStateOf("2026-12-31") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = Color.White,
      border = BorderStroke(1.5.dp, AdminCardBorderWarm),
      shadowElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(text = "নতুন কুপন কোড তৈরি করুন", fontSize = 15.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)

        OutlinedTextField(
          value = code,
          onValueChange = { code = it.uppercase() },
          label = { Text("কুপন কোড (যেমন: EID2026)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = valueStr,
            onValueChange = { valueStr = it },
            label = { Text("ছাড়ের মান (10% বা 100৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier.weight(1f)
          )

          OutlinedTextField(
            value = minOrderStr,
            onValueChange = { minOrderStr = it },
            label = { Text("মিনিমাম অর্ডার (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = expiry,
          onValueChange = { expiry = it },
          label = { Text("মেয়াদ শেষ তারিখ (YYYY-MM-DD)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = {
            if (code.isNotBlank()) {
              onSave(
                Coupon(
                  id = "cp_${System.currentTimeMillis()}",
                  code = code,
                  type = type,
                  value = valueStr.toIntOrNull() ?: 10,
                  minOrder = minOrderStr.toIntOrNull() ?: 0,
                  expiry = expiry,
                  isActive = true
                )
              )
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("কুপন সেভ করুন", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun BannerFormDialog(
  initialBanner: BannerItem? = null,
  onDismiss: () -> Unit,
  onSave: (BannerItem) -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var title by remember(initialBanner) { mutableStateOf(initialBanner?.title.orEmpty()) }
  var subtitle by remember(initialBanner) { mutableStateOf(initialBanner?.subtitle.orEmpty()) }
  var buttonText by remember(initialBanner) { mutableStateOf(initialBanner?.buttonText.orEmpty()) }
  var link by remember(initialBanner) { mutableStateOf(initialBanner?.link.orEmpty()) }
  var imageUrl by remember(initialBanner) { mutableStateOf(initialBanner?.imageUrl.orEmpty()) }
  var showOverlay by remember(initialBanner) { mutableStateOf(initialBanner?.showOverlay ?: true) }
  var isCopyingImage by remember { mutableStateOf(false) }

  val pickBannerImageLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      isCopyingImage = true
      scope.launch {
        val localUri = withContext(Dispatchers.IO) {
          ProductMediaHelper.copyPickedImageToInternalStorage(context, uri)
        }
        if (!localUri.isNullOrBlank()) {
          imageUrl = localUri
        }
        isCopyingImage = false
      }
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = Color.White,
      border = BorderStroke(1.5.dp, AdminCardBorderWarm),
      shadowElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(12.dp)
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (initialBanner != null) "ব্যানার এডিট করুন" else "নতুন ব্যানার যোগ করুন",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = AdminInkPrimary
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = AdminInkMuted)
          }
        }

        // Banner Image Preview + Gallery Upload
        val previewModel = imageUrl.trim().ifBlank { "res://banner_eid_special_1792530116809" }
        AppImage(
          model = previewModel,
          contentDescription = "ব্যানার প্রিভিউ",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, AdminCardBorderSoft, RoundedCornerShape(12.dp))
        )

        OutlinedButton(
          onClick = {
            pickBannerImageLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, AdminV2CyanDeep),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("banner_pick_gallery_btn")
        ) {
          if (isCopyingImage) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
          } else {
            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "গ্যালারি থেকে ব্যানার ছবি আপলোড করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
        }

        OutlinedTextField(
          value = imageUrl,
          onValueChange = { imageUrl = it },
          label = { Text("ছবির URL (ঐচ্ছিক — না দিলে ডিফল্ট)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("ব্যানার টাইটেল (ঐচ্ছিক — খালি রাখা যাবে)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("banner_title_input")
        )

        OutlinedTextField(
          value = subtitle,
          onValueChange = { subtitle = it },
          label = { Text("সাবটাইটেল (ঐচ্ছিক — খালি রাখা যাবে)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("banner_subtitle_input")
        )

        OutlinedTextField(
          value = buttonText,
          onValueChange = { buttonText = it },
          label = { Text("বাটন টেক্সট (ঐচ্ছিক — খালি রাখা যাবে)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("banner_button_input")
        )

        OutlinedTextField(
          value = link,
          onValueChange = { link = it },
          label = { Text("ক্লিক লিংক (উদাঃ /product/p1 বা /category/চুরি)") },
          placeholder = { Text("খালি রাখলে ছবিতে ক্লিক করলে কিছু হবে না", fontSize = 11.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("banner_link_input")
        )

        PageConfigSwitchRow(
          title = "এই ব্যানারে Overlay দেখান",
          subtitle = "বন্ধ করলে শুধু ছবি দেখাবে (লেখা ও বাটন লুকানো থাকবে)",
          checked = showOverlay,
          onCheckedChange = { showOverlay = it },
          testTag = "banner_item_overlay_switch"
        )

        Button(
          onClick = {
            onSave(
              BannerItem(
                id = initialBanner?.id ?: "b_${System.currentTimeMillis()}",
                title = title.trim(),
                subtitle = subtitle.trim(),
                buttonText = buttonText.trim(),
                link = link.trim(),
                imageUrl = imageUrl.trim().ifBlank { "res://banner_eid_special_1792530116809" },
                showOverlay = showOverlay,
                isActive = initialBanner?.isActive ?: true
              )
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("banner_save_btn")
        ) {
          Text(if (initialBanner != null) "ব্যানার আপডেট করুন" else "ব্যানার যুক্ত করুন", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
