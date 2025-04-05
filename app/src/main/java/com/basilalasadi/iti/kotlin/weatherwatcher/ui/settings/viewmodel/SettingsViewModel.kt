package com.basilalasadi.iti.kotlin.weatherwatcher.ui.settings.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Setting
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Settings
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.SettingsRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.CityCardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import java.util.Locale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val cityRepository: CityRepository,
) : ViewModel() {
    private val TAG = "SettingsViewModel"
    
    val allSettingsFlow = settingsRepository.settingsFlow
        .runningFold(settingsRepository.get()) { map, pair ->
            Log.i(TAG, "folding $pair onto $map")
            map.plus(pair)
        }
        
    fun saveSetting(setting: Setting) {
        settingsRepository.put(setting)
    }
    
    fun searchCities(query: String): Flow<Result<List<CityCardData>>> {
        val appLocale = when (settingsRepository.get(Settings.Language) as Setting.Language) {
            Setting.Language.Default -> if (Locale.getDefault().language == "ar") AppLocale.Arabic else AppLocale.English
            Setting.Language.English -> AppLocale.English
            Setting.Language.Arabic -> AppLocale.Arabic
        }
        
        return cityRepository.searchCities(query).map {
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
    
    data class Dependencies(
        val settingsRepository: SettingsRepository,
        val cityRepository: CityRepository,
    )
    
    class Factory(private val dependencies: Dependencies) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(
                settingsRepository = dependencies.settingsRepository,
                cityRepository = dependencies.cityRepository,
            ) as T
        }
    }
}
