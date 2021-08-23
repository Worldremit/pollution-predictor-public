package com.worldremit.confusion

import com.worldremit.avro.ConfusionFeature
import com.worldremit.avro.ConfusionRecord
import com.worldremit.avro.MatchedRecord
import com.worldremit.avro.MeasurementStatsResult
import com.worldremit.config.Norm
import com.worldremit.config.Norms
import com.worldremit.util.denormalize
import com.worldremit.util.getOrThrow
import com.worldremit.util.matchAndTransform
import org.springframework.stereotype.Component

/**
 * Prediction vs actual measurement
 */
@Component
class ConfusionService(norms: Norms) {

    private val pollutionNorms: Map<String, Norm> = norms.norms.mapKeys { it.key.name }

    fun createConfusionRecord(matched: MatchedRecord, stats: MeasurementStatsResult) = ConfusionRecord().apply {
        locationTime = matched.predicted.locationTime
        matrix = matched.predicted.predictedPatterns
            .mapValues { it.value.toPredictedPollution() to matched.actual.pollutionFeatures.getOrThrow(it.key) }
            .matchAndTransform(stats.pollution) { value, meanDev -> meanDev.denormalize(value.first) to meanDev.denormalize(value.second) }
            .matchAndTransform(pollutionNorms) { value, norm -> norm.assignLevel(value.first) to norm.assignLevel(value.second) }
            .mapValues { kv -> kv.value.let { ConfusionFeature(it.first, it.second) } }
    }

}
