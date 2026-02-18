package com.particlesector.soniflac.feature.radio

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RadioViewModelTest {

    @Test
    fun `initial state is empty`() {
        val viewModel = RadioViewModel()
        val state = viewModel.uiState.value

        assertEquals("", state.query)
        assertEquals(emptyList<Any>(), state.stations)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `search event updates query`() {
        val viewModel = RadioViewModel()

        viewModel.onEvent(RadioEvent.Search("jazz"))

        assertEquals("jazz", viewModel.uiState.value.query)
    }
}
