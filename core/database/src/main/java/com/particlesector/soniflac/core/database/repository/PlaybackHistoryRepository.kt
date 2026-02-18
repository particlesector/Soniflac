package com.particlesector.soniflac.core.database.repository

import com.particlesector.soniflac.core.database.dao.PlaybackHistoryDao
import com.particlesector.soniflac.core.database.entity.PlaybackHistoryEntity
import com.particlesector.soniflac.core.model.PlaybackItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackHistoryRepository @Inject constructor(
    private val playbackHistoryDao: PlaybackHistoryDao,
) {
    fun observeRecent(limit: Int = 50): Flow<List<PlaybackHistoryEntity>> =
        playbackHistoryDao.observeRecent(limit)

    suspend fun recordPlayback(item: PlaybackItem, durationMs: Long) {
        val entity = when (item) {
            is PlaybackItem.TrackItem -> PlaybackHistoryEntity(
                itemType = "track",
                itemId = item.track.filePath,
                title = item.track.title,
                artist = item.track.artist,
                durationMs = durationMs,
            )
            is PlaybackItem.StationItem -> PlaybackHistoryEntity(
                itemType = "station",
                itemId = item.station.stationUuid,
                title = item.station.name,
                artist = item.station.country,
                durationMs = durationMs,
            )
        }
        playbackHistoryDao.insert(entity)
    }
}
