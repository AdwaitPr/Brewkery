package com.clickretina.brewkery.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = CremaSurface,
    primaryContainer = CremaSoft,
    onPrimaryContainer = TerracottaDark,
    secondary = Espresso,
    onSecondary = CremaSurface,
    background = Crema,
    onBackground = Espresso,
    surface = CremaSurface,
    onSurface = Espresso,
    surfaceVariant = CremaSoft,
    onSurfaceVariant = TextMuted,
    outline = CremaBorder
)

@Composable
fun BrewkeryTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Crema.toArgb()
            window.navigationBarColor = Crema.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
