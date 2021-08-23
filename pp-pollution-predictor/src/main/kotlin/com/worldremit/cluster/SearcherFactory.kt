package com.worldremit.cluster

import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure
import org.apache.mahout.math.Centroid
import org.apache.mahout.math.neighborhood.ProjectionSearch
import org.apache.mahout.math.neighborhood.UpdatableSearcher
import org.springframework.stereotype.Component

private val DISTANCE_MEASURE = SquaredEuclideanDistanceMeasure()

@Component
class SearcherFactory(private val clusterSettings: ClusterSettings) {

    fun create(centroidsToImport: List<Centroid>): UpdatableSearcher =
        clusterSettings.searcher.let { // FastProjectionSearch?
            ProjectionSearch(DISTANCE_MEASURE, it.numberProjections, it.searchSize).apply {
                addAll(centroidsToImport)
            }
        }

}