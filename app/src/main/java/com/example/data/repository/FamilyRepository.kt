package com.example.data.repository

import com.example.data.local.ActivityLogDao
import com.example.data.local.DeviceDao
import com.example.data.local.SafeZoneDao
import com.example.data.model.ActivityLog
import com.example.data.model.PairedDevice
import com.example.data.model.SafeZone
import com.example.security.CryptoEngine
import kotlinx.coroutines.flow.Flow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FamilyRepository(
    private val deviceDao: DeviceDao,
    private val activityLogDao: ActivityLogDao,
    private val safeZoneDao: SafeZoneDao
) {
    val allDevices: Flow<List<PairedDevice>> = deviceDao.getAllDevices()
    val allLogs: Flow<List<ActivityLog>> = activityLogDao.getAllLogs()
    val allSafeZones: Flow<List<SafeZone>> = safeZoneDao.getAllSafeZones()

    suspend fun initializeDefaultDataIfEmpty() {
        val existing = deviceDao.getDeviceByIdDirect("DEV-88219")
        if (existing == null) {
            val defaultDevice = PairedDevice(
                id = "DEV-88219",
                name = "Samsung Galaxy S24 (Adik Budi)",
                role = "TARGET",
                pairingCode = "842915",
                isConnected = true,
                latitude = -6.2088,
                longitude = 106.8456,
                addressName = "Jl. Menteng Raya No. 14, Jakarta",
                speedKmh = 14.5f,
                batteryLevel = 82,
                isCharging = false,
                networkType = "5G Telkomsel (Aman)",
                isMonitoringActive = true,
                emergencyStatus = false,
                encryptionKeyFingerprint = CryptoEngine.getKeyFingerprint(),
                safeZoneStatus = "Di dalam Zona Aman (Rumah)"
            )
            deviceDao.insertDevice(defaultDevice)

            // Seed safe zones
            val safeZones = listOf(
                SafeZone(
                    id = 1,
                    name = "Rumah Utama",
                    description = "Kediaman Keluarga - Menteng",
                    latitude = -6.2088,
                    longitude = 106.8456,
                    radiusMeters = 350,
                    isActive = true,
                    iconType = "HOME"
                ),
                SafeZone(
                    id = 2,
                    name = "Sekolah Harapan",
                    description = "SMP Budi Mulia",
                    latitude = -6.2150,
                    longitude = 106.8520,
                    radiusMeters = 400,
                    isActive = true,
                    iconType = "SCHOOL"
                ),
                SafeZone(
                    id = 3,
                    name = "Kantor Ayah",
                    description = "Sudirman Central Business District",
                    latitude = -6.2250,
                    longitude = 106.8080,
                    radiusMeters = 500,
                    isActive = true,
                    iconType = "OFFICE"
                )
            )
            for (zone in safeZones) {
                safeZoneDao.insertSafeZone(zone)
            }

            // Seed initial activity logs with AES encryption demonstration
            val rawPayload = """{"event":"HANDSHAKE","code":"842915","status":"VERIFIED_CONSENT"}"""
            val encryptedEnvelope = CryptoEngine.encryptPayload(rawPayload)

            val initialLogs = listOf(
                ActivityLog(
                    deviceId = "DEV-88219",
                    deviceName = "Samsung Galaxy S24 (Adik Budi)",
                    timestamp = System.currentTimeMillis() - 15 * 60 * 1000,
                    eventType = "SECURITY",
                    title = "Izin Resmi Diaktifkan via QR/PIN",
                    description = "Pemberian izin bilateral berhasil diverifikasi. Saluran E2EE AES-256-GCM aktif.",
                    severity = "INFO",
                    isEncrypted = true,
                    payloadPreview = encryptedEnvelope.ciphertext.take(32) + "..."
                ),
                ActivityLog(
                    deviceId = "DEV-88219",
                    deviceName = "Samsung Galaxy S24 (Adik Budi)",
                    timestamp = System.currentTimeMillis() - 8 * 60 * 1000,
                    eventType = "GEOFENCE",
                    title = "Tiba di Zona Aman (Rumah Utama)",
                    description = "Perangkat memasuki radius 350m dari titik Rumah Utama.",
                    severity = "INFO",
                    isEncrypted = true,
                    payloadPreview = CryptoEngine.encryptPayload("""{"zone":"Rumah Utama","event":"ENTER"}""").ciphertext.take(32) + "..."
                ),
                ActivityLog(
                    deviceId = "DEV-88219",
                    deviceName = "Samsung Galaxy S24 (Adik Budi)",
                    timestamp = System.currentTimeMillis() - 2 * 60 * 1000,
                    eventType = "SYSTEM",
                    title = "Pemeriksaan Diagnostik Sistem",
                    description = "Kondisi perangkat prima. Baterai: 82%, Penggunaan CPU latar belakang: 1.4% (Sangat Ringan).",
                    severity = "INFO",
                    isEncrypted = true,
                    payloadPreview = CryptoEngine.encryptPayload("""{"battery":82,"cpu":1.4,"opt":"OK"}""").ciphertext.take(32) + "..."
                )
            )
            for (log in initialLogs) {
                activityLogDao.insertLog(log)
            }
        }
    }

    suspend fun addDevice(device: PairedDevice) {
        deviceDao.insertDevice(device)
        logEvent(
            deviceId = device.id,
            deviceName = device.name,
            eventType = "SECURITY",
            title = "Perangkat Baru Ditambahkan",
            description = "Perangkat berhasil dihubungkan dengan otorisasi PIN ${device.pairingCode}.",
            severity = "INFO",
            plainPayload = """{"action":"ADD_DEVICE","id":"${device.id}"}"""
        )
    }

    suspend fun deleteDevice(deviceId: String, deviceName: String) {
        deviceDao.deleteDevice(deviceId)
        logEvent(
            deviceId = deviceId,
            deviceName = deviceName,
            eventType = "SECURITY",
            title = "Perangkat Dihapus",
            description = "Perangkat $deviceName telah dihapus dari daftar pantauan dan koneksi diputuskan.",
            severity = "WARNING",
            plainPayload = """{"action":"DELETE_DEVICE","id":"$deviceId"}"""
        )
    }

    suspend fun updateDevice(device: PairedDevice) {
        deviceDao.updateDevice(device)
    }

    suspend fun setMonitoringState(deviceId: String, isActive: Boolean, deviceName: String) {
        deviceDao.setMonitoringState(deviceId, isActive)
        val title = if (isActive) "Pemantauan Dilanjutkan" else "Pemantauan Dijeda (Hemat Baterai)"
        val desc = if (isActive)
            "Pemantauan telemetri real-time aktif kembali atas permintaan pemantau."
        else
            "Mode hemat daya diaktifkan dari pemantau. Telemetri perangkat sasaran memasuki mode tidur/siaga untuk menghemat konsumsi baterai."

        logEvent(
            deviceId = deviceId,
            deviceName = deviceName,
            eventType = "TRACKING_STATE",
            title = title,
            description = desc,
            severity = if (isActive) "INFO" else "WARNING",
            plainPayload = """{"isMonitoringActive":$isActive,"reason":"BATTERY_SAVER_TOGGLE"}"""
        )
    }

    suspend fun setEmergencyStatus(deviceId: String, isEmergency: Boolean, deviceName: String) {
        deviceDao.setEmergencyStatus(deviceId, isEmergency)
        val title = if (isEmergency) "DARURAT! Peringatan SOS Diaktifkan" else "Status Darurat Diakhiri"
        val desc = if (isEmergency)
            "Kondisi darurat terdeteksi/dipicu. Akses verifikasi darurat (mikrofon & kamera) dibuka dengan notifikasi transparan di layar perangkat."
        else
            "Pemeriksaan darurat selesai. Perangkat kembali ke mode siaga normal."

        logEvent(
            deviceId = deviceId,
            deviceName = deviceName,
            eventType = "EMERGENCY",
            title = title,
            description = desc,
            severity = if (isEmergency) "CRITICAL" else "INFO",
            plainPayload = """{"emergency":$isEmergency,"timestamp":${System.currentTimeMillis()}}"""
        )
    }

    suspend fun logEvent(
        deviceId: String,
        deviceName: String,
        eventType: String,
        title: String,
        description: String,
        severity: String,
        plainPayload: String = ""
    ) {
        val encryptedEnvelope = CryptoEngine.encryptPayload(
            if (plainPayload.isNotEmpty()) plainPayload else """{"title":"$title","time":${System.currentTimeMillis()}}"""
        )
        val log = ActivityLog(
            deviceId = deviceId,
            deviceName = deviceName,
            timestamp = System.currentTimeMillis(),
            eventType = eventType,
            title = title,
            description = description,
            severity = severity,
            isEncrypted = true,
            payloadPreview = encryptedEnvelope.ciphertext.take(36) + "..."
        )
        activityLogDao.insertLog(log)
    }

    suspend fun insertSafeZone(safeZone: SafeZone) {
        safeZoneDao.insertSafeZone(safeZone)
    }

    suspend fun deleteSafeZone(id: Long) {
        safeZoneDao.deleteSafeZone(id)
    }

    suspend fun clearLogs() {
        activityLogDao.clearLogs()
    }

    // Distance calculation in meters using Haversine formula
    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
