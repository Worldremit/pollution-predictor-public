package com.worldremit.cluster

import com.worldremit.avro.ClusterParams
import com.worldremit.avro.KmeansModel
import com.worldremit.avro.Location
import com.worldremit.avro.MeasurementNormalized
import com.worldremit.feature.FeatureService
import org.springframework.stereotype.Component

@Component
class ClusterService(
    private val searcherFactory: SearcherFactory,
    private val clusterSettings: ClusterSettings,
    private val featureService: FeatureService,
) {

    private val kMeans = OnlineKmeans(clusterSettings.kmeans)

    fun initModel(location: Location, targetFeature: String): KmeansModel = clusterSettings.init.let {
        KmeansModel().apply {
            featuresDescription = featureService.featuresDescriptionByPollution(targetFeature)
            clusterCenters = listOf()
            clusterParams = ClusterParams().apply {
                processedDataPoints = 0L
                clustersEstimated = it.clustersEstimated
                distanceCutoff = it.distanceCutoff // 1.0 / clustersEstimated
            }
        }
    }

    fun clusterMeasurement(measurement: MeasurementNormalized, model: KmeansModel): KmeansModel {
        val searcher = searcherFactory.create(model.toClusterCentroids())
        val centroidToCluster = measurement.toCentroid(model.featuresDescription)
        return kMeans.cluster(searcher, model.clusterParams, centroidToCluster).toModel(model.featuresDescription)
    }
}
