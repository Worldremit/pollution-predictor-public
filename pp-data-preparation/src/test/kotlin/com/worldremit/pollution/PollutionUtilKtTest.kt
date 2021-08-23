package com.worldremit.pollution

import assertk.assertThat
import assertk.assertions.contains
import com.worldremit.avro.PollutionFeature
import com.worldremit.avro.PollutionRawData
import org.junit.jupiter.api.Test

internal class PollutionUtilKtTest {

    @Test
    fun `should transform PollutionRawData to map`() {
        val pollutionRawData = PollutionRawData().apply {
            pm25 = 10.0
        }

        pollutionRawData.toFeatures().also {
            assertThat(it).contains(PollutionFeature.PM25.name to 10.0)
        }

    }
}