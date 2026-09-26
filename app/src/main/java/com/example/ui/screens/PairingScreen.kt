package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PairedDevice
import com.example.ui.components.QrCodeRenderer
import com.example.ui.theme.GuardBorderDark
import com.example.ui.theme.GuardCardDark
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen
import com.example.ui.viewmodel.FamilyGuardViewModel

@Composable
fun PairingScreen(
    viewModel: FamilyGuardViewModel,
    modifier: Modifier = Modifier
) {
    val myPin by viewModel.myTargetPin.collectAsStateWithLifecycle()
    val appRole by viewModel.appRole.collectAsStateWithLifecycle()
    val devices by viewModel.devices.collectAsStateWithLifecycle()

    var inputPin by remember { mutableStateOf("") }
    var inputDeviceName by remember { mutableStateOf("") }
    var pairingErrorMessage by remember { mutableStateOf<String?>(null) }
    var deviceToDelete by remember { mutableStateOf<PairedDevice?>(null) }

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
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = GuardPrimaryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OTORISASI RESMI BERIZIN",
                        color = GuardPrimaryCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Pairing QR & Kode Izin",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Role Selector Tabs (Mode Target vs Mode Pemantau)
        val isTarget = appRole == "TARGET"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF101C2B))
                .padding(4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.setAppRole("TARGET") }
                    .testTag("tab_role_target"),
                color = if (isTarget) GuardPrimaryCyan else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Perangkat Saya (Sasaran)",
                    color = if (isTarget) GuardNavyDark else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.setAppRole("GUARDIAN") }
                    .testTag("tab_role_guardian"),
                color = if (!isTarget) GuardPrimaryCyan else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Mode Pemantau (Admin)",
                    color = if (!isTarget) GuardNavyDark else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PANDUAN MASING-MASING MODE (LANGKAH DEMI LANGKAH)
        PairingGuideCard(isTargetMode = isTarget)

        if (appRole == "TARGET") {
            // TARGET / OWNER CONSENT MODE: Generates PIN & QR Code
            Card(
                modifier = Modifier.fillMaxWidth().testTag("target_consent_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GuardCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bukti Otorisasi Izin Pemilik Perangkat",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tunjukkan QR atau berikan 6-Digit PIN ini kepada anggota keluarga / pemantau terpercaya untuk menghubungkan perangkat secara resmi.",
                        color = Color(0xFF8B949E),
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code Renderer
                    QrCodeRenderer(
                        content = "FAMILY_GUARD_PAIRING_PAYLOAD:$myPin:${System.currentTimeMillis()}",
                        size = 180.dp,
                        modifier = Modifier.border(2.dp, GuardPrimaryCyan, RoundedCornerShape(18.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 6-Digit PIN display
                    Surface(
                        color = Color(0xFF0C1624),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GuardPrimaryCyan)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "KODE PIN KHUSUS",
                                color = Color(0xFF8B949E),
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = myPin.chunked(3).joinToString(" - "),
                                color = GuardPrimaryCyan,
                                fontSize = 28.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    IconButton(
                        onClick = { viewModel.generateNewTargetPin() },
                        modifier = Modifier.testTag("regenerate_pin_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Ganti Kode",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ganti Kode Baru",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Permission Consent Badges
                    Surface(
                        color = Color(0xFF0F1B2B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            ConsentRow(label = "Izin Pelacakan GPS Akurat Real-time")
                            ConsentRow(label = "Izin Notifikasi Zona Aman (Geofencing)")
                            ConsentRow(label = "Izin Verifikasi Darurat Kamera & Audio (Transparan)")
                            ConsentRow(label = "Enkripsi End-to-End AES-256 Otomatis")
                        }
                    }
                }
            }
        } else {
            // GUARDIAN / MONITOR MODE: Enters PIN to connect
            Card(
                modifier = Modifier.fillMaxWidth().testTag("guardian_pairing_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GuardCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Hubungkan Perangkat Baru",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Masukkan 6-digit kode PIN yang ditampilkan pada layar HP keluarga/anak yang telah memberi izin.",
                        color = Color(0xFF8B949E),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = inputDeviceName,
                        onValueChange = { inputDeviceName = it },
                        label = { Text("Nama Perangkat (misal: HP Anak - Kevin)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_device_name"),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF101B2B),
                            unfocusedContainerColor = Color(0xFF101B2B),
                            focusedIndicatorColor = GuardPrimaryCyan,
                            focusedLabelColor = GuardPrimaryCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputPin,
                        onValueChange = { if (it.length <= 6) inputPin = it },
                        label = { Text("6-Digit Kode PIN Izin") },
                        placeholder = { Text("Contoh: 842915") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_pairing_pin"),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF101B2B),
                            unfocusedContainerColor = Color(0xFF101B2B),
                            focusedIndicatorColor = GuardPrimaryCyan,
                            focusedLabelColor = GuardPrimaryCyan
                        )
                    )

                    if (pairingErrorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pairingErrorMessage!!,
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (inputPin.length == 6) {
                                viewModel.pairNewDevice(inputDeviceName, inputPin) { success ->
                                    if (success) {
                                        inputPin = ""
                                        inputDeviceName = ""
                                        pairingErrorMessage = null
                                    } else {
                                        pairingErrorMessage = "Gagal memverifikasi kode pairing."
                                    }
                                }
                            } else {
                                pairingErrorMessage = "Harap masukkan 6-digit kode PIN lengkap."
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("submit_pair_device_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = GuardPrimaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = GuardNavyDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Verifikasi & Hubungkan Perangkat",
                            color = GuardNavyDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Current Connected Devices List
            Text(
                text = "Daftar Perangkat Terotorisasi (${devices.size})",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            devices.forEach { dev ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GuardCardDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GuardPrimaryCyan.copy(alpha = 0.2f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        tint = GuardPrimaryCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = dev.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PIN Izin: ${dev.pairingCode} • E2EE Aktif",
                                    color = Color(0xFF8B949E),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = GuardSafeGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "TERHUBUNG",
                                    color = GuardSafeGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { deviceToDelete = dev },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("delete_device_${dev.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Perangkat",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Dialog Konfirmasi Hapus Perangkat
            if (deviceToDelete != null) {
                val targetDev = deviceToDelete!!
                AlertDialog(
                    onDismissRequest = { deviceToDelete = null },
                    containerColor = GuardCardDark,
                    title = {
                        Text(
                            text = "Hapus Perangkat Target?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    },
                    text = {
                        Text(
                            text = "Apakah Anda yakin ingin memutuskan sambungan dan menghapus \"${targetDev.name}\" (ID: ${targetDev.id}) dari daftar pantauan? Seluruh izin akses pemantauan bilateral akan dihentikan.",
                            color = Color(0xFFCAD5E2),
                            fontSize = 13.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteDevice(targetDev.id, targetDev.name)
                                deviceToDelete = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GuardEmergencyRed)
                        ) {
                            Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { deviceToDelete = null }) {
                            Text("Batal", color = Color(0xFF94A3B8))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ConsentRow(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = GuardSafeGreen,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = Color(0xFFCAD5E2),
            fontSize = 11.sp
        )
    }
}

@Composable
fun PairingGuideCard(
    isTargetMode: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .testTag("pairing_guide_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132237)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isTargetMode) GuardSafeGreen.copy(alpha = 0.6f) else GuardPrimaryCyan.copy(alpha = 0.6f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (isTargetMode) GuardSafeGreen.copy(alpha = 0.2f) else GuardPrimaryCyan.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isTargetMode) GuardSafeGreen else GuardPrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isTargetMode) "PANDUAN MODE PERANGKAT SASARAN (LANSIA)" else "PANDUAN MODE PEMANTAU (ADMIN / KELUARGA)",
                        color = if (isTargetMode) GuardSafeGreen else GuardPrimaryCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (isTargetMode) "Petunjuk untuk HP orang tua yang akan dipantau" else "Petunjuk untuk HP anak / pendamping yang memantau",
                        color = Color(0xFF8B949E),
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isTargetMode) {
                GuideStepItem(
                    stepNumber = "1",
                    title = "Buka Mode Ini di HP Orang Tua / Lansia",
                    desc = "Pastikan tab 'Perangkat Saya (Sasaran)' ini aktif pada ponsel yang ingin dipantau atau didampingi."
                )
                GuideStepItem(
                    stepNumber = "2",
                    title = "Izinkan Hak Akses Sistem",
                    desc = "Beri persetujuan izin Lokasi GPS dan Notifikasi saat diminta agar perangkat dapat dipantau secara akurat."
                )
                GuideStepItem(
                    stepNumber = "3",
                    title = "Tunjukkan QR atau Bacakan 6-Digit PIN",
                    desc = "Tunjukkan Kode QR atau sebutkan 6-digit PIN unik di bawah kepada anak/pemantau untuk otorisasi resmi."
                )
                GuideStepItem(
                    stepNumber = "4",
                    title = "Perangkat Siap & Aman",
                    desc = "Setelah dihubungkan, kedua HP terhubung otomatis dengan enkripsi AES-256-GCM. Orang tua cukup menekan tombol 'Minta Bantuan' jika mengalami kendala."
                )
            } else {
                GuideStepItem(
                    stepNumber = "1",
                    title = "Buka Mode Ini di HP Pemantau / Anak",
                    desc = "Gunakan tab 'Mode Pemantau (Admin)' ini di ponsel Anda sebagai pengontrol dan pendamping keluarga."
                )
                GuideStepItem(
                    stepNumber = "2",
                    title = "Masukkan Nama & 6-Digit PIN",
                    desc = "Ketik nama ponsel keluarga (misal: 'HP Ibu') dan 6-digit PIN yang tertera di layar HP sasaran."
                )
                GuideStepItem(
                    stepNumber = "3",
                    title = "Klik 'Verifikasi & Hubungkan Perangkat'",
                    desc = "Sistem akan bertukar kunci enkripsi bilateral dan mendaftarkan perangkat sasaran secara resmi."
                )
                GuideStepItem(
                    stepNumber = "4",
                    title = "Mulai Bantuan di Tab 'Layar & Bantuan'",
                    desc = "Gunakan tab 'Radar Peta' untuk cek posisi/baterai, atau tab 'Layar & Bantuan' untuk memandu layar dan memperbaiki volume/Wi-Fi jarak jauh."
                )
            }
        }
    }
}

@Composable
fun GuideStepItem(
    stepNumber: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF1E324D),
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = desc,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}
