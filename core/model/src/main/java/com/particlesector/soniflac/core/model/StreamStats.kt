package com.particlesector.soniflac.core.model

data class StreamStats(
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
)
