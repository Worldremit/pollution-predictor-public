package com.worldremit.stats

import com.worldremit.avro.PollutionFeature
import com.worldremit.avro.WeatherFeature
import com.worldremit.util.Toggle
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class CorrelationTopology(
    private val topologyFactory: CorrelationTopologyFactory,
    private val trainingEnd: LocalDateTime
) {

    @Bean
    @ConditionalOnProperty(value = [Toggle.CORRELATION_PM25_PM10])
    fun correlationPm25Pm10() = topologyFactory.create(
        filterToTime = trainingEnd,
        inputFeatures = { it.toCorrelationInput(PollutionFeature.PM25, PollutionFeature.PM10) }
    )

    @Bean
    @ConditionalOnProperty(value = [Toggle.CORRELATION_TEMP_PRESSURE])
    fun correlationTempPressure() = topologyFactory.create(
        filterToTime = trainingEnd,
        inputFeatures = { it.toCorrelationInput(WeatherFeature.TEMPERATURE, WeatherFeature.PRESSURE) }
    )

    @Bean
    @ConditionalOnProperty(value = [Toggle.CORRELATION_PM25_PRESSURE])
    fun correlationPm25Pressure() = topologyFactory.create(
        filterToTime = trainingEnd,
        inputFeatures = { it.toCorrelationInput(PollutionFeature.PM25, WeatherFeature.PRESSURE) }
    )

}
