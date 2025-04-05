package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.database.dto.WeatherEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface WeatherDao {
    @Query("""
        select
            *,
            abs(dateTime - :now) as fromNow
        from WeatherEntity
        where
            city_name = :cityName and
            city_country = :cityCountry
        order by fromNow asc
        limit 1
    """)
    fun getPresentWeatherFlow(cityName: String, cityCountry: City.Country, now: ZonedDateTime): Flow<WeatherEntity?>
    
    @Query("""
        select *
        from WeatherEntity
        where
            city_name = :cityName and
            city_country = :cityCountry and
            (dateTime between :start and :end)
        order by dateTime asc
    """)
    fun getWeatherRange(
        cityName: String,
        cityCountry: City.Country,
        start: ZonedDateTime,
        end: ZonedDateTime,
    ): Flow<List<WeatherEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putWeather(weathers: List<WeatherEntity>): List<Long>
}
