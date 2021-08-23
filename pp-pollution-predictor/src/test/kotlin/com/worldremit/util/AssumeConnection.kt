package com.worldremit.util

import org.junit.jupiter.api.extension.ExtendWith


@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(AssumeConnectionCondition::class)
annotation class AssumeConnection(val uris: Array<String>)