package com.particlesector.soniflac.feature.streamstats

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StreamStatsViewModelTest {

    @Test
    fun `initial state has empty values`() {
        val viewModel = StreamStatsViewModel()
        val state = viewModel.uiState.value

        assertEquals("", state.codec)
        assertEquals(0, state.sampleRate)
        assertEquals(0, state.bitDepth)
        assertEquals(0, state.channels)
        assertEquals(0, state.bitrate)
        assertNull(state.fileSize)
        assertFalse(state.isStreaming)
        assertEquals(0, state.networkThroughputKbps)
        assertEquals(0f, state.bufferHealthSeconds)
        assertEquals(0L, state.sessionBytesUsed)
        assertEquals(0L, state.todayBytesUsed)
        assertEquals(0L, state.monthBytesUsed)
        assertNull(state.monthlyLimitBytes)
    }
}
