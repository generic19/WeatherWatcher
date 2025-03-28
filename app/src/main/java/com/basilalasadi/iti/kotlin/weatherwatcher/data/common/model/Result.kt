package com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model

import java.lang.Exception

sealed class Result<out V> {
    abstract val value: V?

    data class Initial<out V>(override val value: V? = null) : Result<V>()

    data class Loading<out V>(override val value: V? = null) : Result<V>()

    data class Success<out V>(override val value: V) : Result<V>()

    data class Failure<out V, out E: Exception>(val error: E, override val value: V? = null) : Result<V>()
}
