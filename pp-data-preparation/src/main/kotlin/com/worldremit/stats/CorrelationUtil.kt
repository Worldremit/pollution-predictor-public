package com.worldremit.stats

import com.worldremit.avro.CorrelationInput
import com.worldremit.avro.Measurement
import com.worldremit.avro.PollutionFeature
import com.worldremit.avro.WeatherFeature

fun Measurement.toCorrelationInput(pollutionFeature: PollutionFeature, weatherFeature: WeatherFeature) =
    CorrelationInput().apply {
        featureX = pollutionFeatures[pollutionFeature.name]
        featureY = weatherFeatures[weatherFeature.name]
    }

// TODO: to remove
fun Measurement.toCorrelationInput(weatherFeatureX: WeatherFeature, weatherFeatureY: WeatherFeature) =
    CorrelationInput().apply {
        featureX = weatherFeatures[weatherFeatureX.name]
        featureY = weatherFeatures[weatherFeatureY.name]
    }

// TODO: to remove
fun Measurement.toCorrelationInput(pollutionFeatureX: PollutionFeature, pollutionFeatureY: PollutionFeature) =
    CorrelationInput().apply {
        featureX = pollutionFeatures[pollutionFeatureX.name]
        featureY = pollutionFeatures[pollutionFeatureY.name]
    }
