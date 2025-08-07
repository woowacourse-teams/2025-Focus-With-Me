package com.on.turip.data.content.service

import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.UsersLikeContentsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ContentService {
    @GET("contents/count")
    suspend fun getContentsCountByRegion(
        @Query("region") region: String,
    ): ContentInformationCountResponse

    @GET("contents/count")
    suspend fun getContentsCountByKeyword(
        @Query("keyword") keyword: String,
    ): ContentInformationCountResponse

    @GET("contents")
    suspend fun getContentsByRegion(
        @Query("region") region: String,
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): ContentsInformationResponse

    @GET("contents")
    suspend fun getContentsByKeyword(
        @Query("keyword") keyword: String,
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): ContentsInformationResponse

    @GET("contents/{contentId}")
    suspend fun getContentDetail(
        @Path("contentId") contentId: Long,
        @Header("device-fid") fid: String,
    ): ContentDetailResponse

    @GET("/contents/popular/favorites")
    suspend fun getUsersLikeContents(
        @Query("size") size: Int,
    ): UsersLikeContentsResponse
}
