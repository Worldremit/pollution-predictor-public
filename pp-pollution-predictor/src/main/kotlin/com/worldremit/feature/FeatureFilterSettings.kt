package com.worldremit.feature

import com.worldremit.avro.OneHotFeature
import com.worldremit.avro.PollutionFeature
import com.worldremit.avro.WeatherFeature
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding


@Suppress("ConfigurationProperties")
@ConstructorBinding
@ConfigurationProperties(prefix = "app.features")
data class FeatureFilterSettings(
    val filter: Features,
    val filterNot: Features,
) {
    fun merge() = Features(
        weather = filter.weather.filterNot { filterNot.weather.contains(it) },
        pollution = filter.pollution.filterNot { filterNot.pollution.contains(it) },
        oneHot = filter.oneHot.filterNot { filterNot.oneHot.contains(it) }
    )
}

data class Features(
    val weather: List<WeatherFeature>,
    val pollution: List<PollutionFeature>,
    val oneHot: List<OneHotFeature>,
)
