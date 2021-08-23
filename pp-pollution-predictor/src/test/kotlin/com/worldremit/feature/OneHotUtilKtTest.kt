package com.worldremit.feature

import assertk.assertThat
import assertk.assertions.containsOnly
import com.worldremit.avro.DayNight
import com.worldremit.avro.Season
import com.worldremit.feature.extendOneHotFeatures
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

}