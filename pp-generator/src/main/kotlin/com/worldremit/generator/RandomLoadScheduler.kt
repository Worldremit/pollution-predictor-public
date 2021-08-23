package com.worldremit.generator

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@ConditionalOnProperty(value = ["app.scheduler.enabled"], havingValue = "true", matchIfMissing = false)
@EnableScheduling
class RandomLoadScheduler(private val loadGenerator: LoadGenerator) {

    @Scheduled(
            initialDelayString = "\${app.scheduler.initDelayMs}",
            fixedDelayString = "\${app.scheduler.periodMs}"
    )
    fun sendRandom() {
        loadGenerator.sendLoad()
    }
}