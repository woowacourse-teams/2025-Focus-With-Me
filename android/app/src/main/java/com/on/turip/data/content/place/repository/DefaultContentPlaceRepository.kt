package com.on.turip.data.content.place.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.content.place.datasource.ContentPlaceRemoteDataSource
import com.on.turip.data.content.place.toDomain
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.repository.ContentPlaceRepository

class DefaultContentPlaceRepository(
    private val contentPlaceRemoteDataSource: ContentPlaceRemoteDataSource,
) : ContentPlaceRepository {
    override suspend fun loadTripInfo(contentId: Long): TuripCustomResult<Trip> =
        contentPlaceRemoteDataSource
            .getTrip(contentId)
            .mapCatching { it.toDomain() }
}
