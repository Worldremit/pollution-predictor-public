package com.worldremit.dataloader.mongo.model

import com.mongodb.client.model.geojson.Point
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class MeasurementEntity(
    val measurementId: UUID, // id
    val locationTime: LocationTime,
    val features: Map<String, Double>
)

data class LocationTime(
    val location: Point, // Location
    val timestamp: LocalDateTime,
)
