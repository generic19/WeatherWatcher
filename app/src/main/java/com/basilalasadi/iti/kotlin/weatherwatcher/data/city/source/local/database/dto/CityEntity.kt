package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto

import androidx.room.Embedded
import androidx.room.Entity
import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City

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

fun CityEntity.toModel() = City(
    coordinates = coordinates,
    name = this.localizedName,
    country = country,
)

fun City.toEntity() = CityEntity(
    name = name.get(AppLocale.English),
    country = country,
    localizedName = name,
    coordinates = coordinates,
)