package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FamilyGuardDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.PairedDevice
import com.example.data.model.SafeZone
import com.example.data.repository.FamilyRepository
import com.example.security.CryptoEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SystemPerformance(
    val cpuUsage: Float = 1.2f,
    val memoryUsageMb: Int = 36,
    val batteryDrainPerHour: Float = 0.6f,
    val latencyMs: Int = 42,
    val encryptionStatus: String = "AES-256-GCM Aktif",
    val uptimeHours: Int = 184,
    val packetCount: Int = 1420
)

data class BannerAlert(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val severity: String = "INFO" // INFO, WARNING, CRITICAL
)

class FamilyGuardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FamilyRepository

    val devices: StateFlow<List<PairedDevice>>
    val logs: StateFlow<List<ActivityLog>>
    val safeZones: StateFlow<List<SafeZone>>

    private val _selectedDeviceId = MutableStateFlow<String?>("DEV-88219")
    val selectedDeviceId = _selectedDeviceId.asStateFlow()

    private val _appRole = MutableStateFlow("GUARDIAN") // "GUARDIAN" (Pemantau) or "TARGET" (Sasaran Izin)
    val appRole = _appRole.asStateFlow()

    private val _systemPerf = MutableStateFlow(SystemPerformance())
    val systemPerf = _systemPerf.asStateFlow()

    private val _bannerAlert = MutableStateFlow<BannerAlert?>(null)
    val bannerAlert = _bannerAlert.asStateFlow()

    private val _isEmergencyDialogOpen = MutableStateFlow(false)
    val isEmergencyDialogOpen = _isEmergencyDialogOpen.asStateFlow()

    private val _isE2eeSheetOpen = MutableStateFlow(false)
    val isE2eeSheetOpen = _isE2eeSheetOpen.asStateFlow()

    private val _logFilter = MutableStateFlow("ALL")
    val logFilter = _logFilter.asStateFlow()

    private val _isWebExporting = MutableStateFlow(false)
    val isWebExporting = _isWebExporting.asStateFlow()

    private val _webDashboardUrl = MutableStateFlow("https://dashboard.familyguard.id/live/view?token=fg_${System.currentTimeMillis() % 1000000}&auth=e2ee_verified")
    val webDashboardUrl = _webDashboardUrl.asStateFlow()

    // Generated QR / PIN for target pairing
    private val _myTargetPin = MutableStateFlow("842915")
    val myTargetPin = _myTargetPin.asStateFlow()

    // Remote Screen Sharing & Interactive Assist States
    private val _isScreenShareActive = MutableStateFlow(false)
    val isScreenShareActive = _isScreenShareActive.asStateFlow()

    private val _screenShareMode = MutableStateFlow("SENIOR") // "SENIOR", "PRESENTATION", "HOME", "SETTINGS"
    val screenShareMode = _screenShareMode.asStateFlow()

    private val _presentationSlide = MutableStateFlow(1)
    val presentationSlide = _presentationSlide.asStateFlow()

    private val _remotePointerX = MutableStateFlow<Float?>(null)
    val remotePointerX = _remotePointerX.asStateFlow()

    private val _remotePointerY = MutableStateFlow<Float?>(null)
    val remotePointerY = _remotePointerY.asStateFlow()

    private val _lastRemoteActionText = MutableStateFlow<String?>("Mode Ramah Lansia Aktif: Siap membantu orang tua dari jarak jauh.")
    val lastRemoteActionText = _lastRemoteActionText.asStateFlow()

    private val _isVoiceAssistActive = MutableStateFlow(false)
    val isVoiceAssistActive = _isVoiceAssistActive.asStateFlow()

    private val _isRemoteLandscape = MutableStateFlow(false)
    val isRemoteLandscape = _isRemoteLandscape.asStateFlow()

    // Elderly Senior Care Specific States
    private val _seniorBannerMessage = MutableStateFlow<String?>("Ibu/Bapak, jangan khawatir. Layar ini sedang dipandu oleh keluarga.")
    val seniorBannerMessage = _seniorBannerMessage.asStateFlow()

    private val _seniorVolume = MutableStateFlow(100)
    val seniorVolume = _seniorVolume.asStateFlow()

    private val _seniorWifiActive = MutableStateFlow(true)
    val seniorWifiActive = _seniorWifiActive.asStateFlow()

    private val _seniorFontSize = MutableStateFlow("Sangat Besar (140%)")
    val seniorFontSize = _seniorFontSize.asStateFlow()

    private val _seniorPhoneFinderRinging = MutableStateFlow(false)
    val seniorPhoneFinderRinging = _seniorPhoneFinderRinging.asStateFlow()

    private val _isElderlySimpleLauncherMode = MutableStateFlow(false)
    val isElderlySimpleLauncherMode = _isElderlySimpleLauncherMode.asStateFlow()

    init {
        val db = FamilyGuardDatabase.getDatabase(application)
        repository = FamilyRepository(db.deviceDao(), db.activityLogDao(), db.safeZoneDao())

        devices = repository.allDevices.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        logs = repository.allLogs.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        safeZones = repository.allSafeZones.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }

        // Periodic light heartbeat simulation (simulating live telemetry if monitoring is active)
        startBackgroundTelemetryTicker()
    }

    private fun startBackgroundTelemetryTicker() {
        viewModelScope.launch {
            while (true) {
                delay(12000) // gentle interval
                val currentList = devices.value
                val currentId = _selectedDeviceId.value
                val dev = currentList.find { it.id == currentId }
                if (dev != null && dev.isMonitoringActive) {
                    // Slight variation to simulate real telemetry while in motion
                    val deltaLat = (Random.nextDouble() - 0.5) * 0.0003
                    val deltaLon = (Random.nextDouble() - 0.5) * 0.0003
                    val updatedDev = dev.copy(
                        latitude = dev.latitude + deltaLat,
                        longitude = dev.longitude + deltaLon,
                        speedKmh = if (dev.speedKmh > 0) ((dev.speedKmh + (Random.nextFloat() * 4 - 2)).coerceIn(5f, 45f)) else 0f,
                        lastSeenTimestamp = System.currentTimeMillis()
                    )
                    repository.updateDevice(updatedDev)

                    // Check Geofence proximity
                    checkGeofenceTrigger(updatedDev)
                }
            }
        }
    }

    private suspend fun checkGeofenceTrigger(device: PairedDevice) {
        val zones = safeZones.value
        for (zone in zones) {
            if (zone.isActive) {
                val dist = repository.calculateDistanceMeters(
                    device.latitude, device.longitude,
                    zone.latitude, zone.longitude
                )
                if (dist <= zone.radiusMeters) {
                    if (!device.safeZoneStatus.contains(zone.name)) {
                        val newStatus = "Di dalam Zona Aman (${zone.name})"
                        repository.updateDevice(device.copy(safeZoneStatus = newStatus))
                        triggerAlert(
                            title = "Notifikasi Zona Aman",
                            message = "${device.name} telah memasuki area aman: ${zone.name} (${dist.toInt()}m).",
                            severity = "INFO"
                        )
                        repository.logEvent(
                            deviceId = device.id,
                            deviceName = device.name,
                            eventType = "GEOFENCE",
                            title = "Masuk ${zone.name}",
                            description = "Perangkat terdeteksi berada di dalam perimeter aman ${zone.name}.",
                            severity = "INFO",
                            plainPayload = """{"zone":"${zone.name}","dist":$dist}"""
                        )
                    }
                }
            }
        }
    }

    fun selectDevice(id: String) {
        _selectedDeviceId.value = id
    }

    fun setAppRole(role: String) {
        _appRole.value = role
    }

    fun setLogFilter(filter: String) {
        _logFilter.value = filter
    }

    // Toggle "Tombol tidak pantau untuk menghemat daya baterai"
    fun toggleMonitoringState(device: PairedDevice) {
        viewModelScope.launch {
            val newState = !device.isMonitoringActive
            repository.setMonitoringState(device.id, newState, device.name)
            if (!newState) {
                triggerAlert(
                    title = "Mode Hemat Baterai Aktif",
                    message = "Pemantauan untuk ${device.name} dijeda sementara. Sensor GPS dan koneksi data perangkat sasaran berada dalam mode siaga ultra-hemat.",
                    severity = "WARNING"
                )
            } else {
                triggerAlert(
                    title = "Pemantauan Aktif",
                    message = "Pemantauan real-time untuk ${device.name} telah dilanjutkan.",
                    severity = "INFO"
                )
            }
        }
    }

    // Emergency SOS Trigger
    fun triggerEmergency(device: PairedDevice) {
        viewModelScope.launch {
            val newState = !device.emergencyStatus
            repository.setEmergencyStatus(device.id, newState, device.name)
            if (newState) {
                vibrateAlert()
                _isEmergencyDialogOpen.value = true
                triggerAlert(
                    title = "PERINGATAN DARURAT KRITIS!",
                    message = "Kondisi darurat aktif untuk ${device.name}. Fitur verifikasi audio dan kamera dibuka dengan notifikasi transparan.",
                    severity = "CRITICAL"
                )
            } else {
                _isEmergencyDialogOpen.value = false
            }
        }
    }

    fun openEmergencyDialog(open: Boolean) {
        _isEmergencyDialogOpen.value = open
    }

    fun openE2eeSheet(open: Boolean) {
        _isE2eeSheetOpen.value = open
    }

    fun dismissBannerAlert() {
        _bannerAlert.value = null
    }

    fun triggerAlert(title: String, message: String, severity: String) {
        _bannerAlert.value = BannerAlert(
            title = title,
            message = message,
            severity = severity
        )
    }

    fun pairNewDevice(name: String, pinCode: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (pinCode.length >= 6) {
                val newId = "DEV-" + Random.nextInt(10000, 99999)
                val newDevice = PairedDevice(
                    id = newId,
                    name = name.ifBlank { "Perangkat Keluarga ($pinCode)" },
                    role = "TARGET",
                    pairingCode = pinCode,
                    isConnected = true,
                    latitude = -6.2100 + (Random.nextDouble() - 0.5) * 0.02,
                    longitude = 106.8400 + (Random.nextDouble() - 0.5) * 0.02,
                    addressName = "Jakarta Raya",
                    speedKmh = 0f,
                    batteryLevel = 90,
                    networkType = "Wi-Fi Rumah",
                    isMonitoringActive = true,
                    emergencyStatus = false,
                    safeZoneStatus = "Dalam Zona Aman"
                )
                repository.addDevice(newDevice)
                _selectedDeviceId.value = newId
                triggerAlert(
                    title = "Perangkat Berhasil Terhubung",
                    message = "${newDevice.name} berhasil dipasangkan dengan izin resmi bilateral E2EE.",
                    severity = "INFO"
                )
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    fun deleteDevice(deviceId: String, deviceName: String) {
        viewModelScope.launch {
            repository.deleteDevice(deviceId, deviceName)
            if (_selectedDeviceId.value == deviceId) {
                val remaining = devices.value.filter { it.id != deviceId }
                _selectedDeviceId.value = remaining.firstOrNull()?.id
            }
            triggerAlert(
                title = "Perangkat Dihapus",
                message = "$deviceName berhasil dihapus dari daftar pantauan.",
                severity = "INFO"
            )
        }
    }

    fun addSafeZone(name: String, lat: Double, lon: Double, radius: Int) {
        viewModelScope.launch {
            val zone = SafeZone(
                name = name,
                latitude = lat,
                longitude = lon,
                radiusMeters = radius,
                isActive = true
            )
            repository.insertSafeZone(zone)
            triggerAlert(
                title = "Zona Aman Ditambahkan",
                message = "Geofence $name (Radius ${radius}m) aktif memantau.",
                severity = "INFO"
            )
        }
    }

    fun deleteSafeZone(id: Long) {
        viewModelScope.launch {
            repository.deleteSafeZone(id)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    fun refreshWebDashboardUrl() {
        viewModelScope.launch {
            _isWebExporting.value = true
            delay(1200)
            val token = "fg_sec_" + Random.nextInt(100000, 999999)
            _webDashboardUrl.value = "https://dashboard.familyguard.id/live/view?token=$token&auth=e2ee_verified"
            _isWebExporting.value = false
            triggerAlert(
                title = "Tautan Dasbor Web Diperbarui",
                message = "Sesi enkripsi dasbor admin web telah diperbarui dengan token akses baru.",
                severity = "INFO"
            )
        }
    }

    fun generateNewTargetPin() {
        val newPin = "%06d".format(Random.nextInt(100000, 999999))
        _myTargetPin.value = newPin
    }

    fun startScreenSharing(deviceName: String = "Perangkat Keluarga") {
        viewModelScope.launch {
            _isScreenShareActive.value = true
            _lastRemoteActionText.value = "Sesi aktif. Layar perangkat sasaran ditampilkan secara real-time via E2EE."
            val devId = _selectedDeviceId.value ?: "DEV-88219"
            repository.logEvent(
                deviceId = devId,
                deviceName = deviceName,
                eventType = "SYSTEM",
                title = "Sesi Berbagi Layar & Bantuan Dimulai",
                description = "Pemilik perangkat mengizinkan sesi berbagi layar real-time untuk presentasi/bantuan jarak jauh.",
                severity = "INFO",
                plainPayload = """{"event":"SCREEN_SHARE_START","mode":"${_screenShareMode.value}"}"""
            )
            triggerAlert(
                title = "Berbagi Layar Aktif",
                message = "Sesi streaming layar dan bantuan jarak jauh aktif dengan enkripsi AES-256.",
                severity = "INFO"
            )
        }
    }

    fun stopScreenSharing(deviceName: String = "Perangkat Keluarga") {
        viewModelScope.launch {
            _isScreenShareActive.value = false
            _remotePointerX.value = null
            _remotePointerY.value = null
            _lastRemoteActionText.value = "Sesi berbagi layar dihentikan."
            val devId = _selectedDeviceId.value ?: "DEV-88219"
            repository.logEvent(
                deviceId = devId,
                deviceName = deviceName,
                eventType = "SYSTEM",
                title = "Sesi Berbagi Layar Diakhiri",
                description = "Sesi bantuan jarak jauh telah selesai dan koneksi layar ditutup.",
                severity = "INFO",
                plainPayload = """{"event":"SCREEN_SHARE_STOP"}"""
            )
            triggerAlert(
                title = "Sesi Selesai",
                message = "Berbagi layar telah dinonaktifkan.",
                severity = "INFO"
            )
        }
    }

    fun sendRemoteTouch(normalizedX: Float, normalizedY: Float) {
        viewModelScope.launch {
            _remotePointerX.value = normalizedX
            _remotePointerY.value = normalizedY
            _lastRemoteActionText.value = "Petunjuk visual dikirimkan ke layar: Koordinat (${(normalizedX * 100).toInt()}%, ${(normalizedY * 100).toInt()}%)"
            // Auto fade pointer after 4 seconds
            delay(4000)
            if (_remotePointerX.value == normalizedX && _remotePointerY.value == normalizedY) {
                _remotePointerX.value = null
                _remotePointerY.value = null
            }
        }
    }

    fun sendRemoteQuickAction(actionName: String) {
        viewModelScope.launch {
            _lastRemoteActionText.value = "Instruksi bantuan dikirim: $actionName"
            triggerAlert(
                title = "Panduan Jarak Jauh",
                message = "Instruksi '$actionName' terkirim ke layar keluarga.",
                severity = "INFO"
            )
        }
    }

    fun changePresentationSlide(delta: Int) {
        val next = (_presentationSlide.value + delta).coerceIn(1, 4)
        _presentationSlide.value = next
        _lastRemoteActionText.value = "Slide presentasi beralih ke: Halaman $next / 4"
    }

    fun setScreenShareMode(mode: String) {
        _screenShareMode.value = mode
        _lastRemoteActionText.value = when (mode) {
            "SENIOR" -> "Mode Ramah Lansia: Tampilan sederhana, font besar, dan tombol minta bantuan."
            "PRESENTATION" -> "Mode Tampilan: Presentasi Dokumen & Slide"
            "SETTINGS" -> "Mode Tampilan: Bantuan Pengaturan HP"
            else -> "Mode Tampilan: Layar Utama (Home Screen)"
        }
    }

    fun toggleElderlySimpleLauncher() {
        _isElderlySimpleLauncherMode.value = !_isElderlySimpleLauncherMode.value
    }

    fun executeSeniorFix(actionCode: String, customText: String? = null) {
        viewModelScope.launch {
            when (actionCode) {
                "MAX_VOLUME" -> {
                    _seniorVolume.value = 100
                    _seniorBannerMessage.value = "Volume dering disetel ke 100% Paling Keras."
                    _lastRemoteActionText.value = "Berhasil: Volume dering HP lansia dinaikkan ke level maksimal 100%."
                    triggerAlert(
                        title = "Volume Maksimal",
                        message = "Volume nada dering pada HP orang tua telah disetel ke 100%.",
                        severity = "INFO"
                    )
                    vibrateAlert()
                }
                "CONNECT_WIFI" -> {
                    _seniorWifiActive.value = true
                    _seniorBannerMessage.value = "Wi-Fi Rumah diaktifkan & terhubung kembali."
                    _lastRemoteActionText.value = "Berhasil: Modul Wi-Fi diaktifkan dan terhubung ke jaringan rumah."
                    triggerAlert(
                        title = "Wi-Fi Terhubung",
                        message = "Wi-Fi pada HP keluarga telah diaktifkan kembali.",
                        severity = "INFO"
                    )
                }
                "LARGE_FONT" -> {
                    _seniorFontSize.value = "Ekstra Besar (160%)"
                    _seniorBannerMessage.value = "Ukuran huruf layar telah diperbesar agar nyaman dibaca."
                    _lastRemoteActionText.value = "Berhasil: Ukuran teks diubah ke Ekstra Besar (160%)."
                    triggerAlert(
                        title = "Ukuran Huruf Diperbesar",
                        message = "Font pada layar ponsel diperbesar agar ramah lansia.",
                        severity = "INFO"
                    )
                }
                "RING_FINDER" -> {
                    _seniorPhoneFinderRinging.value = !_seniorPhoneFinderRinging.value
                    val ringing = _seniorPhoneFinderRinging.value
                    _seniorBannerMessage.value = if (ringing) "Ponsel sedang berdering kencang untuk dicari!" else null
                    _lastRemoteActionText.value = if (ringing) "Ponsel berdering kencang untuk membantu mencari lokasi HP di rumah." else "Dering pencarian dihentikan."
                    triggerAlert(
                        title = if (ringing) "Mencari HP" else "Dering Selesai",
                        message = if (ringing) "Nada dering pencarian berbunyi kencang di HP orang tua." else "Dering dihentikan.",
                        severity = if (ringing) "WARNING" else "INFO"
                    )
                    if (ringing) vibrateAlert()
                }
                "CLEAR_MEMORY" -> {
                    _seniorBannerMessage.value = "Aplikasi macet telah ditutup. Ponsel kembali lancar."
                    _lastRemoteActionText.value = "Berhasil: Memori RAM dibebaskan, aplikasi berat ditutup."
                    triggerAlert(
                        title = "Ponsel Dioptimalkan",
                        message = "Memori dibersihkan dari aplikasi yang berjalan di latar belakang.",
                        severity = "INFO"
                    )
                }
                "SEND_MESSAGE" -> {
                    val msg = customText ?: "Halo Ibu/Bapak, anak sedang membantu dari jarak jauh. Tidak perlu panik ya."
                    _seniorBannerMessage.value = msg
                    _lastRemoteActionText.value = "Pesan panduan ditampilkan di layar: \"$msg\""
                    triggerAlert(
                        title = "Pesan Terkirim ke Layar",
                        message = msg,
                        severity = "INFO"
                    )
                }
            }

            val devId = _selectedDeviceId.value ?: "DEV-88219"
            repository.logEvent(
                deviceId = devId,
                deviceName = "Samsung Galaxy S24 (Orang Tua)",
                eventType = "SYSTEM",
                title = "Bantuan Lansia: $actionCode",
                description = _lastRemoteActionText.value ?: "Instruksi bantuan dijalankan.",
                severity = "INFO",
                plainPayload = """{"seniorAction":"$actionCode"}"""
            )
        }
    }

    fun toggleVoiceAssist() {
        _isVoiceAssistActive.value = !_isVoiceAssistActive.value
        val state = if (_isVoiceAssistActive.value) "aktif (Walkie-Talkie Bantuan)" else "dinonaktifkan"
        _lastRemoteActionText.value = "Komunikasi suara bantuan: $state"
    }

    fun toggleRemoteOrientation() {
        _isRemoteLandscape.value = !_isRemoteLandscape.value
    }

    private fun vibrateAlert() {
        try {
            val context = getApplication<Application>()
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        } catch (_: Exception) {}
    }
}
