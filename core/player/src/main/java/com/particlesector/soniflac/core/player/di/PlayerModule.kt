package com.particlesector.soniflac.core.player.di

import com.particlesector.soniflac.core.player.ExoPlayerManager
import com.particlesector.soniflac.core.player.ExoStreamMetrics
import com.particlesector.soniflac.core.player.PlayerManager
import com.particlesector.soniflac.core.player.StreamMetrics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    abstract fun bindPlayerManager(impl: ExoPlayerManager): PlayerManager

    @Binds
    @Singleton
    abstract fun bindStreamMetrics(impl: ExoStreamMetrics): StreamMetrics
}
