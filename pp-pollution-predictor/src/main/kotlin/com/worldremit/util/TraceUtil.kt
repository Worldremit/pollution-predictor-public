package com.worldremit.util

import com.worldremit.avro.*

fun MeasurementNormalized.toLogInfo() = "timestamp=${locationTime.timestamp.toLocalDateTime()}, id=${measurementId.short()}"

fun MeasurementStatsResult.toLogInfo() = "timestamp=${locationTime.timestamp.toLocalDateTime()}"

fun KmeansModels.toLogInfo() = "timestamp=${locationTime.timestamp.toLocalDateTime()}"

fun Predicted.toLogInfo() = "timestamp=${locationTime.timestamp.toLocalDateTime()}, id=${initialMeasurement.measurementId.short()}, delay=${modelMaturityHours}[h]"

fun ConfusionRecord.toLogInfo() = "timestamp=${locationTime.timestamp.toLocalDateTime()}}"

fun MatchedRecord.toLogInfo() = "timestamp=${predicted.locationTime.timestamp.toLocalDateTime()}, id=${predicted.initialMeasurement.measurementId.short()}"