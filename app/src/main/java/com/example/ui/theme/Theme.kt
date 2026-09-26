package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GuardPrimaryCyan,
    onPrimary = GuardNavyDark,
    primaryContainer = GuardCardDark,
    onPrimaryContainer = GuardPrimaryCyan,
    secondary = GuardSecondaryBlue,
    onSecondary = GuardNavyDark,
    tertiary = GuardSafeGreen,
    background = GuardNavyDark,
    surface = GuardSurfaceDark,
    surfaceVariant = GuardCardDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = GuardBorderDark,
    error = GuardEmergencyRed,
    onError = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = GuardPrimaryCyanDark,
    onPrimary = GuardLightSurface,
    primaryContainer = GuardLightCard,
    onPrimaryContainer = GuardPrimaryCyanDark,
    secondary = GuardSecondaryBlue,
    onSecondary = GuardLightSurface,
    tertiary = GuardSafeGreen,
    background = GuardLightBackground,
    surface = GuardLightSurface,
    surfaceVariant = GuardLightCard,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = GuardLightBorder,
    error = GuardEmergencyRed,
    onError = GuardLightSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek cybersecurity dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
