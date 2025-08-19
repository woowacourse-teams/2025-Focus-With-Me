package com.on.turip.data.trip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripCourseResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("place")
    val place: PlaceResponse,
    @SerialName("visitDay")
    val visitDay: Int,
    @SerialName("visitOrder")
    val visitOrder: Int,
)
