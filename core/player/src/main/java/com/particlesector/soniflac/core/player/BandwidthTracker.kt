package com.particlesector.soniflac.core.player

import com.particlesector.soniflac.core.common.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BandwidthTracker @Inject constructor() {

    private val _totalBytes = MutableStateFlow(0L)
    val totalBytes: StateFlow<Long> = _totalBytes

    private val _sessionBytes = MutableStateFlow(0L)
    val sessionBytes: StateFlow<Long> = _sessionBytes

    private var pendingBytes = 0L
    private var flushJob: Job? = null
    private var onFlush: (suspend (Long, Long) -> Unit)? = null
    private var sessionStartTime = 0L

    fun startSession(onFlush: suspend (Long, Long) -> Unit) {
        this.onFlush = onFlush
        _sessionBytes.value = 0L
        sessionStartTime = System.currentTimeMillis()
        pendingBytes = 0L
        startFlushLoop()
    }

    fun stopSession() {
        flushJob?.cancel()
        flushJob = null
        flush()
    }

    fun recordBytes(bytes: Long) {
        _totalBytes.value += bytes
        _sessionBytes.value += bytes
        pendingBytes += bytes
    }

    fun resetSession() {
        _sessionBytes.value = 0L
        sessionStartTime = System.currentTimeMillis()
        pendingBytes = 0L
    }

    private fun startFlushLoop() {
        flushJob?.cancel()
        flushJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(Constants.DATA_USAGE_FLUSH_INTERVAL_MS)
                flush()
            }
        }
    }

    private fun flush() {
        val bytes = pendingBytes
        val durationMs = System.currentTimeMillis() - sessionStartTime
        if (bytes > 0) {
            pendingBytes = 0L
            CoroutineScope(Dispatchers.IO).launch {
                onFlush?.invoke(bytes, durationMs)
            }
        }
    }
}
