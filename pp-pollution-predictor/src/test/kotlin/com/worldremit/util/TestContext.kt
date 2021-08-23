package com.worldremit.util

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.StreamsConfig.*
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.TestRecord
import org.assertj.core.api.Assertions.assertThat

class TestContext(topology: Topology) {

    private val testDriver = TopologyTestDriver(topology, CONFIG)
    private val inputTopics = initInputTopics()
    private val outputTopics = initOutputTopics()

    fun publish(topic: TopicName, key: SpecificRecord, value: SpecificRecord) =
            inputTopics.getOrThrow(topic).pipeInput(key, value)

    private fun consume(topic: TopicName): TestRecord<SpecificRecord, SpecificRecord> =
            outputTopics.getOrThrow(topic)
                    .also {
                        assertThat(it.isEmpty)
                                .withFailMessage("Topic ${topic.topicName} is empty")
                                .isFalse
                    }
                    .readRecord()

    fun verify(topic: TopicName, block: (TestRecord<SpecificRecord, SpecificRecord>) -> Unit) {
        block.invoke(consume(topic))
    }

//    fun driver(): TopologyTestDriver = testDriver

    fun cleanUp() = testDriver.close()

    private fun initInputTopics() = Topic.values()
            .associate { topic: TopicName -> topic to testDriver.createInputTopic(topic.topicName, SERDE_KEY, SERDE_VALUE) }

    private fun initOutputTopics() = Topic.values()
            .associate { topic: TopicName -> topic to testDriver.createOutputTopic(topic.topicName, SERDE_KEY, SERDE_VALUE) }

    companion object {

        private const val SCHEMA_REGISTRY_URL = "mock://"
        private val CONFIG = mapOf(
                APPLICATION_ID_CONFIG to "testId",
                BOOTSTRAP_SERVERS_CONFIG to "dummy:1234",
                DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG to CustomTimestampExtractor::class.java.name,
                DEFAULT_KEY_SERDE_CLASS_CONFIG to SpecificAvroSerde::class.java.name,
                DEFAULT_VALUE_SERDE_CLASS_CONFIG to SpecificAvroSerde::class.java.name,
                SCHEMA_REGISTRY_URL_CONFIG to SCHEMA_REGISTRY_URL,
        ).toProperties()

        private val SERDE_KEY: Serde<SpecificRecord> = createSerde(key = true, SCHEMA_REGISTRY_URL)
        private val SERDE_VALUE: Serde<SpecificRecord> = createSerde(key = false, SCHEMA_REGISTRY_URL)
    }
}
