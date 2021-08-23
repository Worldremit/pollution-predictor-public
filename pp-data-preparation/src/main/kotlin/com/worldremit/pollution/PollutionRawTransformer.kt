package com.worldremit.pollution

import com.worldremit.avro.PollutionRaw
import com.worldremit.avro.PollutionRecord
import com.worldremit.util.Toggle
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(value = [Toggle.POLLUTION])
class PollutionRawTransformer {

    fun toPollutionRecord(raw: PollutionRaw) = PollutionRecord().apply {
        features = raw.data.toFeatures()
            .filterValues { it != null && it != 0.0 } // non-zero
            .mapValues { kv -> kv.value!! }
            .filter { kv -> kv.value >= 0.0 } // realistic
        pollutionId = raw.pollutionId
        locationTime = raw.locationTime
    }
}