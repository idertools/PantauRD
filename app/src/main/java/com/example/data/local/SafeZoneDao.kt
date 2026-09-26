package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SafeZone
import kotlinx.coroutines.flow.Flow

@Dao
interface SafeZoneDao {
    @Query("SELECT * FROM safe_zones ORDER BY id ASC")
    fun getAllSafeZones(): Flow<List<SafeZone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafeZone(safeZone: SafeZone)

    @Update
    suspend fun updateSafeZone(safeZone: SafeZone)

    @Query("DELETE FROM safe_zones WHERE id = :id")
    suspend fun deleteSafeZone(id: Long)
}
