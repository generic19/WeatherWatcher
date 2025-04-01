package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model

import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import java.time.ZonedDateTime

data class DatedLocated<out T>(
    val dateTime: ZonedDateTime,
    val city: City,
    val value: T,
) {
    fun toDated() = Dated(
        dateTime = dateTime,
        value = value,
    )
}
