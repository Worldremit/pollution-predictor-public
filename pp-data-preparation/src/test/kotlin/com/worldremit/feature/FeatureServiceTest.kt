package com.worldremit.feature

import com.worldremit.DevData
import com.worldremit.avro.MeasurementNormalized
import com.worldremit.util.LOCATION_TIME_INIT
import com.worldremit.util.randomUuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FeatureServiceTest {

    private val featureService = FeatureService(DevData.FEATURE_FILTER)

    @Test
    fun `should reduce dimension for measurement`() {

        val measurementNormalized = MeasurementNormalized().apply {
            measurementId = randomUuid()
            locationTime = LOCATION_TIME_INIT
            pollutionFeatures = DevData.NORMALIZED_POLLUTION_FEATURES
            weatherFeatures = DevData.NORMALIZED_WEATHER_FEATURES
            oneHotFeatures = DevData.NORMALIZED_ONE_HOT_FEATURES
        }

        val reduced = featureService.reduceDimensions(measurementNormalized)
        assertThat(reduced).isNotNull
        assertThat(reduced!!.weatherFeatures.keys)
            .containsOnly(*DevData.FEATURE_FILTER.merge().weather.map { it.name }.toTypedArray())
    }

    @Test
    fun `should filter out measurement with incomplete list of features`() {

        val measurementNormalized = MeasurementNormalized().apply {
            measurementId = randomUuid()
            locationTime = LOCATION_TIME_INIT
            pollutionFeatures = DevData.NORMALIZED_POLLUTION_FEATURES
            weatherFeatures = DevData.NORMALIZED_WEATHER_FEATURES
            oneHotFeatures = mapOf()
        }

        featureService.reduceDimensions(measurementNormalized).also {
            assertThat(it).isNull()
        }

    }


}