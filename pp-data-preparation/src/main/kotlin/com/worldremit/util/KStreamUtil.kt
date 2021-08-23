package com.worldremit.util

import com.worldremit.avro.DeduplicationRecord
import com.worldremit.deduplication.DeduplicationTransformer
import com.worldremit.stats.core.ValueAggregator
import com.worldremit.util.core.BasicTransformer
import com.worldremit.util.core.GenericProcessor
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import java.time.Duration
import java.util.function.Predicate

fun <K, V> KStream<K, V>.filter(predicate: Predicate<V>): KStream<K, V> = filter { _, value -> predicate.test(value) }

fun <K, V> KStream<K, V?>.filterNotNull(): KStream<K, V> = filter { _, value -> value != null }
    .mapValues { _, value -> value!! }

fun <K, V, VR, VF> KGroupedStream<K, V>.aggregateAndMap(
    stateStoreName: String,
    valueAggregator: ValueAggregator<V, VR, VF>,
): KTable<K, VF> = aggregate(stateStoreName, valueAggregator)
    .mapValues(valueAggregator::postMap)

fun <K, V, VR, VF> KGroupedStream<K, V>.aggregate(
    stateStoreName: String,
    valueAggregator: ValueAggregator<V, VR, VF>,
): KTable<K, VR> = aggregate(
    { valueAggregator.init() },
    { _, value, aggregate -> valueAggregator.apply(value, aggregate) },
    Materialized.`as`(stateStoreName)
)

fun <K, V, VR> KStream<K, V>.transform(
    storeName: String,
    block: (ProcessorContext, KeyValueStore<K, V>, K, V) -> VR?,
): KStream<K, VR> = transform({ BasicTransformer(storeName, block) }, storeName)

fun <K, V, VR> KStream<K, V>.transform(
    vararg storeNames: String,
    transformerSupplier: TransformerSupplier<K, V, KeyValue<K, VR>>,
): KStream<K, VR> = transform(transformerSupplier, *storeNames)

fun <K, V, E> KStream<K, V>.deduplicate(storeName: String, keyExtractor: (V) -> E): KStream<K, V> = transform({
    DeduplicationTransformer<K, V, E>(storeName) { _, store, _, value ->
        keyExtractor(value).let {
            when (store.putIfAbsent(it, DeduplicationRecord(true))) {
                null -> value // no duplication => pass through
                else -> null // duplication => if null no key-value pair will be forwarded to down stream
            }
        }
    }
}, storeName)

fun <K, V, KR> KStream<K, V>.selectKey(storeName: String, mapper: ValueMapper<V, KR>): KStream<KR, V> =
    selectKey({ _, v -> mapper.apply(v) }, Named.`as`(storeName))

fun <K, V1, V2, VR> KStream<K, V1>.join(
    otherStream: KStream<K, V2>,
    storeName: String,
    windowDuration: Duration,
    joiner: (V1, V2) -> (VR),
): KStream<K, VR> = join(otherStream, joiner, JoinWindows.of(windowDuration), StreamJoined.`as`(storeName))

fun <K, V, KS, VS> KStream<K, V>.process(
    storeName: String,
    block: (ProcessorContext, KeyValueStore<KS, VS>, K, V) -> Unit,
) = process({ GenericProcessor(storeName, block) }, storeName)
