package com.worldremit.util.core

import org.apache.kafka.streams.kstream.ValueTransformerWithKey
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

class GenericTransformer<K, V, VR, KS, VS>(
    private val storeName: String,
    private val block: (ProcessorContext, KeyValueStore<KS, VS>, K, V) -> VR,
) : ValueTransformerWithKey<K, V, VR> {

    private lateinit var context: ProcessorContext
    private lateinit var store: KeyValueStore<KS, VS>

    override fun transform(key: K, value: V): VR = block.invoke(context, store, key, value)

    override fun init(context: ProcessorContext) {
        this.context = context
        this.store = context.getStateStore(storeName) as KeyValueStore<KS, VS>
    }

    override fun close() {
        // do nothing
    }

}