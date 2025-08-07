package com.on.turip.data.region.repository

import com.on.turip.data.region.datasource.RegionRemoteDataSource
import com.on.turip.data.region.toDomain
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.repository.RegionRepository

class DefaultRegionRepository(
    private val regionRemoteDataSource: RegionRemoteDataSource,
) : RegionRepository {
    private val cachedDomesticRegionCategories: MutableList<RegionCategory> = mutableListOf()
    private val cachedAbroadRegionCategories: MutableList<RegionCategory> = mutableListOf()

    override suspend fun loadRegionCategories(isDomestic: Boolean): Result<List<RegionCategory>> {
        val cachedRegionCategories: MutableList<RegionCategory> =
            if (isDomestic) cachedDomesticRegionCategories else cachedAbroadRegionCategories

        if (cachedRegionCategories.isNotEmpty()) return Result.success(cachedRegionCategories)

        return regionRemoteDataSource
            .getRegionCategories(isDomestic)
            .mapCatching {
                val categories: List<RegionCategory> = it.toDomain()
                cachedRegionCategories.addAll(categories)
                categories
            }
    }
}
