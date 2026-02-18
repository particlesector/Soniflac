package com.particlesector.soniflac.core.database.repository

import com.particlesector.soniflac.core.database.dao.FavoriteStationDao
import com.particlesector.soniflac.core.database.dao.RecentStationDao
import com.particlesector.soniflac.core.database.entity.FavoriteStationEntity
import com.particlesector.soniflac.core.model.Station
import com.particlesector.soniflac.core.network.RadioBrowserApi
import com.particlesector.soniflac.core.network.dto.StationDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StationRepositoryTest {

    private val api = mockk<RadioBrowserApi>(relaxed = true)
    private val favoriteDao = mockk<FavoriteStationDao>(relaxed = true)
    private val recentDao = mockk<RecentStationDao>(relaxed = true)

    private lateinit var repository: StationRepository

    @BeforeEach
    fun setUp() {
        every { favoriteDao.observeAll() } returns MutableStateFlow(emptyList())
        every { recentDao.observeAll() } returns MutableStateFlow(emptyList())
        repository = StationRepository(api, favoriteDao, recentDao)
    }

    @Test
    fun `observeFavorites maps entities to domain models`() = runTest {
        val entities = listOf(
            FavoriteStationEntity(
                stationUuid = "uuid-1", name = "Jazz FM",
                url = "https://stream.test/1", urlResolved = "https://stream.test/1",
                codec = "FLAC", bitrate = 320, country = "US",
                language = "English", tags = "jazz,music",
                favicon = null, votes = 100, clickCount = 50,
            )
        )
        every { favoriteDao.observeAll() } returns MutableStateFlow(entities)
        repository = StationRepository(api, favoriteDao, recentDao)

        val result = repository.observeFavorites().first()
        assertEquals(1, result.size)
        assertEquals("Jazz FM", result[0].name)
    }

    @Test
    fun `searchStations delegates to api`() = runTest {
        val dto = StationDto(
            stationuuid = "uuid-1", name = "Jazz FM",
            url = "https://stream.test/1", urlResolved = "https://stream.test/1",
            codec = "FLAC", bitrate = 320, country = "US",
            language = "English", tags = "jazz",
            favicon = null, votes = 100, clickcount = 50,
        )
        coEvery { api.searchStations(name = "jazz") } returns listOf(dto)

        val result = repository.searchStations("jazz")
        assertEquals(1, result.size)
        assertEquals("Jazz FM", result[0].name)
    }

    @Test
    fun `toggleFavorite removes existing favorite`() = runTest {
        coEvery { favoriteDao.isFavorite("uuid-1") } returns true

        val station = Station(stationUuid = "uuid-1", name = "Test")
        val result = repository.toggleFavorite(station)

        assertFalse(result)
        coVerify { favoriteDao.delete("uuid-1") }
    }

    @Test
    fun `toggleFavorite adds new favorite`() = runTest {
        coEvery { favoriteDao.isFavorite("uuid-1") } returns false

        val station = Station(stationUuid = "uuid-1", name = "Test")
        val result = repository.toggleFavorite(station)

        assertTrue(result)
        coVerify { favoriteDao.insert(any()) }
    }

    @Test
    fun `canAddFavorite returns true for premium`() = runTest {
        assertTrue(repository.canAddFavorite(isPremium = true))
    }

    @Test
    fun `canAddFavorite checks count for free tier`() = runTest {
        coEvery { favoriteDao.getCount() } returns 4
        assertTrue(repository.canAddFavorite(isPremium = false))

        coEvery { favoriteDao.getCount() } returns 5
        assertFalse(repository.canAddFavorite(isPremium = false))
    }

    @Test
    fun `reportClick swallows exceptions`() = runTest {
        coEvery { api.clickStation(any()) } throws RuntimeException("Network error")
        repository.reportClick("uuid-1")
    }
}
