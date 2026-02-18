package com.particlesector.soniflac.core.testing.fakes

import com.particlesector.soniflac.core.network.RadioBrowserApi
import com.particlesector.soniflac.core.network.dto.CountryDto
import com.particlesector.soniflac.core.network.dto.StationDto
import com.particlesector.soniflac.core.network.dto.TagDto

class FakeRadioBrowserApi : RadioBrowserApi {

    var searchResult: List<StationDto> = emptyList()
    var topResult: List<StationDto> = emptyList()
    var shouldThrow: Exception? = null
    var clickCount = 0

    override suspend fun searchStations(
        name: String,
        limit: Int,
        offset: Int,
        order: String,
        reverse: Boolean,
    ): List<StationDto> {
        shouldThrow?.let { throw it }
        return searchResult
    }

    override suspend fun getTopStations(limit: Int, offset: Int): List<StationDto> {
        shouldThrow?.let { throw it }
        return topResult
    }

    override suspend fun getStationsByTag(tag: String, limit: Int): List<StationDto> {
        shouldThrow?.let { throw it }
        return searchResult
    }

    override suspend fun getStationsByCountry(country: String, limit: Int): List<StationDto> {
        shouldThrow?.let { throw it }
        return searchResult
    }

    override suspend fun getTags(limit: Int): List<TagDto> = emptyList()
    override suspend fun getCountries(limit: Int): List<CountryDto> = emptyList()

    override suspend fun clickStation(stationUuid: String) {
        shouldThrow?.let { throw it }
        clickCount++
    }
}
