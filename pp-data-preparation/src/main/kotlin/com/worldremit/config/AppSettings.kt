package com.worldremit.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.format.annotation.DateTimeFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Suppress("ConfigurationProperties")
@ConstructorBinding
@ConfigurationProperties(prefix = "app")
data class AppSettings(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val trainingFrom: LocalDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val trainingTo: LocalDate,
    val trainingBuffer: Duration,
    val normalizationThreshold: Int,
) {
    fun trainingEnd(): LocalDateTime = trainingTo.atStartOfDay()

}
