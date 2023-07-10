package com.jzbrooks.splashdown.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(): ImageDataSource {
    override suspend fun getRecentPhotos(page: Long): List<Photo> {
        TODO("Not yet implemented")
    }

    override suspend fun searchPhotos(query: String, page: Long): List<Photo> {
        TODO("Not yet implemented")
    }
}
