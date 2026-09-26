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
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PairedDevice
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen
import com.example.ui.theme.GuardWarningAmber
import kotlin.random.Random

@Composable
fun EmergencyVerificationDialog(
    device: PairedDevice?,
    onDismiss: () -> Unit,
    onStopEmergency: () -> Unit
) {
    var isFrontCamera by remember { mutableStateOf(false) }
    var isSirenPlaying by remember { mutableStateOf(false) }
    var micDecibels by remember { mutableStateOf(48) }

    val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveOffset"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .testTag("emergency_verification_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GuardNavyDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GuardEmergencyRed)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GuardEmergencyRed.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Darurat",
                                    tint = GuardEmergencyRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VERIFIKASI SITUASI DARURAT",
                                color = GuardEmergencyRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = device?.name ?: "Perangkat Terhubung",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_emergency_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legal/Safety Consent Notice
                Surface(
                    color = Color(0x33EF4444),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66EF4444))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = GuardEmergencyRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Akses audio & visual aktif dalam protokol darurat resmi. Notifikasi status darurat ditampilkan transparan pada perangkat sasaran.",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated Viewfinder Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1A28))
                        .border(1.dp, Color(0xFF223A5B), RoundedCornerShape(16.dp))
                ) {
                    // Viewfinder grid & HUD
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 1f
                        val gridCol = Color(0x22FFFFFF)
                        drawLine(gridCol, Offset(size.width / 3f, 0f), Offset(size.width / 3f, size.height), strokeWidth = stroke)
                        drawLine(gridCol, Offset(2 * size.width / 3f, 0f), Offset(2 * size.width / 3f, size.height), strokeWidth = stroke)
                        drawLine(gridCol, Offset(0f, size.height / 2f), Offset(size.width, size.height / 2f), strokeWidth = stroke)

                        // Crosshair in center
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val chLen = 16.dp.toPx()
                        drawLine(Color(0x9900D1C1), Offset(cx - chLen, cy), Offset(cx + chLen, cy), strokeWidth = 2f)
                        drawLine(Color(0x9900D1C1), Offset(cx, cy - chLen), Offset(cx, cy + chLen), strokeWidth = 2f)
                    }

                    // Live camera label & lens switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xBB000000),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(GuardEmergencyRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE CAM: ${if (isFrontCamera) "DEPAN (1080p)" else "BELAKANG (4K)"}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = { isFrontCamera = !isFrontCamera },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xBB000000), CircleShape)
                                .testTag("switch_camera_lens_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Ganti Lensa Kamera",
                                tint = GuardPrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Bottom info HUD
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(10.dp),
                        color = Color(0xCC000000),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ENKRIPSI: AES-256-GCM REALTIME",
                                color = GuardPrimaryCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "FPS: 30 | BITRATE: 1.4 Mbps",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Audio Environmental Meter (Microphone VU)
                Surface(
                    color = Color(0xFF132235),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223A5B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Mikrofon",
                                    tint = GuardPrimaryCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sensor Suara Sekitar (Mikrofon)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "${(micDecibels * waveOffset).toInt()} dB",
                                color = GuardPrimaryCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual VU Meter Bars
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val barCount = 20
                            val activeBars = (barCount * waveOffset).toInt()
                            for (i in 0 until barCount) {
                                val isHigh = i > 15
                                val isMed = i > 10
                                val barColor = when {
                                    i < activeBars && isHigh -> GuardEmergencyRed
                                    i < activeBars && isMed -> GuardWarningAmber
                                    i < activeBars -> GuardSafeGreen
                                    else -> Color(0xFF1E2E44)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(barColor)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Loud Siren + Deactivate SOS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { isSirenPlaying = !isSirenPlaying },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("loud_siren_trigger_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSirenPlaying) GuardEmergencyRed else Color(0xFF223A5B)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isSirenPlaying) Icons.Default.NotificationsActive else Icons.Default.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSirenPlaying) "Sirene Berbunyi" else "Dering Keras",
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onStopEmergency()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("resolve_emergency_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GuardSafeGreen)
                    ) {
                        Text(
                            text = "Akhiri Darurat",
                            color = GuardSafeGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
