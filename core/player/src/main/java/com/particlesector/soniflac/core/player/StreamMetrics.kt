package com.particlesector.soniflac.core.player

import com.particlesector.soniflac.core.model.StreamStats
import kotlinx.coroutines.flow.StateFlow

interface StreamMetrics {
    val streamStats: StateFlow<StreamStats>

    fun startCollecting()
    fun stopCollecting()
}
