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

    @Query("SELECT * FROM data_usage WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun observeInRange(startDate: String, endDate: String): Flow<List<DataUsageEntity>>

    @Query("SELECT SUM(bytesStreamed) FROM data_usage WHERE date BETWEEN :startDate AND :endDate")
    fun observeTotalBytesInRange(startDate: String, endDate: String): Flow<Long>

    @Query("SELECT SUM(bytesStreamed) FROM data_usage WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalBytesInRange(startDate: String, endDate: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DataUsageEntity)
}
