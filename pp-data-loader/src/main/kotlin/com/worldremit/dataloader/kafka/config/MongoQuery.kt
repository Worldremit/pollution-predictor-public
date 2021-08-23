package com.worldremit.dataloader.kafka.config

import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import com.worldremit.avro.Location
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object MongoQuery {

    private const val FIELD_TIMESTAMP = "timestamp"
    private const val FIELD_LOCATION = "location"

    fun create(from: LocalDate, to: LocalDate, filterLocations: List<Location>) = Query()
            .with(Sort.by(FIELD_TIMESTAMP))
            .addCriteria(
                    Criteria().andOperator(
                            Criteria.where(FIELD_TIMESTAMP).gte(LocalDateTime.of(from, LocalTime.MIDNIGHT)),
                            Criteria.where(FIELD_TIMESTAMP).lt(LocalDateTime.of(to, LocalTime.MIDNIGHT)),
                            Criteria.where(FIELD_LOCATION).`in`(filterLocations.map { it.toPoint() }),
                    ))

    private fun Location.toPoint() = Point(Position(getLatitude(), getLongitude()))

}