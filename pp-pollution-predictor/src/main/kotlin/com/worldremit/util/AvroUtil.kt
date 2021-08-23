package com.worldremit.util

import com.worldremit.avro.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

val LOCATION_INIT = Location(0.0, 0.0)
val TIMESTAMP_INIT = Instant.EPOCH.toEpochMilli()
val LOCATION_TIME_INIT = LocationTime(LOCATION_INIT, TIMESTAMP_INIT)

fun Uuid.short() = value.substring(0, 7)

fun Long.toLocalDateTime(): LocalDateTime = this
    .let(Instant::ofEpochMilli)
    .let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }

fun LocalDateTime.toTimestamp(): Long = toInstant(ZoneOffset.UTC).toEpochMilli()

fun MeasurementNormalized.toLocalDateTime(): LocalDateTime = locationTime.timestamp.toLocalDateTime()

fun KmeansModels.isNotEmpty(): Boolean = models.entries.any { entry -> entry.value.clusterCenters.size > 0 }

//     (isBefore(to) || isEqual(from)) && isAfter(from)
fun LocationTime.isAfter(localDateTime: LocalDateTime) = timestamp.toLocalDateTime().isAfter(localDateTime)

fun MatchedRecord.isNotEmpty() = predicted.predictedPatterns.isNotEmpty()