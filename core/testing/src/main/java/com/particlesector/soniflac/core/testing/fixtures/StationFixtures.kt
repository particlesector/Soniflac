package com.particlesector.soniflac.core.testing.fixtures

import com.particlesector.soniflac.core.model.Station

object StationFixtures {

    val jazzFm = Station(
        stationUuid = "station-jazz-fm",
        name = "Jazz FM",
        url = "https://stream.example.com/jazz",
        urlResolved = "https://stream.example.com/jazz/resolved",
        codec = "MP3",
        bitrate = 128,
        country = "United States",
        language = "English",
        tags = listOf("jazz", "smooth"),
        favicon = "https://example.com/jazz-icon.png",
        votes = 42,
        clickCount = 100,
    )

    val classicalRadio = Station(
        stationUuid = "station-classical",
        name = "Classical Radio",
        url = "https://stream.example.com/classical",
        urlResolved = "https://stream.example.com/classical/resolved",
        codec = "AAC",
        bitrate = 256,
        country = "Germany",
        language = "German",
        tags = listOf("classical", "orchestra"),
        favicon = "https://example.com/classical-icon.png",
        votes = 88,
        clickCount = 500,
    )

    val rockStation = Station(
        stationUuid = "station-rock",
        name = "Rock 101",
        url = "https://stream.example.com/rock",
        urlResolved = "https://stream.example.com/rock/resolved",
        codec = "MP3",
        bitrate = 192,
        country = "United Kingdom",
        language = "English",
        tags = listOf("rock", "alternative"),
        votes = 200,
        clickCount = 1000,
    )

    fun createStations(count: Int): List<Station> = (1..count).map { i ->
        Station(
            stationUuid = "station-$i",
            name = "Station $i",
            url = "https://stream.example.com/$i",
            urlResolved = "https://stream.example.com/$i",
            codec = "MP3",
            bitrate = 128,
            country = "US",
            language = "English",
            tags = listOf("tag$i"),
        )
    }
}
