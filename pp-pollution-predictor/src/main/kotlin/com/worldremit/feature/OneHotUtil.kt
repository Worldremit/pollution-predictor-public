package com.worldremit.feature

import com.google.common.base.CaseFormat
import com.worldremit.avro.DayNight
import com.worldremit.avro.PrecipitationType
import com.worldremit.avro.Season
import com.worldremit.avro.WindDirection
import com.worldremit.util.getOrThrow

val ENUM_PREFIXES = mapOf(
    WindDirection::class.simpleName to enumPrefix<WindDirection>(),
    DayNight::class.simpleName to enumPrefix<DayNight>(),
    Season::class.simpleName to enumPrefix<Season>(),
    PrecipitationType::class.simpleName to enumPrefix<PrecipitationType>()
)

inline fun <reified T : Enum<T>> extendOneHotFeatures(): List<String> =
    ENUM_PREFIXES.getOrThrow(T::class.simpleName).let { prefix ->
        enumValues<T>().let { values ->
            when (values.size) {
                2 -> listOf(prefix)
                else -> values
                    .map { "${prefix}_${it.name}" }
                    .toList()
            }
        }
    }

private inline fun <reified T : Enum<T>> enumPrefix(): String = T::class.simpleName!!
    .let { CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, it) }

