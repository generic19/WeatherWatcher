package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api

import com.basilalasadi.iti.kotlin.weatherwatcher.BuildConfig
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.network.QueryAppender
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto.AirPollutionData
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto.AirPollutionDataPoint
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto.WeatherData
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.dto.WeatherDataPoint
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") languageCode: String,
    ): Response<WeatherDataPoint>

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") languageCode: String,
    ): Response<WeatherData>

    @GET("air_pollution")
    suspend fun getCurrentAirPollution(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ) : Response<AirPollutionData>

    companion object {
        fun create(): WeatherApiService = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(QueryAppender("appid", BuildConfig.OPEN_WEATHER_API_KEY))
                    .build()
            )
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}

