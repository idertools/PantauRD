package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paired_devices")
data class PairedDevice(
    @PrimaryKey val id: String,
    val name: String,
    val role: String, // "TARGET" (perangkat sasaran) or "GUARDIAN" (pemantau)
    val pairingCode: String,
    val isConnected: Boolean = true,
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val latitude: Double = -6.2088,
    val longitude: Double = 106.8456,
    val addressName: String = "Menteng, Jakarta Pusat",
    val speedKmh: Float = 0f,
    val batteryLevel: Int = 85,
    val isCharging: Boolean = false,
    val networkType: String = "4G LTE",
    val isMonitoringActive: Boolean = true, // Tombol tidak pantau untuk hemat baterai
    val emergencyStatus: Boolean = false, // Mode darurat
    val encryptionKeyFingerprint: String = "E2EE:F4:9A:8C:21",
    val safeZoneStatus: String = "Dalam Zona Aman (Rumah)",
    val cpuUsagePercent: Float = 1.4f,
    val memoryUsageMb: Int = 38
)
