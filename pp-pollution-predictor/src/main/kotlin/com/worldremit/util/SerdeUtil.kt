package com.worldremit.util

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.StreamJoined
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.apache.kafka.streams.state.Stores

private const val PREFIX_SCHEMA_REGISTRY = "settings.schema-registry"
private val SCHEMA_REGISTRY_URL = loadConfig<String>(PREFIX_SCHEMA_REGISTRY)

fun <T : SpecificRecord> createSerde(key: Boolean, schemaRegistryUrl: String = SCHEMA_REGISTRY_URL): Serde<T> =
    SpecificAvroSerde<T>().apply { configure(mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to schemaRegistryUrl), key) }
