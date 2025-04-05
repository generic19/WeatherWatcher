package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model

import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Distance.Foot
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Distance.Kilometer
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Distance.Meter
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Distance.Mile
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Pressure
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Pressure.Bar
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Pressure.HectoPascal
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Pressure.InchesOfMercury
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Speed.KilometersPerHour
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Speed.MetersPerSecond
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Speed.MilesPerHour
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Temperature.Celsius
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Temperature.Fahrenheit
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units.Temperature.Kelvin

val Double.kelvin: Kelvin get() = Kelvin(this)
val Double.celsius: Celsius get() = Celsius(this)
val Double.fahrenheit: Fahrenheit get() = Fahrenheit(this)
val Double.metersPerSecond: MetersPerSecond get() = MetersPerSecond(this)
val Double.kilometersPerHour: KilometersPerHour get() = KilometersPerHour(this)
val Double.milesPerHour: MilesPerHour get() = MilesPerHour(this)
val Double.hectoPascal: HectoPascal get() = HectoPascal(this)
val Double.inchesOfMercury: InchesOfMercury get() = InchesOfMercury(this)
val Double.bar: Bar get() = Bar(this)
val Double.meter: Meter get() = Meter(this)
val Double.foot: Foot get() = Foot(this)
val Double.kilometer: Kilometer get() = Kilometer(this)
val Double.mile: Mile get() = Mile(this)

sealed interface Units {
    val value: Double
    
    sealed interface Temperature : Units {
        fun toKelvin(): Double
        fun toCelsius(): Double
        fun toFahrenheit(): Double
        
        data class Kelvin(override val value: Double) : Temperature {
            override fun toKelvin(): Double = value
            override fun toCelsius(): Double = value - 273.15
            override fun toFahrenheit(): Double = value * 1.8 - 459.67
        }
        
        data class Celsius(override val value: Double) : Temperature {
            override fun toCelsius(): Double = value
            override fun toKelvin(): Double = value + 273.15
            override fun toFahrenheit(): Double = value * 1.8 + 32
        }
        
        data class Fahrenheit(override val value: Double) : Temperature {
            override fun toFahrenheit(): Double = value
            override fun toCelsius(): Double = (value - 32) / 1.8
            override fun toKelvin(): Double = (value + 459.67) / 1.8
        }
    }
    
    sealed interface Pressure : Units {
        fun toHectoPascal(): Double
        fun toInchesOfMercury(): Double
        fun toBar(): Double
        
        data class HectoPascal(override val value: Double) : Pressure {
            override fun toHectoPascal(): Double = value
            override fun toInchesOfMercury(): Double = value * 0.02953
            override fun toBar(): Double = value / 1000.0
        }
        
        data class InchesOfMercury(override val value: Double) : Pressure {
            override fun toInchesOfMercury(): Double = value
            override fun toHectoPascal(): Double = value / 0.02953
            override fun toBar(): Double = value * 0.0338639
        }
        
        data class Bar(override val value: Double): Pressure {
            override fun toInchesOfMercury(): Double = value * 29.53
            override fun toHectoPascal(): Double = value * 1000.0
            override fun toBar(): Double = value
        }
    }
    
    sealed interface Speed : Units {
        fun toMetersPerSecond(): Double
        fun toKilometersPerHour(): Double
        fun toMilesPerHour(): Double
        
        data class MetersPerSecond(override val value: Double) : Speed {
            override fun toMetersPerSecond(): Double = value
            override fun toKilometersPerHour(): Double = value / 1000.0
            override fun toMilesPerHour(): Double = value * 2.23694
        }
        
        data class KilometersPerHour(override val value: Double) : Speed {
            override fun toMetersPerSecond(): Double = value
            override fun toKilometersPerHour(): Double = value
            override fun toMilesPerHour(): Double = value * 2.23694
        }
        
        data class MilesPerHour(override val value: Double) : Speed {
            override fun toMilesPerHour(): Double = value
            override fun toKilometersPerHour(): Double = value * 1.60934
            override fun toMetersPerSecond(): Double = value / 2.23694
        }
    }
    
    sealed interface Distance : Units {
        fun toMeter(): Double
        fun toFoot(): Double
        fun toKilometer(): Double
        fun toMile(): Double
        
        data class Meter(override val value: Double) : Distance {
            override fun toMeter(): Double = value
            override fun toFoot(): Double = value * 3.28084
            override fun toKilometer(): Double = value / 1000.0
            override fun toMile(): Double = value / 1609.34
        }
        
        data class Foot(override val value: Double) : Distance {
            override fun toFoot(): Double = value
            override fun toMeter(): Double = value / 3.28084
            override fun toKilometer(): Double = toMeter() / 1000.0
            override fun toMile(): Double = toMeter() / 1609.34
        }
        
        data class Kilometer(override val value: Double) : Distance {
            override fun toKilometer(): Double = value
            override fun toMeter(): Double = value * 1000.0
            override fun toFoot(): Double = toMeter() * 3.28084
            override fun toMile(): Double = value / 1.60934
        }
        
        data class Mile(override val value: Double) : Distance {
            override fun toMile(): Double = value
            override fun toMeter(): Double = value * 1609.34
            override fun toFoot(): Double = toMeter() * 3.28084
            override fun toKilometer(): Double = value * 1.60934
        }
    }
}