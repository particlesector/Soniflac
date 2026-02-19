package com.particlesector.soniflac.core.model

data class PlaybackHistory(
    val id: Long,
    val itemType: String,
    val itemId: String,
    val title: String,
    val artist: String,
    val playedAt: Long,
    val durationMs: Long,
)
