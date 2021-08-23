package com.worldremit.normalizer

import com.worldremit.avro.*
import com.worldremit.config.AppSettings
import com.worldremit.util.mergeMaps
import com.worldremit.util.normalize
import com.worldremit.util.oneHotEncoding
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class NormalizerService(private val appSettings: AppSettings) {

    fun normalize(measurement: Measurement, stats: MeasurementStatsResult) =
        MeasurementNormalized().apply {
            measurementId = measurement.measurementId
            locationTime = measurement.locationTime

            pollutionFeatures = normalizeAndFilterOutliers(measurement.pollutionFeatures, stats.pollution)
            weatherFeatures = normalizeAndFilterOutliers(measurement.weatherFeatures, stats.weather)

            oneHotFeatures = mergeMaps(
                oneHotEncoding(measurement.windDirection),
                oneHotEncoding(measurement.dayNight),
                oneHotEncoding(measurement.season)
            )
        }

    private fun normalizeAndFilterOutliers(features: Map<String, Double>, stats: Map<String, MeanStdDev>) = features
        .mapValues { entry -> stats[entry.key]?.normalize(entry.value) }
        .filterValues { it != null && abs(it) <= appSettings.normalizationThreshold }

}