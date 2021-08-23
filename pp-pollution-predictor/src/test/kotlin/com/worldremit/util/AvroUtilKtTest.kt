package com.worldremit.util

import com.worldremit.DevData
import com.worldremit.avro.MeasurementNormalized
import com.worldremit.randomUuid
import org.junit.jupiter.api.Test

internal class AvroUtilKtTest {

    // TODO: change/extend
    @Test
    fun `should check if message is complete`() {

        val measurement = MeasurementNormalized().apply {
            measurementId = randomUuid()
            locationTime = DevData.LOCATION_TIME
            pollutionFeatures = DevData.NORMALIZED_POLLUTION_FEATURES
            weatherFeatures = DevData.NORMALIZED_WEATHER_FEATURES
            oneHotFeatures = DevData.NORMALIZED_ONE_HOT_FEATURES
        }


    }

}