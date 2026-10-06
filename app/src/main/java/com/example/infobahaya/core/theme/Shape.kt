package com.example.infobahaya.core.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class InfoBahayaShapes(
    val extraSmall: CornerBasedShape = RoundedCornerShape(6.dp),
    val small: CornerBasedShape = RoundedCornerShape(10.dp),
    val medium: CornerBasedShape = RoundedCornerShape(14.dp),
    val large: CornerBasedShape = RoundedCornerShape(20.dp),
    val extraLarge: CornerBasedShape = RoundedCornerShape(28.dp),
    val full: CornerBasedShape = RoundedCornerShape(999.dp),
    val card: CornerBasedShape = RoundedCornerShape(16.dp),
    val button: CornerBasedShape = RoundedCornerShape(14.dp),
    val badge: CornerBasedShape = RoundedCornerShape(8.dp)
)

val MaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

val LocalCustomShapes = staticCompositionLocalOf { InfoBahayaShapes() }
