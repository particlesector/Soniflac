package com.particlesector.soniflac.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.particlesector.soniflac.core.database.entity.FavoriteStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStationDao {

    @Query("SELECT * FROM favorite_stations ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteStationEntity>>

    @Query("SELECT * FROM favorite_stations ORDER BY addedAt DESC")
    suspend fun getAll(): List<FavoriteStationEntity>

    @Query("SELECT * FROM favorite_stations WHERE stationUuid = :uuid")
    suspend fun getByUuid(uuid: String): FavoriteStationEntity?

    @Query("SELECT COUNT(*) FROM favorite_stations")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM favorite_stations")
    fun observeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: FavoriteStationEntity)

    @Delete
    suspend fun delete(station: FavoriteStationEntity)

    @Query("DELETE FROM favorite_stations WHERE stationUuid = :uuid")
    suspend fun deleteByUuid(uuid: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stations WHERE stationUuid = :uuid)")
    suspend fun isFavorite(uuid: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stations WHERE stationUuid = :uuid)")
    fun observeIsFavorite(uuid: String): Flow<Boolean>
}
