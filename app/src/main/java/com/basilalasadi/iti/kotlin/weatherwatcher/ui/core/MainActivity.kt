package com.basilalasadi.iti.kotlin.weatherwatcher.ui.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepositoryImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.CityLocalDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.CityRemoteDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.api.CityApiService
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.database.AppDatabase
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.SettingsRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.repository.WeatherRepositoryImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.WeatherLocalDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.WeatherRemoteDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.WeatherApiService
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts.view.AlertsScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts.viewmodel.AlertsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.core.theme.WeatherWatcherTheme
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.CurrentWeatherScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.viewmodel.CurrentWeatherViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.SavedLocationsScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.viewmodel.SavedLocationsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.settings.view.SettingsScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.settings.viewmodel.SettingsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location.LocationHelper
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work.AlertScheduler
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getInstance(this)
        val weatherDao = database.getWeatherDao()
        val cityDao = database.getCityDao()
        val alertDao = database.getAlertDao()
        
        val weatherApiService = WeatherApiService.create()
        val cityApiService = CityApiService.create()
        
        val weatherRepository = WeatherRepositoryImpl(
            localDataSource =  WeatherLocalDataSourceImpl(weatherDao),
            remoteDataSource = WeatherRemoteDataSourceImpl(weatherApiService)
        )
        val cityRepository = CityRepositoryImpl(
            localDataSource = CityLocalDataSourceImpl(cityDao, alertDao),
            remoteDataSource = CityRemoteDataSourceImpl(cityApiService),
        )
        val settingsRepository = SettingsRepository(getSharedPreferences(getString(R.string.shared_preferences), 0))
        val locationHelper = LocationHelper(application)
        val alertScheduler = AlertScheduler(this)
        
        val currentWeatherDependencies = CurrentWeatherViewModel.Dependencies(
            weatherRepository = weatherRepository,
            cityRepository = cityRepository,
            settingsRepository = settingsRepository,
            locationHelper = locationHelper,
        )
        val savedLocationsDependencies = SavedLocationsViewModel.Dependencies(
            cityRepository = cityRepository,
            settingsRepository = settingsRepository,
        )
        val alertsDependencies = AlertsViewModel.Dependencies(
            cityRepository = cityRepository,
            settingsRepository = settingsRepository,
            alertScheduler = alertScheduler,
        )
        val settingsDependencies = SettingsViewModel.Dependencies(
            settingsRepository = settingsRepository,
            cityRepository = cityRepository,
        )
        
        enableEdgeToEdge()
        setContent {
            val darkTheme = remember { mutableStateOf<Boolean>(false) }
            
            val navController = rememberNavController()
            val addAction = remember { mutableStateOf<(() -> Unit)?>(null) }
            
            WeatherWatcherTheme(
                darkTheme = darkTheme.value,
            ) {
                Scaffold(
                    floatingActionButton = {
                        AnimatedVisibility(
                            addAction.value != null,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            addAction.value?.let {
                                FloatingActionButton(
                                    onClick = it,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
                    },
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            
                            tabRoutes.forEach { tabRoute ->
                                NavigationBarItem(
                                    selected = currentDestination.hasRoute(tabRoute.route::class),
                                    onClick = {
                                        addAction.value = null
                                        
                                        navController.navigate(tabRoute.route) {
                                            popUpTo(navController.graph.findStartDestination().id)
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = { Icon(imageVector = tabRoute.icon, contentDescription = null) },
                                    
                                    modifier = Modifier
                                        .padding(horizontal = 0.dp)
                                )
                            }
                        }
                    }
                ) { contentPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.CurrentWeather
                    ) {
                        composable<Route.CurrentWeather> {
                            CurrentWeatherScreen(
                                dependencies = currentWeatherDependencies,
                                darkTheme = darkTheme,
                                modifier = Modifier
                                    .padding(contentPadding)
                            )
                        }
                        
                        composable<Route.SavedLocations> {
                            SavedLocationsScreen(
                                dependencies = savedLocationsDependencies,
                                currentLocationDependencies = currentWeatherDependencies,
                                locationHelper = locationHelper,
                                addAction = addAction,
                                modifier = Modifier
                                    .padding(contentPadding)
                            )
                        }
                        
                        composable<Route.Alerts> {
                            AlertsScreen(
                                dependencies = alertsDependencies,
                                addAction = addAction,
                                modifier = Modifier
                                    .padding(contentPadding)
                            )
                        }
                        
                        composable<Route.Settings> {
                            SettingsScreen(
                                dependencies = settingsDependencies,
                                locationHelper = locationHelper,
                                modifier = Modifier
                                    .padding(contentPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun<T: Route> NavDestination?.hasRoute(route: KClass<T>): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } == true
}