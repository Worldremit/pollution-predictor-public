package com.worldremit.util

import org.apache.kafka.streams.kstream.KStream
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

typealias F<K, V1, V2> = Function<KStream<K, V1>, KStream<K, V2>>

typealias BiF<K, V1, V2, V3> = BiFunction<KStream<K, V1>, KStream<K, V2>, KStream<K, V3>>
