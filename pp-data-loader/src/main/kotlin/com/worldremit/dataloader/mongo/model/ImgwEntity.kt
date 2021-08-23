package com.worldremit.dataloader.mongo.model

import com.mongodb.client.model.geojson.Point
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class ImgwEntity(
    val id: String,
    val location: Point,
    val date: LocalDate, // TODO: change to timestamp
    val data: ImgwDataEntity
)

enum class ObservationStatus {
    OK, NO_MEASUREMENT, NO_OCCURRENCE, UNKNOWN
}

data class ImgwDataEntity(
    val meanDailyCloudCover: Double?, // [okta] // NOS
    val meanDailyCloudCoverStatus: ObservationStatus,

    val meanDailyWindSpeed: Double?, // [m/s] // FWS
    val meanDailyWindSpeedStatus: ObservationStatus,

    val meanDailyTemp: Double?, // ['C] // TEMP
    val meanDailyTempStatus: ObservationStatus,

    val meanDailyPressureWaterVapor: Double?, // [hPa] // CPW
    val meanDailyPressureWaterVaporStatus: ObservationStatus,

    val meanDailyHumidityRelative: Double?, // [%] // WLGS
    val meanDailyHumidityRelativeStatus: ObservationStatus,

    val meanDailyPressureStationLevel: Double?, // [hPa] // PPPS
    val meanDailyPressureStationLevelStatus: ObservationStatus,

    val meanDailyPressureSeaLevel: Double?, // [hPa] // PPPM
    val meanDailyPressureSeaLevelStatus: ObservationStatus,

    val precipitationSumDay: Double?, // [mm] // WODZ
    val precipitationSumDayStatus: ObservationStatus,

    val precipitationSumNigh: Double?, // [mm] // WONO
    val precipitationSumNighStatus: ObservationStatus,

    val tempMaxDaily: Double?, // ['C] // TMAX
    val tempMaxDailyStatus: ObservationStatus,

    val tempMinDaily: Double?, // ['C] // TMIN
    val tempMinDailyStatus: ObservationStatus,

    val tempMeanDaily: Double?, // ['C] // SDT
    val tempMeanDailyStatus: ObservationStatus,

    val tempMinGround: Double?, // ['C] // TMNG
    val tempMinGroundStatus: ObservationStatus,

    val precipitationSumDaily: Double?, // [mm] // SMDB
    val precipitationSumDailyStatus: ObservationStatus,

    val precipitationType: String?, // [S/W/ ]

    val snowCoverHeight: Double?, // [cm] // PKSN
    val snowCoverHeightStatus: ObservationStatus,

    val snowWaterEquivalent: Double?, // [mm/cm] // RWSN
    val snowWaterEquivalentStatus: ObservationStatus,

    val sunshineDuration: Double?, // [h] // USL
    val sunshineDurationStatus: ObservationStatus,

    val precipitationDurationRain: Double?, // [h] // DESZ
    val precipitationDurationRainStatus: ObservationStatus,

    val precipitationDurationSnow: Double?, // [h] // SNEG
    val precipitationDurationSnowStatus: ObservationStatus,

    val precipitationDurationRainAndSnow: Double?, // [h] // DISN
    val precipitationDurationRainAndSnowStatus: ObservationStatus,

    val precipitationDurationHail: Double?, // [h] // GRAD
    val precipitationDurationHailStatus: ObservationStatus,

    val durationFog: Double?, // [h] // MGLA
    val durationFogStatus: ObservationStatus,

    val durationMist: Double?, // [h] // ZMGL // zamglenie
    val durationMistStatus: ObservationStatus,

    val durationRimeIce: Double?, // [h] // SADZ
    val durationRimeIceStatus: ObservationStatus,

    val durationBlackIce: Double?, // [h] // GOLO
    val durationBlackIceStatus: ObservationStatus,

    val durationGroundBlizzardLow: Double?, // [h] // ZMNI
    val durationGroundBlizzardLowStatus: ObservationStatus,

    val durationGroundBlizzardHigh: Double?, // [h] // ZMWS
    val durationGroundBlizzardHighStatus: ObservationStatus,

    val durationHaze: Double?, // [h] // ZMET // zmetnienie (dust, smoke, etc.)
    val durationHazeStatus: ObservationStatus,

    val durationWind10: Double?, // [h] // FF10 // >=10m/s
    val durationWind10Status: ObservationStatus,

    val durationWind15: Double?, // [h] // FF15 // >15m/s
    val durationWind15Status: ObservationStatus,

    val durationThunderstorm: Double?, // [h] // BRZA
    val durationThunderstormStatus: ObservationStatus,

    val durationDew: Double?, // [h] // ROSA
    val durationDewStatus: ObservationStatus,

    val durationFrost: Double?, // [h] // SZRO
    val durationFrostStatus: ObservationStatus,

    val occurrenceSnowCover: Boolean?, // [0/1] // DZPS
    val occurrenceSnowCoverStatus: ObservationStatus,

    val occurrenceLightning: Boolean?, // [0/1] // DZBL

    val occurrenceLightningStatus: ObservationStatus,
    val groundState: String?, // [Z/R]

    val isothermLow: Double?, // [cm] // IZD

    val isothermLowStatus: ObservationStatus,
    val isothermHigh: Double?, // [cm] // IZG

    val isothermHighStatus: ObservationStatus,
    val irradiance: Double?, // [J/cm2] // AKTN, Aktynometria?

    val irradianceStatus: ObservationStatus
)