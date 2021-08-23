package com.worldremit.deduplication

import com.worldremit.avro.*
import com.worldremit.util.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class WeatherDeduplicationTopology {

    @Bean
    @ConditionalOnProperty(value = [Toggle.WEATHER_DEDUPLICATION])
    fun weatherDeduplicationStoreBuilder() = kvStore<LocationTime, DeduplicationRecord>(Store.DEDUPLICATION_WEATHER)

    @Bean
    @ConditionalOnProperty(value = [Toggle.WEATHER_DEDUPLICATION])
    fun weatherDeduplication() = F<Location, WeatherRaw, WeatherRaw> { stream ->
        stream.deduplicate(Store.DEDUPLICATION_WEATHER) { it.locationTime }
    }

}
