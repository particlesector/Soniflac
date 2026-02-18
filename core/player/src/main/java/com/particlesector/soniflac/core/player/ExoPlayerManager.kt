package com.particlesector.soniflac.core.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.particlesector.soniflac.core.model.PlaybackItem
import com.particlesector.soniflac.core.model.PlaybackState
import com.particlesector.soniflac.core.model.RepeatMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val queueManager: QueueManager,
    private val audioFocusManager: AudioFocusManager,
    private val bandwidthTracker: BandwidthTracker,
) : PlayerManager {

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState

    private var exoPlayer: ExoPlayer? = null
    private var positionUpdateJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            updateState()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState()
            if (isPlaying) startPositionUpdates() else stopPositionUpdates()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateState()
        }
    }

    private fun getOrCreatePlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context).build().also {
            it.addListener(playerListener)
            exoPlayer = it
        }
    }

    override fun play(item: PlaybackItem) {
        val granted = audioFocusManager.requestFocus { focusState ->
            when (focusState) {
                AudioFocusManager.FocusState.GAINED -> resume()
                AudioFocusManager.FocusState.LOST -> stop()
                AudioFocusManager.FocusState.LOST_TRANSIENT -> pause()
                AudioFocusManager.FocusState.DUCK -> {
                    exoPlayer?.volume = 0.2f
                }
            }
        }
        if (!granted) return

        val player = getOrCreatePlayer()
        val uri = when (item) {
            is PlaybackItem.TrackItem -> item.track.filePath
            is PlaybackItem.StationItem -> item.station.urlResolved.ifEmpty { item.station.url }
        }
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
        _playbackState.value = _playbackState.value.copy(currentItem = item, isPlaying = true)
    }

    override fun pause() {
        exoPlayer?.pause()
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    override fun resume() {
        exoPlayer?.play()
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
    }

    override fun stop() {
        exoPlayer?.stop()
        audioFocusManager.abandonFocus()
        bandwidthTracker.stopSession()
        _playbackState.value = PlaybackState()
    }

    override fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(positionMs = positionMs)
    }

    override fun skipNext() {
        queueManager.next()?.let { play(it) }
    }

    override fun skipPrevious() {
        queueManager.previous()?.let { play(it) }
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        _playbackState.value = _playbackState.value.copy(isShuffleEnabled = enabled)
    }

    override fun setRepeatMode(mode: RepeatMode) {
        exoPlayer?.repeatMode = when (mode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = mode)
    }

    override fun setQueue(items: List<PlaybackItem>, startIndex: Int) {
        queueManager.setQueue(items, startIndex)
    }

    override fun release() {
        stopPositionUpdates()
        audioFocusManager.abandonFocus()
        bandwidthTracker.stopSession()
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
        _playbackState.value = PlaybackState()
    }

    private fun updateState() {
        val player = exoPlayer ?: return
        _playbackState.value = _playbackState.value.copy(
            isPlaying = player.isPlaying,
            positionMs = player.currentPosition,
            durationMs = player.duration.coerceAtLeast(0),
        )
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                updateState()
                delay(250)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }
}
