package com.worldremit.cluster

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "app.cluster")
data class ClusterSettings(val searcher: SearcherSettings, val kmeans: KmeansSettings, val init: ClusterInitSettings)

data class SearcherSettings(val numberProjections: Int, val searchSize: Int)

data class KmeansSettings(val beta: Double = 1.3, val clusterLogFactor: Double = 10.0, val clusterOvershoot: Double = 2.0)

data class ClusterInitSettings(val clustersEstimated: Int, val distanceCutoff: Double)

