package com.on.turip.data.content.dto

import com.on.turip.data.creator.dto.CreatorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentDetailResponse(
    @SerialName("city")
    val city: CityResponse,
    @SerialName("creator")
    val creator: CreatorResponse,
    @SerialName("id")
    val id: Long,
    @SerialName("isFavorite")
    val isFavorite: Boolean,
    @SerialName("title")
    val title: String,
    @SerialName("uploadedDate")
    val uploadedDate: String,
    @SerialName("url")
    val url: String,
)
