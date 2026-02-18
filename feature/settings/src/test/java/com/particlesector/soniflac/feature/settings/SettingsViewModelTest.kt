package com.particlesector.soniflac.feature.settings

import app.cash.turbine.test
import com.particlesector.soniflac.core.database.repository.DataUsageRepository
import com.particlesector.soniflac.core.testing.MainDispatcherRule
import com.particlesector.soniflac.core.testing.fakes.FakeBillingManager
import io.mockk.coVerify
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

@ExtendWith(MainDispatcherRule::class)
class SettingsViewModelTest {

    private val billingManager = FakeBillingManager()
    private val dataUsageRepository = mockk<DataUsageRepository>()
    private val monthlyFlow = MutableStateFlow(0L)

    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setUp() {
        every { dataUsageRepository.observeMonthly() } returns monthlyFlow
        viewModel = SettingsViewModel(billingManager, dataUsageRepository)
    }

    @Test
    fun `initial state is not premium`() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isPremium)
        assertEquals(0L, state.monthlyDataLimitMb)
    }

    @Test
    fun `premium status updates from billing manager`() = runTest {
        billingManager.setPremium(true)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isPremium)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `premium status reverts when billing changes`() = runTest {
        billingManager.setPremium(true)
        billingManager.setPremium(false)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isPremium)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setMonthlyDataLimit updates state`() = runTest {
        viewModel.setMonthlyDataLimit(500)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(500L, state.monthlyDataLimitMb)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `monthly data usage updates from repository`() = runTest {
        monthlyFlow.value = 1_073_741_824L

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1_073_741_824L, state.monthlyDataUsedBytes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `queryPremiumStatus called on init`() = runTest {
        assertEquals(1, billingManager.queryCount)
    }

    @Test
    fun `combined state reflects all sources`() = runTest {
        billingManager.setPremium(true)
        viewModel.setMonthlyDataLimit(1000)
        monthlyFlow.value = 500_000_000L

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isPremium)
            assertEquals(1000L, state.monthlyDataLimitMb)
            assertEquals(500_000_000L, state.monthlyDataUsedBytes)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
