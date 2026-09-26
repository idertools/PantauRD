package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PairedDevice
import com.example.ui.theme.GuardBorderDark
import com.example.ui.theme.GuardCardDark
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen
import com.example.ui.theme.GuardSecondaryBlue
import com.example.ui.theme.GuardWarningAmber
import com.example.ui.viewmodel.FamilyGuardViewModel

@Composable
fun RemoteAssistScreen(
    viewModel: FamilyGuardViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedDeviceId.collectAsStateWithLifecycle()
    val currentDevice = devices.find { it.id == selectedId } ?: devices.firstOrNull()

    val isScreenShareActive by viewModel.isScreenShareActive.collectAsStateWithLifecycle()
    val screenShareMode by viewModel.screenShareMode.collectAsStateWithLifecycle()
    val currentSlide by viewModel.presentationSlide.collectAsStateWithLifecycle()
    val remotePointerX by viewModel.remotePointerX.collectAsStateWithLifecycle()
    val remotePointerY by viewModel.remotePointerY.collectAsStateWithLifecycle()
    val lastActionText by viewModel.lastRemoteActionText.collectAsStateWithLifecycle()
    val isVoiceAssistActive by viewModel.isVoiceAssistActive.collectAsStateWithLifecycle()
    val isLandscape by viewModel.isRemoteLandscape.collectAsStateWithLifecycle()
    val appRole by viewModel.appRole.collectAsStateWithLifecycle()

    val seniorBannerMessage by viewModel.seniorBannerMessage.collectAsStateWithLifecycle()
    val seniorVolume by viewModel.seniorVolume.collectAsStateWithLifecycle()
    val seniorWifiActive by viewModel.seniorWifiActive.collectAsStateWithLifecycle()
    val seniorFontSize by viewModel.seniorFontSize.collectAsStateWithLifecycle()
    val seniorPhoneFinderRinging by viewModel.seniorPhoneFinderRinging.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuardNavyDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ScreenShare,
                        contentDescription = null,
                        tint = GuardPrimaryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BANTUAN & PRESENTASI KELUARGA",
                        color = GuardPrimaryCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Layar & Kontrol Jarak Jauh",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = if (isScreenShareActive) GuardSafeGreen.copy(alpha = 0.2f) else Color(0xFF132235),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isScreenShareActive) GuardSafeGreen else GuardBorderDark
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isScreenShareActive) GuardSafeGreen else Color(0xFF8B949E))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isScreenShareActive) "LIVE 60FPS" else "SIAGA",
                        color = if (isScreenShareActive) GuardSafeGreen else Color(0xFF8B949E),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Device Selection Bar
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
                            .testTag("remote_assist_device_${dev.id}"),
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
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                tint = if (isSelected) GuardPrimaryCyan else Color(0xFF8B949E),
                                modifier = Modifier.size(16.dp)
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

        if (!isScreenShareActive) {
            // STATE: SCREEN SHARE INACTIVE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_screen_share_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GuardCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GuardPrimaryCyan.copy(alpha = 0.15f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CastConnected,
                                contentDescription = null,
                                tint = GuardPrimaryCyan,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Mulai Bantuan Layar & Presentasi",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Fitur resmi keluarga untuk saling melihat layar secara real-time dengan izin bilateral. " +
                                "Cocok untuk membantu orang tua mengoperasikan HP atau mempresentasikan materi keluarga.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Permission & Security Badges
                    Surface(
                        color = Color(0xFF101B2B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = GuardPrimaryCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Transmisi Video: Terenkripsi AES-256-GCM", color = Color(0xFFCAD5E2), fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, tint = GuardSafeGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Kontrol Bantuan: Laser Pointer & Panduan Sentuhan", color = Color(0xFFCAD5E2), fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            viewModel.startScreenSharing(currentDevice?.name ?: "Perangkat Keluarga")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("launch_screen_share_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = GuardPrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ScreenShare, contentDescription = null, tint = GuardNavyDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mulai Sesi Berbagi Layar (Resmi)",
                            color = GuardNavyDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // STATE: SCREEN SHARE ACTIVE (INTERACTIVE PHONE VIEWPORT)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_screen_viewport_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF09121F)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GuardPrimaryCyan)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Top Viewport Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GuardEmergencyRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LAYAR HP: ${currentDevice?.name ?: "Adik Budi"}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Orientation Switcher
                            IconButton(
                                onClick = { viewModel.toggleRemoteOrientation() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF16253A), CircleShape)
                                    .testTag("toggle_screen_orientation_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ScreenRotation,
                                    contentDescription = "Putar Orientasi",
                                    tint = GuardPrimaryCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Stop Screen Share Button
                            IconButton(
                                onClick = {
                                    viewModel.stopScreenSharing(currentDevice?.name ?: "Perangkat Keluarga")
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0x33EF4444), CircleShape)
                                    .testTag("stop_screen_share_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.StopScreenShare,
                                    contentDescription = "Hentikan",
                                    tint = GuardEmergencyRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Screen Canvas with Interactive Pointer
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(if (isLandscape) 16f / 9f else 9f / 16f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F1A28))
                            .border(2.dp, Color(0xFF1E314B), RoundedCornerShape(16.dp))
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val normX = (offset.x / size.width).coerceIn(0f, 1f)
                                    val normY = (offset.y / size.height).coerceIn(0f, 1f)
                                    viewModel.sendRemoteTouch(normX, normY)
                                }
                            }
                            .testTag("interactive_screen_canvas")
                    ) {
                        val canvasWidth = constraints.maxWidth.toFloat()
                        val canvasHeight = constraints.maxHeight.toFloat()

                        // Render Content inside Simulated Screen
                        when (screenShareMode) {
                            "SENIOR" -> SeniorAssistScreenContent(
                                bannerMessage = seniorBannerMessage,
                                volume = seniorVolume,
                                wifiActive = seniorWifiActive,
                                fontSize = seniorFontSize,
                                isRinging = seniorPhoneFinderRinging,
                                onHelpRequested = {
                                    viewModel.executeSeniorFix("SEND_MESSAGE", "Ibu/Bapak menekan tombol bantuan! Anak sedang merespons.")
                                }
                            )
                            "PRESENTATION" -> PresentationScreenContent(
                                currentSlide = currentSlide,
                                isLandscape = isLandscape
                            )
                            "SETTINGS" -> AndroidSettingsScreenContent()
                            else -> AndroidHomeScreenContent()
                        }

                        // Laser Pointer / Visual Guidance Ripple
                        if (remotePointerX != null && remotePointerY != null) {
                            val px = remotePointerX!! * canvasWidth
                            val py = remotePointerY!! * canvasHeight

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(GuardEmergencyRed.copy(alpha = 0.8f), Color.Transparent),
                                        center = Offset(px, py),
                                        radius = 35.dp.toPx()
                                    ),
                                    radius = 35.dp.toPx(),
                                    center = Offset(px, py)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 8.dp.toPx(),
                                    center = Offset(px, py)
                                )
                                drawCircle(
                                    color = GuardEmergencyRed,
                                    radius = 6.dp.toPx(),
                                    center = Offset(px, py)
                                )
                                drawCircle(
                                    color = GuardEmergencyRed,
                                    radius = 22.dp.toPx(),
                                    center = Offset(px, py),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }

                        // Live Watermark Consent Indicator
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(8.dp),
                            color = Color(0xCC09121F),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Layar Real-time • Sentuh untuk Memandu Keluarga",
                                color = GuardPrimaryCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Selector Tabs (Lansia / Pengaturan / Presentasi / Layar Biasa)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF101B2B))
                    .padding(3.dp)
            ) {
                val modes = listOf(
                    "SENIOR" to "Mode Lansia",
                    "SETTINGS" to "Pengaturan HP",
                    "PRESENTATION" to "Presentasi",
                    "HOME" to "Layar HP"
                )
                modes.forEach { (mKey, mLabel) ->
                    val isSel = screenShareMode == mKey
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setScreenShareMode(mKey) }
                            .testTag("mode_tab_$mKey"),
                        color = if (isSel) GuardPrimaryCyan else Color.Transparent,
                        shape = RoundedCornerShape(9.dp)
                    ) {
                        Text(
                            text = mLabel,
                            color = if (isSel) GuardNavyDark else Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PANDUAN MASING-MASING MODE LAYAR
            RemoteModeGuideCard(mode = screenShareMode)

            // BANTUAN KHUSUS LANSIA (ONE-CLICK SENIOR CARE SUITE)
            Card(
                modifier = Modifier.fillMaxWidth().testTag("senior_care_deck_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14243A)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GuardPrimaryCyan.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GuardPrimaryCyan.copy(alpha = 0.2f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.VolunteerActivism,
                                        contentDescription = null,
                                        tint = GuardPrimaryCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bantuan Sekali-Klik Ramah Lansia",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = GuardSafeGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PEDULI ORANG TUA",
                                color = GuardSafeGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Solusi instan untuk mengatasi masalah umum orang tua yang awam teknologi tanpa membingungkan mereka.",
                        color = Color(0xFFCAD5E2),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6 One-Click Senior Fixes (2 rows of 3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SeniorActionChip(
                            icon = Icons.Default.VolumeUp,
                            title = "Volume 100%",
                            desc = "Dering Paling Keras",
                            color = GuardPrimaryCyan,
                            onClick = { viewModel.executeSeniorFix("MAX_VOLUME") },
                            modifier = Modifier.weight(1f).testTag("senior_fix_volume_btn")
                        )
                        SeniorActionChip(
                            icon = Icons.Default.Wifi,
                            title = "Nyalakan Wi-Fi",
                            desc = "Koneksi Rumah",
                            color = GuardSafeGreen,
                            onClick = { viewModel.executeSeniorFix("CONNECT_WIFI") },
                            modifier = Modifier.weight(1f).testTag("senior_fix_wifi_btn")
                        )
                        SeniorActionChip(
                            icon = Icons.Default.FormatSize,
                            title = "Besarkan Teks",
                            desc = "Font 160% Jelas",
                            color = GuardSecondaryBlue,
                            onClick = { viewModel.executeSeniorFix("LARGE_FONT") },
                            modifier = Modifier.weight(1f).testTag("senior_fix_font_btn")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SeniorActionChip(
                            icon = Icons.Default.NotificationsActive,
                            title = if (seniorPhoneFinderRinging) "Hentikan Dering" else "Cari HP Lupa",
                            desc = if (seniorPhoneFinderRinging) "Dering Aktif" else "Bunyi Kencang",
                            color = if (seniorPhoneFinderRinging) GuardEmergencyRed else GuardWarningAmber,
                            onClick = { viewModel.executeSeniorFix("RING_FINDER") },
                            modifier = Modifier.weight(1f).testTag("senior_fix_finder_btn")
                        )
                        SeniorActionChip(
                            icon = Icons.Default.CleaningServices,
                            title = "Lancar HP",
                            desc = "Bersih RAM & Macet",
                            color = GuardSafeGreen,
                            onClick = { viewModel.executeSeniorFix("CLEAR_MEMORY") },
                            modifier = Modifier.weight(1f).testTag("senior_fix_ram_btn")
                        )
                        SeniorActionChip(
                            icon = Icons.Default.Chat,
                            title = "Kirim Pesan",
                            desc = "Teks Penenang",
                            color = GuardPrimaryCyan,
                            onClick = {
                                viewModel.executeSeniorFix("SEND_MESSAGE", "Halo Ibu/Bapak, jangan khawatir ya. Anak sedang bantu cek HP-nya.")
                            },
                            modifier = Modifier.weight(1f).testTag("senior_fix_message_btn")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Remote Control & Assistance Action Deck
            Card(
                modifier = Modifier.fillMaxWidth().testTag("remote_action_deck_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GuardCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Status & Riwayat Bantuan Jarak Jauh",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lastActionText ?: "",
                        color = GuardPrimaryCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (screenShareMode == "PRESENTATION") {
                        // Presentation Slide Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.changePresentationSlide(-1) },
                                modifier = Modifier.weight(1f).testTag("prev_slide_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E314B)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Slide Sebelumnya", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { viewModel.changePresentationSlide(1) },
                                modifier = Modifier.weight(1f).testTag("next_slide_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = GuardPrimaryCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Slide Selanjutnya", fontSize = 11.sp, color = GuardNavyDark, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuardNavyDark, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Voice Assist Walkie-Talkie Button
                    Button(
                        onClick = { viewModel.toggleVoiceAssist() },
                        modifier = Modifier.fillMaxWidth().testTag("toggle_voice_assist_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVoiceAssistActive) GuardSafeGreen else Color(0xFF1D2E45)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isVoiceAssistActive) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = null,
                            tint = if (isVoiceAssistActive) GuardNavyDark else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isVoiceAssistActive) "Komunikasi Suara Bantuan Aktif (Walkie-Talkie)" else "Bicara Langsung (Pandu Lewat Suara)",
                            color = if (isVoiceAssistActive) GuardNavyDark else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AssistActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = Color(0xFF142438),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223A5B))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = GuardPrimaryCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun PresentationScreenContent(
    currentSlide: Int,
    isLandscape: Boolean
) {
    val slideData = when (currentSlide) {
        1 -> "RENCANA KEGIATAN KELUARGA 2026" to "1. Pertemuan Rutin Bulanan\n2. Pemeriksaan Kesehatan Orang Tua\n3. Pembagian Tanggung Jawab Keamanan Rumah\n4. Verifikasi Koneksi iDerMata"
        2 -> "LAPORAN RINGKASAN ANGGARAN & BIAYA" to "• Anggaran Pendidikan & Sekolah: 40%\n• Dana Darurat & Asuransi: 25%\n• Operasional & Kebutuhan Rumah: 35%\nStatus: Terkelola Baik & Terenkripsi"
        3 -> "JADWAL LIBURAN KELUARGA" to "Destinasi: Dataran Tinggi Dieng\nTanggal: 12 - 16 Oktober\nTransportasi: Mobil Keluarga\nTitik Kumpul: Rumah Utama Menteng"
        else -> "ARSITEKTUR & SISTEM KEAMANAN" to "• Perlindungan Bilateral dengan Izin QR/PIN\n• Geofence Notifikasi Masuk/Keluar\n• Deteksi Dini Kritis & Bantuan Layar Jarak Jauh"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F2137))
            .padding(14.dp)
    ) {
        // Presentation Slide Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SLIDE $currentSlide / 4",
                color = GuardPrimaryCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = Color(0x3300D1C1),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "PRESENTASI RESMI",
                    color = GuardPrimaryCyan,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = slideData.first,
            color = Color.White,
            fontSize = if (isLandscape) 13.sp else 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = Color(0xFF142C47),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = slideData.second,
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun AndroidHomeScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF183B5E), Color(0xFF0D1E32))
                )
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("10:45", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text("5G  100%", color = Color.White, fontSize = 9.sp)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Rabu, 26 September", color = Color(0xFFCAD5E2), fontSize = 11.sp)
        Text("10:45", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Light)

        Spacer(modifier = Modifier.weight(1f))

        // App Icons Grid
        val apps = listOf(
            "Telepon" to Color(0xFF10B981),
            "Pesan" to Color(0xFF388BFD),
            "Kamera" to Color(0xFFF59E0B),
            "Pengaturan" to Color(0xFF8B949E)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            apps.forEach { (name, color) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = name, color = Color.White, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun AndroidSettingsScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111D2D))
            .padding(12.dp)
    ) {
        Text(text = "Pengaturan", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        val settingItems = listOf(
            "Jaringan & Wi-Fi" to "Terhubung ke Rumah (Wi-Fi 6)",
            "Perangkat Terhubung" to "Bluetooth, Android Auto",
            "Tampilan & Ukuran Font" to "Teks Besar, Mode Gelap",
            "Baterai & Penghematan" to "85% - Siaga Optimal",
            "Penyimpanan & Memori" to "Tersedia 42 GB dari 128 GB"
        )

        settingItems.forEach { (title, sub) ->
            Surface(
                color = Color(0xFF17283E),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = sub, color = Color(0xFF8B949E), fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun SeniorActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = Color(0xFF101B2B),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = Color(0xFF94A3B8),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun SeniorAssistScreenContent(
    bannerMessage: String?,
    volume: Int,
    wifiActive: Boolean,
    fontSize: String,
    isRinging: Boolean,
    onHelpRequested: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1A28))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status bar ramah lansia
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("10:45", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (wifiActive) Icons.Default.Wifi else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (wifiActive) GuardSafeGreen else Color(0xFF8B949E),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Baterai 92%",
                    color = GuardSafeGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ringing Alert Banner (jika sedang dicari)
        if (isRinging) {
            Surface(
                color = GuardEmergencyRed.copy(alpha = 0.25f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardEmergencyRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = GuardEmergencyRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HP BERDERING KENCANG (PENCARIAN HP)",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Banner Pesan dari Keluarga / Anak
        if (!bannerMessage.isNullOrBlank()) {
            Surface(
                color = GuardPrimaryCyan.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardPrimaryCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = GuardPrimaryCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = bannerMessage,
                        color = Color.White,
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Clock & Date besar ramah mata
        Text(
            text = "10:45",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            text = "Rabu, 26 September • Tampilan Sederhana",
            color = Color(0xFFCAD5E2),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Indikator Status Suara, Wi-Fi, dan Ukuran Huruf
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                color = Color(0xFF14243A),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GuardPrimaryCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Suara: $volume%", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                color = Color(0xFF14243A),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (wifiActive) Icons.Default.Wifi else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (wifiActive) GuardSafeGreen else GuardWarningAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (wifiActive) "Wi-Fi: Aktif" else "Wi-Fi: Nonaktif", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                color = Color(0xFF14243A),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FormatSize, contentDescription = null, tint = GuardSecondaryBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(fontSize, color = Color.White, fontSize = 9.sp, maxLines = 1)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tombol Kontak Cepat Utama (Ukuran Besar Ramah Orang Tua)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Kontak Anak Tercinta
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onHelpRequested() },
                color = Color(0xFF13322B),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardSafeGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = GuardSafeGreen, modifier = Modifier.size(32.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = GuardNavyDark, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Hubungi Anak", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Budi (Aktif)", color = GuardSafeGreen, fontSize = 9.sp)
                    }
                }
            }

            // Pesan WhatsApp / SMS Keluarga
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onHelpRequested() },
                color = Color(0xFF162D4A),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardPrimaryCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = GuardPrimaryCyan, modifier = Modifier.size(32.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = GuardNavyDark, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Pesan Cucu", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Grup Keluarga", color = Color(0xFFCAD5E2), fontSize = 9.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tombol Darurat Minta Bantuan Langsung Lansia
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onHelpRequested() },
            color = GuardEmergencyRed.copy(alpha = 0.2f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GuardEmergencyRed)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = null,
                    tint = GuardEmergencyRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TEKAN JIKA BINGUNG / BUTUH BANTUAN",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RemoteModeGuideCard(mode: String) {
    val (guideTitle, guideSubtitle, guidePoints, guideColor) = when (mode) {
        "SENIOR" -> Quadruple(
            "PANDUAN MODE RAMAH LANSIA (PEDULI ORANG TUA)",
            "Dirancang khusus untuk membantu anggota keluarga lansia yang awam teknologi.",
            listOf(
                "Tampilan Khusus Lansia: Layar di atas menampilkan jam besar, baterai, status suara, dan tombol panggilan instan ke anak.",
                "Laser Pointer Interaktif: Ketuk layar HP di atas untuk mengirimkan lingkaran merah berkedip ke layar orang tua, menunjukkan tombol mana yang harus mereka tekan.",
                "Perbaikan Sekali Klik: Tekan tombol di bawah untuk menaikkan volume dering 100%, menghubungkan Wi-Fi rumah, membunyikan dering pencari HP, atau kirim pesan teks penenang.",
                "Walkie-Talkie Bantuan: Aktifkan tombol 'Bicara Langsung' untuk mendampingi orang tua lewat suara secara real-time."
            ),
            GuardSafeGreen
        )
        "SETTINGS" -> Quadruple(
            "PANDUAN MODE PENGATURAN HP",
            "Membantu menyelesaikan masalah konfigurasi sistem ponsel keluarga dari jarak jauh.",
            listOf(
                "Diagnosa Konfigurasi: Pantau status koneksi Wi-Fi, bluetooth, kecerahan layar, dan kapasitas memori secara visual.",
                "Arahkan ke Menu yang Tepat: Ketuk bagian pengaturan pada viewport layar untuk menunjukkan kepada keluarga menu apa yang perlu dibuka.",
                "Optimalkan Sistem: Gunakan tombol 'Lancar HP' di bawah jika ponsel lambat atau ada aplikasi latar belakang yang macet."
            ),
            GuardPrimaryCyan
        )
        "PRESENTATION" -> Quadruple(
            "PANDUAN MODE PRESENTASI KELUARGA",
            "Berbagi layar dokumen, rencana keluarga, dan materi visual secara sinkron.",
            listOf(
                "Sinkronisasi Slide: Gunakan tombol 'Slide Sebelumnya' dan 'Slide Selanjutnya' untuk membalik slide secara bersamaan di kedua HP.",
                "Format Lanskap: Tekan tombol rotasi layar di pojok kanan atas viewport untuk memperluas tampilan slide horizontal (16:9).",
                "Terenkripsi E2EE: Seluruh materi presentasi dienkripsi dengan standar AES-256-GCM sehingga kerahasiaan keluarga terlindungi."
            ),
            GuardSecondaryBlue
        )
        else -> Quadruple(
            "PANDUAN MODE LAYAR UTAMA (HOME SCREEN)",
            "Melihat tampilan beranda ponsel keluarga secara langsung dan real-time.",
            listOf(
                "Pemantauan Real-time: Amati widget jam, status sinyal, dan pintasan aplikasi yang ada di layar keluarga.",
                "Tuntunan Interaktif: Sentuh ikon aplikasi di viewport untuk memandu orang tua membuka aplikasi yang dibutuhkan (misal: Telepon atau WhatsApp).",
                "Keamanan Bilateral: Sesi berbagi layar ini bersifat resmi dengan indikator watermark dan persetujuan di layar."
            ),
            GuardPrimaryCyan
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .testTag("remote_mode_guide_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101D30)),
        border = androidx.compose.foundation.BorderStroke(1.dp, guideColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = guideColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = guideColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = guideTitle,
                        color = guideColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = guideSubtitle,
                        color = Color(0xFFCAD5E2),
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            guidePoints.forEach { point ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        color = guideColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text(
                        text = point,
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
