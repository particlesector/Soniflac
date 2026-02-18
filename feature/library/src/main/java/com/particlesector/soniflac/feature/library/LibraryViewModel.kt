package com.particlesector.soniflac.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.particlesector.soniflac.core.model.PlaybackItem
import com.particlesector.soniflac.core.model.Track
import com.particlesector.soniflac.core.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val playerManager: PlayerManager,
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _filter = MutableStateFlow("")

    val uiState: StateFlow<LibraryUiState> = combine(
        _tracks,
        _isLoading,
        _error,
        _filter,
    ) { tracks, loading, error, filter ->
        val filtered = if (filter.isBlank()) tracks
        else tracks.filter { track ->
            track.title.contains(filter, ignoreCase = true) ||
                track.artist.contains(filter, ignoreCase = true) ||
                track.album.contains(filter, ignoreCase = true)
        }
        LibraryUiState(
            tracks = filtered,
            trackCount = tracks.size,
            isLoading = loading,
            error = error,
            filter = filter,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState(),
    )

    fun setFilter(query: String) {
        _filter.value = query
    }

    fun setTracks(tracks: List<Track>) {
        _tracks.value = tracks
    }

    fun playTrack(track: Track) {
        val allTracks = _tracks.value
        val index = allTracks.indexOf(track).coerceAtLeast(0)
        val items = allTracks.map { PlaybackItem.TrackItem(it) }
        playerManager.setQueue(items, index)
        playerManager.play(PlaybackItem.TrackItem(track))
    }

    fun playAll() {
        val tracks = uiState.value.tracks
        if (tracks.isEmpty()) return
        val items = tracks.map { PlaybackItem.TrackItem(it) }
        playerManager.setQueue(items, 0)
        playerManager.play(items.first())
    }

    fun clearError() {
        _error.value = null
    }
}

data class LibraryUiState(
    val tracks: List<Track> = emptyList(),
    val trackCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: String = "",
)
