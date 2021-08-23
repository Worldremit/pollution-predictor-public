package com.worldremit.util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class MapUtilKtTest {

    private val mapBasic = mapOf(
        "a" to 1,
        "b" to 2
    )

    private val mapLessKeys = mapOf(
        "a" to "A"
    )

    @Test
    fun `should get existing value`() {
        assertThat(mapBasic.getOrThrow("a"))
            .isEqualTo(1)
    }

    @Test
    fun `should thrown runtime exception when getting non existing value`() {
        assertThatThrownBy { mapBasic.getOrThrow("c") }
            .isInstanceOf(IllegalStateException::class.java)
            .withFailMessage { "Not found: c" }
    }

    @Test
    fun `should match two maps`() {
        mapBasic.matchAndTransform(mapBasic) { _, v2 -> v2 }
            .also { assertThat(it).containsKeys("a", "b") }
    }

    @Test
    fun `should fail when missing key in second map`() {
        assertThatThrownBy { mapBasic.matchAndTransform(mapLessKeys) { _, v2 -> v2 } }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should find common part when missing key in second map`() {
        mapBasic.matchAndTransform(mapLessKeys, false) { _, v2 -> v2 }
            .also { assertThat(it).containsKeys("a") }
    }

    @Test
    fun `should match only common keys based on first map`() {
        mapLessKeys.matchAndTransform(mapBasic) { _, v2 -> v2 }
            .also { assertThat(it).containsKeys("a") }
    }
}