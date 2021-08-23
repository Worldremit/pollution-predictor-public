package com.worldremit.util


import com.worldremit.avro.DayNight
import com.worldremit.avro.PrecipitationType
import com.worldremit.avro.Season
import com.worldremit.avro.WindDirection
import kotlin.reflect.KClass


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

inline fun <reified T : Enum<T>> oneHotEncoding(enum: Enum<T>): Map<String, Double> =
        ENUM_PREFIXES.getOrThrow(T::class.simpleName).let { prefix ->
            if (enumValues<T>().size == 2) {
                mapOf(prefix to if (enum.ordinal == 0) 1.0 else 0.0)
            } else {
                enumValues<T>().associate { "${prefix}_${it.name}" to if (it == enum) 1.0 else 0.0 }
            }
        }

inline fun <reified T : Enum<T>> enumPrefix(): String = upperCamelCaseToUpperUnderscore(T::class.simpleName!!)

