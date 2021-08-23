package com.worldremit.cluster

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.worldremit.DevData
import com.worldremit.DevData.FEATURE_FILTER
import com.worldremit.avro.KmeansModels
import com.worldremit.feature.FeatureService
import com.worldremit.util.TestContext
import com.worldremit.util.Topic
import com.worldremit.util.TopologyUtil.createTopologyFunction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class ClustererTopologyTest {

    private val clusterService = mock<ClusterService> {
        on { initModel(any(), any()) } doReturn DevData.MODEL
        on { clusterMeasurement(any(), any()) } doReturn DevData.MODEL
    }

    private val clusterAggregator = ClustererAggregator(clusterService)

    private val testContext = createTopologyFunction(
        function = ClustererTopology(clusterAggregator).clusterer(),
        topicInput = Topic.PREDICTIONS_VS_ACTUALS,
        topicOutput = Topic.MODELS
    ).let(::TestContext)

    @Test
    fun `should cluster measurement`() {

        // when
        testContext.publish(Topic.PREDICTIONS_VS_ACTUALS, DevData.LOCATION, DevData.MATCHED_RECORD)

        // then
        testContext.verify(Topic.MODELS) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(KmeansModels::class.java)
            (it.value as KmeansModels).also { m ->
                assertThat(m.models).isNotEmpty
            }
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }

}