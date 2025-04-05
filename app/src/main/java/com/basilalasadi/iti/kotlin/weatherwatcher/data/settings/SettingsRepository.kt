package com.basilalasadi.iti.kotlin.weatherwatcher.data.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Settings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

class SettingsRepository(private val sharedPreferences: SharedPreferences) {
    val settingsFlow = callbackFlow<Pair<Settings, Setting>> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            key?.let {
                val settings = Settings.entries.firstOrNull { it.key == key }
                
                val setting = settings?.let { getSetting(sharedPreferences, it) }
                
                if (settings != null && setting != null) {
                    trySend(settings to setting)
                }
            }
        }
        
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    
    fun get(settings: Settings): Setting = getSetting(sharedPreferences, settings)
    fun get(): Map<Settings, Setting> = getSettings(sharedPreferences)
    fun put(setting: Setting) = putSetting(sharedPreferences, setting)
    
    companion object {
        private fun getSetting(sharedPreferences: SharedPreferences, settings: Settings): Setting = with(sharedPreferences) {
            when (settings) {
                Settings.Language -> Setting.Language.entries[getInt(settings.key, 0)]
                Settings.TemperatureUnit -> Setting.TemperatureUnit.entries[getInt(settings.key, 0)]
                Settings.SpeedUnit -> Setting.SpeedUnit.entries[getInt(settings.key, 0)]
                Settings.DistanceUnit -> Setting.DistanceUnit.entries[getInt(settings.key, 0)]
                Settings.PressureUnit -> Setting.PressureUnit.entries[getInt(settings.key, 0)]
                Settings.Location -> Setting.Location.entries[getInt(settings.key, 0)]
                
                Settings.ManualLocation -> Setting.ManualLocation(
                    getFloat("${Settings.ManualLocation.key}:latitude", 0f),
                    getFloat("${Settings.ManualLocation.key}:longitude", 0f),
                )
            }
        }
        
        private fun getSettings(sharedPreferences: SharedPreferences): Map<Settings, Setting> = with(sharedPreferences) {
            mapOf(
                Settings.Language to Setting.Language.entries[getInt(Settings.Language.key, 0)],
                Settings.TemperatureUnit to Setting.TemperatureUnit.entries[getInt(Settings.TemperatureUnit.key, 0)],
                Settings.SpeedUnit to Setting.SpeedUnit.entries[getInt(Settings.SpeedUnit.key, 0)],
                Settings.DistanceUnit to Setting.DistanceUnit.entries[getInt(Settings.DistanceUnit.key, 0)],
                Settings.PressureUnit to Setting.PressureUnit.entries[getInt(Settings.PressureUnit.key, 0)],
                Settings.Location to Setting.Location.entries[getInt(Settings.Location.key, 0)],
                
                Settings.ManualLocation to Setting.ManualLocation(
                    getFloat("${Settings.ManualLocation.key}:latitude", 0f),
                    getFloat("${Settings.ManualLocation.key}:longitude", 0f),
                )
            )
        }
        
        private fun putSetting(sharedPreferences: SharedPreferences, setting: Setting) {
            with(sharedPreferences) {
                when (setting) {
                    is Setting.Language -> edit { putInt(Settings.Language.key, setting.ordinal) }
                    is Setting.TemperatureUnit -> edit { putInt(Settings.TemperatureUnit.key, setting.ordinal) }
                    is Setting.SpeedUnit -> edit { putInt(Settings.SpeedUnit.key, setting.ordinal) }
                    is Setting.DistanceUnit -> edit { putInt(Settings.DistanceUnit.key, setting.ordinal) }
                    is Setting.PressureUnit -> edit { putInt(Settings.PressureUnit.key, setting.ordinal) }
                    is Setting.Location -> edit { putInt(Settings.Location.key, setting.ordinal) }
                    
                    is Setting.ManualLocation -> edit {
                        putFloat("${Settings.ManualLocation.key}:latitude", setting.latitude)
                        putFloat("${Settings.ManualLocation.key}:longitude", setting.longitude)
                    }
                }
            }
        }
    }
}