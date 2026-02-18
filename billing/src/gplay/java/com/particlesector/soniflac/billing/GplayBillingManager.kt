package com.particlesector.soniflac.billing

import android.app.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GplayBillingManager @Inject constructor() : BillingManager {

    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    override suspend fun queryPremiumStatus() {
        // TODO: Implement Google Play Billing Library query
    }

    override suspend fun launchPurchaseFlow(activity: Activity) {
        // TODO: Implement Google Play purchase flow
    }
}
