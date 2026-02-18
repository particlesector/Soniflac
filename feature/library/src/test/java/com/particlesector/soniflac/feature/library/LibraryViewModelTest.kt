package com.particlesector.soniflac.feature.library

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LibraryViewModelTest {

    @Test
    fun `initial state is empty and not loading`() {
        val viewModel = LibraryViewModel()
        val state = viewModel.uiState.value

        assertEquals(emptyList<Any>(), state.tracks)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(0, state.trackCount)
    }
}
