package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import java.util.Locale

data class CurrentWeatherDisplayData(
    val cityName: String,
    val currentTemperature: Double,
    val feelsLikeTemperature: Double,
    @StringRes val temperatureUnit: Int,
    @DrawableRes val weatherIcon: Int,
    @StringRes val conditionTitle: Int,
    val lowTemperature: Double,
    val highTemperature: Double,
    val dateTime: String,
) {
    companion object {
        val preview = CurrentWeatherDisplayData(
            cityName = "Cairo",
            currentTemperature = 23.0,
            feelsLikeTemperature = 22.0,
            temperatureUnit = R.string.unit_celsius,
            weatherIcon = R.drawable.fair,
            conditionTitle = R.string.cond_clear,
            lowTemperature = 16.0,
            highTemperature = 28.0,
            dateTime = "April 5, 2025 – 8:39 AM",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherDisplay(
    modifier: Modifier = Modifier,
    data: CurrentWeatherDisplayData,
) {
    Surface(
        contentColor = MaterialTheme.colorScheme.onBackground,
        color = Color.Transparent,
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
                    text = "%.0f".format(data.currentTemperature),
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier
                        .padding(start = 12.dp)
                )
                Text(
                    text = stringResource(data.temperatureUnit),
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
                    text = stringResource(data.conditionTitle),
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
                    text = "%.0f%s".format(data.lowTemperature, stringResource(data.temperatureUnit)),
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
                    text = "%.0f%s".format(data.highTemperature, stringResource(data.temperatureUnit)),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                    modifier = Modifier
                )
            }
            Text(
                text = stringResource(R.string.feels_like) + " %.0f%s".format(data.feelsLikeTemperature, stringResource(data.temperatureUnit)),
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
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    CurrentWeatherDisplay(
        data = CurrentWeatherDisplayData.preview,
        modifier = Modifier.padding(vertical = 32.dp)
    )
}