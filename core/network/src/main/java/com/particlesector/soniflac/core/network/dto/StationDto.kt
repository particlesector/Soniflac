package com.particlesector.soniflac.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationDto(
    @SerialName("stationuuid") val stationUuid: String,
    @SerialName("name") val name: String,
    @SerialName("url") val url: String,
    @SerialName("url_resolved") val urlResolved: String = "",
    @SerialName("codec") val codec: String = "",
    @SerialName("bitrate") val bitrate: Int = 0,
    @SerialName("country") val country: String = "",
    @SerialName("language") val language: String = "",
    @SerialName("tags") val tags: String = "",
    @SerialName("favicon") val favicon: String = "",
    @SerialName("votes") val votes: Int = 0,
    @SerialName("clickcount") val clickCount: Int = 0,
)
