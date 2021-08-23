package com.worldremit.util.core

import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

open class ForeignKeyTransformer<K, V, VR, KS, VS>(
    private val storeName: String,
    private val block: (ProcessorContext, KeyValueStore<KS, VS>, K, V) -> VR?,
) : Transformer<K, V, KeyValue<K, VR>> {

    private lateinit var context: ProcessorContext
    private lateinit var store: KeyValueStore<KS, VS>

    override fun transform(key: K, value: V): KeyValue<K, VR>? =
        block.invoke(context, store, key, value)?.let { KeyValue(key, it) }

    override fun init(context: ProcessorContext) {
        this.context = context
        this.store = context.getStateStore(storeName) as KeyValueStore<KS, VS>
    }

    override fun close() {
        // do nothing
    }

}