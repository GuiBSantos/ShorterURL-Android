import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shortenerapp.ui.theme.DarkBorderColor
import com.example.shortenerapp.ui.theme.DarkGlassColor
import com.example.shortenerapp.ui.theme.LightGlassColor

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val glassColor = if (isDark) DarkGlassColor else LightGlassColor

    val borderWidth = if (isDark) 1.dp else 0.dp
    val borderColor = if (isDark) DarkBorderColor else Color.Transparent

    val shimmerGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = if (isDark) 0.05f else 0.4f),
            Color.Transparent
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(glassColor)
            .background(shimmerGradient)
            .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
            .padding(24.dp)
    ) {
        content()
    }
}