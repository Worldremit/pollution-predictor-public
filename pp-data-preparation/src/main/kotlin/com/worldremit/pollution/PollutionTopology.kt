package com.worldremit.pollution

import com.worldremit.avro.Location
import com.worldremit.avro.PollutionRaw
import com.worldremit.avro.PollutionRecord
import com.worldremit.util.F
import com.worldremit.util.Toggle
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@ConditionalOnProperty(value = [Toggle.POLLUTION])
class PollutionTopology(private val transformer: PollutionRawTransformer) {

    @Bean
    fun pollution() = F<Location, PollutionRaw, PollutionRecord> { stream ->
        stream.mapValues(transformer::toPollutionRecord)
    }
}

