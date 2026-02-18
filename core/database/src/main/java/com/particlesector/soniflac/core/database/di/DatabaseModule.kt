package com.particlesector.soniflac.core.database.di

import android.content.Context
import androidx.room.Room
import com.particlesector.soniflac.core.database.SoniFlacDatabase
import com.particlesector.soniflac.core.database.dao.DataUsageDao
import com.particlesector.soniflac.core.database.dao.FavoriteStationDao
import com.particlesector.soniflac.core.database.dao.PlaybackHistoryDao
import com.particlesector.soniflac.core.database.dao.PlaylistDao
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
    fun provideFavoriteStationDao(database: SoniFlacDatabase): FavoriteStationDao =
        database.favoriteStationDao()

    @Provides
    fun provideRecentStationDao(database: SoniFlacDatabase): RecentStationDao =
        database.recentStationDao()

    @Provides
    fun providePlaybackHistoryDao(database: SoniFlacDatabase): PlaybackHistoryDao =
        database.playbackHistoryDao()

    @Provides
    fun provideDataUsageDao(database: SoniFlacDatabase): DataUsageDao =
        database.dataUsageDao()

    @Provides
    fun providePlaylistDao(database: SoniFlacDatabase): PlaylistDao =
        database.playlistDao()
}
