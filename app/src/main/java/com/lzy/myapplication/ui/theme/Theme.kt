package com.lzy.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 浅色主题配置
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E0FA),
    onPrimaryContainer = PrimaryBlueDark,
    secondary = AccentGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5F5E3),
    onSecondaryContainer = AccentGreenDark,
    tertiary = AccentOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFDEBD0),
    onTertiaryContainer = AccentOrangeDark,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE8ECF1),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFC0CDE0),
    outlineVariant = Color(0xFFD8E0EC)
)

// 深色主题配置
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6C8EE0),
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF58D68D),
    onSecondary = Color.White,
    secondaryContainer = AccentGreenDark,
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFF5B041),
    onTertiary = Color.White,
    tertiaryContainer = AccentOrangeDark,
    onTertiaryContainer = Color.White,
    background = BackgroundDark,
    onBackground = Color(0xFFE0E0E0),
    surface = SurfaceDark,
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF505050),
    outlineVariant = Color(0xFF404040)
)

/**
 * App 主题 🎨
 * 自动跟随系统深色/浅色模式
 */
@Composable
fun MeetingTheme(
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
