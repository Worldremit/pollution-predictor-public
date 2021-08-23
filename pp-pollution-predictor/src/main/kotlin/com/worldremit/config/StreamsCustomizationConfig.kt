package com.worldremit.config

import mu.KotlinLogging
import org.apache.kafka.streams.StreamsConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer

private const val TOPOLOGY_DESCRIPTION = "topology-description"

@Configuration
class StreamsCustomizationConfig(private val streamsSettings: StreamsSettings) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun streamsBuilderFactoryBeanConfigurer() = StreamsBuilderFactoryBeanConfigurer { factoryBean ->
        factoryBean.streamsConfiguration?.let { props ->
            props.getProperty(StreamsConfig.APPLICATION_ID_CONFIG)?.let { appId ->
                logTopologyDescription(factoryBean, appId)
                streamsSettings.streams[appId]
                        ?.mapKeys { it.key.replace("-", ".") }
                        ?.let(props::putAll)
            }
        }
    }

    private fun logTopologyDescription(factoryBean: StreamsBuilderFactoryBean, appId: String) {
        factoryBean.setKafkaStreamsCustomizer { kafkaStreams ->
            kafkaStreams.metrics()
                    .filterKeys { it.name() == TOPOLOGY_DESCRIPTION }.values.first()
                    ?.run { log.info { "$TOPOLOGY_DESCRIPTION for ${appId}:\n${metricValue()}" } }
        }
    }
}
