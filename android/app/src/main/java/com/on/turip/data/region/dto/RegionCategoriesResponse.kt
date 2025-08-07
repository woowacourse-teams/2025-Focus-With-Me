package com.on.turip.data.region.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionCategoriesResponse(
    @SerialName("regionCategories")
    val regionCategories: List<RegionCategoryResponse>,
)
