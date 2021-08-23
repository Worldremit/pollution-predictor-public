package com.worldremit.config

import com.worldremit.avro.LocationTime
import com.worldremit.avro.MeasurementNormalized
import com.worldremit.util.LOCATION_INIT
import com.worldremit.util.randomUuid
import com.worldremit.util.toLocalDateTime
import com.worldremit.util.toTimestamp
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

private val LOCAL_DATE_TIME = LocalDate.of(2010, 1, 1).atStartOfDay()
private val LOCATION_TIME = LocationTime(LOCATION_INIT, LOCAL_DATE_TIME.toTimestamp())

internal class CustomTimestampExtractorTest {

    private val timestampExtractor = CustomTimestampExtractor()

    @Test
    fun `should extract timestamp from MeasurementNormalized`() {
        val measurementNormalized = MeasurementNormalized(randomUuid(), LOCATION_TIME, mapOf(), mapOf(), mapOf())
        val consumerRecord = createConsumerRecord(measurementNormalized)
        val extractedTimestamp = timestampExtractor.extract(consumerRecord, System.currentTimeMillis())
        val extractedLocalDateTime = extractedTimestamp.toLocalDateTime()
        assertThat(extractedLocalDateTime).isEqualTo(LOCAL_DATE_TIME)
    }

    private fun <T> createConsumerRecord(payload: T): ConsumerRecord<Any, Any> =
            ConsumerRecord<Any, Any>("topic", 0, 0, LOCATION_INIT, payload)

}