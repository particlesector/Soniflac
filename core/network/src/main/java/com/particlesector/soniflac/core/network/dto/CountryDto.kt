package com.particlesector.soniflac.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    @SerialName("name") val name: String,
    @SerialName("stationcount") val stationCount: Int,
)

@Serializable
data class TagDto(
    @SerialName("name") val name: String,
    @SerialName("stationcount") val stationCount: Int,
)
