package com.worldremit.predictor

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.worldremit.DevData
import com.worldremit.avro.KmeansModels
import com.worldremit.avro.Location
import com.worldremit.avro.MeasurementNormalized
import com.worldremit.avro.Predicted
import com.worldremit.util.TestContext
import com.worldremit.util.Topic
import org.apache.kafka.streams.StreamsBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class PredictorTopologyTest {

    private val predictorService = mock<PredictionService> {
        on { predict(any(), any()) } doReturn DevData.PREDICTED
        on { init(any()) } doReturn DevData.PREDICTED_INIT
    }

    private val trainingEnd = LocalDateTime.now().minusHours(1)

    private val testContext = StreamsBuilder().apply {
        val predictorTopology = PredictorTopology(predictorService, trainingEnd)
        addStateStore(predictorTopology.modelsStoreBuilder())
        val stream1 = stream<Location, MeasurementNormalized>(Topic.MEASUREMENTS_NORMALIZED.topicName)
        val stream2 = stream<Location, KmeansModels>(Topic.MODELS.topicName)
        predictorTopology.predictor().apply(stream1, stream2).apply { to(Topic.PREDICTIONS.topicName) }
    }
        .build()
        .let(::TestContext)

    @Test
    fun `should predict`() {
        // when
        testContext.publish(Topic.MODELS, DevData.LOCATION, DevData.MODELS)

        testContext.publish(Topic.MEASUREMENTS_NORMALIZED, DevData.LOCATION, DevData.MEASUREMENT_NORMALIZED)

        // then
        testContext.verify(Topic.PREDICTIONS) {
            assertThat(it).isNotNull
            assertThat(it.value).isInstanceOf(Predicted::class.java)
        }
    }

    @AfterEach
    fun cleanUp() {
        testContext.cleanUp()
    }


}