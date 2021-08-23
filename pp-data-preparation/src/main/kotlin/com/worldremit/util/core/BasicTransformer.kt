package com.worldremit.util.core

import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

class BasicTransformer<K, V, VR>(storeName: String, block: (ProcessorContext, KeyValueStore<K, V>, K, V) -> VR?) :
    ForeignKeyTransformer<K, V, VR, K, V>(storeName, block)