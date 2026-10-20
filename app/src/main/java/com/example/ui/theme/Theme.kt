package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1E1700),
    primaryContainer = GoldDark,
    onPrimaryContainer = Color(0xFFFFECC0),
    secondary = CoralAccent,
    onSecondary = Color.White,
    secondaryContainer = CoralDark,
    onSecondaryContainer = Color(0xFFFFD9E2),
    tertiary = VioletAccent,
    onTertiary = Color(0xFF1F1042),
    background = NoirBackground,
    onBackground = TextPrimaryDark,
    surface = NoirSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = NoirSurfaceCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = NoirSurfaceBorder,
    outlineVariant = Color(0xFF242338)
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = Color(0xFF332000),
    secondary = CoralAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9E2),
    onSecondaryContainer = CoralDark,
    tertiary = VioletAccent,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightSurfaceBorder,
    outlineVariant = Color(0xFFE2E4EC)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set default false to preserve sleek custom editorial brand
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
