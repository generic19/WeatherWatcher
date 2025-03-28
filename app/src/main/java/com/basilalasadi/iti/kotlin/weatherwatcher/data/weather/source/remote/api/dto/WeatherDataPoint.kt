package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto

import com.google.gson.annotations.SerializedName


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
