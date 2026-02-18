package com.particlesector.soniflac.core.player

import com.particlesector.soniflac.core.model.PlaybackItem
import com.particlesector.soniflac.core.model.Station
import com.particlesector.soniflac.core.model.Track
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class QueueManagerTest {

    private lateinit var queueManager: QueueManager

    private val track1 = PlaybackItem.TrackItem(
        Track(id = 1, title = "Track 1", artist = "Artist", album = "Album", duration = 100_000, path = "/a.flac"),
    )
    private val track2 = PlaybackItem.TrackItem(
        Track(id = 2, title = "Track 2", artist = "Artist", album = "Album", duration = 200_000, path = "/b.flac"),
    )
    private val track3 = PlaybackItem.TrackItem(
        Track(id = 3, title = "Track 3", artist = "Artist", album = "Album", duration = 150_000, path = "/c.flac"),
    )
    private val stationItem = PlaybackItem.StationItem(
        Station(
            stationUuid = "id", name = "Radio", url = "https://stream.com",
            urlResolved = "https://stream.com", codec = "MP3", bitrate = 128,
            country = "US", language = "English", tags = emptyList(),
        ),
    )

    @BeforeEach
    fun setup() {
        queueManager = QueueManager()
    }

    @Test
    fun `initial state is empty`() {
        assertEquals(emptyList<PlaybackItem>(), queueManager.queue.value)
        assertEquals(-1, queueManager.currentIndex.value)
        assertNull(queueManager.current())
        assertEquals(0, queueManager.size())
    }

    @Test
    fun `setQueue sets items and starting index`() {
        queueManager.setQueue(listOf(track1, track2, track3), startIndex = 1)

        assertEquals(3, queueManager.size())
        assertEquals(1, queueManager.currentIndex.value)
        assertEquals(track2, queueManager.current())
    }

    @Test
    fun `setQueue with default start index starts at 0`() {
        queueManager.setQueue(listOf(track1, track2))

        assertEquals(0, queueManager.currentIndex.value)
        assertEquals(track1, queueManager.current())
    }

    @Test
    fun `setQueue clamps out of range start index`() {
        queueManager.setQueue(listOf(track1, track2), startIndex = 10)
        assertEquals(1, queueManager.currentIndex.value)

        queueManager.setQueue(listOf(track1, track2), startIndex = -5)
        assertEquals(0, queueManager.currentIndex.value)
    }

    @Test
    fun `setQueue with empty list sets index to -1`() {
        queueManager.setQueue(emptyList())
        assertEquals(-1, queueManager.currentIndex.value)
        assertNull(queueManager.current())
    }

    @Test
    fun `next advances to next item`() {
        queueManager.setQueue(listOf(track1, track2, track3))

        val next = queueManager.next()
        assertEquals(track2, next)
        assertEquals(1, queueManager.currentIndex.value)
    }

    @Test
    fun `next returns null at end of queue`() {
        queueManager.setQueue(listOf(track1, track2), startIndex = 1)

        val next = queueManager.next()
        assertNull(next)
        assertEquals(1, queueManager.currentIndex.value)
    }

    @Test
    fun `previous goes back to previous item`() {
        queueManager.setQueue(listOf(track1, track2, track3), startIndex = 2)

        val prev = queueManager.previous()
        assertEquals(track2, prev)
        assertEquals(1, queueManager.currentIndex.value)
    }

    @Test
    fun `previous returns null at start of queue`() {
        queueManager.setQueue(listOf(track1, track2), startIndex = 0)

        val prev = queueManager.previous()
        assertNull(prev)
        assertEquals(0, queueManager.currentIndex.value)
    }

    @Test
    fun `hasNext returns true when there are more items`() {
        queueManager.setQueue(listOf(track1, track2))
        assertTrue(queueManager.hasNext())
    }

    @Test
    fun `hasNext returns false at end`() {
        queueManager.setQueue(listOf(track1, track2), startIndex = 1)
        assertFalse(queueManager.hasNext())
    }

    @Test
    fun `hasPrevious returns false at start`() {
        queueManager.setQueue(listOf(track1, track2), startIndex = 0)
        assertFalse(queueManager.hasPrevious())
    }

    @Test
    fun `hasPrevious returns true when not at start`() {
        queueManager.setQueue(listOf(track1, track2), startIndex = 1)
        assertTrue(queueManager.hasPrevious())
    }

    @Test
    fun `clear resets queue`() {
        queueManager.setQueue(listOf(track1, track2))
        queueManager.clear()

        assertEquals(0, queueManager.size())
        assertEquals(-1, queueManager.currentIndex.value)
        assertNull(queueManager.current())
    }

    @Test
    fun `queue works with station items`() {
        queueManager.setQueue(listOf(stationItem))

        assertEquals(stationItem, queueManager.current())
        assertEquals(1, queueManager.size())
    }

    @Test
    fun `sequential navigation through full queue`() {
        queueManager.setQueue(listOf(track1, track2, track3))

        assertEquals(track1, queueManager.current())
        assertEquals(track2, queueManager.next())
        assertEquals(track3, queueManager.next())
        assertNull(queueManager.next())
        assertEquals(track2, queueManager.previous())
        assertEquals(track1, queueManager.previous())
        assertNull(queueManager.previous())
    }
}
