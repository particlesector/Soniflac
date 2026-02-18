package com.particlesector.soniflac.billing.di

import com.particlesector.soniflac.billing.BillingManager
import com.particlesector.soniflac.billing.GplayBillingManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {
    @Binds
    abstract fun bindBillingManager(impl: GplayBillingManager): BillingManager
}
