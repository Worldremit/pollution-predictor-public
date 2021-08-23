package com.worldremit.generator.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.format.annotation.DateTimeFormat
import java.time.Duration
import java.time.LocalDate

@ConstructorBinding
@ConfigurationProperties(prefix = "app.generator")
data class GeneratorSettings(
        val loadType: String,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        val startDate: LocalDate,
        val timeShift: Duration,
        val locations: List<LatLon>
)

data class LatLon(val lat: Double, val lon: Double)
