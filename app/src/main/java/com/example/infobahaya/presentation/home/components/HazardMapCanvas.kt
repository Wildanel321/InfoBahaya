package com.example.infobahaya.presentation.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.infobahaya.core.theme.PrimaryBlue
import com.example.infobahaya.core.theme.SafetyNavy800
import com.example.infobahaya.core.theme.SafetyNavy900
import com.example.infobahaya.core.theme.SeverityEmergency
import com.example.infobahaya.domain.model.HazardReport
import com.example.infobahaya.domain.model.HazardSeverity
import com.example.infobahaya.presentation.home.MapLayerType
import kotlin.math.abs
import kotlin.math.hypot

@Composable
fun HazardMapCanvas(
    reports: List<HazardReport>,
    selectedReport: HazardReport?,
    onSelectReport: (HazardReport?) -> Unit,
    userLatitude: Double,
    userLongitude: Double,
    layerType: MapLayerType,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Pulse animation for active emergency markers and user GPS
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_map")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Center reference coordinate: Jakarta (-6.21, 106.83)
    val centerLat = -6.2100
    val centerLng = 106.8300
    val coordScale = 1400.0 // Pixels per degree

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.6f, 3.5f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            .pointerInput(reports, scale, offsetX, offsetY) {
                detectTapGestures { tapOffset ->
                    // Find tapped marker by hit testing in screen coordinates
                    val boundsCenterX = size.width / 2f + offsetX
                    val boundsCenterY = size.height / 2f + offsetY

                    var closest: HazardReport? = null
                    var minDist = Float.MAX_VALUE
                    val touchThreshold = 44f * scale.coerceAtLeast(1f)

                    for (report in reports) {
                        val screenX = boundsCenterX + ((report.longitude - centerLng) * coordScale * scale).toFloat()
                        val screenY = boundsCenterY + ((centerLat - report.latitude) * coordScale * scale).toFloat()
                        val dist = hypot(tapOffset.x - screenX, tapOffset.y - screenY)

                        if (dist < touchThreshold && dist < minDist) {
                            minDist = dist
                            closest = report
                        }
                    }

                    onSelectReport(closest)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f + offsetX
            val centerY = height / 2f + offsetY

            // 1. Draw Map Base according to Layer Type
            when (layerType) {
                MapLayerType.STANDARD -> {
                    drawRect(Color(0xFFE2E8F0)) // Light slate city base
                    drawMapRoadNetwork(centerX, centerY, scale, isDark = false)
                }
                MapLayerType.SATELLITE -> {
                    drawRect(SafetyNavy900) // Deep dark satellite tile
                    drawMapRoadNetwork(centerX, centerY, scale, isDark = true)
                }
                MapLayerType.HEATMAP -> {
                    drawRect(Color(0xFF0F172A))
                    drawMapRoadNetwork(centerX, centerY, scale, isDark = true)
                    // Draw Heatmap Risk Blobs around reports
                    reports.forEach { r ->
                        val screenX = centerX + ((r.longitude - centerLng) * coordScale * scale).toFloat()
                        val screenY = centerY + ((centerLat - r.latitude) * coordScale * scale).toFloat()
                        val heatColor = when (r.severity) {
                            HazardSeverity.DARURAT -> Color(0xFFEF4444)
                            HazardSeverity.TINGGI -> Color(0xFFF97316)
                            HazardSeverity.SEDANG -> Color(0xFFF59E0B)
                            HazardSeverity.RENDAH -> Color(0xFF10B981)
                        }
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(heatColor.copy(alpha = 0.45f), Color.Transparent),
                                center = Offset(screenX, screenY),
                                radius = 90f * scale
                            ),
                            radius = 90f * scale,
                            center = Offset(screenX, screenY)
                        )
                    }
                }
            }

            // 2. Draw User Location GPS Beacon
            val userScreenX = centerX + ((userLongitude - centerLng) * coordScale * scale).toFloat()
            val userScreenY = centerY + ((centerLat - userLatitude) * coordScale * scale).toFloat()

            // User pulse
            drawCircle(
                color = PrimaryBlue.copy(alpha = pulseAlpha),
                radius = pulseRadius * scale.coerceIn(0.8f, 1.4f),
                center = Offset(userScreenX, userScreenY)
            )
            // Outer white ring
            drawCircle(
                color = Color.White,
                radius = 11f * scale.coerceIn(0.8f, 1.3f),
                center = Offset(userScreenX, userScreenY)
            )
            // Inner blue dot
            drawCircle(
                color = PrimaryBlue,
                radius = 7.5f * scale.coerceIn(0.8f, 1.3f),
                center = Offset(userScreenX, userScreenY)
            )

            // 3. Draw Hazard Markers
            reports.forEach { report ->
                val screenX = centerX + ((report.longitude - centerLng) * coordScale * scale).toFloat()
                val screenY = centerY + ((centerLat - report.latitude) * coordScale * scale).toFloat()

                // Skip drawing if outside viewport with buffer
                if (screenX < -60 || screenX > width + 60 || screenY < -60 || screenY > height + 60) {
                    return@forEach
                }

                val isSelected = selectedReport?.id == report.id
                val isEmergency = report.severity == HazardSeverity.DARURAT

                // Emergency Ripple Animation
                if (isEmergency) {
                    drawCircle(
                        color = SeverityEmergency.copy(alpha = pulseAlpha * 0.7f),
                        radius = (pulseRadius + 10f) * scale.coerceIn(0.8f, 1.3f),
                        center = Offset(screenX, screenY)
                    )
                }

                // Selected Target Highlight Ring
                if (isSelected) {
                    drawCircle(
                        color = PrimaryBlue.copy(alpha = 0.35f),
                        radius = 28f * scale.coerceIn(0.8f, 1.3f),
                        center = Offset(screenX, screenY)
                    )
                    drawCircle(
                        color = PrimaryBlue,
                        radius = 22f * scale.coerceIn(0.8f, 1.3f),
                        center = Offset(screenX, screenY),
                        style = Stroke(width = 8f)
                    )
                }

                // Draw Custom Pin Marker
                drawHazardPin(
                    center = Offset(screenX, screenY),
                    severity = report.severity,
                    isSelected = isSelected,
                    scale = scale
                )
            }
        }
    }
}

