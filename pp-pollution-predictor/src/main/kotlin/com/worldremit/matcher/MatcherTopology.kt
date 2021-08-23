package com.worldremit.matcher

import com.worldremit.avro.*
import com.worldremit.util.*
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(value = [Toggle.MATCHER])
class MatcherTopology(private val config: MatcherSettings) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun matcher() =
        BiF<Location, Predicted, MeasurementNormalized, MatchedRecord> { predictedStream, measurementStream ->

            val actualMeasurements = measurementStream
                .trace(log, Step.MATCHER_NEW_ACTUAL) { it.toLogInfo() }
                .selectKey(Store.MATCHER_ACTUAL_REKEY) { it.locationTime }

            val predictionsForwarded = predictedStream
                .mapValues { _, predicted -> predicted.transformTime { it.plus(config.predictionTime) } }
                .trace(log, Step.MATCHER_PREDICTION_FORWARDED) { it.toLogInfo() }
                .selectKey(Store.MATCHER_PREDICTIONS_FORWARDED_REKEY) { it.locationTime }

            predictionsForwarded.join(Store.MATCHER_JOIN, actualMeasurements, config.joinWindow, ::match)
                .trace(log, Step.MATCHER_JOINED) { it.toLogInfo() }
                .selectKey(Store.MATCHER_REKEY) { it.predicted.initialMeasurement.locationTime.location }
        }

    private fun match(predicted: Predicted, measurement: MeasurementNormalized) = MatchedRecord(predicted, measurement)


}
