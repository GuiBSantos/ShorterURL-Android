package com.example.shortenerapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortenerapp.R
import com.example.shortenerapp.ui.theme.ArkhipFont
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToNext: () -> Unit,
    isAppDarkTheme: Boolean
) {
    var startAnimation by remember { mutableStateOf(false) }

    val bgImageRes = if (isAppDarkTheme) R.drawable.bg_app_dark else R.drawable.bg_app_light
    val logoImageRes = if (isAppDarkTheme) R.drawable.ic_logo_app_dark else R.drawable.ic_logo_app_light

    val textColor = if (isAppDarkTheme) Color.White else Color(0xFF1E293B)

    val textShadow = Shadow(
        color = if (isAppDarkTheme) Color.Black.copy(0.5f) else Color.Black.copy(0.3f),
        offset = Offset(0f, 4f),
        blurRadius = 8f
    )

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000),
        label = "scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        onNavigateToNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = bgImageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        BoxWithConstraints {
            val logoSize = if (maxWidth > 600.dp) 180.dp else 140.dp

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            ) {
                Image(painter = painterResource(id = logoImageRes), contentDescription = "Logo", modifier = Modifier.size(logoSize))
                Spacer(modifier = Modifier.height(24.dp))
                Text("SHORTEN", style = TextStyle(fontFamily = ArkhipFont, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor, letterSpacing = 2.sp, shadow = textShadow))
            }
        }
    }
}