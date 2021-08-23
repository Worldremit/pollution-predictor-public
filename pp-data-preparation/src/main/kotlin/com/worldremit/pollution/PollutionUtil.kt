package com.worldremit.pollution

import com.worldremit.avro.PollutionFeature
import com.worldremit.avro.PollutionRawData

fun PollutionRawData.toFeatures(): Map<String, Double?> = mapOf(
    PollutionFeature.PM10 to pm10,
    PollutionFeature.PM25 to pm25,
    PollutionFeature.NOX to nox,
    PollutionFeature.NO to no,
    PollutionFeature.NO2 to no2,
    PollutionFeature.BZN to bzn,
    PollutionFeature.CO to co,
    PollutionFeature.SO2 to so2
).mapKeys { it.key.name }