package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.api

import com.basilalasadi.iti.kotlin.weatherwatcher.BuildConfig
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.network.QueryAppender
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CityApiService {

    @GET("direct")
    suspend fun findCityByName(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
    ): Response<List<GeocodingResult>>

    @GET("reverse")
    suspend fun findCityByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int = 1,
    ): Response<List<GeocodingResult>>

    companion object {
        fun create(): CityApiService = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(QueryAppender("appid", BuildConfig.OPEN_WEATHER_API_KEY))
                    .build()
            )
            .baseUrl("http://api.openweathermap.org/geo/1.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CityApiService::class.java)
    }
}
