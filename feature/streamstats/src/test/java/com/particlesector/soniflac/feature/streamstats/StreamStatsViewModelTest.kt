package com.particlesector.soniflac.feature.streamstats

import app.cash.turbine.test
import com.particlesector.soniflac.core.database.repository.DataUsageRepository
import com.particlesector.soniflac.core.model.DataUsage
import com.particlesector.soniflac.core.model.StreamStats
import com.particlesector.soniflac.core.testing.MainDispatcherRule
import com.particlesector.soniflac.core.testing.fakes.FakeStreamMetrics
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MainDispatcherRule::class)
class StreamStatsViewModelTest {

    private val streamMetrics = FakeStreamMetrics()
    private val dataUsageRepository = mockk<DataUsageRepository>()
    private val todayFlow = MutableStateFlow<DataUsage?>(null)
    private val monthlyFlow = MutableStateFlow(0L)

    private lateinit var viewModel: StreamStatsViewModel

    @BeforeEach
    fun setUp() {
        every { dataUsageRepository.observeToday() } returns todayFlow
        every { dataUsageRepository.observeMonthly() } returns monthlyFlow
        viewModel = StreamStatsViewModel(streamMetrics, dataUsageRepository)
    }

    @Test
    fun `initial state has empty values`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("", state.codec)
        assertFalse(state.isCollecting)
    }

    @Test
    fun `codec info updates from stream metrics`() = runTest {
        streamMetrics.emitStats(StreamStats(codec = "FLAC", sampleRate = 44100, bitDepth = 16, channels = 2, bitrate = 1411))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("FLAC", state.codec)
            assertEquals("44.1 kHz", state.sampleRate)
            assertEquals("16-bit", state.bitDepth)
            assertEquals("Stereo", state.channels)
            assertTrue(state.isCollecting)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mono channel displays correctly`() = runTest {
        streamMetrics.emitStats(StreamStats(codec = "AAC", channels = 1))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Mono", state.channels)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multi-channel displays channel count`() = runTest {
        streamMetrics.emitStats(StreamStats(codec = "FLAC", channels = 6))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("6ch", state.channels)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `daily data usage displays from repository`() = runTest {
        todayFlow.value = DataUsage(LocalDate.now(), 1_048_576L, 3600_000L)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("1.0 MB", state.dailyDataUsed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `monthly data usage displays from repository`() = runTest {
        monthlyFlow.value = 1_073_741_824L

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("1.0 GB", state.monthlyDataUsed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `session bytes display from stream stats`() = runTest {
        streamMetrics.emitStats(StreamStats(codec = "MP3", sessionBytesUsed = 5_242_880L))

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("5.0 MB", state.sessionDataUsed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startCollecting delegates to stream metrics`() {
        viewModel.startCollecting()
        assertEquals(1, streamMetrics.startCount)
    }

    @Test
    fun `stopCollecting delegates to stream metrics`() {
        viewModel.stopCollecting()
        assertEquals(1, streamMetrics.stopCount)
    }
}
