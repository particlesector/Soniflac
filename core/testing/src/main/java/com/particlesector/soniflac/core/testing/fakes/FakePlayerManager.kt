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

    var playCallCount = 0; private set
    var pauseCallCount = 0; private set
    var resumeCallCount = 0; private set
    var stopCallCount = 0; private set
    var lastSeekPosition: Long? = null; private set

    override fun play(item: PlaybackItem) {
        playCallCount++
        _playbackState.value = _playbackState.value.copy(
            isPlaying = true,
            currentItem = item,
        )
    }

    override fun pause() {
        pauseCallCount++
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    override fun resume() {
        resumeCallCount++
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
    }

    override fun stop() {
        stopCallCount++
        _playbackState.value = PlaybackState()
    }

    override fun seekTo(positionMs: Long) {
        lastSeekPosition = positionMs
        _playbackState.value = _playbackState.value.copy(position = positionMs)
    }

    override fun skipNext() {
        // No-op in fake
    }

    override fun skipPrevious() {
        // No-op in fake
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        _playbackState.value = _playbackState.value.copy(shuffleEnabled = enabled)
    }

    override fun setRepeatMode(mode: RepeatMode) {
        _playbackState.value = _playbackState.value.copy(repeatMode = mode)
    }

    override fun setQueue(items: List<PlaybackItem>, startIndex: Int) {
        // No-op in fake
    }

    override fun release() {
        _playbackState.value = PlaybackState()
    }

    fun emitState(state: PlaybackState) {
        _playbackState.value = state
    }
}
