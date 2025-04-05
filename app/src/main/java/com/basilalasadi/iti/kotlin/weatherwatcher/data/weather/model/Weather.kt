package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import java.time.LocalTime

/**
 * @property temperature Kelvin
 * @property pressure hPa
 * @property precipitation mm/h, ratio
 * @property wind m/s, degrees
 * @property airPollution μg/m^3
 * @property cloudiness ratio
 * @property humidity ratio
 * @property visibility meters
 */
data class Weather(
    val temperature: Temperature,
    val condition: Condition,
    val pressure: Pressure,
    val precipitation: Precipitation,
    val wind: Wind,
    val airPollution: AirPollution?,
    val cloudiness: Double,
    val humidity: Double,
    val visibility: Double,
    val sunrise: LocalTime?,
    val sunset: LocalTime?,
) {
    data class Temperature(
        val current: Double,
        val feelsLike: Double,
        val min: Double,
        val max: Double,
    )

    data class Pressure(
        val seaLevel: Double,
        val groundLevel: Double,
    )

    data class Precipitation(
        val rain: Double,
        val snow: Double,
        val probability: Double?,
    )

    data class Wind(
        val speed: Double,
        val direction: Double,
        val gust: Double,
    )

    data class AirPollution(
        val airQualityIndex: AirQualityIndex,
        val carbonMonoxide: Double,
        val nitrogenMonoxide: Double,
        val nitrogenDioxide: Double,
        val ozone: Double,
        val sulfurDioxide: Double,
        val fineParticleMatter: Double,
        val coarseParticleMatter: Double,
        val ammonia: Double,
    ) {
        enum class AirQualityIndex(val index: Int, @StringRes val title: Int) {
            Good(1, R.string.rating_good),
            Fair(2, R.string.rating_fair),
            Moderate(3, R.string.rating_moderate),
            Poor(4, R.string.rating_poor),
            VeryPoor(5, R.string.rating_very_poor);

            companion object {
                private val indexMap by lazy {
                    val map = mutableMapOf<Int, AirQualityIndex>()
                    entries.forEach { map.put(it.index, it) }
                    map as Map<Int, AirQualityIndex>
                }

                fun byIndex(index: Int): AirQualityIndex = indexMap.getValue(index)
            }
        }
    }
    
    enum class Condition(
        val id: Int,
        val group: Group,
        @StringRes val title: Int,
        @StringRes val description: Int,
        @DrawableRes val icon: Int,
        @DrawableRes val nightIcon: Int,
    ) {
        ThunderstormWithLightRain   (id = 200, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_light_rain,    icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        ThunderstormWithRain        (id = 201, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_rain,          icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        ThunderstormWithHeavyRain   (id = 202, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_heavy_rain,    icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        LightThunderstorm           (id = 210, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_light_thunderstorm,              icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        Thunderstorm                (id = 211, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm,                    icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        HeavyThunderstorm           (id = 212, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_heavy_thunderstorm,              icon = R.drawable.severe_thunderstorm, nightIcon = R.drawable.severe_thunderstorm),
        RaggedThunderstorm          (id = 221, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_ragged_thunderstorm,             icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        ThunderstormWithLightDrizzle(id = 230, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_light_drizzle, icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        ThunderstormWithDrizzle     (id = 231, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_drizzle,       icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        ThunderstormWithHeavyDrizzle(id = 232, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_heavy_drizzle, icon = R.drawable.thunder,             nightIcon = R.drawable.thunder),
        LightIntensityDrizzle       (id = 300, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_light_intensity_drizzle,         icon =  R.drawable.rainy_7,            nightIcon = R.drawable.rainy_7),
        Drizzle                     (id = 301, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_drizzle,                         icon =  R.drawable.rainy_7,            nightIcon = R.drawable.rainy_7),
        HeavyIntensityDrizzle       (id = 302, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_heavy_intensity_drizzle,         icon =  R.drawable.rainy_7,            nightIcon = R.drawable.rainy_7),
        LightIntensityDrizzleRain   (id = 310, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_light_intensity_drizzle_rain,    icon =  R.drawable.rainy_4,            nightIcon = R.drawable.rainy_4),
        DrizzleRain                 (id = 311, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_drizzle_rain,                    icon =  R.drawable.rainy_5,            nightIcon = R.drawable.rainy_5),
        HeavyIntensityDrizzleRain   (id = 312, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_heavy_intensity_drizzle_rain,    icon =  R.drawable.rainy_6,            nightIcon = R.drawable.rainy_6),
        ShowerRainAndDrizzle        (id = 313, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_shower_rain_and_drizzle,         icon =  R.drawable.rainy_6,            nightIcon = R.drawable.rainy_6),
        HeavyShowerRainAndDrizzle   (id = 314, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_heavy_shower_rain_and_drizzle,   icon =  R.drawable.rainy_6,            nightIcon = R.drawable.rainy_6),
        ShowerDrizzle               (id = 321, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_shower_drizzle,                  icon =  R.drawable.rainy_6,            nightIcon = R.drawable.rainy_6),
        LightRain                   (id = 500, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_light_rain,                      icon = R.drawable.rainy_4,             nightIcon = R.drawable.rainy_4),
        ModerateRain                (id = 501, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_moderate_rain,                   icon = R.drawable.rainy_5,             nightIcon = R.drawable.rainy_5),
        HeavyIntensityRain          (id = 502, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_heavy_intensity_rain,            icon = R.drawable.rainy_6,             nightIcon = R.drawable.rainy_6),
        VeryHeavyRain               (id = 503, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_very_heavy_rain,                 icon = R.drawable.rainy_6,             nightIcon = R.drawable.rainy_6),
        ExtremeRain                 (id = 504, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_extreme_rain,                    icon = R.drawable.rainy_6,             nightIcon = R.drawable.rainy_6),
        FreezingRain                (id = 511, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_freezing_rain,                   icon = R.drawable.snow_and_sleet_mix,  nightIcon = R.drawable.snow_and_sleet_mix),
        LightIntensityShowerRain    (id = 520, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_light_intensity_shower_rain,     icon =  R.drawable.rainy_6,            nightIcon = R.drawable.rainy_6),
        ShowerRain                  (id = 521, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_shower_rain,                     icon =  R.drawable.rainy_6,            nightIcon = R.drawable.rainy_6),
        HeavyIntensityShowerRain    (id = 522, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_heavy_intensity_shower_rain,     icon =  R.drawable.rainy_6,            nightIcon = R.drawable.rainy_6),
        RaggedShowerRain            (id = 531, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_ragged_shower_rain,              icon =  R.drawable.rainy_1,            nightIcon = R.drawable.rainy_1_night),
        LightSnow                   (id = 600, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_light_snow,                      icon = R.drawable.snowy_4,             nightIcon = R.drawable.snowy_4),
        Snow                        (id = 601, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_snow,                            icon = R.drawable.snowy_5,             nightIcon = R.drawable.snowy_5),
        HeavySnow                   (id = 602, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_heavy_snow,                      icon = R.drawable.snowy_6,             nightIcon = R.drawable.snowy_6),
        Sleet                       (id = 611, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_sleet,                           icon = R.drawable.sleet,               nightIcon = R.drawable.sleet),
        LightShowerSleet            (id = 612, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_light_shower_sleet,              icon = R.drawable.sleet,               nightIcon = R.drawable.sleet),
        ShowerSleet                 (id = 613, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_shower_sleet,                    icon = R.drawable.snow_and_sleet_mix,  nightIcon = R.drawable.snow_and_sleet_mix),
        LightRainAndSnow            (id = 615, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_light_rain_and_snow,             icon = R.drawable.snow_and_sleet_mix,  nightIcon = R.drawable.snow_and_sleet_mix),
        RainAndSnow                 (id = 616, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_rain_and_snow,                   icon = R.drawable.snow_and_sleet_mix,  nightIcon = R.drawable.snow_and_sleet_mix),
        LightShowerSnow             (id = 620, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_light_shower_snow,               icon = R.drawable.snowy_4,             nightIcon = R.drawable.snowy_4),
        ShowerSnow                  (id = 621, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_shower_snow,                     icon = R.drawable.snowy_5,             nightIcon = R.drawable.snowy_5),
        HeavyShowerSnow             (id = 622, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_heavy_shower_snow,               icon = R.drawable.snowy_6,             nightIcon = R.drawable.snowy_6),
        Mist                        (id = 701, group = Group.Atmosphere,   title = R.string.cond_mist,         description = R.string.desc_mist,                            icon = R.drawable.fog,                 nightIcon = R.drawable.fog),
        Smoke                       (id = 711, group = Group.Atmosphere,   title = R.string.cond_smoke,        description = R.string.desc_smoke,                           icon = R.drawable.fog,                 nightIcon = R.drawable.fog),
        Haze                        (id = 721, group = Group.Atmosphere,   title = R.string.cond_haze,         description = R.string.desc_haze,                            icon = R.drawable.fog,                 nightIcon = R.drawable.fog),
        SandDustWhirls              (id = 731, group = Group.Atmosphere,   title = R.string.cond_dust,         description = R.string.desc_sand_dust_whirls,                icon = R.drawable.wind,                nightIcon = R.drawable.wind),
        Fog                         (id = 741, group = Group.Atmosphere,   title = R.string.cond_fog,          description = R.string.desc_fog,                             icon = R.drawable.fog,                 nightIcon = R.drawable.fog),
        Sand                        (id = 751, group = Group.Atmosphere,   title = R.string.cond_sand,         description = R.string.desc_sand,                            icon = R.drawable.haze,                nightIcon = R.drawable.haze),
        Dust                        (id = 761, group = Group.Atmosphere,   title = R.string.cond_dust,         description = R.string.desc_dust,                            icon = R.drawable.haze,                nightIcon = R.drawable.haze),
        VolcanicAsh                 (id = 762, group = Group.Atmosphere,   title = R.string.cond_ash,          description = R.string.desc_volcanic_ash,                    icon = R.drawable.fog,                 nightIcon = R.drawable.fog),
        Squalls                     (id = 771, group = Group.Atmosphere,   title = R.string.cond_squall,       description = R.string.desc_squalls,                         icon = R.drawable.tropical_storm,      nightIcon = R.drawable.tropical_storm),
        Tornado                     (id = 781, group = Group.Atmosphere,   title = R.string.cond_tornado,      description = R.string.desc_tornado,                         icon = R.drawable.tornado,             nightIcon = R.drawable.tornado),
        ClearSky                    (id = 800, group = Group.Clear,        title = R.string.cond_clear,        description = R.string.desc_clear_sky,                       icon =  R.drawable.clear,              nightIcon = R.drawable.clear_night),
        FewClouds                   (id = 801, group = Group.Clouds,       title = R.string.cond_clouds,       description = R.string.desc_few_clouds_11_25,                icon =  R.drawable.fair,               nightIcon = R.drawable.fair_night),
        ScatteredClouds             (id = 802, group = Group.Clouds,       title = R.string.cond_clouds,       description = R.string.desc_scattered_clouds_25_50,          icon =  R.drawable.cloudy_1,           nightIcon = R.drawable.cloudy_1_night),
        BrokenClouds                (id = 803, group = Group.Clouds,       title = R.string.cond_clouds,       description = R.string.desc_broken_clouds_51_84,             icon =  R.drawable.cloudy_2,           nightIcon = R.drawable.cloudy_2_night),
        OvercastClouds              (id = 804, group = Group.Clouds,       title = R.string.cond_clouds,       description = R.string.desc_overcast_clouds_85_100,          icon =  R.drawable.cloudy_original,    nightIcon = R.drawable.cloudy_original);

        enum class Group {
            Thunderstorm,
            Drizzle,
            Rain,
            Snow,
            Atmosphere,
            Clear,
            Clouds,
        }

        companion object {
            private val idMap by lazy {
                val map = mutableMapOf<Int, Condition>()
                Condition.entries.forEach { map.put(it.id, it) }
                map as Map<Int, Condition>
            }

            fun byId(id: Int): Condition = idMap.getValue(id)
        }
    }
}
