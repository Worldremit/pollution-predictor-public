package com.worldremit.generator

import com.worldremit.avro.Location
import com.worldremit.avro.LocationTime
import com.worldremit.generator.RandomMeasurement.randomMeasurementNormalized
import com.worldremit.generator.RandomMeasurement.randomPollutionRaw
import com.worldremit.generator.RandomMeasurement.randomWeatherRaw
import com.worldremit.generator.config.GeneratorSettings
import com.worldremit.generator.config.LoadType
import com.worldremit.generator.config.TopicName
import com.worldremit.util.randomUuid
import com.worldremit.util.short
import com.worldremit.util.toTimestamp
import mu.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LoadGenerator(
        private val kafkaTemplate: KafkaTemplate<Location, SpecificRecord>,
        private val settings: GeneratorSettings,
) {
    private val log = KotlinLogging.logger {}
    private val locations = settings.locations.map { Location(it.lat, it.lon) }

    private var currentTime = settings.startDate.atStartOfDay()

    fun sendLoad() {
        when(settings.loadType) {
            LoadType.RAW -> sendRawData()
            LoadType.STATS -> sendStats()
            LoadType.NORMALIZED -> sendMeasurementNormalized()
        }
        currentTime = currentTime.plus(settings.timeShift)
    }

    private fun sendMeasurementNormalized() {
        locations.forEach { location ->
            val measurementId = randomUuid()

            val locationTime = LocationTime(location, currentTime.toTimestamp())
            val measurementNormalized = randomMeasurementNormalized(measurementId, locationTime)

            log.info("Sending MeasurementNormalized, timestamp: {} (m={})", currentTime, measurementId.short())
            send(TopicName.MEASUREMENTS_NORMALIZED, currentTime, location, measurementNormalized)
        }
    }

    private fun sendRawData() {
        locations.forEach { location ->
            val pollutionId = randomUuid()
            val weatherId = randomUuid()

            val locationTime = LocationTime(location, currentTime.toTimestamp())
            val pollutionRaw = randomPollutionRaw(pollutionId, locationTime)
            val weatherRaw = randomWeatherRaw(weatherId, locationTime)

            log.info("Sending PollutionRaw/WeatherRaw, timestamp: {} (w={}, p={})", currentTime, weatherId.short(), pollutionId.short())
            send(TopicName.POLLUTIONS_RAW, currentTime, location, pollutionRaw)
            send(TopicName.WEATHERS_RAW, currentTime, location, weatherRaw)
        }
    }

    private fun sendStats() {
        locations.forEach { location ->
            log.info("Sending Stats, timestamp: {}", DevData.TRAINING_END)
            send(TopicName.GLOBAL_STATS, DevData.TRAINING_END, location, DevData.STATS)
        }
    }

    private fun send(topicName: String, dateTime: LocalDateTime, location: Location, record: SpecificRecord) {
        kafkaTemplate.send(ProducerRecord(topicName, null, dateTime.toTimestamp(), location, record)).get()
    }

}
