package com.on.turip.data.content.datasource

import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.UsersLikeContentsResponse

interface ContentRemoteDataSource {
    suspend fun getContentsSizeByRegion(region: String): Result<ContentInformationCountResponse>

    suspend fun getContentsSizeByKeyword(keyword: String): Result<ContentInformationCountResponse>

    suspend fun getContentsByRegion(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<ContentsInformationResponse>

    suspend fun getContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): Result<ContentsInformationResponse>

    suspend fun getContentDetail(
        contentId: Long,
        fid: String,
    ): Result<ContentDetailResponse>

    suspend fun getUsersLikeContents(size: Int): Result<UsersLikeContentsResponse>
}
