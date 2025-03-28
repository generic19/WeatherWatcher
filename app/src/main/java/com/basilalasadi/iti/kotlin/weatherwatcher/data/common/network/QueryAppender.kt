package com.basilalasadi.iti.kotlin.weatherwatcher.data.common.network

import okhttp3.Interceptor
import okhttp3.Response

class QueryAppender(private val key: String, private val value: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .url(
                chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter(key, value)
                    .build()
            )
            .build()

        return chain.proceed(request)
    }
}