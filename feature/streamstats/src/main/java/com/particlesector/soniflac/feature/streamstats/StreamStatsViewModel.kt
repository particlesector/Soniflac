package com.particlesector.soniflac.feature.streamstats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.particlesector.soniflac.core.common.extensions.formatBitrate
import com.particlesector.soniflac.core.common.extensions.formatBytes
import com.particlesector.soniflac.core.common.extensions.formatChannels
import com.particlesector.soniflac.core.common.extensions.formatSampleRate
import com.particlesector.soniflac.core.database.repository.DataUsageRepository
import com.particlesector.soniflac.core.model.StreamStats
import com.particlesector.soniflac.core.player.StreamMetrics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StreamStatsViewModel @Inject constructor(
    private val streamMetrics: StreamMetrics,
    private val dataUsageRepository: DataUsageRepository,
) : ViewModel() {

    val uiState: StateFlow<StreamStatsUiState> = combine(
        streamMetrics.streamStats,
        dataUsageRepository.observeToday(),
        dataUsageRepository.observeMonthly(),
    ) { stats, todayUsage, monthlyBytes ->
        StreamStatsUiState(
            codec = stats.codec,
            sampleRate = stats.sampleRate.formatSampleRate(),
            bitDepth = "${stats.bitDepth}-bit",
            channels = stats.channels.formatChannels(),
            bitrate = stats.bitrate.formatBitrate(),
            networkThroughput = stats.networkThroughputKbps.formatBitrate(),
            bufferHealth = "%.1fs".format(stats.bufferHealthSeconds),
            sessionDataUsed = stats.sessionBytesUsed.formatBytes(),
            dailyDataUsed = (todayUsage?.bytesStreamed ?: 0L).formatBytes(),
            monthlyDataUsed = monthlyBytes.formatBytes(),
            isCollecting = stats.codec.isNotEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StreamStatsUiState(),
    )

    fun startCollecting() = streamMetrics.startCollecting()
    fun stopCollecting() = streamMetrics.stopCollecting()
}

data class StreamStatsUiState(
    val codec: String = "",
    val sampleRate: String = "",
    val bitDepth: String = "",
    val channels: String = "",
    val bitrate: String = "",
    val networkThroughput: String = "",
    val bufferHealth: String = "",
    val sessionDataUsed: String = "",
    val dailyDataUsed: String = "",
    val monthlyDataUsed: String = "",
    val isCollecting: Boolean = false,
)
