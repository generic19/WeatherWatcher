package com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.util.fastDistinctBy
import androidx.compose.ui.util.fastFilter
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.AppLocale
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Setting
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.Settings
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Dated
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Units
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.Weather
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.bar
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.celsius
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.fahrenheit
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.foot
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.hectoPascal
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.inchesOfMercury
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.kelvin
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.kilometer
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.kilometersPerHour
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.meter
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.metersPerSecond
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.mile
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model.milesPerHour
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.CurrentWeatherScreenData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.CloudsCardData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.CurrentWeatherDisplayData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.DailyForecastCardData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.HourlyForecastCardItemData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.PrecipitationCardData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.PressureCardData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.SunriseSunsetCardData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.VisibilityCardData
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.view.component.WindCardData
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.text.format

class CurrentWeatherViewModelMapper(city: City, settings: Map<Settings, Setting>) {
    private val mappingContext by lazy {
        val appLocale = when (settings[Settings.Language] as Setting.Language) {
            Setting.Language.Default -> if (Locale.getDefault().language == "ar") AppLocale.Arabic else AppLocale.English
            Setting.Language.English -> AppLocale.English
            Setting.Language.Arabic -> AppLocale.Arabic
        }
        
        val locale = when (appLocale) {
            AppLocale.Arabic -> Locale.forLanguageTag("ar")
            AppLocale.English -> Locale.forLanguageTag("en")
        }
        
        val temperaturePair: Pair<Int, (Double) -> Units.Temperature> =
            when (settings[Settings.TemperatureUnit] as Setting.TemperatureUnit) {
                Setting.TemperatureUnit.Celsius -> R.string.unit_celsius to { x -> x.kelvin.toCelsius().celsius }
                Setting.TemperatureUnit.Fahrenheit -> R.string.unit_fahrenheit to { x -> x.kelvin.toFahrenheit().fahrenheit }
                Setting.TemperatureUnit.Kelvin -> R.string.unit_kelvin to { x -> x.kelvin }
            }
        val (temperatureUnit, temperatureConverter) = temperaturePair
        
        val speedPair: Pair<Int, (Double) -> Units.Speed> =
            when (settings[Settings.SpeedUnit] as Setting.SpeedUnit) {
                Setting.SpeedUnit.MetersPerSecond -> R.string.unit_meters_per_second to { x -> x.metersPerSecond }
                Setting.SpeedUnit.KilometersPerHour -> R.string.unit_kilometers_per_hour to { x -> x.metersPerSecond.toKilometersPerHour().kilometersPerHour }
                Setting.SpeedUnit.MilesPerHour -> R.string.unit_miles_per_hour to { x -> x.metersPerSecond.toMilesPerHour().milesPerHour }
            }
        val (speedUnit, speedConverter) = speedPair
        
        val pressurePair: Pair<Int, (Double) -> Units.Pressure> =
            when (settings[Settings.PressureUnit] as Setting.PressureUnit) {
                Setting.PressureUnit.HectoPascal -> R.string.unit_hectopascal to { x -> x.hectoPascal }
                Setting.PressureUnit.InchesOfMurcury -> R.string.unit_inches_of_mucrury to { x -> x.hectoPascal.toInchesOfMercury().inchesOfMercury }
                Setting.PressureUnit.Bar -> R.string.unit_bar to { x -> x.hectoPascal.toBar().bar }
            }
        val (pressureUnit, pressureConverter) = pressurePair
        
        val distancePair: Pair<Int, (Double) -> Units.Distance> =
            when (settings[Settings.DistanceUnit] as Setting.DistanceUnit) {
                Setting.DistanceUnit.Meters -> R.string.unit_meter to { x -> x.meter }
                Setting.DistanceUnit.Kilometers -> R.string.unit_kilometer to { x -> x.meter.toKilometer().kilometer }
                Setting.DistanceUnit.Feet -> R.string.unit_foot to { x -> x.meter.toFoot().foot }
                Setting.DistanceUnit.Miles -> R.string.unit_mile to { x -> x.meter.toMile().mile }
            }
        val (distanceUnit, distanceConverter) = distancePair
        
        MappingContext(
            city = city,
            appLocale = appLocale,
            locale = locale,
            temperatureUnit = temperatureUnit,
            temperatureConverter = temperatureConverter,
            speedUnit = speedUnit,
            speedConverter = speedConverter,
            pressureUnit = pressureUnit,
            pressureConverter = pressureConverter,
            distanceUnit = distanceUnit,
            distanceConverter = distanceConverter,
        )
    }
    
