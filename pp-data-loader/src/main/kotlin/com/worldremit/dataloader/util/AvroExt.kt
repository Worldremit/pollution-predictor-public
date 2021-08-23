package com.worldremit.dataloader.util

import com.mongodb.client.model.geojson.Point
import com.worldremit.avro.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun randomUuid() = UUID.randomUUID().toString().let(::Uuid)

fun Long.toLocalDateTime(): LocalDateTime = this
    .let(Instant::ofEpochMilli)
    .let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }

fun LocalDateTime.toTimestamp(): Long = toInstant(ZoneOffset.UTC).toEpochMilli()

fun Point.toAvroLocation() = Location(position.values[0], position.values[1])
