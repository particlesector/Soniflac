package com.particlesector.soniflac.core.database.dao

import androidx.room.Room
import androidx.room.RoomDatabase
import com.particlesector.soniflac.core.database.SoniFlacDatabase
import com.particlesector.soniflac.core.database.dao.DataUsageDao
import com.particlesector.soniflac.core.database.dao.FavoriteStationDao
import com.particlesector.soniflac.core.database.dao.PlaybackHistoryDao
import com.particlesector.soniflac.core.database.dao.PlaylistDao
import com.particlesector.soniflac.core.database.dao.RecentStationDao

/**
 * In-memory test wrapper for SoniFlacDatabase.
 * Requires Robolectric runner to provide Android context.
 * In unit tests that don't run with Robolectric, this class serves as a
 * structural reference — actual DAO tests require an instrumented or
 * Robolectric environment.
 */
class TestDatabase private constructor(
    private val db: SoniFlacDatabase,
) {
    fun favoriteStationDao(): FavoriteStationDao = db.favoriteStationDao()
    fun recentStationDao(): RecentStationDao = db.recentStationDao()
    fun playbackHistoryDao(): PlaybackHistoryDao = db.playbackHistoryDao()
    fun dataUsageDao(): DataUsageDao = db.dataUsageDao()
    fun playlistDao(): PlaylistDao = db.playlistDao()
    fun close() = db.close()

    companion object {
        fun create(): TestDatabase {
            val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
            val db = Room.inMemoryDatabaseBuilder(context, SoniFlacDatabase::class.java)
                .allowMainThreadQueries()
                .build()
            return TestDatabase(db)
        }
    }
}
