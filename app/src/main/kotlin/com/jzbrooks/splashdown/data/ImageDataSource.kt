package com.jzbrooks.splashdown.data

interface ImageDataSource {
    suspend fun getRecentPhotos(page: Long): PhotoResult
    suspend fun searchPhotos(query: String, page: Long): PhotoResult
}
