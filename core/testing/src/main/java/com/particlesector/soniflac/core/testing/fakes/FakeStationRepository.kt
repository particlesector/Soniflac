package com.particlesector.soniflac.core.testing.fakes

import com.particlesector.soniflac.core.model.Station
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeStationRepository {

    private val _favorites = MutableStateFlow<List<Station>>(emptyList())
    private val _recents = MutableStateFlow<List<Station>>(emptyList())

    var searchResult: List<Station> = emptyList()
    var topResult: List<Station> = emptyList()
    var shouldThrow: Exception? = null
    var canAddFavoriteResult: Boolean = true
    var clickCount = 0

    fun observeFavorites(): Flow<List<Station>> = _favorites
    fun observeRecents(): Flow<List<Station>> = _recents

    suspend fun getFavoriteCount(): Int = _favorites.value.size

    suspend fun isFavorite(stationUuid: String): Boolean =
        _favorites.value.any { it.stationUuid == stationUuid }

    suspend fun toggleFavorite(station: Station): Boolean {
        val current = _favorites.value.toMutableList()
        return if (current.any { it.stationUuid == station.stationUuid }) {
            current.removeAll { it.stationUuid == station.stationUuid }
            _favorites.value = current
            false
        } else {
            current.add(station.copy(isFavorite = true))
            _favorites.value = current
            true
        }
    }

    suspend fun canAddFavorite(isPremium: Boolean): Boolean = canAddFavoriteResult

    suspend fun addRecent(station: Station) {
        _recents.value = listOf(station) + _recents.value
    }

    suspend fun searchStations(query: String): List<Station> {
        shouldThrow?.let { throw it }
        return searchResult
    }

    suspend fun getTopStations(limit: Int = 50): List<Station> {
        shouldThrow?.let { throw it }
        return topResult
    }

    suspend fun reportClick(stationUuid: String) {
        clickCount++
    }

    fun setFavorites(stations: List<Station>) {
        _favorites.value = stations
    }

    fun setRecents(stations: List<Station>) {
        _recents.value = stations
    }
}
