package com.on.turip.ui.common.mapper

import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.TripDuration
import com.on.turip.ui.common.model.trip.TripDurationModel
import com.on.turip.ui.common.model.trip.TripModel
import com.on.turip.ui.main.home.UsersLikeContentModel

fun Trip.toUiModel(): TripModel =
    TripModel(
        tripDurationModel = tripDuration.toUiModel(),
        tripPlaceCount = tripPlaceCount,
        tripCours = contentPlaces,
    )

fun TripDuration.toUiModel(): TripDurationModel =
    TripDurationModel(
        nights = nights,
        days = days,
    )

fun UsersLikeContent.toUiModel(): UsersLikeContentModel =
    UsersLikeContentModel(
        content = content,
        tripDuration =
            TripDurationModel(
                nights = tripDuration.nights,
                days = tripDuration.days,
            ),
    )
