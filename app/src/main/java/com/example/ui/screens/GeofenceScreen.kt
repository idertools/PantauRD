package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SafeZone
import com.example.ui.theme.GuardBorderDark
import com.example.ui.theme.GuardCardDark
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen
import com.example.ui.theme.GuardWarningAmber
import com.example.ui.viewmodel.FamilyGuardViewModel

@Composable
fun GeofenceScreen(
    viewModel: FamilyGuardViewModel,
    modifier: Modifier = Modifier
) {
    val safeZones by viewModel.safeZones.collectAsStateWithLifecycle()
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedDeviceId.collectAsStateWithLifecycle()
    val currentDevice = devices.find { it.id == selectedId } ?: devices.firstOrNull()

    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GuardNavyDark)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = GuardPrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GEOFENCING & ZONA AMAN",
                            color = GuardPrimaryCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Area & Notifikasi Instan",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GuardPrimaryCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_geofence_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = GuardNavyDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Tambah", color = GuardNavyDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Info Card
            Surface(
                color = Color(0xFF132235),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = GuardSafeGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Notifikasi instan otomatis dikirimkan saat perangkat terdeteksi masuk atau keluar dari radius zona aman yang telah ditentukan.",
                        color = Color(0xFFCAD5E2),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safe Zones List
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(safeZones) { zone ->
                    SafeZoneCard(
                        zone = zone,
                        onDelete = { viewModel.deleteSafeZone(zone.id) },
                        onSimulateEntry = {
                            viewModel.triggerAlert(
                                title = "Simulasi Geofence Masuk",
                                message = "Perangkat '${currentDevice?.name}' terdeteksi memasuki perimeter ${zone.name} (Radius ${zone.radiusMeters}m).",
                                severity = "INFO"
                            )
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSafeZoneDialog(
            currentLat = currentDevice?.latitude ?: -6.2088,
            currentLon = currentDevice?.longitude ?: 106.8456,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, lat, lon, radius ->
                viewModel.addSafeZone(name, lat, lon, radius)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SafeZoneCard(
    zone: SafeZone,
    onDelete: () -> Unit,
    onSimulateEntry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("safe_zone_card_${zone.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = GuardSafeGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (zone.iconType) {
                                    "SCHOOL" -> Icons.Default.School
                                    "OFFICE" -> Icons.Default.Business
                                    else -> Icons.Default.Home
                                },
                                contentDescription = null,
                                tint = GuardSafeGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = zone.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Radius: ${zone.radiusMeters} Meter | Masuk & Keluar",
                            color = Color(0xFF8B949E),
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_zone_${zone.id}_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Zona",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lat: %.4f, Lon: %.4f".format(zone.latitude, zone.longitude),
                    color = Color(0xFF6E7681),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )

                Button(
                    onClick = onSimulateEntry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2F44)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("test_geofence_trigger_${zone.id}_btn")
                ) {
                    Text(text = "Uji Peringatan", fontSize = 11.sp, color = GuardPrimaryCyan)
                }
            }
        }
    }
}

@Composable
fun AddSafeZoneDialog(
    currentLat: Double,
    currentLon: Double,
    onDismiss: () -> Unit,
    onConfirm: (name: String, lat: Double, lon: Double, radius: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var radiusSlider by remember { mutableFloatStateOf(350f) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GuardNavyDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, GuardPrimaryCyan),
            modifier = Modifier.fillMaxWidth().testTag("add_safe_zone_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Tambah Zona Aman Baru",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Area (misal: Rumah Nenek / Bimbel)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("zone_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF111E30),
                        unfocusedContainerColor = Color(0xFF111E30),
                        focusedIndicatorColor = GuardPrimaryCyan,
                        focusedLabelColor = GuardPrimaryCyan
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Radius Pemantauan: ${radiusSlider.toInt()} meter",
                    color = Color(0xFF8B949E),
                    fontSize = 12.sp
                )

                Slider(
                    value = radiusSlider,
                    onValueChange = { radiusSlider = it },
                    valueRange = 100f..1200f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = GuardPrimaryCyan,
                        activeTrackColor = GuardPrimaryCyan,
                        inactiveTrackColor = Color(0xFF223A5B)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF223A5B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Batal", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name, currentLat, currentLon, radiusSlider.toInt())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GuardPrimaryCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_zone_btn")
                    ) {
                        Text("Simpan Zona", color = GuardNavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
