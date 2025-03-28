package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto

import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("cod") val statusCode: Int,
    @SerializedName("message") val errorMessage: String?,
    @SerializedName("list") val dataPoints: ArrayList<WeatherDataPoint>,
    @SerializedName("city") val city: City
)

data class City(
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long,
)