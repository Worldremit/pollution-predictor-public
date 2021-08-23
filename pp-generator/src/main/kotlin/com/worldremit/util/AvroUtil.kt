package com.worldremit.util

import com.worldremit.avro.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*

fun randomUuid() = UUID.randomUUID().toString().let(::Uuid)

fun Uuid.short() = value.substring(0, 7)

fun LocalDateTime.toTimestamp(): Long = toInstant(ZoneOffset.UTC).toEpochMilli()


