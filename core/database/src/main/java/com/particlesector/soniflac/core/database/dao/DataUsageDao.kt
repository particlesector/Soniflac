package com.particlesector.soniflac.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.particlesector.soniflac.core.database.entity.DataUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DataUsageDao {

    @Query("SELECT * FROM data_usage WHERE date = :date")
    suspend fun getByDate(date: String): DataUsageEntity?

    @Query("SELECT * FROM data_usage WHERE date = :date")
    fun observeByDate(date: String): Flow<DataUsageEntity?>

    @Query("SELECT COALESCE(SUM(bytesStreamed), 0) FROM data_usage WHERE date = :date")
    suspend fun getTodayUsage(date: String): Long

    @Query("SELECT COALESCE(SUM(bytesStreamed), 0) FROM data_usage WHERE date >= :startDate AND date <= :endDate")
    suspend fun getUsageForRange(startDate: String, endDate: String): Long

    @Query("SELECT COALESCE(SUM(bytesStreamed), 0) FROM data_usage WHERE date >= :startDate AND date <= :endDate")
    fun observeUsageForRange(startDate: String, endDate: String): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DataUsageEntity)

    @Query("DELETE FROM data_usage WHERE date < :beforeDate")
    suspend fun deleteOlderThan(beforeDate: String)
}
