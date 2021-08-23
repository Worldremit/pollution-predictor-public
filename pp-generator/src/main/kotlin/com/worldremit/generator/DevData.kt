package com.worldremit.generator

import com.worldremit.avro.Location
import com.worldremit.avro.LocationTime
import com.worldremit.avro.MeanStdDev
import com.worldremit.avro.MeasurementStatsResult
import com.worldremit.avro.PollutionFeature.*
import com.worldremit.avro.WeatherFeature.*
import com.worldremit.util.toTimestamp
import java.time.LocalDate

object DevData {

    val TRAINING_END = LocalDate.of(2015, 3, 8).atStartOfDay()

    val STATS_LOCATION_TIME = LocationTime().apply {
        location = Location(50.057678, 19.926189)
        timestamp = TRAINING_END.toTimestamp()
    }

    val STATS = MeasurementStatsResult().apply {
        locationTime = STATS_LOCATION_TIME
        weather = STATS_WEATHER
        pollution = STATS_POLLUTION
    }

    private val STATS_WEATHER = mapOf(
        TEMPERATURE to MeanStdDev(9.5753, 8.3775),
        TEMPERATURE_APPARENT to MeanStdDev(8.3335, 9.7450),
        DEW_POINT to MeanStdDev(3.6070, 9.6763),
        HUMIDITY to MeanStdDev(0.7219, 0.2333),
        PRESSURE to MeanStdDev(1016.5743, 7.4747),
        WIND_SPEED to MeanStdDev(11.02921, 6.9186),
        WIND_GUST to MeanStdDev(16.6379, 15.5401),
        CLOUD_COVER to MeanStdDev(0.5806, 0.2559),
        VISIBILITY to MeanStdDev(7.9423, 3.0396),
        OZONE to MeanStdDev(0.2, 0.1), // TODO: check
        SOLAR_DHI to MeanStdDev(0.2, 0.1), // TODO: check
        SOLAR_ETR to MeanStdDev(0.2, 0.1), // TODO: check
        UV_INDEX to MeanStdDev(2.8946, 1.8932),
        SOLAR_AZIMUTH to MeanStdDev(2.8946, 1.8932), // TODO: check
        SOLAR_ALTITUDE to MeanStdDev(2.8946, 1.8932), // TODO: check
        SOLAR_DNI to MeanStdDev(2.8946, 1.8932), // TODO: check
        SOLAR_GHI to MeanStdDev(2.8946, 1.8932), // TODO: check
        RAIN_INTENSITY to MeanStdDev(0.1801, 0.2115), // TODO: check
        SNOW_INTENSITY to MeanStdDev(0.6406, 0.7971), // TODO: check
    ).mapKeys { it.key.name }

    private val STATS_POLLUTION = mapOf(
        PM25 to MeanStdDev(44.1671, 34.4954),
        PM10 to MeanStdDev(62.4365, 46.3573),
        BZN to MeanStdDev(2.3928, 2.8032),
        NO2 to MeanStdDev(63.4223, 27.6681),
        NOX to MeanStdDev(226.8915, 147.5402),
        NO to MeanStdDev(107.2109, 85.0401),
        CO to MeanStdDev(1076.5211, 631.2872),
        SO2 to MeanStdDev(34.9213, 10.5372), // TODO: check
        O3 to MeanStdDev(100.1321, 25.1272), // TODO: check
    ).mapKeys { it.key.name }

}