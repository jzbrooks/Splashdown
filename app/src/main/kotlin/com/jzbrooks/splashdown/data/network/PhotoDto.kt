package com.jzbrooks.splashdown.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoDto(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Long,
    val title: String,
    @Json(name = "ispublic")
    val isPublic: Int,
    @Json(name = "isfriend")
    val isFriend: Int,
    @Json(name = "isfamily")
    val isFamily: Int,
)
