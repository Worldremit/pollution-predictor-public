package com.worldremit.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@Suppress("ConfigurationProperties")
@ConstructorBinding
@ConfigurationProperties
data class StreamsSettings(val streams: Map<String, Map<String, Any>>)

