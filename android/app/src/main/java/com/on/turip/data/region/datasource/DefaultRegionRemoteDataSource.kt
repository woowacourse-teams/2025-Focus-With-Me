package com.on.turip.data.region.datasource

import com.on.turip.data.region.dto.RegionCategoriesResponse
import com.on.turip.data.region.service.RegionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultRegionRemoteDataSource(
    private val regionService: RegionService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : RegionRemoteDataSource {
    override suspend fun getRegionCategories(isDomestic: Boolean): Result<RegionCategoriesResponse> =
        withContext(coroutineContext) {
            runCatching { regionService.getRegionCategories(isDomestic) }
        }
}
