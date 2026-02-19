package com.particlesector.soniflac.core.player

import com.particlesector.soniflac.core.common.Constants
import com.particlesector.soniflac.core.model.StreamStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoStreamMetrics @Inject constructor(
    private val bandwidthTracker: BandwidthTracker,
) : StreamMetrics {

    private val _streamStats = MutableStateFlow(StreamStats())
    override val streamStats: StateFlow<StreamStats> = _streamStats

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var collectJob: Job? = null

    override fun startCollecting() {
        collectJob?.cancel()
        collectJob = scope.launch {
            while (isActive) {
                _streamStats.value = _streamStats.value.copy(
                    sessionBytesUsed = bandwidthTracker.sessionBytes.value,
                )
                delay(Constants.STREAM_METRICS_POLL_INTERVAL_MS)
            }
        }
    }

    override fun stopCollecting() {
        collectJob?.cancel()
        collectJob = null
        _streamStats.value = StreamStats()
    }

    fun updateCodecInfo(
        codec: String,
        sampleRate: Int,
        bitDepth: Int,
        channels: Int,
        bitrate: Int,
    ) {
        _streamStats.value = _streamStats.value.copy(
            codec = codec,
            sampleRate = sampleRate,
            bitDepth = bitDepth,
            channels = channels,
            bitrate = bitrate,
        )
    }

    fun updateNetworkStats(networkThroughputKbps: Int, bufferHealthSeconds: Float) {
        _streamStats.value = _streamStats.value.copy(
            networkThroughputKbps = networkThroughputKbps,
            bufferHealthSeconds = bufferHealthSeconds,
        )
    }
}
