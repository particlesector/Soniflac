package com.particlesector.soniflac.billing

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FossBillingManagerTest {

    @Test
    fun `FOSS billing manager is always premium`() = runTest {
        val manager = FossBillingManager()

        assertTrue(manager.isPremium.value)
    }

    @Test
    fun `queryPremiumStatus does not change premium state`() = runTest {
        val manager = FossBillingManager()

        manager.queryPremiumStatus()

        assertTrue(manager.isPremium.value)
    }
}
