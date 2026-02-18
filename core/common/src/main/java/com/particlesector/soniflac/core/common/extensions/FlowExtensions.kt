package com.particlesector.soniflac.core.common.extensions

import com.particlesector.soniflac.core.common.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }

fun tickerFlow(periodMs: Long, initialDelayMs: Long = 0L): Flow<Unit> = flow {
    if (initialDelayMs > 0) delay(initialDelayMs)
    while (true) {
        emit(Unit)
        delay(periodMs)
    }
}
