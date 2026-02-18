package com.particlesector.soniflac.feature.radio

import androidx.lifecycle.ViewModel
import com.particlesector.soniflac.core.model.Station
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class RadioUiState(
    val query: String = "",
    val stations: List<Station> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface RadioEvent {
    data class Search(val query: String) : RadioEvent
    data class ToggleFavorite(val station: Station) : RadioEvent
    data class Play(val station: Station) : RadioEvent
}

@HiltViewModel
class RadioViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RadioUiState())
    val uiState: StateFlow<RadioUiState> = _uiState.asStateFlow()

    fun onEvent(event: RadioEvent) {
        when (event) {
            is RadioEvent.Search -> {
                _uiState.value = _uiState.value.copy(query = event.query)
            }
            is RadioEvent.ToggleFavorite -> {
                // TODO: implement
            }
            is RadioEvent.Play -> {
                // TODO: implement
            }
        }
    }
}
