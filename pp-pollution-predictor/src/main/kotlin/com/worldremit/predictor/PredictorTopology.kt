package com.worldremit.predictor

import com.worldremit.avro.*
import com.worldremit.util.*
import mu.KotlinLogging
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
@ConditionalOnProperty(value = [Toggle.PREDICTOR])
class PredictorTopology(
    private val predictionService: PredictionService,
    private val trainingEnd: LocalDateTime
) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun modelsStoreBuilder() = kvStore<LocationTime, KmeansModels>(Store.PREDICTOR_MODELS)

    @Bean
    fun predictor() = BiF<Location, MeasurementNormalized, KmeansModels, Predicted> { measurementStream, modelsStream ->

            modelsStream
                .process(Store.PREDICTOR_MODELS) { _, store: KeyValueStore<Location, KmeansModels>, location, kmeansModels ->
                    val currentModels: KmeansModels? = store.get(location)
                    if (currentModels == null || currentModels.locationTime.timestamp < kmeansModels.locationTime.timestamp) {
                        store.put(location, kmeansModels)
                        kmeansModels.trace(log, Step.PREDICTOR_MODEL_UPDATED) { it.toLogInfo() }
                    }
                }

            measurementStream
                // filter completed measurements first / supported features // dimension reduction
                .transformValues(Store.PREDICTOR_MODELS) { _, store: KeyValueStore<Location, KmeansModels>, location, measurement ->
                    predict(measurement, store.get(location))
                }
                .trace(log, "Joined with model") { it.toLogInfo() }
        }

    private fun predict(measurement: MeasurementNormalized, models: KmeansModels?): Predicted =
        if (models != null && models.isNotEmpty() && measurement.toLocalDateTime().isAfter(trainingEnd)) {
            models.trace(log, "Model found") { it.toLogInfo() }
            predictionService.predict(measurement, models)
        } else {
            measurement.trace(log, "Initializing models") { it.toLogInfo() }
            predictionService.init(measurement)
        }
}
