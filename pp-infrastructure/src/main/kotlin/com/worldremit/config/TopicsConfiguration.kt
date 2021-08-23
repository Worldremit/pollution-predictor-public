package com.worldremit.config

import mu.KotlinLogging
import org.apache.kafka.common.config.TopicConfig.CLEANUP_POLICY_CONFIG
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class TopicsConfiguration {

    private val log = KotlinLogging.logger {}

    @Bean
    fun newTopics(settings: TopicsSettings) = settings.topics.values.map {
        TopicBuilder
            .name(it.name)
            .config(CLEANUP_POLICY_CONFIG, it.cleanupPolicy)
            .build()
    }
        .also { it.joinToString(prefix = "Creating topics: ") { topic -> topic.name() }.let(log::info) }
        .let { KafkaAdmin.NewTopics(*it.toTypedArray()) }

}
