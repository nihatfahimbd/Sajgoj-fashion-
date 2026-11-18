package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.example.data.model.CourierConfig
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.PixelConfig
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.ui.AdminTab
import com.example.ui.components.AdminQrScannerDialog
import com.example.ui.components.buildShippingQrPayload
import com.example.ui.screens.AdminDashboardScreen
import com.example.util.AdminQrLookupHelper
import com.example.util.AdminQrLookupResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AdminQrLookupTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private val sampleProducts = listOf(
    Product(
      id = "p_silk_1",
      name = "রাজশাহী কাতান সিল্ক শাড়ি",
      price = 3450,
      regularPrice = 4200,
      stock = 9,
      category = "শাড়ি",
      imageUrl = "",
      sku = "SJG-SAREE-01",
      description = "Handloom Katan silk saree",
      colors = listOf("মেরুন", "গোল্ডেন"),
      sizes = listOf("Free Size")
    ),
    Product(
      id = "p_panjabi_2",
      name = "প্রিমিয়াম কটন পাঞ্জাবি",
      price = 1650,
      stock = 4,
      category = "পাঞ্জাবি",
      imageUrl = "",
      sku = "SJG-PNJ-202",
      description = "Cotton embroidery panjabi"
    )
  )

  private val sampleOrders = listOf(
    Order(
      id = "ORD-501",
      customerName = "তাসনিম তাবাসসুম",
      phone = "01711223344",
      district = "ঢাকা",
      thana = "ধানমন্ডি",
      address = "বাড়ি ১৪, রোড ৬",
      items = listOf(OrderItem("p_silk_1", "রাজশাহী কাতান সিল্ক শাড়ি", 1, 3450)),
      subtotal = 3450,
      deliveryCharge = 70,
      discount = 0,
      total = 3520,
      paymentMethod = "Cash on Delivery",
      status = "Shipped",
      date = "2026-11-18",
      trackingId = "STF-998877"
    )
  )

  @Test
  fun lookup_resolvesShippingLabelJsonOrderIdTrackingPhoneAndProductSku() {
    val labelQr = buildShippingQrPayload(sampleOrders.first(), StoreConfig())

    val fromShippingQr = AdminQrLookupHelper.lookup(labelQr, sampleProducts, sampleOrders)
    assertTrue(fromShippingQr is AdminQrLookupResult.OrderMatch)
    assertEquals("ORD-501", (fromShippingQr as AdminQrLookupResult.OrderMatch).order.id)

    val fromTracking = AdminQrLookupHelper.lookup("stf-998877", sampleProducts, sampleOrders)
    assertTrue(fromTracking is AdminQrLookupResult.OrderMatch)
    assertEquals("ORD-501", (fromTracking as AdminQrLookupResult.OrderMatch).order.id)

    val fromOrderHash = AdminQrLookupHelper.lookup("#ORD-501", sampleProducts, sampleOrders)
    assertTrue(fromOrderHash is AdminQrLookupResult.OrderMatch)
    assertEquals("ORD-501", (fromOrderHash as AdminQrLookupResult.OrderMatch).order.id)

    val fromPhone = AdminQrLookupHelper.lookup("01711223344", sampleProducts, sampleOrders)
    assertTrue(fromPhone is AdminQrLookupResult.OrderMatch)
    assertEquals("ORD-501", (fromPhone as AdminQrLookupResult.OrderMatch).order.id)

    val fromSku = AdminQrLookupHelper.lookup("SJG-SAREE-01", sampleProducts, sampleOrders)
    assertTrue(fromSku is AdminQrLookupResult.ProductMatch)
    assertEquals("p_silk_1", (fromSku as AdminQrLookupResult.ProductMatch).product.id)

    val fromDeepLink = AdminQrLookupHelper.lookup("https://sajgoj.shop/#product-p_panjabi_2", sampleProducts, sampleOrders)
    assertTrue(fromDeepLink is AdminQrLookupResult.ProductMatch)
    assertEquals("p_panjabi_2", (fromDeepLink as AdminQrLookupResult.ProductMatch).product.id)

    val notFound = AdminQrLookupHelper.lookup("UNKNOWN-CODE-999", sampleProducts, sampleOrders)
    assertTrue(notFound is AdminQrLookupResult.NotFound)
  }

  @Test
  fun adminQrScannerDialog_displaysProductAndOrderLookupCards() {
    var editedProductId = ""
    var viewedInvoiceOrderId = ""

    composeTestRule.setContent {
      AdminQrScannerDialog(
        products = sampleProducts,
        orders = sampleOrders,
        onDismiss = {},
        onEditProduct = { editedProductId = it.id },
        onViewInvoice = { viewedInvoiceOrderId = it.id },
        onOpenShippingLabel = {},
        onUpdateOrderStatus = { _, _ -> },
        onShowToast = {}
      )
    }

    composeTestRule.onNodeWithTag("admin_qr_scanner_dialog").assertIsDisplayed()

    // Lookup Product by SKU
    composeTestRule.onNodeWithTag("admin_qr_manual_input").performTextInput("SJG-SAREE-01")
    composeTestRule.onNodeWithTag("admin_qr_lookup_button").performClick()

    composeTestRule.onNodeWithTag("admin_qr_result_card").assertIsDisplayed()
    composeTestRule.onNodeWithText("রাজশাহী কাতান সিল্ক শাড়ি", substring = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("admin_qr_edit_product_button").performClick()
    assertEquals("p_silk_1", editedProductId)

    // Lookup Order by Tracking ID
    composeTestRule.onNodeWithTag("admin_qr_manual_input").performScrollTo().performTextReplacement("STF-998877")
    composeTestRule.onNodeWithTag("admin_qr_lookup_button").performScrollTo().performClick()

    composeTestRule.onNodeWithTag("admin_qr_result_card").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("অর্ডার #ORD-501 • ৳3520").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("তাসনিম তাবাসসুম (01711223344)").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("admin_qr_view_invoice_button").performScrollTo().performClick()
    assertEquals("ORD-501", viewedInvoiceOrderId)
  }

  @Test
  fun adminDashboardTopBarButton_opensQrScannerDialog() {
    composeTestRule.setContent {
      AdminDashboardScreen(
        currentTab = AdminTab.DASHBOARD,
        onSelectTab = {},
        isAdminLoggedIn = true,
        onLogin = { true },
        onLogout = {},
        products = sampleProducts,
        categories = emptyList(),
        orders = sampleOrders,
        coupons = emptyList(),
        banners = emptyList(),
        storeConfig = StoreConfig(),
        courierConfig = CourierConfig(),
        pixelConfig = PixelConfig(),
        onAddProduct = {},
        onUpdateProduct = {},
        onDeleteProduct = {},
        onUpdateOrderStatus = { _, _ -> },
        onDispatchCourier = { _, _ -> },
        onViewInvoice = {},
        onAddCategory = {},
        onDeleteCategory = {},
        onAddCoupon = {},
        onToggleCoupon = {},
        onDeleteCoupon = {},
        onAddBanner = {},
        onToggleBanner = {},
        onDeleteBanner = {},
        onUpdateStoreConfig = {},
        onUpdateCourierConfig = {},
        onUpdatePixelConfig = {},
        onResetDefaults = {},
        onShowToast = {}
      )
    }

    composeTestRule.onNodeWithTag("admin_topbar_qr_scanner_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("admin_topbar_qr_scanner_button").performClick()
    composeTestRule.onNodeWithTag("admin_qr_scanner_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("admin_qr_close_button").performClick()
    composeTestRule.onNodeWithTag("admin_qr_scanner_dialog").assertDoesNotExist()
  }
}
