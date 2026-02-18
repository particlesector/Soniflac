package com.particlesector.soniflac.core.database.dao

import com.particlesector.soniflac.core.database.entity.RecentStationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RecentStationDaoTest {

    private val dao = InMemoryRecentStationDao()

    @BeforeEach
    fun setUp() = runTest {
        dao.clear()
    }

    @Test
    fun `insert and observe recent station`() = runTest {
        dao.insert(createEntity("uuid-1", "Jazz FM", 1000L))

        val result = dao.observeAll().first()
        assertEquals(1, result.size)
        assertEquals("Jazz FM", result[0].name)
    }

    @Test
    fun `recents ordered by lastPlayedAt descending`() = runTest {
        dao.insert(createEntity("uuid-1", "Old", 1000L))
        dao.insert(createEntity("uuid-2", "New", 2000L))

        val result = dao.observeAll().first()
        assertEquals("New", result[0].name)
        assertEquals("Old", result[1].name)
    }

    @Test
    fun `insert replaces on conflict and updates timestamp`() = runTest {
        dao.insert(createEntity("uuid-1", "Station", 1000L))
        dao.insert(createEntity("uuid-1", "Station", 2000L))

        val result = dao.observeAll().first()
        assertEquals(1, result.size)
        assertEquals(2000L, result[0].lastPlayedAt)
    }

    @Test
    fun `getRecent respects limit`() = runTest {
        repeat(10) { i ->
            dao.insert(createEntity("uuid-$i", "Station $i", i.toLong()))
        }

        val result = dao.getRecent(5)
        assertEquals(5, result.size)
    }

    @Test
    fun `observeAll emits reactively`() = runTest {
        val initial = dao.observeAll().first()
        assertEquals(0, initial.size)

        dao.insert(createEntity("uuid-1", "Station"))
        val afterInsert = dao.observeAll().first()
        assertEquals(1, afterInsert.size)
    }

    private fun createEntity(
        uuid: String,
        name: String,
        lastPlayedAt: Long = System.currentTimeMillis(),
    ) = RecentStationEntity(
        stationUuid = uuid,
        name = name,
        url = "https://stream.example.com/$uuid",
        urlResolved = "https://stream.example.com/$uuid",
        codec = "FLAC",
        bitrate = 320,
        country = "US",
        language = "English",
        tags = "jazz",
        favicon = null,
        lastPlayedAt = lastPlayedAt,
    )
}

private class InMemoryRecentStationDao : RecentStationDao {
    private val stations = mutableListOf<RecentStationEntity>()
    private val flow = MutableStateFlow<List<RecentStationEntity>>(emptyList())

    override fun observeAll(): Flow<List<RecentStationEntity>> =
        flow.map { it.sortedByDescending { s -> s.lastPlayedAt } }

    override suspend fun getRecent(limit: Int): List<RecentStationEntity> =
        stations.sortedByDescending { it.lastPlayedAt }.take(limit)

    override suspend fun insert(entity: RecentStationEntity) {
        stations.removeAll { it.stationUuid == entity.stationUuid }
        stations.add(entity)
        flow.value = stations.toList()
    }

    override suspend fun delete(stationUuid: String) {
        stations.removeAll { it.stationUuid == stationUuid }
        flow.value = stations.toList()
    }

    suspend fun clear() {
        stations.clear()
        flow.value = emptyList()
    }
}
