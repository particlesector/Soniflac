package com.particlesector.soniflac.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_usage")
data class DataUsageEntity(
    @PrimaryKey
    val date: String,
    val bytesStreamed: Long,
    val streamingDurationMs: Long,
)
