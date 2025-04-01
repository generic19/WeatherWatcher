package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R

interface HourlyForecastCardItemData {
    val timeLabel: String
    @get:DrawableRes val weatherIcon: Int
    val temperature: String
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
            text = item.temperature,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    val items = listOf(
        object : HourlyForecastCardItemData {
            override val timeLabel: String = "Now"
            override val weatherIcon: Int = R.drawable.cloudy_2
            override val temperature: String = "12°C"
        },
        object : HourlyForecastCardItemData {
            override val timeLabel: String = "3 AM"
            override val weatherIcon: Int = R.drawable.cloudy_2
            override val temperature: String = "13°C"
        },
        object : HourlyForecastCardItemData {
            override val timeLabel: String = "6 AM"
            override val weatherIcon: Int = R.drawable.cloudy_1
            override val temperature: String = "15°C"
        },
        object : HourlyForecastCardItemData {
            override val timeLabel: String = "9 AM"
            override val weatherIcon: Int = R.drawable.fair
            override val temperature: String = "17°C"
        },
        object : HourlyForecastCardItemData {
            override val timeLabel: String = "12 PM"
            override val weatherIcon: Int = R.drawable.clear
            override val temperature: String = "21°C"
        },
        object : HourlyForecastCardItemData {
            override val timeLabel: String = "3 PM"
            override val weatherIcon: Int = R.drawable.fair
            override val temperature: String = "19°C"
        },
        object : HourlyForecastCardItemData {
            override val timeLabel: String = "6 PM"
            override val weatherIcon: Int = R.drawable.clear
            override val temperature: String = "18°C"
        },
        object : HourlyForecastCardItemData {
            override val timeLabel: String = "9 PM"
            override val weatherIcon: Int = R.drawable.cloudy_1
            override val temperature: String = "15°C"
        },
    )
    
    HourlyForecastCard(
        items = items,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}