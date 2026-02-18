package com.particlesector.soniflac.feature.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.particlesector.soniflac.core.model.PlaybackItem
import com.particlesector.soniflac.core.model.PlaybackState
import com.particlesector.soniflac.core.model.RepeatMode
import com.particlesector.soniflac.core.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playerManager: PlayerManager,
) : ViewModel() {

    val uiState: StateFlow<NowPlayingUiState> = playerManager.playbackState
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NowPlayingUiState(),
        )

    fun togglePlayPause() {
        val state = playerManager.playbackState.value
        if (state.isPlaying) playerManager.pause() else playerManager.resume()
    }

    fun seekTo(positionMs: Long) = playerManager.seekTo(positionMs)
    fun skipNext() = playerManager.skipNext()
    fun skipPrevious() = playerManager.skipPrevious()

    fun toggleShuffle() {
        val current = playerManager.playbackState.value.isShuffleEnabled
        playerManager.setShuffleEnabled(!current)
    }

    fun cycleRepeatMode() {
        val next = when (playerManager.playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        playerManager.setRepeatMode(next)
    }

    private fun PlaybackState.toUiState(): NowPlayingUiState {
        val (title, artist, album, artworkUri) = when (val item = currentItem) {
            is PlaybackItem.TrackItem -> listOf(
                item.track.title, item.track.artist, item.track.album, item.track.albumArtUri
            )
            is PlaybackItem.StationItem -> listOf(
                item.station.name, item.station.country, "", item.station.favicon
            )
            null -> listOf("Not Playing", "", "", null)
        }
        return NowPlayingUiState(
            title = title,
            artist = artist,
            album = album ?: "",
            artworkUri = artworkUri,
            isPlaying = isPlaying,
            positionMs = positionMs,
            durationMs = durationMs,
            isShuffleEnabled = isShuffleEnabled,
            repeatMode = repeatMode,
            hasItem = currentItem != null,
        )
    }
}

data class NowPlayingUiState(
    val title: String = "Not Playing",
    val artist: String = "",
    val album: String = "",
    val artworkUri: String? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val hasItem: Boolean = false,
)
