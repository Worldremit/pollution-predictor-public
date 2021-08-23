package com.worldremit.stats

import com.worldremit.avro.*
import com.worldremit.util.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StatsTopology(private val mapStatsAggregator: MapStatsAggregator) {

    @Bean
    @ConditionalOnProperty(value = [Toggle.POLLUTION_MEAN_STD_DEV])
    fun pollutionMeanStdDev() = F<Location, PollutionRecord, StatsMapResult> { stream ->
        stream
            .mapValues(PollutionRecord::toStatsRecord)
            .groupByKey()
            .aggregateAndMap(Store.POLLUTION_STATS, mapStatsAggregator)
            .toStream()
    }

    @Bean
    @ConditionalOnProperty(value = [Toggle.WEATHER_MEAN_STD_DEV])
    fun weatherMeanStdDev() = F<Location, WeatherRecord, StatsMapResult> { stream ->
        stream
            .mapValues(WeatherRecord::toStatsRecord)
            .groupByKey()
            .aggregateAndMap(Store.WEATHER_STATS, mapStatsAggregator)
            .toStream()
    }

}

