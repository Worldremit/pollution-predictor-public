package com.worldremit.cluster

import com.worldremit.avro.ClusterParams
import com.worldremit.avro.FeaturesDescription
import com.worldremit.avro.KmeansModel
import org.apache.mahout.math.Centroid

data class ClusterResult(
    val clusterParams: ClusterParams, // muttable
    val closestCenter: Centroid,
    val clusterCenters: List<Centroid>,
) {
    fun toModel(featuresDescription: FeaturesDescription) = KmeansModel().also {
        it.featuresDescription = featuresDescription
        it.clusterParams = clusterParams
        it.clusterCenters = clusterCenters.map(Centroid::toClusterCenter)
    }

}