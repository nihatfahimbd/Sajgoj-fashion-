package com.example

import com.example.data.BangladeshLocations
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.ShippingInfoConfig
import com.example.ui.components.OrderDateRangePreset
import com.example.ui.components.SellerOrderFilterHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BangladeshLocationsAndOrdersTest {

  @Test
  fun testAll64DistrictsPresentWithSerialThanas() {
    assertEquals(64, BangladeshLocations.districts.size)
    val uniqueNames = BangladeshLocations.districts.map { it.nameEn }.toSet()
    assertEquals(64, uniqueNames.size)
    BangladeshLocations.districts.forEach { dist ->
      assertTrue("${dist.nameEn} must have thanas", dist.thanas.isNotEmpty())
    }
  }

  @Test
  fun testDistrictAndThanaSearchInEnglishAndBengali() {
    val chandpurByEn = BangladeshLocations.searchDistricts("Chandpur")
    assertTrue(chandpurByEn.any { it.nameEn == "Chandpur" })

    val chandpurByBn = BangladeshLocations.searchDistricts("চাঁদপুর")
    assertTrue(chandpurByBn.any { it.nameEn == "Chandpur" })

    val byThana = BangladeshLocations.searchDistricts("হাজীগঞ্জ")
    assertTrue(byThana.any { it.nameEn == "Chandpur" })

    val chandpurThanas = BangladeshLocations.getThanasForDistrict("চাঁদপুর (Chandpur)")
    assertTrue(chandpurThanas.contains("চাঁদপুর সদর"))
    assertTrue(chandpurThanas.contains("হাজীগঞ্জ"))
  }

  @Test
  fun testExpressDeliveryExclusiveToChandpur() {
    assertTrue(BangladeshLocations.isChandpurDistrict("Chandpur"))
    assertTrue(BangladeshLocations.isChandpurDistrict("চাঁদপুর (Chandpur)"))
    assertFalse(BangladeshLocations.isChandpurDistrict("Dhaka"))
    assertFalse(BangladeshLocations.isChandpurDistrict("Cumilla"))

    val config = ShippingInfoConfig()
    // Standard in Chandpur -> 60 BDT
    val chandpurStandardFee = BangladeshLocations.calculateShippingFee(
      subtotal = 1500,
      district = "Chandpur",
      deliveryType = "Standard",
      shippingInfo = config
    )
    assertEquals(60, chandpurStandardFee)

    // Express requested in Chandpur -> Express fee (200 BDT)
    val chandpurExpressFee = BangladeshLocations.calculateShippingFee(
      subtotal = 1500,
      district = "Chandpur",
      deliveryType = "Express",
      shippingInfo = config
    )
    assertEquals(200, chandpurExpressFee)

    // Express requested outside Chandpur -> falls back to Standard fee (120 BDT)
    val cumillaAttemptExpressFee = BangladeshLocations.calculateShippingFee(
      subtotal = 1500,
      district = "Cumilla",
      deliveryType = "Express",
      shippingInfo = config
    )
    assertEquals(120, cumillaAttemptExpressFee)

    // Standard above freeAbove (2000) -> 0
    val dhakaFreeStandardFee = BangladeshLocations.calculateShippingFee(
      subtotal = 2500,
      district = "Dhaka",
      deliveryType = "Standard",
      shippingInfo = config
    )
    assertEquals(0, dhakaFreeStandardFee)
  }

  private fun sampleOrder(
    id: String,
    status: String = "Pending",
    orderType: String = "Normal",
    deliveryType: String = "Standard",
    deliveredBy: String = "SteadFast",
    createdAtMs: Long = System.currentTimeMillis(),
    dateStr: String = "2026-11-20"
  ) = Order(
    id = id,
    customerName = "Test Customer",
    phone = "01800000000",
    district = "Chandpur",
    thana = "হাজীগঞ্জ",
    address = "Main Road",
    items = listOf(OrderItem("p1", "Test Sharee", 1, 1500)),
    subtotal = 1500,
    deliveryCharge = 100,
    discount = 0,
    total = 1600,
    paymentMethod = "COD",
    status = status,
    date = dateStr,
    orderType = orderType,
    deliveryType = deliveryType,
    deliveredBy = deliveredBy,
    createdAt = createdAtMs
  )

  @Test
  fun testSellerOrderTabMapping() {
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("1", status = "Pending"), "TO_SHIP"))
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("2", status = "Confirmed"), "TO_SHIP"))
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("3", status = "To Ship"), "TO_SHIP"))
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("4", status = "Shipped"), "SHIPPING"))
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("5", status = "Shipping"), "SHIPPING"))
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("6", status = "Delivered"), "DELIVERED"))
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("7", status = "Failed Delivered"), "FAILED_DELIVERED"))
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("8", status = "Cancelled"), "CANCELLATION"))
    assertTrue(SellerOrderFilterHelper.matchesTab(sampleOrder("9", status = "Cancellation"), "CANCELLATION"))
  }

  @Test
  fun testMultiYearDatePresetsAndCustomRange() {
    val dayMs = 24L * 3600L * 1000L
    val nowMs = 1_800_000_000_000L

    assertTrue(SellerOrderFilterHelper.matchesDatePreset(nowMs - 3L * dayMs, OrderDateRangePreset.LAST_7_DAYS, null, null, nowMs))
    assertFalse(SellerOrderFilterHelper.matchesDatePreset(nowMs - 10L * dayMs, OrderDateRangePreset.LAST_7_DAYS, null, null, nowMs))
    assertTrue(SellerOrderFilterHelper.matchesDatePreset(nowMs - 60L * dayMs, OrderDateRangePreset.MONTHS_1_TO_3, null, null, nowMs))
    assertTrue(SellerOrderFilterHelper.matchesDatePreset(nowMs - 120L * dayMs, OrderDateRangePreset.MONTHS_3_TO_6, null, null, nowMs))
    assertTrue(SellerOrderFilterHelper.matchesDatePreset(nowMs - 220L * dayMs, OrderDateRangePreset.MONTHS_6_TO_9, null, null, nowMs))
    assertTrue(SellerOrderFilterHelper.matchesDatePreset(nowMs - 500L * dayMs, OrderDateRangePreset.YEARS_1_TO_2, null, null, nowMs))
    assertTrue(SellerOrderFilterHelper.matchesDatePreset(nowMs - 900L * dayMs, OrderDateRangePreset.YEARS_2_TO_3, null, null, nowMs))

    val start2024 = SellerOrderFilterHelper.parseDateInput("2024-01-01", endOfDay = false)!!
    val end2024 = SellerOrderFilterHelper.parseDateInput("2024-12-31", endOfDay = true)!!
    val mid2024 = SellerOrderFilterHelper.parseDateInput("2024-06-15", endOfDay = false)!!
    assertTrue(SellerOrderFilterHelper.matchesDatePreset(mid2024, OrderDateRangePreset.CUSTOM, start2024, end2024, nowMs))
  }

  @Test
  fun testSellerOrderMultiFacetFilteringAndSorting() {
    val nowMs = 1_800_000_000_000L
    val o1 = sampleOrder("ORD-1", status = "Pending", orderType = "Normal", deliveryType = "Standard", deliveredBy = "Pathao", createdAtMs = nowMs - 1000L)
    val o2 = sampleOrder("ORD-2", status = "Pending", orderType = "Custom", deliveryType = "Express", deliveredBy = "SteadFast", createdAtMs = nowMs - 500L)
    val o3 = sampleOrder("ORD-3", status = "Delivered", orderType = "Custom", deliveryType = "Express", deliveredBy = "Digital", createdAtMs = nowMs)

    val customExpress = SellerOrderFilterHelper.filterAndSortOrders(
      orders = listOf(o1, o2, o3),
      tabKey = "ALL",
      searchQuery = "",
      newestFirst = true,
      datePreset = OrderDateRangePreset.ALL,
      customStartMs = null,
      customEndMs = null,
      orderTypeFilter = "Custom",
      deliveryTypeFilter = "Express",
      deliveredByFilter = "All",
      nowMs = nowMs
    )
    assertEquals(listOf("ORD-3", "ORD-2"), customExpress.map { it.id })

    val oldestDigital = SellerOrderFilterHelper.filterAndSortOrders(
      orders = listOf(o1, o2, o3),
      tabKey = "ALL",
      searchQuery = "",
      newestFirst = false,
      datePreset = OrderDateRangePreset.ALL,
      customStartMs = null,
      customEndMs = null,
      orderTypeFilter = "All",
      deliveryTypeFilter = "All",
      deliveredByFilter = "Digital",
      nowMs = nowMs
    )
    assertEquals(listOf("ORD-3"), oldestDigital.map { it.id })
  }
}
