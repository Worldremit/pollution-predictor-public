package com.worldremit.dataloader.kafka.config

object TopicDefaults {
    const val PARTITIONS = 3
    const val REPLICAS = 1
    const val RETENTION_MS = -1
    const val MAX_COMPACTION_LAG_MS = 60_000
}