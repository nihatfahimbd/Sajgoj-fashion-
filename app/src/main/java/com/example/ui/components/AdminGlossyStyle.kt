package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================================
// Admin Dashboard V2 — Frosted Ocean Slate + Sky Cyan + White Ice Glass Theme
// ============================================================================

val AdminV2DeepOcean = Color(0xFF133446)
val AdminV2OceanMid = Color(0xFF1E4E68)
val AdminV2OceanLight = Color(0xFF2B6784)
val AdminV2Cyan = Color(0xFF0EA5E9)
val AdminV2CyanDeep = Color(0xFF0284C7)
val AdminV2SkySoft = Color(0xFFE0F2FE)
val AdminV2IceSurface = Color(0xFFF0F9FF)
val AdminV2Emerald = Color(0xFF059669)
val AdminV2EmeraldSoft = Color(0xFFD1FAE5)
val AdminV2Amber = Color(0xFFD97706)
val AdminV2AmberSoft = Color(0xFFFEF3C7)
val AdminV2Rose = Color(0xFFDC2626)

// High-Contrast Ink Palette on Frosted White-Ice Glass
val AdminInkPrimary = Color(0xFF0F2430)
val AdminInkSecondary = Color(0xFF2D495A)
val AdminInkMuted = Color(0xFF4B6476)

// Canvas & Glass Gradient Tokens — Airy Luminous Sky-White
val AdminGlossCanvasTop = Color(0xFFE8F5FC)
val AdminGlossCanvasMid = Color(0xFFF5FAFE)
val AdminGlossCanvasBot = Color(0xFFE4F2FA)

val AdminRoyalGradStart = AdminV2CyanDeep
val AdminRoyalGradEnd = AdminV2Cyan
val AdminNavyGradStart = AdminV2CyanDeep
val AdminNavyGradEnd = Color(0xFF38BDF8)
val AdminEmeraldGradStart = Color(0xFF047857)
val AdminEmeraldGradEnd = Color(0xFF10B981)
val AdminAmberGradStart = Color(0xFFB45309)
val AdminAmberGradEnd = Color(0xFFD97706)

val AdminCardBorderWarm = Color(0xFFC7E3F4)
val AdminCardBorderSoft = Color(0xFFDDECF5)

fun adminCanvasBrush(): Brush = Brush.verticalGradient(
  colors = listOf(
    AdminGlossCanvasTop,
    AdminGlossCanvasMid,
    AdminGlossCanvasBot
  )
)

fun adminSidebarBrush(): Brush = Brush.verticalGradient(
  colors = listOf(
    Color.White,
    Color(0xFFF3FAFF),
    Color(0xFFE6F4FD)
  )
)

fun adminRoyalGlossBrush(): Brush = Brush.linearGradient(
  colors = listOf(
    Color(0xFF0284C7),
    Color(0xFF0EA5E9),
    Color(0xFF38BDF8)
  )
)

fun adminAccentGlossBrush(
  start: Color = AdminV2CyanDeep,
  end: Color = AdminV2Cyan
): Brush = Brush.horizontalGradient(listOf(start, end))

@Composable
fun adminOutlinedTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
  focusedTextColor = AdminInkPrimary,
  unfocusedTextColor = AdminInkPrimary,
  disabledTextColor = AdminInkMuted,
  focusedContainerColor = Color.White,
  unfocusedContainerColor = Color.White,
  disabledContainerColor = Color(0xFFF4F9FD),
  focusedBorderColor = AdminV2CyanDeep,
  unfocusedBorderColor = Color(0xFFBFD9EA),
  focusedLabelColor = AdminV2CyanDeep,
  unfocusedLabelColor = AdminInkSecondary,
  focusedPlaceholderColor = Color(0xFF6B8294),
  unfocusedPlaceholderColor = Color(0xFF6B8294),
  focusedLeadingIconColor = AdminV2CyanDeep,
  unfocusedLeadingIconColor = AdminInkSecondary,
  focusedTrailingIconColor = AdminV2CyanDeep,
  unfocusedTrailingIconColor = AdminInkSecondary,
  cursorColor = AdminV2CyanDeep
)

@Composable
fun GlossyAdminCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 20.dp,
  accentBorderColor: Color = AdminCardBorderWarm,
  containerTintTop: Color = Color(0xFAFFFFFF),
  containerTintBottom: Color = Color(0xEDF1F9FE),
  elevation: Dp = 6.dp,
  contentPadding: Dp = 15.dp,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)
  Box(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Color(0xFF0C2D40).copy(alpha = 0.14f),
        spotColor = AdminV2CyanDeep.copy(alpha = 0.16f)
      )
      .clip(shape)
      .background(
        Brush.verticalGradient(
          colors = listOf(containerTintTop, containerTintBottom)
        )
      )
      .border(
        width = 1.25.dp,
        brush = Brush.verticalGradient(
          colors = listOf(
            Color.White,
            accentBorderColor,
            accentBorderColor.copy(alpha = 0.72f)
          )
        ),
        shape = shape
      )
  ) {
    // Crisp frosted top specular highlight rim
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(2.dp)
        .background(
          Brush.horizontalGradient(
            listOf(
              Color.White.copy(alpha = 0.45f),
              Color.White,
              Color.White.copy(alpha = 0.45f)
            )
          )
        )
    )
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(contentPadding),
      content = content
    )
  }
}

