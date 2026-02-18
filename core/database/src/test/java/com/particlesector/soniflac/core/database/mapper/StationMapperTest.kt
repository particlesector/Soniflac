package com.particlesector.soniflac.core.database.mapper

import com.particlesector.soniflac.core.database.entity.FavoriteStationEntity
import com.particlesector.soniflac.core.database.entity.RecentStationEntity
import com.particlesector.soniflac.core.model.Station
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StationMapperTest {

    private val station = Station(
        stationUuid = "abc-123",
        name = "Jazz FM",
        url = "https://stream.example.com/jazz",
        urlResolved = "https://stream.example.com/jazz/resolved",
        codec = "MP3",
        bitrate = 128,
        country = "US",
        language = "English",
        tags = listOf("jazz", "smooth", "instrumental"),
        favicon = "https://example.com/icon.png",
        votes = 42,
        clickCount = 100,
        isFavorite = false,
    )

    @Test
    fun `toFavoriteEntity maps all fields correctly`() {
        val entity = station.toFavoriteEntity()

        assertEquals(station.stationUuid, entity.stationUuid)
        assertEquals(station.name, entity.name)
        assertEquals(station.url, entity.url)
        assertEquals(station.urlResolved, entity.urlResolved)
        assertEquals(station.codec, entity.codec)
        assertEquals(station.bitrate, entity.bitrate)
        assertEquals(station.country, entity.country)
        assertEquals(station.language, entity.language)
        assertEquals("jazz,smooth,instrumental", entity.tags)
        assertEquals(station.favicon, entity.favicon)
        assertEquals(station.votes, entity.votes)
        assertEquals(station.clickCount, entity.clickCount)
    }

    @Test
    fun `FavoriteStationEntity toStation maps all fields and sets isFavorite true`() {
        val entity = FavoriteStationEntity(
            stationUuid = "abc-123",
            name = "Jazz FM",
            url = "https://stream.example.com/jazz",
            urlResolved = "https://stream.example.com/jazz/resolved",
            codec = "MP3",
            bitrate = 128,
            country = "US",
            language = "English",
            tags = "jazz,smooth,instrumental",
            favicon = "https://example.com/icon.png",
            votes = 42,
            clickCount = 100,
        )

        val result = entity.toStation()

        assertEquals("abc-123", result.stationUuid)
        assertEquals("Jazz FM", result.name)
        assertEquals(listOf("jazz", "smooth", "instrumental"), result.tags)
        assertTrue(result.isFavorite)
    }

    @Test
    fun `FavoriteStationEntity with blank tags maps to empty list`() {
        val entity = FavoriteStationEntity(
            stationUuid = "abc", name = "Test", url = "http://a",
            urlResolved = "http://a", codec = "MP3", bitrate = 128,
            country = "US", language = "English", tags = "",
            favicon = null, votes = 0, clickCount = 0,
        )

        val result = entity.toStation()
        assertEquals(emptyList<String>(), result.tags)
    }

    @Test
    fun `toRecentEntity maps all fields correctly`() {
        val entity = station.toRecentEntity()

        assertEquals(station.stationUuid, entity.stationUuid)
        assertEquals(station.name, entity.name)
        assertEquals(station.url, entity.url)
        assertEquals("jazz,smooth,instrumental", entity.tags)
    }

    @Test
    fun `RecentStationEntity toStation maps correctly`() {
        val entity = RecentStationEntity(
            stationUuid = "abc-123",
            name = "Jazz FM",
            url = "https://stream.example.com/jazz",
            urlResolved = "https://stream.example.com/jazz/resolved",
            codec = "MP3",
            bitrate = 128,
            country = "US",
            language = "English",
            tags = "jazz,smooth",
            favicon = null,
        )

        val result = entity.toStation()

        assertEquals("abc-123", result.stationUuid)
        assertEquals(listOf("jazz", "smooth"), result.tags)
        assertEquals(false, result.isFavorite)
    }

    @Test
    fun `round trip station to favorite entity and back preserves data`() {
        val entity = station.toFavoriteEntity()
        val roundTripped = entity.toStation()

        assertEquals(station.stationUuid, roundTripped.stationUuid)
        assertEquals(station.name, roundTripped.name)
        assertEquals(station.url, roundTripped.url)
        assertEquals(station.codec, roundTripped.codec)
        assertEquals(station.bitrate, roundTripped.bitrate)
        assertEquals(station.tags, roundTripped.tags)
        assertTrue(roundTripped.isFavorite)
    }
}
