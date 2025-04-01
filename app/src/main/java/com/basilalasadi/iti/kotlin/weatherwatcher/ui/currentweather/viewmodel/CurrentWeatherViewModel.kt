package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

interface CurrentWeatherViewModel {
    val currentWeatherFlow: MutableStateFlow<WeatherDisplay>
}

//class CurrentWeatherViewModelImpl(
//
//) : ViewModel(), CurrentWeatherViewModel {
//}