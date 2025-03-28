package com.basilalasadi.iti.kotlin.weatherwatcher.ui.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.core.theme.WeatherWatcherTheme
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.CurrentWeatherScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherWatcherTheme {
                CurrentWeatherScreen()
            }
        }
    }
}
