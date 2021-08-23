package com.worldremit

import com.worldremit.avro.*
import com.worldremit.avro.PollutionFeature.*
import com.worldremit.avro.WeatherFeature.*
import com.worldremit.config.AppSettings
import com.worldremit.config.TrainingConfiguration
import com.worldremit.feature.FeatureFilterSettings
import com.worldremit.feature.Features
import com.worldremit.util.randomUuid
import com.worldremit.util.toTimestamp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

object DevData {

    val MEASUREMENT_ID = randomUuid()

    val TRAINING_START = LocalDate.of(2013, 3, 8).atStartOfDay()
    val TRAINING_END = LocalDate.of(2015, 3, 8).atStartOfDay()

    val LOCATION = Location(50.057678, 19.926189)

    val LOCATION_TIME = LocationTime(LOCATION, LocalDateTime.now().toTimestamp())

    val LOCATION_TIME_TRAINING1 = LocationTime(LOCATION, TRAINING_END.minusDays(10).toTimestamp())
    val LOCATION_TIME_TRAINING2 = LocationTime(LOCATION, TRAINING_END.minusDays(5).toTimestamp())
    val LOCATION_TIME_TRAINING_END = LocationTime(LOCATION, TRAINING_END.toTimestamp())
    val LOCATION_TIME_REGULAR = LocationTime(LOCATION, TRAINING_END.plusHours(3).toTimestamp())

    val WEATHER_FEATURES = mapOf(
        TEMPERATURE.name to 10.0,
        VISIBILITY.name to 0.2,
        WIND_SPEED.name to 5.2,
        VISIBILITY.name to 4.1,
        CLOUD_COVER.name to 0.55,
        RAIN_INTENSITY.name to 0.1,
        SNOW_INTENSITY.name to 0.25,
        HUMIDITY.name to 0.7,
        PRESSURE.name to 1010.0,
        RAIN_INTENSITY.name to 0.5,
        SNOW_INTENSITY.name to 0.0
    )

    val POLLUTION_FEATURES = mapOf(
        PM25.name to 20.3,
        PM10.name to 10.5,
        BZN.name to 3.1,
        CO.name to 15.2,
        NO2.name to 100.1,
        NO.name to 14.0,
        NOX.name to 20.6
    )

    val POLLUTION_RECORD = PollutionRecord().apply {
        pollutionId = randomUuid()
        locationTime = LOCATION_TIME
        features = POLLUTION_FEATURES
    }

    val WEATHER_RECORD = WeatherRecord().apply {
        weatherId = randomUuid()
        locationTime = LOCATION_TIME
        features = WEATHER_FEATURES
        windDirection = WindDirection.W
    }

    val MEASUREMENT = Measurement().apply {
        measurementId = MEASUREMENT_ID
        locationTime = LOCATION_TIME
        dayNight = DayNight.DAY
        season = Season.SPRING
        windDirection = WindDirection.E
        weatherFeatures = WEATHER_FEATURES
        pollutionFeatures = POLLUTION_FEATURES
    }

    val NORMALIZED_WEATHER_FEATURES = mapOf(
        TEMPERATURE.name to 0.3,
        VISIBILITY.name to 0.15,
        WIND_SPEED.name to 0.5,
        VISIBILITY.name to 0.1,
        PRESSURE.name to -0.2,
        CLOUD_COVER.name to -0.15,
        HUMIDITY.name to -0.1,
        RAIN_INTENSITY.name to -0.5,
        SNOW_INTENSITY.name to 0.0,
    )

    val NORMALIZED_POLLUTION_FEATURES = mapOf(
        PM25.name to 0.1,
        PM10.name to -0.3,
        BZN.name to 0.2,
        CO.name to -0.2,
        NO2.name to -0.1,
        NO.name to 0.0,
        NOX.name to 0.6
    )

    val NORMALIZED_ONE_HOT_FEATURES = mapOf(
        OneHotFeature.DAY_NIGHT.name to 0.0,
        "SEASON_WINTER" to 0.0,
        "SEASON_SPRING" to 0.0,
        "SEASON_SUMMER" to 1.0,
        "SEASON_AUTUMN" to 0.0,
        "WIND_DIRECTION_N" to 0.0,
        "WIND_DIRECTION_NE" to 0.0,
        "WIND_DIRECTION_E" to 0.0,
        "WIND_DIRECTION_SE" to 0.0,
        "WIND_DIRECTION_S" to 0.0,
        "WIND_DIRECTION_SW" to 1.0,
        "WIND_DIRECTION_W" to 0.0,
        "WIND_DIRECTION_NW" to 0.0,
        "WIND_DIRECTION_NONE" to 0.0,
        "WIND_DIRECTION_UNKNOWN" to 0.0,
    )

