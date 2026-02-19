package com.particlesector.soniflac.core.database.repository

import com.particlesector.soniflac.core.common.Constants
import com.particlesector.soniflac.core.database.dao.FavoriteStationDao
import com.particlesector.soniflac.core.database.dao.RecentStationDao
import com.particlesector.soniflac.core.database.mapper.toFavoriteEntity
import com.particlesector.soniflac.core.database.mapper.toRecentEntity
import com.particlesector.soniflac.core.database.mapper.toStation
import com.particlesector.soniflac.core.model.Station
import com.particlesector.soniflac.core.network.RadioBrowserApi
import com.particlesector.soniflac.core.network.mapper.toStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(
    private val api: RadioBrowserApi,
    private val favoriteStationDao: FavoriteStationDao,
    private val recentStationDao: RecentStationDao,
) {
    fun observeFavorites(): Flow<List<Station>> =
        favoriteStationDao.observeAll().map { entities ->
            entities.map { it.toStation() }
        }

    fun observeRecents(): Flow<List<Station>> =
        recentStationDao.observeRecent().map { entities ->
            entities.map { it.toStation() }
        }

    suspend fun getFavoriteCount(): Int = favoriteStationDao.getCount()

    suspend fun isFavorite(stationUuid: String): Boolean =
        favoriteStationDao.isFavorite(stationUuid)

    suspend fun toggleFavorite(station: Station): Boolean {
        return if (favoriteStationDao.isFavorite(station.stationUuid)) {
            favoriteStationDao.deleteByUuid(station.stationUuid)
            false
        } else {
            favoriteStationDao.insert(station.toFavoriteEntity())
            true
        }
    }

    suspend fun canAddFavorite(isPremium: Boolean): Boolean {
        if (isPremium) return true
        return favoriteStationDao.getCount() < Constants.FREE_FAVORITE_LIMIT
    }

    suspend fun addRecent(station: Station) {
        recentStationDao.upsert(station.toRecentEntity())
    }

    suspend fun searchStations(query: String): List<Station> =
        api.searchStations(name = query).map { it.toStation() }

    suspend fun getTopStations(limit: Int = 50): List<Station> =
        api.getTopStations(limit = limit).map { it.toStation() }

    suspend fun getStationsByTag(tag: String): List<Station> =
        api.getStationsByTag(tag = tag).map { it.toStation() }

    suspend fun getStationsByCountry(country: String): List<Station> =
        api.getStationsByCountry(country = country).map { it.toStation() }

    suspend fun reportClick(stationUuid: String) {
        try { api.clickStation(stationUuid) } catch (_: Exception) { }
    }
}
