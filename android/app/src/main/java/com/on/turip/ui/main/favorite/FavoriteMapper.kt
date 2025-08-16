package com.on.turip.ui.main.favorite

import androidx.core.net.toUri
import com.on.turip.domain.trip.Place
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel

fun Place.toUiModel(): FavoritePlaceModel =
    FavoritePlaceModel(
        placeId = placeId,
        name = name,
        uri = url.toUri(),
        category = category.joinToString(),
        isFavorite = true,
    )
