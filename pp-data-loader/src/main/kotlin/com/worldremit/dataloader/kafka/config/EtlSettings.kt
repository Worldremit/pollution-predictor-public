package com.worldremit.dataloader.kafka.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "etl")
data class EtlSettings(val weather: EtlEntry, val pollution: EtlEntry)

data class EtlEntry(val collection: String, val topic: String)
