package com.worldremit.cluster

import com.worldremit.avro.ClusterParams
import org.apache.mahout.math.Centroid
import org.apache.mahout.math.jet.math.Constants
import org.apache.mahout.math.neighborhood.UpdatableSearcher
import kotlin.random.Random.Default.nextDouble

/**
 * A fork of org.apache.mahout.clustering.streaming.cluster.StreamingKMeans
 *
 * Based on:
 * "Streaming k-means approximation" by N. Ailon, R. Jaiswal, C. Monteleoni http://books.nips.cc/papers/files/nips22/NIPS2009_1085.pdf
 * "Fast and Accurate k-means for Large Datasets" by M. Shindler, A. Wong, A. Meyerson, http://books.nips.cc/papers/files/nips24/NIPS2011_1271.pdf
 *
 */
class OnlineKmeans(private val settings: KmeansSettings) {

    fun cluster(searcher: UpdatableSearcher, clusterParams: ClusterParams, dataPoint: Centroid): ClusterResult {
        val closestCenters = cluster(searcher, clusterParams, listOf(dataPoint), false)
        return ClusterResult(clusterParams, closestCenters[0], searcher.centers())
    }

    private fun cluster(searcher: UpdatableSearcher, params: ClusterParams, centroidsToCluster: Iterable<Centroid>, collapseClusters: Boolean): List<Centroid> {
        val closestCenters = mutableListOf<Centroid>()
        val iterator = centroidsToCluster.iterator()
        val oldNumProcessedDataPoints = params.processedDataPoints
        if (collapseClusters) {
            searcher.clear()
            params.processedDataPoints = 0
        }
        if (searcher.size() == 0) {
            val dataPoint = iterator.next()
            searcher.add(dataPoint)
            closestCenters.add(dataPoint)
            params.processedDataPoints++
        }
        while (iterator.hasNext()) {
            val centroidToCluster = iterator.next()
            val closestPoint = searcher.searchFirst(centroidToCluster, false)

            if (nextDouble() < centroidToCluster.weight * closestPoint.weight / params.distanceCutoff) {
                searcher.add(centroidToCluster)
                closestCenters.add(centroidToCluster) // new center
            } else {
                val closestPointCentroid = closestPoint.value as Centroid
                require(searcher.remove(closestPointCentroid, Constants.EPSILON)) { "Unable to remove centroid" }
                closestPointCentroid.update(centroidToCluster)
                searcher.add(closestPointCentroid)
                closestCenters.add(closestPointCentroid) // updated centroid
            }
            params.processedDataPoints++
            if (!collapseClusters && searcher.size() > settings.clusterOvershoot * params.clustersEstimated) {
                params.estimateClusters(settings.clusterLogFactor)

                val shuffled = searcher.centers().shuffled()
                cluster(searcher, params, shuffled, collapseClusters = true)

                if (searcher.size() > params.clustersEstimated) {
                    params.distanceCutoff *= settings.beta
                }
            }
        }
        if (collapseClusters) {
            params.processedDataPoints = oldNumProcessedDataPoints
        }
        return closestCenters
    }
}
