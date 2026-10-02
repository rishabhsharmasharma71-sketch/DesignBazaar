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
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = BrandSecondaryVariant,
    onSecondaryContainer = Color.White,
    tertiary = BrandAccent,
    background = DarkSurface,
    surface = DarkSurfaceElevated,
    surfaceVariant = DarkSurfaceBorder,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = BrandPrimaryVariant,
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = BrandSecondaryVariant,
    tertiary = BrandAccent,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded DesignBazaar colors consistent
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
