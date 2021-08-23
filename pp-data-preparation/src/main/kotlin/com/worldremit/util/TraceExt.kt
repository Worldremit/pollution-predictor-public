package com.worldremit.util

import mu.KLogger
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.kstream.KStream

fun <K, V : SpecificRecord> KStream<K, V>.trace(log: KLogger, message: String, valueTransformer: (V) -> Any?): KStream<K, V> =
    peek { _, value -> value.trace(log, message, valueTransformer) }

fun <V: SpecificRecord> V.trace(log: KLogger, message: String, valueTransformer: (V) -> Any?) {
    log.debug("Schema=${schema.name}, ${valueTransformer.invoke(this)}, $message")
}