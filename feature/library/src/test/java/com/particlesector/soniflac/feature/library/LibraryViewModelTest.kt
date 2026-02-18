package com.particlesector.soniflac.feature.library

import app.cash.turbine.test
import com.particlesector.soniflac.core.testing.MainDispatcherRule
import com.particlesector.soniflac.core.testing.fakes.FakePlayerManager
import com.particlesector.soniflac.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherRule::class)
class LibraryViewModelTest {

    private val playerManager = FakePlayerManager()
    private lateinit var viewModel: LibraryViewModel

    @BeforeEach
    fun setUp() {
        viewModel = LibraryViewModel(playerManager)
    }

    @Test
    fun `initial state is empty`() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state.tracks.isEmpty())
        assertEquals(0, state.trackCount)
        assertFalse(state.isLoading)
    }

    @Test
    fun `setTracks updates track list`() = runTest {
        val tracks = TrackFixtures.createTracks(5)
        viewModel.setTracks(tracks)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(5, state.tracks.size)
            assertEquals(5, state.trackCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter by title narrows results`() = runTest {
        viewModel.setTracks(listOf(TrackFixtures.autumnLeaves, TrackFixtures.blueInGreen))
        viewModel.setFilter("autumn")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.tracks.size)
            assertEquals("Autumn Leaves", state.tracks[0].title)
            assertEquals(2, state.trackCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter by artist narrows results`() = runTest {
        viewModel.setTracks(listOf(TrackFixtures.autumnLeaves, TrackFixtures.blueInGreen))
        viewModel.setFilter("Miles")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.tracks.size)
            assertEquals("Blue in Green", state.tracks[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty filter shows all tracks`() = runTest {
        viewModel.setTracks(TrackFixtures.createTracks(3))
        viewModel.setFilter("xyz")
        viewModel.setFilter("")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(3, state.tracks.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `playTrack starts playback at correct index`() = runTest {
        val tracks = listOf(TrackFixtures.autumnLeaves, TrackFixtures.blueInGreen)
        viewModel.setTracks(tracks)

        viewModel.playTrack(TrackFixtures.blueInGreen)

        assertEquals(1, playerManager.playCount)
        assertEquals(1, playerManager.setQueueCount)
    }

    @Test
    fun `playAll starts from first track`() = runTest {
        viewModel.setTracks(TrackFixtures.createTracks(3))
        viewModel.playAll()

        assertEquals(1, playerManager.playCount)
        assertEquals(1, playerManager.setQueueCount)
    }

    @Test
    fun `playAll does nothing with empty tracks`() = runTest {
        viewModel.playAll()
        assertEquals(0, playerManager.playCount)
    }
}
