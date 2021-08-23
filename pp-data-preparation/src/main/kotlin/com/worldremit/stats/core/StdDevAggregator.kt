package com.worldremit.stats.core

import com.worldremit.avro.StdDevAggregate
import org.springframework.stereotype.Component
import kotlin.math.sqrt

/**
 * B. P. Welford's algorithm for computing variance (Donald Knuth's Art of Computer Programming, 1962, Vol 2, page 232, 3rd edition)
 * @see <a href="https://www.johndcook.com/blog/standard_deviation/">Standard deviation</a>
 * @see <a href="https://stackoverflow.com/a/897463">Welford's method</a>
 */
@Component
class StdDevAggregator : ValueAggregator<Double, StdDevAggregate, Double> {

    override fun init() = StdDevAggregate(0.0, 0.0, 1)

    /**
     * aggregate variance
     */
    override fun apply(value: Double, aggregate: StdDevAggregate) = StdDevAggregate().also {
        with(aggregate.mean + (value - aggregate.mean) / aggregate.counter) {
            it.mean = this
            it.variance = aggregate.variance + (value - aggregate.mean) * (value - this)
            it.counter = aggregate.counter + 1
        }
    }

    /**
     * calculate stdDev
     */
    override fun postMap(aggregate: StdDevAggregate) = sqrt(aggregate.variance / (aggregate.counter - 1))
}