package com.particlesector.soniflac.core.database.repository

import com.particlesector.soniflac.core.database.dao.PlaybackHistoryDao
import com.particlesector.soniflac.core.database.entity.PlaybackHistoryEntity
import com.particlesector.soniflac.core.model.PlaybackHistory
import com.particlesector.soniflac.core.model.PlaybackItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackHistoryRepository @Inject constructor(
    private val playbackHistoryDao: PlaybackHistoryDao,
) {
    fun observeRecent(limit: Int = 50): Flow<List<PlaybackHistory>> =
        playbackHistoryDao.observeRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun recordPlayback(item: PlaybackItem, durationMs: Long) {
        val entity = when (item) {
            is PlaybackItem.TrackItem -> PlaybackHistoryEntity(
                itemType = "track",
                itemId = item.track.path,
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

    private fun PlaybackHistoryEntity.toDomain(): PlaybackHistory = PlaybackHistory(
        id = id,
        itemType = itemType,
        itemId = itemId,
        title = title,
        artist = artist,
        playedAt = playedAt,
        durationMs = durationMs,
    )
}
