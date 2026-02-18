package com.particlesector.soniflac.core.testing.fixtures

import com.particlesector.soniflac.core.model.Track

object TrackFixtures {

    val autumnLeaves = Track(
        id = 1,
        title = "Autumn Leaves",
        artist = "Bill Evans",
        album = "Portrait in Jazz",
        duration = 300_000,
        path = "/storage/music/autumn_leaves.flac",
        codec = "FLAC",
        sampleRate = 44100,
        bitDepth = 16,
        bitrate = 923000,
        fileSize = 35_864_371,
        trackNumber = 1,
    )

    val blueInGreen = Track(
        id = 2,
        title = "Blue in Green",
        artist = "Miles Davis",
        album = "Kind of Blue",
        duration = 327_000,
        path = "/storage/music/blue_in_green.flac",
        codec = "FLAC",
        sampleRate = 96000,
        bitDepth = 24,
        bitrate = 2304000,
        fileSize = 94_371_840,
        trackNumber = 3,
    )

    val mp3Track = Track(
        id = 3,
        title = "Test MP3",
        artist = "Test Artist",
        album = "Test Album",
        duration = 180_000,
        path = "/storage/music/test.mp3",
        codec = "MP3",
        sampleRate = 44100,
        bitDepth = 0,
        bitrate = 320000,
        fileSize = 7_200_000,
    )

    fun createTracks(count: Int): List<Track> = (1..count).map { i ->
        Track(
            id = i.toLong(),
            title = "Track $i",
            artist = "Artist $i",
            album = "Album ${(i - 1) / 3 + 1}",
            duration = (180_000L + i * 10_000),
            path = "/storage/music/track_$i.flac",
        )
    }
}
