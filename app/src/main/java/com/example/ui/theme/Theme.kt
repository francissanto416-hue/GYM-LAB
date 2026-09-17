package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NightGymColorScheme = darkColorScheme(
    primary = GymElectricLime,
    onPrimary = GymBlack,
    primaryContainer = GymSurfaceCard,
    onPrimaryContainer = GymElectricLime,
    secondary = GymNeonCyan,
    onSecondary = GymBlack,
    secondaryContainer = GymSurfaceElevated,
    onSecondaryContainer = GymNeonCyan,
    tertiary = GymElectricAmber,
    onTertiary = GymBlack,
    background = GymBlack,
    onBackground = GymTextPrimary,
    surface = GymSurfaceDark,
    onSurface = GymTextPrimary,
    surfaceVariant = GymSurfaceElevated,
    onSurfaceVariant = GymTextSecondary,
    outline = GymBorderDark,
    error = GymHotRed,
    onError = Color.White
)

private val DayGymColorScheme = lightColorScheme(
    primary = GymLightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001B3E),
    secondary = Color(0xFF007A87),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC7F3F7),
    onSecondaryContainer = Color(0xFF002024),
    tertiary = GymElectricAmber,
    onTertiary = Color.White,
    background = GymLightBg,
    onBackground = GymLightTextPrimary,
    surface = GymLightSurface,
    onSurface = GymLightTextPrimary,
    surfaceVariant = GymLightCard,
    onSurfaceVariant = GymLightTextSecondary,
    outline = Color(0xFFCBD5E1),
    error = GymHotRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to true for Night Gym sessions
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NightGymColorScheme else DayGymColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
