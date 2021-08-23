package com.worldremit.dataloader.kafka.config

import com.worldremit.avro.Location
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO
import java.time.Duration
import java.time.LocalDate

@ConstructorBinding
@ConfigurationProperties(prefix = "loader")
data class LoaderSettings(
        val init: LoaderParams,
        val regular: LoaderParams,
        val filteredLocations: List<Location>
)

data class LoaderParams(
    @DateTimeFormat(iso = ISO.DATE) val queryFrom: LocalDate,
    @DateTimeFormat(iso = ISO.DATE) val queryTo: LocalDate,
    val delayElements: Duration,
)
