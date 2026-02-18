package com.particlesector.soniflac.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.particlesector.soniflac.core.database.entity.PlaybackHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackHistoryDao {

    @Query("SELECT * FROM playback_history ORDER BY playedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<PlaybackHistoryEntity>

    @Query("SELECT * FROM playback_history ORDER BY playedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<PlaybackHistoryEntity>>

    @Query("SELECT * FROM playback_history WHERE itemType = :type ORDER BY playedAt DESC LIMIT :limit")
    suspend fun getRecentByType(type: String, limit: Int = 50): List<PlaybackHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlaybackHistoryEntity)
}
