package com.worldremit.feature

import com.worldremit.avro.*
import com.worldremit.util.getOrThrow
import org.springframework.stereotype.Service

@Service
class FeatureService(featureFilter: FeatureFilterSettings) {

    private val featuresGlobal = featureFilter.merge()

    private val featuresExtended = FeaturesExtended(
        weather = featuresGlobal.weather.map { it.name },
        pollution = featuresGlobal.pollution.map { it.name },
        oneHot = featuresGlobal.oneHot.flatMap { EXTENDED_ONE_HOT_FEATURES.getOrThrow(it) }
    )

    private val pollutionFeaturesDescription: Map<String, FeaturesDescription> = featuresExtended.let {
        it.pollution.associateWith { target -> FeaturesDescription(it.weather, it.oneHot, target) }
    }

    fun featuresDescriptionByPollution(pollutionFeature: String) = pollutionFeaturesDescription.getOrThrow(pollutionFeature)

    companion object {

        private val EXTENDED_ONE_HOT_FEATURES: Map<OneHotFeature, List<String>> = mapOf(
            OneHotFeature.DAY_NIGHT to extendOneHotFeatures<DayNight>(),
            OneHotFeature.SEASON to extendOneHotFeatures<Season>(),
            OneHotFeature.WIND_DIRECTION to extendOneHotFeatures<WindDirection>(),
        )

        private data class FeaturesExtended(
            val weather: List<String>,
            val pollution: List<String>,
            val oneHot: List<String>,
        )
    }
}

