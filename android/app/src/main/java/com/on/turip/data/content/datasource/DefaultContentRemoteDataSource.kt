package com.on.turip.data.content.datasource

import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.UsersLikeContentsResponse
import com.on.turip.data.content.service.ContentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultContentRemoteDataSource(
    private val contentService: ContentService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ContentRemoteDataSource {
    override suspend fun getContentsSizeByRegion(region: String): Result<ContentInformationCountResponse> =
        withContext(coroutineContext) {
            runCatching { contentService.getContentsCountByRegion(region) }
        }

    override suspend fun getContentsSizeByKeyword(keyword: String): Result<ContentInformationCountResponse> =
        withContext(coroutineContext) {
            runCatching { contentService.getContentsCountByKeyword(keyword) }
        }

    override suspend fun getContentsByRegion(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<ContentsInformationResponse> =
        withContext(coroutineContext) {
            runCatching {
                contentService.getContentsByRegion(
                    region = region,
                    size = size,
                    lastId = lastId,
                )
            }
        }

    override suspend fun getContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): Result<ContentsInformationResponse> =
        withContext(coroutineContext) {
            runCatching {
                contentService.getContentsByKeyword(
                    keyword = keyword,
                    size = size,
                    lastId = lastId,
                )
            }
        }

    override suspend fun getContentDetail(
        contentId: Long,
        fid: String,
    ): Result<ContentDetailResponse> =
        withContext(coroutineContext) {
            runCatching { contentService.getContentDetail(contentId, fid) }
        }

    override suspend fun getUsersLikeContents(size: Int): Result<UsersLikeContentsResponse> =
        withContext(coroutineContext) {
            runCatching { contentService.getUsersLikeContents(size) }
        }
}
