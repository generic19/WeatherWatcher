package com.basilalasadi.iti.kotlin.weatherwatcher.data.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.AlertDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.CityDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.AlertEntity
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.CityEntity
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.database.WeatherDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.database.dto.WeatherEntity

@Database(version = 1, entities = [WeatherEntity::class, CityEntity::class, AlertEntity::class])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao
    abstract fun getCityDao(): CityDao
    abstract fun getAlertDao(): AlertDao
    
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
