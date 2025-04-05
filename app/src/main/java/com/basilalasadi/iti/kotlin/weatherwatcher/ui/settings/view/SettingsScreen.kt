package com.basilalasadi.iti.kotlin.weatherwatcher.ui.settings.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsRadioButton
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Setting
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Setting.EnumSetting
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Settings
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.component.MapDialog
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.component.SearchDialog
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.settings.viewmodel.SettingsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location.LocationHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    dependencies: SettingsViewModel.Dependencies,
    locationHelper: LocationHelper,
) {
    val viewModel = viewModel<SettingsViewModel>(factory = remember { SettingsViewModel.Factory(dependencies) })
    val allSettings = viewModel.allSettingsFlow.collectAsStateWithLifecycle(emptyMap())
    
    var itemToChange by remember { mutableStateOf<Pair<Settings, Setting>?>(null) }
    var showMapDialog by remember { mutableStateOf(false) }
    
    val permissionState = rememberMultiplePermissionsState(listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))
    
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(16.dp)
        )
        
        allSettings.value[Settings.Language]?.let { setting ->
            SettingsMenuLink(
                title = { Text(stringResource(Settings.Language.title)) },
                subtitle = { Text(stringResource(setting.label)) },
                onClick = { itemToChange = Settings.Language to setting }
            )
        }
        allSettings.value[Settings.TemperatureUnit]?.let { setting ->
            SettingsMenuLink(
                title = { Text(stringResource(Settings.TemperatureUnit.title)) },
                subtitle = { Text(stringResource(setting.label)) },
                onClick = { itemToChange = Settings.TemperatureUnit to setting }
            )
        }
        allSettings.value[Settings.SpeedUnit]?.let { setting ->
            SettingsMenuLink(
                title = { Text(stringResource(Settings.SpeedUnit.title)) },
                subtitle = { Text(stringResource(setting.label)) },
                onClick = { itemToChange = Settings.SpeedUnit to setting }
            )
        }
        allSettings.value[Settings.DistanceUnit]?.let { setting ->
            SettingsMenuLink(
                title = { Text(stringResource(Settings.DistanceUnit.title)) },
                subtitle = { Text(stringResource(setting.label)) },
                onClick = { itemToChange = Settings.DistanceUnit to setting }
            )
        }
        allSettings.value[Settings.PressureUnit]?.let { setting ->
            SettingsMenuLink(
                title = { Text(stringResource(Settings.PressureUnit.title)) },
                subtitle = { Text(stringResource(setting.label)) },
                onClick = { itemToChange = Settings.PressureUnit to setting }
            )
        }
        allSettings.value[Settings.Location]?.let { setting ->
            SettingsMenuLink(
                title = { Text(stringResource(Settings.Location.title)) },
                subtitle = { Text(stringResource(setting.label)) },
                onClick = { itemToChange = Settings.Location to setting }
            )
        }
        allSettings.value[Settings.ManualLocation]?.let { setting ->
            SettingsMenuLink(
                title = { Text(stringResource(Settings.ManualLocation.title)) },
                subtitle = { Text(stringResource(setting.label)) },
                onClick = { itemToChange = Settings.ManualLocation to setting }
            )
        }
    }
    
    itemToChange?.let {
        when (it.second) {
            is EnumSetting<*> -> {
                EnumSettingDialog(
                    settings = it.first,
                    setting = it.second as EnumSetting<*>,
                    onCommit = { viewModel.saveSetting(it) },
                    onDismissRequest = { itemToChange = null },
                    modifier = Modifier
                )
            }
            
            is Setting.LocationSetting -> {
                if (!showMapDialog) {
                    SearchDialog(
                        onDismissRequest = { afterSelectFromMap ->
                            if (!afterSelectFromMap) {
                                itemToChange = null
                            }
                        },
                        onSelectFromMap =
                            if (permissionState.allPermissionsGranted) {
                                { showMapDialog = true }
                            } else {
                                null
                            },
                        searchCities = { viewModel.searchCities(it) },
                        onSelect = {
                            viewModel.saveSetting(
                                Setting.ManualLocation(it.coordinates.latitude.toFloat(), it.coordinates.longitude.toFloat())
                            )
                        },
                        modifier = Modifier
                    )
                } else {
                    MapDialog(
                        onDismissRequest = {
                            showMapDialog = false
                            itemToChange = null
                        },
                        locationHelper = locationHelper,
                        onLocationSelected = {
                            viewModel.saveSetting(
                                Setting.ManualLocation(it.latitude.toFloat(), it.longitude.toFloat())
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnumSettingDialog(
    modifier: Modifier = Modifier,
    settings: Settings,
    setting: EnumSetting<*>,
    onCommit: (Setting) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var selected by remember { mutableStateOf(setting) }
    
    SettingDialog(
        settings = settings,
        onCommit = {
            onCommit(selected)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        setting.settingEntries.forEach {
            SettingsRadioButton(
                state = it == selected,
                title = { Text(stringResource((it as Setting).label)) },
                onClick = { selected = it as EnumSetting<*> },
                colors = SettingsTileDefaults.colors(
                    containerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingDialog(
    modifier: Modifier = Modifier,
    settings: Settings,
    onCommit: () -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = stringResource(settings.title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                
                content()
                
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.discard))
                    }
                    
                    Button(
                        onClick = {
                            onCommit()
                            onDismissRequest()
                        }
                    ) { Text(stringResource(R.string.confirm)) }
                }
            }
        }
    }
}
