package com.worldremit.metric

import com.worldremit.avro.*
import com.worldremit.stats.MapStatsAggregator
import com.worldremit.util.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
@ConditionalOnProperty(value = [Toggle.ACCURACY])
class AccuracyTopology(
    private val trainingEnd: LocalDateTime,
    private val aggregator: MapStatsAggregator,
) {

    @Bean
    fun accuracy() = F<Location, ConfusionRecord, StatsMapResult> { stream ->
        stream
            .filter { c -> c.matrix.isNotEmpty() && c.locationTime.isAfter(trainingEnd) }
            .mapValues { confusion -> confusion.toStatsMapRecord() }
            .groupByKey()
            .aggregateAndMap(Store.ACCURACY_STATS, aggregator)
            .toStream()
    }

    private fun ConfusionRecord.toStatsMapRecord() = StatsMapRecord().also {
        it.locationTime = locationTime
        it.values = matrix.mapValues { kv -> if (kv.value.predicted == kv.value.actual) 1.0 else 0.0 }
    }

}

