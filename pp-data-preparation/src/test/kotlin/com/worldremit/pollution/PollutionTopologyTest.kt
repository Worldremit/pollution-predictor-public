package com.worldremit.pollution

import com.worldremit.avro.PollutionRaw
import com.worldremit.avro.PollutionRawData
import com.worldremit.avro.PollutionRecord
import com.worldremit.util.LOCATION_TIME_INIT
import com.worldremit.util.core.TestContext
import com.worldremit.util.Topic
import com.worldremit.util.TopologyUtil.createTopologyFunction
import com.worldremit.util.randomUuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test


internal class PollutionTopologyTest {

    private val testContext = createTopologyFunction(
        function = PollutionTopology(PollutionRawTransformer()).pollution(),
        topicInput = Topic.POLLUTIONS_RAW,
        topicOutput = Topic.POLLUTIONS
    ).let(::TestContext)

    @Test
    fun `should process raw pollution`() {

        val locationTime = LOCATION_TIME_INIT
        val pollutionRaw = PollutionRaw(randomUuid(), locationTime, PollutionRawData())

        // when
        testContext.publish(Topic.POLLUTIONS_RAW, locationTime.location, pollutionRaw)

        // then
        testContext.verify(Topic.POLLUTIONS) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(PollutionRecord::class.java)
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }
}
