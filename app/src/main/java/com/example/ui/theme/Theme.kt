package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = AntiqueGold,
  onPrimary = DarkEspresso,
  primaryContainer = RoyalMaroonDark,
  onPrimaryContainer = CreamSurface,
  secondary = AntiqueGoldDark,
  onSecondary = Color.White,
  background = Color(0xFF1A110F),
  surface = Color(0xFF261916),
  onBackground = CreamBackground,
  onSurface = CreamBackground,
  outline = AntiqueGoldDark
)

private val LightColorScheme = lightColorScheme(
  primary = RoyalMaroon,
  onPrimary = Color.White,
  primaryContainer = CreamSurface,
  onPrimaryContainer = RoyalMaroon,
  secondary = AntiqueGold,
  onSecondary = DarkEspresso,
  secondaryContainer = CreamSurface,
  onSecondaryContainer = DarkEspresso,
  background = CreamBackground,
  surface = Color.White,
  onBackground = DarkEspresso,
  onSurface = DarkEspresso,
  outline = BorderGold
)

@Composable
fun SajgojFashionTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
