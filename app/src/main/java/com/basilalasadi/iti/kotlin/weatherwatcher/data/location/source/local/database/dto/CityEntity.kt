package com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.local.database.dto

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.model.City

@Entity(
    primaryKeys = ["name", "country"],
)
data class CityEntity(
    val name: String,
    val country: City.Country,
    @Embedded(prefix = "localizedName_") val localizedName: LocalizedName,
    @Embedded val coordinates: City.Coordinates,
    val isFavorite: Boolean = false,
)
