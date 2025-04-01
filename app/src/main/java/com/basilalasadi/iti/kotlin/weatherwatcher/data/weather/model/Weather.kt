package com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.model

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
        enum class AirQualityIndex(val index: Int, val title: String) {
            Good(1, "Good"),
            Fair(2, "Fair"),
            Moderate(3, "Moderate"),
            Poor(4, "Poor"),
            VeryPoor(5, "Very Poor");

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
        val iconId: Int,
    ) {
        ThunderstormWithLightRain   (id = 200, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_light_rain,    iconId = 11),
        ThunderstormWithRain        (id = 201, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_rain,          iconId = 11),
        ThunderstormWithHeavyRain   (id = 202, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_heavy_rain,    iconId = 11),
        LightThunderstorm           (id = 210, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_light_thunderstorm,              iconId = 11),
        Thunderstorm                (id = 211, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm,                    iconId = 11),
        HeavyThunderstorm           (id = 212, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_heavy_thunderstorm,              iconId = 11),
        RaggedThunderstorm          (id = 221, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_ragged_thunderstorm,             iconId = 11),
        ThunderstormWithLightDrizzle(id = 230, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_light_drizzle, iconId = 11),
        ThunderstormWithDrizzle     (id = 231, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_drizzle,       iconId = 11),
        ThunderstormWithHeavyDrizzle(id = 232, group = Group.Thunderstorm, title = R.string.cond_thunderstorm, description = R.string.desc_thunderstorm_with_heavy_drizzle, iconId = 11),
        LightIntensityDrizzle       (id = 300, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_light_intensity_drizzle,         iconId =  9),
        Drizzle                     (id = 301, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_drizzle,                         iconId =  9),
        HeavyIntensityDrizzle       (id = 302, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_heavy_intensity_drizzle,         iconId =  9),
        LightIntensityDrizzleRain   (id = 310, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_light_intensity_drizzle_rain,    iconId =  9),
        DrizzleRain                 (id = 311, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_drizzle_rain,                    iconId =  9),
        HeavyIntensityDrizzleRain   (id = 312, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_heavy_intensity_drizzle_rain,    iconId =  9),
        ShowerRainAndDrizzle        (id = 313, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_shower_rain_and_drizzle,         iconId =  9),
        HeavyShowerRainAndDrizzle   (id = 314, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_heavy_shower_rain_and_drizzle,   iconId =  9),
        ShowerDrizzle               (id = 321, group = Group.Drizzle,      title = R.string.cond_drizzle,      description = R.string.desc_shower_drizzle,                  iconId =  9),
        LightRain                   (id = 500, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_light_rain,                      iconId = 10),
        ModerateRain                (id = 501, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_moderate_rain,                   iconId = 10),
        HeavyIntensityRain          (id = 502, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_heavy_intensity_rain,            iconId = 10),
        VeryHeavyRain               (id = 503, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_very_heavy_rain,                 iconId = 10),
        ExtremeRain                 (id = 504, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_extreme_rain,                    iconId = 10),
        FreezingRain                (id = 511, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_freezing_rain,                   iconId = 13),
        LightIntensityShowerRain    (id = 520, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_light_intensity_shower_rain,     iconId =  9),
        ShowerRain                  (id = 521, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_shower_rain,                     iconId =  9),
        HeavyIntensityShowerRain    (id = 522, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_heavy_intensity_shower_rain,     iconId =  9),
        RaggedShowerRain            (id = 531, group = Group.Rain,         title = R.string.cond_rain,         description = R.string.desc_ragged_shower_rain,              iconId =  9),
        LightSnow                   (id = 600, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_light_snow,                      iconId = 13),
        Snow                        (id = 601, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_snow,                            iconId = 13),
        HeavySnow                   (id = 602, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_heavy_snow,                      iconId = 13),
        Sleet                       (id = 611, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_sleet,                           iconId = 13),
        LightShowerSleet            (id = 612, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_light_shower_sleet,              iconId = 13),
        ShowerSleet                 (id = 613, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_shower_sleet,                    iconId = 13),
        LightRainAndSnow            (id = 615, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_light_rain_and_snow,             iconId = 13),
        RainAndSnow                 (id = 616, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_rain_and_snow,                   iconId = 13),
        LightShowerSnow             (id = 620, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_light_shower_snow,               iconId = 13),
        ShowerSnow                  (id = 621, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_shower_snow,                     iconId = 13),
        HeavyShowerSnow             (id = 622, group = Group.Snow,         title = R.string.cond_snow,         description = R.string.desc_heavy_shower_snow,               iconId = 13),
        Mist                        (id = 701, group = Group.Atmosphere,   title = R.string.cond_mist,         description = R.string.desc_mist,                            iconId = 50),
        Smoke                       (id = 711, group = Group.Atmosphere,   title = R.string.cond_smoke,        description = R.string.desc_smoke,                           iconId = 50),
        Haze                        (id = 721, group = Group.Atmosphere,   title = R.string.cond_haze,         description = R.string.desc_haze,                            iconId = 50),
        SandDustWhirls              (id = 731, group = Group.Atmosphere,   title = R.string.cond_dust,         description = R.string.desc_sand_dust_whirls,                iconId = 50),
        Fog                         (id = 741, group = Group.Atmosphere,   title = R.string.cond_fog,          description = R.string.desc_fog,                             iconId = 50),
        Sand                        (id = 751, group = Group.Atmosphere,   title = R.string.cond_sand,         description = R.string.desc_sand,                            iconId = 50),
        Dust                        (id = 761, group = Group.Atmosphere,   title = R.string.cond_dust,         description = R.string.desc_dust,                            iconId = 50),
        VolcanicAsh                 (id = 762, group = Group.Atmosphere,   title = R.string.cond_ash,          description = R.string.desc_volcanic_ash,                    iconId = 50),
        Squalls                     (id = 771, group = Group.Atmosphere,   title = R.string.cond_squall,       description = R.string.desc_squalls,                         iconId = 50),
        Tornado                     (id = 781, group = Group.Atmosphere,   title = R.string.cond_tornado,      description = R.string.desc_tornado,                         iconId = 50),
        ClearSky                    (id = 800, group = Group.Clear,        title = R.string.cond_clear,        description = R.string.desc_clear_sky,                       iconId =  1),
        FewClouds                   (id = 801, group = Group.Clouds,       title = R.string.cond_clouds,       description = R.string.desc_few_clouds_11_25,                iconId =  2),
        ScatteredClouds             (id = 802, group = Group.Clouds,       title = R.string.cond_clouds,       description = R.string.desc_scattered_clouds_25_50,          iconId =  3),
        BrokenClouds                (id = 803, group = Group.Clouds,       title = R.string.cond_clouds,       description = R.string.desc_broken_clouds_51_84,             iconId =  4),
        OvercastClouds              (id = 804, group = Group.Clouds,       title = R.string.cond_clouds,       description = R.string.desc_overcast_clouds_85_100,          iconId =  4);

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
