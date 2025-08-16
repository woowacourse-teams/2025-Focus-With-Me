package com.on.turip.data.content.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    @SerialName("name")
    val name: String,
)
