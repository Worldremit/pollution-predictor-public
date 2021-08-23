package com.worldremit.util

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.Stores
import org.apache.kafka.streams.state.WindowStore
import java.time.Duration

fun <K : SpecificRecord, V : SpecificRecord> kvStore(storeName: String): StoreBuilder<KeyValueStore<K, V>> =
    Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(storeName),
        createSerde<K>(key = true),
        createSerde<V>(key = false)
    )


