package com.worldremit.confusion

import com.worldremit.DevData
import com.worldremit.DevData.NORMS
import com.worldremit.avro.ConfusionRecord
import com.worldremit.util.TestContext
import com.worldremit.util.Topic
import com.worldremit.util.TopologyUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class ConfusionTopologyTest {

//    FeatureService(DevData.FEATURE_FILTER)
    private val confusionService = ConfusionService(NORMS)
    private val testContext = TopologyUtil.createTopologyBiFunctionStreamStream(
        biFunction = ConfusionTopology(confusionService, DevData.TRAINING_END).confusion(),
        topicInput1 = Topic.PREDICTIONS_VS_ACTUALS,
        topicInput2 = Topic.GLOBAL_STATS,
        topicOutput = Topic.CONFUSIONS
    )
        .build()
        .let(::TestContext)

    @Test
    fun `should create confusion record`() {

        // when
        testContext.publish(Topic.GLOBAL_STATS, DevData.LOCATION, DevData.MEASUREMENT_STATS_RESULT)
        testContext.publish(Topic.PREDICTIONS_VS_ACTUALS, DevData.LOCATION, DevData.MATCHED_RECORD)

        // then
        testContext.verify(Topic.CONFUSIONS) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(ConfusionRecord::class.java)
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }

}