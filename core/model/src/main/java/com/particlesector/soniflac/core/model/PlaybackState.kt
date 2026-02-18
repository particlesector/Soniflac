package com.particlesector.soniflac.core.model

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentItem: PlaybackItem? = null,
    val position: Long = 0L,
    val duration: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
)

sealed interface PlaybackItem {
    data class TrackItem(val track: Track) : PlaybackItem
    data class StationItem(val station: Station) : PlaybackItem
}

enum class RepeatMode {
    OFF,
    ONE,
    ALL,
}
