package com.worldremit.stats

import com.worldremit.avro.*
import com.worldremit.util.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MeasurementStatsTopology(private val measurementStatsAggregator: MeasurementStatsAggregator) {

    @Bean
    @ConditionalOnProperty(value = [Toggle.MEASUREMENT_STATS])
    fun measurementStats() = F<Location, Measurement, MeasurementStatsResult> { stream ->
        stream
            .groupByKey()
            .aggregateAndMap(Store.MEASUREMENT_STATS, measurementStatsAggregator)
            .toStream()
    }

}

