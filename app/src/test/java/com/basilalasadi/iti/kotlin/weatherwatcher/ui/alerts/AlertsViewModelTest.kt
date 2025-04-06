package com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts

import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.SettingsRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts.viewmodel.AlertsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work.AlertScheduler
import io.mockk.*
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class AlertsViewModelTest {
    private lateinit var cityRepository: CityRepository
    private lateinit var alertScheduler: AlertScheduler
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: AlertsViewModel
    
    private val city = City(
        coordinates = City.Coordinates(0.0, 0.0),
        name = LocalizedName("testAr", "testEn"),
        country = City.Country.EG,
    )
    
    private val alert = Alert(requestCode = 1, city = city, alertTime = ZonedDateTime.now())
    
    @Before
    fun setup() {
        cityRepository = mockkClass(CityRepository::class, relaxed = true)
        alertScheduler = mockkClass(AlertScheduler::class, relaxed = true)
        settingsRepository = mockkClass(SettingsRepository::class, relaxed = true)
        
        viewModel = AlertsViewModel(cityRepository, alertScheduler, settingsRepository)
        
        coEvery { cityRepository.addAlert(alert) } returns(alert)
    }
    
    @Test
    fun addAlert_alert_addedScheduledAndSetActive() {
        viewModel.addAlert(alert)
        
        coVerify { cityRepository.addAlert(alert) }
        coVerify { alertScheduler.scheduleAlert(alert) }
        coVerify { cityRepository.setAlertActive(alert, true) }
    }
    
    @Test
    fun removeAlert_alert_unscheduledAndRemoved() {
        viewModel.removeAlert(alert)
        
        verify { alertScheduler.unscheduleAlert(alert) }
        coVerify { cityRepository.removeAlert(alert) }
    }
}