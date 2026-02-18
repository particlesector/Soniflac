package com.particlesector.soniflac.core.player

import com.particlesector.soniflac.core.model.PlaybackItem
import com.particlesector.soniflac.core.model.PlaybackState
import com.particlesector.soniflac.core.model.RepeatMode
import kotlinx.coroutines.flow.StateFlow

interface PlayerManager {
    val playbackState: StateFlow<PlaybackState>

    fun play(item: PlaybackItem)
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(positionMs: Long)
    fun skipNext()
    fun skipPrevious()
    fun setShuffleEnabled(enabled: Boolean)
    fun setRepeatMode(mode: RepeatMode)
    fun setQueue(items: List<PlaybackItem>, startIndex: Int = 0)
    fun release()
}
