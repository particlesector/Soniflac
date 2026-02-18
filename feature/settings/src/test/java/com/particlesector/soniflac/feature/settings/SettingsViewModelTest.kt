package com.particlesector.soniflac.feature.settings

import com.particlesector.soniflac.billing.BillingManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val premiumFlow = MutableStateFlow(false)
    private val billingManager = mockk<BillingManager> {
        every { isPremium } returns premiumFlow
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects billing manager premium status`() {
        val viewModel = SettingsViewModel(billingManager)
        assertFalse(viewModel.uiState.value.isPremium)
    }

    @Test
    fun `state updates when premium status changes`() {
        val viewModel = SettingsViewModel(billingManager)

        premiumFlow.value = true
        assertTrue(viewModel.uiState.value.isPremium)

        premiumFlow.value = false
        assertFalse(viewModel.uiState.value.isPremium)
    }

    @Test
    fun `monthly data limit is null by default`() {
        val viewModel = SettingsViewModel(billingManager)
        assertNull(viewModel.uiState.value.monthlyDataLimitBytes)
    }
}
