package com.on.turip.data.content.repository

import com.on.turip.data.content.datasource.ContentRemoteDataSource
import com.on.turip.data.content.toDomain
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import timber.log.Timber

class DefaultContentRepository(
    private val contentRemoteDataSource: ContentRemoteDataSource,
    private val userStorageRepository: UserStorageRepository,
) : ContentRepository {
    override suspend fun loadContentsSizeByRegion(regionCategoryName: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSizeByRegion(regionCategoryName)
            .mapCatching { it.count }

    override suspend fun loadContentsSizeByKeyword(keyword: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSizeByKeyword(keyword)
            .mapCatching { it.count }

    override suspend fun loadContentsByRegion(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult> =
        contentRemoteDataSource
            .getContentsByRegion2(regionCategoryName, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult> =
        contentRemoteDataSource
            .getContentsByKeyword(keyword, size, lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadContent(contentId: Long): Result<Content> {
        val turipDeviceIdentifier: TuripDeviceIdentifier =
            CoroutineScope(Dispatchers.IO)
                .async {
                    userStorageRepository
                        .loadId()
                        .onFailure {
                            Timber.e("${it.message}")
                        }
                }.await()
                .getOrThrow()

        return contentRemoteDataSource
            .getContentDetail(contentId, turipDeviceIdentifier.fid)
            .mapCatching { it.toDomain() }
    }

    override suspend fun loadPopularFavoriteContents(size: Int): Result<List<UsersLikeContent>> =
        contentRemoteDataSource
            .getUsersLikeContents(size)
            .mapCatching { it.toDomain() }
}
