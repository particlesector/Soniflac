package com.particlesector.soniflac.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val stationUuid: String,
    val name: String,
    val url: String,
    val urlResolved: String,
    val codec: String,
    val bitrate: Int,
    val country: String,
    val language: String,
    val tags: List<String>,
    val favicon: String? = null,
    val votes: Int = 0,
    val clickCount: Int = 0,
    val isFavorite: Boolean = false,
)
