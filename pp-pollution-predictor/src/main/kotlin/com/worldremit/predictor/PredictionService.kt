package com.worldremit.predictor

import com.worldremit.avro.*
import com.worldremit.cluster.SearcherFactory
import com.worldremit.cluster.toCentroid
import com.worldremit.cluster.toClusterCenter
import com.worldremit.cluster.toClusterCentroids
import com.worldremit.util.toLocalDateTime
import org.apache.mahout.math.Centroid
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class PredictionService(private val searcherFactory: SearcherFactory) {

    fun init(measurement: MeasurementNormalized) = createPrediction(measurement, 0L, mapOf())

    fun predict(measurement: MeasurementNormalized, models: KmeansModels): Predicted {
        val delay = Duration.between(models.locationTime.timestamp.toLocalDateTime(), measurement.toLocalDateTime()).toHours()
        val patterns = models.models.mapValues {
            PredictedPattern(it.value.featuresDescription, findClosestCenter(measurement, it.value).coordinates)
        }
        return createPrediction(measurement, delay, patterns)
    }

    private fun findClosestCenter(measurement: MeasurementNormalized, model: KmeansModel): ClusterCenter {
        val searcher = searcherFactory.create(model.toClusterCentroids())
        return measurement.toCentroid(model.featuresDescription)
            .let { searcher.searchFirst(it, false).value as Centroid }
            .toClusterCenter()
    }

    private fun createPrediction(measurement: MeasurementNormalized, delay: Long, patterns: Map<String, PredictedPattern>) =
        Predicted().apply {
            locationTime = measurement.locationTime
            initialMeasurement = measurement
            modelMaturityHours = delay
            predictedPatterns = patterns
        }

}