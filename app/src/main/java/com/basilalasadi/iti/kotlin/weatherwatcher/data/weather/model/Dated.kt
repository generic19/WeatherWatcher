package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model

import java.time.ZonedDateTime

data class Dated<out T>(
    val dateTime: ZonedDateTime,
    val value: T,
)