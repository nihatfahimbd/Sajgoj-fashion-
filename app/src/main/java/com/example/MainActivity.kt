package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.firebase.configureAppCheck
import com.example.notifications.OrderNotificationHelper
import com.example.ui.AppScreen
import com.example.ui.ProductSortOption
import com.example.ui.StoreViewModel
import com.example.ui.components.AiStylistDialog
import com.example.ui.components.CartAndCheckoutDialog
import com.example.ui.components.CustomerAccountDialog
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.LiveSupportChatDialog
import com.example.ui.components.OrderNotificationsDialog
import com.example.ui.components.OrderStatusAlertBanner
import com.example.ui.components.ProductDetailDialog
import com.example.ui.components.ShippingLabelDialog
import com.example.ui.components.StoreHeader
import com.example.ui.components.StorefrontBottomNavBar
import com.example.ui.components.StorefrontBottomTab
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CustomerProfileScreen
import com.example.ui.screens.StorefrontScreen
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.SajgojFashionTheme
import com.example.ui.theme.parseColorHex
import com.example.util.VisualImageSearchHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : ComponentActivity() {
  private val viewModel: StoreViewModel by viewModels()
  private val deepLinkOrderIdState = mutableStateOf<String?>(null)
  private val deepLinkUrlState = mutableStateOf<String?>(null)

  private fun extractDeepLinkFromIntent(incomingIntent: Intent?) {
    if (incomingIntent == null) return
    val orderId = incomingIntent.getStringExtra(OrderNotificationHelper.EXTRA_ORDER_ID)
    if (!orderId.isNullOrBlank()) {
      deepLinkOrderIdState.value = orderId
    }
    if (incomingIntent.action == Intent.ACTION_VIEW && incomingIntent.data != null) {
      val url = incomingIntent.dataString
      if (!url.isNullOrBlank()) {
        deepLinkUrlState.value = url
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    extractDeepLinkFromIntent(intent)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    configureAppCheck(this, intent)
    extractDeepLinkFromIntent(intent)
    enableEdgeToEdge()

    setContent {
      SajgojFashionTheme {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        // State collection
        val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
        val adminTab by viewModel.adminTab.collectAsStateWithLifecycle()
        val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
        val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
        val allProducts by viewModel.products.collectAsStateWithLifecycle()
        val categories by viewModel.categories.collectAsStateWithLifecycle()
        val banners by viewModel.banners.collectAsStateWithLifecycle()
        val cartItems by viewModel.cart.collectAsStateWithLifecycle()
        val orders by viewModel.orders.collectAsStateWithLifecycle()
        val returns by viewModel.returns.collectAsStateWithLifecycle()
        val coupons by viewModel.coupons.collectAsStateWithLifecycle()
        val storeConfig by viewModel.storeConfig.collectAsStateWithLifecycle()
        val homeSections by viewModel.homeSections.collectAsStateWithLifecycle()
        val reels by viewModel.reels.collectAsStateWithLifecycle()
        val combos by viewModel.combos.collectAsStateWithLifecycle()
        val courierConfig by viewModel.courierConfig.collectAsStateWithLifecycle()
        val pixelConfig by viewModel.pixelConfig.collectAsStateWithLifecycle()
        val posInventory by viewModel.posInventory.collectAsStateWithLifecycle()
        val syncEvents by viewModel.syncEvents.collectAsStateWithLifecycle()
        val syncHealth by viewModel.syncHealth.collectAsStateWithLifecycle()
        val syncErrorLogs by viewModel.syncErrorLogs.collectAsStateWithLifecycle()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
        val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
        val imageSearchState by viewModel.imageSearchState.collectAsStateWithLifecycle()
        val homeScrollTrigger by viewModel.homeScrollTrigger.collectAsStateWithLifecycle()

        val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
        val reviews by viewModel.reviews.collectAsStateWithLifecycle()
        val selectedOrderInvoice by viewModel.selectedOrderInvoice.collectAsStateWithLifecycle()
        val selectedShippingLabelOrder by viewModel.selectedShippingLabelOrder.collectAsStateWithLifecycle()
        val isCartOpen by viewModel.isCartOpen.collectAsStateWithLifecycle()

        val authUser by viewModel.authUser.collectAsStateWithLifecycle()
        val isAuthBusy by viewModel.isAuthBusy.collectAsStateWithLifecycle()
        val aiStylistState by viewModel.aiStylistState.collectAsStateWithLifecycle()
        val aiAdminInsightsState by viewModel.aiAdminInsightsState.collectAsStateWithLifecycle()
        val smsQueue by viewModel.smsQueue.collectAsStateWithLifecycle()
        val isProcessingSmsQueue by viewModel.isProcessingSmsQueue.collectAsStateWithLifecycle()
        val smsGatewayLastLog by viewModel.smsGatewayLastLog.collectAsStateWithLifecycle()

        val customerSupportThreadState by viewModel.customerSupportThreadState.collectAsStateWithLifecycle()
        val customerSupportMessagesState by viewModel.customerSupportMessagesState.collectAsStateWithLifecycle()
        val customerUnreadSupportCount by viewModel.customerUnreadSupportCount.collectAsStateWithLifecycle()
        val adminSupportThreadsState by viewModel.adminSupportThreadsState.collectAsStateWithLifecycle()
        val selectedAdminChatId by viewModel.selectedAdminChatId.collectAsStateWithLifecycle()
        val adminSelectedChatMessagesState by viewModel.adminSelectedChatMessagesState.collectAsStateWithLifecycle()
        val adminUnreadSupportCount by viewModel.adminUnreadSupportCount.collectAsStateWithLifecycle()
        val isSendingSupportChat by viewModel.isSendingSupportChat.collectAsStateWithLifecycle()
        val orderNotifications by viewModel.orderNotifications.collectAsStateWithLifecycle()
        val savedAddresses by viewModel.savedAddresses.collectAsStateWithLifecycle()
        val customerLoyaltyStatus by viewModel.customerLoyaltyStatus.collectAsStateWithLifecycle()
        val unreadOrderNotificationCount by viewModel.unreadOrderNotificationCount.collectAsStateWithLifecycle()
        val activeOrderBannerAlert by viewModel.activeOrderBannerAlert.collectAsStateWithLifecycle()
        val activePolicyTopic by viewModel.activePolicyTopic.collectAsStateWithLifecycle()
        val pendingDeepLinkOrderId by deepLinkOrderIdState
        val pendingDeepLinkUrl by deepLinkUrlState

        var showVoiceFallbackDialog by remember { mutableStateOf(false) }
        var voiceFallbackInput by remember { mutableStateOf("") }
        var showScannerDialog by remember { mutableStateOf(false) }
        var manualScanInput by remember { mutableStateOf("") }
        var showAiStylistDialog by remember { mutableStateOf(false) }
        var aiStylistInitialPrompt by remember { mutableStateOf("") }
        var storeBottomTab by remember { mutableStateOf(StorefrontBottomTab.HOME) }
        var openOffersTrigger by remember { mutableStateOf(0) }
        var isOffersDialogOpen by remember { mutableStateOf(false) }
        var showAccountDialog by remember { mutableStateOf(false) }
        var showLiveSupportDialog by remember { mutableStateOf(false) }
        var showNotificationsDialog by remember { mutableStateOf(false) }
        var hasPushPermission by remember {
          mutableStateOf(OrderNotificationHelper.hasNotificationPermission(context))
        }

        // Zero-permission Photo Picker for Visual Image Search
        val photoPickerLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
          if (uri != null) {
            coroutineScope.launch {
              val result = withContext(Dispatchers.IO) {
                VisualImageSearchHelper.analyzeImageUriForProducts(context, uri, allProducts)
              }
              if (result != null) {
                viewModel.applyImageSearch(uri.toString(), result.first, result.second)
              } else {
                viewModel.emitToast("⚠️ ছবিটি বিশ্লেষণ করা যায়নি, অনুগ্রহ করে অন্য ছবি দিন।")
              }
            }
          }
        }

        // SpeechRecognizer Launcher for Voice Product Search
        val voiceSearchLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
          if (activityResult.resultCode == Activity.RESULT_OK) {
            val spokenList = activityResult.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = spokenList?.firstOrNull()?.trim().orEmpty()
            if (spoken.isNotEmpty()) {
              viewModel.clearImageSearch()
              viewModel.setSelectedCategory("All")
              viewModel.setSearchQuery(spoken)
              viewModel.emitToast("🎙️ ভয়েস সার্চ: \"$spoken\"")
            }
          }
        }

        fun launchVoiceSearch() {
          val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault().toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "পণ্যের নাম বলুন (যেমন: জামদানি শাড়ি, ঝুমকা)")
          }
          val canHandle = intent.resolveActivity(context.packageManager) != null
          if (canHandle) {
            try {
              voiceSearchLauncher.launch(intent)
            } catch (_: Exception) {
              voiceFallbackInput = ""
              showVoiceFallbackDialog = true
            }
          } else {
            voiceFallbackInput = ""
            showVoiceFallbackDialog = true
          }
        }

        val notificationPermissionLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
          hasPushPermission = granted || OrderNotificationHelper.hasNotificationPermission(context)
          viewModel.onPushPermissionResult(hasPushPermission)
        }

        fun requestPushPermission() {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (OrderNotificationHelper.hasNotificationPermission(context)) {
              hasPushPermission = true
              viewModel.onPushPermissionResult(true)
            } else {
              notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
          } else {
            hasPushPermission = OrderNotificationHelper.hasNotificationPermission(context)
            viewModel.onPushPermissionResult(hasPushPermission)
          }
        }

        LaunchedEffect(Unit) {
          OrderNotificationHelper.ensureNotificationChannel(context)
        }

        LaunchedEffect(pendingDeepLinkOrderId) {
          val orderId = pendingDeepLinkOrderId
          if (!orderId.isNullOrBlank()) {
            viewModel.openOrderById(orderId)
            deepLinkOrderIdState.value = null
          }
        }

        LaunchedEffect(pendingDeepLinkUrl) {
          val url = pendingDeepLinkUrl
          if (!url.isNullOrBlank()) {
            viewModel.handleIncomingDomainLink(url)
            deepLinkUrlState.value = null
          }
        }

        LaunchedEffect(activeOrderBannerAlert?.id) {
          val alert = activeOrderBannerAlert
          if (alert != null && alert.channel != "fcm" && OrderNotificationHelper.hasNotificationPermission(context)) {
            OrderNotificationHelper.postSystemNotification(context, alert)
          }
        }

        // Toast listener
        LaunchedEffect(Unit) {
          viewModel.toastEvent.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
          }
        }

        // BackHandler
        BackHandler(
          enabled = currentScreen == AppScreen.ADMIN_PANEL ||
            currentScreen == AppScreen.CUSTOMER_PROFILE ||
            isCartOpen ||
            selectedProduct != null ||
            selectedOrderInvoice != null ||
            selectedShippingLabelOrder != null ||
            showVoiceFallbackDialog ||
            showScannerDialog ||
            showAiStylistDialog ||
            showAccountDialog ||
            showLiveSupportDialog ||
            showNotificationsDialog
        ) {
          when {
            showNotificationsDialog -> showNotificationsDialog = false
            showLiveSupportDialog -> showLiveSupportDialog = false
            showAccountDialog -> showAccountDialog = false
            showAiStylistDialog -> showAiStylistDialog = false
            showScannerDialog -> showScannerDialog = false
            showVoiceFallbackDialog -> showVoiceFallbackDialog = false
            selectedShippingLabelOrder != null -> viewModel.closeShippingLabel()
            selectedOrderInvoice != null -> viewModel.closeInvoice()
            selectedProduct != null -> viewModel.closeProductDetail()
            isCartOpen -> viewModel.setCartOpen(false)
            currentScreen == AppScreen.CUSTOMER_PROFILE -> viewModel.setScreen(AppScreen.STOREFRONT)
            currentScreen == AppScreen.ADMIN_PANEL -> viewModel.setScreen(AppScreen.STOREFRONT)
          }
        }

        val customPageActive = currentScreen == AppScreen.STOREFRONT && storeConfig.design.useCustomPageBg
        val scaffoldBg = if (customPageActive) {
          parseColorHex(storeConfig.design.pageBgStart, CreamBackground)
        } else {
          CreamBackground
        }
        val activeBottomBarTab = when {
          isCartOpen -> StorefrontBottomTab.CART
          currentScreen == AppScreen.CUSTOMER_PROFILE || showAccountDialog -> StorefrontBottomTab.ACCOUNT
          isOffersDialogOpen -> StorefrontBottomTab.OFFERS
          storeBottomTab == StorefrontBottomTab.SHOPPING -> StorefrontBottomTab.SHOPPING
          else -> StorefrontBottomTab.HOME
        }
        Scaffold(
          containerColor = scaffoldBg,
          bottomBar = {
            if (currentScreen == AppScreen.STOREFRONT || currentScreen == AppScreen.CUSTOMER_PROFILE) {
              StorefrontBottomNavBar(
                activeTab = activeBottomBarTab,
                cartItemCount = cartItems.sumOf { it.quantity },
                onSelectHome = {
                  showAccountDialog = false
                  viewModel.setScreen(AppScreen.STOREFRONT)
                  storeBottomTab = StorefrontBottomTab.HOME
                  viewModel.resetHomeFilters()
                },
                onSelectShopping = {
                  showAccountDialog = false
                  viewModel.setScreen(AppScreen.STOREFRONT)
                  viewModel.resetHomeFilters()
                  storeBottomTab = StorefrontBottomTab.SHOPPING
                },
                onSelectOffers = {
                  showAccountDialog = false
                  viewModel.setScreen(AppScreen.STOREFRONT)
                  openOffersTrigger += 1
                },
                onSelectAccount = {
                  showAccountDialog = false
                  viewModel.setScreen(AppScreen.CUSTOMER_PROFILE)
                },
                onSelectCart = {
                  showAccountDialog = false
                  viewModel.setCartOpen(true)
                },
                homePage = storeConfig.pageConfig.homePage
              )
            }
          },
          modifier = Modifier
            .fillMaxSize()
            .background(scaffoldBg)
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .then(
                if (customPageActive) {
                  Modifier.background(
                    Brush.verticalGradient(
                      listOf(
                        parseColorHex(storeConfig.design.pageBgStart, CreamBackground),
                        parseColorHex(storeConfig.design.pageBgEnd, CreamSurface)
                      )
                    )
                  )
                } else {
                  Modifier
                }
              )
              .padding(innerPadding)
              .statusBarsPadding()
          ) {
            Column(modifier = Modifier.fillMaxSize()) {
            val activeHomeSections = remember(homeSections) { homeSections.filter { it.enabled } }
            val isFilteredOrShopping = selectedCategory != "All" ||
              searchQuery.isNotBlank() ||
              imageSearchState != null ||
              sortOption != ProductSortOption.DEFAULT ||
              storeBottomTab == StorefrontBottomTab.SHOPPING
            val showSearchStripInHeader = isFilteredOrShopping || activeHomeSections.firstOrNull()?.type == "search_bar"

            // App Header
            StoreHeader(
              config = storeConfig,
              currentScreen = currentScreen,
              cartItemCount = cartItems.sumOf { it.quantity },
              searchQuery = searchQuery,
              onSearchChange = { viewModel.setSearchQuery(it) },
              onToggleScreen = {
                if (currentScreen == AppScreen.STOREFRONT) {
                  viewModel.setScreen(AppScreen.ADMIN_PANEL)
                } else {
                  viewModel.setScreen(AppScreen.STOREFRONT)
                }
              },
              onOpenCart = { viewModel.setCartOpen(true) },
              categories = categories,
              selectedCategory = selectedCategory,
              onCategorySelected = {
                viewModel.setScreen(AppScreen.STOREFRONT)
                storeBottomTab = StorefrontBottomTab.HOME
                viewModel.setSelectedCategory(it)
              },
              onHomeReset = {
                viewModel.setScreen(AppScreen.STOREFRONT)
                storeBottomTab = StorefrontBottomTab.HOME
                viewModel.resetHomeFilters()
              },
              onVoiceSearchClick = { launchVoiceSearch() },
              onImageSearchClick = {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              onScanClick = {
                manualScanInput = ""
                showScannerDialog = true
              },
              isImageSearchActive = imageSearchState != null,
              onClearAllSearch = {
                viewModel.setSearchQuery("")
                viewModel.clearImageSearch()
              },
              authUser = authUser,
              isAuthBusy = isAuthBusy,
              onGoogleAuthClick = {
                if (authUser != null) {
                  viewModel.signOutGoogle(context)
                } else {
                  viewModel.signInWithGoogle(context, unlockAdminIfVerified = false)
                }
              },
              unreadNotificationCount = unreadOrderNotificationCount,
              onOpenNotifications = {
                hasPushPermission = OrderNotificationHelper.hasNotificationPermission(context)
                showNotificationsDialog = true
              },
              showSearchStripInHeader = showSearchStripInHeader
            )

            // Screen Switcher
            Box(modifier = Modifier.fillMaxSize()) {
              when (currentScreen) {
                AppScreen.STOREFRONT -> {
                  StorefrontScreen(
                    products = products,
                    banners = banners,
                    config = storeConfig,
                    categories = categories,
                    homeSections = homeSections,
                    reels = reels,
                    combos = combos,
                    orders = orders,
                    coupons = coupons,
                    cartItemCount = cartItems.sumOf { it.quantity },
                    cartItems = cartItems,
                    selectedCategory = selectedCategory,
                    searchQuery = searchQuery,
                    sortOption = sortOption,
                    onCategorySelected = { viewModel.setSelectedCategory(it) },
                    onSortOptionSelected = { viewModel.setSortOption(it) },
                    onResetFilters = { viewModel.resetHomeFilters() },
                    imageSearchState = imageSearchState,
                    onClearImageSearch = { viewModel.clearImageSearch() },
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onVoiceSearchClick = { launchVoiceSearch() },
                    onImageSearchClick = {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                    onScanClick = {
                      manualScanInput = ""
                      showScannerDialog = true
                    },
                    scrollToTopTrigger = homeScrollTrigger,
                    onProductClick = { viewModel.openProductDetail(it) },
                    onAddToCart = { viewModel.addToCart(it, 1) },
                    onAddComboToCart = { viewModel.addComboToCart(it) },
                    onLikeReel = { viewModel.likeReel(it) },
                    onBannerClick = { /* scroll or search */ },
                    onOpenAiStylist = {
                      aiStylistInitialPrompt = ""
                      showAiStylistDialog = true
                    },
                    isShoppingAllMode = storeBottomTab == StorefrontBottomTab.SHOPPING,
                    onExitShoppingAllMode = {
                      storeBottomTab = StorefrontBottomTab.HOME
                      viewModel.resetHomeFilters()
                    },
                    onOpenShopping = {
                      showAccountDialog = false
                      viewModel.resetHomeFilters()
                      storeBottomTab = StorefrontBottomTab.SHOPPING
                    },
                    openOffersTrigger = openOffersTrigger,
                    onOffersVisibilityChange = { isOffersDialogOpen = it },
                    authUser = authUser,
                    isAuthBusy = isAuthBusy,
                    supportThreadState = customerSupportThreadState,
                    supportMessagesState = customerSupportMessagesState,
                    customerUnreadSupportCount = customerUnreadSupportCount,
                    isSendingSupportChat = isSendingSupportChat,
                    onGoogleSignIn = { viewModel.signInWithGoogle(context, unlockAdminIfVerified = false) },
                    onSendSupportMessage = { text, ordId -> viewModel.sendCustomerSupportMessage(text, ordId) },
                    onMarkSupportRead = { viewModel.markCustomerSupportRead() },
                    incomingPolicyTopic = activePolicyTopic,
                    onClearIncomingPolicyTopic = { viewModel.clearPolicyTopic() }
                  )
                }

                AppScreen.ADMIN_PANEL -> {
                  AdminDashboardScreen(
                    currentTab = adminTab,
                    onSelectTab = { viewModel.setAdminTab(it) },
                    isAdminLoggedIn = isAdminLoggedIn,
                    onLogin = { pass -> viewModel.loginAdmin(pass) },
                    onLogout = { viewModel.logoutAdmin() },
                    products = allProducts,
                    categories = categories,
                    orders = orders,
                    returns = returns,
                    coupons = coupons,
                    banners = banners,
                    storeConfig = storeConfig,
                    courierConfig = courierConfig,
                    pixelConfig = pixelConfig,
                    onAddProduct = { viewModel.addProduct(it) },
                    onUpdateProduct = { viewModel.updateProduct(it) },
                    onDeleteProduct = { viewModel.deleteProduct(it) },
                    onUpdateOrderStatus = { id, status -> viewModel.updateOrderStatus(id, status) },
                    onDeleteOrder = { viewModel.deleteOrder(it) },
                    onDispatchCourier = { id, provider -> viewModel.dispatchCourier(id, provider) },
                    onUpdateDeliveredBy = { id, partner -> viewModel.updateOrderDeliveryPartner(id, partner) },
                    onViewInvoice = { viewModel.openInvoice(it) },
                    onOpenShippingLabel = { viewModel.openShippingLabel(it) },
                    onAddReturnRecord = { viewModel.addReturnRecord(it) },
                    onAddCategory = { viewModel.addCategory(it) },
                    onDeleteCategory = { viewModel.deleteCategory(it) },
                    onAddCoupon = { viewModel.addCoupon(it) },
                    onToggleCoupon = { viewModel.toggleCoupon(it) },
                    onDeleteCoupon = { viewModel.deleteCoupon(it) },
                    onAddBanner = { viewModel.addBanner(it) },
                    onToggleBanner = { viewModel.toggleBanner(it) },
                    onDeleteBanner = { viewModel.deleteBanner(it) },
                    onUpdateStoreConfig = { viewModel.updateStoreConfig(it) },
                    onUpdateCourierConfig = { viewModel.updateCourierConfig(it) },
                    onUpdatePixelConfig = { viewModel.updatePixelConfig(it) },
                    onResetDefaults = { viewModel.resetToDefaults() },
                    onShowToast = { viewModel.emitToast(it) },
                    posInventory = posInventory,
                    syncEvents = syncEvents,
                    syncHealth = syncHealth,
                    syncErrorLogs = syncErrorLogs,
                    onRecordPosSale = { prod, qty, cust, pay -> viewModel.recordPosSale(prod, qty, cust, pay) },
                    onReconcileInventory = { viewModel.reconcileInventoryNow() },
                    onClearSyncErrors = { viewModel.clearSyncErrorLogs() },
                    homeSections = homeSections,
                    onToggleHomeSection = { viewModel.toggleHomeSection(it) },
                    onMoveHomeSection = { id, delta -> viewModel.moveHomeSection(id, delta) },
                    onUpdateHomeSection = { id, transform -> viewModel.updateHomeSection(id, transform) },
                    onToggleProductInHomeSection = { secId, prodId -> viewModel.toggleProductInHomeSection(secId, prodId) },
                    onAddCustomHomeSection = { viewModel.addCustomHomeSection(it) },
                    onDeleteHomeSection = { viewModel.deleteHomeSection(it) },
                    onResetHomeSectionsToDefault = { viewModel.resetHomeSectionsToDefault() },
                    authUser = authUser,
                    isAuthBusy = isAuthBusy,
                    onGoogleSignInAdmin = { viewModel.signInWithGoogle(context, unlockAdminIfVerified = true) },
                    onGoogleSignOut = { viewModel.signOutGoogle(context) },
                    aiInsightsState = aiAdminInsightsState,
                    onGenerateAiInsights = { viewModel.generateAdminStoreInsights() },
                    smsQueue = smsQueue,
                    isProcessingSmsQueue = isProcessingSmsQueue,
                    smsGatewayLastLog = smsGatewayLastLog,
                    onEnqueueSingleSms = { phone, custName, msg, ordId, type ->
                      viewModel.enqueueSingleSms(phone, custName, msg, ordId, type)
                    },
                    onEnqueueBulkSms = { recipients, template, type ->
                      viewModel.enqueueBulkSms(recipients, template, type)
                    },
                    onProcessPendingSmsNow = { viewModel.processPendingSmsNow() },
                    onRetrySmsItem = { viewModel.retrySmsQueueItem(it) },
                    onMarkSmsSentViaIntent = { viewModel.markSmsSentViaPlaySafeIntent(it) },
                    onDeleteSmsItem = { viewModel.deleteSmsQueueItem(it) },
                    onClearCompletedSms = { viewModel.clearCompletedSmsQueue() },
                    unreadSupportCount = adminUnreadSupportCount,
                    supportThreadsState = adminSupportThreadsState,
                    selectedSupportChatId = selectedAdminChatId,
                    supportMessagesState = adminSelectedChatMessagesState,
                    isSendingSupportReply = isSendingSupportChat,
                    onSelectSupportThread = { viewModel.selectAdminSupportThread(it) },
                    onSendSupportReply = { chatId, text -> viewModel.sendAdminSupportReply(chatId, text) },
                    onToggleSupportThreadStatus = { chatId, status -> viewModel.toggleAdminSupportThreadStatus(chatId, status) }
                  )
                }

                AppScreen.CUSTOMER_PROFILE -> {
                  CustomerProfileScreen(
                    authUser = authUser,
                    isAuthBusy = isAuthBusy,
                    orders = orders,
                    savedAddresses = savedAddresses,
                    loyaltyStatus = customerLoyaltyStatus,
                    storeConfig = storeConfig,
                    unreadNotificationCount = unreadOrderNotificationCount,
                    unreadSupportCount = customerUnreadSupportCount,
                    onGoogleSignIn = { viewModel.signInWithGoogle(context, unlockAdminIfVerified = false) },
                    onGoogleSignOut = { viewModel.signOutGoogle(context) },
                    onBackToStore = { viewModel.setScreen(AppScreen.STOREFRONT) },
                    onStartShopping = {
                      viewModel.setScreen(AppScreen.STOREFRONT)
                      viewModel.resetHomeFilters()
                      storeBottomTab = StorefrontBottomTab.SHOPPING
                    },
                    onOpenOrderInvoice = { order -> viewModel.openInvoice(order) },
                    onSaveAddress = { addr -> viewModel.saveCustomerAddress(addr) },
                    onSetDefaultAddress = { id -> viewModel.setDefaultCustomerAddress(id) },
                    onDeleteAddress = { id -> viewModel.deleteCustomerAddress(id) },
                    onOpenNotifications = {
                      hasPushPermission = OrderNotificationHelper.hasNotificationPermission(context)
                      showNotificationsDialog = true
                    },
                    onOpenLiveSupport = { showLiveSupportDialog = true }
                  )
                }
              }

              // Floating Automated Order Status Alert Banner
              OrderStatusAlertBanner(
                notification = activeOrderBannerAlert,
                onViewOrder = { notif -> viewModel.openOrderFromNotification(notif) },
                onDismiss = { viewModel.dismissOrderBannerAlert() },
                modifier = Modifier.align(Alignment.TopCenter)
              )

              // Overlays & Modals
              if (selectedProduct != null) {
                val productReviews = reviews.filter { it.productId == selectedProduct!!.id }
                ProductDetailDialog(
                  product = selectedProduct!!,
                  reviews = productReviews,
                  storeConfig = storeConfig,
                  allProducts = allProducts,
                  onDismiss = { viewModel.closeProductDetail() },
                  onAddToCart = { prod, qty, color, size, ageGroup ->
                    viewModel.addToCart(prod, qty, color, size, ageGroup)
                  },
                  onBuyNow = { prod, qty, color, size, ageGroup ->
                    viewModel.buyNow(prod, qty, color, size, ageGroup)
                  },
                  onAddReview = { rating, name, comment ->
                    viewModel.addReview(selectedProduct!!.id, name, rating, comment)
                  },
                  onAskAiForProduct = { prod ->
                    viewModel.closeProductDetail()
                    aiStylistInitialPrompt = "\"${prod.name}\" (৳${prod.price}) এর সাথে কী ধরনের গহনা ও স্টাইলিং মানাবে?"
                    showAiStylistDialog = true
                  }
                )
              }

              if (showAiStylistDialog) {
                AiStylistDialog(
                  initialPrompt = aiStylistInitialPrompt,
                  aiState = aiStylistState,
                  products = allProducts,
                  onAskStylist = { prompt, occasion, bitmap ->
                    viewModel.askAiStylist(prompt, occasion, bitmap)
                  },
                  onClearState = { viewModel.clearAiStylist() },
                  onProductClick = { prod ->
                    showAiStylistDialog = false
                    viewModel.openProductDetail(prod)
                  },
                  onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                  onDismiss = { showAiStylistDialog = false }
                )
              }

              if (selectedOrderInvoice != null) {
                InvoiceDialog(
                  order = selectedOrderInvoice!!,
                  config = storeConfig,
                  onDismiss = { viewModel.closeInvoice() },
                  onOpenShippingLabel = { viewModel.openShippingLabel(it) }
                )
              }

              if (selectedShippingLabelOrder != null) {
                ShippingLabelDialog(
                  order = selectedShippingLabelOrder!!,
                  config = storeConfig,
                  onDismiss = { viewModel.closeShippingLabel() },
                  onShowToast = { viewModel.emitToast(it) }
                )
              }

              if (showVoiceFallbackDialog) {
                @OptIn(ExperimentalLayoutApi::class)
                AlertDialog(
                  onDismissRequest = { showVoiceFallbackDialog = false },
                  title = {
                    Text(
                      text = "🎙️ ভয়েস প্রোডাক্ট সার্চ",
                      fontWeight = FontWeight.Black,
                      fontSize = 17.sp
                    )
                  },
                  text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                      Text(
                        text = "ভয়েস বা কি-ওয়ার্ড দিয়ে দ্রুত পণ্য খুঁজুন:",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                      )
                      OutlinedTextField(
                        value = voiceFallbackInput,
                        onValueChange = { voiceFallbackInput = it },
                        label = { Text("পণ্যের নাম লিখুন বা বলুন") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                      )
                      FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        listOf("শাড়ি", "জুয়েলারি", "ব্রাইডাল", "জামা", "টি-শার্ট", "পাঞ্জাবি", "লাল").forEach { tag ->
                          Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFEF4444),
                            border = BorderStroke(1.dp, Color.White),
                            modifier = Modifier.clickable {
                              viewModel.clearImageSearch()
                              viewModel.setSelectedCategory("All")
                              viewModel.setSearchQuery(tag)
                              showVoiceFallbackDialog = false
                            }
                          ) {
                            Text(
                              text = "🎙️ $tag",
                              color = Color.Black,
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                          }
                        }
                      }
                    }
                  },
                  confirmButton = {
                    Button(
                      onClick = {
                        if (voiceFallbackInput.isNotBlank()) {
                          viewModel.clearImageSearch()
                          viewModel.setSelectedCategory("All")
                          viewModel.setSearchQuery(voiceFallbackInput.trim())
                          showVoiceFallbackDialog = false
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon)
                    ) {
                      Text("খুঁজুন")
                    }
                  },
                  dismissButton = {
                    TextButton(onClick = { showVoiceFallbackDialog = false }) {
                      Text("বন্ধ করুন")
                    }
                  }
                )
              }

              if (showScannerDialog) {
                @OptIn(ExperimentalLayoutApi::class)
                AlertDialog(
                  onDismissRequest = { showScannerDialog = false },
                  title = {
                    Text(
                      text = "📷 QR / SKU স্ক্যানার লুকআপ",
                      fontWeight = FontWeight.Black,
                      fontSize = 17.sp
                    )
                  },
                  text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                      Text(
                        text = "পণ্যের SKU বা QR কোড লিংক দিন অথবা নিচের জনপ্রিয় SKU ট্যাপ করুন:",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                      )
                      OutlinedTextField(
                        value = manualScanInput,
                        onValueChange = { manualScanInput = it },
                        label = { Text("যেমন: SJG-101 বা #product-p1") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                      )
                      FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        allProducts.take(5).forEach { prod ->
                          Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFFEBEE),
                            border = BorderStroke(1.dp, BorderGold),
                            modifier = Modifier.clickable {
                              showScannerDialog = false
                              viewModel.handleScannedPayload(prod.sku.ifBlank { prod.id })
                            }
                          ) {
                            Text(
                              text = "${prod.sku.ifBlank { prod.id }} • ${prod.name.take(14)}",
                              color = Color.Black,
                              fontSize = 10.5.sp,
                              fontWeight = FontWeight.Bold,
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                          }
                        }
                      }
                    }
                  },
                  confirmButton = {
                    Button(
                      onClick = {
                        if (manualScanInput.isNotBlank()) {
                          showScannerDialog = false
                          viewModel.handleScannedPayload(manualScanInput)
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon)
                    ) {
                      Text("খুঁজুন")
                    }
                  },
                  dismissButton = {
                    OutlinedButton(onClick = {
                      showScannerDialog = false
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    }) {
                      Text("🖼️ ছবি দিয়ে খুঁজুন")
                    }
                  }
                )
              }
            }
          }

          CartAndCheckoutDialog(
            visible = isCartOpen,
            cartItems = cartItems,
            coupons = coupons,
            storeConfig = storeConfig,
            savedAddresses = savedAddresses,
            authUser = authUser,
            onDismiss = { viewModel.setCartOpen(false) },
            onUpdateQty = { id, delta -> viewModel.updateCartQty(id, delta) },
            onRemoveItem = { id -> viewModel.removeFromCart(id) },
            onPlaceOrder = { name, phone, district, thana, address, payment, coupon, orderType, deliveryType, landmark, category, isDefault ->
              viewModel.placeOrder(
                customerName = name,
                phone = phone,
                district = district,
                thana = thana,
                address = address,
                paymentMethod = payment,
                appliedCoupon = coupon,
                orderType = orderType,
                deliveryType = deliveryType,
                landmark = landmark,
                addressCategory = category,
                defaultShippingAddress = isDefault
              )
            },
            onShowToast = { viewModel.emitToast(it) }
          )

          if (showAccountDialog) {
            CustomerAccountDialog(
              authUser = authUser,
              isAuthBusy = isAuthBusy,
              orders = orders,
              storeConfig = storeConfig,
              unreadSupportCount = customerUnreadSupportCount,
              unreadNotificationCount = unreadOrderNotificationCount,
              onGoogleAuthClick = {
                if (authUser != null) {
                  viewModel.signOutGoogle(context)
                } else {
                  viewModel.signInWithGoogle(context, unlockAdminIfVerified = false)
                }
              },
              onOpenOrderInvoice = { order -> viewModel.openInvoice(order) },
              onOpenLiveSupport = { showLiveSupportDialog = true },
              onOpenNotifications = {
                hasPushPermission = OrderNotificationHelper.hasNotificationPermission(context)
                showNotificationsDialog = true
              },
              onDismiss = { showAccountDialog = false }
            )
          }

          if (showNotificationsDialog) {
            OrderNotificationsDialog(
              notifications = orderNotifications,
              hasPushPermission = hasPushPermission,
              fcmTokenPresent = OrderNotificationHelper.getSavedFcmToken(context).isNotBlank(),
              onRequestPushPermission = { requestPushPermission() },
              onNotificationClick = { notif ->
                showNotificationsDialog = false
                viewModel.openOrderFromNotification(notif)
              },
              onMarkAllRead = { viewModel.markAllOrderNotificationsRead() },
              onClearAll = { viewModel.clearOrderNotifications() },
              onDismiss = { showNotificationsDialog = false }
            )
          }

          if (showLiveSupportDialog) {
            LiveSupportChatDialog(
              config = storeConfig,
              authUser = authUser,
              isAuthBusy = isAuthBusy,
              orders = orders,
              threadState = customerSupportThreadState,
              messagesState = customerSupportMessagesState,
              isSending = isSendingSupportChat,
              onGoogleSignIn = { viewModel.signInWithGoogle(context, unlockAdminIfVerified = false) },
              onSendMessage = { text, ordId -> viewModel.sendCustomerSupportMessage(text, ordId) },
              onMarkRead = { viewModel.markCustomerSupportRead() },
              onDismiss = { showLiveSupportDialog = false }
            )
          }
          }
        }
      }
    }
  }
}
