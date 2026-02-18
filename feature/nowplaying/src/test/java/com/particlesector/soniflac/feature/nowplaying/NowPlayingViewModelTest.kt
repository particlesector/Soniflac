package com.particlesector.soniflac.feature.nowplaying

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class NowPlayingViewModelTest {

    @Test
    fun `initial state shows nothing playing`() {
        val viewModel = NowPlayingViewModel()
        val state = viewModel.uiState.value

        assertEquals("", state.title)
        assertEquals("", state.artist)
        assertEquals("", state.album)
        assertNull(state.albumArtUri)
        assertFalse(state.isPlaying)
        assertEquals(0L, state.position)
        assertEquals(0L, state.duration)
    }
}
