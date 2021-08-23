package com.worldremit.normalizer

import com.worldremit.avro.Location
import com.worldremit.avro.Measurement
import com.worldremit.avro.MeasurementNormalized
import com.worldremit.avro.NormalizerStats
import com.worldremit.config.TrainingTimestampConfig
import com.worldremit.util.*
import mu.KotlinLogging
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.WindowStore

class NormalizerTransformer(
    private val normalizerService: NormalizerService,
    private val config: TrainingTimestampConfig,
) : Transformer<Location, Measurement, KeyValue<Location, MeasurementNormalized>> {

    private val log = KotlinLogging.logger {}
    private lateinit var context: ProcessorContext

    private lateinit var measurementsTrainingStore: WindowStore<Location, Measurement>
    private lateinit var normalizerStatsStore: KeyValueStore<Location, NormalizerStats>

    override fun transform(location: Location, measurement: Measurement): KeyValue<Location, MeasurementNormalized>? {
        val normalizerStats = normalizerStatsStore.get(location)

        if (normalizerStats == null) {
            measurement.trace(log, Step.NORMALIZER_TRAINING_FLOW) { it.toLogInfo() }
            measurementsTrainingStore.put(location, measurement, measurement.locationTime.timestamp)
            return null
        }

        if (!normalizerStats.processedTrainingMeasurements) {
            location.trace(log, Step.NORMALIZER_TRAINING_FLOW_CLOSING) { it.toLogInfo() }
            @Suppress("DEPRECATION")
            measurementsTrainingStore.fetch(location, config.startTimestamp, config.endBufferedTimestamp).forEach {
                context.forward(location, normalizerService.normalize(it.value, normalizerStats.globalStats))
            }
            normalizerStatsStore.put(location, NormalizerStats(normalizerStats.globalStats, true))
        }
        measurement.trace(log, Step.NORMALIZER_REGULAR_FLOW) { it.toLogInfo() }
        return KeyValue(location, normalizerService.normalize(measurement, normalizerStats.globalStats))
    }

    override fun init(context: ProcessorContext) {
        this.context = context
        this.measurementsTrainingStore = context.getStateStore(Store.MEASUREMENTS_TRAINING) as WindowStore<Location, Measurement>
        this.normalizerStatsStore = context.getStateStore(Store.NORMALIZER_STATS) as KeyValueStore<Location, NormalizerStats>
    }

    override fun close() {
        // do nothing
    }

}