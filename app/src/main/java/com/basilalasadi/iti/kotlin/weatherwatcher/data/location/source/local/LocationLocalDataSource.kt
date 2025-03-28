package com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.local

import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.local.database.LocationDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.local.database.dto.CityEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocationLocalDataSource {
    val favoritesFlow: Flow<List<City>>

    suspend fun findCitiesByName(query: String): List<City>
    suspend fun findCloseCity(coordinates: City.Coordinates, maxDeviation: Double = 0.3): City?
    suspend fun putCities(cities: List<City>)
    suspend fun setFavorite(city: City, isFavorite: Boolean): Boolean
}

class LocationLocalDataSourceImpl(private val locationDao: LocationDao) : LocationLocalDataSource {
    override val favoritesFlow: Flow<List<City>> by lazy {
        locationDao.getFavoritesFlow().map { it.map { it.toModel() } }
    }

    override suspend fun findCitiesByName(query: String): List<City> =
        locationDao.findCitiesByName(name = query).map { it.toModel() }

    override suspend fun findCloseCity(coordinates: City.Coordinates, maxDeviation: Double): City? =
        locationDao.findCloseCity(coordinates.latitude, coordinates.longitude, maxDeviation)?.toModel()

    override suspend fun putCities(cities: List<City>) {
        val entities = cities.map { it.toEntity() }
        locationDao.putCities(entities)
    }

    override suspend fun setFavorite(city: City, isFavorite: Boolean): Boolean {
        val updated = locationDao.setFavorite(
            name = city.name.get(AppLocale.English),
            country = city.country,
            isFavorite = isFavorite,
        )
        return updated > 0
    }
}

private fun CityEntity.toModel() = City(
    coordinates = coordinates,
    name = this.localizedName,
    country = country,
)

private fun City.toEntity() = CityEntity(
    name = name.get(AppLocale.English),
    country = country,
    localizedName = name,
    coordinates = coordinates,
)
