package com.worldremit.util

enum class Topic(override val topicName: String) : TopicName {

    MEASUREMENTS_NORMALIZED("measurements-normalized"),
    PREDICTIONS("predictions"),
    FORECASTS("forecasts"),
    PREDICTIONS_VS_ACTUALS("predictions-vs-actuals"),
    CONFUSIONS("confusions"),
    GLOBAL_STATS("global-stats"),
    ACCURACIES("accuracies"),
    MODELS("models"),
    MODEL_DELAY_STATS("model-delay-stats")
}
