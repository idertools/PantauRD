package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "safe_zones")
data class SafeZone(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int = 300,
    val isActive: Boolean = true,
    val alertOnEntry: Boolean = true,
    val alertOnExit: Boolean = true,
    val iconType: String = "HOME" // HOME, SCHOOL, OFFICE, AREA
)
