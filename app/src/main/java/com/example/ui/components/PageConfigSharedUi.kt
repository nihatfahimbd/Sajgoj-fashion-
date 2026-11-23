package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex
import kotlin.math.roundToInt

fun resolvePageFontFamily(name: String): FontFamily = when (name.trim().lowercase()) {
  "serif", "playfair display", "noto serif bengali" -> FontFamily.Serif
  "monospace", "roboto mono", "jetbrains mono" -> FontFamily.Monospace
  "default", "system" -> FontFamily.Default
  else -> FontFamily.SansSerif
}

val PAGE_FONT_CHOICES = listOf(
  "Sans Serif" to "Sans Serif (Modern)",
  "Hind Siliguri" to "Hind Siliguri",
  "Serif" to "Serif (Classic)",
  "Monospace" to "Monospace (Dev)",
  "Default" to "System Default"
)

private val QUICK_SWATCH_PALETTE = listOf(
  "#000000", "#0F172A", "#1F2937", "#374151", "#64748B", "#94A3B8", "#E2E8F0", "#FFFFFF",
  "#7A1C2B", "#991B1B", "#DC2626", "#E11D48", "#F97316", "#D97706", "#FCD34D", "#FFFAF5",
  "#0F294A", "#1E3A8A", "#2563EB", "#0284C7", "#0EA5E9", "#0D9488", "#047857", "#15803D",
  "#4F46E5", "#7C3AED", "#9333EA", "#F3E2D0", "#FFF8F0", "#F4F6F9", "#CBD5E1", "#B8956A"
)

fun colorToHex(color: Color): String {
  val argb = color.toArgb()
  val r = (argb shr 16) and 0xFF
  val g = (argb shr 8) and 0xFF
  val b = argb and 0xFF
  return String.format("#%02X%02X%02X", r, g, b)
}

@Composable
fun PageColorHexPickerRow(
  label: String,
  hexValue: String,
  testTag: String,
  modifier: Modifier = Modifier,
  onHexChange: (String) -> Unit
) {
  var showPicker by remember { mutableStateOf(false) }
  val resolvedColor = remember(hexValue) { parseColorHex(hexValue, RoyalMaroon) }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Visual Swatch Box (>=48dp touch target)
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = resolvedColor,
      border = BorderStroke(1.5.dp, Color(0xFF94A3B8)),
      shadowElevation = 2.dp,
      modifier = Modifier
        .size(48.dp)
        .clip(RoundedCornerShape(12.dp))
        .clickable { showPicker = true }
        .testTag("${testTag}_swatch")
    ) {
      Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(3.dp)) {
        Icon(
          imageVector = Icons.Default.ColorLens,
          contentDescription = "$label কালার পিকার",
          tint = if ((resolvedColor.red * 0.299f + resolvedColor.green * 0.587f + resolvedColor.blue * 0.114f) > 0.55f) {
            Color(0xFF0F172A)
          } else {
            Color.White
          },
          modifier = Modifier.size(14.dp)
        )
      }
    }

    OutlinedTextField(
      value = hexValue,
      onValueChange = onHexChange,
      label = { Text(label, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
      singleLine = true,
      shape = RoundedCornerShape(10.dp),
      colors = adminOutlinedTextFieldColors(),
      modifier = Modifier
        .weight(1f)
        .testTag("${testTag}_hex")
    )
  }

  if (showPicker) {
    VisualColorPickerDialog(
      title = label,
      initialHex = hexValue,
      onDismiss = { showPicker = false },
      onApply = { nextHex ->
        onHexChange(nextHex)
        showPicker = false
      }
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualColorPickerDialog(
  title: String,
  initialHex: String,
  onDismiss: () -> Unit,
  onApply: (String) -> Unit
) {
  val initial = remember(initialHex) { parseColorHex(initialHex, RoyalMaroon) }
  var r by remember { mutableFloatStateOf((initial.red * 255f)) }
  var g by remember { mutableFloatStateOf((initial.green * 255f)) }
  var b by remember { mutableFloatStateOf((initial.blue * 255f)) }
  var manualHex by remember { mutableStateOf(colorToHex(initial)) }

  val currentColor = Color(
    red = r.roundToInt().coerceIn(0, 255) / 255f,
    green = g.roundToInt().coerceIn(0, 255) / 255f,
    blue = b.roundToInt().coerceIn(0, 255) / 255f
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      shadowElevation = 16.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)

        // Color Preview & Hex
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(currentColor)
              .border(1.5.dp, Color(0xFF94A3B8), RoundedCornerShape(12.dp))
          )
          OutlinedTextField(
            value = manualHex,
            onValueChange = { txt ->
              manualHex = txt
              val parsed = parseColorHex(txt, Color.Unspecified)
              if (parsed != Color.Unspecified) {
                r = parsed.red * 255f
                g = parsed.green * 255f
                b = parsed.blue * 255f
              }
            },
            label = { Text("Hex (#RRGGBB)", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          )
        }

        // Quick Swatches
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          QUICK_SWATCH_PALETTE.forEach { swatchHex ->
            val sc = parseColorHex(swatchHex, Color.Black)
            val isSel = manualHex.equals(swatchHex, ignoreCase = true)
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(sc)
                .border(if (isSel) 2.5.dp else 1.dp, if (isSel) AdminV2CyanDeep else Color(0xFFCBD5E1), CircleShape)
                .clickable {
                  r = sc.red * 255f
                  g = sc.green * 255f
                  b = sc.blue * 255f
                  manualHex = swatchHex
                }
            )
          }
        }

        // RGB Sliders
        RgbChannelSlider("R", r, Color(0xFFEF4444)) { next ->
          r = next
          manualHex = colorToHex(Color(r.roundToInt(), g.roundToInt(), b.roundToInt()))
        }
        RgbChannelSlider("G", g, Color(0xFF10B981)) { next ->
          g = next
          manualHex = colorToHex(Color(r.roundToInt(), g.roundToInt(), b.roundToInt()))
        }
        RgbChannelSlider("B", b, Color(0xFF3B82F6)) { next ->
          b = next
          manualHex = colorToHex(Color(r.roundToInt(), g.roundToInt(), b.roundToInt()))
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
          ) {
            Text("বাতিল")
          }
          Button(
            onClick = { onApply(colorToHex(currentColor)) },
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("প্রয়োগ করুন", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun RgbChannelSlider(
  label: String,
  value: Float,
  tint: Color,
  onValueChange: (Float) -> Unit
) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Black, color = tint, modifier = Modifier.width(16.dp))
    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = 0f..255f,
      colors = SliderDefaults.colors(thumbColor = tint, activeTrackColor = tint),
      modifier = Modifier.weight(1f)
    )
    Text(value.roundToInt().toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
  }
}

@Composable
fun PageIntSliderRow(
  label: String,
  value: Int,
  range: IntRange,
  unit: String = "px",
  onValueChange: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
      Text("$value$unit", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AdminV2CyanDeep)
    }
    Slider(
      value = value.toFloat(),
      onValueChange = { onValueChange(it.roundToInt().coerceIn(range.first, range.last)) },
      valueRange = range.first.toFloat()..range.last.toFloat(),
      colors = SliderDefaults.colors(thumbColor = AdminV2CyanDeep, activeTrackColor = AdminV2Cyan)
    )
  }
}

