package com.particlesector.soniflac.billing

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over billing/purchase status. Implementations differ between
 * the gplay (Google Play Billing) and foss (always-unlocked) flavors.
 */
interface BillingManager {

    /** Emits `true` when the user has an active premium entitlement. */
    val isPremium: StateFlow<Boolean>

    /** Re-check premium status against the billing back-end. */
    suspend fun queryPremiumStatus()

    /**
     * Launch the purchase flow for the premium SKU.
     * No-op in the FOSS flavor.
     */
    suspend fun launchPurchaseFlow(activity: Activity)
}
