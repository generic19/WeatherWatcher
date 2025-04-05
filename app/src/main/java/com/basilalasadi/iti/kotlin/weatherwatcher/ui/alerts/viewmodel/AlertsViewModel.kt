package com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Setting
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Settings
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.SettingsRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.CityCardData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work.AlertData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work.AlertScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.map

class AlertsViewModel(
    private val cityRepository: CityRepository,
    private val alertScheduler: AlertScheduler,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val alertsFlow = cityRepository.alertsFlow
        .map {
            val appLocale = when (settingsRepository.get(Settings.Language) as Setting.Language) {
                Setting.Language.Default -> if (Locale.getDefault().language == "ar") AppLocale.Arabic else AppLocale.English
                Setting.Language.English -> AppLocale.English
                Setting.Language.Arabic -> AppLocale.Arabic
            }
            val locale = when (appLocale) {
                AppLocale.Arabic -> Locale.forLanguageTag("ar")
                AppLocale.English -> Locale.forLanguageTag("en")
            }
            
            it.map {
                AlertData(
                    alert = it,
                    cityName = it.city.name.get(appLocale),
                    alertTime = it.alertTime.format(DateTimeFormatter.ofPattern("d MMMM yyyy h:mm a OOOO", locale)),
                )
            }
        }
    
    fun addAlert(a: Alert) = viewModelScope.launch {
        val alert = cityRepository.addAlert(a)
        alertScheduler.scheduleAlert(alert)
        cityRepository.setAlertActive(alert, true)
    }
    
    fun removeAlert(alert: Alert) = viewModelScope.launch {
        alertScheduler.unscheduleAlert(alert)
        cityRepository.removeAlert(alert)
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
        val cityRepository: CityRepository,
        val alertScheduler: AlertScheduler,
        val settingsRepository: SettingsRepository,
    )
    
    class Factory(private val dependencies: Dependencies) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlertsViewModel(
                cityRepository = dependencies.cityRepository,
                alertScheduler = dependencies.alertScheduler,
                settingsRepository = dependencies.settingsRepository,
            ) as T
        }
    }
}