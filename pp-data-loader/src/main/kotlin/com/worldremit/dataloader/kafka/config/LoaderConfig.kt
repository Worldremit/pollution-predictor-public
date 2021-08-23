package com.worldremit.dataloader.kafka.config

import com.worldremit.avro.Location
import com.worldremit.avro.PollutionRaw
import com.worldremit.avro.WeatherRaw
import com.worldremit.dataloader.Loader
import com.worldremit.dataloader.mongo.model.DarkskyEntity
import com.worldremit.dataloader.mongo.model.WiosEntity
import com.worldremit.dataloader.toPollutionRaw
import com.worldremit.dataloader.toWeatherRaw
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.kafka.core.KafkaTemplate

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
class LoaderConfig(private val settings: EtlSettings) {

    @Bean
    fun pollutionLoader(mongoTemplate: ReactiveMongoTemplate, kafkaTemplate: KafkaTemplate<Location, PollutionRaw>) =
        settings.pollution.let {
            Loader(mongoTemplate, WiosEntity::class.java, it.collection, WiosEntity::toPollutionRaw,
                PollutionRaw::getLocationTime, kafkaTemplate, it.topic)
        }

    @Bean
    fun weatherLoader(mongoTemplate: ReactiveMongoTemplate, kafkaTemplate: KafkaTemplate<Location, WeatherRaw>) =
        settings.weather.let {
            Loader(mongoTemplate, DarkskyEntity::class.java, it.collection, DarkskyEntity::toWeatherRaw,
                WeatherRaw::getLocationTime, kafkaTemplate, it.topic)
        }

}