package com.particlesector.soniflac.core.testing.fakes

import com.particlesector.soniflac.core.model.DataUsage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDataUsageRepository {

    private val _todayUsage = MutableStateFlow<DataUsage?>(null)
    private val _monthlyBytes = MutableStateFlow(0L)

    var recordedBytes = 0L
        private set
    var recordedDurationMs = 0L
        private set

    fun observeToday(): Flow<DataUsage?> = _todayUsage
    fun observeMonthly(): Flow<Long> = _monthlyBytes

    suspend fun recordUsage(bytes: Long, durationMs: Long) {
        recordedBytes += bytes
        recordedDurationMs += durationMs
    }

    suspend fun getMonthlyTotal(): Long = _monthlyBytes.value

    fun setTodayUsage(usage: DataUsage?) {
        _todayUsage.value = usage
    }

    fun setMonthlyBytes(bytes: Long) {
        _monthlyBytes.value = bytes
    }
}
