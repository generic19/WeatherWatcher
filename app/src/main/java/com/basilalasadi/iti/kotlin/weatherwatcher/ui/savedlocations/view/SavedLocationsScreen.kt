package com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.core.theme.WeatherWatcherTheme
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.CurrentWeatherScreen
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.viewmodel.CurrentWeatherViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.component.CityCard
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.component.MapDialog
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.component.SearchDialog
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.viewmodel.SavedLocationsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location.LocationHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

data class SavedLocationsScreenData(
    val items: List<CityCardData>,
)

data class CityCardData(
    val city: City,
    val cityName: String,
    val countryName: String,
)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SavedLocationsScreen(
    modifier: Modifier = Modifier,
    dependencies: SavedLocationsViewModel.Dependencies,
    currentLocationDependencies: CurrentWeatherViewModel.Dependencies,
    locationHelper: LocationHelper,
    addAction: MutableState<(() -> Unit)?>,
) {
    val viewModel: SavedLocationsViewModel = viewModel(factory = remember { SavedLocationsViewModel.Factory(dependencies) })
    val result = viewModel.favoritesFlow.collectAsStateWithLifecycle(Result.Initial())
    val data = result.value.value
    
    val showAddDialog = remember { mutableStateOf(false) }
    addAction.value = {
        showAddDialog.value = true
    }
    
    var cityToDelete by remember { mutableStateOf<CityCardData?>(null) }
    var showMapDialog by remember { mutableStateOf(false) }
    var showCityWeather by remember { mutableStateOf<City?>(null) }
    
    val permissionState = rememberMultiplePermissionsState(listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            bottom = 88.dp,
            top = 8.dp,
            start = 8.dp,
            end = 8.dp,
        ),
        modifier = modifier
    ) {
        item {
            Text(
                text = "Saved Locations",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
        if (data?.items?.isNotEmpty() == true) {
            itemsIndexed(
                items = data.items
            ) { _, item ->
                CityCard(
                    data = item,
                    onClick = { showCityWeather = item.city },
                    onDelete = { cityToDelete = item },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
        } else {
            item {
                Text(
                    text = "No saved locations.",
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .alpha(0.7f)
                        .fillMaxWidth()
                        .padding(top = 48.dp)
                )
            }
        }
    }
    
    if (showAddDialog.value) {
        SearchDialog(
            onDismissRequest = { showAddDialog.value = false },
            onSelectFromMap =
                if (permissionState.allPermissionsGranted) {
                    { showMapDialog = true }
                } else {
                    null
                },
            searchCities = { viewModel.searchCities(it) },
            onSelect = { viewModel.addFavorite(it) },
            modifier = Modifier
        )
    }
    
    if (showMapDialog) {
        MapDialog(
            onDismissRequest = { showMapDialog = false },
            locationHelper = locationHelper,
            onLocationSelected = {
                viewModel.addFavorite(it)
            }
        )
    }
    
    cityToDelete?.let {
        AlertDialog(
            onDismissRequest = { cityToDelete = null },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.removeFavorite(it.city)
                        cityToDelete = null
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        cityToDelete = null
                    },
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.template_remove_city).format(it.cityName)) },
            text = { Text(stringResource(R.string.template_remove_city_desc).format(it.cityName, it.countryName)) },
        )
    }
    
    showCityWeather?.let {
        WeatherBottomSheet(
            city = it,
            onDismissRequest = { showCityWeather = null },
            currentLocationDependencies = currentLocationDependencies,
            modifier = Modifier
                .statusBarsPadding(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    currentLocationDependencies: CurrentWeatherViewModel.Dependencies,
    city: City,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    
    val darkTheme = remember { mutableStateOf(false) }
    
    WeatherWatcherTheme(
        darkTheme = darkTheme.value
    ) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            modifier = modifier
        ) {
            CurrentWeatherScreen(
                darkTheme = darkTheme,
                dependencies = currentLocationDependencies,
                loadCity = city,
                showMap = false,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}
