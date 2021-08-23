package com.worldremit.dataloader

import com.worldremit.avro.PollutionRaw
import com.worldremit.avro.WeatherRaw
import com.worldremit.dataloader.kafka.config.LoaderParams
import com.worldremit.dataloader.kafka.config.LoaderSettings
import com.worldremit.dataloader.mongo.model.DarkskyEntity
import com.worldremit.dataloader.mongo.model.WiosEntity
import org.apache.avro.specific.SpecificRecord
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.scheduler.Schedulers

@RestController
class LoadController(
        private val pollutionLoader: Loader<WiosEntity, PollutionRaw>,
        private val weatherLoader: Loader<DarkskyEntity, WeatherRaw>,
        private val settings: LoaderSettings
) {

    @GetMapping("/init")
    fun init() {
        load(pollutionLoader, settings.init)
        load(weatherLoader, settings.init)
    }

    @GetMapping("/regular")
    fun regular() {
        load(pollutionLoader, settings.regular)
        load(weatherLoader, settings.regular)
    }

    private fun <E, V : SpecificRecord> load(loader: Loader<E, V>, loaderParams: LoaderParams) =
            loader.load(loaderParams, settings.filteredLocations)
                    .subscribeOn(Schedulers.single())
                    .subscribe()

}
