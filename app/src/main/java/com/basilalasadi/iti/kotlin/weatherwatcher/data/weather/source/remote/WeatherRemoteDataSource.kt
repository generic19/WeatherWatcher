package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote

import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.WeatherException
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Dated
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.WeatherApiService
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto.toDatedModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.IOException

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(city: City): Dated<Weather>
    suspend fun getWeatherForecast(city: City): List<Dated<Weather>>
}

class WeatherRemoteDataSourceImpl(private val weatherApiService: WeatherApiService) : WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(city: City): Dated<Weather> = coroutineScope {
        val currentWeatherDeferred = async {
            weatherApiService.getCurrentWeather(
                latitude = city.coordinates.latitude,
                longitude = city.coordinates.longitude,
            )
        }

        val airPollutionDeferred = async {
            weatherApiService.getCurrentAirPollution(
                latitude = city.coordinates.latitude,
                longitude = city.coordinates.longitude,
            )
        }

        try {
            val currentWeatherResponse = currentWeatherDeferred.await()
            val airPollutionResponse = airPollutionDeferred.await()

            if (currentWeatherResponse.isSuccessful) {
                val currentWeather = currentWeatherResponse.body()!!

                return@coroutineScope currentWeather.toDatedModel(
                    timezone = currentWeather.timezone!!,
                    sunrise = currentWeather.system.sunriseUtcTimestamp!!,
                    sunset = currentWeather.system.sunsetUtcTimestamp!!,
                    airPollutionDataPoint = airPollutionResponse.body()?.dataPoints?.getOrNull(0),
                )
            } else {
                throw WeatherException("API responded with ${currentWeatherResponse.message()}.")
            }
        } catch (ex: IOException) {
            throw WeatherException("Could not reach weather API.", ex)
        }
    }

    override suspend fun getWeatherForecast(city: City): List<Dated<Weather>> {
        val response = try {
            weatherApiService.getWeatherForecast(
                latitude = city.coordinates.latitude,
                longitude = city.coordinates.longitude,
            )
        } catch (ex: IOException) {
            throw WeatherException("Could not reach weather API.", ex)
        }

        if (response.isSuccessful) {
            val weatherData = response.body()!!

            return weatherData.dataPoints.map {
                it.toDatedModel(
                    timezone = weatherData.city.timezone,
                    sunrise = weatherData.city.sunrise,
                    sunset = weatherData.city.sunset,
                    airPollutionDataPoint = null,
                )
            }
        } else {
            throw WeatherException("Weather API responded with ${response.message()}.")
        }
    }
}
