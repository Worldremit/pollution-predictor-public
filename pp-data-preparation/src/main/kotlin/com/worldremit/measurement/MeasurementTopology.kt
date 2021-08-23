package com.worldremit.measurement

import com.worldremit.avro.*
import com.worldremit.util.*
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@ConditionalOnProperty(value = [Toggle.MEASUREMENT])
class MeasurementTopology {

    private val log = KotlinLogging.logger {}
    private val joinWindow = Duration.ofDays(2 * 365)

    @Bean
    fun measurement() =
        BiF<Location, WeatherRecord, PollutionRecord, Measurement> { weatherStream, pollutionStream ->
            val weatherByLocationTime = weatherStream
                .selectKey(Store.WEATHER_REKEY) { it.locationTime }

            val pollutionByLocationTime = pollutionStream
                .selectKey(Store.POLLUTION_REKEY) { it.locationTime }

            weatherByLocationTime
                .join(pollutionByLocationTime, Store.MEASUREMENT_JOIN, joinWindow, ::joinWeatherPollution)
                .selectKey(Store.MEASUREMENT_REKEY) { it.locationTime.location }
                .trace(log, Step.MEASUREMENT_JOINED) { it.toLogInfo() }
        }

    private fun joinWeatherPollution(w: WeatherRecord, p: PollutionRecord) = Measurement().apply {
        measurementId = randomUuid()
        locationTime = w.locationTime
        dayNight = if (w.features[WeatherFeature.SOLAR_AZIMUTH.name] != null) DayNight.DAY else DayNight.NIGHT
        season = w.locationTime.timestamp.toLocalDateTime().toSeason()
        weatherFeatures = w.features
        pollutionFeatures = p.features
        windDirection = w.windDirection
    }
}
