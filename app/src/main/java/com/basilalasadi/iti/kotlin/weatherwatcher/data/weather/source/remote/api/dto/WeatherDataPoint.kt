package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto

import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Dated
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime


data class WeatherDataPoint(
    @SerializedName("cod") val statusCode: Int,
    @SerializedName("message") val errorMessage: String?,
    @SerializedName("coord") val coordinates: Coordinates,
    @SerializedName("weather") val weatherConditions: ArrayList<WeatherCondition>,
    @SerializedName("main") val mainWeather: MainWeather,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("wind") val windStatus: WindStatus,
    @SerializedName("rain") val rainPrecipitation: Precipitation?,
    @SerializedName("snow") val snowPrecipitation: Precipitation?,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("dt") val utcTimestamp: Int,
    @SerializedName("sys") val system: System,
    @SerializedName("timezone") val timezone: Int?,
    @SerializedName("name") val cityName: String,
    @SerializedName("pop") val probabilityOfRain: Double?,
)

data class Clouds(
    @SerializedName("all") val cloudinessPercent: Int,
)

data class Coordinates(
    @SerializedName("lon") val longitude: Double,
    @SerializedName("lat") val latitude: Double,
)

data class WeatherCondition(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val iconId: String,
)

data class MainWeather(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("feels_like") val feelsLikeTemperature: Double,
    @SerializedName("temp_min") val minTemperature: Double,
    @SerializedName("temp_max") val maxTemperature: Double,
    @SerializedName("humidity") val humidity: Double,
    @SerializedName("sea_level") val seaLevelPressure: Double,
    @SerializedName("grnd_level") val groundLevelPressure: Double,
)

data class WindStatus(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val direction: Int,
    @SerializedName("gust") val gust: Double,
)

data class Precipitation(
    @SerializedName("1h") val oneHour: Double? = null,
    @SerializedName("3h") val threeHours: Double? = null,
) {
    val perHour: Double get() = threeHours?.div(3) ?: oneHour ?: 0.0
}

data class System(
    @SerializedName("sunrise") val sunriseUtcTimestamp: Long?,
    @SerializedName("sunset") val sunsetUtcTimestamp: Long?,
)

fun WeatherDataPoint.toDatedModel(
    timezone: Int,
    sunrise: Long,
    sunset: Long,
    airPollutionDataPoint: AirPollutionDataPoint?,
): Dated<Weather> = Dated(
    dateTime = ZonedDateTime
        .ofInstant(
            Instant.ofEpochSecond(utcTimestamp.toLong()),
            ZoneOffset.UTC
        )
        .withZoneSameInstant(ZoneOffset.ofTotalSeconds(timezone)),
    value = Weather(
        temperature = Weather.Temperature(
            current = mainWeather.temperature,
            feelsLike = mainWeather.feelsLikeTemperature,
            min = mainWeather.minTemperature,
            max = mainWeather.maxTemperature,
        ),
        condition = Weather.Condition.byId(weatherConditions[0].id),
        pressure = Weather.Pressure(
            seaLevel = mainWeather.seaLevelPressure,
            groundLevel = mainWeather.groundLevelPressure,
        ),
        precipitation = Weather.Precipitation(
            rain = rainPrecipitation?.perHour ?: 0.0,
            snow = snowPrecipitation?.perHour ?: 0.0,
            probability = probabilityOfRain,
        ),
        wind = Weather.Wind(
            speed = windStatus.speed,
            direction = windStatus.direction.toDouble(),
            gust = windStatus.gust,
        ),
        airPollution = airPollutionDataPoint?.run {
            Weather.AirPollution(
                airQualityIndex = Weather.AirPollution.AirQualityIndex.byIndex(mainAirPollution.airQualityIndex),
                carbonMonoxide = components.carbonMonoxide,
                nitrogenMonoxide = components.nitrogenMonoxide,
                nitrogenDioxide = components.nitrogenDioxide,
                ozone = components.ozone,
                sulfurDioxide = components.sulfurDioxide,
                fineParticleMatter = components.fineParticleMatter,
                coarseParticleMatter = components.coarseParticleMatter,
                ammonia = components.ammonia,
            )
        },
        cloudiness = clouds.cloudinessPercent / 100.0,
        humidity = mainWeather.humidity / 100.0,
        visibility = visibility.toDouble(),
        sunrise = Instant.ofEpochSecond(sunrise).atOffset(ZoneOffset.ofTotalSeconds(timezone)).toLocalTime(),
        sunset = Instant.ofEpochSecond(sunset).atOffset(ZoneOffset.ofTotalSeconds(timezone)).toLocalTime(),
    ),
)
