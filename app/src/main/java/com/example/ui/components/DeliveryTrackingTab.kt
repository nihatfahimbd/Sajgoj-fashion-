package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.AccentGreen
import kotlin.math.roundToInt

private val PendingAmber = Color(0xFFF59E0B)
private val PendingAmberDark = Color(0xFF92400E)
private val PendingAmberBg = Color(0xFFFEF3C7)

private val ShippedBlue = Color(0xFF2563EB)
private val ShippedBlueDark = Color(0xFF0284C7)
private val ShippedBlueBg = Color(0xFFE0F2FE)

private val DeliveredEmerald = Color(0xFF16A34A)
private val DeliveredEmeraldDark = Color(0xFF166534)
private val DeliveredEmeraldBg = Color(0xFFDCFCE7)

@Composable
fun AdminDeliveryTrackingTab(
  orders: List<Order>,
  onUpdateStatus: (String, String) -> Unit,
  onViewInvoice: (Order) -> Unit,
  onOpenShippingLabel: (Order) -> Unit = {}
) {
  var selectedFilter by remember { mutableStateOf("ALL") }
  var animateChart by remember { mutableStateOf(false) }

  LaunchedEffect(orders.size) {
    animateChart = true
  }

  val pendingOrders = remember(orders) { orders.filter { it.status.equals("Pending", ignoreCase = true) } }
  val shippedOrders = remember(orders) { orders.filter { it.status.equals("Shipped", ignoreCase = true) } }
  val deliveredOrders = remember(orders) { orders.filter { it.status.equals("Delivered", ignoreCase = true) } }

  val pendingCount = pendingOrders.size
  val shippedCount = shippedOrders.size
  val deliveredCount = deliveredOrders.size
  val totalTracked = pendingCount + shippedCount + deliveredCount

  val chartProgress by animateFloatAsState(
    targetValue = if (animateChart) 1f else 0f,
    animationSpec = spring(stiffness = Spring.StiffnessLow),
    label = "delivery_chart_progress"
  )

  val displayedOrders = remember(orders, selectedFilter) {
    when (selectedFilter) {
      "PENDING" -> pendingOrders
      "SHIPPED" -> shippedOrders
      "DELIVERED" -> deliveredOrders
      else -> orders.filter {
        it.status.equals("Pending", ignoreCase = true) ||
          it.status.equals("Shipped", ignoreCase = true) ||
          it.status.equals("Delivered", ignoreCase = true)
      }
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("delivery_tracking_view"),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // 1. Hero Summary Chart Card with Bright Azure Gradient Header Band
    item {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.2.dp, Color(0xFFBAE6FD)),
        shadowElevation = 5.dp,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("delivery_summary_chart_card")
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(adminRoyalGlossBrush())
              .padding(horizontal = 15.dp, vertical = 12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "ডেলিভারি ট্র্যাকিং ওভারভিউ",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
                Text(
                  text = "Pending, Shipped ও Delivered অর্ডারের ডিস্ট্রিবিউশন",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color.White.copy(alpha = 0.90f)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.22f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
              ) {
                Text(
                  text = "মোট $totalTracked টি",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {

          if (totalTracked == 0) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(AdminV2IceSurface, RoundedCornerShape(14.dp))
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = "কোনো ডেলিভারি ডেটা নেই",
                tint = AdminInkMuted,
                modifier = Modifier.size(34.dp)
              )
              Text(
                text = "এখনও কোনো ট্র্যাকযোগ্য ডেলিভারি নেই",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkPrimary
              )
              Text(
                text = "নতুন অর্ডার এলে Pending, Shipped এবং Delivered চার্ট এখানে দেখা যাবে।",
                fontSize = 11.sp,
                color = AdminInkMuted
              )
            }
          } else {
            // Donut Chart + Breakdown Legend
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(126.dp)
                  .testTag("delivery_donut_chart"),
                contentAlignment = Alignment.Center
              ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                  val strokeWidth = 22.dp.toPx()
                  val diameter = minOf(size.width, size.height) - strokeWidth
                  val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                  )
                  val arcSize = Size(diameter, diameter)

                  // Track ring
                  drawArc(
                    color = Color(0xFFD9EEF9),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                  )

                  val segments = listOf(
                    pendingCount to PendingAmber,
                    shippedCount to ShippedBlue,
                    deliveredCount to DeliveredEmerald
                  )
                  var currentAngle = -90f
                  val totalFloat = totalTracked.toFloat().coerceAtLeast(1f)
                  segments.forEach { (count, color) ->
                    if (count > 0) {
                      val sweep = (count / totalFloat) * 360f * chartProgress
                      drawArc(
                        color = color,
                        startAngle = currentAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                      )
                      currentAngle += sweep
                    }
                  }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "$totalTracked",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AdminInkPrimary
                  )
                  Text(
                    text = "অর্ডার",
                    fontSize = 10.sp,
                    color = AdminInkMuted,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }

              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                DeliveryDistributionBarRow(
                  label = "Pending",
                  subLabel = "পেন্ডিং",
                  count = pendingCount,
                  total = totalTracked,
                  color = PendingAmber,
                  progress = chartProgress
                )
                DeliveryDistributionBarRow(
                  label = "Shipped",
                  subLabel = "পথে আছে",
                  count = shippedCount,
                  total = totalTracked,
                  color = ShippedBlue,
                  progress = chartProgress
                )
                DeliveryDistributionBarRow(
                  label = "Delivered",
                  subLabel = "সম্পন্ন",
                  count = deliveredCount,
                  total = totalTracked,
                  color = DeliveredEmerald,
                  progress = chartProgress
                )
              }
            }

            // Proportional Segmented Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "অনুপাত বার (Pending / Shipped / Delivered)",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkSecondary
              )
              Canvas(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(14.dp)
                  .clip(RoundedCornerShape(8.dp))
              ) {
                val totalFloat = totalTracked.toFloat().coerceAtLeast(1f)
                val pWidth = (pendingCount / totalFloat) * size.width * chartProgress
                val sWidth = (shippedCount / totalFloat) * size.width * chartProgress
                val dWidth = (deliveredCount / totalFloat) * size.width * chartProgress

                drawRoundRect(
                  color = AdminV2IceSurface,
                  size = size,
                  cornerRadius = CornerRadius(12f, 12f)
                )
                var x = 0f
                if (pWidth > 0f) {
                  drawRect(color = PendingAmber, topLeft = Offset(x, 0f), size = Size(pWidth, size.height))
                  x += pWidth
                }
                if (sWidth > 0f) {
                  drawRect(color = ShippedBlue, topLeft = Offset(x, 0f), size = Size(sWidth, size.height))
                  x += sWidth
                }
                if (dWidth > 0f) {
                  drawRect(color = DeliveredEmerald, topLeft = Offset(x, 0f), size = Size(dWidth, size.height))
                }
              }
            }
          }
        }
        }
      }
    }

    // 2. Three KPI Cards (Pending, Shipped, Delivered)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DeliveryKpiCard(
          title = "Pending",
          count = pendingCount,
          amount = pendingOrders.sumOf { it.total },
          accent = PendingAmberDark,
          bg = PendingAmberBg,
          icon = Icons.Default.HourglassTop,
          modifier = Modifier.weight(1f)
        )
        DeliveryKpiCard(
          title = "Shipped",
          count = shippedCount,
          amount = shippedOrders.sumOf { it.total },
          accent = ShippedBlueDark,
          bg = ShippedBlueBg,
          icon = Icons.Default.LocalShipping,
          modifier = Modifier.weight(1f)
        )
        DeliveryKpiCard(
          title = "Delivered",
          count = deliveredCount,
          amount = deliveredOrders.sumOf { it.total },
          accent = DeliveredEmeraldDark,
          bg = DeliveredEmeraldBg,
          icon = Icons.Default.CheckCircle,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Bright Filter Chips
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val filters = listOf(
          Triple("ALL", "সকল ($totalTracked)", AdminV2CyanDeep),
          Triple("PENDING", "Pending ($pendingCount)", PendingAmber),
          Triple("SHIPPED", "Shipped ($shippedCount)", ShippedBlue),
          Triple("DELIVERED", "Delivered ($deliveredCount)", DeliveredEmerald)
        )
        filters.forEach { (key, label, activeColor) ->
          val isSelected = selectedFilter == key
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (isSelected) activeColor else Color.White,
            border = BorderStroke(1.2.dp, if (isSelected) activeColor else Color(0xFFBAE6FD)),
            shadowElevation = if (isSelected) 3.dp else 1.dp,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .clip(RoundedCornerShape(18.dp))
              .testTag("delivery_filter_$key")
              .clickable { selectedFilter = key }
          ) {
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
              color = if (isSelected) Color.White else AdminInkPrimary,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
          }
        }
      }
    }

    // 4. Order Delivery List
    if (displayedOrders.isEmpty()) {
      item {
        GlossyAdminCard(
          cornerRadius = 16.dp,
          contentPadding = 24.dp
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalShipping,
              contentDescription = "খালি তালিকা",
              tint = AdminV2CyanDeep,
              modifier = Modifier.size(32.dp)
            )
            Text(
              text = "এই ফিল্টারে কোনো অর্ডার পাওয়া যায়নি",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = AdminInkPrimary
            )
          }
        }
      }
    } else {
      items(displayedOrders, key = { it.id }) { order ->
        val (badgeBg, badgeFg, badgeLabel) = when {
          order.status.equals("Delivered", ignoreCase = true) -> Triple(DeliveredEmeraldBg, DeliveredEmeraldDark, "Delivered")
          order.status.equals("Shipped", ignoreCase = true) -> Triple(ShippedBlueBg, ShippedBlueDark, "Shipped")
          else -> Triple(PendingAmberBg, PendingAmberDark, "Pending")
        }

        GlossyAdminCard(
          modifier = Modifier.testTag("delivery_order_card_${order.id}"),
          cornerRadius = 16.dp,
          contentPadding = 14.dp
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "অর্ডার #${order.id}",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminV2CyanDeep
                )
                Text(
                  text = order.date,
                  fontSize = 11.sp,
                  color = AdminInkMuted
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = badgeBg
              ) {
                Text(
                  text = badgeLabel,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = badgeFg,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }

            Text(
              text = "${order.customerName} • ${order.phone}",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = AdminInkPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "ঠিকানা: ${order.address}${if (order.district.isNotBlank()) ", ${order.district}" else ""}",
              fontSize = 11.sp,
              color = AdminInkMuted,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )

            if (order.trackingId.isNotBlank()) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = AdminV2IceSurface,
                border = BorderStroke(1.dp, AdminCardBorderWarm)
              ) {
                Text(
                  text = "ট্র্যাকিং আইডি: ${order.trackingId}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "মোট: ৳${order.total}",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AdminInkPrimary,
                modifier = Modifier.weight(1f)
              )

              if (order.status.equals("Pending", ignoreCase = true)) {
                Button(
                  onClick = { onUpdateStatus(order.id, "Shipped") },
                  colors = ButtonDefaults.buttonColors(containerColor = ShippedBlue),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("mark_shipped_${order.id}"),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = "Shipped চিহ্নিত করুন",
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Shipped", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }

              if (order.status.equals("Shipped", ignoreCase = true)) {
                Button(
                  onClick = { onOpenShippingLabel(order) },
                  colors = ButtonDefaults.buttonColors(containerColor = ShippedBlueDark),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("delivery_shipping_label_${order.id}"),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "শিপিং লেবেল প্রিন্ট",
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("লেবেল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }

              if (!order.status.equals("Delivered", ignoreCase = true)) {
                Button(
                  onClick = { onUpdateStatus(order.id, "Delivered") },
                  colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("mark_delivered_${order.id}"),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Delivered চিহ্নিত করুন",
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Delivered", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }

              Button(
                onClick = { onViewInvoice(order) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = AdminV2IceSurface,
                  contentColor = AdminV2CyanDeep
                ),
                border = BorderStroke(1.dp, AdminCardBorderWarm),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .heightIn(min = 48.dp)
                  .testTag("delivery_invoice_${order.id}"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Receipt,
                  contentDescription = "ইনভয়েস দেখুন",
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DeliveryDistributionBarRow(
  label: String,
  subLabel: String,
  count: Int,
  total: Int,
  color: Color,
  progress: Float
) {
  val pct = if (total > 0) ((count.toFloat() / total) * 100f).roundToInt() else 0
  val ratio = if (total > 0) (count.toFloat() / total) * progress else 0f

  Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(
          modifier = Modifier
            .size(9.dp)
            .background(color, CircleShape)
        )
        Text(
          text = "$label ($subLabel)",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = AdminInkPrimary
        )
      }
      Text(
        text = "$count টি ($pct%)",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = AdminInkPrimary
      )
    }
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(6.dp))
    ) {
      drawRoundRect(
        color = AdminV2IceSurface,
        size = size,
        cornerRadius = CornerRadius(8f, 8f)
      )
      if (ratio > 0f) {
        drawRoundRect(
          color = color,
          size = Size(size.width * ratio.coerceIn(0f, 1f), size.height),
          cornerRadius = CornerRadius(8f, 8f)
        )
      }
    }
  }
}

@Composable
private fun DeliveryKpiCard(
  title: String,
  count: Int,
  amount: Int,
  accent: Color,
  bg: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    color = bg.copy(alpha = 0.55f),
    border = BorderStroke(1.2.dp, accent.copy(alpha = 0.35f)),
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(9.dp),
        color = accent
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(13.dp)
          )
          Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
        }
      }
      Text(
        text = "$count টি",
        fontSize = 17.5.sp,
        fontWeight = FontWeight.Black,
        color = AdminInkPrimary
      )
      Text(
        text = "৳$amount",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = accent,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
