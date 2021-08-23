package com.worldremit.cluster

import com.worldremit.DevData.CLUSTER_RESULT
import com.worldremit.DevData.FEATURES_DESCRIPTION
import com.worldremit.avro.KmeansModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ClusterResultTest {

    @Test
    fun `should convert ClusterResult to Model`() {

        CLUSTER_RESULT.toModel(FEATURES_DESCRIPTION).also {
            assertThat(it).isInstanceOf(KmeansModel::class.java)
            assertThat(it.clusterCenters).hasSize(3)
        }

    }
}