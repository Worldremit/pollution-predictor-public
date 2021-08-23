package com.worldremit.util

import com.worldremit.stats.core.ValueAggregator
import com.worldremit.util.core.GenericProcessor
import com.worldremit.util.core.GenericTransformer
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import java.time.Duration
import java.util.function.Predicate

fun <K : SR, V : SR> KStream<K, V>.filter(predicate: Predicate<V>): KStream<K, V> =
    filter { _, value -> predicate.test(value) }

fun <K : SR, V : SR> KStream<K, V>.filter(storeName: String, predicate: Predicate<V>): KStream<K, V> =
    filter({ _, value -> predicate.test(value) }, Named.`as`(storeName))

fun <K : SR, V : SR, VR : SR, VF : SR> KGroupedStream<K, V>.aggregateAndMap(
    stateStoreName: String,
    valueAggregator: ValueAggregator<V, VR, VF>,
): KTable<K, VF> = aggregate(stateStoreName, valueAggregator)
    .mapValues(valueAggregator::postMap)

fun <K : SR, V : SR, VR : SR, VF : SR> KGroupedStream<K, V>.aggregate(
    stateStoreName: String,
    valueAggregator: ValueAggregator<V, VR, VF>,
): KTable<K, VR> = aggregate(
    { valueAggregator.init() },
    { _, value, aggregate -> valueAggregator.apply(value, aggregate) },
    Materialized.`as`(stateStoreName)
)

fun <K : SR, V : SR, KS : SR, VS : SR, VR : SR> KStream<K, V>.transformValues(
    storeName: String,
    block: (ProcessorContext, KeyValueStore<KS, VS>, K, V) -> VR,
): KStream<K, VR> = transformValues(ValueTransformerWithKeySupplier { GenericTransformer(storeName, block) }, storeName)

fun <K : SR, V : SR, KS : SR, VS : SR> KStream<K, V>.process(
    storeName: String,
    block: (ProcessorContext, KeyValueStore<KS, VS>, K, V) -> Unit,
) = process({ GenericProcessor(storeName, block) }, storeName)

fun <K, V, KR> KStream<K, V>.selectKey(storeName: String, mapper: ValueMapper<V, KR>): KStream<KR, V> =
    selectKey({ _, v -> mapper.apply(v) }, Named.`as`(storeName))

fun <K, V> KStream<K, V>.toTable(storeName: String): KTable<K, V> = toTable(Named.`as`(storeName))

fun <K, V, VT, VR> KStream<K, V>.leftJoin(
    storeName: String,
    table: KTable<K, VT>,
    joiner: ValueJoiner<V, VT, VR>,
): KStream<K, VR> = leftJoin(table, joiner, Joined.`as`(storeName))

fun <K, V, VT, VR> KStream<K, V>.join(
    storeName: String,
    table: KTable<K, VT>,
    joiner: ValueJoiner<V, VT, VR>,
): KStream<K, VR> = join(table, joiner, Joined.`as`(storeName))

fun <K, V, V0, VR> KStream<K, V>.join(
    storeName: String,
    otherStream: KStream<K, V0>,
    windowsDuration: Duration,
    joiner: ValueJoiner<V, V0, VR>,
): KStream<K, VR> = join(otherStream, joiner, JoinWindows.of(windowsDuration), StreamJoined.`as`(storeName))
