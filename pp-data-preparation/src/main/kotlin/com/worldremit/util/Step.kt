package com.worldremit.util

object Step {

    const val MEASUREMENT_JOINED = "Joined weather and pollution records"

    const val NORMALIZER_NEW_STATS = "New stats saved to state store"
    const val NORMALIZER_NORMALIZED = "Measurement was normalized"
    const val NORMALIZER_COMPLETED = "Measurement contains required features"

    const val NORMALIZER_TRAINING_FLOW = "Training flow, saving measurement to training store"
    const val NORMALIZER_TRAINING_FLOW_CLOSING = "Training flow, closing"
    const val NORMALIZER_REGULAR_FLOW = "Regular flow, passing measurement through"

}
