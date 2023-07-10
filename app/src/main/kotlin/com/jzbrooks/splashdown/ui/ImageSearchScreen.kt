package com.jzbrooks.splashdown.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.jzbrooks.splashdown.ui.theme.SplashdownTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import logcat.logcat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageSearchScreen(
    it: PaddingValues,
    snackbarHostState: SnackbarHostState,
    viewModel: ImageSearchViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullRefreshState(refreshing = state.isLoading, onRefresh = viewModel::reload)
    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .map { index -> state.photos.isNotEmpty() && index > state.photos.size - 30 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                logcat { "Loading more images. Current size ${state.photos.size}" }
                viewModel.loadMore()
            }
    }

    LaunchedEffect(state.error) {
        val error = state.error ?: return@LaunchedEffect

        val result = snackbarHostState.showSnackbar(
            message = when (error) {
                ImageSearchViewModel.Error.NETWORK -> "Are you connected to the internet?"
                ImageSearchViewModel.Error.SERVER -> "There was a problem with our server.\nPlease try again later."
                ImageSearchViewModel.Error.UNKNOWN -> "There was an unanticipated error.\nPlease try again later."
            }
        )

        if (result === SnackbarResult.Dismissed) {
            viewModel.acknowledgeError()
        }
    }

    Column(modifier = Modifier
        .padding(it)
        .fillMaxSize()
    ) {
        OutlinedTextField(
            label = { Text("Search Images") },
            value = state.query,
            onValueChange = viewModel::updateQuery,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .pullRefresh(pullRefreshState)
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.photos) {
                    AsyncImage(
                        model = it.url.toASCIIString(),
                        contentDescription = it.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageSearchScreenPreview() {
    SplashdownTheme {
        ImageSearchScreen(PaddingValues.Absolute(), SnackbarHostState())
    }
}
