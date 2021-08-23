package com.worldremit.util

fun <K, V> Map<K, V>.getOrThrow(key: K) = get(key) ?: error("Not found: $key")

fun <K, V> mergeMaps(vararg maps: Map<K, V>, reductionStrategy: (acc: V, V) -> V): Map<K, V> =
    maps.map { it.asSequence() }
        .reduce { a, b -> a + b }
        .groupBy({ it.key }, { it.value })
        .mapValues { it.value.reduce(reductionStrategy) }

fun <K, V> mergeMaps(vararg maps: Map<K, V>): Map<K, V> = mergeMaps(*maps) { v1, _ -> v1 }

/**
 * @return a new map with filtered keys or null if not all required keys exist
 */
fun <K, V> Map<K, V>.filterRequiredKeys(requiredKeys: Set<K>): Map<K, V>? {
    return if (this.keys.containsAll(requiredKeys)) this.filterKeys(requiredKeys::contains) else null
}
