package com.worldremit.util.core

import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

class GenericProcessor<K, V, KS, VS>(
    private val storeName: String,
    private val block: (ProcessorContext, KeyValueStore<KS, VS>, K, V) -> Unit,
) : Processor<K, V> {

    private lateinit var context: ProcessorContext
    private lateinit var store: KeyValueStore<KS, VS>

    override fun process(key: K, value: V) = block.invoke(context, store, key, value)

    override fun init(context: ProcessorContext) {
        this.context = context
        this.store = context.getStateStore(storeName) as KeyValueStore<KS, VS>
    }

    override fun close() {
        // do nothing
    }

}

