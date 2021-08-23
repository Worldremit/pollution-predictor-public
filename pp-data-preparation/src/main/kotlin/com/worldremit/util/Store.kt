package com.worldremit.util

object Store {
    const val DEDUPLICATION_POLLUTION = "pollution-deduplication-store"
    const val DEDUPLICATION_WEATHER = "weather-deduplication-store"

    const val POLLUTION_STATS = "pollution-stats-store"
    const val WEATHER_STATS = "weather-stats-store"

    const val MEASUREMENT_JOIN = "measurement-join-store"
    const val WEATHER_REKEY = "weather-rekey-store"
    const val POLLUTION_REKEY = "pollution-rekey-store"
    const val MEASUREMENT_REKEY = "measurement-rekey-store"

    const val MEASUREMENT_STATS = "measurement-stats-store"
    const val CORRELATION = "correlation-store" // TODO: will be together with measurement-stats

    const val NORMALIZER_STATS = "normalizer-stats-store"
    const val MEASUREMENTS_TRAINING = "measurement-training-store"

    const val GLOBAL_STATS = "global-stats-store"

}