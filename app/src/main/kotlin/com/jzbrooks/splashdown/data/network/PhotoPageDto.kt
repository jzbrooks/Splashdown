package com.jzbrooks.splashdown.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoPageDto(
    val page: Long,
    val pages: Long,
    @Json(name = "perpage")
    val perPage: Long,
    val total: Long,
    val photo: List<PhotoDto>,
)