@Composable
fun PageTextStyleControlRow(
  title: String,
  size: Int,
  sizeRange: IntRange,
  isBold: Boolean,
  isItalic: Boolean,
  onSizeChange: (Int) -> Unit,
  onBoldChange: (Boolean) -> Unit,
  onItalicChange: (Boolean) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFFF8FAFC),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "$title (${size}sp)",
          fontSize = 12.sp,
          fontWeight = FontWeight.ExtraBold,
          color = AdminInkPrimary
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          StyleToggleChip(label = "B", bold = true, selected = isBold, onClick = { onBoldChange(!isBold) })
          StyleToggleChip(label = "I", italic = true, selected = isItalic, onClick = { onItalicChange(!isItalic) })
        }
      }
      Slider(
        value = size.toFloat(),
        onValueChange = { onSizeChange(it.roundToInt().coerceIn(sizeRange.first, sizeRange.last)) },
        valueRange = sizeRange.first.toFloat()..sizeRange.last.toFloat(),
        colors = SliderDefaults.colors(thumbColor = AdminV2CyanDeep, activeTrackColor = AdminV2Cyan)
      )
    }
  }
}

@Composable
private fun StyleToggleChip(
  label: String,
  bold: Boolean = false,
  italic: Boolean = false,
  selected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (selected) AdminV2CyanDeep else Color.White,
    border = BorderStroke(1.dp, if (selected) AdminV2CyanDeep else Color(0xFFCBD5E1)),
    modifier = Modifier
      .size(width = 40.dp, height = 34.dp)
      .clip(RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = if (bold) FontWeight.Black else FontWeight.Medium,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        color = if (selected) Color.White else AdminInkPrimary
      )
    }
  }
}

@Composable
fun PageRenameInputRow(
  label: String,
  value: String,
  onValueChange: (String) -> Unit
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, fontSize = 11.sp) },
    singleLine = true,
    shape = RoundedCornerShape(10.dp),
    colors = adminOutlinedTextFieldColors(),
    modifier = Modifier.fillMaxWidth()
  )
}

@Composable
fun PageOrderItemRow(
  title: String,
  isHidden: Boolean,
  canMoveUp: Boolean,
  canMoveDown: Boolean,
  onMoveUp: () -> Unit,
  onMoveDown: () -> Unit,
  onToggleVisibility: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isHidden) Color(0xFFF1F5F9) else Color.White,
    border = BorderStroke(1.dp, if (isHidden) Color(0xFFCBD5E1) else Color(0xFFBAE6FD)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isHidden) AdminInkMuted else AdminInkPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f)
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onMoveUp, enabled = canMoveUp, modifier = Modifier.size(48.dp)) {
          Icon(
            Icons.Default.ArrowUpward,
            contentDescription = "উপরে নিন",
            tint = if (canMoveUp) AdminV2CyanDeep else Color(0xFFCBD5E1),
            modifier = Modifier.size(18.dp)
          )
        }
        IconButton(onClick = onMoveDown, enabled = canMoveDown, modifier = Modifier.size(48.dp)) {
          Icon(
            Icons.Default.ArrowDownward,
            contentDescription = "নিচে নিন",
            tint = if (canMoveDown) AdminV2CyanDeep else Color(0xFFCBD5E1),
            modifier = Modifier.size(18.dp)
          )
        }
        IconButton(onClick = onToggleVisibility, modifier = Modifier.size(48.dp)) {
          Icon(
            imageVector = if (isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (isHidden) "দৃশ্যমান করুন" else "লুকান",
            tint = if (isHidden) Color(0xFFDC2626) else Color(0xFF059669),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun PageConfigSwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFFF8FAFC))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
      .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
      Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
      Text(subtitle, fontSize = 10.5.sp, color = AdminInkMuted)
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AdminV2CyanDeep),
      modifier = Modifier.testTag(testTag)
    )
  }
}
