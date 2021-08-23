package com.worldremit.stats

import com.worldremit.avro.StatsMapAggregate
import com.worldremit.avro.StatsMapRecord
import com.worldremit.avro.StatsMapResult
import com.worldremit.stats.core.MapMeanStdDevAggregator
import com.worldremit.stats.core.ValueAggregator
import com.worldremit.util.LOCATION_TIME_INIT
import org.springframework.stereotype.Component

@Component
class MapStatsAggregator(
    private val mapMeanStdDevAggregator: MapMeanStdDevAggregator,
) : ValueAggregator<StatsMapRecord, StatsMapAggregate, StatsMapResult> {

    override fun init() = StatsMapAggregate().apply {
        locationTime = LOCATION_TIME_INIT
        values = mapMeanStdDevAggregator.init()
    }

    override fun apply(value: StatsMapRecord, aggregate: StatsMapAggregate) = StatsMapAggregate().apply {
        locationTime = value.locationTime
        values = mapMeanStdDevAggregator.apply(value.values, aggregate.values)
    }

    override fun postMap(aggregate: StatsMapAggregate) = StatsMapResult().apply {
        locationTime = aggregate.locationTime
        values = mapMeanStdDevAggregator.postMap(aggregate.values)
    }

}