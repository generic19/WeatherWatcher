package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model

import java.time.ZonedDateTime

data class Alert(
    val requestCode: Int? = null,
    val city: City,
    val alertTime: ZonedDateTime,
    val isActive: Boolean = false,
)
