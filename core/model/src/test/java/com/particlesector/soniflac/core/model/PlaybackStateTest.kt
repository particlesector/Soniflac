package com.particlesector.soniflac.core.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class PlaybackStateTest {

    @Test
    fun `default playback state is idle`() {
        val state = PlaybackState()

        assertFalse(state.isPlaying)
        assertNull(state.currentItem)
        assertEquals(0L, state.position)
        assertEquals(0L, state.duration)
        assertFalse(state.shuffleEnabled)
        assertEquals(RepeatMode.OFF, state.repeatMode)
    }

    @Test
    fun `playback state with track item`() {
        val track = Track(
            id = 1, title = "Test", artist = "Artist",
            album = "Album", duration = 200_000, path = "/test.flac",
        )
        val state = PlaybackState(
            isPlaying = true,
            currentItem = PlaybackItem.TrackItem(track),
            position = 50_000,
            duration = 200_000,
        )

        assertEquals(true, state.isPlaying)
        assert(state.currentItem is PlaybackItem.TrackItem)
        assertEquals(track, (state.currentItem as PlaybackItem.TrackItem).track)
    }

    @Test
    fun `playback state with station item`() {
        val station = Station(
            stationUuid = "id",
            name = "Radio",
            url = "https://stream.example.com",
            urlResolved = "https://stream.example.com",
            codec = "MP3",
            bitrate = 128,
            country = "US",
            language = "English",
            tags = emptyList(),
        )
        val state = PlaybackState(
            isPlaying = true,
            currentItem = PlaybackItem.StationItem(station),
        )

        assert(state.currentItem is PlaybackItem.StationItem)
        assertEquals(station, (state.currentItem as PlaybackItem.StationItem).station)
    }

    @Test
    fun `repeat mode cycles correctly`() {
        val modes = RepeatMode.entries
        assertEquals(3, modes.size)
        assertEquals(RepeatMode.OFF, modes[0])
        assertEquals(RepeatMode.ONE, modes[1])
        assertEquals(RepeatMode.ALL, modes[2])
    }
}
