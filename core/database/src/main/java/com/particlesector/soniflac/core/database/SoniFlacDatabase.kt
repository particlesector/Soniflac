package com.particlesector.soniflac.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.particlesector.soniflac.core.database.converter.Converters
import com.particlesector.soniflac.core.database.dao.DataUsageDao
import com.particlesector.soniflac.core.database.dao.FavoriteStationDao
import com.particlesector.soniflac.core.database.dao.PlaybackHistoryDao
import com.particlesector.soniflac.core.database.dao.PlaylistDao
import com.particlesector.soniflac.core.database.dao.RecentStationDao
import com.particlesector.soniflac.core.database.entity.DataUsageEntity
import com.particlesector.soniflac.core.database.entity.FavoriteStationEntity
import com.particlesector.soniflac.core.database.entity.PlaybackHistoryEntity
import com.particlesector.soniflac.core.database.entity.PlaylistEntity
import com.particlesector.soniflac.core.database.entity.PlaylistTrackEntity
import com.particlesector.soniflac.core.database.entity.RecentStationEntity

@Database(
    entities = [
        FavoriteStationEntity::class,
        RecentStationEntity::class,
        PlaybackHistoryEntity::class,
        DataUsageEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class SoniFlacDatabase : RoomDatabase() {
    abstract fun favoriteStationDao(): FavoriteStationDao
    abstract fun recentStationDao(): RecentStationDao
    abstract fun playbackHistoryDao(): PlaybackHistoryDao
    abstract fun dataUsageDao(): DataUsageDao
    abstract fun playlistDao(): PlaylistDao
}
