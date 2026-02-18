package com.particlesector.soniflac.core.model

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val albumArtUri: String? = null,
    val trackNumber: Int = 0,
    val codec: String = "",
    val sampleRate: Int = 0,
    val bitDepth: Int = 0,
    val bitrate: Int = 0,
    val fileSize: Long = 0,
)
