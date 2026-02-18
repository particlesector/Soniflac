package com.particlesector.soniflac.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.particlesector.soniflac.core.database.entity.RecentStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentStationDao {

    @Query("SELECT * FROM recent_stations ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<RecentStationEntity>>

    @Query("SELECT * FROM recent_stations ORDER BY lastPlayedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<RecentStationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(station: RecentStationEntity)

    @Query("DELETE FROM recent_stations WHERE stationUuid = :uuid")
    suspend fun deleteByUuid(uuid: String)

    @Query("DELETE FROM recent_stations")
    suspend fun deleteAll()
}
