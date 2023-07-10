package com.jzbrooks.splashdown.data

import com.jzbrooks.splashdown.data.network.PhotosResponseDto
import java.net.URI

fun PhotosResponseDto.toPhotos(): List<Photo> = photos.photo.map {
    Photo(
        it.id,
        it.title,
        URI("https://live.staticflickr.com/${it.server}/${it.id}_${it.secret}.jpg")
    )
}
