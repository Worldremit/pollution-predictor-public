package com.worldremit.feature

import com.worldremit.avro.*
import com.worldremit.util.extendOneHotFeatures
import com.worldremit.util.filterRequiredKeys
import com.worldremit.util.getOrThrow
import org.springframework.stereotype.Service

@Service
class FeatureService(featureFilter: FeatureFilterSettings) {

    private val featuresGlobal = featureFilter.merge()

    private val featuresExtended = FeaturesExtended(
        weather = featuresGlobal.weather.map { it.name }.toSet(),
        pollution = featuresGlobal.pollution.map { it.name }.toSet(),
        oneHot = featuresGlobal.oneHot.flatMap { EXTENDED_ONE_HOT_FEATURES.getOrThrow(it) }.toSet()
    )

    /**
     * @return filtered MeasurementNormalized or null
     */
    fun reduceDimensions(m: MeasurementNormalized): MeasurementNormalized? {
        val filteredWeather = m.weatherFeatures.filterRequiredKeys(featuresExtended.weather)
        val filteredPollution = m.pollutionFeatures.filterRequiredKeys(featuresExtended.pollution)
        val filteredOneHot = m.oneHotFeatures.filterRequiredKeys(featuresExtended.oneHot)

        return if (filteredWeather != null && filteredPollution != null && filteredOneHot != null) {
            MeasurementNormalized().apply {
                measurementId = m.measurementId
                locationTime = m.locationTime
                weatherFeatures = filteredWeather
                pollutionFeatures = filteredPollution
                oneHotFeatures = filteredOneHot
            }
        } else null
    }

    companion object {

        private val EXTENDED_ONE_HOT_FEATURES: Map<OneHotFeature, List<String>> = mapOf(
            OneHotFeature.DAY_NIGHT to extendOneHotFeatures<DayNight>(),
            OneHotFeature.SEASON to extendOneHotFeatures<Season>(),
            OneHotFeature.WIND_DIRECTION to extendOneHotFeatures<WindDirection>(),
        )

        private data class FeaturesExtended(
            val weather: Set<String>,
            val pollution: Set<String>,
            val oneHot: Set<String>,
        )
    }
}

