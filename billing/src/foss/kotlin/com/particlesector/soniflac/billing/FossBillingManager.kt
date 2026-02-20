package com.particlesector.soniflac.billing

import android.app.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FOSS flavor billing implementation — all premium features are always unlocked.
 */
@Singleton
class FossBillingManager @Inject constructor() : BillingManager {

    private val _isPremium = MutableStateFlow(true)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    override suspend fun queryPremiumStatus() {
        // Always premium in FOSS builds — nothing to query.
    }

    override suspend fun launchPurchaseFlow(activity: Activity) {
        // No-op in FOSS builds.
    }
}
