package com.worldremit.measurement

import com.worldremit.avro.Season
import java.time.LocalDateTime

fun LocalDateTime.toSeason() = Season.values()[monthValue % 12 / 3]