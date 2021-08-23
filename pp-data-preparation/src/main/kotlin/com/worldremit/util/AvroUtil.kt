package com.worldremit.util

import com.worldremit.avro.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

val LOCATION_INIT = Location(0.0, 0.0)
val TIMESTAMP_INIT = Instant.EPOCH.toEpochMilli()
val LOCATION_TIME_INIT = LocationTime(LOCATION_INIT, TIMESTAMP_INIT)

fun randomUuid() = UUID.randomUUID().toString().let(::Uuid)

fun Uuid.short() = value.substring(0, 7)

fun Long.toLocalDateTime(): LocalDateTime = this
    .let(Instant::ofEpochMilli)
    .let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }

fun LocalDateTime.toTimestamp(): Long = toInstant(ZoneOffset.UTC).toEpochMilli()

/**
 * @from inclusive
 * @to exclusive
 */
private fun LocalDateTime.isBetween(from: LocalDateTime, to: LocalDateTime) = (isBefore(to) || isEqual(from)) && isAfter(from)

fun Measurement.isTimeBetween(filterFromTime: LocalDateTime, filterToTime: LocalDateTime) =
    locationTime.timestamp.toLocalDateTime().isBetween(filterFromTime, filterToTime)

fun MeanStdDev.normalize(value: Double): Double = (mean - value) / stdDev

