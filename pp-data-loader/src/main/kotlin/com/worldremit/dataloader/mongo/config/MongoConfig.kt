package com.worldremit.dataloader.mongo.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.worldremit.dataloader.mongo.DarkskyRepository
import com.worldremit.dataloader.mongo.WiosRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = [WiosRepository::class, DarkskyRepository::class])
class MongoConfig(@Value("\${spring.data.mongodb.database}") private val databaseName: String) :
    AbstractReactiveMongoConfiguration() {

    @Bean
    override fun reactiveMongoTemplate(dbFactory: ReactiveMongoDatabaseFactory, converter: MappingMongoConverter) =
        ReactiveMongoTemplate(dbFactory, converter.apply {
            setTypeMapper(DefaultMongoTypeMapper(null))
        })

    override fun reactiveMongoClient() = mongoClient()

    @Bean
    fun mongoClient(): MongoClient = MongoClients.create() // TODO: set hostname
//    fun mongoClient(): MongoClient = MongoClients.create("mongodb+srv://db:db123@cluster0.4btby.mongodb.net/pp?retryWrites=true&w=majority") // TODO: set hostname

    override fun getDatabaseName() = databaseName

}