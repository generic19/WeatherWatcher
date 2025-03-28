package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.repository

import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.repository.WeatherRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.WeatherLocalDataSource
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.WeatherRemoteDataSource

class WeatherRepositoryImpl(
    localDataSource: WeatherLocalDataSource,
    remoteDataSource: WeatherRemoteDataSource,
) : WeatherRepository {

}