package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.viewmodel

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.annotation.RequiresPermission
import androidx.lifecycle.*
import com.basilalasadi.iti.kotlin.weatherwatcher.data.DataException
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.*
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.repository.WeatherRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.CurrentWeatherScreenData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location.LocationHelper
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location.asCoordinates
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
    private val settingsRepository: SettingsRepository,
    private val locationHelper: LocationHelper,
) : ViewModel() {
    private val mutableData = MutableStateFlow<Result<CurrentWeatherScreenData>>(Result.Initial())
    val data: StateFlow<Result<CurrentWeatherScreenData>> = mutableData
    
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    fun load(loadCity: City? = null) = viewModelScope.launch {
        if (mutableData.value is Result.Loading) {
            return@launch
        }
        
        mutableData.emit(Result.Loading())
        
        val settings = settingsRepository.get()
        
        val city = if (loadCity == null) {
            val coordinates = if (settings[Settings.Location] == Setting.Location.Automatic) {
                locationHelper.getLocationFlow().first().asCoordinates()
                
            } else {
                val setting = settingsRepository.get(Settings.ManualLocation) as Setting.ManualLocation
                City.Coordinates(setting.latitude.toDouble(), setting.longitude.toDouble())
            }
            try {
                cityRepository.getCity(coordinates)
            }
            catch (ex: DataException) {
                mutableData.emit(Result.Failure(ex))
                return@launch
            }
        } else {
            loadCity
        }
        
        val mapper = CurrentWeatherViewModelMapper(city, settings)
        
        val weatherFlow = weatherRepository.getLatestWeatherFlow(city)
        val forecastFlow = weatherRepository.getWeatherForecastFlow(city)
        
        val loadingFlow = flow<Result<Unit>> {
            emit(Result.Loading())
            
            var error: DataException? = null
            
            joinAll(
                launch {
                    try {
                        weatherRepository.loadCurrentWeather(city)
                    }
                    catch (ex: DataException) {
                        error = ex
                    }
                },
                launch {
                    try {
                        weatherRepository.loadWeatherForecast(city)
                    }
                    catch (ex: DataException) {
                        error = ex
                    }
                },
            )
            
            if (error == null) {
                emit(Result.Success(Unit))
            } else {
                emit(Result.Failure(error))
            }
        }
        
        combine(weatherFlow, forecastFlow, loadingFlow) { weather, forecast, loadingStatus ->
            val data = mapper.map(weather, forecast)
            
            when (loadingStatus) {
                is Result.Initial -> Result.Initial(data)
                is Result.Loading -> Result.Loading(data)
                is Result.Success -> Result.Success(data)
                is Result.Failure -> Result.Failure(loadingStatus.error, data)
            }
        }.collect {
            mutableData.emit(it)
        }
    }
    
    data class Dependencies(
        val weatherRepository: WeatherRepository,
        val cityRepository: CityRepository,
        val settingsRepository: SettingsRepository,
        val locationHelper: LocationHelper,
    )
    
    class Factory(private val dependencies: Dependencies) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CurrentWeatherViewModel(
                weatherRepository = dependencies.weatherRepository,
                cityRepository = dependencies.cityRepository,
                settingsRepository = dependencies.settingsRepository,
                locationHelper = dependencies.locationHelper,
            ) as T
        }
    }
}
