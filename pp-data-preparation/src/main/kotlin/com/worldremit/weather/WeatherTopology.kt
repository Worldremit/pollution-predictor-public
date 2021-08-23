package com.worldremit.weather

import com.worldremit.avro.Location
import com.worldremit.avro.WeatherRaw
import com.worldremit.avro.WeatherRecord
import com.worldremit.util.F
import com.worldremit.util.Toggle
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@ConditionalOnProperty(value = [Toggle.WEATHER])
class WeatherTopology(private val transformer: WeatherRawTransformer) {

    @Bean
    fun weather() = F<Location, WeatherRaw, WeatherRecord> { stream ->
        stream.mapValues(transformer::toWeatherRecord)
    }

}
