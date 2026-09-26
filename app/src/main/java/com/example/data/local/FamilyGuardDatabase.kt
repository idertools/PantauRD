package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.PairedDevice
import com.example.data.model.SafeZone

@Database(
    entities = [PairedDevice::class, ActivityLog::class, SafeZone::class],
    version = 1,
    exportSchema = false
)
abstract class FamilyGuardDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun safeZoneDao(): SafeZoneDao

    companion object {
        @Volatile
        private var INSTANCE: FamilyGuardDatabase? = null

        fun getDatabase(context: Context): FamilyGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FamilyGuardDatabase::class.java,
                    "family_guard_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
