package com.particlesector.soniflac.core.network

import com.particlesector.soniflac.core.network.dto.CountryDto
import com.particlesector.soniflac.core.network.dto.StationDto
import com.particlesector.soniflac.core.network.dto.TagDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RadioBrowserApi {

    @GET("json/stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("tag") tag: String? = null,
        @Query("country") country: String? = null,
        @Query("language") language: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
        @Query("hidebroken") hideBroken: Boolean = true,
    ): List<StationDto>

    @GET("json/stations/topvote/{limit}")
    suspend fun getTopStations(
        @Path("limit") limit: Int = 50,
    ): List<StationDto>

    @GET("json/stations/bytag/{tag}")
    suspend fun getStationsByTag(
        @Path("tag") tag: String,
        @Query("limit") limit: Int = 50,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
    ): List<StationDto>

    @GET("json/stations/bycountry/{country}")
    suspend fun getStationsByCountry(
        @Path("country") country: String,
        @Query("limit") limit: Int = 50,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
    ): List<StationDto>

    @GET("json/tags")
    suspend fun getTags(
        @Query("order") order: String = "stationcount",
        @Query("reverse") reverse: Boolean = true,
        @Query("limit") limit: Int = 100,
    ): List<TagDto>

    @GET("json/countries")
    suspend fun getCountries(
        @Query("order") order: String = "stationcount",
        @Query("reverse") reverse: Boolean = true,
    ): List<CountryDto>

    @POST("json/url/{stationUuid}")
    suspend fun clickStation(
        @Path("stationUuid") stationUuid: String,
    )
}
