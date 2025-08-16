package com.on.turip.ui.common.model.trip

import com.on.turip.domain.trip.ContentPlace

data class TripModel(
    val tripDurationModel: TripDurationModel,
    val tripPlaceCount: Int,
    val tripCours: List<ContentPlace> = emptyList<ContentPlace>(),
)
