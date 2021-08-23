package com.worldremit.deduplication

import com.worldremit.avro.*
import com.worldremit.util.*
import com.worldremit.util.core.TestContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class WeatherDeduplicationTopologyTest {
    private val testContext = WeatherDeduplicationTopology().let {
        TopologyUtil.createTopologyFunction(
            function = it.weatherDeduplication(),
            topicInput = Topic.WEATHERS_RAW,
            topicOutput = Topic.WEATHERS_DEDUPLICATED,
            storeBuilder = it.weatherDeduplicationStoreBuilder()
        )
    }.let(::TestContext)

    @Test
    fun `should pass first event`() {
        // when
        testContext.publish(Topic.WEATHERS_RAW, LOCATION_INIT, WEATHER_RAW)

        // then
        testContext.verify(Topic.WEATHERS_DEDUPLICATED) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(WeatherRaw::class.java)
        }
    }

    @Test
    fun `should deduplicate the same location-time events`() {
        // given
        testContext.driver().getKeyValueStore<LocationTime, DeduplicationRecord>(Store.DEDUPLICATION_WEATHER)
            .apply {
                put(LOCATION_TIME_1, DeduplicationRecord(true))
            }

        // when
        testContext.publish(Topic.WEATHERS_RAW, LOCATION_INIT, WEATHER_RAW)

        // then
        testContext.verifyNoRecords(Topic.WEATHERS_DEDUPLICATED)
    }

    @Test
    fun `should pass events with different location-time`() {

        val id1 = randomUuid()
        val id2 = randomUuid()

        val weatherRaw1 = WeatherRaw(id1, LOCATION_TIME_1, WeatherRawData())
        val weatherRaw2 = WeatherRaw(id2, LOCATION_TIME_2, WeatherRawData())

        // when
        testContext.publish(Topic.WEATHERS_RAW, LOCATION_INIT, weatherRaw1)
        testContext.publish(Topic.WEATHERS_RAW, LOCATION_INIT, weatherRaw2)

        // then
        testContext.verify(Topic.WEATHERS_DEDUPLICATED) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(WeatherRaw::class.java)
            assertThat((it.value as WeatherRaw).weatherId).isEqualTo(id1)
        }

        testContext.verify(Topic.WEATHERS_DEDUPLICATED) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(WeatherRaw::class.java)
            assertThat((it.value as WeatherRaw).weatherId).isEqualTo(id2)
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }

    companion object {
        val LOCATION_TIME_1 = LocationTime(LOCATION_INIT, LocalDateTime.now().toTimestamp())
        val LOCATION_TIME_2 = LocationTime(LOCATION_INIT, LocalDateTime.now().plusHours(1).toTimestamp())
        val WEATHER_RAW = WeatherRaw(randomUuid(), LOCATION_TIME_1, WeatherRawData())
    }

}