package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.security.CryptoEngine
import com.example.security.EncryptedEnvelope
import com.example.ui.theme.GuardBorderDark
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen

@Composable
fun EncryptionInspectorSheet(
    onDismiss: () -> Unit
) {
    var sampleTelemetry by remember {
        mutableStateOf("""{"lat":-6.2088,"lon":106.8456,"battery":82,"speed":14.5}""")
    }
    var currentEnvelope by remember {
        mutableStateOf(CryptoEngine.encryptPayload(sampleTelemetry))
    }
    var decryptedResult by remember {
        mutableStateOf(CryptoEngine.decryptPayload(currentEnvelope))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .testTag("encryption_inspector_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GuardNavyDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, GuardPrimaryCyan)
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
                            shape = RoundedCornerShape(8.dp),
                            color = GuardPrimaryCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "E2EE",
                                    tint = GuardPrimaryCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "INSPEKTUR ENKRIPSI END-TO-END",
                                color = GuardPrimaryCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Protokol AES-256-GCM Terverifikasi",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_e2ee_dialog_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Fingerprint Badge
                Surface(
                    color = Color(0xFF132235),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GuardBorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = GuardPrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Fingerprint Kunci Sesi:",
                                color = Color(0xFF8B949E),
                                fontSize = 10.sp
                            )
                            Text(
                                text = currentEnvelope.keyFingerprint,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Valid",
                            tint = GuardSafeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Plaintext Telemetry
                Text(
                    text = "1. Payload Asli (Plaintext Telemetri):",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = sampleTelemetry,
                    onValueChange = {
                        sampleTelemetry = it
                        currentEnvelope = CryptoEngine.encryptPayload(it)
                        decryptedResult = CryptoEngine.decryptPayload(currentEnvelope)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("plaintext_telemetry_input"),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color.White
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F1A28),
                        unfocusedContainerColor = Color(0xFF0F1A28),
                        focusedIndicatorColor = GuardPrimaryCyan,
                        unfocusedIndicatorColor = GuardBorderDark
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Encrypted Ciphertext
                Text(
                    text = "2. Ciphertext Terenkripsi (Terkirim via Jaringan):",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0D1724))
                        .border(1.dp, GuardBorderDark, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = currentEnvelope.ciphertext,
                            color = GuardPrimaryCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "IV / Nonce: ${currentEnvelope.iv}",
                            color = Color(0xFF6E7681),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Decrypted Verification
                Text(
                    text = "3. Hasil Dekripsi di Sisi Penerima Berwenang:",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F2520))
                        .border(1.dp, GuardSafeGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = decryptedResult,
                        color = GuardSafeGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        currentEnvelope = CryptoEngine.encryptPayload(sampleTelemetry)
                        decryptedResult = CryptoEngine.decryptPayload(currentEnvelope)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("regenerate_crypto_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GuardPrimaryCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = GuardNavyDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Uji Ulang Enkripsi (Kocok IV)",
                        color = GuardNavyDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
