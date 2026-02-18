package com.particlesector.soniflac.feature.streamstats

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class StreamStatsUiState(
    val codec: String = "",
    val sampleRate: Int = 0,
    val bitDepth: Int = 0,
    val channels: Int = 0,
    val bitrate: Int = 0,
    val fileSize: Long? = null,
    val isStreaming: Boolean = false,
    val networkThroughputKbps: Int = 0,
    val bufferHealthSeconds: Float = 0f,
    val sessionBytesUsed: Long = 0,
    val todayBytesUsed: Long = 0,
    val monthBytesUsed: Long = 0,
    val monthlyLimitBytes: Long? = null,
)

@HiltViewModel
class StreamStatsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StreamStatsUiState())
    val uiState: StateFlow<StreamStatsUiState> = _uiState.asStateFlow()
}
