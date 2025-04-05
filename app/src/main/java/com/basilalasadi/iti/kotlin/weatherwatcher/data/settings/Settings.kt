package com.basilalasadi.iti.kotlin.weatherwatcher.data.settings

import androidx.annotation.StringRes
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries

enum class Settings(val key: String, @get:StringRes val title: Int) {
    Language("Language", R.string.title_language),
    TemperatureUnit("TemperatureUnit", R.string.title_temperature_unit),
    SpeedUnit("SpeedUnit", R.string.title_speed_unit),
    DistanceUnit("DistanceUnit", R.string.title_distance_unit),
    PressureUnit("PressureUnit", R.string.title_pressure_unit),
    Location("Location", R.string.title_location),
    ManualLocation("ManualLocation", R.string.title_manual_location);
}

interface Setting {
    @get:StringRes val label: Int
    
    interface EnumSetting<T : Enum<T>> : Setting {
        val settingEntries: EnumEntries<T>
    }
    
    interface LocationSetting : Setting {
        val latitude: Float
        val longitude: Float
    }
    
    enum class Language(@StringRes override val label: Int) : EnumSetting<Language> {
        Default(R.string.system_default),
        English(R.string.english),
        Arabic(R.string.arabic);
        
        override val settingEntries: EnumEntries<Language> get() = enumEntries()
    }
    
    enum class TemperatureUnit(@StringRes override val label: Int) : EnumSetting<TemperatureUnit> {
        Celsius(R.string.celsius),
        Fahrenheit(R.string.fahrenheit),
        Kelvin(R.string.kelvin);
        
        override val settingEntries: EnumEntries<TemperatureUnit> get() = enumEntries()
    }
    
    enum class SpeedUnit(@StringRes override val label: Int) : EnumSetting<SpeedUnit> {
        MetersPerSecond(R.string.meters_per_second),
        KilometersPerHour(R.string.kilometers_per_hour),
        MilesPerHour(R.string.miles_per_hour);
        
        override val settingEntries: EnumEntries<SpeedUnit> get() = enumEntries()
    }
    
    enum class DistanceUnit(@StringRes override val label: Int) : EnumSetting<DistanceUnit> {
        Meters(R.string.meters),
        Kilometers(R.string.kilometers),
        Feet(R.string.feet),
        Miles(R.string.miles);
        
        override val settingEntries: EnumEntries<DistanceUnit> get() = enumEntries()
    }
    
    enum class PressureUnit(@StringRes override val label: Int) : EnumSetting<PressureUnit> {
        HectoPascal(R.string.hectopascal),
        InchesOfMurcury(R.string.inches_of_murcury),
        Bar(R.string.bar);
        
        override val settingEntries: EnumEntries<PressureUnit> get() = enumEntries()
    }
    
    enum class Location(@StringRes override val label: Int) : EnumSetting<Location> {
        Automatic(R.string.automatic),
        Manual(R.string.manual);
        
        override val settingEntries: EnumEntries<Location> get() = enumEntries()
    }
    
    data class ManualLocation(
        override val latitude: Float,
        override val longitude: Float,
    ) : LocationSetting {
        @StringRes override val label =
            if (latitude == 0f && longitude == 0f) {
                R.string.location_not_set
            } else {
                R.string.location_set
            }
    }
}
