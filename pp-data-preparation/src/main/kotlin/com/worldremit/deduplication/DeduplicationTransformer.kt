package com.worldremit.deduplication

import com.worldremit.avro.DeduplicationRecord
import com.worldremit.util.core.ForeignKeyTransformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

class DeduplicationTransformer<K, V, E>(
    storeName: String,
    block: (ProcessorContext, KeyValueStore<E, DeduplicationRecord>, K, V) -> V?,
) : ForeignKeyTransformer<K, V, V, E, DeduplicationRecord>(storeName, block)