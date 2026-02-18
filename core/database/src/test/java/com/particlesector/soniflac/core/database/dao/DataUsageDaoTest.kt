package com.particlesector.soniflac.core.database.dao

import com.particlesector.soniflac.core.database.entity.DataUsageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DataUsageDaoTest {

    private val dao = InMemoryDataUsageDao()

    @BeforeEach
    fun setUp() = runTest {
        dao.clear()
    }

    @Test
    fun `insert and retrieve by date`() = runTest {
        val entity = DataUsageEntity("2025-01-15", 1024L, 60_000L)
        dao.insert(entity)

        val result = dao.getByDate("2025-01-15")
        assertEquals(1024L, result?.bytesStreamed)
    }

    @Test
    fun `getByDate returns null for missing date`() = runTest {
        assertNull(dao.getByDate("2025-01-15"))
    }

    @Test
    fun `upsert replaces existing entry`() = runTest {
        dao.insert(DataUsageEntity("2025-01-15", 1024L, 60_000L))
        dao.insert(DataUsageEntity("2025-01-15", 2048L, 120_000L))

        val result = dao.getByDate("2025-01-15")
        assertEquals(2048L, result?.bytesStreamed)
        assertEquals(120_000L, result?.streamingDurationMs)
    }

    @Test
    fun `getTotalBytesInRange sums correctly`() = runTest {
        dao.insert(DataUsageEntity("2025-01-10", 100L, 1000L))
        dao.insert(DataUsageEntity("2025-01-15", 200L, 2000L))
        dao.insert(DataUsageEntity("2025-01-20", 300L, 3000L))
        dao.insert(DataUsageEntity("2025-02-01", 999L, 9999L))

        val total = dao.getTotalBytesInRange("2025-01-01", "2025-01-31")
        assertEquals(600L, total)
    }

    @Test
    fun `getTotalBytesInRange returns null for empty range`() = runTest {
        val total = dao.getTotalBytesInRange("2025-01-01", "2025-01-31")
        assertNull(total)
    }

    @Test
    fun `observeByDate emits updates`() = runTest {
        val initial = dao.observeByDate("2025-01-15").first()
        assertNull(initial)

        dao.insert(DataUsageEntity("2025-01-15", 512L, 30_000L))
        val afterInsert = dao.observeByDate("2025-01-15").first()
        assertEquals(512L, afterInsert?.bytesStreamed)
    }

    @Test
    fun `observeTotalBytesInRange emits cumulative sum`() = runTest {
        dao.insert(DataUsageEntity("2025-01-10", 100L, 1000L))
        dao.insert(DataUsageEntity("2025-01-20", 200L, 2000L))

        val total = dao.observeTotalBytesInRange("2025-01-01", "2025-01-31").first()
        assertEquals(300L, total)
    }
}

private class InMemoryDataUsageDao : DataUsageDao {
    private val data = mutableMapOf<String, DataUsageEntity>()
    private val flow = MutableStateFlow<Map<String, DataUsageEntity>>(emptyMap())

    override suspend fun getByDate(date: String): DataUsageEntity? = data[date]

    override fun observeByDate(date: String): Flow<DataUsageEntity?> =
        flow.map { it[date] }

    override fun observeInRange(startDate: String, endDate: String): Flow<List<DataUsageEntity>> =
        flow.map { map -> map.values.filter { it.date in startDate..endDate }.sortedBy { it.date } }

    override fun observeTotalBytesInRange(startDate: String, endDate: String): Flow<Long> =
        flow.map { map ->
            map.values.filter { it.date in startDate..endDate }.sumOf { it.bytesStreamed }
        }

    override suspend fun getTotalBytesInRange(startDate: String, endDate: String): Long? {
        val entries = data.values.filter { it.date in startDate..endDate }
        return if (entries.isEmpty()) null else entries.sumOf { it.bytesStreamed }
    }

    override suspend fun insert(entity: DataUsageEntity) {
        data[entity.date] = entity
        flow.value = data.toMap()
    }

    suspend fun clear() {
        data.clear()
        flow.value = emptyMap()
    }
}
