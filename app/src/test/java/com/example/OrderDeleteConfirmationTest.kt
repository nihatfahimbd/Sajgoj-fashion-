package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CourierConfig
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.StoreConfig
import com.example.data.repository.StoreRepository
import com.example.ui.components.AdminSellerOrdersHub
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp-xxhdpi")
class OrderDeleteConfirmationTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private fun sampleOrder(id: String = "ORD-9001", name: String = "রাহিমা খাতুন"): Order = Order(
    id = id,
    customerName = name,
    phone = "01711223344",
    district = "Dhaka",
    thana = "Dhanmondi",
    address = "House 12, Road 5",
    items = listOf(
      OrderItem(
        productId = "p1",
        name = "রয়েল জামদানি শাড়ি",
        quantity = 1,
        price = 3500
      )
    ),
    subtotal = 3500,
    deliveryCharge = 120,
    discount = 0,
    total = 3620,
    paymentMethod = "COD",
    status = "Pending",
    date = "2026-11-24",
    createdAt = 1_795_000_000_000L
  )

  @Test
  fun deleteOrderFromAdminHub_requiresConfirmationDialogBeforeDeleting() {
    val order = sampleOrder()
    val deletedOrderIds = mutableListOf<String>()

    composeTestRule.setContent {
      AdminSellerOrdersHub(
        orders = listOf(order),
        products = emptyList(),
        returns = emptyList(),
        storeConfig = StoreConfig(),
        courierConfig = CourierConfig(),
        onUpdateStatus = { _, _ -> },
        onDispatchCourier = { _, _ -> },
        onViewInvoice = {},
        onOpenShippingLabel = {},
        onOpenReturn = {},
        onViewReturnSlip = {},
        onUpdateCourierConfig = {},
        onShowToast = {},
        onDeleteOrder = { deletedOrderIds.add(it) }
      )
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("delete_order_confirmation_dialog").assertDoesNotExist()

    // Open three-dot menu on order card and select Delete
    composeTestRule.onNodeWithTag("order_three_dot_${order.id}").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("order_delete_menu_item_${order.id}").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Modal confirmation dialog must be visible, and order must NOT be deleted yet
    composeTestRule.onNodeWithTag("delete_order_confirmation_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithText("অর্ডারটি স্থায়ীভাবে মুছে ফেলবেন?").assertIsDisplayed()
    composeTestRule.onNodeWithText("অর্ডার নাম্বার: #${order.id}").assertIsDisplayed()
    assertTrue("Order must not be deleted before confirming", deletedOrderIds.isEmpty())

    // Cancel must dismiss without deleting
    composeTestRule.onNodeWithTag("cancel_delete_order_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("delete_order_confirmation_dialog").assertDoesNotExist()
    assertTrue("Cancelling must not delete the order", deletedOrderIds.isEmpty())

    // Re-open menu -> Delete -> Confirm
    composeTestRule.onNodeWithTag("order_three_dot_${order.id}").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("order_delete_menu_item_${order.id}").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("confirm_delete_order_btn").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("delete_order_confirmation_dialog").assertDoesNotExist()
    assertEquals(listOf(order.id), deletedOrderIds)
  }

  @Test
  fun storeRepository_deleteOrderRemovesTargetOrderAndKeepsOthers() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context.getSharedPreferences("sajgoj_store_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    val repo = StoreRepository(context)
    val initialOrders = repo.orders.value
    assertTrue("Expected seeded orders to be non-empty", initialOrders.size >= 2)

    val targetId = initialOrders.first().id
    val remainingId = initialOrders[1].id

    repo.deleteOrder(targetId)

    val updatedIds = repo.orders.value.map { it.id }
    assertFalse("Deleted order should be removed", updatedIds.contains(targetId))
    assertTrue("Other orders should remain intact", updatedIds.contains(remainingId))
  }
}
