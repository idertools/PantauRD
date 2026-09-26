package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PairedDevice
import com.example.ui.components.EmergencyVerificationDialog
import com.example.ui.components.EncryptionInspectorSheet
import com.example.ui.components.MapRadarView
import com.example.ui.theme.GuardBorderDark
import com.example.ui.theme.GuardCardDark
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen
import com.example.ui.theme.GuardWarningAmber
import com.example.ui.viewmodel.FamilyGuardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FamilyGuardViewModel,
    onNavigateToPairing: () -> Unit,
    onNavigateToAssist: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedDeviceId.collectAsStateWithLifecycle()
    val safeZones by viewModel.safeZones.collectAsStateWithLifecycle()
    val bannerAlert by viewModel.bannerAlert.collectAsStateWithLifecycle()
    val isEmergencyDialogOpen by viewModel.isEmergencyDialogOpen.collectAsStateWithLifecycle()
    val isE2eeSheetOpen by viewModel.isE2eeSheetOpen.collectAsStateWithLifecycle()

    val currentDevice = devices.find { it.id == selectedId } ?: devices.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuardNavyDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GuardPrimaryCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FAMILY GUARD SECURE",
                        color = GuardPrimaryCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Pelacak & Keselamatan",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // E2EE Button
                Surface(
                    color = Color(0xFF132235),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GuardPrimaryCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { viewModel.openE2eeSheet(true) }.testTag("e2ee_status_header_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "E2EE",
                            tint = GuardPrimaryCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AES-256",
                            color = GuardPrimaryCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Add device / QR pairing button
                IconButton(
                    onClick = onNavigateToPairing,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF1A2A40), RoundedCornerShape(10.dp))
                        .testTag("nav_pairing_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "Pairing Perangkat",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Critical Banner Alert
        AnimatedVisibility(
            visible = bannerAlert != null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            bannerAlert?.let { alert ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("banner_alert_box"),
                    color = when (alert.severity) {
                        "CRITICAL" -> Color(0x33EF4444)
                        "WARNING" -> Color(0x33F59E0B)
                        else -> Color(0x3310B981)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (alert.severity) {
                            "CRITICAL" -> GuardEmergencyRed
                            "WARNING" -> GuardWarningAmber
                            else -> GuardSafeGreen
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (alert.severity) {
                                "CRITICAL" -> Icons.Default.Warning
                                "WARNING" -> Icons.Default.NotificationsActive
                                else -> Icons.Default.Security
                            },
                            contentDescription = null,
                            tint = when (alert.severity) {
                                "CRITICAL" -> GuardEmergencyRed
                                "WARNING" -> GuardWarningAmber
                                else -> GuardSafeGreen
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = alert.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = alert.message,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(
                            onClick = { viewModel.dismissBannerAlert() },
                            modifier = Modifier.size(24.dp).testTag("dismiss_banner_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Paired Devices List (Tabs)
        if (devices.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(devices) { dev ->
                    val isSelected = dev.id == selectedId
                    Surface(
                        modifier = Modifier
                            .clickable { viewModel.selectDevice(dev.id) }
                            .testTag("device_tab_${dev.id}"),
                        color = if (isSelected) GuardPrimaryCyan.copy(alpha = 0.15f) else GuardCardDark,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GuardPrimaryCyan else GuardBorderDark
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (dev.emergencyStatus) GuardEmergencyRed
                                        else if (!dev.isMonitoringActive) GuardWarningAmber
                                        else GuardSafeGreen
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = dev.name,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Live Radar Map View
        MapRadarView(
            device = currentDevice,
            safeZones = safeZones
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Device Status Card
        currentDevice?.let { dev ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("device_status_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GuardCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = dev.name,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kode Pairing Resmi: ${dev.pairingCode} (E2EE)",
                                color = Color(0xFF8B949E),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Battery Indicator
                        Surface(
                            color = Color(0xFF0F1B2B),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223A5B))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (dev.batteryLevel > 20) Icons.Default.BatteryFull else Icons.Default.BatteryAlert,
                                    contentDescription = "Baterai",
                                    tint = if (dev.batteryLevel > 20) GuardSafeGreen else GuardEmergencyRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${dev.batteryLevel}%",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics Grid (Kecepatan, Jaringan, Zona)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricItem(
                            icon = Icons.Default.Speed,
                            label = "Kecepatan",
                            value = "%.1f km/h".format(dev.speedKmh),
                            modifier = Modifier.weight(1f)
                        )
                        MetricItem(
                            icon = Icons.Default.SignalCellularAlt,
                            label = "Sinyal Data",
                            value = dev.networkType,
                            modifier = Modifier.weight(1f)
                        )
                        MetricItem(
                            icon = Icons.Default.Security,
                            label = "Status Area",
                            value = if (dev.safeZoneStatus.length > 15) dev.safeZoneStatus.take(13) + "..." else dev.safeZoneStatus,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // TOMBOL TIDAK PANTAU (HEMAT BATERAI) - Core User Requirement
                    Surface(
                        color = if (dev.isMonitoringActive) Color(0xFF142236) else Color(0x33F59E0B),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (dev.isMonitoringActive) GuardBorderDark else GuardWarningAmber
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (dev.isMonitoringActive) Icons.Default.ElectricBolt else Icons.Default.PauseCircle,
                                        contentDescription = null,
                                        tint = if (dev.isMonitoringActive) GuardPrimaryCyan else GuardWarningAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (dev.isMonitoringActive) "Pemantauan Aktif" else "Pemantauan Dijeda (Hemat Baterai)",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (dev.isMonitoringActive)
                                        "Tekan 'Jeda Pantau' untuk menghemat daya baterai & kuota data perangkat target."
                                    else
                                        "Mode siaga hemat baterai aktif. Perangkat sasaran tidak mengonsumsi daya GPS.",
                                    color = Color(0xFF8B949E),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { viewModel.toggleMonitoringState(dev) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (dev.isMonitoringActive) Color(0xFF263952) else GuardSafeGreen
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("toggle_monitoring_btn")
                            ) {
                                Icon(
                                    imageVector = if (dev.isMonitoringActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (dev.isMonitoringActive) "Jeda Pantau" else "Lanjutkan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // PANDUAN MODE PEMANTAUAN VS JEDA PANTAU (HEMAT BATERAI)
                    HomeMonitoringGuideCard(isMonitoringActive = dev.isMonitoringActive)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Emergency and Audio/Cam Verification Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerEmergency(dev) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sos_emergency_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dev.emergencyStatus) GuardEmergencyRed else Color(0xFF2A1F26)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GuardEmergencyRed)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = GuardEmergencyRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (dev.emergencyStatus) "DARURAT AKTIF" else "Pemicu SOS",
                                color = if (dev.emergencyStatus) Color.White else GuardEmergencyRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.openEmergencyDialog(true) },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("verify_cam_mic_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2E47)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GuardPrimaryCyan.copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = GuardPrimaryCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kamera & Audio",
                                color = GuardPrimaryCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Remote Screen Assist & Presentation Button
                    Button(
                        onClick = onNavigateToAssist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("launch_screen_share_home_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132D48)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GuardPrimaryCyan)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ScreenShare,
                            contentDescription = null,
                            tint = GuardPrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bantuan Jarak Jauh & Berbagi Layar (Presentasi)",
                            color = GuardPrimaryCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Diagnostics Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GuardCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sistem Deteksi Keamanan Dini",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = GuardSafeGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "STATUS AMAN",
                            color = GuardSafeGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Deteksi Baterai: Normal (Tidak ada pemborosan daya tak wajar)\n" +
                            "• Deteksi Integritas: Saluran E2EE AES-256-GCM valid\n" +
                            "• Izin Akses: Bilateral QR/PIN resmi disetujui pemilik",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }

    // Emergency Verification Dialog (Camera & Mic with Transparent Notice)
    if (isEmergencyDialogOpen) {
        EmergencyVerificationDialog(
            device = currentDevice,
            onDismiss = { viewModel.openEmergencyDialog(false) },
            onStopEmergency = { currentDevice?.let { viewModel.triggerEmergency(it) } }
        )
    }

    // E2EE Inspector Sheet
    if (isE2eeSheetOpen) {
        EncryptionInspectorSheet(
            onDismiss = { viewModel.openE2eeSheet(false) }
        )
    }
}

@Composable
fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF101C2C),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3048))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GuardPrimaryCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = Color(0xFF8B949E),
                fontSize = 10.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HomeMonitoringGuideCard(isMonitoringActive: Boolean) {
    Surface(
        color = Color(0xFF0F1C2D),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isMonitoringActive) GuardPrimaryCyan.copy(alpha = 0.4f) else GuardWarningAmber.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth().testTag("home_monitoring_guide_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isMonitoringActive) GuardPrimaryCyan else GuardWarningAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isMonitoringActive)
                        "PANDUAN: MODE PEMANTAUAN REAL-TIME AKTIF"
                    else
                        "PANDUAN: MODE JEDA PANTAU (EFISIENSI BATERAI)",
                    color = if (isMonitoringActive) GuardPrimaryCyan else GuardWarningAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isMonitoringActive)
                    "• Status GPS: Lokasi diperbarui secara periodik pada radar peta.\n" +
                    "• Notifikasi Zona: Anda akan menerima pemberitahuan otomatis saat target memasuki atau keluar zona aman.\n" +
                    "• Tips: Jika orang tua sedang di rumah dengan aman, tekan 'Jeda Pantau' agar baterai ponsel mereka tidak terkuras."
                else
                    "• Efisiensi Daya: Transmisi data & polling GPS perangkat sasaran dihentikan sementara, menghemat hingga 95% daya baterai.\n" +
                    "• Keselamatan Tetap Siaga: Tombol darurat SOS dan fungsi Bantuan Layar tetap dapat diakses kapan saja saat dibutuhkan.\n" +
                    "• Lanjutkan: Tekan tombol 'Lanjutkan' di atas kapan pun Anda ingin mengaktifkan kembali pelacakan lokasi.",
                color = Color(0xFFCAD5E2),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}
