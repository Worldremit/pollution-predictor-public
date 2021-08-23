package com.worldremit.stats.core

import com.worldremit.avro.StdDevAggregate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class StdDevAggregatorTest {

    private val aggregator = StdDevAggregator()

    @Test
    fun `should init aggregate`() {
        assertThat(aggregator.init())
            .isEqualTo(StdDevAggregate(0.0, 0.0, 1))
    }

    @Test
    fun `should apply aggregate for init value`() {
        val aggregate = StdDevAggregate(0.0, 0.0, 1)
        aggregator.apply(20.0, aggregate).also {
            assertThat(it).isEqualTo(StdDevAggregate(20.0, 0.0, 2))
        }
    }

    @Test
    fun `should apply aggregate for first value`() {
        val aggregate = StdDevAggregate(20.0, 0.0, 2)
        aggregator.apply(10.0, aggregate).also {
            assertThat(it).isEqualTo(StdDevAggregate(15.0, 50.0, 3))
        }
    }

    @Test
    fun `should calculate stdDev for init value`() {
        val aggregate = StdDevAggregate(0.0, 0.0, 1)
        aggregator.postMap(aggregate).also {
            assertThat(it).isNaN()
        }
    }

    @Test
    fun `should calculate stdDev for first value`() {
        val aggregate = StdDevAggregate(20.0, 0.0, 2)
        aggregator.postMap(aggregate).also {
            assertThat(it).isEqualTo(0.0)
        }
    }

    @Test
    fun `should calculate stdDev`() {
        val aggregate = StdDevAggregate(15.0, 50.0, 3)
        aggregator.postMap(aggregate).also {
            assertThat(it).isEqualTo(5.0)
        }
    }

}