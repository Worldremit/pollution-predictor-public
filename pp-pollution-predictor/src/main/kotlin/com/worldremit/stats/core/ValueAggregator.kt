package com.worldremit.stats.core

interface ValueAggregator<V, VR, VF> {

    fun init(): VR

    fun apply(value: V, aggregate: VR): VR

    fun postMap(aggregate: VR): VF

}
