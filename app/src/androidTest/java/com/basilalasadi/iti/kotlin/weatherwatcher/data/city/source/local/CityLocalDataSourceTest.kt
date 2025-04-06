package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.basilalasadi.iti.kotlin.weatherwatcher.data.LocalizedName
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.AlertDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.CityDao
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.database.AppDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
@SmallTest
class CityLocalDataSourceTest {
    private lateinit var database: AppDatabase
    private lateinit var cityDao: CityDao
    private lateinit var alertDao: AlertDao
    private lateinit var localDataSource: CityLocalDataSource
    
    private val city = City(
        City.Coordinates(0.0, 0.0),
        name = LocalizedName("a", "e"),
        country = City.Country.EG,
    )
    
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        
        cityDao = database.getCityDao()
        alertDao = database.getAlertDao()
        localDataSource = CityLocalDataSourceImpl(cityDao, alertDao)
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addAlert_alert_alertAdded() = runTest {
        val alert = Alert(
            city = city,
            alertTime = ZonedDateTime.now(),
        )
        
        val addedAlert = localDataSource.addAlert(alert)
        
        assertEquals(alert.city, addedAlert.city)
        assertEquals(alert.alertTime.toEpochSecond(), addedAlert.alertTime.toEpochSecond())
        assertNotNull(addedAlert.requestCode)
        
        val storedAlert = localDataSource.alertsFlow.first()[0]
        
        assertEquals(addedAlert.city, storedAlert.city)
        assertEquals(addedAlert.alertTime.toEpochSecond(), storedAlert.alertTime.toEpochSecond())
        assertEquals(addedAlert.requestCode, storedAlert.requestCode)
    }
    
    @Test
    fun removeAlert_alerts_alertRemoved() = runTest {
        val alert1 = localDataSource.addAlert(Alert(city = city, alertTime =  ZonedDateTime.now()))
        val alert2 = localDataSource.addAlert(Alert(city = city, alertTime =  ZonedDateTime.now()))
        
        localDataSource.removeAlert(alert1)
        
        val storedAlerts = localDataSource.alertsFlow.first()
        
        assertEquals(1, storedAlerts.size)
        assertEquals(alert2.requestCode, storedAlerts[0].requestCode)
    }
}