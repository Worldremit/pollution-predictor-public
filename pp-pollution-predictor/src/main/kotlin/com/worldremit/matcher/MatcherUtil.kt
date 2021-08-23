package com.worldremit.matcher

import com.worldremit.avro.LocationTime
import com.worldremit.avro.Predicted
import com.worldremit.util.toLocalDateTime
import com.worldremit.util.toTimestamp
import java.time.LocalDateTime

private fun LocalDateTime.transformTime(transformer: (LocalDateTime) -> (LocalDateTime)) = transformer.invoke(this)

private fun Long.transformTime(transformer: (LocalDateTime) -> (LocalDateTime)): Long = this
    .toLocalDateTime()
    .transformTime(transformer)
    .toTimestamp()

private fun LocationTime.transformTime(transformer: (LocalDateTime) -> (LocalDateTime)) =
    LocationTime(location, timestamp.transformTime(transformer))

fun Predicted.transformTime(transformer: (LocalDateTime) -> (LocalDateTime)) = Predicted().also {
    it.locationTime = locationTime.transformTime(transformer)
    it.initialMeasurement = initialMeasurement
    it.predictedPatterns = predictedPatterns
}