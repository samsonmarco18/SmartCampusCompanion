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

/* 🔹 Smart Campus Custom Colors (ADDED) */
private val CampusPrimary = Color(0xFF1565C0)
private val CampusSecondary = Color(0xFF42A5F5)
private val CampusBackgroundLight = Color(0xFFF5F7FA)
private val CampusBackgroundDark = Color(0xFF121212)

/* 🌙 Dark Theme */
private val DarkColorScheme = darkColorScheme(
    primary = CampusPrimary,
    secondary = CampusSecondary,
    tertiary = Pink80,
    background = CampusBackgroundDark,
    surface = CampusBackgroundDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color.White
)

/* ☀️ Light Theme */
private val LightColorScheme = lightColorScheme(
    primary = CampusPrimary,
    secondary = CampusSecondary,
    tertiary = Pink40,
    background = CampusBackgroundLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color.Black

    /* Other default colors preserved
    onBackground = Color(0xFF1C1B1F),
    */
)

@Composable
fun SmartCampusCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
        typography = Typography,
        content = content
    )
}
