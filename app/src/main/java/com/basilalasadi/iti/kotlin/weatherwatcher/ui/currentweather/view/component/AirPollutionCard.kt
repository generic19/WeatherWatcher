package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather

@Composable
fun AirPollutionCard(modifier: Modifier = Modifier, data: Weather.AirPollution) {
    LabeledCard(
        label = R.string.lbl_air_pollution,
        painter = painterResource(R.drawable.wi_smoke),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Air Quality Index",
                style = MaterialTheme.typography.titleLarge
            )
            
            LinearProgressIndicator(
                progress = { (6 - data.airQualityIndex.index).toFloat() / 5f },
                color = when (data.airQualityIndex) {
                    Weather.AirPollution.AirQualityIndex.Good -> Color(0xFF009688)
                    Weather.AirPollution.AirQualityIndex.Fair -> Color(0xFF4CAF50)
                    Weather.AirPollution.AirQualityIndex.Moderate -> Color(0xFFFFEB3B)
                    Weather.AirPollution.AirQualityIndex.Poor -> Color(0xFFFF9800)
                    Weather.AirPollution.AirQualityIndex.VeryPoor -> Color(0xFFF44336)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            
            Text(
                text = stringResource(data.airQualityIndex.title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 12.dp)
            )
            
            sequenceOf<Pair<String, Double>>(
                "Carbon Monoxide" to data.carbonMonoxide,
                "Nitrogen Monoxide" to data.nitrogenMonoxide,
                "Nitrogen Dioxide" to data.nitrogenDioxide,
                "Ozone" to data.ozone,
                "Sulfur Dioxide" to data.sulfurDioxide,
                "Ammonia" to data.ammonia,
                "Fine Particle Matter" to data.fineParticleMatter,
                "Coarse Particle Matter" to data.coarseParticleMatter,
            ).forEach {
                HorizontalDivider()
                
                Row(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = it.first,
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "%.2f μg/m³".format(it.second),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AirPollutionCard(
        data = AirPollutionPreview,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

val AirPollutionPreview = Weather.AirPollution(
    airQualityIndex = Weather.AirPollution.AirQualityIndex.Poor,
    carbonMonoxide = 327.11,
    nitrogenMonoxide = 8.05,
    nitrogenDioxide = 23.99,
    ozone = 60.08,
    sulfurDioxide = 21.46,
    fineParticleMatter = 38.62,
    coarseParticleMatter = 159.62,
    ammonia = 14.06,
)








