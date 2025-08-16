package com.on.turip.data.content.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(
    @SerialName("address")
    val address: String,
    @SerialName("categories")
    val categories: List<CategoryResponse>,
    @SerialName("id")
    val id: Long,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String,
)
