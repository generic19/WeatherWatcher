package com.basilalasadi.iti.kotlin.weatherwatcher.data.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.local.database.LocationDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.local.database.dto.CityEntity

@Database(version = 1, entities = [CityEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLocationDao(): LocationDao
    
    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = Room
                            .databaseBuilder(
                                context,
                                AppDatabase::class.java,
                                "AppDatabase"
                            )
                            .build()
                    }
                }
            }

            return instance!!
        }
    }
}
