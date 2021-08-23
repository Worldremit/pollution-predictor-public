package com.worldremit

import com.worldremit.avro.*
import com.worldremit.avro.OneHotFeature.*
import com.worldremit.avro.PollutionFeature.*
import com.worldremit.avro.WeatherFeature.*
import com.worldremit.cluster.ClusterResult
import com.worldremit.cluster.ClusterSettings
import com.worldremit.config.Norms
import com.worldremit.feature.FeatureFilterSettings
import com.worldremit.feature.Features
import com.worldremit.matcher.MatcherSettings
import com.worldremit.util.loadConfig
import com.worldremit.util.toTimestamp
import org.apache.mahout.math.Centroid
import org.apache.mahout.math.DenseVector
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

fun randomUuid() = UUID.randomUUID().toString().let(::Uuid)

object DevData {

    val MEASUREMENT_ID = randomUuid()

    val TRAINING_END = LocalDate.of(2015, 3, 8).atStartOfDay()

    val LOCATION = Location(50.057678, 19.926189)

    val LOCATION_TIME = LocationTime(LOCATION, LocalDateTime.now().toTimestamp())

    val LOCATION_TIME_TRAINING = LocationTime(LOCATION, TRAINING_END.minusDays(10).toTimestamp())
    val LOCATION_TIME_TRAINING_LAST_DAY = LocationTime(LOCATION, TRAINING_END.minusHours(3).toTimestamp())
    val LOCATION_TIME_REGULAR = LocationTime(LOCATION, TRAINING_END.plusHours(3).toTimestamp())

    val WEATHER_FEATURES = mapOf(
        TEMPERATURE to 10.0,
        VISIBILITY to 0.2,
        WIND_SPEED to 5.2,
        VISIBILITY to 4.1,
        CLOUD_COVER to 0.55,
        RAIN_INTENSITY to 0.1,
        SNOW_INTENSITY to 0.25,
        HUMIDITY to 0.7,
        PRESSURE to 1010.0,
        RAIN_INTENSITY to 0.5,
        SNOW_INTENSITY to 0.0
    ).mapKeys { it.key.name }

    val POLLUTION_FEATURES = mapOf(
        PM25 to 20.3,
        PM10 to 10.5,
        BZN to 3.1,
        CO to 15.2,
        NO2 to 100.1,
        NO to 14.0,
        NOX to 20.6
    ).mapKeys { it.key.name }

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
        DAY_NIGHT.name to 0.0,
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

    val MEASUREMENT_NORMALIZED = MeasurementNormalized().apply {
        measurementId = MEASUREMENT_ID
        locationTime = LOCATION_TIME
        weatherFeatures = NORMALIZED_WEATHER_FEATURES
        pollutionFeatures = NORMALIZED_POLLUTION_FEATURES
        oneHotFeatures = NORMALIZED_ONE_HOT_FEATURES
    }

    val CONFUSION_MATRIX = mapOf(
        PM25.name to ConfusionFeature(Level.MODERATE, Level.GOOD),
        BZN.name to ConfusionFeature(Level.UNHEALTHY, Level.UNHEALTHY)
    )

    val CLUSTER_PARAMS = ClusterParams().apply {
        clustersEstimated = 5
        distanceCutoff = 2.0
        processedDataPoints = 1
    }

    val FEATURES_DESCRIPTION = FeaturesDescription().apply {
        weather = listOf("TEMPERATURE", "VISIBILITY", "PRESSURE")
        oneHot = listOf("SEASON_WINTER", "SEASON_SPRING", "SEASON_SUMMER", "SEASON_AUTUMN")
        target = "PM25"
    }

    val COORDINATES = listOf(0.1, 0.2, 0.4, -0.3, 0.1, 0.2, 0.4, -0.3)

    val MODEL = KmeansModel().apply {
        clusterCenters = listOf(
            ClusterCenter().apply {
                weight = 1.0
                coordinates = COORDINATES
            }
        )
        clusterParams = CLUSTER_PARAMS
        featuresDescription = FEATURES_DESCRIPTION

    }

    val MODELS = KmeansModels().apply {
        locationTime = LOCATION_TIME
        models = mapOf(
            PM25.name to MODEL,
            CO.name to MODEL,
            NO2.name to MODEL,
        )
    }

    val PREDICTED_PATTERNS = mapOf(
        PM25.name to PredictedPattern().apply {
            featuresDescription = FEATURES_DESCRIPTION
            features = COORDINATES
        },
        BZN.name to PredictedPattern().apply {
            featuresDescription = FEATURES_DESCRIPTION
            features = COORDINATES
        }
    )

    val PREDICTED = Predicted().apply {
        locationTime = LOCATION_TIME
        initialMeasurement = MEASUREMENT_NORMALIZED
        predictedPatterns = PREDICTED_PATTERNS
        modelMaturityHours = 1L
    }

    val PREDICTED_INIT = Predicted().apply {
        locationTime = LOCATION_TIME
        initialMeasurement = MEASUREMENT_NORMALIZED
        predictedPatterns = mapOf()
        modelMaturityHours = 0L
    }

    val CONFUSION_RECORD = ConfusionRecord().apply {
        locationTime = LOCATION_TIME
        matrix = CONFUSION_MATRIX
    }

    val CLUSTER_CENTER = ClusterCenter().apply {
        weight = 3.0
        coordinates = listOf(-1.2, 0.3, 2.4)
    }

    val MATCHED_RECORD = MatchedRecord().apply {
        predicted = PREDICTED
        actual = MEASUREMENT_NORMALIZED
    }

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

    val FEATURE_FILTER = FeatureFilterSettings(
        filter = Features(
            weather = listOf(TEMPERATURE, VISIBILITY, PRESSURE),
            pollution = listOf(PM25, PM10, NO2, CO, NOX),
            oneHot = listOf(SEASON)
        ),
        filterNot = Features(
            weather = listOf(),
            pollution = listOf(),
            oneHot = listOf()
        )
    )

    val CENTROID_0 = Centroid(0, DenseVector(doubleArrayOf(1.0, -1.0, 3.0)), 3.0)
    val CENTROID_1 = Centroid(0, DenseVector(doubleArrayOf(2.0, -1.0, 2.0)), 1.0)
    val CENTROID_2 = Centroid(0, DenseVector(doubleArrayOf(0.0, -1.0, 0.0)), 1.0)

    val CLUSTER_RESULT = ClusterResult(
        clusterParams = CLUSTER_PARAMS,
        closestCenter = Centroid(0, DenseVector(doubleArrayOf(1.0, -1.0, 3.0)), 3.0),
        clusterCenters = listOf(CENTROID_0, CENTROID_1, CENTROID_2)
    )

    val NORMS = loadConfig<Norms>()
    val CLUSTER_SETTINGS = loadConfig<ClusterSettings>("app.cluster")
    val FEATURES = loadConfig<FeatureFilterSettings>("app.features")

    val MATCHER_SETTINGS = MatcherSettings(Duration.ofDays(1), Duration.ofDays(365))
}