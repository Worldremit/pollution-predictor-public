package com.worldremit.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.LocalDateTime

@Configuration
class TrainingConfig {

    @Bean
    fun trainingEnd(
        @Value("\${app.training.to}")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        trainingEnd: LocalDate,
    ): LocalDateTime = trainingEnd.atStartOfDay()

}