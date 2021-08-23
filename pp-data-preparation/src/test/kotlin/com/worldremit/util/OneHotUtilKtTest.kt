package com.worldremit.util

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import com.worldremit.avro.DayNight
import com.worldremit.avro.Season
import com.worldremit.avro.WindDirection
import org.junit.jupiter.api.Test

internal class OneHotUtilKtTest {

    @Test
    fun `should extend binary oneHotFeatures`() {
        val extended = extendOneHotFeatures<DayNight>()
        assertThat(extended)
                .containsOnly("DAY_NIGHT")
    }

    @Test
    fun `should extend multiple features oneHotFeatures`() {
        val extended = extendOneHotFeatures<Season>()
        assertThat(extended)
                .containsOnly("SEASON_WINTER", "SEASON_SPRING", "SEASON_SUMMER", "SEASON_AUTUMN")
    }

    @Test
    fun `should encode oneHot multiple values`() {
        val encoded = oneHotEncoding(WindDirection.E)
        assertThat(encoded)
                .containsOnly(
                        "WIND_DIRECTION_N" to 0.0,
                        "WIND_DIRECTION_NE" to 0.0,
                        "WIND_DIRECTION_E" to 1.0,
                        "WIND_DIRECTION_SE" to 0.0,
                        "WIND_DIRECTION_S" to 0.0,
                        "WIND_DIRECTION_SW" to 0.0,
                        "WIND_DIRECTION_W" to 0.0,
                        "WIND_DIRECTION_NW" to 0.0,
                        "WIND_DIRECTION_NONE" to 0.0,
                        "WIND_DIRECTION_UNKNOWN" to 0.0
                )
    }

    @Test
    fun `should encode oneHot binary values`() {
        val encoded = oneHotEncoding(DayNight.DAY)
        assertThat(encoded)
                .containsOnly("DAY_NIGHT" to 1.0)
    }

    @Test
    fun `should create enumPrefix from multiple words`() {
        val prefix = enumPrefix<WindDirection>()
        assertThat(prefix)
                .isEqualTo("WIND_DIRECTION")
    }

    @Test
    fun `should create enumPrefix from one word`() {
        val prefix = enumPrefix<Season>()
        assertThat(prefix)
                .isEqualTo("SEASON")
    }

}