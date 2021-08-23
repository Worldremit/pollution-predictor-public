package com.worldremit.cluster

import com.worldremit.avro.KmeansModels
import com.worldremit.avro.Location
import com.worldremit.avro.MatchedRecord
import com.worldremit.util.*
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(value = [Toggle.CLUSTERER])
class ClustererTopology(private val clustererAggregator: ClustererAggregator) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun clusterer() = F<Location, MatchedRecord, KmeansModels> { stream ->
        stream
            .mapValues { matched -> matched.toRecordToCluster() }
            .groupByKey()
            .aggregate(Store.CLUSTERER_MODELS, clustererAggregator)
            .toStream()
            .trace(log, Step.CLUSTERER_NEW_MODEL) { it.toLogInfo() }
    }

}
