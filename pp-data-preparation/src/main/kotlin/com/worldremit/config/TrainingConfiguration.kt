package com.worldremit.config

import com.worldremit.util.toTimestamp
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.LocalDateTime

@Configuration
class TrainingConfiguration {

    @Bean
    fun trainingEnd(appSettings: AppSettings): LocalDateTime = appSettings.trainingTo.atStartOfDay()

    // TODO: move to util
    @Bean
    fun trainingTimestampConfig(appSettings: AppSettings): TrainingTimestampConfig = appSettings.let {
        TrainingTimestampConfig(
            startTimestamp = it.trainingFrom.atStartOfDay().toTimestamp(),
            endTimestamp = it.trainingTo.atStartOfDay().toTimestamp(),
            endBufferedTimestamp = it.trainingTo.atStartOfDay().plus(it.trainingBuffer).toTimestamp()
        )
    }
}

data class TrainingTimestampConfig(val startTimestamp: Long, val endTimestamp: Long, val endBufferedTimestamp: Long) {
    fun toDuration(): Duration = Duration.ofMillis(endBufferedTimestamp - startTimestamp)
}
