package com.worldremit.util

fun <K, V> Map<K, V>.getOrThrow(key: K) = get(key) ?: error("Not found: $key")

/**
 * Match to other map and transform values for matching keys
 */
fun <K, V1, V2, VR> Map<K, V1>.matchAndTransform(
    otherMap: Map<K, V2>,
    failIfMissing: Boolean = true,
    transformer: (V1, V2) -> (VR),
): Map<K, VR> = if (failIfMissing) {
    mapValues { entry1 -> otherMap.getOrThrow(entry1.key).let { transformer.invoke(entry1.value, it) } }
} else {
    mapNotNull { entry1 -> otherMap[entry1.key]?.let { entry1.key to transformer.invoke(entry1.value, it) } }.toMap()
}
