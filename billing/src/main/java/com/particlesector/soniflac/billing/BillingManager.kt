package com.particlesector.soniflac.billing

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface BillingManager {
    val isPremium: StateFlow<Boolean>
    suspend fun queryPremiumStatus()
    suspend fun launchPurchaseFlow(activity: Activity)
}
