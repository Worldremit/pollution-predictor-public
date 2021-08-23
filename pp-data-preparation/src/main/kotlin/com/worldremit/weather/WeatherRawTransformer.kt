package com.worldremit.weather

import com.worldremit.avro.*
import com.worldremit.avro.WeatherFeature.*
import com.worldremit.util.Toggle
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(value = [Toggle.WEATHER])
class WeatherRawTransformer {

    fun toWeatherRecord(raw: WeatherRaw) = WeatherRecord().apply {
        weatherId = raw.weatherId
        locationTime = raw.locationTime
        windDirection = toWindDirection(raw.data.windDirection, raw.data.windSpeed)
        features = raw.data.toFeatures()
            .filterValues { it != null && it != 0.0 }
            .filter { entry -> WEATHER_SPECIFIC_RULES[entry.key]?.invoke(entry.value!!) ?: true }
            .toMutableMap()
            .also {
                raw.data.precipitationType?.let { type ->
                    extendPrecipitation(type, raw.data.precipitationIntensity).let { precipitation ->
                        it[RAIN_INTENSITY.name] = precipitation.rain
                        it[SNOW_INTENSITY.name] = precipitation.snow
                    }
                }
            }
    }

    private fun toPrecipitationType(precipitationText: String?): PrecipitationType = when (precipitationText) {
        null -> PrecipitationType.NONE
        else -> try {
            PrecipitationType.valueOf(precipitationText.uppercase())
        } catch (e: IllegalArgumentException) {
            PrecipitationType.UNKNOWN
        }
    }

    private fun extendPrecipitation(precipitationText: String?, precipitationIntensity: Double): RainSnowIntensity =
        if (precipitationIntensity == 0.0) {
            RainSnowIntensity(0.0, 0.0)
        } else {
            when (toPrecipitationType(precipitationText)) {
                PrecipitationType.RAIN -> RainSnowIntensity(precipitationIntensity, 0.0)
                PrecipitationType.SNOW -> RainSnowIntensity(0.0, precipitationIntensity)
                else -> RainSnowIntensity(0.0, 0.0)
            }
        }

    private fun toWindDirection(angle: Int?, windSpeed: Double?): WindDirection = when {
        angle == null || windSpeed == null || windSpeed == 0.0 -> WindDirection.NONE
        angle < 0.0 || angle >= 360 || windSpeed < 0 -> WindDirection.UNKNOWN
        else -> try {
            WindDirection.values()[angle / 45]
        } catch (e: IndexOutOfBoundsException) {
            WindDirection.UNKNOWN
        }
    }

    private data class RainSnowIntensity(val rain: Double, val snow: Double)

    companion object {
        private val WEATHER_SPECIFIC_RULES: Map<String, (Double) -> Boolean> = mapOf<WeatherFeature, (Double) -> Boolean>(
            TEMPERATURE to { it in -100.0..100.0 },
            TEMPERATURE_APPARENT to { it in -100.0..100.0 },
            HUMIDITY to { it in 0.0..1.0 },
            PRESSURE to { it in 300.0..1100.0 },
            WIND_SPEED to { it in 0.0..400.0 },
            WIND_GUST to { it in 0.0..400.0 },
            CLOUD_COVER to { it in 0.0..1.0 },
            PRECIPITATION_PROBABILITY to { it in 0.0..1.0 },
            PRECIPITATION_INTENSITY to { it in 0.0..100.0 },
            VISIBILITY to { it >= 0.0 },
            OZONE to { it >= 0.0 },
            SOLAR_DHI to { it >= 0.0 },
            SOLAR_ETR to { it >= 0.0 },
            UV_INDEX to { it >= 0.0 },
            SOLAR_AZIMUTH to { it in 0.0..360.0 },
            SOLAR_ALTITUDE to { it >= 0.0 },
            SOLAR_DNI to { it >= 0.0 },
            SOLAR_GHI to { it >= 0.0 }
        ).mapKeys { it.key.name }
    }
}