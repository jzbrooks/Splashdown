package com.jzbrooks.splashdown.data

import com.jzbrooks.splashdown.data.network.FlickerService
import com.squareup.moshi.JsonDataException
import logcat.asLog
import logcat.logcat
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val flickrService: FlickerService,
): ImageDataSource {
    override suspend fun getRecentPhotos(page: Int): PhotoResult {
        val recentPhotos = try {
            flickrService.getRecentPhotos(page)
        } catch (e: HttpException) {
            logcat { e.asLog() }
            return PhotoResult.Error.Server
        } catch (e: JsonDataException) {
            logcat { e.asLog() }
            return PhotoResult.Error.Deserialization
        } catch (e: IOException) {
            logcat { e.asLog() }
            return PhotoResult.Error.Network
        } catch (e: Exception) {
            logcat { e.asLog() }
            return PhotoResult.Error.Unknown
        }

        return PhotoResult.Success(recentPhotos.toPhotos())
    }

    override suspend fun searchPhotos(query: String, page: Int): PhotoResult {
        val recentPhotos = try {
            flickrService.searchPhotos(query, page)
        } catch (e: HttpException) {
            logcat { e.asLog() }
            return PhotoResult.Error.Server
        } catch (e: JsonDataException) {
            logcat { e.asLog() }
            return PhotoResult.Error.Deserialization
        } catch (e: IOException) {
            logcat { e.asLog() }
            return PhotoResult.Error.Network
        } catch (e: Exception) {
            logcat { e.asLog() }
            return PhotoResult.Error.Unknown
        }

        return PhotoResult.Success(recentPhotos.toPhotos())
    }
}
