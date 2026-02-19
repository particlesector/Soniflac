package com.particlesector.soniflac.core.database.dao

import com.particlesector.soniflac.core.database.entity.FavoriteStationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FavoriteStationDaoTest {

    private val dao = InMemoryFavoriteStationDao()

    @BeforeEach
    fun setUp() = runTest {
        dao.clear()
    }

    @Test
    fun `insert and observe favorite station`() = runTest {
        val entity = createEntity("uuid-1", "Jazz FM")
        dao.insert(entity)

        val result = dao.observeAll().first()
        assertEquals(1, result.size)
        assertEquals("Jazz FM", result[0].name)
    }

    @Test
    fun `deleteByUuid removes station`() = runTest {
        dao.insert(createEntity("uuid-1", "Jazz FM"))
        dao.insert(createEntity("uuid-2", "Rock Station"))
        dao.deleteByUuid("uuid-1")

        val result = dao.observeAll().first()
        assertEquals(1, result.size)
        assertEquals("Rock Station", result[0].name)
    }

    @Test
    fun `isFavorite returns true for inserted station`() = runTest {
        dao.insert(createEntity("uuid-1", "Jazz FM"))
        assertTrue(dao.isFavorite("uuid-1"))
        assertFalse(dao.isFavorite("uuid-999"))
    }

    @Test
    fun `getCount returns correct count`() = runTest {
        assertEquals(0, dao.getCount())
        dao.insert(createEntity("uuid-1", "Station 1"))
        dao.insert(createEntity("uuid-2", "Station 2"))
        assertEquals(2, dao.getCount())
    }

    @Test
    fun `getByUuid returns matching station`() = runTest {
        val entity = createEntity("uuid-1", "Jazz FM")
        dao.insert(entity)

        val result = dao.getByUuid("uuid-1")
        assertEquals("Jazz FM", result?.name)
    }

    @Test
    fun `getByUuid returns null for missing station`() = runTest {
        val result = dao.getByUuid("nonexistent")
        assertEquals(null, result)
    }

    @Test
    fun `insert replaces on conflict`() = runTest {
        dao.insert(createEntity("uuid-1", "Old Name"))
        dao.insert(createEntity("uuid-1", "New Name"))

        val result = dao.observeAll().first()
        assertEquals(1, result.size)
        assertEquals("New Name", result[0].name)
    }

    private fun createEntity(uuid: String, name: String) = FavoriteStationEntity(
        stationUuid = uuid,
        name = name,
        url = "https://stream.example.com/$uuid",
        urlResolved = "https://stream.example.com/$uuid",
        codec = "FLAC",
        bitrate = 320,
        country = "US",
        language = "English",
        tags = "jazz,music",
        favicon = null,
        votes = 100,
        clickCount = 50,
    )
}

private class InMemoryFavoriteStationDao : FavoriteStationDao {
    private val stations = mutableListOf<FavoriteStationEntity>()
    private val flow = MutableStateFlow<List<FavoriteStationEntity>>(emptyList())

    override fun observeAll(): Flow<List<FavoriteStationEntity>> = flow

    override suspend fun getAll(): List<FavoriteStationEntity> = stations.toList()

    override suspend fun getByUuid(uuid: String): FavoriteStationEntity? =
        stations.find { it.stationUuid == uuid }

    override suspend fun getCount(): Int = stations.size

    override fun observeCount(): Flow<Int> = flow.map { it.size }

    override suspend fun insert(station: FavoriteStationEntity) {
        stations.removeAll { it.stationUuid == station.stationUuid }
        stations.add(station)
        flow.value = stations.toList()
    }

    override suspend fun delete(station: FavoriteStationEntity) {
        stations.removeAll { it.stationUuid == station.stationUuid }
        flow.value = stations.toList()
    }

    override suspend fun deleteByUuid(uuid: String) {
        stations.removeAll { it.stationUuid == uuid }
        flow.value = stations.toList()
    }

    override suspend fun isFavorite(uuid: String): Boolean =
        stations.any { it.stationUuid == uuid }

    override fun observeIsFavorite(uuid: String): Flow<Boolean> =
        flow.map { list -> list.any { it.stationUuid == uuid } }

    suspend fun clear() {
        stations.clear()
        flow.value = emptyList()
    }
}
