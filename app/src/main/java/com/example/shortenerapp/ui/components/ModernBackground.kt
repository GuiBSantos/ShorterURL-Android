package com.example.shortenerapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun ModernBackground(
    isDark: Boolean,
    content: @Composable () -> Unit
) {

    val darkBgColor = Color(0xFF050511)
    val darkOrb1 = Color(0xFFFF0080)
    val darkOrb2 = Color(0xFF7928CA)
    val darkOrb3 = Color(0xFF00C6FF)

    val lightBgColor = Color(0xFFF0F2F5)
    val lightOrb1 = Color(0xFFFFAFCC)
    val lightOrb2 = Color(0xFFA2D2FF)
    val lightOrb3 = Color(0xFFBDE0FE)

    val bgColor = if (isDark) darkBgColor else lightBgColor
    val orb1Color = if (isDark) darkOrb1 else lightOrb1
    val orb2Color = if (isDark) darkOrb2 else lightOrb2
    val orb3Color = if (isDark) darkOrb3 else lightOrb3

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orb1Color.copy(alpha = if (isDark) 0.4f else 0.6f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = 1000f
                ),
                center = Offset(0f, 0f),
                radius = 1000f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orb2Color.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(size.width, size.height * 0.5f),
                    radius = 800f
                ),
                center = Offset(size.width, size.height * 0.5f),
                radius = 800f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orb3Color.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height),
                    radius = 900f
                ),
                center = Offset(size.width * 0.2f, size.height),
                radius = 900f
            )
        }
        content()
    }
}