package com.worldremit.normalizer

import com.worldremit.DevData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NormalizerServiceTest {

    private val normalizerService = NormalizerService(DevData.APP_SETTINGS)

    @Test
    fun `should normalize measurement and filter out outliers`() {
        normalizerService.normalize(DevData.MEASUREMENT, DevData.MEASUREMENT_STATS_RESULT).apply {
            assertThat(this).isNotNull
            assertThat(weatherFeatures.values).allSatisfy { it < 3.0 }
        }
    }

}