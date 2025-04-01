package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R

interface CurrentWeatherDisplayData {
    val cityName: String
    val currentTemperature: String
    val feelsLikeTemperature: String
    val temperatureUnit: String
    @get:DrawableRes val weatherIcon: Int
    val conditionTitle: String
    val lowTemperature: String
    val highTemperature: String
    val dateTime: String
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherDisplay(
    modifier: Modifier = Modifier,
    data: CurrentWeatherDisplayData,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = data.cityName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .alpha(0.7f)
        )
        Row {
            Text(
                text = data.currentTemperature,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            Text(
                text = data.temperatureUnit,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(data.weatherIcon),
                tint = null,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )
            Text(
                text = data.conditionTitle,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .alpha(0.6f)
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.lbl_low),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .alpha(0.8f)
            )
            Text(
                text = data.lowTemperature + data.temperatureUnit,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
            )
            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )
            Text(
                text = stringResource(R.string.lbl_high),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .alpha(0.8f)
            )
            Text(
                text = data.highTemperature + data.temperatureUnit,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
            )
        }
        Text(
            text = "Feels like 22°C",
            modifier = Modifier
                .alpha(0.5f)
                .padding(top = 2.dp)
        )
        Text(
            text = data.dateTime,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .alpha(0.4f)
                .padding(top = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    CurrentWeatherDisplay(
        data = object : CurrentWeatherDisplayData {
            override val cityName = "Cairo"
            override val currentTemperature = "23"
            override val feelsLikeTemperature = "22"
            override val temperatureUnit = "°C"
            override val weatherIcon = R.drawable.fair
            override val conditionTitle = "Clear"
            override val lowTemperature = "16"
            override val highTemperature = "28"
            override val dateTime = "April 5, 2025 – 8:39 AM"
        },
        modifier = Modifier.padding(vertical = 32.dp)
    )
}