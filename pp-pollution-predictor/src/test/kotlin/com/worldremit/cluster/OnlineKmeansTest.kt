package com.worldremit.cluster

import assertk.assertThat
import assertk.assertions.hasSize
import com.worldremit.DevData
import com.worldremit.DevData.CENTROID_0
import com.worldremit.DevData.CLUSTER_SETTINGS
import org.junit.jupiter.api.Test

internal class OnlineKmeansTest {

    private val kMeans = OnlineKmeans(KmeansSettings())

    @Test
    fun `should cluster centroid`() {
        val searcher = SearcherFactory(CLUSTER_SETTINGS).create(listOf())
        val model = kMeans.cluster(searcher, DevData.CLUSTER_PARAMS, CENTROID_0)
        assertThat(model.clusterCenters)
            .hasSize(1)
    }

}