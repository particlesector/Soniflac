package com.particlesector.soniflac.core.database.di

import android.content.Context
import androidx.room.Room
import com.particlesector.soniflac.core.database.SoniFlacDatabase
import com.particlesector.soniflac.core.database.dao.DataUsageDao
import com.particlesector.soniflac.core.database.dao.FavoriteStationDao
import com.particlesector.soniflac.core.database.dao.PlaybackHistoryDao
import com.particlesector.soniflac.core.database.dao.RecentStationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SoniFlacDatabase =
        Room.databaseBuilder(
            context,
            SoniFlacDatabase::class.java,
            "soniflac.db",
        ).build()

    @Provides
    fun provideFavoriteStationDao(db: SoniFlacDatabase): FavoriteStationDao =
        db.favoriteStationDao()

    @Provides
    fun provideRecentStationDao(db: SoniFlacDatabase): RecentStationDao =
        db.recentStationDao()

    @Provides
    fun providePlaybackHistoryDao(db: SoniFlacDatabase): PlaybackHistoryDao =
        db.playbackHistoryDao()

    @Provides
    fun provideDataUsageDao(db: SoniFlacDatabase): DataUsageDao =
        db.dataUsageDao()
}
