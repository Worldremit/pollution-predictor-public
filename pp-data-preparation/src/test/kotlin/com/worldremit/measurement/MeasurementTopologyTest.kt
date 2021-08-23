package com.worldremit.measurement

import com.worldremit.DevData
import com.worldremit.DevData.copy
import com.worldremit.avro.LocationTime
import com.worldremit.avro.Measurement
import com.worldremit.util.Topic
import com.worldremit.util.TopologyUtil
import com.worldremit.util.core.TestContext
import com.worldremit.util.toTimestamp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class MeasurementTopologyTest {

    private val testContext = TopologyUtil.createTopologyBiFunctionStreamStream(
        biFunction = MeasurementTopology().measurement(),
        topicInput1 = Topic.WEATHERS,
        topicInput2 = Topic.POLLUTIONS,
        topicOutput = Topic.MEASUREMENTS
    ).let(::TestContext)


    @Test
    fun `should join weather and pollution records if they match time and location`() {

        // when
        testContext.publish(Topic.WEATHERS, DevData.LOCATION, DevData.WEATHER_RECORD)
        testContext.publish(Topic.POLLUTIONS, DevData.LOCATION, DevData.POLLUTION_RECORD)

        // then
        testContext.verify(Topic.MEASUREMENTS) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(Measurement::class.java)
        }
    }

    @Test
    fun `should not join weather and pollution records unless they match time and location`() {

        val pollutionRecord = DevData.POLLUTION_RECORD.copy {
            it.locationTime = LocationTime(DevData.LOCATION, LocalDateTime.now().plusHours(1).toTimestamp())
        }

        // when
        testContext.publish(Topic.WEATHERS, DevData.LOCATION, DevData.WEATHER_RECORD)
        testContext.publish(Topic.POLLUTIONS, DevData.LOCATION, pollutionRecord)

        // then
        testContext.verifyNoRecords(Topic.MEASUREMENTS)
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }
}