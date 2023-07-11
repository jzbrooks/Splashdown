package com.jzbrooks.splashdown.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jzbrooks.splashdown.data.ImageDataSource
import com.jzbrooks.splashdown.data.Photo
import com.jzbrooks.splashdown.data.PhotoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageSearchViewModel @Inject constructor(
    private val dataSource: ImageDataSource,
) : ViewModel() {

    val state: StateFlow<ViewState>
        get() = _state

    private val _state = MutableStateFlow(ViewState("", emptyList(), false, 1))

    private var job: Job? = null

    init {
        reload()
    }

    fun loadMore() {
        val state = _state.value
        loadPhotos(state.photos, state.nextPage)
    }

    fun updateQuery(query: String) {
        _state.update { it.copy(query = query) }
    }

    fun acknowledgeError() {
        _state.update { it.copy(error = null) }
    }

    fun reload() {
        _state.update { it.copy(isLoading = true) }
        loadPhotos(emptyList(), 1)
    }

    private fun loadPhotos(existing: List<Photo>, page: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            val state = _state.value
            val images = if (!state.query.isNullOrBlank()) {
                dataSource.searchPhotos(state.query, page)
            } else {
                dataSource.getRecentPhotos(page)
            }

            when (images) {
                is PhotoResult.Success -> {
                    val newState = state.copy(
                        photos = existing + images.photos,
                        isLoading = false,
                        nextPage = page + 1,
                    )

                    _state.emit(newState)
                }

                PhotoResult.Error.Network -> _state.update {
                    it.copy(isLoading = false, error = Error.NETWORK)
                }

                PhotoResult.Error.Deserialization,
                PhotoResult.Error.Server -> _state.update {
                    it.copy(isLoading = false, error = Error.SERVER)
                }

                PhotoResult.Error.Unknown -> _state.update {
                    it.copy(isLoading = false, error = Error.UNKNOWN)
                }
            }
        }
    }

    data class ViewState(
        val query: String,
        val photos: List<Photo>,
        val isLoading: Boolean,
        val nextPage: Int,
        val error: Error? = null
    )

    enum class Error {
        NETWORK,
        SERVER,
        UNKNOWN,
    }
}
