package com.on.turip.ui.main.favorite.model

import android.net.Uri

data class FavoritePlaceModel(
    val placeId: Long,
    val name: String,
    val uri: Uri,
    val category: String,
    val isFavorite: Boolean,
) {
    val turipCategory: String
        get() = parseCategory()

    private fun parseCategory(): String {
        val findIndex: Int = category.indexOfLast { it == '>' }
        if (findIndex == -1) return category
        return category.substring(findIndex + 1).trim()
    }
}
