package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local

import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.AlertDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.CityDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.toEntity
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CityLocalDataSource {
    val favoritesFlow: Flow<List<City>>
    val alertsFlow: Flow<List<Alert>>

    suspend fun findCitiesByName(query: String): List<City>
    suspend fun findClosestCity(coordinates: City.Coordinates, maxDeviation: Double = 0.3): City?
    suspend fun putCities(cities: List<City>)
    suspend fun setFavorite(city: City, isFavorite: Boolean): Boolean
    suspend fun addAlert(alert: Alert): Alert
    suspend fun removeAlert(alert: Alert): Boolean
    suspend fun setAlertIsActive(requestCode: Int, isActive: Boolean): Boolean
}

class CityLocalDataSourceImpl(
    private val cityDao: CityDao,
    private val alertDao: AlertDao,
) : CityLocalDataSource {
    
    override val favoritesFlow: Flow<List<City>> by lazy {
        cityDao.getFavoritesFlow().map { it.map { it.toModel() } }
    }
    
    override val alertsFlow: Flow<List<Alert>> by lazy {
        alertDao.getAlertsFlow().map { it.map { it.toModel() } }
    }
    
    override suspend fun findCitiesByName(query: String): List<City> =
        cityDao.findCitiesByName(name = query).map { it.toModel() }

    override suspend fun findClosestCity(coordinates: City.Coordinates, maxDeviation: Double): City? =
        cityDao.findCloseCity(coordinates.latitude, coordinates.longitude, maxDeviation)?.toModel()

    override suspend fun putCities(cities: List<City>) {
        val entities = cities.map { it.toEntity() }
        cityDao.putCities(entities)
    }

    override suspend fun setFavorite(city: City, isFavorite: Boolean): Boolean {
        val updated = cityDao.setFavorite(
            name = city.name.get(AppLocale.English),
            country = city.country,
            isFavorite = isFavorite,
        )
        return updated > 0
    }
    
    override suspend fun addAlert(alert: Alert): Alert {
        val requestCode = alertDao.addAlert(alert.toEntity())
        return alert.copy(requestCode = requestCode.toInt())
    }
    
    override suspend fun removeAlert(alert: Alert): Boolean {
        return alertDao.removeAlert(alert.toEntity()) > 0
    }
    
    override suspend fun setAlertIsActive(requestCode: Int, isActive: Boolean): Boolean {
        return alertDao.setActive(requestCode, isActive) > 0
    }
}
