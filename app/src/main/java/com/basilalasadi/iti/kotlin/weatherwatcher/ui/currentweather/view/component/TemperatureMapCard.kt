package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TemperatureMapCard(modifier: Modifier, mapUrl: String) {
    LabeledCard(
        painter = painterResource(R.drawable.wi_thermometer),
        label = R.string.lbl_temperature_map,
        modifier = modifier
    ) {
            GlideImage(
                model = mapUrl,
                contentDescription = null,
                
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(shape = MaterialTheme.shapes.small)
                    .alpha(0.8f)
            )
        }
    
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TemperatureMapCard(
        mapUrl = CityRepository.getTemperatureMapTileUrl(coordinates = City.Coordinates(31.0, 30.0), zoomLevel = 3),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}