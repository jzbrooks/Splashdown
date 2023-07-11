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
import logcat.LogPriority
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class ImageSearchViewModel @Inject constructor(
    private val dataSource: ImageDataSource,
) : ViewModel() {

    val state: StateFlow<ViewState>
        get() = _state

    private val _state = MutableStateFlow(
        ViewState(
            "",
            "",
            emptyList(),
            false,
            1,
        )
    )

    private var job: Job? = null

    init {
        reload()
    }

    fun loadMore() {
        val state = _state.value
        loadPhotos(state.photos, state.nextPage)
    }

    fun updateQuery(query: String) {
        _state.update { it.copy(pendingQuery = query) }
    }

    fun acknowledgeError() {
        _state.update { it.copy(error = null) }
    }

    fun reload() {
        _state.update { it.copy(isLoading = true) }
        loadPhotos(emptyList(), 1)
    }

    fun search() {
        _state.update { it.copy(isLoading = true, committedQuery = it.pendingQuery) }
        loadPhotos(emptyList(), 1)
    }

    private fun loadPhotos(existing: List<Photo>, page: Int) {
        if (job != null) {
            logcat(LogPriority.WARN) { "loadPhotos was called again before the next page loaded." }
            return
        }


        job = viewModelScope.launch {
            val state = _state.value
            val query = state.committedQuery

            val images = if (query.isNotBlank()) {
                logcat { "Loading page $page with query $query" }
                dataSource.searchPhotos(query, page)
            } else {
                logcat { "Loading page $page of recent images" }
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

        job?.invokeOnCompletion { job = null }
    }

    data class ViewState(
        val pendingQuery: String,
        val committedQuery: String,
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
