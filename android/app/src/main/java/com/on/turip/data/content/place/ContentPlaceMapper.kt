package com.on.turip.data.content.place

import com.on.turip.data.content.place.dto.CategoryResponse
import com.on.turip.data.content.place.dto.PlaceResponse
import com.on.turip.data.content.place.dto.TripCourseResponse
import com.on.turip.data.content.place.dto.TripDurationResponse
import com.on.turip.data.content.place.dto.TripResponse
import com.on.turip.domain.trip.ContentPlace
import com.on.turip.domain.trip.Place
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.TripDuration

fun TripResponse.toDomain(): Trip =
    Trip(
        tripDuration = tripDuration.toDomain(),
        tripPlaceCount = contentPlaceCount,
        contentPlaces = tripCourses.map(TripCourseResponse::toDomain),
    )

fun TripDurationResponse.toDomain(): TripDuration =
    TripDuration(
        nights = nights,
        days = days,
    )

fun TripCourseResponse.toDomain(): ContentPlace =
    ContentPlace(
        tripCourseId = id,
        visitDay = visitDay,
        visitOrder = visitOrder,
        place = place.toDomain(),
        timeLine = timeLine,
    )

fun PlaceResponse.toDomain(): Place =
    Place(
        placeId = id,
        name = name,
        url = url,
        address = address,
        latitude = latitude,
        longitude = longitude,
        category = categories.map(CategoryResponse::name),
    )
