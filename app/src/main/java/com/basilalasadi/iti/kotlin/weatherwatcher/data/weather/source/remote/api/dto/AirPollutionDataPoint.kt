package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto

import com.google.gson.annotations.SerializedName

data class AirPollutionDataPoint(
    @SerializedName("main") var mainAirPollution: MainAirPollution,
    @SerializedName("components") var components: PollutionComponents,
    @SerializedName("dt") var utcTimestamp: Int,
)

data class MainAirPollution(
    @SerializedName("aqi") var airQualityIndex: Int,
)

data class PollutionComponents(
    @SerializedName("co") var carbonMonoxide: Double,
    @SerializedName("no") var nitrogenMonoxide: Double,
    @SerializedName("no2") var nitrogenDioxide: Double,
    @SerializedName("o3") var ozone: Double,
    @SerializedName("so2") var sulfurDioxide: Double,
    @SerializedName("pm2_5") var fineParticleMatter: Double,
    @SerializedName("pm10") var coarseParticleMatter: Double,
    @SerializedName("nh3") var ammonia: Double,
)