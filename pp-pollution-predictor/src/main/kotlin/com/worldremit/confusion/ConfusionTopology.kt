package com.worldremit.confusion

import com.worldremit.avro.ConfusionRecord
import com.worldremit.avro.Location
import com.worldremit.avro.MatchedRecord
import com.worldremit.avro.MeasurementStatsResult
import com.worldremit.util.*
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
@ConditionalOnProperty(value = [Toggle.CONFUSION])
class ConfusionTopology(
    private val confusionService: ConfusionService,
    private val trainingEnd: LocalDateTime,
) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun confusion() =
        BiF<Location, MatchedRecord, MeasurementStatsResult, ConfusionRecord> { matchedStream, statsStream ->

            val statsTable = statsStream
                .trace(log, Step.CONFUSION_NEW_STATS) { it.toLogInfo() }
                .toTable(Store.CONFUSION_STATS)

            matchedStream
                .trace(log, Step.CONFUSION_NEW_MATCHED) { it.toLogInfo() }
                .filter(Store.CONFUSION_FILTER) { matched -> matched.isNotEmpty() && matched.predicted.locationTime.isAfter(trainingEnd) }
                .join(Store.CONFUSION_JOIN, statsTable, confusionService::createConfusionRecord)
                .trace(log, Step.CONFUSION_AFTER_JOIN) { it.toLogInfo() }
        }

}

