package com.on.turip.data.favorite.service

import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.dto.FavoriteContentsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface FavoriteService {
    @POST("favorites")
    suspend fun postFavorite(
        @Header("device-fid") fid: String,
        @Body favoriteAddRequest: FavoriteAddRequest,
    ): Response<Unit>

    @DELETE("favorites")
    suspend fun deleteFavorite(
        @Header("device-fid") fid: String,
        @Query("contentId") contentId: Long,
    ): Response<Unit>

    @GET("favorites")
    suspend fun getFavoriteContents(
        @Header("device-fid") fid: String,
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): Response<FavoriteContentsResponse>
}
