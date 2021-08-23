package com.worldremit.dataloader.mongo.model

import com.mongodb.client.model.geojson.Point
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class WiosEntity(
    val id: String,
    val location: Point,
    val timestamp: LocalDateTime,
    val data: WiosDataEntity
)

data class WiosDataEntity(
    val pm10: Double?,
    val pm25: Double?,
    val bzn: Double?,
    val no2: Double?,
    val nox: Double?,
    val no: Double?,
    val co: Double?,
    val so2: Double?,
    val o3: Double? // not used
)