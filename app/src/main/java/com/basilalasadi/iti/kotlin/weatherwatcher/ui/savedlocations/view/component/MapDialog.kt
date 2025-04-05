package com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.first

@SuppressLint("MissingPermission")
@Composable
fun MapDialog(
    modifier: Modifier = Modifier.Companion,
    onDismissRequest: () -> Unit,
    onLocationSelected: (LatLng) -> Unit,
    locationHelper: LocationHelper,
) {
    val positionState = rememberCameraPositionState()
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    
    LaunchedEffect(Unit) {
        locationHelper.getLocationFlow().first().let {
            positionState.move(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 10f))
        }
    }
    
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .imePadding()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.Companion
            ) {
                GoogleMap(
                    cameraPositionState = positionState,
                    onMapClick = { selectedLocation = it },
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    selectedLocation?.let {
                        Marker(
                            state = MarkerState.Companion(position = it),
                            title = "Selected Location",
                        )
                    }
                }
                TextButton(
                    enabled = selectedLocation != null,
                    onClick = {
                        selectedLocation?.let {
                            onLocationSelected(it)
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.select_location))
                }
            }
        }
    }
}