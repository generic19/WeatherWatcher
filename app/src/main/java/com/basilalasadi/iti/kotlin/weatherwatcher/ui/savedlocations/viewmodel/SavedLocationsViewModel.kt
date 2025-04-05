package com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.*
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.CityCardData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.SavedLocationsScreenData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

class SavedLocationsViewModel(private val dependencies: Dependencies) : ViewModel() {
    val favoritesFlow = dependencies.cityRepository.favoritesFlow.map {
        val appLocale = when (dependencies.settingsRepository.get(Settings.Language) as Setting.Language) {
            Setting.Language.Default -> if (Locale.getDefault().language == "ar") AppLocale.Arabic else AppLocale.English
            Setting.Language.English -> AppLocale.English
            Setting.Language.Arabic -> AppLocale.Arabic
        }

        Result.Success(
            SavedLocationsScreenData(
                items = it.map {
                    CityCardData(
                        city = it,
                        cityName = it.name.get(appLocale),
                        countryName = it.country.countryName.get(appLocale),
                    )
                }
            )
        )
    }
    
    fun searchCities(query: String): Flow<Result<List<CityCardData>>> {
        val appLocale = when (dependencies.settingsRepository.get(Settings.Language) as Setting.Language) {
            Setting.Language.Default -> if (Locale.getDefault().language == "ar") AppLocale.Arabic else AppLocale.English
            Setting.Language.English -> AppLocale.English
            Setting.Language.Arabic -> AppLocale.Arabic
        }
        
        return dependencies.cityRepository.searchCities(query).map {
            val data = it.value?.let {
                it.map {
                    CityCardData(
                        city = it,
                        cityName = it.name.get(appLocale),
                        countryName = it.country.countryName.get(appLocale),
                    )
                }
            }
            
            return@map when (it) {
                is Result.Failure -> Result.Failure(it.error, data)
                is Result.Initial -> Result.Initial(data)
                is Result.Loading -> Result.Loading(data)
                is Result.Success -> Result.Success(data!!)
            }
        }
    }
    
    fun addFavorite(city: City) = viewModelScope.launch {
        dependencies.cityRepository.setFavorite(city, true)
    }
    
    fun removeFavorite(city: City) = viewModelScope.launch {
        dependencies.cityRepository.setFavorite(city, false)
    }
    
    fun addFavorite(location: LatLng) = viewModelScope.launch {
        val city = dependencies.cityRepository.getCity(City.Coordinates(location.latitude, location.longitude))
        dependencies.cityRepository.setFavorite(city, true)
    }
    
    data class Dependencies(
        val cityRepository: CityRepository,
        val settingsRepository: SettingsRepository,
    )
    
    class Factory(private val dependencies: Dependencies) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SavedLocationsViewModel(dependencies) as T
        }
    }
}
