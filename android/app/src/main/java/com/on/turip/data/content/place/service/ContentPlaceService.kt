package com.on.turip.data.content.place.service

import com.on.turip.data.content.place.dto.TripResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ContentPlaceService {
    @GET("content-places")
    suspend fun getTrip(
        @Query("contentId") contentId: Long,
    ): Response<TripResponse>
}
