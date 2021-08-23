package com.worldremit.deduplication

import com.worldremit.avro.DeduplicationRecord
import com.worldremit.avro.LocationTime
import com.worldremit.avro.PollutionRaw
import com.worldremit.avro.PollutionRawData
import com.worldremit.util.*
import com.worldremit.util.TopologyUtil.createTopologyFunction
import com.worldremit.util.core.TestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class PollutionDeduplicationTopologyTest {

    private val testContext = PollutionDeduplicationTopology().let {
        createTopologyFunction(
            function = it.pollutionDeduplication(),
            topicInput = Topic.POLLUTIONS_RAW,
            topicOutput = Topic.POLLUTIONS_DEDUPLICATED,
            storeBuilder = it.pollutionDeduplicationStoreBuilder()
        )
    }.let(::TestContext)

    @Test
    fun `should pass first event`() {
        // when
        testContext.publish(Topic.POLLUTIONS_RAW, LOCATION_INIT, POLLUTION_RAW)

        // then
        testContext.verify(Topic.POLLUTIONS_DEDUPLICATED) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(PollutionRaw::class.java)
        }
    }

    @Test
    fun `should deduplicate the same location-time events`() {
        // given
        testContext.driver().getKeyValueStore<LocationTime, DeduplicationRecord>(Store.DEDUPLICATION_POLLUTION)
            .apply {
                put(LOCATION_TIME_1, DeduplicationRecord(true))
            }

        // when
        testContext.publish(Topic.POLLUTIONS_RAW, LOCATION_INIT, POLLUTION_RAW)

        // then
        testContext.verifyNoRecords(Topic.POLLUTIONS_DEDUPLICATED)
    }

    @Test
    fun `should pass events with different location-time`() {

        val id1 = randomUuid()
        val id2 = randomUuid()

        val pollutionRaw1 = PollutionRaw(id1, LOCATION_TIME_1, PollutionRawData())
        val pollutionRaw2 = PollutionRaw(id2, LOCATION_TIME_2, PollutionRawData())

        // when
        testContext.publish(Topic.POLLUTIONS_RAW, LOCATION_INIT, pollutionRaw1)
        testContext.publish(Topic.POLLUTIONS_RAW, LOCATION_INIT, pollutionRaw2)

        // then
        testContext.verify(Topic.POLLUTIONS_DEDUPLICATED) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(PollutionRaw::class.java)
            assertThat((it.value as PollutionRaw).pollutionId).isEqualTo(id1)
        }

        testContext.verify(Topic.POLLUTIONS_DEDUPLICATED) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(PollutionRaw::class.java)
            assertThat((it.value as PollutionRaw).pollutionId).isEqualTo(id2)
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }

    companion object {
        val LOCATION_TIME_1 = LocationTime(LOCATION_INIT, LocalDateTime.now().toTimestamp())
        val LOCATION_TIME_2 = LocationTime(LOCATION_INIT, LocalDateTime.now().plusHours(1).toTimestamp())
        val POLLUTION_RAW = PollutionRaw(randomUuid(), LOCATION_TIME_1, PollutionRawData())
    }

}