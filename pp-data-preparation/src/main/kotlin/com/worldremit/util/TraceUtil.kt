package com.worldremit.util

import com.worldremit.avro.Location
import com.worldremit.avro.Measurement
import com.worldremit.avro.MeasurementNormalized
import com.worldremit.avro.MeasurementStatsResult

fun Location.toLogInfo() = "[${latitude},${longitude}]"

fun Measurement.toLogInfo() = "timestamp=${locationTime.timestamp.toLocalDateTime()}, id=${measurementId.short()}"

fun MeasurementNormalized.toLogInfo() = "timestamp=${locationTime.timestamp.toLocalDateTime()}, id=${measurementId.short()}"

fun MeasurementStatsResult.toLogInfo() = "timestamp=${locationTime.timestamp.toLocalDateTime()}"
