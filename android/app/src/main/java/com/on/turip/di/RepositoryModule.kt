package com.on.turip.di

import com.on.turip.data.content.repository.DefaultContentRepository
import com.on.turip.data.creator.repository.DefaultCreatorRepository
import com.on.turip.data.trip.repository.DefaultTripRepository
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.creator.repository.CreatorRepository
import com.on.turip.domain.trip.repository.TripRepository

object RepositoryModule {
    val contentRepository: ContentRepository by lazy {
        DefaultContentRepository(DataSourceModule.contentRemoteDataSource)
    }
    val creatorRepository: CreatorRepository by lazy {
        DefaultCreatorRepository(DataSourceModule.creatorRemoteDataSource)
    }
    val tripRepository: TripRepository by lazy {
        DefaultTripRepository(DataSourceModule.tripRemoteDataSource)
    }
}
