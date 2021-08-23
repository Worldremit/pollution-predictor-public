package com.worldremit.weather

import com.worldremit.DevData
import com.worldremit.avro.*
import com.worldremit.util.randomUuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class WeatherRawTransformerKtTest {

    private val transformer = WeatherRawTransformer()

    @Test
    fun `should transform WeatherRaw to WeatherRecord`() {

        val weatherRaw = createWeatherRaw {
            it.precipitationIntensity = 0.1
            it.precipitationType = PrecipitationType.SNOW.name
            it.windDirection = 35
            it.windSpeed = 10.0
        }

        transformer.toWeatherRecord(weatherRaw).also {
            assertThat(it.windDirection).isEqualTo(WindDirection.N)
            assertThat(it.features[WeatherFeature.WIND_SPEED.name]).isEqualTo(10.0)
            assertThat(it.features[WeatherFeature.SNOW_INTENSITY.name]).isEqualTo(0.1)
        }
    }

    @Test
    fun `should transform WeatherRaw with invalid WindDirection`() {
        val weatherRaw = createWeatherRaw {
            it.windSpeed = 10.0
            it.windDirection = 365
        }

        transformer.toWeatherRecord(weatherRaw).also {
            assertThat(it.windDirection).isEqualTo(WindDirection.UNKNOWN)
        }
    }

    @Test
    fun `should transform WeatherRaw with invalid PrecipitationType`() {
        val weatherRaw = createWeatherRaw {
            it.precipitationType = "foo"
            it.precipitationIntensity = 10.0
        }

        transformer.toWeatherRecord(weatherRaw).also {
            assertThat(it.features[WeatherFeature.SNOW_INTENSITY.name]).isZero
            assertThat(it.features[WeatherFeature.RAIN_INTENSITY.name]).isZero
        }
    }

    @Test
    fun `should skip temperature if it's too high`() {
        val weatherRaw = createWeatherRaw {
            it.temperature = 105.0
        }

        transformer.toWeatherRecord(weatherRaw).also {
            assertThat(it.features[WeatherFeature.TEMPERATURE.name]).isNull()
        }
    }

    private fun createWeatherRaw(block: (WeatherRawData) -> Unit) = WeatherRaw().apply {
        weatherId = randomUuid()
        locationTime = DevData.LOCATION_TIME
        data = WeatherRawData().also(block::invoke)
    }

}