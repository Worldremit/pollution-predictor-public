package com.worldremit.metric

import com.worldremit.avro.*
import com.worldremit.stats.MapStatsAggregator
import com.worldremit.util.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class DelayModelStatsTopology(
    private val trainingEnd: LocalDateTime,
    private val aggregator: MapStatsAggregator,
) {

    @Bean
    @ConditionalOnProperty(value = [Toggle.DELAY_MODEL_STATS])
    fun delayModelStats() = F<Location, Predicted, StatsMapResult> { stream ->
        stream
            .filter { predicted -> predicted.toLocalDateTime().isAfter(trainingEnd) }
            .mapValues { predicted -> predicted.toStatsMapRecord() }
            .groupByKey()
            .aggregateAndMap(Store.DELAY_MODEL_STATS, aggregator)
            .toStream()
    }

}

