package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourierConfig
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ReturnRecord
import com.example.data.model.StoreConfig
import com.example.sms.SimSmsGatewayManager
import com.example.sms.SmsHelper
import com.example.ui.screens.AdminCourierTab
import com.example.ui.screens.AdminReturnsTab
import com.example.ui.screens.AdminShippingLabelsTab
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val DeepSkyTabStart = Color(0xFF0284C7)
private val DeepSkyTabEnd = Color(0xFF0369A1)
private val SellerSurfaceBg = Color(0xFFF3F6FA)
private val SellerOrangeAccent = Color(0xFFEA580C)

enum class OrderDateRangePreset(val labelBn: String, val shortLabel: String) {
  ALL("সকল অর্ডার (All Time)", "Newest Order Created"),
  TODAY("আজকের অর্ডার (Today)", "Today"),
  LAST_7_DAYS("লাস্ট ৭ দিনের অর্ডার (Last 7 Days)", "Last 7 Days"),
  LAST_15_DAYS("লাস্ট ১৫ দিনের অর্ডার (Last 15 Days)", "Last 15 Days"),
  LAST_1_MONTH("লাস্ট ১ মাসের অর্ডার (Last 1 Month)", "Last 1 Month"),
  MONTHS_1_TO_3("১ থেকে ৩ মাস আগের (1–3 Months)", "1–3 Months"),
  MONTHS_3_TO_6("৩ থেকে ৬ মাস আগের (3–6 Months)", "3–6 Months"),
  MONTHS_6_TO_9("৬ থেকে ৯ মাস আগের (6–9 Months)", "6–9 Months"),
  LAST_1_YEAR("১ বছর পর্যন্ত (Last 1 Year)", "Last 1 Year"),
  YEARS_1_TO_2("১ থেকে ২ বছর আগের (1–2 Years)", "1–2 Years"),
  YEARS_2_TO_3("২ থেকে ৩ বছর আগের (2–3 Years)", "2–3 Years"),
  CUSTOM("কাস্টম তারিখ সিলেক্ট (Custom Range)", "Custom Date")
}

object SellerOrderFilterHelper {
  private val dateFormats = listOf("yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy", "yyyy/MM/dd")

  fun resolveOrderTimestamp(order: Order): Long {
    if (order.createdAt > 0L) return order.createdAt
    val raw = order.date.trim()
    if (raw.isNotEmpty()) {
      for (fmt in dateFormats) {
        try {
          val parsed = SimpleDateFormat(fmt, Locale.US).parse(raw)?.time
          if (parsed != null) return parsed
        } catch (_: Exception) {
        }
      }
    }
    return System.currentTimeMillis()
  }

