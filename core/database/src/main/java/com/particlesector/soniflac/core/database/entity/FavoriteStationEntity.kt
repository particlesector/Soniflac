package com.particlesector.soniflac.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stations")
data class FavoriteStationEntity(
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
    val votes: Int,
    val clickCount: Int,
    val addedAt: Long = System.currentTimeMillis(),
)
