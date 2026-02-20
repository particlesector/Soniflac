package com.particlesector.soniflac.billing

import android.app.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Play flavor billing implementation using the Play Billing Library.
 * TODO: Wire up BillingClient, purchase verification, and entitlement checks.
 */
@Singleton
class PlayBillingManager @Inject constructor() : BillingManager {

    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    override suspend fun queryPremiumStatus() {
        // TODO: Query BillingClient for existing purchases and update _isPremium.
    }

    override suspend fun launchPurchaseFlow(activity: Activity) {
        // TODO: Launch Play Billing purchase flow for premium SKU.
    }
}
