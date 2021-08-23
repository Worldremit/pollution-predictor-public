package com.worldremit.predictor

import com.worldremit.DevData
import com.worldremit.DevData.CLUSTER_SETTINGS
import com.worldremit.cluster.SearcherFactory
import org.junit.jupiter.api.Test

internal class PredictionServiceTest {

    private val predictorService = CLUSTER_SETTINGS
        .let(::SearcherFactory)
        .let(::PredictionService)

    @Test
    fun `should predict initial`() {
        predictorService.init(DevData.MEASUREMENT_NORMALIZED)
    }

    @Test
    fun `should predict`() {
        predictorService.predict(DevData.MEASUREMENT_NORMALIZED, DevData.MODELS)
    }

}