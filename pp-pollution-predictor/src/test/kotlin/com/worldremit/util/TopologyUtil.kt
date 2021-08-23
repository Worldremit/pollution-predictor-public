package com.worldremit.util

import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import java.util.function.BiFunction
import java.util.function.Function

object TopologyUtil {

    fun <K1, V1, K2, V2> createTopologyFunction(
            function: Function<KStream<K1, V1>, KStream<K2, V2>>,
            topicInput: TopicName,
            topicOutput: TopicName
    ): Topology = StreamsBuilder().apply {
        stream<K1, V1>(topicInput.topicName).apply {
            function.apply(this).apply { to(topicOutput.topicName) }
        }
    }.build() // TODO: change this

    fun <K1, V1, K2, V2, K3, V3> createTopologyBiFunctionStreamStream(
            biFunction: BiFunction<KStream<K1, V1>, KStream<K2, V2>, KStream<K3, V3>>,
            topicInput1: TopicName,
            topicInput2: TopicName,
            topicOutput: TopicName
    ): StreamsBuilder = StreamsBuilder().apply {
        val stream1 = stream<K1, V1>(topicInput1.topicName)
        val stream2 = stream<K2, V2>(topicInput2.topicName)
        biFunction.apply(stream1, stream2).apply { to(topicOutput.topicName) }
    }

}