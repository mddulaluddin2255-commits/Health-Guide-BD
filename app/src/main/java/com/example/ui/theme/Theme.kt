package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HealthPrimaryThemeDark,
    onPrimary = HealthPrimaryDark,
    primaryContainer = HealthPrimaryContainerDark,
    onPrimaryContainer = HealthPrimaryThemeDark,
    secondary = HealthSecondary,
    secondaryContainer = HealthSecondaryContainer,
    tertiary = HealthTertiary,
    background = HealthBackgroundDark,
    surface = HealthSurfaceDark,
    surfaceVariant = HealthSurfaceVariantDark,
    onBackground = HealthOnSurfaceDark,
    onSurface = HealthOnSurfaceDark,
    onSurfaceVariant = HealthOnSurfaceVariantDark,
    outline = HealthSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = HealthPrimary,
    onPrimary = HealthSurface,
    primaryContainer = HealthPrimaryContainer,
    onPrimaryContainer = HealthOnPrimaryContainer,
    secondary = HealthSecondary,
    secondaryContainer = HealthSecondaryContainer,
    onSecondaryContainer = HealthOnSecondaryContainer,
    tertiary = HealthTertiary,
    tertiaryContainer = HealthTertiaryContainer,
    onTertiaryContainer = HealthOnTertiaryContainer,
    background = HealthBackground,
    surface = HealthSurface,
    surfaceVariant = HealthSurfaceVariant,
    onBackground = HealthOnSurface,
    onSurface = HealthOnSurface,
    onSurfaceVariant = HealthOnSurfaceVariant,
    outline = HealthOutlineVariant
)

@Composable
fun HealthGuideBDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep brand healthcare colors consistent
    content: @Composable () -> Unit
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