    fun map(weather: Dated<Weather>?, forecast: List<Dated<Weather>>): CurrentWeatherScreenData {
        val now = ZonedDateTime.now()
        
        with(mappingContext) {
            val isNight = weather?.let { it.dateTime.hour !in 6..19 } == true
            
            val data = CurrentWeatherScreenData(
                weatherDisplay = weather?.let {
                    val (fakeMaxTemp, fakeMinTemp) = fakeMinMaxTemp(it.value.temperature.current, it.dateTime.toLocalTime())
                    
                    CurrentWeatherDisplayData(
                        cityName = city.name.get(appLocale),
                        currentTemperature = temperatureConverter(it.value.temperature.current).value,
                        feelsLikeTemperature = temperatureConverter(it.value.temperature.feelsLike).value,
                        temperatureUnit = temperatureUnit,
                        weatherIcon = if (isNight) {
                            it.value.condition.nightIcon
                        } else {
                            it.value.condition.icon
                        },
                        conditionTitle = it.value.condition.title,
                        lowTemperature = temperatureConverter(fakeMinTemp).value,
                        highTemperature = temperatureConverter(fakeMaxTemp).value,
                        dateTime = it.dateTime
                            .format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy h:mm a OOOO", locale)),
                    )
                },
                hourlyForecastItems = forecast
                    .filter { ChronoUnit.HOURS.between(now, it.dateTime) <= 24 }
                    .map {
                        val isNight = it.dateTime.hour !in 6..19
                        
                        HourlyForecastCardItemData(
                            timeLabel = it.dateTime
                                .format(DateTimeFormatter.ofPattern("h a", locale)),
                            weatherIcon = if (isNight) {
                                it.value.condition.nightIcon
                            } else {
                                it.value.condition.icon
                            },
                            temperature = temperatureConverter(it.value.temperature.current).value,
                            temperatureUnit = temperatureUnit,
                        )
                    },
                dailyForecast = forecast.let {
                    val minTemp = it.minOfOrNull { it.value.temperature.min } ?: -50.0
                    val maxTemp = it.maxOfOrNull { it.value.temperature.max } ?: 50.0
                    
                    val weatherDays = it
                        .fastFilter { ChronoUnit.DAYS.between(now, it.dateTime) <= 5 }
                        .fastDistinctBy { it.dateTime.toLocalDate().atStartOfDay() }
                    
                    DailyForecastCardData(
                        forecastMin = temperatureConverter(minTemp),
                        forecastMax = temperatureConverter(maxTemp),
                        forecastDays = weatherDays.map {
                            val (fakeMin, fakeMax) = fakeMinMaxTemp(it.value.temperature.current, it.dateTime.toLocalTime())
                            
                            val min = fakeMin.coerceIn(minTemp..maxTemp)
                            val max = fakeMax.coerceIn(minTemp..maxTemp)
                            
                            DailyForecastCardData.ForecastDay(
                                dayName = it.dateTime.format(DateTimeFormatter.ofPattern("EEEE", locale)),
                                minTemperature = temperatureConverter(min),
                                maxTemperature = temperatureConverter(max),
                                weatherIcon = it.value.condition.icon,
                            )
                        },
                        temperatureUnit = temperatureUnit,
                    )
                },
                coordinates = city.coordinates,
                clouds = weather?.value?.cloudiness?.let {
                    CloudsCardData(
                        coverage = "%.0f%%".format(it * 100),
                        coverageDescription = when ((it * 100).roundToInt()) {
                            in 11 until 25 -> R.string.desc_few_clouds_11_25
                            in 25..50 -> R.string.desc_scattered_clouds_25_50
                            in 51..84 -> R.string.desc_broken_clouds_51_84
                            in 85..100 -> R.string.desc_overcast_clouds_85_100
                            else -> R.string.desc_clear_sky
                        }
                    )
                },
                precipitation = weather?.value?.precipitation?.let {
                    if (it.snow > 0.0) {
                        PrecipitationCardData(
                            precipitation = "%.0f".format(it.snow),
                            precipitationType = R.string.cond_snow,
                            probability = it.probability?.let { "%.0f%%".format(it) }
                        )
                    } else {
                        PrecipitationCardData(
                            precipitation = "%.0f".format(it.rain),
                            precipitationType = R.string.cond_rain,
                            probability = it.probability?.let { "%.0f%%".format(it) }
                        )
                    }
                },
                wind = weather?.value?.wind?.let {
                    WindCardData(
                        windSpeed = speedConverter(it.speed),
                        windGust = speedConverter(it.gust),
                        speedUnit = speedUnit,
                        directionDegrees = it.direction,
                        generalDirection = getGeneralDirection(it.direction),
                    )
                },
                humidity = weather?.value?.humidity?.let { "%.0f%%".format(it * 100) },
                pressure = weather?.value?.pressure?.let {
                    PressureCardData(
                        seaLevelPressure = pressureConverter(it.seaLevel).value,
                        groundLevelPressure = pressureConverter(it.groundLevel).value,
                        pressureUnit = pressureUnit,
                    )
                },
                sunriseSunset = weather?.value?.sunrise?.let { sr ->
                    weather.value.sunset?.let { ss ->
                        SunriseSunsetCardData(
                            sunrise = sr.format(DateTimeFormatter.ofPattern("h:mm a", locale)),
                            sunset = ss.format(DateTimeFormatter.ofPattern("h:mm a", locale)),
                        )
                    }
                },
                visibility = weather?.value?.visibility?.let {
                    VisibilityCardData(
                        visibility = "%.0f".format(distanceConverter(it).value),
                        visibilityUnit = distanceUnit
                    )
                },
                airPollution = weather?.value?.airPollution,
                localTime = weather?.dateTime?.toLocalTime(),
                isNight = isNight,
            )
            
            return data
        }
    }
    
