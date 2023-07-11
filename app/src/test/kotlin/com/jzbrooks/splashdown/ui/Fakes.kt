package com.jzbrooks.splashdown.ui

import com.jzbrooks.splashdown.data.ImageDataSource
import com.jzbrooks.splashdown.data.Photo
import com.jzbrooks.splashdown.data.PhotoResult

class SuccessfulResultDataSource(
    private val recents: List<Photo>,
    private val searchResults: List<Photo>,
) : ImageDataSource {
    override suspend fun getRecentPhotos(page: Int): PhotoResult {
        return PhotoResult.Success(recents)
    }

    override suspend fun searchPhotos(query: String, page: Int): PhotoResult {
        return PhotoResult.Success(searchResults)
    }
}

class FailureResultDataSource(
    private val error: PhotoResult.Error = PhotoResult.Error.Unknown
) : ImageDataSource {
    override suspend fun getRecentPhotos(page: Int): PhotoResult {
        return error
    }

    override suspend fun searchPhotos(query: String, page: Int): PhotoResult {
        return error
    }
}

class SwitcharooResultDataSource(
    private val first: List<Photo>,
    private val subsequent: List<Photo>,
) : ImageDataSource {
    private var getRecentPhotoCallCount = 0
    private var getSearchPhotoCallCount = 0

    override suspend fun getRecentPhotos(page: Int): PhotoResult {
        return PhotoResult.Success(if (getRecentPhotoCallCount == 0) first else subsequent).also {
            getRecentPhotoCallCount += 1
        }
    }

    override suspend fun searchPhotos(query: String, page: Int): PhotoResult {
        return PhotoResult.Success(if (getSearchPhotoCallCount == 0) first else subsequent).also {
            getSearchPhotoCallCount += 1
        }
    }
}

