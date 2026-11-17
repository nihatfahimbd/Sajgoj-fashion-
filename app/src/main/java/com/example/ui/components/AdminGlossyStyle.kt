package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RoyalMaroon

// High-Contrast Glossy Admin Color Palette (WCAG AA/AAA compliant on luminous light cards)
val AdminInkPrimary = Color(0xFF0F172A)
val AdminInkSecondary = Color(0xFF334155)
val AdminInkMuted = Color(0xFF475569)

val AdminGlossCanvasTop = Color(0xFFFFFBF8)
val AdminGlossCanvasMid = Color(0xFFFDF2F6)
val AdminGlossCanvasBot = Color(0xFFEFF4FF)

val AdminRoyalGradStart = Color(0xFF881337)
val AdminRoyalGradEnd = Color(0xFFBE123C)
val AdminNavyGradStart = Color(0xFF0F294A)
val AdminNavyGradEnd = Color(0xFF1D4ED8)
val AdminEmeraldGradStart = Color(0xFF047857)
val AdminEmeraldGradEnd = Color(0xFF10B981)
val AdminAmberGradStart = Color(0xFFB45309)
val AdminAmberGradEnd = Color(0xFFD97706)

val AdminCardBorderWarm = Color(0xFFE8D5C4)
val AdminCardBorderSoft = Color(0xFFE2E8F0)

fun adminCanvasBrush(): Brush = Brush.verticalGradient(
  colors = listOf(
    AdminGlossCanvasTop,
    AdminGlossCanvasMid,
    AdminGlossCanvasBot
  )
)

fun adminRoyalGlossBrush(): Brush = Brush.linearGradient(
  colors = listOf(
    AdminNavyGradStart,
    Color(0xFF4C1432),
    AdminRoyalGradStart
  )
)

fun adminAccentGlossBrush(
  start: Color = AdminRoyalGradStart,
  end: Color = AdminRoyalGradEnd
): Brush = Brush.horizontalGradient(listOf(start, end))

@Composable
fun adminOutlinedTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
  focusedTextColor = AdminInkPrimary,
  unfocusedTextColor = AdminInkPrimary,
  disabledTextColor = AdminInkMuted,
  focusedContainerColor = Color.White,
  unfocusedContainerColor = Color(0xFFFFFCFA),
  disabledContainerColor = Color(0xFFF8FAFC),
  focusedBorderColor = RoyalMaroon,
  unfocusedBorderColor = Color(0xFFCBD5E1),
  focusedLabelColor = RoyalMaroon,
  unfocusedLabelColor = AdminInkMuted,
  focusedPlaceholderColor = Color(0xFF64748B),
  unfocusedPlaceholderColor = Color(0xFF64748B),
  focusedLeadingIconColor = RoyalMaroon,
  unfocusedLeadingIconColor = AdminInkMuted,
  focusedTrailingIconColor = RoyalMaroon,
  unfocusedTrailingIconColor = AdminInkMuted,
  cursorColor = RoyalMaroon
)

@Composable
fun GlossyAdminCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 20.dp,
  accentBorderColor: Color = AdminCardBorderWarm,
  containerTintTop: Color = Color.White,
  containerTintBottom: Color = Color(0xFFFFF9F5),
  elevation: Dp = 6.dp,
  contentPadding: Dp = 14.dp,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)
  Box(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Color(0xFF0F294A).copy(alpha = 0.14f),
        spotColor = RoyalMaroon.copy(alpha = 0.16f)
      )
      .clip(shape)
      .background(
        Brush.verticalGradient(
          colors = listOf(containerTintTop, containerTintBottom)
        )
      )
      .border(
        width = 1.5.dp,
        brush = Brush.verticalGradient(
          colors = listOf(
            Color.White,
            accentBorderColor,
            accentBorderColor.copy(alpha = 0.7f)
          )
        ),
        shape = shape
      )
  ) {
    // Subtle glossy specular rim along top edge
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(2.dp)
        .background(
          Brush.horizontalGradient(
            listOf(
              Color.White.copy(alpha = 0.35f),
              Color.White.copy(alpha = 0.95f),
              Color.White.copy(alpha = 0.35f)
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
  selectedBrush: Brush = adminAccentGlossBrush(),
  badgeText: String? = null
) {
  val shape = RoundedCornerShape(14.dp)
  Box(
    modifier = modifier
      .heightIn(min = 44.dp)
      .shadow(
        elevation = if (selected) 5.dp else 2.dp,
        shape = shape,
        ambientColor = Color(0xFF0F294A).copy(alpha = 0.12f),
        spotColor = RoyalMaroon.copy(alpha = 0.22f)
      )
      .clip(shape)
      .then(
        if (selected) {
          Modifier
            .background(selectedBrush)
            .border(1.dp, Color.White.copy(alpha = 0.55f), shape)
        } else {
          Modifier
            .background(
              Brush.verticalGradient(
                listOf(Color.White, Color(0xFFF8FAFC))
              )
            )
            .border(1.dp, Color(0xFFCBD5E1), shape)
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
          color = if (selected) Color.White.copy(alpha = 0.22f) else Color(0xFFFEE2E2)
        ) {
          Text(
            text = badgeText,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (selected) Color.White else RoyalMaroon,
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
  brush: Brush = adminAccentGlossBrush(),
  textColor: Color = Color.White,
  borderColor: Color = Color.White.copy(alpha = 0.55f)
) {
  val shape = RoundedCornerShape(14.dp)
  Box(
    modifier = modifier
      .heightIn(min = 44.dp)
      .shadow(
        elevation = 4.dp,
        shape = shape,
        spotColor = RoyalMaroon.copy(alpha = 0.28f)
      )
      .clip(shape)
      .background(brush)
      .border(BorderStroke(1.dp, borderColor), shape)
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 10.dp),
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
