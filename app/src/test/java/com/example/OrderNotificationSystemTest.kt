package com.example

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatusNotification
import com.example.data.repository.StoreRepository
import com.example.notifications.OrderNotificationHelper
import com.example.ui.components.OrderNotificationsDialog
import com.example.ui.components.OrderStatusAlertBanner
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp-xxhdpi")
class OrderNotificationSystemTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    context.getSharedPreferences("sajgoj_store_prefs", Context.MODE_PRIVATE).edit().clear().commit()
    context.getSharedPreferences("sajgoj_fcm_prefs", Context.MODE_PRIVATE).edit().clear().commit()
  }

  @Test
  fun storeRepository_updateOrderStatusEmitsNotificationAndAvoidsDuplicates() = runBlocking {
    val repo = StoreRepository(context)
    // Seeded initial orders should not emit spurious alerts on startup
    assertTrue("Initial orderNotifications should be empty", repo.orderNotifications.value.isEmpty())

    val pendingOrder = repo.orders.value.firstOrNull { it.status.equals("Pending", ignoreCase = true) }
      ?: repo.orders.value.first()
    val originalStatus = pendingOrder.status
    val targetStatus = if (originalStatus == "Shipped") "Delivered" else "Shipped"

    val deferredAlert = async { repo.latestStatusAlert.first() }
    repo.updateOrderStatus(pendingOrder.id, targetStatus)
    val emitted = deferredAlert.await()

    assertEquals(pendingOrder.id, emitted.orderId)
    assertEquals(originalStatus, emitted.previousStatus)
    assertEquals(targetStatus, emitted.newStatus)
    assertFalse(emitted.read)
    assertEquals(1, repo.orderNotifications.value.size)

    // Repeating the same status must not create a duplicate notification
    repo.updateOrderStatus(pendingOrder.id, targetStatus)
    assertEquals(1, repo.orderNotifications.value.size)
  }

  @Test
  fun storeRepository_dispatchToCourierIncludesTrackingAndPartnerInNotification() {
    val repo = StoreRepository(context)
    val pendingOrder = repo.orders.value.firstOrNull { it.status == "Pending" } ?: repo.orders.value.first()

    val trackingId = repo.dispatchToCourier(pendingOrder.id, "SteadFast")

    val latest = repo.orderNotifications.value.firstOrNull()
    assertTrue("Notification should be created on dispatch", latest != null)
    assertEquals("Shipped", latest?.newStatus)
    assertEquals(trackingId, latest?.trackingId)
    assertEquals("SteadFast", latest?.deliveredBy)
    assertTrue(latest?.message?.contains(trackingId) == true)
  }

  @Test
  fun storeRepository_markReadAndClearNotificationsUpdatesUnreadState() {
    val repo = StoreRepository(context)
    val order1 = repo.orders.value[0]
    val order2 = repo.orders.value[1]

    repo.updateOrderStatus(order1.id, "Confirmed")
    repo.updateOrderStatus(order2.id, "Shipped")

    val notifications = repo.orderNotifications.value
    assertEquals(2, notifications.size)
    assertEquals(2, notifications.count { !it.read })

    repo.markOrderNotificationRead(notifications.first().id)
    assertEquals(1, repo.orderNotifications.value.count { !it.read })

    repo.markAllOrderNotificationsRead()
    assertEquals(0, repo.orderNotifications.value.count { !it.read })

    repo.clearOrderNotifications()
    assertTrue(repo.orderNotifications.value.isEmpty())
  }

  @Test
  fun helper_buildStatusChangeNotificationGeneratesLocalizedMessages() {
    val sample = Order(
      id = "ORD-7788",
      customerName = "তানিয়া আক্তার",
      phone = "01700000000",
      district = "Dhaka",
      thana = "Banani",
      address = "Road 11, Banani",
      items = listOf(OrderItem(productId = "p1", name = "জামদানি", quantity = 1, price = 2500)),
      subtotal = 2500,
      deliveryCharge = 80,
      discount = 0,
      total = 2580,
      paymentMethod = "COD",
      status = "Delivered",
      date = "2026-11-24",
      trackingId = "PTH-445566",
      deliveredBy = "Pathao"
    )

    val shipped = OrderNotificationHelper.buildStatusChangeNotification(
      order = sample.copy(status = "Shipped"),
      previousStatus = "Pending"
    )
    assertTrue(shipped.title.contains("ORD-7788"))
    assertTrue(shipped.message.contains("Pathao"))
    assertTrue(shipped.message.contains("PTH-445566"))

    val delivered = OrderNotificationHelper.buildStatusChangeNotification(
      order = sample,
      previousStatus = "Shipped"
    )
    assertTrue(delivered.title.contains("ডেলিভারি সম্পন্ন"))

    val cancelled = OrderNotificationHelper.buildStatusChangeNotification(
      order = sample.copy(status = "Cancelled"),
      previousStatus = "Confirmed"
    )
    assertTrue(cancelled.title.contains("বাতিল"))
  }

  @Test
  fun orderStatusAlertBanner_andInboxDialog_handleInteractions() {
    val notif = OrderStatusNotification(
      id = "n1",
      orderId = "ORD-1024",
      customerName = "আয়েশা",
      previousStatus = "Pending",
      newStatus = "Shipped",
      title = "🚚 অর্ডার #ORD-1024 শিপড হয়েছে!",
      message = "আপনার অর্ডারটি কুরিয়ারে পাঠানো হয়েছে।",
      trackingId = "ST-900112",
      deliveredBy = "SteadFast",
      channel = "fcm",
      read = false,
      createdAtMs = 1_795_000_000_000L
    )

    var bannerAlert by mutableStateOf<OrderStatusNotification?>(notif)
    var viewedOrderId = ""
    var markAllReadClicked = false
    var pushRequested = false

    composeTestRule.setContent {
      OrderStatusAlertBanner(
        notification = bannerAlert,
        onViewOrder = { viewedOrderId = it.orderId },
        onDismiss = { bannerAlert = null }
      )
      OrderNotificationsDialog(
        notifications = listOf(notif),
        hasPushPermission = false,
        fcmTokenPresent = false,
        onRequestPushPermission = { pushRequested = true },
        onNotificationClick = { viewedOrderId = it.orderId },
        onMarkAllRead = { markAllReadClicked = true },
        onClearAll = {},
        onDismiss = {}
      )
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("order_status_alert_banner").assertIsDisplayed()
    composeTestRule.onNodeWithTag("order_alert_banner_view_btn").performClick()
    assertEquals("ORD-1024", viewedOrderId)

    composeTestRule.onNodeWithTag("order_alert_banner_dismiss_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("order_status_alert_banner").assertDoesNotExist()

    composeTestRule.onNodeWithTag("order_notifications_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("enable_push_notifications_btn").performClick()
    assertTrue(pushRequested)

    composeTestRule.onNodeWithTag("mark_all_notifications_read_btn").performClick()
    assertTrue(markAllReadClicked)
  }
}
