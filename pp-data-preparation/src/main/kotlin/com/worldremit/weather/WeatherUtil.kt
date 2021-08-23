package com.worldremit.weather

import com.worldremit.avro.WeatherFeature.*
import com.worldremit.avro.WeatherRawData

fun WeatherRawData.toFeatures(): Map<String, Double?> = mapOf(
    TEMPERATURE to temperature,
    TEMPERATURE_APPARENT to temperatureApparent,
    DEW_POINT to dewPoint,
    HUMIDITY to humidity,
    PRESSURE to pressure,
    WIND_SPEED to windSpeed,
    WIND_GUST to windGust,
    CLOUD_COVER to cloudCover,
    PRECIPITATION_INTENSITY to precipitationIntensity,
    PRECIPITATION_PROBABILITY to precipitationProbability,
    VISIBILITY to visibility,
    SOLAR_DHI to solar?.dhi,
    SOLAR_ETR to solar?.etr,
    UV_INDEX to uvIndex?.toDouble(),
    SOLAR_AZIMUTH to solar?.azimuth?.toDouble(),
    SOLAR_ALTITUDE to solar?.altitude?.toDouble(),
    SOLAR_DNI to solar?.dni?.toDouble(),
    SOLAR_GHI to solar?.ghi?.toDouble()
).mapKeys { it.key.name }

