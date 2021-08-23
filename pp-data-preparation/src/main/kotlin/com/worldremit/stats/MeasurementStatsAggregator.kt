package com.worldremit.stats

import com.worldremit.avro.*
import com.worldremit.stats.core.MapMeanStdDevAggregator
import com.worldremit.stats.core.ValueAggregator
import com.worldremit.util.LOCATION_TIME_INIT
import org.springframework.stereotype.Component

@Component
class MeasurementStatsAggregator(
    private val aggregator: MapMeanStdDevAggregator
) : ValueAggregator<Measurement, MeasurementStatsAggregate, MeasurementStatsResult> {

    override fun init() = MeasurementStatsAggregate().apply {
        locationTime = LOCATION_TIME_INIT
        weather = aggregator.init()
        pollution = aggregator.init()
    }

    override fun apply(value: Measurement, aggregate: MeasurementStatsAggregate) = MeasurementStatsAggregate().apply {
        locationTime = value.locationTime
        weather = aggregator.apply(value.weatherFeatures, aggregate.weather)
        pollution = aggregator.apply(value.pollutionFeatures, aggregate.pollution)
    }

    override fun postMap(aggregate: MeasurementStatsAggregate) = MeasurementStatsResult().apply {
        locationTime = aggregate.locationTime
        weather = aggregator.postMap(aggregate.weather)
        pollution = aggregator.postMap(aggregate.pollution)
    }

}
