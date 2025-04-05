package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R

data class HourlyForecastCardItemData(
    val timeLabel: String,
    @DrawableRes val weatherIcon: Int,
    val temperature: Double,
    @StringRes val temperatureUnit: Int,
) {
    companion object {
        val preview = listOf(
            HourlyForecastCardItemData(
                timeLabel = "Now",
                weatherIcon = R.drawable.cloudy_2,
                temperature = 12.0,
                temperatureUnit = R.string.unit_celsius,
            ),
            HourlyForecastCardItemData(
                timeLabel = "3 AM",
                weatherIcon = R.drawable.cloudy_2,
                temperature = 13.0,
                temperatureUnit = R.string.unit_celsius,
            ),
            HourlyForecastCardItemData(
                timeLabel = "6 AM",
                weatherIcon = R.drawable.cloudy_1,
                temperature = 15.0,
                temperatureUnit = R.string.unit_celsius,
            ),
            HourlyForecastCardItemData(
                timeLabel = "9 AM",
                weatherIcon = R.drawable.fair,
                temperature = 17.0,
                temperatureUnit = R.string.unit_celsius,
            ),
            HourlyForecastCardItemData(
                timeLabel = "12 PM",
                weatherIcon = R.drawable.clear,
                temperature = 21.0,
                temperatureUnit = R.string.unit_celsius,
            ),
            HourlyForecastCardItemData(
                timeLabel = "3 PM",
                weatherIcon = R.drawable.fair,
                temperature = 19.0,
                temperatureUnit = R.string.unit_celsius,
            ),
            HourlyForecastCardItemData(
                timeLabel = "6 PM",
                weatherIcon = R.drawable.clear,
                temperature = 18.0,
                temperatureUnit = R.string.unit_celsius,
            ),
            HourlyForecastCardItemData(
                timeLabel = "9 PM",
                weatherIcon = R.drawable.cloudy_1,
                temperature = 15.0,
                temperatureUnit = R.string.unit_celsius,
            ),
        )
    }
}

@Composable
fun HourlyForecastCard(modifier: Modifier = Modifier, items: List<HourlyForecastCardItemData>) {
    LabeledCard(
        modifier = modifier,
        label = R.string.lbl_hourly_forecast,
        painter = painterResource(R.drawable.wi_time_10),
    ) {
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 16.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            modifier = Modifier
        ) {
            itemsIndexed(items) { _, item ->
                HourlyWeatherItem(item = item)
            }
        }
    }
}

@Composable
private fun HourlyWeatherItem(item: HourlyForecastCardItemData) {
    Column(
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.Companion
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = item.timeLabel,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
        )
        Image(
            painter = painterResource(item.weatherIcon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
        )
        Text(
            text = "%.0f%s".format(item.temperature, stringResource(item.temperatureUnit)),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    HourlyForecastCard(
        items = HourlyForecastCardItemData.preview,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}