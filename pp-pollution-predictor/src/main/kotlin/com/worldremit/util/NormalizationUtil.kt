package com.worldremit.util

import com.worldremit.avro.MeanStdDev

fun MeanStdDev.normalize(value: Double): Double = (mean - value) / stdDev
fun MeanStdDev.denormalize(normalized: Double): Double = mean - normalized * stdDev
