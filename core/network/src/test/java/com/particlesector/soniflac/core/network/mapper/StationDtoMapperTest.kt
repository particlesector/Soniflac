package com.particlesector.soniflac.core.network.mapper

import com.particlesector.soniflac.core.network.dto.StationDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StationDtoMapperTest {

    @Test
    fun `toStation maps all fields correctly`() {
        val dto = StationDto(
            stationUuid = "abc-123",
            name = "  Jazz FM  ",
            url = "https://stream.example.com/jazz",
            urlResolved = "https://stream.example.com/jazz/resolved",
            codec = "MP3",
            bitrate = 128,
            country = "United States",
            language = "English",
            tags = "jazz,smooth,instrumental",
            favicon = "https://example.com/icon.png",
            votes = 42,
            clickCount = 100,
        )

        val station = dto.toStation()

        assertEquals("abc-123", station.stationUuid)
        assertEquals("Jazz FM", station.name)
        assertEquals("https://stream.example.com/jazz", station.url)
        assertEquals("https://stream.example.com/jazz/resolved", station.urlResolved)
        assertEquals("MP3", station.codec)
        assertEquals(128, station.bitrate)
        assertEquals("United States", station.country)
        assertEquals("English", station.language)
        assertEquals(listOf("jazz", "smooth", "instrumental"), station.tags)
        assertEquals("https://example.com/icon.png", station.favicon)
        assertEquals(42, station.votes)
        assertEquals(100, station.clickCount)
        assertFalse(station.isFavorite)
    }

    @Test
    fun `toStation handles blank urlResolved by falling back to url`() {
        val dto = StationDto(
            stationUuid = "abc",
            name = "Test",
            url = "https://stream.example.com",
            urlResolved = "",
            codec = "MP3",
            bitrate = 128,
            country = "US",
            language = "English",
            tags = "",
            favicon = "",
            votes = 0,
            clickCount = 0,
        )

        val station = dto.toStation()
        assertEquals("https://stream.example.com", station.urlResolved)
    }

    @Test
    fun `toStation handles blank tags as empty list`() {
        val dto = StationDto(
            stationUuid = "abc",
            name = "Test",
            url = "https://stream.example.com",
            tags = "",
        )

        val station = dto.toStation()
        assertEquals(emptyList<String>(), station.tags)
    }

    @Test
    fun `toStation handles blank favicon as null`() {
        val dto = StationDto(
            stationUuid = "abc",
            name = "Test",
            url = "https://stream.example.com",
            favicon = "",
        )

        val station = dto.toStation()
        assertNull(station.favicon)
    }

    @Test
    fun `toStation trims tag whitespace`() {
        val dto = StationDto(
            stationUuid = "abc",
            name = "Test",
            url = "https://stream.example.com",
            tags = "jazz , smooth , instrumental",
        )

        val station = dto.toStation()
        assertEquals(listOf("jazz", "smooth", "instrumental"), station.tags)
    }

    @Test
    fun `toStations maps list correctly`() {
        val dtos = listOf(
            StationDto(stationUuid = "1", name = "Station 1", url = "https://1.com"),
            StationDto(stationUuid = "2", name = "Station 2", url = "https://2.com"),
        )

        val stations = dtos.toStations()
        assertEquals(2, stations.size)
        assertEquals("1", stations[0].stationUuid)
        assertEquals("2", stations[1].stationUuid)
    }
}
