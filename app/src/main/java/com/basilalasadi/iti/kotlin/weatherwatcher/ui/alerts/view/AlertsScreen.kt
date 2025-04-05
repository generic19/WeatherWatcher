package com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.alerts.viewmodel.AlertsViewModel
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.WeatherBottomSheet
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.component.*
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location.LocationHelper
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work.AlertData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

@SuppressLint("InlinedApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier,
    dependencies: AlertsViewModel.Dependencies,
    addAction: MutableState<(() -> Unit)?>,
) {
    val viewModel = viewModel<AlertsViewModel>(factory = remember { AlertsViewModel.Factory(dependencies) })
    
    val alerts = viewModel.alertsFlow.collectAsStateWithLifecycle(emptyList())
    
    val showSearchDialog = remember { mutableStateOf(false) }
    
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var alertToDelete by remember { mutableStateOf<AlertData?>(null) }
    
    val permissionState = rememberPermissionState(POST_NOTIFICATIONS)
    var permissionRequested by remember { mutableStateOf(false) }
    
    LaunchedEffect(permissionState.status.isGranted) {
        if (!permissionState.status.isGranted && !permissionRequested) {
            permissionState.launchPermissionRequest()
            permissionRequested = true
        } else if (permissionState.status.isGranted) {
            addAction.value = { showSearchDialog.value = true }
        }
    }
    
    
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
                text = "Weather Alerts",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
        if (alerts.value.isNotEmpty() == true) {
            itemsIndexed(
                items = alerts.value
            ) { _, item ->
                AlertCard(
                    data = item,
                    onDelete = { alertToDelete = item },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
        } else {
            item {
                Text(
                    text = "No scheduled alerts.",
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .alpha(0.7f)
                        .fillMaxWidth()
                        .padding(top = 48.dp)
                )
            }
        }
    }
    
    if (showSearchDialog.value) {
        SearchDialog(
            onDismissRequest = { showSearchDialog.value = false },
            searchCities = { viewModel.searchCities(it) },
            onSelect = { selectedCity = it },
            modifier = Modifier
        )
    }
    
    if (selectedCity != null) {
        DateTimePickerDialog(
            modifier = Modifier,
            onDismissRequest = {
                selectedCity = null
            },
            onCommit = { time ->
                selectedCity?.let { city ->
                    viewModel.addAlert(
                        Alert(
                            city = city,
                            alertTime = time,
                        )
                    )
                }
            }
        )
    }
    
    alertToDelete?.let {
        AlertDialog(
            onDismissRequest = { alertToDelete = null },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.removeAlert(it.alert)
                        alertToDelete = null
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
                    onClick = { alertToDelete = null },
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.template_remove_alert).format(it.cityName)) },
            text = { Text(stringResource(R.string.template_remove_alert_desc).format(it.cityName, it.alertTime)) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onCommit: (ZonedDateTime) -> Unit,
) {
    var selectedTime by remember { mutableStateOf(ZonedDateTime.now()) }
    
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(utcTimeMillis), ZoneId.systemDefault())
                return date.isAfter(ZonedDateTime.now())
            }
            
            override fun isSelectableYear(year: Int): Boolean {
                return Date().year <= year
            }
        },
        initialSelectedDateMillis = selectedTime.toEpochSecond() * 1000,
    )
    
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = false,
    )
    
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            val date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            selectedTime = selectedTime.withDayOfYear(date.dayOfYear)
        }
    }
    
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        selectedTime = selectedTime.withHour(timePickerState.hour).withMinute(timePickerState.minute)
    }
    
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .imePadding()
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Pick Alert Time",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = Color.Transparent,
                    ),
                    modifier = Modifier
                )
                
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier
                        .offset(8.dp)
                )
                
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
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            onCommit(selectedTime)
                            onDismissRequest()
                        }
                    ) { Text("Confirm") }
                }
            }
        }
    }
}