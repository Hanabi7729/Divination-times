package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MysticColorScheme = darkColorScheme(
    primary = Color(0xFFA855F7),
    onPrimary = Color(0xFF1E1035),
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = Color(0xFF22D3EE),
    onSecondary = Color(0xFF083344),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF451A03),
    background = Color(0xFF090A18),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F1026),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF181B3E),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF333866)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MysticColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF090A18).toArgb()
            window.navigationBarColor = Color(0xFF0F1026).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
