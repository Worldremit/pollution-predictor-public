package com.worldremit.util

import com.worldremit.util.core.TopicName
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.WindowStore
import java.util.function.BiFunction
import java.util.function.Function

object TopologyUtil {

    fun <K1, V1, K2, V2> createTopologyFunction(
        function: Function<KStream<K1, V1>, KStream<K2, V2>>,
        topicInput: TopicName,
        topicOutput: TopicName,
    ): Topology = StreamsBuilder().apply {
        stream<K1, V1>(topicInput.topicName).apply {
            function.apply(this).apply { to(topicOutput.topicName) }
        }
    }.build()

    fun <K1, V1, K2, V2, SK, SV> createTopologyFunction(
        function: Function<KStream<K1, V1>, KStream<K2, V2>>,
        topicInput: TopicName,
        topicOutput: TopicName,
        storeBuilder: StoreBuilder<KeyValueStore<SK, SV>>,
    ): Topology = StreamsBuilder().apply {
        addStateStore(storeBuilder)
        stream<K1, V1>(topicInput.topicName).apply {
            function.apply(this).apply { to(topicOutput.topicName) }
        }
    }.build()

    fun <K, V1, V2, VR, VSW, VSK> createTopologyBiFunctionStreamStream(
        biFunction: BiFunction<KStream<K, V1>, KStream<K, V2>, KStream<K, VR>>,
        topicInput1: TopicName,
        topicInput2: TopicName,
        topicOutput: TopicName,
        windowStoreBuilder: StoreBuilder<WindowStore<K, VSW>>,
        kvStoreBuilder: StoreBuilder<KeyValueStore<K, VSK>>,
    ): Topology = StreamsBuilder().apply {
        addStateStore(windowStoreBuilder)
        addStateStore(kvStoreBuilder)
        val stream1 = stream<K, V1>(topicInput1.topicName)
        val stream2 = stream<K, V2>(topicInput2.topicName)
        biFunction.apply(stream1, stream2).apply { to(topicOutput.topicName) }
    }.build()

    fun <K, V1, V2, VR> createTopologyBiFunctionStreamStream(
        biFunction: BiFunction<KStream<K, V1>, KStream<K, V2>, KStream<K, VR>>,
        topicInput1: TopicName,
        topicInput2: TopicName,
        topicOutput: TopicName,
    ): Topology = StreamsBuilder().apply {
        val stream1 = stream<K, V1>(topicInput1.topicName)
        val stream2 = stream<K, V2>(topicInput2.topicName)
        biFunction.apply(stream1, stream2).apply { to(topicOutput.topicName) }
    }.build()


}