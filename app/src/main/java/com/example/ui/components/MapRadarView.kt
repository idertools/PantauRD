package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PairedDevice
import com.example.data.model.SafeZone
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen

@Composable
fun MapRadarView(
    device: PairedDevice?,
    safeZones: List<SafeZone>,
    modifier: Modifier = Modifier
) {
    var mapType by remember { mutableStateOf("RADAR") } // "RADAR" or "SCHEMATIC"

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(GuardNavyDark)
            .border(1.dp, Color(0xFF1E2E44), RoundedCornerShape(20.dp))
            .testTag("map_radar_container")
    ) {
        // Radar / Map Grid Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.minDimension / 2f) * 0.9f

            // Map grid lines
            val gridStep = 40.dp.toPx()
            val gridColor = Color(0x1800D1C1)
            var x = 0f
            while (x <= size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += gridStep
            }
            var y = 0f
            while (y <= size.height) {
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                y += gridStep
            }

            // Radar circular rings
            val ringCount = 4
            for (i in 1..ringCount) {
                val r = (maxRadius / ringCount) * i
                drawCircle(
                    color = Color(0x2800D1C1),
                    radius = r,
                    center = center,
                    style = Stroke(
                        width = 1.2f,
                        pathEffect = if (i % 2 == 0) PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) else null
                    )
                )
            }

            // Animated sweep or ripple
            val currentPulseRadius = maxRadius * pulseProgress
            val pulseColor = if (device?.emergencyStatus == true)
                GuardEmergencyRed.copy(alpha = (1f - pulseProgress) * 0.6f)
            else
                GuardPrimaryCyan.copy(alpha = (1f - pulseProgress) * 0.45f)

            drawCircle(
                color = pulseColor,
                radius = currentPulseRadius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw Safe Zone representations
            safeZones.forEachIndexed { index, zone ->
                val angle = Math.toRadians((index * 110.0) + 40.0)
                val distFactor = (maxRadius * 0.6f)
                val zoneCenter = Offset(
                    center.x + (Math.cos(angle) * distFactor).toFloat(),
                    center.y + (Math.sin(angle) * distFactor).toFloat()
                )

                // Safe zone perimeter circle
                drawCircle(
                    color = GuardSafeGreen.copy(alpha = 0.15f),
                    radius = 35.dp.toPx(),
                    center = zoneCenter
                )
                drawCircle(
                    color = GuardSafeGreen.copy(alpha = 0.6f),
                    radius = 35.dp.toPx(),
                    center = zoneCenter,
                    style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                )
            }

            // Device Pin marker in center
            val pinColor = if (device?.emergencyStatus == true) GuardEmergencyRed else GuardPrimaryCyan
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(pinColor.copy(alpha = 0.9f), pinColor.copy(alpha = 0.1f)),
                    center = center,
                    radius = 24.dp.toPx()
                ),
                radius = 24.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = 7.dp.toPx(),
                center = center
            )
            drawCircle(
                color = pinColor,
                radius = 5.dp.toPx(),
                center = center
            )
        }

        // Top Status HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xDD09111E),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF223A5B))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (device?.emergencyStatus == true) GuardEmergencyRed
                                else if (device?.isMonitoringActive == false) Color(0xFFF59E0B)
                                else GuardSafeGreen
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (device?.emergencyStatus == true) "STATUS: DARURAT SOS"
                        else if (device?.isMonitoringActive == false) "STATUS: JEDA (HEMAT DAYA)"
                        else "LIVE GPS: AKTIF",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Layer / View Switcher
            Surface(
                color = Color(0xDD09111E),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF223A5B))
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { mapType = if (mapType == "RADAR") "SCHEMATIC" else "RADAR" },
                        modifier = Modifier.size(32.dp).testTag("toggle_map_view_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Ganti Tampilan Peta",
                            tint = GuardPrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bottom Telemetry Overlay
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            color = Color(0xEB0D1B2A),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFF223A5B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = device?.addressName ?: "Mendeteksi koordinat GPS...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        text = "Lat: %.4f, Lon: %.4f | Kecepatan: %.1f km/h".format(
                            device?.latitude ?: -6.2088,
                            device?.longitude ?: 106.8456,
                            device?.speedKmh ?: 0f
                        ),
                        color = Color(0xFF8B949E),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Surface(
                    color = Color(0x2200D1C1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Zona",
                            tint = GuardSafeGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Zona Aman",
                            color = GuardSafeGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) =
    androidx.compose.foundation.BorderStroke(width, color)
