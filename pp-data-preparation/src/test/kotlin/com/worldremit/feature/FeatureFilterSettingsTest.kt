package com.worldremit.feature

import com.worldremit.avro.OneHotFeature
import com.worldremit.avro.PollutionFeature
import com.worldremit.avro.WeatherFeature
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FeatureFilterSettingsTest {

    @Test
    fun `should merge filter and filterNot`() {

        val featureFilterSettings = FeatureFilterSettings(
            filter = Features(
                weather = listOf(WeatherFeature.TEMPERATURE, WeatherFeature.PRESSURE),
                pollution = listOf(PollutionFeature.PM25, PollutionFeature.PM10),
                oneHot = listOf(OneHotFeature.SEASON)
            ),
            filterNot = Features(
                weather = listOf(WeatherFeature.PRESSURE),
                pollution = listOf(),
                oneHot = listOf(OneHotFeature.DAY_NIGHT)
            )
        )

        val features = featureFilterSettings.merge()

        assertThat(features.weather).containsExactly(WeatherFeature.TEMPERATURE)
        assertThat(features.pollution).containsExactly(PollutionFeature.PM25, PollutionFeature.PM10)
        assertThat(features.oneHot).containsExactly(OneHotFeature.SEASON)
    }

}