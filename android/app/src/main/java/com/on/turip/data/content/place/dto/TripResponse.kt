package com.on.turip.data.content.place.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripResponse(
    @SerialName("contentPlaces")
    val tripCourses: List<TripCourseResponse>,
    @SerialName("tripDuration")
    val tripDuration: TripDurationResponse,
    @SerialName("contentPlaceCount")
    val contentPlaceCount: Int,
)
