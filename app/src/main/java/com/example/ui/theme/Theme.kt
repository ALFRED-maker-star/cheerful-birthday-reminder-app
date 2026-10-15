package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = PrimaryPink,
  onPrimary = Color.White,
  primaryContainer = PrimaryPinkContainer,
  onPrimaryContainer = OnPrimaryPinkContainer,
  secondary = AccentGold,
  onSecondary = Color(0xFF3E2723),
  secondaryContainer = AccentGoldContainer,
  onSecondaryContainer = OnAccentGoldContainer,
  tertiary = CelebrationPurple,
  onTertiary = Color.White,
  background = SoftPinkBackground,
  onBackground = DarkText,
  surface = CardSurface,
  onSurface = DarkText,
  surfaceVariant = CardSurfaceVariant,
  onSurfaceVariant = SubtitleText,
  outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryPinkLight,
  onPrimary = Color(0xFF50002A),
  primaryContainer = PrimaryPinkDark,
  onPrimaryContainer = PrimaryPinkContainer,
  secondary = AccentGold,
  onSecondary = Color(0xFF452200),
  secondaryContainer = Color(0xFF5A3900),
  onSecondaryContainer = AccentGoldLight,
  tertiary = CelebrationPurple,
  onTertiary = Color.White,
  background = Color(0xFF1E141D),
  onBackground = Color(0xFFFCE7F3),
  surface = Color(0xFF281C27),
  onSurface = Color(0xFFFCE7F3),
  surfaceVariant = Color(0xFF3D273A),
  onSurfaceVariant = Color(0xFFE2D0DD),
  outline = Color(0xFF5A3C55)
)

@Composable
fun CelebrateTheme(
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

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  CelebrateTheme(darkTheme = darkTheme, content = content)
}
