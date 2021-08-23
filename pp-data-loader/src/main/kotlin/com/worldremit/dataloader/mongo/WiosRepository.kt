package com.worldremit.dataloader.mongo

import com.worldremit.dataloader.mongo.model.WiosEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface WiosRepository : ReactiveMongoRepository<WiosEntity, String>
