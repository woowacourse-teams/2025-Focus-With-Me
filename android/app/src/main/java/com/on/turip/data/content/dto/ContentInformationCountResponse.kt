package com.on.turip.data.content.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentInformationCountResponse(
    @SerialName("count")
    val count: Int,
)
