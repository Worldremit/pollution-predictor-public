package com.worldremit.forcast

import com.worldremit.avro.ConfusionRecord
import com.worldremit.avro.Forecast

fun ConfusionRecord.toForecast() = Forecast().also {
    it.locationTime = locationTime
    it.predictions = matrix.mapValues { kv -> kv.value.predicted }
}
