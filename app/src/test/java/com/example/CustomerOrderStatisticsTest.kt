package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.StoreConfig
import com.example.ui.components.CustomerAccountDialog
import com.example.ui.components.CustomerOrderStatisticsCard
import com.example.ui.components.OrderFrequencyRange
import com.example.ui.components.computeCustomerOrderFrequencyBuckets
import com.example.ui.components.computeCustomerOrderSummaryStats
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp-xxhdpi")
class CustomerOrderStatisticsTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private fun fixedNowMillis(): Long {
    return Calendar.getInstance().apply {
      set(2026, Calendar.NOVEMBER, 24, 12, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }.timeInMillis
  }

  private fun sampleOrder(
    id: String,
    date: String,
    total: Int,
    status: String = "Delivered"
  ): Order = Order(
    id = id,
    customerName = "সুমাইয়া আক্তার",
    phone = "01711223344",
    district = "Dhaka",
    thana = "Banani",
    address = "Road 11, Banani",
    items = listOf(OrderItem("p1", "রয়েল জামদানি", 1, total)),
    subtotal = total,
    deliveryCharge = 0,
    discount = 0,
    total = total,
    paymentMethod = "bKash",
    status = status,
    date = date
  )

  @Test
  fun computeBucketsAndStats_accuratelyAggregatesMonthlyAndWeeklyFrequency() {
    val nowMs = fixedNowMillis()
    val orders = listOf(
      sampleOrder("ORD-1", "2026-09-12", 2500, "Delivered"),
      sampleOrder("ORD-2", "2026-10-05", 3500, "Shipped"),
      sampleOrder("ORD-3", "2026-10-19", 1500, "Cancelled"),
      sampleOrder("ORD-4", "2026-11-22", 4200, "Delivered"),
      sampleOrder("ORD-5", "2026-11-24", 1800, "Pending")
    )

    val monthlyBuckets = computeCustomerOrderFrequencyBuckets(
      orders = orders,
      range = OrderFrequencyRange.MONTHLY,
      nowMillis = nowMs
    )
    assertEquals(6, monthlyBuckets.size)
    // Nov 2026 is the last bucket (index 5)
    assertEquals("2026-11", monthlyBuckets[5].bucketKey)
    assertEquals(2, monthlyBuckets[5].orderCount)
    assertEquals(6000, monthlyBuckets[5].totalSpent)

    // Oct 2026 (index 4): 2 total orders, 1 cancelled excluded from spend
    assertEquals("2026-10", monthlyBuckets[4].bucketKey)
    assertEquals(2, monthlyBuckets[4].orderCount)
    assertEquals(3500, monthlyBuckets[4].totalSpent)

    // Sep 2026 (index 3)
    assertEquals("2026-09", monthlyBuckets[3].bucketKey)
    assertEquals(1, monthlyBuckets[3].orderCount)

    val weeklyBuckets = computeCustomerOrderFrequencyBuckets(
      orders = orders,
      range = OrderFrequencyRange.WEEKLY,
      nowMillis = nowMs
    )
    assertEquals(4, weeklyBuckets.size)
    // This week (Nov 18 - Nov 24) has ORD-4 and ORD-5
    assertEquals(2, weeklyBuckets[3].orderCount)
    assertEquals(6000, weeklyBuckets[3].totalSpent)

    val stats = computeCustomerOrderSummaryStats(orders, monthlyBuckets)
    assertEquals(5, stats.totalOrders)
    assertEquals(4, stats.completedOrActiveOrders)
    assertEquals(12000, stats.totalSpent)
    assertEquals(3000, stats.averageOrderValue)
  }

  @Test
  fun statisticsCard_displaysEmptyStateWhenCustomerHasNoOrders() {
    composeTestRule.setContent {
      CustomerOrderStatisticsCard(
        orders = emptyList(),
        nowMillis = fixedNowMillis()
      )
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("customer_order_statistics_card").assertIsDisplayed()
    composeTestRule.onNodeWithText("Customer Order Statistics").assertIsDisplayed()
    composeTestRule.onNodeWithTag("order_statistics_empty_state").assertIsDisplayed()
    composeTestRule.onNodeWithTag("order_frequency_bar_chart").assertDoesNotExist()
  }

  @Test
  fun customerAccountDialog_rendersOrderStatisticsCardAndSupportsBarAndRangeSelection() {
    val nowMs = fixedNowMillis()
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val calNov = Calendar.getInstance().apply { timeInMillis = nowMs }
    val calOct = Calendar.getInstance().apply {
      timeInMillis = nowMs
      add(Calendar.MONTH, -1)
    }

    val orders = listOf(
      sampleOrder("ORD-201", fmt.format(calNov.time), 3200, "Delivered"),
      sampleOrder("ORD-202", fmt.format(calNov.time), 1800, "Pending"),
      sampleOrder("ORD-203", fmt.format(calOct.time), 2750, "Shipped")
    )

    composeTestRule.setContent {
      CustomerAccountDialog(
        authUser = null,
        isAuthBusy = false,
        orders = orders,
        storeConfig = StoreConfig(),
        onGoogleAuthClick = {},
        onOpenOrderInvoice = {},
        onDismiss = {}
      )
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("customer_order_statistics_card").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Customer Order Statistics").assertIsDisplayed()
    composeTestRule.onNodeWithTag("order_frequency_bar_chart").performScrollTo().assertIsDisplayed()

    // Tap October bar (index 4 in 6-month series) and verify tooltip reflects 1 order & ৳2750
    composeTestRule.onNodeWithTag("order_stat_bar_4").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("order_stat_active_tooltip")
      .assertTextContains("1 টি অর্ডার", substring = true)

    // Switch to 4-week range and verify weekly bucket count (4 bars instead of 6)
    composeTestRule.onNodeWithTag("order_stat_range_weekly").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("order_stat_bar_3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("order_stat_bar_5").assertDoesNotExist()
  }
}
