package com.worldremit.measurement

import com.worldremit.DevData
import com.worldremit.DevData.APP_SETTINGS
import com.worldremit.DevData.TRAINING_CONFIG
import com.worldremit.feature.FeatureService
import com.worldremit.normalizer.NormalizerService
import com.worldremit.normalizer.NormalizerTopology
import com.worldremit.util.core.TestContext
import com.worldremit.util.Topic
import com.worldremit.util.TopologyUtil
import com.worldremit.util.loadConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class NormalizerTopologyTest {

    private val featureService = FeatureService(loadConfig("features"))
    private val testContext = NormalizerTopology(NormalizerService(APP_SETTINGS), featureService, TRAINING_CONFIG).let {
        TopologyUtil.createTopologyBiFunctionStreamStream(
            biFunction = it.normalizer(),
            topicInput1 = Topic.MEASUREMENTS,
            topicInput2 = Topic.GLOBAL_STATS,
            topicOutput = Topic.MEASUREMENTS_NORMALIZED,
            windowStoreBuilder = it.normalizerMeasurementsStoreBuilder(),
            kvStoreBuilder = it.normalizerStatsStoreBuilder(),
        ).let(::TestContext)
    }

    @Test
    fun `should not publish MeasurementNormalized when consumed training measurement and training stats`() {

        // when
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_TRAINING1)
        testContext.publish(Topic.GLOBAL_STATS, DevData.LOCATION, DevData.GLOBAL_STATS_TRAINING1)

        // then
        testContext.verifyNoRecords(Topic.MEASUREMENTS_NORMALIZED)
    }

    @Test
    fun `should not publish MeasurementNormalized when consumed regular measurement and training stats`() {

        // when
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_REGULAR)
        testContext.publish(Topic.GLOBAL_STATS, DevData.LOCATION, DevData.GLOBAL_STATS_TRAINING1)

        // then
        testContext.verifyNoRecords(Topic.MEASUREMENTS_NORMALIZED)
    }

    @Test
    fun `should not publish MeasurementNormalized when consumed training measurement and training-end stats`() {

        // when
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_TRAINING1)
        testContext.publish(Topic.GLOBAL_STATS, DevData.LOCATION, DevData.GLOBAL_STATS_TRAINING_END)

        // then
        testContext.verifyNoRecords(Topic.MEASUREMENTS_NORMALIZED)
    }

    @Test
    fun `should not publish saved MeasurementNormalized when consumed regular measurement only`() {

        // when
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_REGULAR)

        // then
        testContext.verifyNoRecords(Topic.MEASUREMENTS_NORMALIZED)
    }

    @Test
    fun `should not publish saved MeasurementNormalized when consumed training measurement`() {

        // when
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_TRAINING1)

        // then
        testContext.verifyNoRecords(Topic.MEASUREMENTS_NORMALIZED)
    }

    @Test
    fun `should publish saved MeasurementNormalized when consumed training-end stats and a measurement`() {

        // when
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_TRAINING1)
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_TRAINING2)
        testContext.publish(Topic.GLOBAL_STATS, DevData.LOCATION, DevData.GLOBAL_STATS_TRAINING_END)

        // then
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_TRAINING_END)

        // then
        testContext.verifyAll(Topic.MEASUREMENTS_NORMALIZED) {
            assertThat(it).hasSize(3)
        }
    }

    @Test
    fun `should publish MeasurementNormalized when consumed measurement after training-end stats`() {

        // when
        testContext.publish(Topic.GLOBAL_STATS, DevData.LOCATION, DevData.GLOBAL_STATS_TRAINING_END)
        testContext.publish(Topic.MEASUREMENTS, DevData.LOCATION, DevData.MEASUREMENT_TRAINING_END)

        // then
        testContext.verifyAll(Topic.MEASUREMENTS_NORMALIZED) { records ->
            assertThat(records).hasSize(1)
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }
}