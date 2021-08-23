package com.worldremit.weather

import com.worldremit.DevData
import com.worldremit.avro.WeatherRaw
import com.worldremit.avro.WeatherRawData
import com.worldremit.avro.WeatherRecord
import com.worldremit.util.core.TestContext
import com.worldremit.util.Topic
import com.worldremit.util.TopologyUtil.createTopologyFunction
import com.worldremit.util.randomUuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class WeatherTopologyTest {

    private val testContext = createTopologyFunction(
        function = WeatherTopology(WeatherRawTransformer()).weather(),
        topicInput = Topic.WEATHERS_RAW,
        topicOutput = Topic.WEATHERS
    ).let(::TestContext)

    @Test
    fun `should process raw weather`() {

        val weatherRaw = WeatherRaw(randomUuid(), DevData.LOCATION_TIME, WeatherRawData().apply {
            precipitationIntensity = 0.1
        })

        // when
        testContext.publish(Topic.WEATHERS_RAW, DevData.LOCATION, weatherRaw)

        // then
        testContext.verify(Topic.WEATHERS) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(WeatherRecord::class.java)
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }

}