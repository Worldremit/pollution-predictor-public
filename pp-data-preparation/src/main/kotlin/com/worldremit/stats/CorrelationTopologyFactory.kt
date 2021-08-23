package com.worldremit.stats

import com.worldremit.avro.CorrelationInput
import com.worldremit.avro.CorrelationResult
import com.worldremit.avro.Location
import com.worldremit.avro.Measurement
import com.worldremit.stats.core.CorrelationAggregator
import com.worldremit.util.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component
class CorrelationTopologyFactory(private val correlationAggregator: CorrelationAggregator) {

    fun create(
        filterFromTime: LocalDateTime = LocalDateTime.MIN,
        filterToTime: LocalDateTime = LocalDateTime.MAX,
        inputFeatures: (Measurement) -> CorrelationInput,
    ) = F<Location, Measurement, CorrelationResult> { stream ->
        stream
            .filter { measurement -> measurement.isTimeBetween(filterFromTime, filterToTime) }
            .mapValues(inputFeatures::invoke)
            .filter { input -> input.featureX != null && input.featureY != null }
            .groupByKey()
            .aggregateAndMap(Store.CORRELATION, correlationAggregator)
            .toStream()
    }

}
