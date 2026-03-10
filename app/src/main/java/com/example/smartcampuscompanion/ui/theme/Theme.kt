package com.example.smartcampuscompanion.ui.theme

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

/* 🌙 Dark Theme - Beige Based */
private val DarkColorScheme = darkColorScheme(
    primary = BeigePrimaryDark,
    secondary = BeigeSecondaryDark,
    tertiary = Pink80,
    background = BeigeBackgroundDark,
    surface = BeigeCardDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = BeigeSurfaceDark,
    onSurfaceVariant = Color.LightGray
)

/* ☀️ Light Theme - Beige Based */
private val LightColorScheme = lightColorScheme(
    primary = BeigePrimary,
    secondary = BeigeSecondary,
    tertiary = Pink40,
    background = BeigeBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surfaceVariant = BeigeAccent.copy(alpha = 0.1f)
)

@Composable
fun SmartCampusCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set too false to force our Beige theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
