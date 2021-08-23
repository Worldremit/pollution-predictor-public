package com.worldremit.stats

import com.worldremit.avro.Location
import com.worldremit.avro.MeasurementStatsResult
import com.worldremit.config.TrainingTimestampConfig
import com.worldremit.util.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(value = [Toggle.GLOBAL_STATS])
class GlobalStatsTopology(private val config: TrainingTimestampConfig) {

    @Bean
    fun globalStatsStoreBuilder() = kvStore<Location, MeasurementStatsResult>(Store.GLOBAL_STATS)

    @Bean
    fun globalStats() = F<Location, MeasurementStatsResult, MeasurementStatsResult> { stream ->
        stream
            .transform(Store.GLOBAL_STATS) { _, store, location, stats ->
                if (stats.locationTime.timestamp >= config.endTimestamp) { // prd period
                    store.get(location).let {
                        if (it != null && it.locationTime.timestamp >= config.endTimestamp) { // stats already emitted
                            null
                        } else { // update stats and emmit
                            stats.also { store.put(location, stats) }
                        }
                    }
                } else { // training period, update stats, don't emit
                    null.also { store.put(location, stats) }
                }
            }
    }

}
