package com.on.turip.data.content.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersLikeContentsResponse(
    @SerialName("contents")
    val contents: List<UsersLikeContentResponse>,
)
