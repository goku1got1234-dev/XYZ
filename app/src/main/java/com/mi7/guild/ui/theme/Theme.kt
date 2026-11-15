package com.mi7.guild.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OledBlack = Color(0xFF000000)
val Charcoal = Color(0xFF121214)
val CharcoalHigh = Color(0xFF1C1C20)
val NeonRed = Color(0xFFFF2A3D)
val Gold = Color(0xFFFFC53D)
val TextPrimary = Color(0xFFF2F2F4)
val TextMuted = Color(0xFF9A9AA3)

private val Scheme = darkColorScheme(
    primary = NeonRed, onPrimary = Color.White,
    secondary = Gold, onSecondary = Color.Black,
    background = OledBlack, onBackground = TextPrimary,
    surface = Charcoal, onSurface = TextPrimary,
    surfaceVariant = CharcoalHigh, onSurfaceVariant = TextMuted,
    outline = Color(0xFF2E2E34)
)

@Composable
fun MI7Theme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = Scheme, typography = Typography(), content = content)
