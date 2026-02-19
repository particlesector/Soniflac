package com.particlesector.soniflac.core.player

import com.particlesector.soniflac.core.common.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BandwidthTracker @Inject constructor() {

    private val _totalBytes = MutableStateFlow(0L)
    val totalBytes: StateFlow<Long> = _totalBytes

    private val _sessionBytes = MutableStateFlow(0L)
    val sessionBytes: StateFlow<Long> = _sessionBytes

    private val pendingBytes = AtomicLong(0L)
    private var flushJob: Job? = null
    @Volatile private var onFlush: (suspend (Long, Long) -> Unit)? = null
    @Volatile private var sessionStartTime = 0L

    private var scope: CoroutineScope? = null

    fun startSession(onFlush: suspend (Long, Long) -> Unit) {
        this.onFlush = onFlush
        _sessionBytes.value = 0L
        sessionStartTime = System.currentTimeMillis()
        pendingBytes.set(0L)
        startFlushLoop()
    }

    fun stopSession() {
        flushJob?.cancel()
        flushJob = null
        flush()
        scope?.cancel()
        scope = null
    }

    fun recordBytes(bytes: Long) {
        _totalBytes.value = _totalBytes.value + bytes
        _sessionBytes.value = _sessionBytes.value + bytes
        pendingBytes.addAndGet(bytes)
    }

    fun resetSession() {
        _sessionBytes.value = 0L
        sessionStartTime = System.currentTimeMillis()
        pendingBytes.set(0L)
    }

    private fun startFlushLoop() {
        scope?.cancel()
        val newScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope = newScope
        flushJob?.cancel()
        flushJob = newScope.launch {
            while (isActive) {
                delay(Constants.DATA_USAGE_FLUSH_INTERVAL_MS)
                flush()
            }
        }
    }

    private fun flush() {
        val bytes = pendingBytes.getAndSet(0L)
        val durationMs = System.currentTimeMillis() - sessionStartTime
        if (bytes > 0) {
            val callback = onFlush ?: return
            scope?.launch {
                callback(bytes, durationMs)
            }
        }
    }
}
