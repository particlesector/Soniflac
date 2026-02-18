package com.particlesector.soniflac.feature.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.particlesector.soniflac.billing.BillingManager
import com.particlesector.soniflac.core.database.repository.StationRepository
import com.particlesector.soniflac.core.model.PlaybackItem
import com.particlesector.soniflac.core.model.Station
import com.particlesector.soniflac.core.player.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val stationRepository: StationRepository,
    private val playerManager: PlayerManager,
    private val billingManager: BillingManager,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<Station>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<RadioUiState> = combine(
        _query,
        _searchResults,
        _isLoading,
        _error,
        stationRepository.observeFavorites(),
    ) { query, results, loading, error, favorites ->
        val favoriteIds = favorites.map { it.stationUuid }.toSet()
        RadioUiState(
            query = query,
            stations = results.map { station ->
                station.copy(isFavorite = station.stationUuid in favoriteIds)
            },
            isLoading = loading,
            error = error,
            favorites = favorites,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RadioUiState(),
    )

    fun onEvent(event: RadioEvent) {
        when (event) {
            is RadioEvent.Search -> search(event.query)
            is RadioEvent.ToggleFavorite -> toggleFavorite(event.station)
            is RadioEvent.Play -> playStation(event.station)
            is RadioEvent.LoadTop -> loadTopStations()
            RadioEvent.ClearError -> _error.value = null
        }
    }

    private fun search(query: String) {
        _query.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _searchResults.value = stationRepository.searchStations(query)
            } catch (e: Exception) {
                _error.value = e.message ?: "Search failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadTopStations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _searchResults.value = stationRepository.getTopStations()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load stations"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun toggleFavorite(station: Station) {
        viewModelScope.launch {
            val isPremium = billingManager.isPremium.value
            if (!station.isFavorite && !stationRepository.canAddFavorite(isPremium)) {
                _error.value = "Free tier limited to 5 favorites. Upgrade to premium for unlimited."
                return@launch
            }
            stationRepository.toggleFavorite(station)
        }
    }

    private fun playStation(station: Station) {
        viewModelScope.launch {
            stationRepository.addRecent(station)
            stationRepository.reportClick(station.stationUuid)
        }
        playerManager.play(PlaybackItem.StationItem(station))
    }
}

data class RadioUiState(
    val query: String = "",
    val stations: List<Station> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val favorites: List<Station> = emptyList(),
)

sealed interface RadioEvent {
    data class Search(val query: String) : RadioEvent
    data class ToggleFavorite(val station: Station) : RadioEvent
    data class Play(val station: Station) : RadioEvent
    data object LoadTop : RadioEvent
    data object ClearError : RadioEvent
}
