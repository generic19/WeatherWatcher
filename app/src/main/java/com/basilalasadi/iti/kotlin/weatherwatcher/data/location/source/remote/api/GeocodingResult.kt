package com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote.api

import com.google.gson.annotations.SerializedName

data class GeocodingResult(
    @SerializedName("name") val cityNameEnglish: String,
    @SerializedName("local_names") val cityName: LocalizedCityName?,
    @SerializedName("country") val countryCode: String,
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double,
)

data class LocalizedCityName(
    @SerializedName("ar") val arabic: String?,
    @SerializedName("en") val english: String?,
)
