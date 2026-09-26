package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PairedDevice
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM paired_devices ORDER BY lastSeenTimestamp DESC")
    fun getAllDevices(): Flow<List<PairedDevice>>

    @Query("SELECT * FROM paired_devices WHERE id = :id LIMIT 1")
    fun getDeviceById(id: String): Flow<PairedDevice?>

    @Query("SELECT * FROM paired_devices WHERE id = :id LIMIT 1")
    suspend fun getDeviceByIdDirect(id: String): PairedDevice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: PairedDevice)

    @Update
    suspend fun updateDevice(device: PairedDevice)

    @Query("UPDATE paired_devices SET isMonitoringActive = :isActive WHERE id = :id")
    suspend fun setMonitoringState(id: String, isActive: Boolean)

    @Query("UPDATE paired_devices SET emergencyStatus = :isEmergency WHERE id = :id")
    suspend fun setEmergencyStatus(id: String, isEmergency: Boolean)

    @Query("DELETE FROM paired_devices WHERE id = :id")
    suspend fun deleteDevice(id: String)
}
