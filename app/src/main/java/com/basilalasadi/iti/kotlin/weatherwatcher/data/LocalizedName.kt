package com.basilalasadi.iti.kotlin.weatherwatcher.data

data class LocalizedName(
    val arabic: String?,
    val english: String?,
) {
    fun get(locale: AppLocale): String = when (locale) {
        AppLocale.Arabic -> arabic ?: english ?: "لا يوجد"
        AppLocale.English -> english ?: arabic ?: "Not Available"
    }
}