package com.worldremit.measurement

import com.worldremit.avro.Season
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal class MeasurementUtilKtTest {

    @DisplayName("Should transform date to season")
    @ParameterizedTest(name = "in: {0}, ex: {1}")
    @MethodSource("params")
    fun test(month: Int, expected: Season) {

        // given
        val localDateTime = LocalDate.of(2000, month, 1)
            .let { LocalDateTime.of(it, LocalTime.MIDNIGHT) }

        // when & then
        Assertions.assertThat(localDateTime.toSeason())
            .isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun params() = listOf(
            of(12, Season.WINTER),
            of(2, Season.WINTER),
            of(3, Season.SPRING),
            of(7, Season.SUMMER)
        )
    }
}