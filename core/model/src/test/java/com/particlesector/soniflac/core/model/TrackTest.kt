package com.particlesector.soniflac.core.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TrackTest {

    @Test
    fun `track has correct default values`() {
        val track = Track(
            id = 1,
            title = "Autumn Leaves",
            artist = "Bill Evans",
            album = "Portrait in Jazz",
            duration = 300_000,
            path = "/storage/music/autumn_leaves.flac",
        )

        assertNull(track.albumArtUri)
        assertEquals(0, track.trackNumber)
        assertEquals("", track.codec)
        assertEquals(0, track.sampleRate)
        assertEquals(0, track.bitDepth)
        assertEquals(0, track.bitrate)
        assertEquals(0, track.fileSize)
    }

    @Test
    fun `track with full metadata`() {
        val track = Track(
            id = 1,
            title = "Autumn Leaves",
            artist = "Bill Evans",
            album = "Portrait in Jazz",
            duration = 300_000,
            path = "/storage/music/autumn_leaves.flac",
            albumArtUri = "content://media/external/audio/albumart/1",
            trackNumber = 3,
            codec = "FLAC",
            sampleRate = 44100,
            bitDepth = 16,
            bitrate = 923000,
            fileSize = 35_864_371,
        )

        assertEquals("FLAC", track.codec)
        assertEquals(44100, track.sampleRate)
        assertEquals(16, track.bitDepth)
        assertEquals(923000, track.bitrate)
    }

    @Test
    fun `track equality is by all fields`() {
        val track1 = Track(id = 1, title = "A", artist = "B", album = "C", duration = 100, path = "/a")
        val track2 = Track(id = 1, title = "A", artist = "B", album = "C", duration = 100, path = "/a")
        val track3 = Track(id = 2, title = "A", artist = "B", album = "C", duration = 100, path = "/a")

        assertEquals(track1, track2)
        assert(track1 != track3)
    }
}
