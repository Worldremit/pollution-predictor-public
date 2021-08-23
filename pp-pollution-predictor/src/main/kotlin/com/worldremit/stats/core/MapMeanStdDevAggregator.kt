package com.worldremit.stats.core

import com.worldremit.avro.MeanStdDev
import com.worldremit.avro.MeanStdDevAggregate
import org.springframework.stereotype.Component

@Component
class MapMeanStdDevAggregator(
    private val avgAggregator: AvgAggregator,
    private val stdDevAggregator: StdDevAggregator,
) : ValueAggregator<Map<String, Double>, Map<String, MeanStdDevAggregate>, Map<String, MeanStdDev>> {

    override fun init(): Map<String, MeanStdDevAggregate> = mapOf()

    override fun apply(value: Map<String, Double>, aggregate: Map<String, MeanStdDevAggregate>) =
        aggregate.toMutableMap().also {
            value.forEach { entry ->
                it.getOrPut(entry.key) { initAggregate() }.apply {
                    mean = avgAggregator.apply(entry.value, mean)
                    stdDev = stdDevAggregator.apply(entry.value, stdDev)
                }
            }
        }

    private fun initAggregate() = MeanStdDevAggregate().apply {
        mean = avgAggregator.init()
        stdDev = stdDevAggregator.init()
    }

    override fun postMap(aggregate: Map<String, MeanStdDevAggregate>): Map<String, MeanStdDev> = aggregate.mapValues {
        calculateMeanStdDev(it.value)
    }

    private fun calculateMeanStdDev(aggregate: MeanStdDevAggregate) = MeanStdDev(
        avgAggregator.postMap(aggregate.mean),
        stdDevAggregator.postMap(aggregate.stdDev)
    )

}


