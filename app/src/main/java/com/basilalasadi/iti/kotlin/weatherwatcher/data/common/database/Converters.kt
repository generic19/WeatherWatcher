package com.basilalasadi.iti.kotlin.weatherwatcher.data.common.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class Converters {
    @TypeConverter
    fun toZonedDateTime(timestamp: Long): ZonedDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("UTC"))
    
    @TypeConverter
    fun fromZonedDateTime(zonedDateTime: ZonedDateTime): Long =
        zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toEpochSecond()
    
    @TypeConverter
    fun fromLocalTime(localTime: LocalTime): Int = localTime.toSecondOfDay()
    
    @TypeConverter
    fun toLocalTime(secondOfDay: Int): LocalTime = LocalTime.ofSecondOfDay(secondOfDay.toLong())
}
