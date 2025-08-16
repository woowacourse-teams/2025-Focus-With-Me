package com.on.turip.data.content.place.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.content.place.dto.TripResponse

interface ContentPlaceRemoteDataSource {
    suspend fun getTrip(contentId: Long): TuripCustomResult<TripResponse>
}
