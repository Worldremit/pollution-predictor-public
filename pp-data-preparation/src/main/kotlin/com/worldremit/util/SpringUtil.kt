package com.worldremit.util

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource
import org.springframework.core.io.ClassPathResource

/**
 * Load config without spring context
 * @param prefix must match with prefix in config's annotation
 * @param <T> the bound type
 * @return the binding result
 */
inline fun <reified T> loadConfig(prefix: String): T {
    val factoryBean = YamlPropertiesFactoryBean().apply {
        setResources(ClassPathResource("application.yml"))
    }
    val propertySource = MapConfigurationPropertySource(factoryBean.getObject())
    return Binder(propertySource).bind(prefix, T::class.java).get()
}

inline fun <reified T> loadConfig(): T =
    T::class.java.getAnnotation(ConfigurationProperties::class.java).prefix.let(::loadConfig)