    val WEATHER_STATS = mapOf(
        TEMPERATURE to MeanStdDev(9.5753, 8.3775),
        TEMPERATURE_APPARENT to MeanStdDev(8.3335, 9.7450),
        DEW_POINT to MeanStdDev(3.6070, 9.6763),
        PRESSURE to MeanStdDev(1016.5743, 7.4747),
        HUMIDITY to MeanStdDev(0.7219, 0.2333),
        VISIBILITY to MeanStdDev(7.9423, 3.0396),
        RAIN_INTENSITY to MeanStdDev(0.1801, 0.2115),
        SNOW_INTENSITY to MeanStdDev(0.6406, 0.7971),
        WIND_SPEED to MeanStdDev(11.02921, 6.9186),
        WIND_GUST to MeanStdDev(16.6379, 15.5401),
        CLOUD_COVER to MeanStdDev(0.5806, 0.2559),
        UV_INDEX to MeanStdDev(2.8946, 1.8932),
    ).mapKeys { it.key.name }

    val POLLUTION_STATS = mapOf(
        PM25 to MeanStdDev(44.1671, 34.4954),
        PM10 to MeanStdDev(62.4365, 46.3573),
        BZN to MeanStdDev(2.3928, 2.8032),
        NO2 to MeanStdDev(63.4223, 27.6681),
        CO to MeanStdDev(1076.5211, 631.2872),
        NO to MeanStdDev(107.2109, 85.0401),
        NOX to MeanStdDev(226.8915, 147.5402),
    ).mapKeys { it.key.name }

    val MEASUREMENT_STATS_RESULT = MeasurementStatsResult().apply {
        locationTime = LOCATION_TIME
        weather = WEATHER_STATS
        pollution = POLLUTION_STATS
    }

    val MEASUREMENT_TRAINING1 = MEASUREMENT.copy { it.locationTime = LOCATION_TIME_TRAINING1 }
    val MEASUREMENT_TRAINING2 = MEASUREMENT.copy { it.locationTime = LOCATION_TIME_TRAINING2 }
    val MEASUREMENT_TRAINING_END = MEASUREMENT.copy { it.locationTime = LOCATION_TIME_TRAINING_END }
    val MEASUREMENT_REGULAR = MEASUREMENT.copy { it.locationTime = LOCATION_TIME_REGULAR }

    val GLOBAL_STATS_TRAINING1 = MEASUREMENT_STATS_RESULT.copy { it.locationTime = LOCATION_TIME_TRAINING1 }
    val GLOBAL_STATS_TRAINING_END = MEASUREMENT_STATS_RESULT.copy { it.locationTime = LOCATION_TIME_TRAINING_END }

    val APP_SETTINGS = AppSettings(
        trainingFrom = TRAINING_START.toLocalDate(),
        trainingTo = TRAINING_END.toLocalDate(),
        trainingBuffer = Duration.ofDays(365),
        normalizationThreshold = 3
    )

    val TRAINING_CONFIG = TrainingConfiguration().trainingTimestampConfig(APP_SETTINGS)

    val FEATURE_FILTER = FeatureFilterSettings(
        filter = Features(
            weather = listOf(TEMPERATURE, VISIBILITY, PRESSURE),
            pollution = listOf(PM25, PM10, NO2, CO, NOX),
            oneHot = listOf(OneHotFeature.SEASON)
        ),
        filterNot = Features(
            weather = listOf(),
            pollution = listOf(),
            oneHot = listOf()
        )
    )

    fun PollutionRecord.copy(consumer: (PollutionRecord.Builder) -> Unit): PollutionRecord =
        PollutionRecord.newBuilder(this).also {
            consumer.invoke(it)
        }.build()

    private fun Measurement.copy(consumer: (Measurement.Builder) -> Unit): Measurement =
        Measurement.newBuilder(this).also {
            consumer.invoke(it)
        }.build()

    private fun MeasurementStatsResult.copy(consumer: (MeasurementStatsResult.Builder) -> Unit): MeasurementStatsResult =
        MeasurementStatsResult.newBuilder(this).also {
            consumer.invoke(it)
        }.build()



}