package com.on.turip.domain.trip

data class ContentPlace(
    val tripCourseId: Long,
    val visitDay: Int,
    val visitOrder: Int,
    val place: Place,
    val timeLine: String,
)
