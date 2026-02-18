package com.particlesector.soniflac.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.particlesector.soniflac.core.database.entity.PlaybackHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackHistoryDao {

    @Query("SELECT * FROM playback_history ORDER BY playedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 100): Flow<List<PlaybackHistoryEntity>>

    @Query("SELECT * FROM playback_history ORDER BY playedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 100): List<PlaybackHistoryEntity>

    @Insert
    suspend fun insert(entry: PlaybackHistoryEntity)

    @Query("DELETE FROM playback_history")
    suspend fun deleteAll()
}
