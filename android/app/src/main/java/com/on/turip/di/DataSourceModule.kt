package com.on.turip.di

import com.on.turip.data.content.datasource.ContentRemoteDataSource
import com.on.turip.data.content.datasource.DefaultContentRemoteDataSource
import com.on.turip.data.creator.datasource.CreatorRemoteDataSource
import com.on.turip.data.creator.datasource.DefaultCreatorRemoteDataSource
import com.on.turip.data.favorite.datasource.DefaultFavoriteRemoteDataSource
import com.on.turip.data.favorite.datasource.FavoriteRemoteDataSource
import com.on.turip.data.region.datasource.DefaultRegionRemoteDataSource
import com.on.turip.data.region.datasource.RegionRemoteDataSource
import com.on.turip.data.trip.datasource.DefaultTripRemoteDataSource
import com.on.turip.data.trip.datasource.TripRemoteDataSource
import com.on.turip.data.userstorage.datasource.DefaultUserStorageLocalDataSource
import com.on.turip.data.userstorage.datasource.UserStorageLocalDataSource

object DataSourceModule {
    val contentRemoteDataSource: ContentRemoteDataSource by lazy {
        DefaultContentRemoteDataSource(NetworkModule.contentService)
    }
    val creatorRemoteDataSource: CreatorRemoteDataSource by lazy {
        DefaultCreatorRemoteDataSource(NetworkModule.creatorService)
    }
    val tripRemoteDataSource: TripRemoteDataSource by lazy {
        DefaultTripRemoteDataSource(NetworkModule.tripService)
    }
    val userStorageLocalDataSource: UserStorageLocalDataSource by lazy {
        DefaultUserStorageLocalDataSource(LocalStorageModule.userStorage)
    }
    val favoriteRemoteDataSource: FavoriteRemoteDataSource by lazy {
        DefaultFavoriteRemoteDataSource(NetworkModule.favoriteService)
    }
    val regionRemoteDataSource: RegionRemoteDataSource by lazy {
        DefaultRegionRemoteDataSource(NetworkModule.regionService)
    }
}
