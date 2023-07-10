package com.jzbrooks.splashdown.data.network

import com.jzbrooks.splashdown.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickerService {
    @GET("?method=flickr.photos.getRecent&api_key=${BuildConfig.flickrApiKey}&format=json&nojsoncallback=1")
    suspend fun getRecentPhotos(@Query("page") page: Long): PhotosResponseDto

    @GET("?method=flickr.photos.search&api_key=${BuildConfig.flickrApiKey}&format=json&nojsoncallback=1")
    suspend fun searchPhotos(@Query("text") query: String, @Query("page") page: Long): PhotosResponseDto
}
