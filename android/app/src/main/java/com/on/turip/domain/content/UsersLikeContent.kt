package com.on.turip.domain.content

import com.on.turip.domain.trip.TripDuration

data class UsersLikeContent(
    val content: Content,
    val tripDuration: TripDuration,
)
