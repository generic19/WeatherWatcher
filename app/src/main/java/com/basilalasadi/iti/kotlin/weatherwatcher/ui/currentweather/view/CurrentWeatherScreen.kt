package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.celsius
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.*

@OptIn(ExperimentalLayoutApi::class)
@Preview(showSystemUi = true)
@Composable
fun CurrentWeatherScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
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
            modifier = Modifier
                .padding(vertical = 32.dp)
        )
        
        HourlyForecastCard(
            items = listOf(
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
            ),
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        )
        
        DailyForecastCard(
            data = DailyForecastCardData.preview,
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        ) {
            CloudsCard(
                data = object : CloudsCardData {
                    override val coverage: String = "24%"
                    override val coverageDescription: Int = R.string.few_clouds
                },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )
            PrecipitationCard(
                data = PrecipitationCardData.preview,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )
        }
        
        WindCard(
            data = WindCardData.preview,
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
                .aspectRatio(2f)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        ) {
            HumidityCard(
                humidity = "55%",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )
            PressureCard(
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )
        }
        
        TemperatureMapCard(
            mapUrl = CityRepository.getTemperatureMapTileUrl(
                coordinates = City.Coordinates(31.0, 30.0),
                zoomLevel = 3
            ),
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        )
    }
}

