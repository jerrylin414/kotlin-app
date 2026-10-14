package com.lzy.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = Color.White,
    primaryContainer = PeachPink,
    onPrimaryContainer = TextPrimary,
    secondary = BabyBlue,
    onSecondary = Color.White,
    secondaryContainer = SoftMint,
    onSecondaryContainer = TextPrimary,
    tertiary = Lavender,
    onTertiary = Color.White,
    tertiaryContainer = CreamYellow,
    onTertiaryContainer = TextPrimary,
    background = Color(0xFFFFFBFF),
    onBackground = TextPrimary,
    surface = Color(0xFFFFFBFF),
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF5F0F4),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFD0C8D0),
    outlineVariant = Color(0xFFE8E0E8)
)

private val DarkColorScheme = darkColorScheme(
    primary = CoralDark,
    onPrimary = Color.White,
    primaryContainer = PeachPinkDark,
    onPrimaryContainer = Color.White,
    secondary = BabyBlueDark,
    onSecondary = Color.White,
    secondaryContainer = SoftMintDark,
    onSecondaryContainer = Color.White,
    tertiary = LavenderDark,
    onTertiary = Color.White,
    tertiaryContainer = CreamYellowDark,
    onTertiaryContainer = Color.White,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2D2B30),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF49454F),
    outlineVariant = Color(0xFF49454F)
)

@Composable
fun PiggyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
