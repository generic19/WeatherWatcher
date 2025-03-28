package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote

import com.basilalasadi.iti.kotlin.weatherwatcher.data.location.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.WeatherException
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.WeatherApiService
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto.AirPollutionDataPoint
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto.WeatherDataPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(city: City, locale: AppLocale): Weather
    suspend fun getWeatherForecast(city: City, locale: AppLocale): List<Weather>
}

class WeatherRemoteDataSourceImpl(private val weatherApiService: WeatherApiService) : WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(city: City, locale: AppLocale): Weather = coroutineScope {
        val currentWeatherDeferred = async {
            weatherApiService.getCurrentWeather(
                latitude = city.coordinates.latitude,
                longitude = city.coordinates.longitude,
                languageCode = locale.languageCode,
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

                return@coroutineScope weatherFromDtos(
                    weatherDataPoint = currentWeather,
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

    override suspend fun getWeatherForecast(
        city: City,
        locale: AppLocale,
    ): List<Weather> {
        val response = try {
            weatherApiService.getWeatherForecast(
                latitude = city.coordinates.latitude,
                longitude = city.coordinates.longitude,
                languageCode = locale.languageCode,
            )
        } catch (ex: IOException) {
            throw WeatherException("Could not reach weather API.", ex)
        }

        if (response.isSuccessful) {
            val weatherData = response.body()!!

            return weatherData.dataPoints.map {
                weatherFromDtos(
                    weatherDataPoint = it,
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

private fun weatherFromDtos(
    weatherDataPoint: WeatherDataPoint,
    timezone: Int,
    sunrise: Long,
    sunset: Long,
    airPollutionDataPoint: AirPollutionDataPoint?,
): Weather {
    return weatherDataPoint.run {
        Weather(
            temperature = Weather.Temperature(
                currentTemperature = mainWeather.temperature,
                feelsLikeTemperature = mainWeather.feelsLikeTemperature,
                minTemperature = mainWeather.minTemperature,
                maxTemperature = mainWeather.maxTemperature,
            ),
            condition = Weather.Condition.byId(weatherConditions[0].id),
            pressure = Weather.Pressure(
                seaLevel = mainWeather.seaLevelPressure,
                groundLevel = mainWeather.groundLevelPressure,
            ),
            precipitation = Weather.Precipitation(
                rain = rainPrecipitation?.perHour ?: 0.0,
                snow = snowPrecipitation?.perHour ?: 0.0,
                probability = probabilityOfRain,
            ),
            wind = Weather.Wind(
                speed = windStatus.speed,
                direction = windStatus.direction.toDouble(),
                gust = windStatus.gust,
            ),
            airPollution = airPollutionDataPoint?.run {
                Weather.AirPollution(
                    airQualityIndex = Weather.AirPollution.AirQualityIndex.byIndex(mainAirPollution.airQualityIndex),
                    carbonMonoxide = components.carbonMonoxide,
                    nitrogenMonoxide = components.nitrogenMonoxide,
                    nitrogenDioxide = components.nitrogenDioxide,
                    ozone = components.ozone,
                    sulfurDioxide = components.sulfurDioxide,
                    fineParticleMatter = components.fineParticleMatter,
                    coarseParticleMatter = components.coarseParticleMatter,
                    ammonia = components.ammonia,
                )
            },
            cloudiness = clouds.cloudinessPercent / 100.0,
            humidity = mainWeather.humidity / 100.0,
            visibility = visibility.toDouble(),
            sunrise = Instant.ofEpochSecond(sunrise).atOffset(ZoneOffset.ofTotalSeconds(timezone)).toLocalTime(),
            sunset = Instant.ofEpochSecond(sunset).atOffset(ZoneOffset.ofTotalSeconds(timezone)).toLocalTime(),
        )
    }
}