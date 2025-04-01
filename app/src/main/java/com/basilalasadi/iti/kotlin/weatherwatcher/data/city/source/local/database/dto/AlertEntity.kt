package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import java.time.ZonedDateTime

@Entity
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val requestCode: Int?,
    @Embedded(prefix = "city_") val cityEntity: CityEntity,
    val alertTime: ZonedDateTime,
    val isActive: Boolean = false,
)

fun AlertEntity.toModel() = Alert(
    requestCode = requestCode,
    city = cityEntity.toModel(),
    alertTime = alertTime,
    isActive = isActive,
)

fun Alert.toEntity() = AlertEntity(
    requestCode = requestCode,
    cityEntity = city.toEntity(),
    alertTime = alertTime,
    isActive = isActive,
)
