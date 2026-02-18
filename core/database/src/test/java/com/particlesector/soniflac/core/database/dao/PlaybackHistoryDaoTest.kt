package com.particlesector.soniflac.core.database.dao

import com.particlesector.soniflac.core.database.entity.PlaybackHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlaybackHistoryDaoTest {

    private val dao = InMemoryPlaybackHistoryDao()

    @BeforeEach
    fun setUp() = runTest {
        dao.clear()
    }

    @Test
    fun `insert and get recent history`() = runTest {
        dao.insert(createEntity("track", "path/song.flac", "Song", "Artist", 1000L))

        val result = dao.getRecent(10)
        assertEquals(1, result.size)
        assertEquals("Song", result[0].title)
    }

    @Test
    fun `recent ordered by playedAt descending`() = runTest {
        dao.insert(createEntity("track", "path/old.flac", "Old", "A", playedAt = 1000L))
        dao.insert(createEntity("track", "path/new.flac", "New", "B", playedAt = 2000L))

        val result = dao.getRecent(10)
        assertEquals("New", result[0].title)
        assertEquals("Old", result[1].title)
    }

    @Test
    fun `getRecent respects limit`() = runTest {
        repeat(20) { i ->
            dao.insert(createEntity("track", "path/$i.flac", "Track $i", "A", playedAt = i.toLong()))
        }

        val result = dao.getRecent(5)
        assertEquals(5, result.size)
    }

    @Test
    fun `getRecentByType filters by type`() = runTest {
        dao.insert(createEntity("track", "path/song.flac", "Song", "A", playedAt = 1000L))
        dao.insert(createEntity("station", "uuid-1", "Station", "US", playedAt = 2000L))

        val tracks = dao.getRecentByType("track", 10)
        assertEquals(1, tracks.size)
        assertEquals("Song", tracks[0].title)

        val stations = dao.getRecentByType("station", 10)
        assertEquals(1, stations.size)
        assertEquals("Station", stations[0].title)
    }

    @Test
    fun `observeRecent emits updates`() = runTest {
        val initial = dao.observeRecent(10).first()
        assertEquals(0, initial.size)

        dao.insert(createEntity("track", "path/song.flac", "Song", "A"))
        val afterInsert = dao.observeRecent(10).first()
        assertEquals(1, afterInsert.size)
    }

    @Test
    fun `mixed types preserve order`() = runTest {
        dao.insert(createEntity("track", "p1", "T1", "A", playedAt = 100L))
        dao.insert(createEntity("station", "s1", "S1", "US", playedAt = 200L))
        dao.insert(createEntity("track", "p2", "T2", "B", playedAt = 300L))

        val result = dao.getRecent(10)
        assertEquals(3, result.size)
        assertEquals("T2", result[0].title)
        assertEquals("S1", result[1].title)
        assertEquals("T1", result[2].title)
    }

    private fun createEntity(
        type: String,
        id: String,
        title: String,
        artist: String,
        playedAt: Long = System.currentTimeMillis(),
    ) = PlaybackHistoryEntity(
        itemType = type,
        itemId = id,
        title = title,
        artist = artist,
        playedAt = playedAt,
        durationMs = 180_000,
    )
}

private class InMemoryPlaybackHistoryDao : PlaybackHistoryDao {
    private val history = mutableListOf<PlaybackHistoryEntity>()
    private val flow = MutableStateFlow<List<PlaybackHistoryEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun getRecent(limit: Int): List<PlaybackHistoryEntity> =
        history.sortedByDescending { it.playedAt }.take(limit)

    override fun observeRecent(limit: Int): Flow<List<PlaybackHistoryEntity>> =
        flow.map { it.sortedByDescending { e -> e.playedAt }.take(limit) }

    override suspend fun getRecentByType(type: String, limit: Int): List<PlaybackHistoryEntity> =
        history.filter { it.itemType == type }.sortedByDescending { it.playedAt }.take(limit)

    override suspend fun insert(entity: PlaybackHistoryEntity) {
        history.add(entity.copy(id = nextId++))
        flow.value = history.toList()
    }

    suspend fun clear() {
        history.clear()
        nextId = 1L
        flow.value = emptyList()
    }
}
