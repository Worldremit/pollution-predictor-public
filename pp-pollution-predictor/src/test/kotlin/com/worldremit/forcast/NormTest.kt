package com.worldremit.forcast

import com.worldremit.avro.Level
import com.worldremit.config.Norm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NormTest {

    private val norm = Norm(1.0, 2.0)

    @Test
    fun `should assign good level when number less than medium`() {
        assertThat(norm.assignLevel(0.5))
            .isEqualTo(Level.GOOD)
    }

    @Test
    fun `should assign moderate level when medium number`() {
        assertThat(norm.assignLevel(1.5))
            .isEqualTo(Level.MODERATE)
    }

    @Test
    fun `should assign unhealthy level when high number`() {
        assertThat(norm.assignLevel(2.5))
            .isEqualTo(Level.UNHEALTHY)
    }

    @Test
    fun `should assign moderate level when number is equal to medium`() {
        assertThat(norm.assignLevel(1.0))
            .isEqualTo(Level.MODERATE)
    }
}