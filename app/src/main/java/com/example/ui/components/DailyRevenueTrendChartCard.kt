package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DailyRevenuePoint(
  val dateKey: String,
  val label: String,
  val revenue: Int,
  val orderCount: Int
)

fun computeLast7DaysRevenuePoints(orders: List<Order>, nowMillis: Long = System.currentTimeMillis()): List<DailyRevenuePoint> {
  val keyFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
  val shortDateFormatter = SimpleDateFormat("dd/MM", Locale.US)

  return (6 downTo 0).map { daysAgo ->
    val cal = Calendar.getInstance().apply {
      timeInMillis = nowMillis
      add(Calendar.DAY_OF_YEAR, -daysAgo)
    }
    val dateKey = keyFormatter.format(cal.time)
    val label = when (daysAgo) {
      0 -> "আজ"
      1 -> "গতকাল"
      else -> shortDateFormatter.format(cal.time)
    }
    val matchingOrders = orders.filter { o ->
      o.status != "Cancelled" && o.date.take(10) == dateKey
    }
    DailyRevenuePoint(
      dateKey = dateKey,
      label = label,
      revenue = matchingOrders.sumOf { it.total },
      orderCount = matchingOrders.size
    )
  }
}

@Composable
fun DailyRevenueTrendChartCard(
  orders: List<Order>,
  modifier: Modifier = Modifier
) {
  val series = remember(orders) { computeLast7DaysRevenuePoints(orders) }
  var selectedIndex by remember { mutableIntStateOf(6) }

  val total7dRevenue = remember(series) { series.sumOf { it.revenue } }
  val avgDailyRevenue = remember(total7dRevenue) { total7dRevenue / 7 }
  val total7dOrders = remember(series) { series.sumOf { it.orderCount } }
  val selectedPoint = series.getOrElse(selectedIndex) { series.last() }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BorderGold),
    modifier = modifier
      .fillMaxWidth()
      .testTag("daily_revenue_7d_chart_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "📈 গত ৭ দিনের দৈনিক রেভিনিউ ট্রেন্ড",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          Text(
            text = "মোট: ৳$total7dRevenue • দৈনিক গড়: ৳$avgDailyRevenue • $total7dOrders টি অর্ডার",
            fontSize = 10.5.sp,
            color = AntiqueGoldDark,
            fontWeight = FontWeight.SemiBold
          )
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = CreamSurface,
          border = BorderStroke(1.dp, BorderGold)
        ) {
          Text(
            text = "${selectedPoint.label}: ৳${selectedPoint.revenue}",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Line Chart Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
          .background(CreamBackground, RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 14.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val maxVal = (series.maxOfOrNull { it.revenue } ?: 0).coerceAtLeast(1000)
          val count = series.size.coerceAtLeast(2)
          val stepX = size.width / (count - 1).toFloat()

          // Dashed horizontal grid lines
          listOf(0.25f, 0.5f, 0.75f, 1f).forEach { ratio ->
            val y = size.height * (1f - ratio)
            drawLine(
              color = Color(0xFFD6CFC2),
              start = Offset(0f, y),
              end = Offset(size.width, y),
              strokeWidth = 1.5f,
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
          }

          val points = series.mapIndexed { idx, pt ->
            val x = idx * stepX
            val normalized = pt.revenue.toFloat() / maxVal.toFloat()
            val y = size.height - (normalized * (size.height * 0.88f)) - (size.height * 0.04f)
            Offset(x, y)
          }

          if (points.isNotEmpty()) {
            val linePath = Path().apply {
              moveTo(points.first().x, points.first().y)
              for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                val midX = (prev.x + curr.x) / 2f
                cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
              }
            }

            val areaPath = Path().apply {
              addPath(linePath)
              lineTo(points.last().x, size.height)
              lineTo(points.first().x, size.height)
              close()
            }

            drawPath(
              path = areaPath,
              brush = Brush.verticalGradient(
                listOf(RoyalMaroon.copy(alpha = 0.24f), Color.Transparent)
              )
            )

            drawPath(
              path = linePath,
              color = RoyalMaroon,
              style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            points.forEachIndexed { idx, offset ->
              val isSelected = idx == selectedIndex
              drawCircle(
                color = if (isSelected) RoyalMaroon else Color.White,
                radius = if (isSelected) 11f else 8f,
                center = offset
              )
              drawCircle(
                color = if (isSelected) Color.White else RoyalMaroon,
                radius = if (isSelected) 5.5f else 4f,
                center = offset
              )
            }
          }
        }
      }

      // Interactive Day Selector Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        series.forEachIndexed { idx, pt ->
          val isSelected = idx == selectedIndex
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) RoyalMaroon else CreamSurface,
            border = BorderStroke(1.dp, if (isSelected) RoyalMaroon else BorderGold),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedIndex = idx }
              .testTag("rev_day_$idx")
          ) {
            Column(
              modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = pt.label,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else DarkEspresso,
                maxLines = 1,
                textAlign = TextAlign.Center
              )
              Text(
                text = "${pt.orderCount}টি",
                fontSize = 8.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Gray
              )
            }
          }
        }
      }
    }
  }
}
