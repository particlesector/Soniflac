package com.particlesector.soniflac.core.testing.fakes

import com.particlesector.soniflac.core.model.StreamStats
import com.particlesector.soniflac.core.player.StreamMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeStreamMetrics : StreamMetrics {

    private val _streamStats = MutableStateFlow(StreamStats())
    override val streamStats: StateFlow<StreamStats> = _streamStats

    var isCollecting = false
        private set
    var startCount = 0
        private set
    var stopCount = 0
        private set

    override fun startCollecting() {
        isCollecting = true
        startCount++
    }

    override fun stopCollecting() {
        isCollecting = false
        stopCount++
    }

    fun emitStats(stats: StreamStats) {
        _streamStats.value = stats
    }
}