@Composable
fun GlossySegmentPill(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  selectedBrush: Brush = adminAccentGlossBrush(AdminV2CyanDeep, AdminV2Cyan),
  badgeText: String? = null
) {
  val shape = RoundedCornerShape(14.dp)
  Box(
    modifier = modifier
      .heightIn(min = 44.dp)
      .shadow(
        elevation = if (selected) 4.dp else 1.dp,
        shape = shape,
        ambientColor = AdminV2CyanDeep.copy(alpha = 0.12f),
        spotColor = AdminV2Cyan.copy(alpha = 0.24f)
      )
      .clip(shape)
      .then(
        if (selected) {
          Modifier
            .background(selectedBrush)
            .border(1.dp, Color(0xFF7DD3FC).copy(alpha = 0.55f), shape)
        } else {
          Modifier
            .background(
              Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.94f), Color(0xFFF1F8FC))
              )
            )
            .border(1.dp, Color(0xFFC5DFEE), shape)
        }
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 9.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(
        text = label,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) Color.White else AdminInkPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      if (!badgeText.isNullOrBlank()) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (selected) Color.White.copy(alpha = 0.22f) else AdminV2SkySoft
        ) {
          Text(
            text = badgeText,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (selected) Color.White else AdminV2CyanDeep,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

@Composable
fun GlossyActionPillButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  brush: Brush = adminAccentGlossBrush(AdminV2CyanDeep, AdminV2Cyan),
  textColor: Color = Color.White,
  borderColor: Color = Color.White.copy(alpha = 0.55f)
) {
  val shape = RoundedCornerShape(50)
  Box(
    modifier = modifier
      .heightIn(min = 44.dp)
      .shadow(
        elevation = 4.dp,
        shape = shape,
        spotColor = AdminV2CyanDeep.copy(alpha = 0.25f)
      )
      .clip(shape)
      .background(brush)
      .border(BorderStroke(1.dp, borderColor), shape)
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontSize = 12.sp,
      fontWeight = FontWeight.ExtraBold,
      color = textColor,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun AdminV2BentoStatCard(
  title: String,
  value: String,
  trendText: String,
  trendColor: Color,
  icon: ImageVector,
  iconTint: Color = AdminV2CyanDeep,
  iconBg: Color = AdminV2SkySoft,
  accentBorderColor: Color = AdminCardBorderWarm,
  modifier: Modifier = Modifier
) {
  GlossyAdminCard(
    modifier = modifier,
    cornerRadius = 18.dp,
    accentBorderColor = accentBorderColor,
    contentPadding = 13.dp
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = title,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.SemiBold,
        color = AdminInkSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f)
      )
      Box(
        modifier = Modifier
          .size(30.dp)
          .clip(CircleShape)
          .background(iconBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = iconTint,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = value,
      fontSize = 20.sp,
      fontWeight = FontWeight.Black,
      color = AdminInkPrimary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(3.dp))

    Text(
      text = trendText,
      fontSize = 10.5.sp,
      fontWeight = FontWeight.Bold,
      color = trendColor,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun AdminV2FulfillmentRingCard(
  title: String,
  subtitle: String,
  progressRatio: Float,
  centerText: String,
  centerSubText: String,
  leftLabel: String,
  leftValue: String,
  rightLabel: String,
  rightValue: String,
  modifier: Modifier = Modifier
) {
  val clamped = progressRatio.coerceIn(0f, 1f)
  GlossyAdminCard(
    modifier = modifier,
    cornerRadius = 20.dp,
    accentBorderColor = AdminCardBorderWarm,
    contentPadding = 15.dp
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Column {
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = AdminInkMuted
        )
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(124.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier.size(116.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 13.dp.toPx()
            drawArc(
              color = Color(0xFFD9EEF9),
              startAngle = 0f,
              sweepAngle = 360f,
              useCenter = false,
              style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            if (clamped > 0f) {
              drawArc(
                brush = Brush.sweepGradient(
                  listOf(
                    AdminV2Cyan,
                    AdminV2CyanDeep,
                    Color(0xFF38BDF8),
                    AdminV2Cyan
                  )
                ),
                startAngle = -90f,
                sweepAngle = 360f * clamped,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
              )
            }
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = centerText,
              fontSize = 21.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary
            )
            Text(
              text = centerSubText,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = AdminInkMuted
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          color = AdminV2IceSurface,
          border = BorderStroke(1.dp, AdminCardBorderSoft)
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = leftValue, fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
            Text(text = leftLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AdminInkMuted)
          }
        }

        Surface(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          color = AdminV2IceSurface,
          border = BorderStroke(1.dp, AdminCardBorderSoft)
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = rightValue, fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = AdminV2Emerald)
            Text(text = rightLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AdminInkMuted)
          }
        }
      }
    }
  }
}
