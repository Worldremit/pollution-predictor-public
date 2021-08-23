package com.worldremit.config

import com.worldremit.avro.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.FailOnInvalidTimestamp
import org.apache.kafka.streams.processor.TimestampExtractor

private val DEFAULT_EXTRACTOR = FailOnInvalidTimestamp()

class CustomTimestampExtractor : TimestampExtractor {

    override fun extract(record: ConsumerRecord<Any, Any>, partitionTime: Long): Long {
        return when (val value = record.value()) {
            is PollutionRaw -> value.locationTime.timestamp
            is WeatherRaw -> value.locationTime.timestamp
            is PollutionRecord -> value.locationTime.timestamp
            is WeatherRecord -> value.locationTime.timestamp
            is Measurement -> value.locationTime.timestamp
            is MeasurementNormalized -> value.locationTime.timestamp
            is MeasurementStatsResult -> value.locationTime.timestamp
            else -> DEFAULT_EXTRACTOR.extract(record, partitionTime)
        }
    }

}