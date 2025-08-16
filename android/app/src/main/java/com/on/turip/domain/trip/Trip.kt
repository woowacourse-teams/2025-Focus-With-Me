package com.on.turip.domain.trip

data class Trip(
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
    val contentPlaces: List<ContentPlace> = emptyList<ContentPlace>(),
)
