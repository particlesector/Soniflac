package com.particlesector.soniflac.core.model

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `station has correct default values`() {
        val station = Station(
            stationUuid = "abc-123",
            name = "Jazz FM",
            url = "https://stream.example.com/jazz",
            urlResolved = "https://stream.example.com/jazz",
            codec = "MP3",
            bitrate = 128,
            country = "US",
            language = "English",
            tags = listOf("jazz", "smooth"),
        )

        assertNull(station.favicon)
        assertEquals(0, station.votes)
        assertEquals(0, station.clickCount)
        assertFalse(station.isFavorite)
    }

    @Test
    fun `station serialization round trip`() {
        val station = Station(
            stationUuid = "abc-123",
            name = "Jazz FM",
            url = "https://stream.example.com/jazz",
            urlResolved = "https://stream.example.com/jazz",
            codec = "MP3",
            bitrate = 128,
            country = "US",
            language = "English",
            tags = listOf("jazz", "smooth"),
            favicon = "https://example.com/icon.png",
            votes = 42,
            clickCount = 100,
        )

        val encoded = json.encodeToString(Station.serializer(), station)
        val decoded = json.decodeFromString(Station.serializer(), encoded)

        assertEquals(station, decoded)
    }

    @Test
    fun `station copy with isFavorite`() {
        val station = Station(
            stationUuid = "abc-123",
            name = "Jazz FM",
            url = "https://stream.example.com/jazz",
            urlResolved = "https://stream.example.com/jazz",
            codec = "MP3",
            bitrate = 128,
            country = "US",
            language = "English",
            tags = emptyList(),
        )

        val favorited = station.copy(isFavorite = true)
        assertFalse(station.isFavorite)
        assertEquals(true, favorited.isFavorite)
        assertEquals(station.stationUuid, favorited.stationUuid)
    }
}
