package com.on.turip.data.region

import com.on.turip.data.region.dto.CountryResponse
import com.on.turip.data.region.dto.RegionCategoriesResponse
import com.on.turip.data.region.dto.RegionCategoryResponse
import com.on.turip.domain.region.Country
import com.on.turip.domain.region.RegionCategory

fun RegionCategoriesResponse.toDomain(): List<RegionCategory> = regionCategories.map { it.toDomain() }

fun RegionCategoryResponse.toDomain(): RegionCategory =
    RegionCategory(
        name = regionCategoryName,
        country = country?.toDomain(),
        imageUrl = regionCategoryImageUrl,
    )

fun CountryResponse.toDomain(): Country =
    Country(
        id = id,
        name = countryName,
        imageUrl = countryImageUrl,
    )
