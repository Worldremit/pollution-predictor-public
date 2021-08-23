package com.worldremit.util.core

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver

fun <K, V> TopologyTestDriver.createInputTopic(topicName: String, serdeKey: Serde<K>, serdeValue: Serde<V>): TestInputTopic<K, V> =
        createInputTopic(topicName, serdeKey.serializer(), serdeValue.serializer())

fun <K, V> TopologyTestDriver.createOutputTopic(topicName: String, serdeKey: Serde<K>, serdeValue: Serde<V>): TestOutputTopic<K, V> =
        createOutputTopic(topicName, serdeKey.deserializer(), serdeValue.deserializer())
