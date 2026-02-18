package com.particlesector.soniflac.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackPath"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["playlistId"])]
)
data class PlaylistTrackEntity(
    val playlistId: Long,
    val trackPath: String,
    val position: Int,
    val addedAt: Long = System.currentTimeMillis(),
)
