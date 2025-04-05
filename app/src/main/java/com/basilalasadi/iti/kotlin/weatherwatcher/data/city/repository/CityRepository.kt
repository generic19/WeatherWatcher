package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository

import com.basilalasadi.iti.kotlin.weatherwatcher.BuildConfig
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.data.DataException
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.CityLocalDataSource
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.CityRemoteDataSource
import com.basilalasadi.iti.kotlin.weatherwatcher.utility.math.AppMath
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

interface CityRepository {
    val favoritesFlow: Flow<List<City>>
    val alertsFlow: Flow<List<Alert>>
    
    fun searchCities(query: String): Flow<Result<List<City>>>
    
    suspend fun getCity(coordinates: City.Coordinates): City
    suspend fun setFavorite(city: City, isFavorite: Boolean): Boolean
    suspend fun addAlert(alert: Alert): Alert
    suspend fun removeAlert(alert: Alert): Boolean
    suspend fun setAlertActive(alert: Alert, isActive: Boolean): Boolean
    
    companion object {
        fun getTemperatureMapTileUrl(tileX: Int, tileY: Int, zoomLevel: Int): String {
            val base = "https://tile.openweathermap.org/map/temp_new"
            return "$base/$zoomLevel/$tileX/$tileY?appid=${BuildConfig.OPEN_WEATHER_API_KEY}"
        }
        
        fun getTemperatureMapTileUrl(coordinates: City.Coordinates, zoomLevel: Int): String {
            val (tileX, tileY) = AppMath.coordinatesToMapTile(coordinates, zoomLevel)
            return getTemperatureMapTileUrl(tileX, tileY, zoomLevel)
        }
    }
}

class CityRepositoryImpl(
    private val localDataSource: CityLocalDataSource,
    private val remoteDataSource: CityRemoteDataSource,
) : CityRepository {
    override val favoritesFlow: Flow<List<City>> = localDataSource.favoritesFlow
    override val alertsFlow: Flow<List<Alert>> = localDataSource.alertsFlow
    
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun searchCities(query: String): Flow<Result<List<City>>> {
        return channelFlow<Result<List<City>>> {
            if (query.isBlank()) {
                send(Result.Initial())
                return@channelFlow
            }

            send(Result.Loading())
            
            val deferredLocal = async { localDataSource.findCitiesByName(query) }
            val deferredRemote = async<Pair<List<City>?, DataException?>> {
                try {
                    return@async remoteDataSource.findCitiesByQuery(query) to null
                } catch (ex: DataException) {
                    return@async null to ex
                }
            }
            
            val localData = deferredLocal.await()
            send(Result.Loading(localData))
            
            val remoteResult = deferredRemote.await()
            
            if (remoteResult.first != null) {
                localDataSource.putCities(remoteResult.first!!)
                send(Result.Success(remoteResult.first!!))
            } else {
                send(Result.Failure(remoteResult.second!!, localData))
            }
        }
    }

    override suspend fun getCity(coordinates: City.Coordinates): City {
        val local = localDataSource.findClosestCity(coordinates)
        
        if (local != null) {
            return local
        } else {
            val remote = remoteDataSource.findCityByCoordinates(coordinates)
            localDataSource.putCities(listOf(remote))
            return remote
        }
    }

    override suspend fun setFavorite(city: City, isFavorite: Boolean) =
        localDataSource.setFavorite(city, isFavorite)
    
    override suspend fun addAlert(alert: Alert): Alert = localDataSource.addAlert(alert)
    
    override suspend fun removeAlert(alert: Alert): Boolean = localDataSource.removeAlert(alert)
    
    override suspend fun setAlertActive(alert: Alert, isActive: Boolean): Boolean {
        return if (alert.requestCode == null) {
            if (isActive) {
                throw DataException("Alert does not have a request code.")
            } else {
                true
            }
        } else {
            localDataSource.setAlertIsActive(alert.requestCode, isActive)
        }
    }
}
