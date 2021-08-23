package com.worldremit.generator

import com.worldremit.avro.*
import com.worldremit.avro.PollutionFeature.*
import com.worldremit.avro.WeatherFeature.*
import com.worldremit.util.mergeMaps
import com.worldremit.util.oneHotEncoding
import java.util.*
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

object RandomMeasurement {

    fun randomPollutionRaw(pollutionId: Uuid, locationTime: LocationTime) = PollutionRaw().also {
        it.pollutionId = pollutionId
        it.locationTime = locationTime
        it.data = PollutionRawData().apply {
            pm10 = nextDouble(0.0, 190.0) // 604
            pm25 = nextDouble(0.0, 140.0) // 453
            bzn = nextDouble(0.0, 10.0) // 40
            co = nextDouble(0.0, 2800.0) // 7900
            no = nextDouble(0.0, 340.0) // 958
            no2 = nextDouble(0.0, 140.0) // 225
            nox = nextDouble(0.0, 650.0) // 1623
            so2 = nextDouble(0.0, 50.0) // 175
        }
    }

    fun randomWeatherRaw(weatherId: Uuid, locationTime: LocationTime) = WeatherRaw().also {
        it.weatherId = weatherId
        it.locationTime = locationTime
        it.data = WeatherRawData().apply {
            icon = UUID.randomUUID().toString()
            temperature = nextDouble(-14.0, 30.0) // -26,37
            temperatureApparent = nextDouble(-20.0, 37.0)
            dewPoint = nextDouble(-5.0, 5.0)
            humidity = nextDouble(0.1, 1.0)
            pressure = nextDouble(995.0, 1035.0) // 0->995
            windSpeed = nextDouble(0.0, 30.0) // 0,103
            windGust = nextDouble(0.0, 60.0) // 103->60
            windDirection = nextInt(0, 360)
            cloudCover = nextDouble(0.0, 1.0)
            listOf(null, "rain", "snow")[nextInt(3)].let { precipitation ->
                precipitationIntensity = precipitation?.let { nextDouble(0.0, 0.7) } ?: 0.0 // 2.0 -> 0.7
                precipitationProbability = precipitation?.let { nextDouble(0.0, 100.0) }
                precipitationType = precipitation
            }
            uvIndex = nextInt(0, 8) // 10->8
            visibility = nextDouble(0.0, 16.0)
            solar = SolarRaw().apply {
                azimuth = nextInt(56, 301)
                altitude = nextInt(1, 64)
                dni = nextInt(0, 911)
                ghi = nextInt(0, 894)
                dhi = nextDouble(0.0, 81.2)
                etr = nextDouble(1316.0, 1407.0)
            }
        }
    }

    fun randomMeasurementNormalized(measurementId: Uuid, locationTime: LocationTime) = MeasurementNormalized().also {
        it.measurementId = measurementId
        it.locationTime = locationTime
        it.weatherFeatures = WeatherFeature.values().associate { key -> key.toString() to randomValue() }
        it.pollutionFeatures = PollutionFeature.values().associate { key -> key.toString() to randomValue() }
        it.oneHotFeatures = mergeMaps(
            randomOneHot<WindDirection>(),
            randomOneHot<DayNight>(),
            randomOneHot<Season>()
        )
    }

    private inline fun <reified T : Enum<T>> randomOneHot(): Map<String,Double> = oneHotEncoding(randomEnum<T>())

    private inline fun <reified T : Enum<T>> randomEnum(): T = enumValues<T>().let { it[nextInt(it.size)] }

    private fun randomValue(): Double = nextDouble(-2.9, 2.9)

}