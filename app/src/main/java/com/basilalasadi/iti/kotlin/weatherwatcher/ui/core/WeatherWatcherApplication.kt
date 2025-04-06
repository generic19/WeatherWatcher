package com.basilalasadi.iti.kotlin.weatherwatcher.ui.core

import android.app.Application
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepositoryImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.CityLocalDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.CityRemoteDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.api.CityApiService
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.database.AppDatabase
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.SettingsRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.repository.WeatherRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.repository.WeatherRepositoryImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.WeatherLocalDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.WeatherRemoteDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.WeatherApiService
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location.LocationHelper
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work.AlertScheduler

class WeatherWatcherApplication : Application() {
    lateinit var weatherRepository: WeatherRepository
    lateinit var cityRepository: CityRepository
    lateinit var settingsRepository: SettingsRepository
    lateinit var locationHelper: LocationHelper
    lateinit var alertScheduler: AlertScheduler
    
    override fun onCreate() {
        super.onCreate()
        
        val database = AppDatabase.getInstance(this)
        val weatherDao = database.getWeatherDao()
        val cityDao = database.getCityDao()
        val alertDao = database.getAlertDao()
        
        val weatherApiService = WeatherApiService.create()
        val cityApiService = CityApiService.create()
        
        weatherRepository = WeatherRepositoryImpl(
            localDataSource = WeatherLocalDataSourceImpl(weatherDao),
            remoteDataSource = WeatherRemoteDataSourceImpl(weatherApiService)
        )
        cityRepository = CityRepositoryImpl(
            localDataSource = CityLocalDataSourceImpl(cityDao, alertDao),
            remoteDataSource = CityRemoteDataSourceImpl(cityApiService),
        )
        settingsRepository = SettingsRepository(getSharedPreferences(getString(R.string.shared_preferences), 0))
        locationHelper = LocationHelper(this)
        alertScheduler = AlertScheduler(this)
    }
}