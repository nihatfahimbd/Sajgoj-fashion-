package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

enum class OrderFrequencyRange(val label: String) {
  MONTHLY("৬ মাস"),
  WEEKLY("৪ সপ্তাহ")
}

data class OrderFrequencyBucket(
  val bucketKey: String,
  val label: String,
  val orderCount: Int,
  val totalSpent: Int
)

data class CustomerOrderSummaryStats(
  val totalOrders: Int,
  val completedOrActiveOrders: Int,
  val totalSpent: Int,
  val averageOrderValue: Int,
  val peakPeriodLabel: String,
  val peakOrderCount: Int
)

private fun resolveOrderTimestampMillis(order: Order): Long {
  val dateStr = order.date.trim().take(10)
  if (dateStr.length == 10 && dateStr[4] == '-' && dateStr[7] == '-') {
    try {
      val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        isLenient = false
      }.parse(dateStr)
      if (parsed != null) return parsed.time
    } catch (_: Exception) {
      // Fallback to createdAt
    }
  }
  return order.createdAt
}

fun computeCustomerOrderFrequencyBuckets(
  orders: List<Order>,
  range: OrderFrequencyRange = OrderFrequencyRange.MONTHLY,
  nowMillis: Long = System.currentTimeMillis()
): List<OrderFrequencyBucket> {
  val monthKeyFmt = SimpleDateFormat("yyyy-MM", Locale.US)
  val monthLabelFmt = SimpleDateFormat("MMM", Locale.US)

  return when (range) {
    OrderFrequencyRange.MONTHLY -> {
      val buckets = (5 downTo 0).map { monthsAgo ->
        val cal = Calendar.getInstance().apply {
          timeInMillis = nowMillis
          set(Calendar.DAY_OF_MONTH, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
          add(Calendar.MONTH, -monthsAgo)
        }
        Triple(
          monthKeyFmt.format(cal.time),
          monthLabelFmt.format(cal.time),
          0
        )
      }

      val countsByKey = mutableMapOf<String, Int>()
      val spentByKey = mutableMapOf<String, Int>()
      orders.forEach { order ->
        val ts = resolveOrderTimestampMillis(order)
        if (ts > 0L) {
          val cal = Calendar.getInstance().apply { timeInMillis = ts }
          val key = monthKeyFmt.format(cal.time)
          countsByKey[key] = (countsByKey[key] ?: 0) + 1
          if (!order.status.equals("Cancelled", ignoreCase = true)) {
            spentByKey[key] = (spentByKey[key] ?: 0) + order.total
          }
        }
      }

      buckets.map { (key, label, _) ->
        OrderFrequencyBucket(
          bucketKey = key,
          label = label,
          orderCount = countsByKey[key] ?: 0,
          totalSpent = spentByKey[key] ?: 0
        )
      }
    }

    OrderFrequencyRange.WEEKLY -> {
      val endOfToday = Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
      }.timeInMillis
      val weekMs = 7L * 24L * 3600L * 1000L

      (3 downTo 0).map { weeksAgo ->
        val windowEnd = endOfToday - weeksAgo * weekMs
        val windowStart = windowEnd - weekMs + 1L
        val matching = orders.filter { order ->
          val ts = resolveOrderTimestampMillis(order)
          ts in windowStart..windowEnd
        }
        val label = when (weeksAgo) {
          0 -> "এই সপ্তাহ"
          1 -> "গত সপ্তাহ"
          else -> "${weeksAgo + 1} স. আগে"
        }
        OrderFrequencyBucket(
          bucketKey = "week_$weeksAgo",
          label = label,
          orderCount = matching.size,
          totalSpent = matching
            .filterNot { it.status.equals("Cancelled", ignoreCase = true) }
            .sumOf { it.total }
        )
      }
    }
  }
}

fun computeCustomerOrderSummaryStats(
  orders: List<Order>,
  buckets: List<OrderFrequencyBucket>
): CustomerOrderSummaryStats {
  val validOrders = orders.filterNot { it.status.equals("Cancelled", ignoreCase = true) }
  val totalSpent = validOrders.sumOf { it.total }
  val avg = if (validOrders.isNotEmpty()) totalSpent / validOrders.size else 0
  val peak = buckets.maxByOrNull { it.orderCount }
  return CustomerOrderSummaryStats(
    totalOrders = orders.size,
    completedOrActiveOrders = validOrders.size,
    totalSpent = totalSpent,
    averageOrderValue = avg,
    peakPeriodLabel = peak?.label ?: "-",
    peakOrderCount = peak?.orderCount ?: 0
  )
}