  fun parseDateInput(dateStr: String, endOfDay: Boolean = false): Long? {
    val clean = dateStr.trim()
    if (clean.isEmpty()) return null
    for (fmt in dateFormats) {
      try {
        val d = SimpleDateFormat(fmt, Locale.US).apply { isLenient = false }.parse(clean) ?: continue
        val cal = Calendar.getInstance().apply { time = d }
        if (endOfDay) {
          cal.set(Calendar.HOUR_OF_DAY, 23)
          cal.set(Calendar.MINUTE, 59)
          cal.set(Calendar.SECOND, 59)
          cal.set(Calendar.MILLISECOND, 999)
        } else {
          cal.set(Calendar.HOUR_OF_DAY, 0)
          cal.set(Calendar.MINUTE, 0)
          cal.set(Calendar.SECOND, 0)
          cal.set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
      } catch (_: Exception) {
      }
    }
    return null
  }

  fun matchesTab(order: Order, tabKey: String): Boolean {
    val st = order.status.trim().lowercase(Locale.ROOT)
    return when (tabKey) {
      "ALL" -> true
      "TO_SHIP" -> st in setOf("pending", "confirmed", "processing", "to ship", "to_ship")
      "SHIPPING" -> st in setOf("shipped", "shipping", "in transit")
      "DELIVERED" -> st == "delivered"
      "FAILED_DELIVERED" -> st in setOf("failed delivered", "failed delivery", "failed_delivered", "returned")
      "CANCELLATION" -> st in setOf("cancelled", "cancellation", "canceled")
      else -> true
    }
  }

  fun matchesDatePreset(
    orderTs: Long,
    preset: OrderDateRangePreset,
    customStartMs: Long?,
    customEndMs: Long?,
    nowMs: Long = System.currentTimeMillis()
  ): Boolean {
    val dayMs = 24L * 3600L * 1000L
    val ageMs = (nowMs - orderTs).coerceAtLeast(0L)
    return when (preset) {
      OrderDateRangePreset.ALL -> true
      OrderDateRangePreset.TODAY -> ageMs <= dayMs
      OrderDateRangePreset.LAST_7_DAYS -> ageMs <= 7L * dayMs
      OrderDateRangePreset.LAST_15_DAYS -> ageMs <= 15L * dayMs
      OrderDateRangePreset.LAST_1_MONTH -> ageMs <= 30L * dayMs
      OrderDateRangePreset.MONTHS_1_TO_3 -> ageMs in (30L * dayMs)..(90L * dayMs)
      OrderDateRangePreset.MONTHS_3_TO_6 -> ageMs in (90L * dayMs)..(180L * dayMs)
      OrderDateRangePreset.MONTHS_6_TO_9 -> ageMs in (180L * dayMs)..(270L * dayMs)
      OrderDateRangePreset.LAST_1_YEAR -> ageMs <= 365L * dayMs
      OrderDateRangePreset.YEARS_1_TO_2 -> ageMs in (365L * dayMs)..(730L * dayMs)
      OrderDateRangePreset.YEARS_2_TO_3 -> ageMs in (730L * dayMs)..(1095L * dayMs)
      OrderDateRangePreset.CUSTOM -> {
        val afterStart = customStartMs == null || orderTs >= customStartMs
        val beforeEnd = customEndMs == null || orderTs <= customEndMs
        afterStart && beforeEnd
      }
    }
  }

  fun filterAndSortOrders(
    orders: List<Order>,
    tabKey: String,
    searchQuery: String,
    newestFirst: Boolean,
    datePreset: OrderDateRangePreset,
    customStartMs: Long?,
    customEndMs: Long?,
    orderTypeFilter: String,
    deliveryTypeFilter: String,
    deliveredByFilter: String,
    nowMs: Long = System.currentTimeMillis()
  ): List<Order> {
    val q = searchQuery.trim().lowercase(Locale.ROOT)
    val filtered = orders.filter { order ->
      if (!matchesTab(order, tabKey)) return@filter false
      if (!orderTypeFilter.equals("All", true) && !order.orderType.equals(orderTypeFilter, true)) return@filter false
      if (!deliveryTypeFilter.equals("All", true) && !order.deliveryType.equals(deliveryTypeFilter, true)) return@filter false
      if (!deliveredByFilter.equals("All", true) && !order.deliveredBy.equals(deliveredByFilter, true)) return@filter false

      val ts = resolveOrderTimestamp(order)
      if (!matchesDatePreset(ts, datePreset, customStartMs, customEndMs, nowMs)) return@filter false

      if (q.isNotEmpty()) {
        val hay = "${order.id} ${order.customerName} ${order.phone} ${order.trackingId} ${order.district} ${order.thana} ${order.paymentMethod} ${order.items.joinToString { it.name }}"
          .lowercase(Locale.ROOT)
        if (!hay.contains(q)) return@filter false
      }
      true
    }
    return if (newestFirst) {
      filtered.sortedByDescending { resolveOrderTimestamp(it) }
    } else {
      filtered.sortedBy { resolveOrderTimestamp(it) }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSellerOrdersHub(
  orders: List<Order>,
  products: List<Product>,
  returns: List<ReturnRecord>,
  storeConfig: StoreConfig,
  courierConfig: CourierConfig,
  onUpdateStatus: (String, String) -> Unit,
  onDispatchCourier: (String, String) -> Unit,
  onUpdateDeliveredBy: (String, String) -> Unit = { _, _ -> },
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

  var selectedSellerTab by remember { mutableStateOf("ALL") }
  var showSearchBar by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }

  var newestFirst by remember { mutableStateOf(true) }
  var datePreset by remember { mutableStateOf(OrderDateRangePreset.ALL) }
  var showDateSortMenu by remember { mutableStateOf(false) }
  var showCustomDateDialog by remember { mutableStateOf(false) }
  var customStartInput by remember { mutableStateOf("") }
  var customEndInput by remember { mutableStateOf("") }

  var showFilterSheet by remember { mutableStateOf(false) }
  var orderTypeFilter by remember { mutableStateOf("All") }
  var deliveryTypeFilter by remember { mutableStateOf("All") }
  var deliveredByFilter by remember { mutableStateOf("All") }

  var showHeaderMoreMenu by remember { mutableStateOf(false) }
  var utilityView by remember { mutableStateOf<String?>(null) } // "DELIVERY_TRACKING", "COURIER", "RETURNS", "SHIPPING_LABELS"

  val customStartMs = remember(customStartInput) { SellerOrderFilterHelper.parseDateInput(customStartInput, endOfDay = false) }
  val customEndMs = remember(customEndInput) { SellerOrderFilterHelper.parseDateInput(customEndInput, endOfDay = true) }

  val cancellationCount = remember(orders) {
    orders.count { SellerOrderFilterHelper.matchesTab(it, "CANCELLATION") }
  }

  val filteredOrders = remember(
    orders, selectedSellerTab, searchQuery, newestFirst, datePreset,
    customStartMs, customEndMs, orderTypeFilter, deliveryTypeFilter, deliveredByFilter
  ) {
    SellerOrderFilterHelper.filterAndSortOrders(
      orders = orders,
      tabKey = selectedSellerTab,
      searchQuery = searchQuery,
      newestFirst = newestFirst,
      datePreset = datePreset,
      customStartMs = customStartMs,
      customEndMs = customEndMs,
      orderTypeFilter = orderTypeFilter,
      deliveryTypeFilter = deliveryTypeFilter,
      deliveredByFilter = deliveredByFilter
    )
  }

  val activeFilterCount = listOf(orderTypeFilter, deliveryTypeFilter, deliveredByFilter).count { !it.equals("All", true) }

  if (utilityView != null) {
    Column(modifier = Modifier.fillMaxSize()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = { utilityView = null },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.heightIn(min = 48.dp)
        ) {
          Text("← Orders তালিকায় ফিরুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(
          text = when (utilityView) {
            "RETURNS" -> "রিটার্ন ও এক্সচেঞ্জ"
            "COURIER" -> "কুরিয়ার কনফিগারেশন"
            "SHIPPING_LABELS" -> "শিপিং লেবেল ডেস্ক"
            else -> "ডেলিভারি ট্র্যাকিং"
          },
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary
        )
      }
      when (utilityView) {
        "DELIVERY_TRACKING" -> AdminDeliveryTrackingTab(
          orders = orders,
          onUpdateStatus = onUpdateStatus,
          onViewInvoice = onViewInvoice,
          onOpenShippingLabel = onOpenShippingLabel
        )
        "SHIPPING_LABELS" -> AdminShippingLabelsTab(
          shippedOrders = orders.filter { it.status.equals("Shipped", true) || it.status.equals("Shipping", true) },
          storeConfig = storeConfig,
          onOpenShippingLabel = onOpenShippingLabel,
          onGoToAllOrders = { utilityView = null },
          onShowToast = onShowToast
        )
        "COURIER" -> AdminCourierTab(
          config = courierConfig,
          onSave = onUpdateCourierConfig,
          onTest = onShowToast
        )
        "RETURNS" -> AdminReturnsTab(
          returns = returns,
          orders = orders,
          products = products,
          onOpenNewReturn = { onOpenReturn(null) },
          onViewReturnSlip = onViewReturnSlip
        )
      }
    }
    return
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SellerSurfaceBg, RoundedCornerShape(16.dp))
  ) {
    // 1. Orders Header with Title + Scanner Icon & Search Icon at right end (Screenshot 1 point 2)
    Surface(
      color = Color.White,
      shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Orders",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = AdminInkPrimary
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE0F2FE)
          ) {
            Text(
              text = "${filteredOrders.size}",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = DeepSkyTabEnd,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onOpenQrScanner,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("seller_orders_scanner_icon")
          ) {
            Icon(
              imageVector = Icons.Default.QrCodeScanner,
              contentDescription = "QR ও বারকোড স্ক্যানার",
              tint = AdminInkPrimary,
              modifier = Modifier.size(22.dp)
            )
          }

          IconButton(
            onClick = { showSearchBar = !showSearchBar },
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("seller_orders_search_icon")
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "অর্ডার সার্চ করুন",
              tint = if (showSearchBar) DeepSkyTabStart else AdminInkPrimary,
              modifier = Modifier.size(22.dp)
            )
          }

          Box {
            IconButton(
              onClick = { showHeaderMoreMenu = true },
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("seller_orders_more_icon")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "আরও অপশন",
                tint = AdminInkPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            DropdownMenu(
              expanded = showHeaderMoreMenu,
              onDismissRequest = { showHeaderMoreMenu = false }
            ) {
              DropdownMenuItem(
                text = { Text("🏷️ শিপিং লেবেল ডেস্ক") },
                onClick = {
                  showHeaderMoreMenu = false
                  utilityView = "SHIPPING_LABELS"
                }
              )
              DropdownMenuItem(
                text = { Text("📊 ডেলিভারি ট্র্যাকিং") },
                onClick = {
                  showHeaderMoreMenu = false
                  utilityView = "DELIVERY_TRACKING"
                }
              )
              DropdownMenuItem(
                text = { Text("🚚 কুরিয়ার সেটিংস (Pathao/SteadFast)") },
                onClick = {
                  showHeaderMoreMenu = false
                  utilityView = "COURIER"
                }
              )
              DropdownMenuItem(
                text = { Text("🔄 রিটার্ন ও এক্সচেঞ্জ (${returns.size})") },
                onClick = {
                  showHeaderMoreMenu = false
                  utilityView = "RETURNS"
                }
              )
            }
          }
        }
      }
    }

    // Deep Sky TabBar Block (#0284C7 / #0369A1) with Light White Text (Point 1)
    val sellerTabs = listOf(
      "ALL" to "All",
      "TO_SHIP" to "To Ship",
      "SHIPPING" to "Shipping",
      "DELIVERED" to "Delivered",
      "FAILED_DELIVERED" to "Failed Delivered",
      "CANCELLATION" to "Cancellation"
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.horizontalGradient(listOf(DeepSkyTabStart, DeepSkyTabEnd)))
        .testTag("seller_orders_deep_sky_tabbar")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        sellerTabs.forEach { (key, label) ->
          val selected = selectedSellerTab == key
          Column(
            modifier = Modifier
              .heightIn(min = 48.dp)
              .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
              .clickable { selectedSellerTab = key }
              .padding(horizontal = 12.dp, vertical = 10.dp)
              .testTag("seller_tab_$key"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (selected) Color.White else Color(0xFFF0F9FF),
                maxLines = 1
              )
              if (key == "CANCELLATION" && cancellationCount > 0) {
                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFDC2626))
                    .padding(horizontal = 6.dp, vertical = 1.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "$cancellationCount",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Box(
              modifier = Modifier
                .width(if (selected) 34.dp else 0.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) Color.White else Color.Transparent)
            )
          }
        }
      }
    }

    // Expandable Search Input
    AnimatedVisibility(visible = showSearchBar) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Order ID, ফোন নম্বর, নাম বা প্রোডাক্ট লিখুন...", fontSize = 12.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DeepSkyTabStart, modifier = Modifier.size(18.dp)) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "মুছুন", modifier = Modifier.size(16.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // 2. Order Created Dropdown + Filter Row (Screenshot 1 & 2 points 3 & 5)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF8FAFC))
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box {
        Row(
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { showDateSortMenu = true }
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .testTag("order_created_dropdown_trigger"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          val sortTitle = when {
            datePreset != OrderDateRangePreset.ALL -> datePreset.shortLabel
            newestFirst -> "Newest Order Created"
            else -> "Oldest Order Created"
          }
          Text(
            text = sortTitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "তারিখ ও ক্রম নির্বাচন",
            tint = AdminInkPrimary,
            modifier = Modifier.size(18.dp)
          )
        }

        DropdownMenu(
          expanded = showDateSortMenu,
          onDismissRequest = { showDateSortMenu = false },
          modifier = Modifier.background(Color.White)
        ) {
          DropdownMenuItem(
            text = {
              Text(
                text = if (newestFirst) "✓ Newest Order Created (নতুন আগে)" else "Newest Order Created (নতুন আগে)",
                fontWeight = if (newestFirst) FontWeight.Bold else FontWeight.Normal,
                color = if (newestFirst) DeepSkyTabEnd else AdminInkPrimary
              )
            },
            onClick = {
              newestFirst = true
              showDateSortMenu = false
            }
          )
          DropdownMenuItem(
            text = {
              Text(
                text = if (!newestFirst) "✓ Oldest Order Created (পুরনো আগে)" else "Oldest Order Created (পুরনো আগে)",
                fontWeight = if (!newestFirst) FontWeight.Bold else FontWeight.Normal,
                color = if (!newestFirst) DeepSkyTabEnd else AdminInkPrimary
              )
            },
            onClick = {
              newestFirst = false
              showDateSortMenu = false
            }
          )
          HorizontalDivider(color = AdminCardBorderSoft)
          OrderDateRangePreset.values().forEach { preset ->
            DropdownMenuItem(
              text = {
                Text(
                  text = (if (datePreset == preset) "● " else "") + preset.labelBn,
                  fontSize = 12.5.sp,
                  fontWeight = if (datePreset == preset) FontWeight.Bold else FontWeight.Normal,
                  color = if (datePreset == preset) DeepSkyTabEnd else AdminInkPrimary
                )
              },
              onClick = {
                datePreset = preset
                showDateSortMenu = false
                if (preset == OrderDateRangePreset.CUSTOM) {
                  showCustomDateDialog = true
                }
              }
            )
          }
        }
      }

      Row(
        modifier = Modifier
          .heightIn(min = 48.dp)
          .clip(RoundedCornerShape(10.dp))
          .clickable { showFilterSheet = true }
          .padding(horizontal = 8.dp, vertical = 6.dp)
          .testTag("seller_orders_filter_button"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        Text(
          text = "Filter",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = if (activeFilterCount > 0) SellerOrangeAccent else AdminInkPrimary
        )
        if (activeFilterCount > 0) {
          Box(
            modifier = Modifier
              .size(18.dp)
              .clip(CircleShape)
              .background(SellerOrangeAccent),
            contentAlignment = Alignment.Center
          ) {
            Text("$activeFilterCount", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
        Icon(
          imageVector = Icons.Default.FilterList,
          contentDescription = "ফিল্টার",
          tint = if (activeFilterCount > 0) SellerOrangeAccent else AdminInkPrimary,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // Active custom date range chip
    if (datePreset == OrderDateRangePreset.CUSTOM && (customStartInput.isNotBlank() || customEndInput.isNotBlank())) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE0F2FE)) {
          Text(
            text = "📅 ${customStartInput.ifBlank { "শুরু" }} → ${customEndInput.ifBlank { "আজ" }}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DeepSkyTabEnd,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        TextButton(onClick = {
          datePreset = OrderDateRangePreset.ALL
          customStartInput = ""
          customEndInput = ""
        }) {
          Text("রিসেট", fontSize = 11.sp)
        }
      }
    }

    // 3. Orders List
    if (filteredOrders.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = "📦", fontSize = 40.sp)
          Text("কোনো অর্ডার পাওয়া যায়নি", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
          Text(
            text = "ট্যাব, তারিখ অথবা ফিল্টার পরিবর্তন করে চেষ্টা করুন।",
            fontSize = 12.sp,
            color = AdminInkMuted
          )
          Button(
            onClick = {
              selectedSellerTab = "ALL"
              datePreset = OrderDateRangePreset.ALL
              orderTypeFilter = "All"
              deliveryTypeFilter = "All"
              deliveredByFilter = "All"
              searchQuery = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = DeepSkyTabStart)
          ) {
            Text("সব ফিল্টার রিসেট করুন")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
      ) {
        items(filteredOrders, key = { it.id }) { order ->
          SellerCenterOrderCard(
            order = order,
            products = products,
            storeName = storeConfig.storeName,
            onCopyOrderId = {
              val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              cb.setPrimaryClip(ClipData.newPlainText("Order Number", order.id))
              onShowToast("Order Number #${order.id} কপি করা হয়েছে!")
            },
            onOpenShippingLabel = { onOpenShippingLabel(order) },
            onViewInvoice = { onViewInvoice(order) },
            onCallCustomer = {
              val dialUri = Uri.parse("tel:${order.phone}")
              runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, dialUri)) }
            },
            onWhatsAppCustomer = {
              val msg = SmsHelper.buildOrderStatusSms(order, storeConfig.storeName)
              val cleanDigits = order.phone.filter(Char::isDigit).let {
                if (it.startsWith("01") && it.length == 11) "88$it" else it
              }
              val waUrl = "https://wa.me/$cleanDigits?text=${URLEncoder.encode(msg, "UTF-8")}"
              runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))) }
            },
            onSmsCustomer = {
              val msg = SmsHelper.buildOrderStatusSms(order, storeConfig.storeName)
              if (storeConfig.smsGatewayDeviceEnabled && SimSmsGatewayManager.hasSendSmsPermission(context)) {
                onEnqueueOrderSms(order)
              } else {
                onEnqueueOrderSms(order)
                SimSmsGatewayManager.launchPlaySafeSmsIntent(context, order.phone, msg)
              }
            },
            onUpdateStatus = { st -> onUpdateStatus(order.id, st) },
            onDispatchCourier = { partner -> onDispatchCourier(order.id, partner) },
            onSetDeliveredBy = { partner -> onUpdateDeliveredBy(order.id, partner) },
            onOpenReturn = { onOpenReturn(order) }
          )
        }
      }
    }
  }

  // Filter Bottom Sheet (Screenshot 2 + Point 5)
  if (showFilterSheet) {
    ModalBottomSheet(
      onDismissRequest = { showFilterSheet = false },
      containerColor = Color.White
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
          Text(text = "Filter", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
        }

        // Order Type: All | Normal | Custom
        FilterOptionGroupRow(
          title = "Order Type",
          options = listOf("All", "Normal", "Custom"),
          selected = orderTypeFilter,
          onSelect = { orderTypeFilter = it }
        )

        // Delivery Type: All | Standard | Express
        FilterOptionGroupRow(
          title = "Delivery Type",
          options = listOf("All", "Standard", "Express"),
          selected = deliveryTypeFilter,
          onSelect = { deliveryTypeFilter = it }
        )

        // Delivered By: All | Pathao | SteadFast | Digital
        FilterOptionGroupRow(
          title = "Delivered By",
          options = listOf("All", "Pathao", "SteadFast", "Digital"),
          selected = deliveredByFilter,
          onSelect = { deliveredByFilter = it }
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 18.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              orderTypeFilter = "All"
              deliveryTypeFilter = "All"
              deliveredByFilter = "All"
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Text("Reset", fontWeight = FontWeight.Bold)
          }
          Button(
            onClick = { showFilterSheet = false },
            colors = ButtonDefaults.buttonColors(containerColor = SellerOrangeAccent),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Text("Apply Filter", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }

  // Custom Date Range Dialog (supports 1 to 3+ years back)
  if (showCustomDateDialog) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    AlertDialog(
      onDismissRequest = { showCustomDateDialog = false },
      containerColor = Color.White,
      title = {
        Text("কাস্টম তারিখ নির্বাচন (১–৩ বছর+)", fontSize = 15.5.sp, fontWeight = FontWeight.Black)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            "শুরুর এবং শেষের তারিখ লিখুন (yyyy-MM-dd ফরম্যাটে) অথবা দ্রুত পিরিয়ড বেছে নিন:",
            fontSize = 11.5.sp,
            color = AdminInkMuted
          )
          OutlinedTextField(
            value = customStartInput,
            onValueChange = { customStartInput = it },
            label = { Text("শুরুর তারিখ (From: yyyy-MM-dd)") },
            placeholder = { Text("যেমন: 2024-01-01") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = customEndInput,
            onValueChange = { customEndInput = it },
            label = { Text("শেষ তারিখ (To: yyyy-MM-dd)") },
            placeholder = { Text("যেমন: 2026-11-21") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              "১ বছর" to 365,
              "২ বছর" to 730,
              "৩ বছর" to 1095
            ).forEach { (label, days) ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE0F2FE),
                modifier = Modifier.clickable {
                  val now = Date()
                  val from = Date(System.currentTimeMillis() - days.toLong() * 24L * 3600L * 1000L)
                  customStartInput = sdf.format(from)
                  customEndInput = sdf.format(now)
                }
              ) {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = DeepSkyTabEnd,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { showCustomDateDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = DeepSkyTabStart)
        ) {
          Text("প্রয়োগ করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = {
          datePreset = OrderDateRangePreset.ALL
          showCustomDateDialog = false
        }) {
          Text("বাতিল")
        }
      }
    )
  }
}

@Composable
private fun FilterOptionGroupRow(
  title: String,
  options: List<String>,
  selected: String,
  onSelect: (String) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(text = title, fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold, color = AdminInkPrimary)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      options.forEach { opt ->
        val active = selected.equals(opt, ignoreCase = true)
        Surface(
          shape = RoundedCornerShape(50),
          color = if (active) SellerOrangeAccent else Color(0xFFF3F4F6),
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable { onSelect(opt) }
            .testTag("filter_${title}_$opt")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (active) Color.White else AdminInkPrimary)
            )
            Text(
              text = opt,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = if (active) Color.White else AdminInkPrimary
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SellerCenterOrderCard(
  order: Order,
  products: List<Product>,
  storeName: String,
  onCopyOrderId: () -> Unit,
  onOpenShippingLabel: () -> Unit,
  onViewInvoice: () -> Unit,
  onCallCustomer: () -> Unit,
  onWhatsAppCustomer: () -> Unit,
  onSmsCustomer: () -> Unit,
  onUpdateStatus: (String) -> Unit,
  onDispatchCourier: (String) -> Unit,
  onSetDeliveredBy: (String) -> Unit,
  onOpenReturn: () -> Unit
) {
  var showCardMenu by remember { mutableStateOf(false) }

  val firstItem = order.items.firstOrNull()
  val totalQty = order.items.sumOf { it.quantity }.coerceAtLeast(1)
  val thumbModel = remember(firstItem, products) {
    products.find { it.id == firstItem?.productId }?.imageUrl ?: "res://banner_eid_special_1792530116809"
  }
  val paymentLabel = when {
    order.paymentMethod.contains("Cash", true) -> "COD"
    else -> order.paymentMethod.ifBlank { "COD" }
  }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    shadowElevation = 2.dp,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("seller_order_card_${order.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 1. Order Number + Copy + Status Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "Order Number: ${order.id}",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AdminInkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "অর্ডার নাম্বার কপি",
            tint = SellerOrangeAccent,
            modifier = Modifier
              .size(16.dp)
              .clickable(onClick = onCopyOrderId)
          )
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFE0F2FE)
        ) {
          Text(
            text = order.status,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = DeepSkyTabEnd,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      // 2. Payment Method Badge + Order Type + Delivery Type + Delivered By pills (Point 4)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFFFFF7ED),
          border = BorderStroke(1.dp, SellerOrangeAccent)
        ) {
          Text(
            text = paymentLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SellerOrangeAccent,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFFF1F5F9)
        ) {
          Text(
            text = order.orderType,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkSecondary,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (order.deliveryType.equals("Express", true)) Color(0xFFFEF3C7) else Color(0xFFECFDF5)
        ) {
          Text(
            text = if (order.deliveryType.equals("Express", true)) "⚡ Express" else "Standard",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (order.deliveryType.equals("Express", true)) Color(0xFFB45309) else Color(0xFF047857),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
          )
        }

        if (order.deliveredBy.isNotBlank()) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFEEF2FF)
          ) {
            Text(
              text = "🚚 ${order.deliveredBy}",
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF4338CA),
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
            )
          }
        }
      }

      // 3. Product Summary Block
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFF8FAFC))
          .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AppImage(
          model = thumbModel,
          contentDescription = firstItem?.name ?: "Product",
          modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(8.dp))
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Text(
            text = firstItem?.name ?: "অর্ডার আইটেম",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "x $totalQty" + if (order.items.size > 1) " (${order.items.size}টি পণ্য)" else "",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AdminInkPrimary
          )
          Text(
            text = "${order.customerName} • ${order.district}${if (order.thana.isNotBlank()) " / ${order.thana}" else ""}",
            fontSize = 11.sp,
            color = AdminInkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // 4. Total Purchase + BDT
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Total Purchase",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = AdminInkMuted
        )
        Text(
          text = "BDT ${order.total}.00",
          fontSize = 14.sp,
          fontWeight = FontWeight.ExtraBold,
          color = AdminInkPrimary
        )
      }

      // 5. Print Status + AWB (Shipping Label) & Invoice triggers
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Print Status",
          fontSize = 12.5.sp,
          fontWeight = FontWeight.SemiBold,
          color = AdminInkMuted
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable(onClick = onOpenShippingLabel)
              .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            Icon(Icons.Default.Print, contentDescription = "Shipping Label প্রিন্ট", tint = AdminInkMuted, modifier = Modifier.size(14.dp))
            Text("Shipping Label", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = AdminInkMuted)
          }
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable(onClick = onViewInvoice)
              .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            Icon(Icons.Default.Print, contentDescription = "Invoice প্রিন্ট", tint = AdminInkMuted, modifier = Modifier.size(14.dp))
            Text("Invoice", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = AdminInkMuted)
          }
        }
      }

      // 6. Contact Customer Pill + Call + WhatsApp + Mobile SIM SMS + Three-dot Menu (•••)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFF8FAFC),
          border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Contact Customer",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AdminInkPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = onCallCustomer,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFDCFCE7))
                  .testTag("contact_call_${order.id}")
              ) {
                Icon(Icons.Default.Call, contentDescription = "গ্রাহককে কল করুন", tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.width(3.dp))
              IconButton(
                onClick = onWhatsAppCustomer,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFD1FAE5))
                  .testTag("contact_whatsapp_${order.id}")
              ) {
                Icon(Icons.Default.Chat, contentDescription = "হোয়াটসঅ্যাপ মেসেজ পাঠান", tint = Color(0xFF047857), modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.width(3.dp))
              IconButton(
                onClick = onSmsCustomer,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE0F2FE))
                  .testTag("contact_sms_${order.id}")
              ) {
                Icon(Icons.Default.Sms, contentDescription = "মোবাইল সিম থেকে SMS পাঠান", tint = DeepSkyTabEnd, modifier = Modifier.size(18.dp))
              }
            }
          }
        }

        Box {
          IconButton(
            onClick = { showCardMenu = true },
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("order_three_dot_${order.id}")
          ) {
            Icon(
              imageVector = Icons.Default.MoreHoriz,
              contentDescription = "অর্ডার অ্যাকশন মেনু",
              tint = AdminInkPrimary
            )
          }
          DropdownMenu(
            expanded = showCardMenu,
            onDismissRequest = { showCardMenu = false },
            modifier = Modifier.background(Color.White)
          ) {
            DropdownMenuItem(
              text = { Text("🖨️ প্রিন্ট Shipping Label", fontWeight = FontWeight.Bold) },
              onClick = {
                showCardMenu = false
                onOpenShippingLabel()
              }
            )
            DropdownMenuItem(
              text = { Text("🖨️ প্রিন্ট Invoice", fontWeight = FontWeight.Bold) },
              onClick = {
                showCardMenu = false
                onViewInvoice()
              }
            )
            HorizontalDivider(color = AdminCardBorderSoft)
            listOf("Pathao", "SteadFast", "Digital").forEach { partner ->
              DropdownMenuItem(
                text = { Text("🚚 ডিসপ্যাচ: $partner") },
                onClick = {
                  showCardMenu = false
                  onDispatchCourier(partner)
                  onSetDeliveredBy(partner)
                }
              )
            }
            HorizontalDivider(color = AdminCardBorderSoft)
            listOf("Pending", "Confirmed", "To Ship", "Shipped", "Delivered", "Failed Delivered", "Cancelled").forEach { st ->
              DropdownMenuItem(
                text = { Text("স্ট্যাটাস: $st") },
                onClick = {
                  showCardMenu = false
                  onUpdateStatus(st)
                }
              )
            }
            DropdownMenuItem(
              text = { Text("🔄 রিটার্ন / এক্সচেঞ্জ") },
              onClick = {
                showCardMenu = false
                onOpenReturn()
              }
            )
          }
        }
      }
    }
  }
}
