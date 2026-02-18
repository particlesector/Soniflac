package com.particlesector.soniflac.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_history")
data class PlaybackHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemType: String,
    val itemId: String,
    val title: String,
    val artist: String,
    val playedAt: Long = System.currentTimeMillis(),
    val durationMs: Long = 0,
)
