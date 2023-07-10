package com.jzbrooks.splashdown.data

interface ImageDataSource {
    suspend fun getRecentPhotos(page: Long): List<Photo>
    suspend fun searchPhotos(query: String, page: Long): List<Photo>
}
