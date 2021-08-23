package com.worldremit.confusion

import com.worldremit.avro.FeaturesDescription
import com.worldremit.avro.PredictedPattern

fun FeaturesDescription.targetIdx(): Int = weather.size + oneHot.size

fun PredictedPattern.toPredictedPollution(): Double = features[featuresDescription.targetIdx()]
