package com.particlesector.soniflac.core.network.dto

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StationDtoSerializationTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Test
    fun `deserialize station from API JSON`() {
        val jsonString = """
            {
                "stationuuid": "abc-123",
                "name": "Jazz FM",
                "url": "https://stream.example.com/jazz",
                "url_resolved": "https://stream.example.com/jazz/resolved",
                "codec": "MP3",
                "bitrate": 128,
                "country": "United States",
                "language": "english",
                "tags": "jazz,smooth",
                "favicon": "https://example.com/icon.png",
                "votes": 42,
                "clickcount": 100,
                "some_unknown_field": "ignored"
            }
        """.trimIndent()

        val dto = json.decodeFromString<StationDto>(jsonString)

        assertEquals("abc-123", dto.stationUuid)
        assertEquals("Jazz FM", dto.name)
        assertEquals("https://stream.example.com/jazz", dto.url)
        assertEquals("https://stream.example.com/jazz/resolved", dto.urlResolved)
        assertEquals("MP3", dto.codec)
        assertEquals(128, dto.bitrate)
        assertEquals("United States", dto.country)
        assertEquals("english", dto.language)
        assertEquals("jazz,smooth", dto.tags)
        assertEquals("https://example.com/icon.png", dto.favicon)
        assertEquals(42, dto.votes)
        assertEquals(100, dto.clickCount)
    }

    @Test
    fun `deserialize station with missing optional fields uses defaults`() {
        val jsonString = """
            {
                "stationuuid": "abc-123",
                "name": "Jazz FM",
                "url": "https://stream.example.com/jazz"
            }
        """.trimIndent()

        val dto = json.decodeFromString<StationDto>(jsonString)

        assertEquals("abc-123", dto.stationUuid)
        assertEquals("Jazz FM", dto.name)
        assertEquals("", dto.urlResolved)
        assertEquals("", dto.codec)
        assertEquals(0, dto.bitrate)
        assertEquals("", dto.country)
        assertEquals("", dto.tags)
        assertEquals(0, dto.votes)
    }

    @Test
    fun `deserialize list of stations`() {
        val jsonString = """
            [
                {"stationuuid": "1", "name": "Station 1", "url": "https://1.com"},
                {"stationuuid": "2", "name": "Station 2", "url": "https://2.com"}
            ]
        """.trimIndent()

        val dtos = json.decodeFromString<List<StationDto>>(jsonString)

        assertEquals(2, dtos.size)
        assertEquals("1", dtos[0].stationUuid)
        assertEquals("2", dtos[1].stationUuid)
    }

    @Test
    fun `deserialize country dto`() {
        val jsonString = """{"name": "United States", "stationcount": 12345}"""
        val dto = json.decodeFromString<CountryDto>(jsonString)

        assertEquals("United States", dto.name)
        assertEquals(12345, dto.stationCount)
    }

    @Test
    fun `deserialize tag dto`() {
        val jsonString = """{"name": "jazz", "stationcount": 5678}"""
        val dto = json.decodeFromString<TagDto>(jsonString)

        assertEquals("jazz", dto.name)
        assertEquals(5678, dto.stationCount)
    }
}
