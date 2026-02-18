package com.particlesector.soniflac.feature.radio

import app.cash.turbine.test
import com.particlesector.soniflac.core.testing.MainDispatcherRule
import com.particlesector.soniflac.core.testing.fakes.FakeBillingManager
import com.particlesector.soniflac.core.testing.fakes.FakePlayerManager
import com.particlesector.soniflac.core.testing.fixtures.StationFixtures
import com.particlesector.soniflac.core.database.repository.StationRepository
import com.particlesector.soniflac.core.model.Station
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherRule::class)
class RadioViewModelTest {

    private val stationRepository = mockk<StationRepository>(relaxed = true)
    private val playerManager = FakePlayerManager()
    private val billingManager = FakeBillingManager()

    private lateinit var viewModel: RadioViewModel

    @BeforeEach
    fun setUp() {
        every { stationRepository.observeFavorites() } returns MutableStateFlow(emptyList())
        viewModel = RadioViewModel(stationRepository, playerManager, billingManager)
    }

    @Test
    fun `initial state is empty`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.stations.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `search updates query and fetches results`() = runTest {
        val stations = listOf(StationFixtures.jazzFm)
        coEvery { stationRepository.searchStations("jazz") } returns stations

        viewModel.onEvent(RadioEvent.Search("jazz"))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("jazz", state.query)
            assertEquals(1, state.stations.size)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search with blank query clears results`() = runTest {
        viewModel.onEvent(RadioEvent.Search(""))

        val state = viewModel.uiState.value
        assertTrue(state.stations.isEmpty())
        assertEquals("", state.query)
    }

    @Test
    fun `search error sets error message`() = runTest {
        coEvery { stationRepository.searchStations(any()) } throws RuntimeException("Network error")

        viewModel.onEvent(RadioEvent.Search("test"))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadTop fetches top stations`() = runTest {
        val stations = StationFixtures.createStations(3)
        coEvery { stationRepository.getTopStations(any()) } returns stations

        viewModel.onEvent(RadioEvent.LoadTop)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(3, state.stations.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite succeeds for premium user`() = runTest {
        billingManager.setPremium(true)
        coEvery { stationRepository.canAddFavorite(true) } returns true

        viewModel.onEvent(RadioEvent.ToggleFavorite(StationFixtures.jazzFm))

        coVerify { stationRepository.toggleFavorite(StationFixtures.jazzFm) }
    }

    @Test
    fun `toggleFavorite blocked at free limit`() = runTest {
        billingManager.setPremium(false)
        coEvery { stationRepository.canAddFavorite(false) } returns false

        val station = StationFixtures.jazzFm.copy(isFavorite = false)
        viewModel.onEvent(RadioEvent.ToggleFavorite(station))

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.error?.contains("5 favorites") == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite always allows unfavoriting`() = runTest {
        billingManager.setPremium(false)
        val station = StationFixtures.jazzFm.copy(isFavorite = true)

        viewModel.onEvent(RadioEvent.ToggleFavorite(station))

        coVerify { stationRepository.toggleFavorite(station) }
    }

    @Test
    fun `play station starts playback and records recent`() = runTest {
        viewModel.onEvent(RadioEvent.Play(StationFixtures.jazzFm))

        assertEquals(1, playerManager.playCount)
        coVerify { stationRepository.addRecent(StationFixtures.jazzFm) }
        coVerify { stationRepository.reportClick(StationFixtures.jazzFm.stationUuid) }
    }

    @Test
    fun `clearError clears error state`() = runTest {
        coEvery { stationRepository.searchStations(any()) } throws RuntimeException("Error")
        viewModel.onEvent(RadioEvent.Search("test"))

        viewModel.onEvent(RadioEvent.ClearError)

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stations marked with favorite status from favorites flow`() = runTest {
        val favFlow = MutableStateFlow(listOf(StationFixtures.jazzFm.copy(isFavorite = true)))
        every { stationRepository.observeFavorites() } returns favFlow

        viewModel = RadioViewModel(stationRepository, playerManager, billingManager)
        val searchResults = listOf(StationFixtures.jazzFm, StationFixtures.classicalRadio)
        coEvery { stationRepository.searchStations("test") } returns searchResults

        viewModel.onEvent(RadioEvent.Search("test"))

        viewModel.uiState.test {
            val state = awaitItem()
            val jazz = state.stations.find { it.stationUuid == StationFixtures.jazzFm.stationUuid }
            assertTrue(jazz?.isFavorite == true)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
