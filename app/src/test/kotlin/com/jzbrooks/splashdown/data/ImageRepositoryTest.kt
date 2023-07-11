package com.jzbrooks.splashdown.data

import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.prop
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ImageRepositoryTest {

    @Test
    fun `successful recent photos`() = runTest {
        val repository = ImageRepository(SuccessfulImageDataSource)

        val result = repository.getRecentPhotos(1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Success>()
            .prop("photos") { it.photos }
            .isNotEmpty()
    }

    @Test
    fun `successful photo search`() = runTest {
        val repository = ImageRepository(SuccessfulImageDataSource)

        val result = repository.searchPhotos("decking", 1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Success>()
            .prop("photos") { it.photos }
            .isNotEmpty()
    }

    @Test
    fun `recent photos failure - network`() = runTest {
        val repository = ImageRepository(FailureFlickrService(::IOException))

        val result = repository.getRecentPhotos(1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Error.Network>()
    }

    @Test
    fun `recent photos failure - deserialization`() = runTest {
        val repository = ImageRepository(FailureFlickrService(::JsonDataException))

        val result = repository.getRecentPhotos(1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Error.Deserialization>()
    }

    @Test
    fun `recent photos failure - server`() = runTest {
        val repository = ImageRepository(FailureFlickrService {
            HttpException(
                Response.error<Unit>(
                    503,
                    "Send help?".toResponseBody(),
                ),
            )
        })

        val result = repository.getRecentPhotos(1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Error.Server>()
    }

    @Test
    fun `recent photos failure - unknown`() = runTest {
        val repository = ImageRepository(FailureFlickrService(::Exception))

        val result = repository.getRecentPhotos(1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Error.Unknown>()
    }

    @Test
    fun `search photos failure - network`() = runTest {
        val repository = ImageRepository(FailureFlickrService(::IOException))

        val result = repository.searchPhotos("decking", 1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Error.Network>()
    }

    @Test
    fun `search photos failure - deserialization`() = runTest {
        val repository = ImageRepository(FailureFlickrService(::JsonDataException))

        val result = repository.searchPhotos("decking", 1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Error.Deserialization>()
    }

    @Test
    fun `search photos failure - server`() = runTest {
        val repository = ImageRepository(FailureFlickrService {
            HttpException(
                Response.error<Unit>(
                    503,
                    "Send help?".toResponseBody(),
                ),
            )
        })

        val result = repository.searchPhotos("decking", 1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Error.Server>()
    }

    @Test
    fun `search photos failure - unknown`() = runTest {
        val repository = ImageRepository(FailureFlickrService(::Exception))

        val result = repository.searchPhotos("decking", 1)

        assertThat(result, "result")
            .isInstanceOf<PhotoResult.Error.Unknown>()
    }
}
