package com.jzbrooks.splashdown.data

sealed interface PhotoResult {
    data class Success(val photos: List<Photo>) : PhotoResult

    sealed interface Error : PhotoResult {
        object Server : Error
        object Deserialization : Error
        object Network : Error
        object Unknown : Error
    }
}
