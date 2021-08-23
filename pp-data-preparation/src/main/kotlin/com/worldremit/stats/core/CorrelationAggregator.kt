package com.worldremit.stats.core

import com.worldremit.avro.CorrelationAggregate
import com.worldremit.avro.CorrelationInput
import com.worldremit.avro.CorrelationResult
import org.apache.commons.math3.util.FastMath
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.math.sqrt


/**
 * Pearson's correlation coefficient that is accomplished in a single pass.
 * @see: <a href="https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online">Online variance calculation</a>
 */
@Component
class CorrelationAggregator : ValueAggregator<CorrelationInput, CorrelationAggregate, CorrelationResult> {

    override fun init() = CorrelationAggregate(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0L)

    override fun apply(value: CorrelationInput, aggregate: CorrelationAggregate) =
        aggregateCorrelation(value.featureX, value.featureY, aggregate)

    override fun postMap(aggregate: CorrelationAggregate) = calculateR(aggregate).let(::CorrelationResult)

    private fun aggregateCorrelation(x: Double, y: Double, aggregate: CorrelationAggregate): CorrelationAggregate {
        if (aggregate.n == 0L) {
            return CorrelationAggregate(x, y, x, y, 0.0, 0.0, 0.0, 1)
        }
        val fact1 = 1.0 + aggregate.n
        val fact2 = aggregate.n / (1.0 + aggregate.n)
        val dx = x - aggregate.xMean
        val dy = y - aggregate.yMean
        return CorrelationAggregate().apply {
            sumXx = aggregate.sumXx + dx * dx * fact2
            sumYy = aggregate.sumYy + dy * dy * fact2
            sumXy = aggregate.sumXy + dx * dy * fact2
            xMean = aggregate.xMean + dx / fact1
            yMean = aggregate.yMean + dy / fact1
            sumX = aggregate.sumX + x
            sumY = aggregate.sumY + y
            n = aggregate.n + 1
        }
    }

    private fun calculateR(aggregate: CorrelationAggregate): Double {
        val totalSumSquares = if (aggregate.n < 2) Double.NaN else aggregate.sumYy
        val sumSquaredErrors = max(0.0, aggregate.sumYy - aggregate.sumXy * aggregate.sumXy / aggregate.sumXx)
        val result = sqrt((totalSumSquares - sumSquaredErrors) / totalSumSquares)
        return if (shouldSlope(aggregate)) -result else result
    }

    private fun shouldSlope(aggregate: CorrelationAggregate) = aggregate.n >= 2
            && FastMath.abs(aggregate.sumXx) >= 10 * Double.MIN_VALUE
            && aggregate.sumXy / aggregate.sumXx < 0

}