package com.example.shortenerapp.ui.theme

import android.app.Activity
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

val LightBgStart = Color(0xFFE0F7FA)
val LightBgEnd = Color(0xFFF3E5F5)
val LightGlassColor = Color.White.copy(alpha = 0.60f)
val LightAccentStart = Color(0xFF4FC3F7)
val LightAccentEnd = Color(0xFFBA68C8)
val LightTextColor = Color(0xFF2C3E50)

val DarkBgStart = Color(0xFF120C34)
val DarkBgEnd = Color(0xFF241744)

val DarkOrb1 = Color(0xFF6200EA).copy(alpha = 0.5f)
val DarkOrb2 = Color(0xFFC51162).copy(alpha = 0.5f)
val DarkGlassColor = Color(0xFF2D2D2D).copy(alpha = 0.30f)
val DarkBorderColor = Color.White.copy(alpha = 0.2f)
val DarkTextColor = Color(0xFFF5F5F5)
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ShortenerAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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