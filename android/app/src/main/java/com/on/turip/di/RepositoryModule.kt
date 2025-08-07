package com.on.turip.di

import com.on.turip.data.content.repository.DefaultContentRepository
import com.on.turip.data.creator.repository.DefaultCreatorRepository
import com.on.turip.data.favorite.repository.DefaultFavoriteRepository
import com.on.turip.data.region.repository.DefaultRegionRepository
import com.on.turip.data.trip.repository.DefaultTripRepository
import com.on.turip.data.userstorage.repository.DefaultUserStorageRepository
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.creator.repository.CreatorRepository
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.region.repository.RegionRepository
import com.on.turip.domain.trip.repository.TripRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository

object RepositoryModule {
    val contentRepository: ContentRepository by lazy {
        DefaultContentRepository(DataSourceModule.contentRemoteDataSource, userStorageRepository)
    }
    val creatorRepository: CreatorRepository by lazy {
        DefaultCreatorRepository(DataSourceModule.creatorRemoteDataSource)
    }
    val tripRepository: TripRepository by lazy {
        DefaultTripRepository(DataSourceModule.tripRemoteDataSource)
    }
    val regionRepository: RegionRepository by lazy {
        DefaultRegionRepository(DataSourceModule.regionRemoteDataSource)
    }
    val userStorageRepository: UserStorageRepository by lazy {
        DefaultUserStorageRepository(DataSourceModule.userStorageLocalDataSource)
    }
    val favoriteRepository: FavoriteRepository by lazy {
        DefaultFavoriteRepository(DataSourceModule.favoriteRemoteDataSource, userStorageRepository)
    }
}
