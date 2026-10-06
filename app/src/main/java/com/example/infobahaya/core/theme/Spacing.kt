package com.example.infobahaya.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class InfoBahayaSpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp,
    val buttonHeight: Dp = 52.dp,
    val inputHeight: Dp = 56.dp,
    val cardPadding: Dp = 16.dp,
    val screenPadding: Dp = 20.dp
)

val LocalSpacing = staticCompositionLocalOf { InfoBahayaSpacing() }
