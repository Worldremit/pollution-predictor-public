package com.worldremit.dataloader

import com.worldremit.avro.Location
import com.worldremit.avro.LocationTime
import com.worldremit.dataloader.kafka.config.LoaderParams
import com.worldremit.dataloader.kafka.config.MongoQuery
import com.worldremit.dataloader.util.toLocalDateTime
import mu.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono

class Loader<E, V : SpecificRecord>(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val entityClass: Class<E>,
    private val collection: String,
    private val mapper: (E) -> V,
    private val locationTimeMapper: (V) -> LocationTime,
    private val kafkaTemplate: KafkaTemplate<Location, V>,
    private val topicName: String,
) {

    private val log = KotlinLogging.logger {}

    fun load(loaderParams: LoaderParams, filteredLocations: List<Location>): Flux<SendResult<Location, V>> {
        val query = MongoQuery.create(loaderParams.queryFrom, loaderParams.queryTo, filteredLocations)
        return mongoTemplate.find(query, entityClass, collection)
            .map(mapper)
            .map(::createProducerRecord)
            .flatMap { kafkaTemplate.send(it).get().toMono() } // completable would be ok, but we won't have an order
            .let { if (loaderParams.delayElements.isZero) it else it.delayElements(loaderParams.delayElements) }
    }

    private fun createProducerRecord(record: V): ProducerRecord<Location, V> = locationTimeMapper.invoke(record).let { lt ->
        lt.timestamp.toLocalDateTime().let {
            if (it.dayOfMonth == 1 && it.hour == 0) {
                log.info { "${record.schema.name}: $it" }
            }
        }
        ProducerRecord(topicName, null, lt.timestamp, lt.location, record)
    }

}
