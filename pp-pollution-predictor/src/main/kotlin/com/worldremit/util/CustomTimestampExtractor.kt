package com.worldremit.util

import com.worldremit.avro.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.FailOnInvalidTimestamp
import org.apache.kafka.streams.processor.TimestampExtractor

private val DEFAULT_EXTRACTOR = FailOnInvalidTimestamp()

class CustomTimestampExtractor : TimestampExtractor {

    override fun extract(record: ConsumerRecord<Any, Any>, partitionTime: Long): Long {
        return when (val value = record.value()) {
            is MeasurementNormalized -> value.locationTime.timestamp
            is KmeansModels -> value.locationTime.timestamp
            is ConfusionRecord -> value.locationTime.timestamp
            is MatchedRecord -> value.predicted.locationTime.timestamp
            else -> DEFAULT_EXTRACTOR.extract(record, partitionTime)
        }
    }

}