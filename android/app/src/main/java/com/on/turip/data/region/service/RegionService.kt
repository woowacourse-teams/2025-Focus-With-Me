package com.on.turip.data.region.service

import com.on.turip.data.region.dto.RegionCategoriesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RegionService {
    @GET("region-categories")
    suspend fun getRegionCategories(
        @Query("isKorea") isDomestic: Boolean,
    ): RegionCategoriesResponse
}
