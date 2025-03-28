package com.basilalasadi.iti.kotlin.weatherwatcher.data.location.repository

import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.LocationException
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.local.LocationLocalDataSource
import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.source.remote.LocationRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.CoroutineContext

interface LocationRepository {
    val favoritesFlow: Flow<List<City>>

    fun searchCities(query: String): Flow<Result<List<City>>>
    suspend fun getCity(coordinates: City.Coordinates): City
    suspend fun setFavorite(city: City, isFavorite: Boolean): Boolean
}

class LocationRepositoryImpl(
    private val localDataSource: LocationLocalDataSource,
    private val remoteDataSource: LocationRemoteDataSource,
) : LocationRepository {
    override val favoritesFlow: Flow<List<City>> = localDataSource.favoritesFlow

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun searchCities(query: String): Flow<Result<List<City>>> {
        return channelFlow<Result<List<City>>> {
            if (query.isBlank()) {
                send(Result.Initial())
                return@channelFlow
            }

            send(Result.Loading())
            
            val deferredLocal = async { localDataSource.findCitiesByName(query) }
            val deferredRemote = async<Pair<List<City>?, LocationException?>> {
                try {
                    return@async remoteDataSource.findCitiesByQuery(query) to null
                } catch (ex: LocationException) {
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
        return localDataSource.findCloseCity(coordinates)
            ?: remoteDataSource.findCityByCoordinates(coordinates)
    }

    override suspend fun setFavorite(city: City, isFavorite: Boolean) = localDataSource.setFavorite(city, isFavorite)

}