private fun DrawScope.drawMapRoadNetwork(centerX: Float, centerY: Float, scale: Float, isDark: Boolean) {
    val roadColor = if (isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1)
    val arterialColor = if (isDark) Color(0xFF334155) else Color(0xFF94A3B8)
    val riverColor = if (isDark) Color(0xFF0C4A6E) else Color(0xFFBAE6FD)

    // River Line (Ciliwung corridor simulation)
    val riverPath = Path().apply {
        moveTo(centerX - 350f * scale, centerY - 600f * scale)
        cubicTo(
            centerX - 100f * scale, centerY - 200f * scale,
            centerX + 80f * scale, centerY + 100f * scale,
            centerX + 200f * scale, centerY + 600f * scale
        )
    }
    drawPath(riverPath, riverColor, style = Stroke(width = 24f * scale))

    // Grid Arterials (Sudirman - Thamrin - Rasuna Said grid)
    val gridStep = 90f * scale
    for (i in -8..8) {
        val y = centerY + i * gridStep
        drawLine(
            color = roadColor,
            start = Offset(centerX - 800f * scale, y),
            end = Offset(centerX + 800f * scale, y),
            strokeWidth = 3f * scale
        )

        val x = centerX + i * gridStep
        drawLine(
            color = roadColor,
            start = Offset(x, centerY - 800f * scale),
            end = Offset(x, centerY + 800f * scale),
            strokeWidth = 3f * scale
        )
    }

    // Main Diagonal Arterials (Gatot Subroto & Thamrin)
    drawLine(
        color = arterialColor,
        start = Offset(centerX - 600f * scale, centerY - 500f * scale),
        end = Offset(centerX + 600f * scale, centerY + 500f * scale),
        strokeWidth = 8f * scale
    )
    drawLine(
        color = arterialColor,
        start = Offset(centerX - 600f * scale, centerY + 300f * scale),
        end = Offset(centerX + 600f * scale, centerY - 300f * scale),
        strokeWidth = 8f * scale
    )
}

private fun DrawScope.drawHazardPin(
    center: Offset,
    severity: HazardSeverity,
    isSelected: Boolean,
    scale: Float
) {
    val baseRadius = (if (isSelected) 16f else 13f) * scale.coerceIn(0.75f, 1.35f)
    val pinColor = severity.color

    // Pin shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.25f),
        radius = baseRadius + 2f,
        center = Offset(center.x, center.y + 2f)
    )

    // Outer white halo
    drawCircle(
        color = Color.White,
        radius = baseRadius + 3f,
        center = center
    )

    // Inner filled color body
    drawCircle(
        color = pinColor,
        radius = baseRadius,
        center = center
    )

    // Center icon dot / accent
    drawCircle(
        color = Color.White,
        radius = baseRadius * 0.42f,
        center = center
    )
}
