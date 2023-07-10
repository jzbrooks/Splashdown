package com.jzbrooks.splashdown.data

import com.jzbrooks.splashdown.BuildConfig
import com.jzbrooks.splashdown.data.network.FlickerService
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface Module {
    @Binds
    fun bindImageRepository(imageRepository: ImageRepository): ImageDataSource

    companion object {
        @Provides
        fun imageService(httpClient: OkHttpClient, moshi: Moshi): FlickerService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.flickr.com/services/rest/")
                .callFactory(httpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create()
        }

        @Provides
        @Singleton
        fun moshi(): Moshi = Moshi.Builder().build()

        @Provides
        fun httpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(HttpLoggingInterceptor())
            }

            return builder.build()
        }
    }

}
