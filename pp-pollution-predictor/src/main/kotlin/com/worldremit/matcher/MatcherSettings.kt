package com.worldremit.matcher

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "app.matcher")
data class MatcherSettings(
    val predictionTime: Duration,
    val joinWindow: Duration,
)


