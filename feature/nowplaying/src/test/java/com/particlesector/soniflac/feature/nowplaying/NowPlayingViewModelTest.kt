package com.particlesector.soniflac.feature.nowplaying

import app.cash.turbine.test
import com.particlesector.soniflac.core.model.PlaybackItem
import com.particlesector.soniflac.core.model.PlaybackState
import com.particlesector.soniflac.core.model.RepeatMode
import com.particlesector.soniflac.core.testing.MainDispatcherRule
import com.particlesector.soniflac.core.testing.fakes.FakePlayerManager
import com.particlesector.soniflac.core.testing.fixtures.StationFixtures
import com.particlesector.soniflac.core.testing.fixtures.TrackFixtures
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherRule::class)
class NowPlayingViewModelTest {

    private val playerManager = FakePlayerManager()
    private lateinit var viewModel: NowPlayingViewModel

    @BeforeEach
    fun setUp() {
        viewModel = NowPlayingViewModel(playerManager)
    }

    @Test
    fun `initial state shows not playing`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("Not Playing", state.title)
        assertFalse(state.isPlaying)
        assertFalse(state.hasItem)
    }

    @Test
    fun `track playback shows track metadata`() = runTest {
        val track = TrackFixtures.autumnLeaves
        playerManager.emitState(
            PlaybackState(
                currentItem = PlaybackItem.TrackItem(track),
                isPlaying = true,
                durationMs = track.durationMs,
            )
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Autumn Leaves", state.title)
            assertEquals(track.artist, state.artist)
            assertEquals(track.album, state.album)
            assertTrue(state.isPlaying)
            assertTrue(state.hasItem)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `station playback shows station metadata`() = runTest {
        val station = StationFixtures.jazzFm
        playerManager.emitState(
            PlaybackState(
                currentItem = PlaybackItem.StationItem(station),
                isPlaying = true,
            )
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(station.name, state.title)
            assertEquals(station.country, state.artist)
            assertTrue(state.hasItem)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `togglePlayPause pauses when playing`() = runTest {
        playerManager.emitState(PlaybackState(isPlaying = true, currentItem = PlaybackItem.TrackItem(TrackFixtures.autumnLeaves)))

        viewModel.togglePlayPause()

        assertEquals(1, playerManager.pauseCount)
    }

    @Test
    fun `togglePlayPause resumes when paused`() = runTest {
        playerManager.emitState(PlaybackState(isPlaying = false, currentItem = PlaybackItem.TrackItem(TrackFixtures.autumnLeaves)))

        viewModel.togglePlayPause()

        assertEquals(1, playerManager.resumeCount)
    }

    @Test
    fun `seekTo delegates to player`() = runTest {
        viewModel.seekTo(30_000L)
        assertEquals(1, playerManager.seekCount)
    }

    @Test
    fun `skipNext delegates to player`() = runTest {
        viewModel.skipNext()
        assertEquals(1, playerManager.skipNextCount)
    }

    @Test
    fun `skipPrevious delegates to player`() = runTest {
        viewModel.skipPrevious()
        assertEquals(1, playerManager.skipPreviousCount)
    }

    @Test
    fun `toggleShuffle toggles shuffle state`() = runTest {
        playerManager.emitState(PlaybackState(isShuffleEnabled = false))

        viewModel.toggleShuffle()

        assertEquals(1, playerManager.setShuffleCount)
    }

    @Test
    fun `cycleRepeatMode cycles OFF to ALL to ONE to OFF`() = runTest {
        playerManager.emitState(PlaybackState(repeatMode = RepeatMode.OFF))
        viewModel.cycleRepeatMode()
        assertEquals(1, playerManager.setRepeatModeCount)

        playerManager.emitState(PlaybackState(repeatMode = RepeatMode.ALL))
        viewModel.cycleRepeatMode()
        assertEquals(2, playerManager.setRepeatModeCount)

        playerManager.emitState(PlaybackState(repeatMode = RepeatMode.ONE))
        viewModel.cycleRepeatMode()
        assertEquals(3, playerManager.setRepeatModeCount)
    }

    @Test
    fun `position and duration update from playback state`() = runTest {
        playerManager.emitState(
            PlaybackState(
                currentItem = PlaybackItem.TrackItem(TrackFixtures.autumnLeaves),
                positionMs = 45_000L,
                durationMs = 180_000L,
            )
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(45_000L, state.positionMs)
            assertEquals(180_000L, state.durationMs)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
