package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model

import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import java.time.ZonedDateTime

data class Dated<out T>(
    val dateTime: ZonedDateTime,
    val value: T,
) {
    fun toDatedLocated(city: City) = DatedLocated(
        dateTime = dateTime,
        city = city,
        value = value,
    )
}
