package com.particlesector.soniflac.core.database.dao

import com.particlesector.soniflac.core.database.entity.PlaylistEntity
import com.particlesector.soniflac.core.database.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlaylistDaoTest {

    private val dao = InMemoryPlaylistDao()

    @BeforeEach
    fun setUp() = runTest {
        dao.clear()
    }

    @Test
    fun `insertPlaylist and observe`() = runTest {
        val id = dao.insertPlaylist(PlaylistEntity(name = "My Playlist", createdAt = 1000L))

        val playlists = dao.observeAll().first()
        assertEquals(1, playlists.size)
        assertEquals("My Playlist", playlists[0].name)
        assertEquals(id, playlists[0].id)
    }

    @Test
    fun `getById returns matching playlist`() = runTest {
        val id = dao.insertPlaylist(PlaylistEntity(name = "Test"))
        val result = dao.getById(id)
        assertEquals("Test", result?.name)
    }

    @Test
    fun `getById returns null for missing id`() = runTest {
        assertNull(dao.getById(999L))
    }

    @Test
    fun `deletePlaylist removes playlist and its tracks`() = runTest {
        val id = dao.insertPlaylist(PlaylistEntity(name = "ToDelete"))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/a.flac", position = 0))

        dao.deletePlaylist(id)

        val playlists = dao.observeAll().first()
        assertEquals(0, playlists.size)
        val tracks = dao.observeTracks(id).first()
        assertEquals(0, tracks.size)
    }

    @Test
    fun `insertTrack and observeTracks`() = runTest {
        val id = dao.insertPlaylist(PlaylistEntity(name = "Playlist"))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/a.flac", position = 0))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/b.flac", position = 1))

        val tracks = dao.observeTracks(id).first()
        assertEquals(2, tracks.size)
        assertEquals("/path/a.flac", tracks[0].trackPath)
        assertEquals("/path/b.flac", tracks[1].trackPath)
    }

    @Test
    fun `removeTrack removes specific track`() = runTest {
        val id = dao.insertPlaylist(PlaylistEntity(name = "Playlist"))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/a.flac", position = 0))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/b.flac", position = 1))

        dao.removeTrack(id, "/path/a.flac")

        val tracks = dao.observeTracks(id).first()
        assertEquals(1, tracks.size)
        assertEquals("/path/b.flac", tracks[0].trackPath)
    }

    @Test
    fun `getTrackCount returns correct count`() = runTest {
        val id = dao.insertPlaylist(PlaylistEntity(name = "Playlist"))
        assertEquals(0, dao.getTrackCount(id))

        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/a.flac", position = 0))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/b.flac", position = 1))
        assertEquals(2, dao.getTrackCount(id))
    }

    @Test
    fun `tracks ordered by position ascending`() = runTest {
        val id = dao.insertPlaylist(PlaylistEntity(name = "Playlist"))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/c.flac", position = 2))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/a.flac", position = 0))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id, trackPath = "/path/b.flac", position = 1))

        val tracks = dao.observeTracks(id).first()
        assertEquals("/path/a.flac", tracks[0].trackPath)
        assertEquals("/path/b.flac", tracks[1].trackPath)
        assertEquals("/path/c.flac", tracks[2].trackPath)
    }

    @Test
    fun `multiple playlists are independent`() = runTest {
        val id1 = dao.insertPlaylist(PlaylistEntity(name = "Playlist 1"))
        val id2 = dao.insertPlaylist(PlaylistEntity(name = "Playlist 2"))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id1, trackPath = "/path/a.flac", position = 0))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id2, trackPath = "/path/b.flac", position = 0))
        dao.insertTrack(PlaylistTrackEntity(playlistId = id2, trackPath = "/path/c.flac", position = 1))

        assertEquals(1, dao.getTrackCount(id1))
        assertEquals(2, dao.getTrackCount(id2))
    }
}

private class InMemoryPlaylistDao : PlaylistDao {
    private val playlists = mutableListOf<PlaylistEntity>()
    private val tracks = mutableListOf<PlaylistTrackEntity>()
    private val playlistFlow = MutableStateFlow<List<PlaylistEntity>>(emptyList())
    private val trackFlow = MutableStateFlow<List<PlaylistTrackEntity>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<PlaylistEntity>> =
        playlistFlow.map { it.sortedByDescending { p -> p.createdAt } }

    override suspend fun getById(id: Long): PlaylistEntity? =
        playlists.find { it.id == id }

    override suspend fun insertPlaylist(playlist: PlaylistEntity): Long {
        val id = nextId++
        val withId = playlist.copy(id = id)
        playlists.removeAll { it.id == id }
        playlists.add(withId)
        playlistFlow.value = playlists.toList()
        return id
    }

    override suspend fun deletePlaylist(id: Long) {
        playlists.removeAll { it.id == id }
        tracks.removeAll { it.playlistId == id }
        playlistFlow.value = playlists.toList()
        trackFlow.value = tracks.toList()
    }

    override suspend fun insertTrack(track: PlaylistTrackEntity) {
        tracks.removeAll { it.playlistId == track.playlistId && it.trackPath == track.trackPath }
        tracks.add(track)
        trackFlow.value = tracks.toList()
    }

    override suspend fun removeTrack(playlistId: Long, trackPath: String) {
        tracks.removeAll { it.playlistId == playlistId && it.trackPath == trackPath }
        trackFlow.value = tracks.toList()
    }

    override fun observeTracks(playlistId: Long): Flow<List<PlaylistTrackEntity>> =
        trackFlow.map { list ->
            list.filter { it.playlistId == playlistId }.sortedBy { it.position }
        }

    override suspend fun getTrackCount(playlistId: Long): Int =
        tracks.count { it.playlistId == playlistId }

    suspend fun clear() {
        playlists.clear()
        tracks.clear()
        nextId = 1L
        playlistFlow.value = emptyList()
        trackFlow.value = emptyList()
    }
}
