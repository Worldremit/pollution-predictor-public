package com.worldremit.pollution

import com.worldremit.DevData
import com.worldremit.avro.PollutionFeature
import com.worldremit.avro.PollutionRaw
import com.worldremit.avro.PollutionRawData
import com.worldremit.util.randomUuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PollutionRawTransformerTest {

    private val transformer = PollutionRawTransformer()

    @Test
    fun `should transform PollutionRaw to PollutionRecord`() {
        val pollutionRawData = PollutionRawData().apply {
            pm25 = 10.0
        }
        val pollutionRaw = PollutionRaw(randomUuid(), DevData.LOCATION_TIME, pollutionRawData)

        transformer.toPollutionRecord(pollutionRaw).also {
            assertThat(it.features).containsKey(PollutionFeature.PM25.name)
        }

    }

}