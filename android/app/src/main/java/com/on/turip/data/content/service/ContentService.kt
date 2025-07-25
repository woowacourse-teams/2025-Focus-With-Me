package com.on.turip.data.content.service

import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ContentService {
    @GET("contents/count")
    suspend fun getContentsCount(
        @Query("region") region: String,
    ): ContentInformationCountResponse

    @GET("contents")
    suspend fun getContentsInformation(
        @Query("region") region: String,
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): ContentsInformationResponse

    @GET("contents/{contentId}")
    suspend fun getContentDetail(
        @Path("contentId") contentId: Long,
    ): ContentDetailResponse
}
