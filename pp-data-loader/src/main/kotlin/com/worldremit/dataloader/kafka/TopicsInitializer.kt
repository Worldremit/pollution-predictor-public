package com.worldremit.dataloader.kafka

import com.worldremit.dataloader.kafka.config.TopicDefaults.MAX_COMPACTION_LAG_MS
import com.worldremit.dataloader.kafka.config.TopicDefaults.PARTITIONS
import com.worldremit.dataloader.kafka.config.TopicDefaults.REPLICAS
import com.worldremit.dataloader.kafka.config.TopicDefaults.RETENTION_MS
import com.worldremit.dataloader.kafka.config.TopicSettings
import com.worldremit.dataloader.kafka.config.TopicsSettings
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.config.TopicConfig
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class TopicsInitializer(private val topicsSettings: TopicsSettings, kafkaAdmin: KafkaAdmin) {

    private val adminClient = AdminClient.create(kafkaAdmin.configurationProperties)

//    @PostConstruct // TODO: it can't fail when topic exist
    fun createTopics() {
        topicsSettings.topics.values
            .map(::newTopic)
            .let(adminClient::createTopics)
            .let(CreateTopicsResult::all)
            .get()
    }

    private fun newTopic(topicSettings: TopicSettings) = NewTopic(topicSettings.name, PARTITIONS, REPLICAS.toShort())
        .configs(
            mapOf(
                TopicConfig.RETENTION_MS_CONFIG to RETENTION_MS.toString(),
                TopicConfig.CLEANUP_POLICY_CONFIG to topicSettings.cleanupPolicy,
                TopicConfig.RETENTION_MS_CONFIG to RETENTION_MS.toString(),
                TopicConfig.MAX_COMPACTION_LAG_MS_CONFIG to MAX_COMPACTION_LAG_MS.toString()
            )
        )
}