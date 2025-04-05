package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Setting
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Settings
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.core.theme.WeatherWatcherTheme
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.*
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.viewmodel.CurrentWeatherViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.modifier.onPointerInteractionStartEnd
import com.google.accompanist.permissions.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.LocalTime

data class CurrentWeatherScreenData(
    val weatherDisplay: CurrentWeatherDisplayData?,
    val hourlyForecastItems: List<HourlyForecastCardItemData>?,
    val dailyForecast: DailyForecastCardData?,
    val coordinates: City.Coordinates?,
    val clouds: CloudsCardData?,
    val precipitation: PrecipitationCardData?,
    val wind: WindCardData?,
    val humidity: String?,
    val pressure: PressureCardData?,
    val sunriseSunset: SunriseSunsetCardData?,
    val visibility: VisibilityCardData?,
    val airPollution: Weather.AirPollution?,
    val localTime: LocalTime?,
    val isNight: Boolean,
) {
    companion object {
        val preview = CurrentWeatherScreenData(
            weatherDisplay = CurrentWeatherDisplayData.preview,
            hourlyForecastItems = HourlyForecastCardItemData.preview,
            dailyForecast = DailyForecastCardData.preview,
            coordinates = City.Coordinates(31.0, 30.0),
            clouds = CloudsCardData.preview,
            precipitation = PrecipitationCardData.preview,
            wind = WindCardData.preview,
            humidity = "55%",
            pressure = PressureCardData.preview,
            sunriseSunset = SunriseSunsetCardData.preview,
            visibility = VisibilityCardData.preview,
            airPollution = AirPollutionPreview,
            localTime = LocalTime.of(8, 21),
            isNight = true,
        )
    }
}

data class Tile(
    val columns: Int = 1,
    val rows: Int = 1,
    val content: @Composable (modifier: Modifier) -> Unit,
)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CurrentWeatherScreen(
    modifier: Modifier = Modifier,
    darkTheme: MutableState<Boolean>,
    dependencies: CurrentWeatherViewModel.Dependencies,
    loadCity: City? = null,
    showMap: Boolean = true,
) {
    val viewModel: CurrentWeatherViewModel = viewModel(factory = remember { CurrentWeatherViewModel.Factory(dependencies) })
    val dataResult by viewModel.data.collectAsStateWithLifecycle()
    
    val isAutomaticLocation by dependencies.settingsRepository.settingsFlow
        .filter { it.first == Settings.Location }
        .map { it.second == Setting.Location.Automatic }
        .collectAsStateWithLifecycle(
            remember { dependencies.settingsRepository.get(Settings.Location) == Setting.Location.Automatic }
        )
    
    val permissionState = rememberMultiplePermissionsState(listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))
    var permissionRequested by remember { mutableStateOf(false) }
    
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (!permissionState.allPermissionsGranted && !permissionRequested) {
            permissionState.launchMultiplePermissionRequest()
            permissionRequested = true
        } else if (permissionState.allPermissionsGranted) {
            viewModel.load(loadCity)
        }
    }
    
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!permissionState.allPermissionsGranted && isAutomaticLocation) {
            ErrorCard(
                title = stringResource(R.string.location_needed),
                errorMessage = stringResource(R.string.location_permission_is_not_granted_and_automatic_location_is_enabled),
                trivial = true,
            )
        } else {
            val data = dataResult.value
            
            if (dataResult is Result.Loading) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else if (dataResult is Result.Failure) {
                ErrorCard(
                    modifier = Modifier,
                    title = stringResource(R.string.fetch_failure),
                    errorMessage = (dataResult as Result.Failure<CurrentWeatherScreenData>).error.message!!,
                    onRetry = {
                        viewModel.load(loadCity)
                    }
                )
            }
            
            if (data != null) {
                darkTheme.value = data.isNight
                
                Content(
                    data = data,
                    showMap = showMap
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(
    modifier: Modifier = Modifier,
    errorMessage: String,
    trivial: Boolean = false,
    title: String? = null,
    onRetry: (() -> Unit)? = null,
) {
    Card(
        colors = if (!trivial) {
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        } else {
            CardDefaults.elevatedCardColors()
        },
        modifier = modifier
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = title ?: "Error",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (onRetry != null) {
                    OutlinedButton(
                        onClick = onRetry
                    ) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Content(modifier: Modifier = Modifier, data: CurrentWeatherScreenData, showMap: Boolean) {
    var scrollingEnabled by remember { mutableStateOf(true) }
    
    val tiles = ArrayList<Tile>().apply {
        data.weatherDisplay?.let { data ->
            add(Tile(2, 0) { modifier ->
                CurrentWeatherDisplay(
                    data = data,
                    modifier = modifier
                        .padding(vertical = 32.dp)
                )
            })
        }
        
        data.hourlyForecastItems?.let { data ->
            add(Tile(2, 0) { modifier ->
                HourlyForecastCard(
                    items = data,
                    modifier = modifier
                )
            })
        }
        
        data.dailyForecast?.let { data ->
            add(Tile(2, 0) { modifier ->
                DailyForecastCard(
                    data = data,
                    modifier = modifier
                )
            })
        }
        
        data.clouds?.let { data ->
            add(Tile { modifier ->
                CloudsCard(
                    data = data,
                    modifier = modifier
                )
            })
        }
        
        data.precipitation?.let { data ->
            add(Tile { modifier ->
                PrecipitationCard(
                    data = data,
                    modifier = modifier
                )
            })
        }
        
        data.wind?.let { data ->
            add(Tile(2, 1) { modifier ->
                WindCard(
                    data = data,
                    modifier = modifier
                )
            })
        }
        
        if (showMap) {
            data.coordinates?.let { data ->
                add(Tile(2, 0) { modifier ->
                    TemperatureMapCard(
                        coordinates = data,
                        modifier = modifier
                            .onPointerInteractionStartEnd(
                                onPointerStart = { scrollingEnabled = false },
                                onPointerEnd = { scrollingEnabled = true }
                            )
                    )
                })
            }
        }
        
        data.humidity?.let { data ->
            add(Tile { modifier ->
                HumidityCard(
                    humidity = data,
                    modifier = modifier
                )
            })
        }
        
        data.pressure?.let { data ->
            add(Tile { modifier ->
                PressureCard(
                    data = data,
                    modifier = modifier
                )
            })
        }
        
        data.airPollution?.let { data ->
            add(Tile(2, 0) { modifier ->
                AirPollutionCard(
                    data = data,
                    modifier = modifier,
                )
            })
        }
        
        data.sunriseSunset?.let { data ->
            add(Tile { modifier ->
                SunriseSunsetCard(
                    data = data,
                    modifier = modifier
                )
            })
        }
        
        data.visibility?.let { data ->
            add(Tile { modifier ->
                VisibilityCard(
                    data = data,
                    modifier = modifier
                )
            })
        }
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        userScrollEnabled = scrollingEnabled,
        state = rememberLazyGridState(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        tiles.forEach { tile ->
            val modifier = if (tile.rows > 0) {
                Modifier.aspectRatio(tile.columns / tile.rows.toFloat())
            } else {
                Modifier
            }
            
            item(span = { GridItemSpan(tile.columns) }) {
                tile.content(modifier)
            }
        }
    }
}

//@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    showBackground = false,
    backgroundColor = 0xff000000,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ContentPreview() {
    WeatherWatcherTheme(darkTheme = false) {
        Content(
            modifier = Modifier
                .systemBarsPadding(),
            data = CurrentWeatherScreenData.preview,
            showMap = true
        )
    }
}
