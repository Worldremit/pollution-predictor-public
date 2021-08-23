package com.worldremit.stats

import com.worldremit.avro.PollutionRecord
import com.worldremit.avro.StatsMapRecord
import com.worldremit.avro.WeatherRecord

fun PollutionRecord.toStatsRecord() = StatsMapRecord(locationTime, features)

fun WeatherRecord.toStatsRecord() = StatsMapRecord(locationTime, features)
