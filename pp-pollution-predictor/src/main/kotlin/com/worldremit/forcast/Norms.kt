package com.worldremit.config

import com.worldremit.avro.Level
import com.worldremit.avro.PollutionFeature
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding


@Suppress("ConfigurationProperties")
@ConstructorBinding
@ConfigurationProperties(prefix = "app")
data class Norms(val norms: Map<PollutionFeature, Norm>)

data class Norm(val medium: Double, val high: Double) {

    fun assignLevel(value: Double): Level {
        if (value < medium) return Level.GOOD
        if (value >= high) return Level.UNHEALTHY
        return Level.MODERATE
    }

}