    companion object {
        fun fakeMinMaxTemp(currentTemp: Double, time: LocalTime): Pair<Double, Double> {
            val timeHours = time.toSecondOfDay() / 60 / 60
            
            val min = currentTemp - 5.0 * (1 + cos(PI * (timeHours - 15.0) / (15.0 - 6.0)))
            val max = currentTemp + 5.0 * (1 + cos(PI * (timeHours - 15.0) / (15.0 - 6.0)))
            
            return min to max
        }
        
        fun getGeneralDirection(bearing: Double): Int {
            val direction = ((bearing + 22.5) % 360 / 45).toInt()
            
            return when (direction) {
                0 -> R.string.direction_north
                1 -> R.string.direction_north_east
                2 -> R.string.direction_east
                3 -> R.string.direction_south_east
                4 -> R.string.direction_south
                5 -> R.string.direction_south_west
                6 -> R.string.direction_west
                7 -> R.string.direction_north_west
                
                else -> throw Exception("Incorrect math")
            }
        }
    }
    
    private data class MappingContext(
        val city: City,
        val appLocale: AppLocale,
        val locale: Locale,
        @StringRes val temperatureUnit: Int,
        val temperatureConverter: (Double) -> Units.Temperature,
        @StringRes val speedUnit: Int,
        val speedConverter: (Double) -> Units.Speed,
        @StringRes val pressureUnit: Int,
        val pressureConverter: (Double) -> Units.Pressure,
        @StringRes val distanceUnit: Int,
        val distanceConverter: (Double) -> Units.Distance,
    )
}