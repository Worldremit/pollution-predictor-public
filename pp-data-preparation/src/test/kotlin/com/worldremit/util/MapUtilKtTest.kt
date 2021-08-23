package com.worldremit.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MapUtilKtTest {

    @Test
    fun `should not filter anything if map keys matches required keys`() {
        MAP_AB.filterRequiredKeys(SET_AB).also {
            assertThat(it).containsOnlyKeys("a", "b")
        }
    }

    @Test
    fun `should return empty map if required set of keys is empty`() {
        MAP_AB.filterRequiredKeys(emptySet()).also {
            assertThat(it).containsOnlyKeys()
        }
    }

    @Test
    fun `should filter out not required keys`() {
        MAP_AB.filterRequiredKeys(SET_A).also {
            assertThat(it).containsOnlyKeys("a")
        }
    }

    @Test
    fun `should return null if required keys are missing`() {
        MAP_B.filterRequiredKeys(SET_AB).also {
            assertThat(it).isNull()
        }
    }

    companion object {
        private val MAP_AB = mapOf("a" to 1, "b" to 0)
        private val MAP_B = mapOf("b" to 1)
        private val SET_A = setOf("a")
        private val SET_AB = setOf("a", "b")
    }

}