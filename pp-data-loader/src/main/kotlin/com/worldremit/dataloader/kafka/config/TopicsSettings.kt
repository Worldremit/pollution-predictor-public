package com.worldremit.dataloader.kafka.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties
data class TopicsSettings(val topics: Map<String, TopicSettings>)

data class TopicSettings(val name: String, val cleanupPolicy: String = "delete")

