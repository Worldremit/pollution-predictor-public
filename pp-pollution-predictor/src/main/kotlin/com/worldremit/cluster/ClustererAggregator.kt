package com.worldremit.cluster

import com.worldremit.avro.KmeansModels
import com.worldremit.avro.MeasurementNormalized
import com.worldremit.stats.core.ValueAggregator
import com.worldremit.util.LOCATION_TIME_INIT
import com.worldremit.util.Toggle
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(value = [Toggle.CLUSTERER])
class ClustererAggregator(
    private val clusterService: ClusterService,
) : ValueAggregator<MeasurementNormalized, KmeansModels, KmeansModels> {

    override fun init() = KmeansModels().apply {
        locationTime = LOCATION_TIME_INIT
        models = emptyMap()
    }

    override fun apply(value: MeasurementNormalized, aggregate: KmeansModels) = KmeansModels().apply {
        locationTime = value.locationTime
        models = when (aggregate.models.isEmpty()) {
            true -> value.pollutionFeatures.mapValues { clusterService.initModel(value.locationTime.location, it.key) }
            false -> aggregate.models.mapValues { clusterService.clusterMeasurement(value, it.value) }
        }
    }

    override fun postMap(aggregate: KmeansModels) = aggregate
}
