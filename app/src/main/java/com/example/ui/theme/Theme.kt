package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeMode

private val CyberNeonColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF003844),
    onPrimaryContainer = NeonCyan,
    secondary = NeonPink,
    onSecondary = Color(0xFF330018),
    secondaryContainer = Color(0xFF4D0025),
    onSecondaryContainer = NeonPink,
    tertiary = NeonPurple,
    onTertiary = Color.White,
    background = CyberBg,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberCardBorder
)

private val OledVoidColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF141E26),
    onPrimaryContainer = NeonCyan,
    secondary = NeonPink,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF26141E),
    onSecondaryContainer = NeonPink,
    tertiary = NeonGold,
    onTertiary = Color.Black,
    background = OledBg,
    onBackground = Color.White,
    surface = OledSurface,
    onSurface = Color.White,
    surfaceVariant = OledSurfaceVariant,
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = OledCardBorder
)

private val MatrixColorScheme = darkColorScheme(
    primary = MatrixAccent,
    onPrimary = Color(0xFF02170B),
    primaryContainer = Color(0xFF07381B),
    onPrimaryContainer = MatrixAccent,
    secondary = NeonCyan,
    onSecondary = Color(0xFF002026),
    secondaryContainer = Color(0xFF003844),
    onSecondaryContainer = NeonCyan,
    tertiary = NeonGreen,
    onTertiary = Color.Black,
    background = MatrixBg,
    onBackground = Color(0xFFE2F7EB),
    surface = MatrixSurface,
    onSurface = Color(0xFFE2F7EB),
    surfaceVariant = MatrixSurfaceVariant,
    onSurfaceVariant = Color(0xFF82B89A),
    outline = MatrixCardBorder
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.CYBER_NEON,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeMode) {
        ThemeMode.CYBER_NEON -> CyberNeonColorScheme
        ThemeMode.OLED_VOID -> OledVoidColorScheme
        ThemeMode.MATRIX -> MatrixColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
