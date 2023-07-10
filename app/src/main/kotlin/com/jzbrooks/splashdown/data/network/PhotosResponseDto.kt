package com.jzbrooks.splashdown.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotosResponseDto(
    val photos: PhotoPageDto,
)
