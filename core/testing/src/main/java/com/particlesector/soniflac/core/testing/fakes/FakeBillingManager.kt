package com.particlesector.soniflac.core.testing.fakes

import android.app.Activity
import com.particlesector.soniflac.billing.BillingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeBillingManager(initialPremium: Boolean = false) : BillingManager {

    private val _isPremium = MutableStateFlow(initialPremium)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    var queryCount = 0; private set
    var purchaseCount = 0; private set

    override suspend fun queryPremiumStatus() {
        queryCount++
    }

    override suspend fun launchPurchaseFlow(activity: Activity) {
        purchaseCount++
    }

    fun setPremium(premium: Boolean) {
        _isPremium.value = premium
    }
}
