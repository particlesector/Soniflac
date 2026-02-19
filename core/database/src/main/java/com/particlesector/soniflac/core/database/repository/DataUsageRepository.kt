package com.particlesector.soniflac.core.database.repository

import com.particlesector.soniflac.core.database.dao.DataUsageDao
import com.particlesector.soniflac.core.database.entity.DataUsageEntity
import com.particlesector.soniflac.core.model.DataUsage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUsageRepository @Inject constructor(
    private val dataUsageDao: DataUsageDao,
) {
    fun observeToday(): Flow<DataUsage?> = flow {
        val today = LocalDate.now().toString()
        emitAll(
            dataUsageDao.observeByDate(today).map { entity ->
                entity?.toDomain()
            }
        )
    }

    fun observeMonthly(): Flow<Long> = flow {
        val month = YearMonth.now()
        val start = month.atDay(1).toString()
        val end = month.atEndOfMonth().toString()
        emitAll(dataUsageDao.observeTotalBytesInRange(start, end))
    }

    suspend fun recordUsage(bytes: Long, durationMs: Long) {
        val today = LocalDate.now().toString()
        val existing = dataUsageDao.getByDate(today)
        if (existing != null) {
            dataUsageDao.insert(
                existing.copy(
                    bytesStreamed = existing.bytesStreamed + bytes,
                    streamingDurationMs = existing.streamingDurationMs + durationMs,
                )
            )
        } else {
            dataUsageDao.insert(
                DataUsageEntity(
                    date = today,
                    bytesStreamed = bytes,
                    streamingDurationMs = durationMs,
                )
            )
        }
    }

    suspend fun getMonthlyTotal(): Long {
        val month = YearMonth.now()
        val start = month.atDay(1).toString()
        val end = month.atEndOfMonth().toString()
        return dataUsageDao.getTotalBytesInRange(start, end) ?: 0L
    }

    private fun DataUsageEntity.toDomain(): DataUsage = DataUsage(
        date = LocalDate.parse(date),
        bytesStreamed = bytesStreamed,
        streamingDurationMs = streamingDurationMs,
    )
}
