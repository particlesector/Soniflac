package com.particlesector.soniflac.core.database.repository

import com.particlesector.soniflac.core.database.dao.DataUsageDao
import com.particlesector.soniflac.core.database.entity.DataUsageEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DataUsageRepositoryTest {

    private val dao = mockk<DataUsageDao>(relaxed = true)
    private lateinit var repository: DataUsageRepository

    @BeforeEach
    fun setUp() {
        repository = DataUsageRepository(dao)
    }

    @Test
    fun `observeToday maps entity to domain model`() = runTest {
        val today = LocalDate.now().toString()
        val entity = DataUsageEntity(today, 1024L, 60_000L)
        every { dao.observeByDate(today) } returns MutableStateFlow(entity)

        val result = repository.observeToday().first()
        assertEquals(1024L, result?.bytesStreamed)
        assertEquals(LocalDate.now(), result?.date)
    }

    @Test
    fun `observeToday returns null when no data`() = runTest {
        val today = LocalDate.now().toString()
        every { dao.observeByDate(today) } returns MutableStateFlow(null)

        val result = repository.observeToday().first()
        assertNull(result)
    }

    @Test
    fun `recordUsage creates new entry when none exists`() = runTest {
        val today = LocalDate.now().toString()
        coEvery { dao.getByDate(today) } returns null

        repository.recordUsage(500L, 10_000L)

        val slot = slot<DataUsageEntity>()
        coVerify { dao.insert(capture(slot)) }
        assertEquals(500L, slot.captured.bytesStreamed)
        assertEquals(10_000L, slot.captured.streamingDurationMs)
    }

    @Test
    fun `recordUsage accumulates to existing entry`() = runTest {
        val today = LocalDate.now().toString()
        val existing = DataUsageEntity(today, 100L, 5_000L)
        coEvery { dao.getByDate(today) } returns existing

        repository.recordUsage(200L, 10_000L)

        val slot = slot<DataUsageEntity>()
        coVerify { dao.insert(capture(slot)) }
        assertEquals(300L, slot.captured.bytesStreamed)
        assertEquals(15_000L, slot.captured.streamingDurationMs)
    }

    @Test
    fun `getMonthlyTotal returns zero when no data`() = runTest {
        coEvery { dao.getTotalBytesInRange(any(), any()) } returns null

        val total = repository.getMonthlyTotal()
        assertEquals(0L, total)
    }

    @Test
    fun `getMonthlyTotal returns sum`() = runTest {
        coEvery { dao.getTotalBytesInRange(any(), any()) } returns 5_000_000L

        val total = repository.getMonthlyTotal()
        assertEquals(5_000_000L, total)
    }
}
