package com.worldremit.util

import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled
import org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.AnnotationUtils
import java.net.Socket
import java.net.URI

class AssumeConnectionCondition : ExecutionCondition {

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        return AnnotationUtils.findAnnotation(context.element, AssumeConnection::class.java).orElse(null)
                ?.uris
                ?.map {
                    when (testConnection(it)) {
                        true -> enabled("Successfully connected")
                        false -> disabled("Could not connect to '$it'. Skipping test!")
                    }
                }
                ?.find { it.isDisabled }
                ?: enabled("Continuing test")
    }

    private fun testConnection(uri: String): Boolean = URI(uri).let {
        try {
            Socket(it.host, it.port)
            true
        } catch (e: Exception) {
            false
        }
    }

}