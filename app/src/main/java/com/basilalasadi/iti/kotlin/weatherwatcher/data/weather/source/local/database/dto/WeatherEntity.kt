package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.database.dto

import androidx.room.Embedded
import androidx.room.Entity
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.CityEntity
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.toEntity
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.toModel
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.DatedLocated
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather.AirPollution
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather.Condition
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather.Precipitation
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather.Pressure
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather.Temperature
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather.Wind
import java.time.LocalTime
import java.time.ZonedDateTime

@Entity(
    primaryKeys = ["timestamp", "city_name", "city_country"]
)
data class WeatherEntity(
    val dateTime: ZonedDateTime,
    @Embedded("city_") val cityEntity: CityEntity,
    @Embedded("temperature_") val temperature: Temperature,
    val condition: Condition,
    @Embedded("pressure_") val pressure: Pressure,
    @Embedded("precipitation_") val precipitation: Precipitation,
    @Embedded("wind_") val wind: Wind,
    @Embedded("airPollution_") val airPollution: AirPollution?,
    val cloudiness: Double,
    val humidity: Double,
    val visibility: Double,
    val sunrise: LocalTime?,
    val sunset: LocalTime?,
)

fun DatedLocated<Weather>.toEntity() = WeatherEntity(
    dateTime = dateTime,
    cityEntity = city.toEntity(),
    temperature = value.temperature,
    condition = value.condition,
    pressure = value.pressure,
    precipitation = value.precipitation,
    wind = value.wind,
    airPollution = value.airPollution,
    cloudiness = value.cloudiness,
    humidity = value.humidity,
    visibility = value.visibility,
    sunrise = value.sunrise,
    sunset = value.sunset,
)

fun WeatherEntity.toModel() = DatedLocated<Weather>(
    dateTime = dateTime,
    city = cityEntity.toModel(),
    value = Weather(
        temperature = temperature,
        condition = condition,
        pressure = pressure,
        precipitation = precipitation,
        wind = wind,
        airPollution = airPollution,
        cloudiness = cloudiness,
        humidity = humidity,
        visibility = visibility,
        sunrise = sunrise,
        sunset = sunset,
    ),
)