package com.jzbrooks.splashdown.data

interface ImageDataSource {
    suspend fun getRecentPhotos(page: Int): PhotoResult
    suspend fun searchPhotos(query: String, page: Int): PhotoResult
}
