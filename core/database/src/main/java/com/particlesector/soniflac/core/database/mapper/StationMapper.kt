package com.particlesector.soniflac.core.database.mapper

import com.particlesector.soniflac.core.database.entity.FavoriteStationEntity
import com.particlesector.soniflac.core.database.entity.RecentStationEntity
import com.particlesector.soniflac.core.model.Station

fun Station.toFavoriteEntity(): FavoriteStationEntity = FavoriteStationEntity(
    stationUuid = stationUuid,
    name = name,
    url = url,
    urlResolved = urlResolved,
    codec = codec,
    bitrate = bitrate,
    country = country,
    language = language,
    tags = tags.joinToString(","),
    favicon = favicon,
    votes = votes,
    clickCount = clickCount,
)

fun FavoriteStationEntity.toStation(): Station = Station(
    stationUuid = stationUuid,
    name = name,
    url = url,
    urlResolved = urlResolved,
    codec = codec,
    bitrate = bitrate,
    country = country,
    language = language,
    tags = if (tags.isBlank()) emptyList() else tags.split(","),
    favicon = favicon,
    votes = votes,
    clickCount = clickCount,
    isFavorite = true,
)

fun Station.toRecentEntity(): RecentStationEntity = RecentStationEntity(
    stationUuid = stationUuid,
    name = name,
    url = url,
    urlResolved = urlResolved,
    codec = codec,
    bitrate = bitrate,
    country = country,
    language = language,
    tags = tags.joinToString(","),
    favicon = favicon,
)

fun RecentStationEntity.toStation(): Station = Station(
    stationUuid = stationUuid,
    name = name,
    url = url,
    urlResolved = urlResolved,
    codec = codec,
    bitrate = bitrate,
    country = country,
    language = language,
    tags = if (tags.isBlank()) emptyList() else tags.split(","),
    favicon = favicon,
)
