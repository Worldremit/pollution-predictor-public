package com.worldremit.matcher

import com.worldremit.DevData
import com.worldremit.avro.MatchedRecord
import com.worldremit.util.TestContext
import com.worldremit.util.Topic
import com.worldremit.util.TopologyUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class MatcherTopologyTest {
    
    private val testContext = TopologyUtil.createTopologyBiFunctionStreamStream(
        biFunction = MatcherTopology(DevData.MATCHER_SETTINGS).matcher(),
        topicInput1 = Topic.PREDICTIONS,
        topicInput2 = Topic.MEASUREMENTS_NORMALIZED,
        topicOutput = Topic.PREDICTIONS_VS_ACTUALS
    )
        .build()
        .let(::TestContext)

    @Test
    fun `should create confusion record`() {

        // when
        testContext.publish(Topic.PREDICTIONS, DevData.LOCATION, DevData.PREDICTED.transformTime { it.minusDays(1) })
        testContext.publish(Topic.MEASUREMENTS_NORMALIZED, DevData.LOCATION, DevData.MEASUREMENT_NORMALIZED)

        // then
        testContext.verify(Topic.PREDICTIONS_VS_ACTUALS) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(MatchedRecord::class.java)
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }
}