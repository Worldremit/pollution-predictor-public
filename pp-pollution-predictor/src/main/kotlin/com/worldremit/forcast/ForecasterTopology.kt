package com.worldremit.forcast

import com.worldremit.avro.ConfusionRecord
import com.worldremit.avro.Forecast
import com.worldremit.avro.Location
import com.worldremit.util.F
import com.worldremit.util.Toggle
import com.worldremit.util.filter
import com.worldremit.util.isAfter
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
@ConditionalOnProperty(value = [Toggle.FORECASTER])
class ForecasterTopology(
    private val trainingEnd: LocalDateTime,
) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun forecaster() =
        F<Location, ConfusionRecord, Forecast> { stream ->
            stream
                .filter { confusion -> confusion.locationTime.isAfter(trainingEnd) }
                .mapValues { confusion -> confusion.toForecast() }
        }
}
