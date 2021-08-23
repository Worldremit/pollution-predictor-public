package com.worldremit.feature

import com.worldremit.DevData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FeatureServiceTest {

    private val featureService = FeatureService(DevData.FEATURE_FILTER)

    @Test
    fun `find FeaturesDescription by pollution target`() {
        featureService.featuresDescriptionByPollution("PM25").also {
            assertThat(it).isEqualTo(DevData.FEATURES_DESCRIPTION)
        }
    }

}