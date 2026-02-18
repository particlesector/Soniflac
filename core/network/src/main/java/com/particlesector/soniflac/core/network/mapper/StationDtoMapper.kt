package com.particlesector.soniflac.core.network.mapper

import com.particlesector.soniflac.core.model.Station
import com.particlesector.soniflac.core.network.dto.StationDto

fun StationDto.toStation(): Station = Station(
    stationUuid = stationUuid,
    name = name.trim(),
    url = url,
    urlResolved = urlResolved.ifBlank { url },
    codec = codec,
    bitrate = bitrate,
    country = country,
    language = language,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    favicon = favicon.ifBlank { null },
    votes = votes,
    clickCount = clickCount,
)

fun List<StationDto>.toStations(): List<Station> = map { it.toStation() }
