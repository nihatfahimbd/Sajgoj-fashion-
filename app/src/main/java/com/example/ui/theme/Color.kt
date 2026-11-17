package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val RoyalMaroon = Color(0xFF7A1C2B)
val RoyalMaroonDark = Color(0xFF53111C)
val RoyalMaroonLight = Color(0xFF9E2A3E)
val AntiqueGold = Color(0xFFD4A574)
val AntiqueGoldDark = Color(0xFFB8956A)
val CreamBackground = Color(0xFFFFFAF5)
val CreamSurface = Color(0xFFFFF8F0)
val DarkEspresso = Color(0xFF2C1810)
val BorderGold = Color(0xFFF3E2D0)
val CardBorder = Color(0xFFE8D5C0)
val AccentGreen = Color(0xFF2E7D32)
val AccentRed = Color(0xFFC62828)
val NavyBlue = Color(0xFF0F294A)
val NavyBlueDark = Color(0xFF0A1F3A)
val NavyBlueCardEnd = Color(0xFF1E3A8A)
val CollectionRedStart = Color(0xFFEF4444)
val CollectionRedEnd = Color(0xFFDC2626)
val CollectionRedBorder = Color(0xFF991B1B)
val SideRosePillStart = Color(0xFFFB7185)
val SideRosePillEnd = Color(0xFFE11D48)
val ReelsRedBadge = Color(0xFFDC2626)
val SlateTextSecondary = Color(0xFF334155)
val CreamPriceBorder = Color(0xFFF3D5B5)

fun parseColorHex(hex: String?, fallback: Color): Color {
  val trimmed = hex?.trim().orEmpty()
  if (trimmed.isEmpty()) return fallback
  return try {
    val normalized = if (trimmed.startsWith("#")) trimmed else "#$trimmed"
    Color(android.graphics.Color.parseColor(normalized))
  } catch (_: Exception) {
    fallback
  }
}
