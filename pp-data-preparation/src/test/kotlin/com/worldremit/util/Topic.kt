package com.worldremit.util

import com.worldremit.util.core.TopicName

enum class Topic(override val topicName: String) : TopicName {

    WEATHERS_RAW("weathers-raw"),
    POLLUTIONS_RAW("pollutions-raw"),

    WEATHERS_DEDUPLICATED("weathers-deduplicated"),
    POLLUTIONS_DEDUPLICATED("pollutions-deduplicated"),

    WEATHERS_MEAN_STDDEV("weathers-mean-stddev"),
    POLLUTIONS_MEAN_STDDEV("pollutions-mean-stddev"),

    WEATHERS("weathers"),
    POLLUTIONS("pollutions"),

    MEASUREMENTS("measurements"),
    GLOBAL_STATS("global-stats"),
    MEASUREMENT_STATS("measurement-stats"),
    MEASUREMENTS_NORMALIZED("measurements-normalized"),

    CORRELATIONS_PM25_PM10("correlations-pm25-pm10"),
    CORRELATIONS_TEMP_PRESSURE("correlations-temp-pressure"),
    CORRELATIONS_PM25_PRESSURE("correlations-pm25-pressure"),

}