@Composable
fun CustomerOrderStatisticsCard(
  orders: List<Order>,
  modifier: Modifier = Modifier,
  nowMillis: Long = System.currentTimeMillis()
) {
  var selectedRange by remember { mutableStateOf(OrderFrequencyRange.MONTHLY) }
  val buckets = remember(orders, selectedRange, nowMillis) {
    computeCustomerOrderFrequencyBuckets(orders, selectedRange, nowMillis)
  }
  val stats = remember(orders, buckets) {
    computeCustomerOrderSummaryStats(orders, buckets)
  }

  val defaultSelectedIndex = remember(buckets) {
    val nonZeroIdx = buckets.indexOfLast { it.orderCount > 0 }
    if (nonZeroIdx >= 0) nonZeroIdx else (buckets.lastIndex.coerceAtLeast(0))
  }
  var selectedIndex by remember(selectedRange, buckets) {
    mutableIntStateOf(defaultSelectedIndex)
  }
  val activeBucket = buckets.getOrElse(selectedIndex) {
    buckets.lastOrNull() ?: OrderFrequencyBucket("", "-", 0, 0)
  }

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color.White,
    border = BorderStroke(1.dp, BorderGold),
    shadowElevation = 2.dp,
    modifier = modifier
      .fillMaxWidth()
      .testTag("customer_order_statistics_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Customer Order Statistics",
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "অর্ডার ফ্রিকোয়েন্সি বার চার্ট (${selectedRange.label})",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = AntiqueGoldDark
          )
        }

        // Range Toggle Pills (6 Months vs 4 Weeks)
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OrderFrequencyRange.entries.forEach { range ->
            val isSelected = selectedRange == range
            val tag = if (range == OrderFrequencyRange.MONTHLY) {
              "order_stat_range_monthly"
            } else {
              "order_stat_range_weekly"
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) RoyalMaroon else CreamSurface,
              border = BorderStroke(1.dp, if (isSelected) RoyalMaroon else BorderGold),
              modifier = Modifier
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedRange = range }
                .testTag(tag)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
              ) {
                Text(
                  text = range.label,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else DarkEspresso
                )
              }
            }
          }
        }
      }

      // Summary KPI Strip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        OrderStatMetricChip(
          label = "মোট অর্ডার",
          value = "${stats.totalOrders} টি",
          modifier = Modifier.weight(1f)
        )
        OrderStatMetricChip(
          label = "মোট ক্রয়",
          value = "৳${stats.totalSpent}",
          modifier = Modifier.weight(1f)
        )
        OrderStatMetricChip(
          label = "গড় অর্ডার",
          value = "৳${stats.averageOrderValue}",
          modifier = Modifier.weight(1f)
        )
      }

      if (orders.isEmpty()) {
        // Empty State
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = CreamSurface,
          border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.6f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("order_statistics_empty_state")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 18.dp, horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.BarChart,
              contentDescription = "অর্ডার পরিসংখ্যান খালি",
              tint = AntiqueGoldDark,
              modifier = Modifier.size(32.dp)
            )
            Text(
              text = "এখনো অর্ডার ফ্রিকোয়েন্সি ডেটা নেই",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
            Text(
              text = "আপনার প্রথম অর্ডার সম্পন্ন হলে এখানে মাস ও সপ্তাহভিত্তিক অর্ডার বার চার্ট দেখা যাবে।",
              fontSize = 10.5.sp,
              color = Color(0xFF4B5563),
              textAlign = TextAlign.Center
            )
          }
        }
      } else {
        // Active Tooltip Pill (Recharts-inspired hover/active tooltip)
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = CreamBackground,
          border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.85f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("order_stat_active_tooltip")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "📅 ${activeBucket.label}: ${activeBucket.orderCount} টি অর্ডার",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
            Text(
              text = "খরচ: ৳${activeBucket.totalSpent}",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = DarkEspresso
            )
          }
        }

        // Bar Chart Canvas Area with Y-Axis Labels + Dashed Gridlines
        val maxCount = (buckets.maxOfOrNull { it.orderCount } ?: 0).coerceAtLeast(2)
        val yMid = (maxCount / 2.0).roundToInt().coerceAtLeast(1)

        val animatedRatios = buckets.map { bucket ->
          val targetRatio = if (maxCount > 0) bucket.orderCount.toFloat() / maxCount.toFloat() else 0f
          animateFloatAsState(
            targetValue = targetRatio.coerceIn(0f, 1f),
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            label = "bar_height_${bucket.bucketKey}"
          ).value
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(listOf(Color(0xFFFFFBF4), CreamSurface)),
              RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp, vertical = 12.dp)
            .testTag("order_frequency_bar_chart"),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(132.dp),
            verticalAlignment = Alignment.Bottom
          ) {
            // Y-Axis Frequency Scale Labels
            Column(
              modifier = Modifier
                .width(24.dp)
                .fillMaxHeight(),
              verticalArrangement = Arrangement.SpaceBetween,
              horizontalAlignment = Alignment.End
            ) {
              Text(
                text = "$maxCount",
                fontSize = 9.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "$yMid",
                fontSize = 9.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "0",
                fontSize = 9.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.SemiBold
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Bars + Grid Canvas
            Canvas(
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
            ) {
              val chartWidth = size.width
              val chartHeight = size.height

              // Horizontal dashed gridlines
              listOf(0f, 0.5f, 1f).forEach { fraction ->
                val y = chartHeight * fraction
                drawLine(
                  color = BorderGold.copy(alpha = 0.55f),
                  start = Offset(0f, y),
                  end = Offset(chartWidth, y),
                  strokeWidth = if (fraction == 1f) 2f else 1.5f,
                  pathEffect = if (fraction == 1f) null else PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )
              }

              val slotCount = buckets.size.coerceAtLeast(1)
              val slotWidth = chartWidth / slotCount.toFloat()
              val barWidth = (slotWidth * 0.54f).coerceIn(18f, 54f)

              buckets.forEachIndexed { idx, bucket ->
                val ratio = animatedRatios.getOrElse(idx) { 0f }
                val barHeight = if (bucket.orderCount > 0) {
                  (chartHeight * ratio).coerceAtLeast(10f)
                } else {
                  4f
                }
                val left = idx * slotWidth + (slotWidth - barWidth) / 2f
                val top = chartHeight - barHeight
                val isSelected = idx == selectedIndex

                val barBrush = when {
                  bucket.orderCount == 0 -> Brush.verticalGradient(
                    listOf(BorderGold.copy(alpha = 0.45f), BorderGold.copy(alpha = 0.25f))
                  )
                  isSelected -> Brush.verticalGradient(
                    listOf(RoyalMaroon, Color(0xFFA61B38))
                  )
                  else -> Brush.verticalGradient(
                    listOf(AntiqueGold, RoyalMaroon.copy(alpha = 0.72f))
                  )
                }

                drawRoundRect(
                  brush = barBrush,
                  topLeft = Offset(left, top),
                  size = Size(barWidth, barHeight),
                  cornerRadius = CornerRadius(12f, 12f)
                )
              }
            }
          }

          // Interactive X-Axis Labels & Counts
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            buckets.forEachIndexed { idx, bucket ->
              val isSelected = idx == selectedIndex
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) RoyalMaroon else Color.White,
                border = BorderStroke(1.dp, if (isSelected) RoyalMaroon else BorderGold.copy(alpha = 0.7f)),
                modifier = Modifier
                  .weight(1f)
                  .heightIn(min = 48.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { selectedIndex = idx }
                  .testTag("order_stat_bar_$idx")
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Text(
                    text = bucket.label,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else DarkEspresso,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                  )
                  Text(
                    text = "${bucket.orderCount}",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) AntiqueGold else RoyalMaroon
                  )
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
private fun OrderStatMetricChip(
  label: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = CreamSurface,
    border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.7f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(
        text = label,
        fontSize = 9.5.sp,
        color = Color(0xFF4B5563),
        fontWeight = FontWeight.SemiBold,
        maxLines = 1
      )
      Text(
        text = value,
        fontSize = 12.sp,
        color = RoyalMaroon,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
