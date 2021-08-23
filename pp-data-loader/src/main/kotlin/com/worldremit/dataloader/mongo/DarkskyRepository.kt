package com.worldremit.dataloader.mongo

import com.worldremit.dataloader.mongo.model.DarkskyEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface DarkskyRepository : ReactiveMongoRepository<DarkskyEntity, String>
