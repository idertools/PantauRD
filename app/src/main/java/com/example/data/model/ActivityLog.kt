package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val deviceName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // GEOFENCE, BATTERY, SECURITY, EMERGENCY, TRACKING_STATE, SYSTEM
    val title: String,
    val description: String,
    val severity: String, // INFO, WARNING, CRITICAL
    val isEncrypted: Boolean = true,
    val payloadPreview: String = ""
)
