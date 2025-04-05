package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.celsius

data class DailyForecastCardData(
    val forecastMin: Units.Temperature,
    val forecastMax: Units.Temperature,
    val forecastDays: List<ForecastDay>,
    @StringRes val temperatureUnit: Int,
) {
    data class ForecastDay(
        val dayName: String,
        val minTemperature: Units.Temperature,
        val maxTemperature: Units.Temperature,
        @get:DrawableRes val weatherIcon: Int,
    )
    
    companion object {
        val preview = DailyForecastCardData(
            forecastMin = 12.0.celsius,
            forecastMax = 33.0.celsius,
            temperatureUnit = R.string.unit_celsius,
            forecastDays = listOf(
                ForecastDay(
                    dayName = "Saturday",
                    minTemperature = 12.0.celsius,
                    maxTemperature = 19.0.celsius,
                    weatherIcon = R.drawable.cloudy_2,
                ),
                ForecastDay(
                    dayName = "Sunday",
                    minTemperature = 13.0.celsius,
                    maxTemperature = 21.0.celsius,
                    weatherIcon = R.drawable.cloudy_1,
                ),
                ForecastDay(
                    dayName = "Monday",
                    minTemperature = 22.0.celsius,
                    maxTemperature = 33.0.celsius,
                    weatherIcon = R.drawable.clear,
                ),
                ForecastDay(
                    dayName = "Tuesday",
                    minTemperature = 17.0.celsius,
                    maxTemperature = 24.0.celsius,
                    weatherIcon = R.drawable.fair,
                ),
                ForecastDay(
                    dayName = "Wednesday",
                    minTemperature = 15.0.celsius,
                    maxTemperature = 23.0.celsius,
                    weatherIcon = R.drawable.fair,
                ),
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyForecastCard(modifier: Modifier, data: DailyForecastCardData) {
    LabeledCard(
        painter = rememberVectorPainter(Icons.Outlined.DateRange),
        label = R.string.lbl_daily_forecast,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
        ) {
            data.forecastDays.forEach { item ->
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = item.dayName,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(2.5f)
                    )
                    Image(
                        painter = painterResource(item.weatherIcon),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                    )
                    Spacer(modifier = Modifier.weight(0.25f))
                    Text(
                        text = "%.0f%s".format(item.minTemperature.value, stringResource(data.temperatureUnit)),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    TemperatureRangeIndicator(
                        lowTemperature = item.minTemperature,
                        highTemperature = item.maxTemperature,
                        minTemperature = data.forecastMin,
                        maxTemperature = data.forecastMax,
                        modifier = Modifier.Companion
                            .weight(2f)
                            .padding(horizontal = 12.dp)
                    )
                    Text(
                        text = "%.0f%s".format(item.maxTemperature.value, stringResource(data.temperatureUnit)),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    
    DailyForecastCard(
        data = DailyForecastCardData.preview,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    )
}