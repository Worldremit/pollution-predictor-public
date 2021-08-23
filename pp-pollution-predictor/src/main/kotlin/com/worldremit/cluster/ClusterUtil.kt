package com.worldremit.cluster

import com.worldremit.avro.*
import com.worldremit.util.getOrThrow
import org.apache.mahout.math.Centroid
import org.apache.mahout.math.DenseVector
import org.apache.mahout.math.neighborhood.UpdatableSearcher
import kotlin.math.ln
import kotlin.math.max

private const val CENTROID_ZERO_KEY = 0
private const val CENTROID_WEIGHT = 1.0

fun ClusterCenter.toCentroid() = Centroid(CENTROID_ZERO_KEY, DenseVector(coordinates.size).apply {
    for (dimension in 0 until coordinates.size) {
        this[dimension] = coordinates[dimension]
    }
}, weight)

fun Centroid.toClusterCenter(): ClusterCenter = IntRange(0, vector.size() - 1)
    .map(vector::get)
    .toList()
    .let { ClusterCenter(weight, it) }

fun MeasurementNormalized.toCentroid(featuresDescription: FeaturesDescription): Centroid {
    val weather = featuresDescription.weather.map { weatherFeatures.getOrThrow(it) }
    val oneHot = featuresDescription.oneHot.map { oneHotFeatures.getOrThrow(it) }
    val pollution = pollutionFeatures.getOrThrow(featuresDescription.target)
    return Centroid(CENTROID_ZERO_KEY, DenseVector((weather + oneHot + pollution).toDoubleArray()), CENTROID_WEIGHT)
}

fun KmeansModel.toClusterCentroids(): List<Centroid> = clusterCenters.map(ClusterCenter::toCentroid)

fun UpdatableSearcher.centers(): List<Centroid> = map { it as Centroid }

fun MatchedRecord.toRecordToCluster() = MeasurementNormalized().apply {
    pollutionFeatures = actual.pollutionFeatures
    predicted.initialMeasurement.also { past ->
        measurementId = past.measurementId
        locationTime = past.locationTime
        weatherFeatures = past.weatherFeatures
        oneHotFeatures = past.oneHotFeatures
    }
}

fun ClusterParams.estimateClusters(clusterLogFactor: Double) {
    clustersEstimated = max(clustersEstimated.toDouble(), clusterLogFactor * ln(processedDataPoints.toDouble())).toInt()
}