package com.particlesector.soniflac.core.testing.fakes

import com.particlesector.soniflac.core.model.PlaybackItem
import com.particlesector.soniflac.core.model.PlaybackState
import com.particlesector.soniflac.core.model.RepeatMode
import com.particlesector.soniflac.core.player.PlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePlayerManager : PlayerManager {

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    var playCount = 0; private set
    var pauseCount = 0; private set
    var resumeCount = 0; private set
    var stopCount = 0; private set
    var seekCount = 0; private set
    var skipNextCount = 0; private set
    var skipPreviousCount = 0; private set
    var setShuffleCount = 0; private set
    var setRepeatModeCount = 0; private set
    var setQueueCount = 0; private set
    var lastSeekPosition: Long? = null; private set

    override fun play(item: PlaybackItem) {
        playCount++
        _playbackState.value = _playbackState.value.copy(
            isPlaying = true,
            currentItem = item,
        )
    }

    override fun pause() {
        pauseCount++
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    override fun resume() {
        resumeCount++
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
    }

    override fun stop() {
        stopCount++
        _playbackState.value = PlaybackState()
    }

    override fun seekTo(positionMs: Long) {
        seekCount++
        lastSeekPosition = positionMs
        _playbackState.value = _playbackState.value.copy(position = positionMs)
    }

    override fun skipNext() {
        skipNextCount++
    }

    override fun skipPrevious() {
        skipPreviousCount++
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        setShuffleCount++
        _playbackState.value = _playbackState.value.copy(shuffleEnabled = enabled)
    }

    override fun setRepeatMode(mode: RepeatMode) {
        setRepeatModeCount++
        _playbackState.value = _playbackState.value.copy(repeatMode = mode)
    }

    override fun setQueue(items: List<PlaybackItem>, startIndex: Int) {
        setQueueCount++
    }

    override fun release() {
        _playbackState.value = PlaybackState()
    }

    fun emitState(state: PlaybackState) {
        _playbackState.value = state
    }
}
