package com.jzbrooks.splashdown.data

import com.jzbrooks.splashdown.data.network.FlickerService
import com.jzbrooks.splashdown.data.network.PhotoDto
import com.jzbrooks.splashdown.data.network.PhotoPageDto
import com.jzbrooks.splashdown.data.network.PhotosResponseDto
import java.lang.Exception

object SuccessfulImageDataSource : FlickerService {
    override suspend fun getRecentPhotos(page: Int): PhotosResponseDto {
        return PhotosResponseDto(
            PhotoPageDto(
                1,
                1,
                1,
                1,
                listOf(
                    PhotoDto(
                        "C8F8E359-B665-4579-B412-F442E50DE7CC",
                        "Justin",
                        "ast3n2nn12310n",
                        "e1",
                        21,
                        "The framing is done for the new build",
                        1,
                        0,
                        0,
                    )
                )
            )
        )
    }

    override suspend fun searchPhotos(query: String, page: Int): PhotosResponseDto {
        return PhotosResponseDto(
            PhotoPageDto(
                1,
                1,
                1,
                1,
                listOf(
                    PhotoDto(
                        "2CB5F7B6-B185-48C3-B564-0CCF06BD3E36",
                        "Justin",
                        "kgbn3p002f",
                        "e1",
                        21,
                        "This decking material is really rough.",
                        1,
                        0,
                        0,
                    )
                )
            )
        )
    }
}

class FailureFlickrService(
    private val exceptionGenerator: () -> Exception
) : FlickerService {
    override suspend fun getRecentPhotos(page: Int): PhotosResponseDto {
        throw exceptionGenerator()
    }

    override suspend fun searchPhotos(query: String, page: Int): PhotosResponseDto {
        throw exceptionGenerator()
    }
}
