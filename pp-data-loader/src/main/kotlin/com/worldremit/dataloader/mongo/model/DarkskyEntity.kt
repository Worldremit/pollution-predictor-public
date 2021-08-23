package com.worldremit.dataloader.mongo.model

import com.mongodb.client.model.geojson.Point
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class DarkskyEntity(
    val id: String,
    val location: Point,
    val timestamp: LocalDateTime,
    val data: DarkskyDataEntity
)

data class DarkskyDataEntity(
    val icon: String? = null,
    val temperature: Double?,
    val temperatureApparent: Double?,
    val dewPoint: Double?,
    val humidity: Double?,
    val pressure: Double?,
    val windSpeed: Double?,
    val windGust: Double?,
    val windDirection: Int?,
    val cloudCover: Double?,
    val precipitationIntensity: Double?,
    val precipitationProbability: Double?,
    val precipitationType: String?,
    val uvIndex: Int?,
    val visibility: Double?,
    val ozone: Double?,
    val solar: DarkSkySolarEntity?
)

data class DarkSkySolarEntity(
    val azimuth: Int?,
    val altitude: Int?,
    val dni: Int?,
    val ghi: Int?,
    val dhi: Double?,
    val etr: Double?
)
