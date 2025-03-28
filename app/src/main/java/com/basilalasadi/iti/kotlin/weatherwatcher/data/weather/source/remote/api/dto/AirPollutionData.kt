package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto

import com.google.gson.annotations.SerializedName

data class AirPollutionData(
    @SerializedName("list") val dataPoints: ArrayList<AirPollutionDataPoint>
)
