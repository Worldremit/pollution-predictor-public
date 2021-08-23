package com.worldremit.normalizer

import com.worldremit.avro.*
import com.worldremit.config.TrainingTimestampConfig
import com.worldremit.feature.FeatureService
import com.worldremit.util.*
import mu.KotlinLogging
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.WindowStore
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(value = [Toggle.NORMALIZER])
class NormalizerTopology(
    private val normalizerService: NormalizerService,
    private val featureService: FeatureService,
    private val config: TrainingTimestampConfig,
) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun normalizerMeasurementsStoreBuilder(): StoreBuilder<WindowStore<Location, Measurement>> =
        windowStore(Store.MEASUREMENTS_TRAINING, config.toDuration())

    @Bean
    fun normalizerStatsStoreBuilder(): StoreBuilder<KeyValueStore<Location, NormalizerStats>> =
        kvStore(Store.NORMALIZER_STATS)

    @Bean
    fun normalizer() =
        BiF<Location, Measurement, MeasurementStatsResult, MeasurementNormalized> { streamMeasurement, statsStream ->

            statsStream
                .process(Store.NORMALIZER_STATS) { _, store: KeyValueStore<Location, NormalizerStats>, location, stats ->
                    store.put(location, NormalizerStats(stats, false)).also {
                        stats.trace(log, Step.NORMALIZER_NEW_STATS) { it.toLogInfo() }
                    }
                }

            streamMeasurement
                .transform(Store.MEASUREMENTS_TRAINING, Store.NORMALIZER_STATS) {
                    NormalizerTransformer(normalizerService, config)
                }
                .trace(log, Step.NORMALIZER_NORMALIZED) { it.toLogInfo() }
                .mapValues { measurement -> featureService.reduceDimensions(measurement) }
                .filterNotNull()
                .trace(log, Step.NORMALIZER_COMPLETED) { it.toLogInfo() }
        }

}
