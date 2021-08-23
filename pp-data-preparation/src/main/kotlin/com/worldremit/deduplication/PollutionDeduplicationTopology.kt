package com.worldremit.deduplication

import com.worldremit.avro.*
import com.worldremit.util.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@ConditionalOnProperty(value = [Toggle.POLLUTION_DEDUPLICATION])
class PollutionDeduplicationTopology {

    @Bean
    fun pollutionDeduplicationStoreBuilder() = kvStore<LocationTime, DeduplicationRecord>(Store.DEDUPLICATION_POLLUTION)

    @Bean
    @ConditionalOnProperty(value = [Toggle.POLLUTION_DEDUPLICATION])
    fun pollutionDeduplication() = F<Location, PollutionRaw, PollutionRaw> { stream ->
        stream.deduplicate(Store.DEDUPLICATION_POLLUTION) { it.locationTime }
    }

}
