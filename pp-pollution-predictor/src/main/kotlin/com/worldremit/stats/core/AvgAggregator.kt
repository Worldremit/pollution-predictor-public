package com.worldremit.stats.core

import com.worldremit.avro.AverageAggregate

import org.springframework.stereotype.Component

@Component
class AvgAggregator : ValueAggregator<Double, AverageAggregate, Double> {

    override fun init() = AverageAggregate()

    /**
     * aggregateCountsAndSums
     */
    override fun apply(value: Double, aggregate: AverageAggregate) = aggregate.apply {
        count++
        sum += value
    }

    /**
     * calculateAverage
     */
    override fun postMap(aggregate: AverageAggregate): Double = aggregate.sum / aggregate.count

}