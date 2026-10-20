package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.StoreViewModel
import com.example.ui.components.CartAndCheckoutDialog
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.ProductDetailDialog
import com.example.ui.components.StoreHeader
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.StorefrontScreen
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.SajgojFashionTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
  private val viewModel: StoreViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      SajgojFashionTheme {
        val context = LocalContext.current

        // State collection
        val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
        val adminTab by viewModel.adminTab.collectAsStateWithLifecycle()
        val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
        val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
        val categories by viewModel.categories.collectAsStateWithLifecycle()
        val banners by viewModel.banners.collectAsStateWithLifecycle()
        val cartItems by viewModel.cart.collectAsStateWithLifecycle()
        val orders by viewModel.orders.collectAsStateWithLifecycle()
        val coupons by viewModel.coupons.collectAsStateWithLifecycle()
        val storeConfig by viewModel.storeConfig.collectAsStateWithLifecycle()
        val courierConfig by viewModel.courierConfig.collectAsStateWithLifecycle()
        val pixelConfig by viewModel.pixelConfig.collectAsStateWithLifecycle()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

        val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
        val selectedOrderInvoice by viewModel.selectedOrderInvoice.collectAsStateWithLifecycle()
        val isCartOpen by viewModel.isCartOpen.collectAsStateWithLifecycle()

        // Toast listener
        LaunchedEffect(Unit) {
          viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
          }
        }

        // BackHandler
        BackHandler(enabled = currentScreen == AppScreen.ADMIN_PANEL || isCartOpen || selectedProduct != null || selectedOrderInvoice != null) {
          when {
            selectedOrderInvoice != null -> viewModel.closeInvoice()
            selectedProduct != null -> viewModel.closeProductDetail()
            isCartOpen -> viewModel.setCartOpen(false)
            currentScreen == AppScreen.ADMIN_PANEL -> viewModel.setScreen(AppScreen.STOREFRONT)
          }
        }

        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
        ) { innerPadding ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
              .statusBarsPadding()
          ) {
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
              onCategorySelected = { viewModel.setSelectedCategory(it) }
            )

            // Screen Switcher
            Box(modifier = Modifier.fillMaxSize()) {
              when (currentScreen) {
                AppScreen.STOREFRONT -> {
                  StorefrontScreen(
                    products = products,
                    banners = banners,
                    config = storeConfig,
                    onProductClick = { viewModel.openProductDetail(it) },
                    onAddToCart = { viewModel.addToCart(it, 1) },
                    onBannerClick = { /* scroll or search */ }
                  )
                }

                AppScreen.ADMIN_PANEL -> {
                  AdminDashboardScreen(
                    currentTab = adminTab,
                    onSelectTab = { viewModel.setAdminTab(it) },
                    isAdminLoggedIn = isAdminLoggedIn,
                    onLogin = { pass -> viewModel.loginAdmin(pass) },
                    onLogout = { viewModel.logoutAdmin() },
                    products = products,
                    categories = categories,
                    orders = orders,
                    coupons = coupons,
                    banners = banners,
                    storeConfig = storeConfig,
                    courierConfig = courierConfig,
                    pixelConfig = pixelConfig,
                    onAddProduct = { viewModel.addProduct(it) },
                    onUpdateProduct = { viewModel.updateProduct(it) },
                    onDeleteProduct = { viewModel.deleteProduct(it) },
                    onUpdateOrderStatus = { id, status -> viewModel.updateOrderStatus(id, status) },
                    onDispatchCourier = { id, provider -> viewModel.dispatchCourier(id, provider) },
                    onViewInvoice = { viewModel.openInvoice(it) },
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
                    onShowToast = { viewModel.emitToast(it) }
                  )
                }
              }

              // Overlays & Modals
              if (selectedProduct != null) {
                ProductDetailDialog(
                  product = selectedProduct!!,
                  onDismiss = { viewModel.closeProductDetail() },
                  onAddToCart = { prod, qty -> viewModel.addToCart(prod, qty) }
                )
              }

              if (isCartOpen) {
                CartAndCheckoutDialog(
                  cartItems = cartItems,
                  coupons = coupons,
                  onDismiss = { viewModel.setCartOpen(false) },
                  onUpdateQty = { id, delta -> viewModel.updateCartQty(id, delta) },
                  onRemoveItem = { id -> viewModel.removeFromCart(id) },
                  onPlaceOrder = { name, phone, district, thana, address, payment, coupon ->
                    viewModel.placeOrder(name, phone, district, thana, address, payment, coupon)
                  },
                  onShowToast = { viewModel.emitToast(it) }
                )
              }

              if (selectedOrderInvoice != null) {
                InvoiceDialog(
                  order = selectedOrderInvoice!!,
                  config = storeConfig,
                  onDismiss = { viewModel.closeInvoice() }
                )
              }
            }
          }
        }
      }
    }
  }
}
