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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.ActivityLog
import com.example.ui.theme.GuardBorderDark
import com.example.ui.theme.GuardCardDark
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen
import com.example.ui.theme.GuardWarningAmber
import com.example.ui.viewmodel.FamilyGuardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityLogScreen(
    viewModel: FamilyGuardViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val activeFilter by viewModel.logFilter.collectAsStateWithLifecycle()

    val filteredLogs = when (activeFilter) {
        "EMERGENCY" -> logs.filter { it.severity == "CRITICAL" || it.eventType == "EMERGENCY" }
        "GEOFENCE" -> logs.filter { it.eventType == "GEOFENCE" }
        "SYSTEM" -> logs.filter { it.eventType == "SYSTEM" || it.eventType == "TRACKING_STATE" }
        else -> logs
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuardNavyDark)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = GuardPrimaryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AUDIT TRAIL RESMI",
                        color = GuardPrimaryCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Laporan Aktivitas & Keamanan",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { viewModel.clearAllLogs() },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF1B293C), RoundedCornerShape(10.dp))
                    .testTag("clear_logs_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Bersihkan Log",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Chips
        val filterOptions = listOf(
            "ALL" to "Semua (${logs.size})",
            "EMERGENCY" to "Darurat SOS",
            "GEOFENCE" to "Zona Aman",
            "SYSTEM" to "Hemat Daya & Sistem"
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { (key, label) ->
                val isSelected = activeFilter == key
                Surface(
                    modifier = Modifier
                        .clickable { viewModel.setLogFilter(key) }
                        .testTag("filter_chip_$key"),
                    color = if (isSelected) GuardPrimaryCyan.copy(alpha = 0.2f) else GuardCardDark,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) GuardPrimaryCyan else GuardBorderDark
                    )
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) GuardPrimaryCyan else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Empty state
        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF334B68),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tidak ada log aktivitas dalam kategori ini.",
                        color = Color(0xFF8B949E),
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredLogs) { log ->
                    LogCard(log = log)
                }
            }
        }
    }
}

@Composable
fun LogCard(log: ActivityLog) {
    val sdf = SimpleDateFormat("HH:mm:ss • dd MMM", Locale.getDefault())
    val formattedTime = sdf.format(Date(log.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity_log_card_${log.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GuardCardDark),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when (log.severity) {
                "CRITICAL" -> GuardEmergencyRed.copy(alpha = 0.7f)
                "WARNING" -> GuardWarningAmber.copy(alpha = 0.5f)
                else -> GuardBorderDark
            }
        )
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
                        color = when (log.severity) {
                            "CRITICAL" -> GuardEmergencyRed.copy(alpha = 0.2f)
                            "WARNING" -> GuardWarningAmber.copy(alpha = 0.2f)
                            else -> GuardSafeGreen.copy(alpha = 0.2f)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (log.eventType) {
                                    "EMERGENCY" -> Icons.Default.Warning
                                    "GEOFENCE" -> Icons.Default.NotificationsActive
                                    "TRACKING_STATE" -> Icons.Default.PowerSettingsNew
                                    "BATTERY" -> Icons.Default.BatteryAlert
                                    else -> Icons.Default.Security
                                },
                                contentDescription = null,
                                tint = when (log.severity) {
                                    "CRITICAL" -> GuardEmergencyRed
                                    "WARNING" -> GuardWarningAmber
                                    else -> GuardSafeGreen
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = log.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${log.deviceName} • $formattedTime",
                            color = Color(0xFF8B949E),
                            fontSize = 10.sp
                        )
                    }
                }

                if (log.isEncrypted) {
                    Surface(
                        color = Color(0xFF0F1B2B),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E334D))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "E2EE",
                                tint = GuardPrimaryCyan,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "E2EE",
                                color = GuardPrimaryCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.description,
                color = Color(0xFFCAD5E2),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            if (log.payloadPreview.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFF0C1522),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cipher: ${log.payloadPreview}",
                        color = Color(0xFF6E7681),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
