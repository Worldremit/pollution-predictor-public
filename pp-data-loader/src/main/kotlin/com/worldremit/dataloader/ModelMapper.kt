package com.worldremit.dataloader

import com.worldremit.avro.*
import com.worldremit.dataloader.mongo.model.DarkskyEntity
import com.worldremit.dataloader.mongo.model.WiosEntity
import com.worldremit.dataloader.util.randomUuid
import com.worldremit.dataloader.util.toAvroLocation
import com.worldremit.dataloader.util.toTimestamp


fun WiosEntity.toPollutionRaw(): PollutionRaw = PollutionRaw().apply {
    val entity = this@toPollutionRaw
    val data = entity.data

    setPollutionId(randomUuid())

    setLocationTime(LocationTime().apply {
        entity.location.toAvroLocation().let(::setLocation)
        entity.timestamp.toTimestamp().let(::setTimestamp)
    })
    setData(PollutionRawData().apply {
        setPm25(data.pm25)
        setPm10(data.pm10)
        setBzn(data.bzn)
        setCo(data.co)
        setNo(data.no)
        setNo2(data.no2)
        setNox(data.nox)
        setSo2(data.so2)
    })
}

fun DarkskyEntity.toWeatherRaw(): WeatherRaw = WeatherRaw().apply {
    val entity = this@toWeatherRaw
    val data = entity.data
    val solar = data.solar

    setWeatherId(randomUuid())

    setLocationTime(LocationTime().apply {
        entity.location.toAvroLocation().let(::setLocation)
        entity.timestamp.toTimestamp().let(::setTimestamp)
    })
    setData(WeatherRawData().apply {
        setIcon(data.icon)
        setTemperature(data.temperature)
        setTemperatureApparent(data.temperatureApparent)
        setDewPoint(data.dewPoint)
        setHumidity(data.humidity)
        setPressure(data.pressure)
        setWindSpeed(data.windSpeed)
        setWindGust(data.windGust)
        setWindDirection(data.windDirection)
        setCloudCover(data.cloudCover)
        setPrecipitationIntensity(data.precipitationIntensity)
        setPrecipitationProbability(data.precipitationProbability)
        setPrecipitationType(data.precipitationType)
        setUvIndex(data.uvIndex)
        setVisibility(data.visibility)
        if (solar != null) {
            setSolar(SolarRaw().apply {
                setAzimuth(solar.azimuth)
                setAltitude(solar.altitude)
                setDni(solar.dni)
                setGhi(solar.ghi)
                setDhi(solar.dhi)
                setEtr(solar.etr)
            })
        }
    })
}
