package com.worldremit.metric

import com.worldremit.avro.Predicted
import com.worldremit.avro.StatsMapRecord
import com.worldremit.util.toLocalDateTime

private const val DELAY_MODEL = "delay-model"

fun Predicted.toLocalDateTime() = locationTime.timestamp.toLocalDateTime()

fun Predicted.toStatsMapRecord() = StatsMapRecord().also {
    it.locationTime = locationTime
    it.values = mapOf(DELAY_MODEL to modelMaturityHours.toDouble())
}
