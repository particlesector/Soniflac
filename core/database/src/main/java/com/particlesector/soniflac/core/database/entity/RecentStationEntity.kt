package com.particlesector.soniflac.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_stations")
data class RecentStationEntity(
    @PrimaryKey
    val stationUuid: String,
    val name: String,
    val url: String,
    val urlResolved: String,
    val codec: String,
    val bitrate: Int,
    val country: String,
    val language: String,
    val tags: String,
    val favicon: String?,
    val lastPlayedAt: Long = System.currentTimeMillis(),
)
