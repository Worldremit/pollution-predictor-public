package com.worldremit.weather

import assertk.assertThat
import assertk.assertions.contains
import com.worldremit.avro.WeatherFeature
import com.worldremit.avro.WeatherRawData
import org.junit.jupiter.api.Test

internal class WeatherUtilKtTest {

    @Test
    fun `should transform WeatherRawData to map`() {
        val weatherRawData = WeatherRawData().apply {
            temperature = 10.0
        }

        weatherRawData.toFeatures().also {
            assertThat(it).contains(WeatherFeature.TEMPERATURE.name to 10.0)
        }

    }
}