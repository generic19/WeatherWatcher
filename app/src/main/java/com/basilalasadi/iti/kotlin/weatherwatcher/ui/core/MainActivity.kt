package com.basilalasadi.iti.kotlin.weatherwatcher.ui.core

import android.app.LocaleManager
import android.os.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.*
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts.view.AlertsScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts.viewmodel.AlertsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.core.theme.WeatherWatcherTheme
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.CurrentWeatherScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.viewmodel.CurrentWeatherViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.SavedLocationsScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.viewmodel.SavedLocationsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.settings.view.SettingsScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as WeatherWatcherApplication
        
        val weatherRepository = app.weatherRepository
        val cityRepository = app.cityRepository
        val settingsRepository = app.settingsRepository
        val locationHelper = app.locationHelper
        val alertScheduler = app.alertScheduler
        
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
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                LanguageWatcher(settingsRepository)
            }
            
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun LanguageWatcher(settingsRepository: SettingsRepository) {
    val localActivity = LocalActivity.current!!
    val localeManager = localActivity.getSystemService(LocaleManager::class.java)
    
    LaunchedEffect(Unit) {
        settingsRepository.settingsFlow
            .filter { it.first == Settings.Language }
            .map { it.second as Setting.Language }
            .collect { languageSetting ->
                val currentLocales = localeManager.applicationLocales
                
                when (languageSetting) {
                    Setting.Language.Default -> {
                        if (currentLocales != LocaleList.getEmptyLocaleList()) {
                            localeManager.applicationLocales = LocaleList.getEmptyLocaleList()
                            localActivity.recreate()
                        }
                    }
                    
                    Setting.Language.English -> {
                        val requestedLocales = LocaleList.forLanguageTags("en")
                        
                        if (currentLocales != requestedLocales) {
                            localeManager.applicationLocales = requestedLocales
                            localActivity.recreate()
                        }
                    }
                    
                    Setting.Language.Arabic -> {
                        val requestedLocales = LocaleList.forLanguageTags("ar")
                        
                        if (currentLocales != requestedLocales) {
                            localeManager.applicationLocales = requestedLocales
                            localActivity.recreate()
                        }
                    }
                }
            }
    }
}

fun <T : Route> NavDestination?.hasRoute(route: KClass<T>): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } == true
}