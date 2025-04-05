package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalScrollCaptureInProgress
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberTileOverlayState
import com.google.maps.android.ktx.model.tileOverlayOptions
import java.net.URL
import kotlin.math.pow

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalComposeUiApi::class)
@Composable
fun TemperatureMapCard(
    modifier: Modifier,
    coordinates: City.Coordinates,
) {
    val positionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(coordinates.latitude, coordinates.longitude), 4f)
    }
    
    LabeledCard(
        painter = painterResource(R.drawable.wi_thermometer),
        label = R.string.lbl_temperature_map,
        modifier = modifier
    ) {
        GoogleMap(
            cameraPositionState = positionState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
            
        ) {
            TileOverlay(
                tileProvider = TemperatureTileProvider,
                state = rememberTileOverlayState()
            )
        }
    }
    
}

object TemperatureTileProvider : UrlTileProvider(256, 256) {
    override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
        val range = 0 until (2.0.pow(zoom).toInt())
        
        return if (x in range && y in range) {
            URL(CityRepository.getTemperatureMapTileUrl(x, y, zoom))
        } else {
            null
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TemperatureMapCard(
        coordinates = City.Coordinates(31.0, 30.0),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}