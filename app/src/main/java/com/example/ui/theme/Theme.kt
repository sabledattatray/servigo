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
    primary = PoliBlue,
    secondary = PoliAccentBlue,
    tertiary = PoliGreen,
    background = PoliBg,
    surface = PoliSurface,
    surfaceVariant = PoliSurfaceVariant,
    onPrimary = Color(0xFF002D6E),
    onSecondary = Color(0xFF1A1C1E),
    onBackground = PoliPrimaryText,
    onSurface = PoliPrimaryText,
    onSurfaceVariant = PoliSecondaryText,
    outline = PoliBorder,
    error = PoliAlertBg,
    onError = PoliAlertText
)

private val LightColorScheme = DarkColorScheme

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
