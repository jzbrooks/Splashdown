package com.jzbrooks.splashdown.ui

import assertk.all
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isNull
import assertk.assertions.prop
import com.jzbrooks.splashdown.data.Photo
import com.jzbrooks.splashdown.data.PhotoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.URI

@OptIn(ExperimentalCoroutinesApi::class)
class ImageSearchViewModelTest {
    private val photos = listOf(
        Photo(
            "61DA496D-AFC7-4592-8619-6A2DA820B9AB",
            "The smokey mountains at dawn",
            URI("https://cdn.example.com/images/61DA496D-AFC7-4592-8619-6A2DA820B9AB/small.jpg"),
            URI("https://cdn.example.com/images/61DA496D-AFC7-4592-8619-6A2DA820B9AB/large.jpg"),
        ),
        Photo(
            "1005E115-A4F6-405F-9C98-7814F4D32343",
            "Early morning fog over the bay",
            URI("https://cdn.example.com/images/1005E115-A4F6-405F-9C98-7814F4D32343/small.jpg"),
            URI("https://cdn.example.com/images/1005E115-A4F6-405F-9C98-7814F4D32343/large.jpg"),
        ),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `recent images are loaded on init`() = runTest {
        val viewModel = ImageSearchViewModel(SuccessfulResultDataSource(photos, emptyList()))

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.photos }
            .isNotEmpty()
    }

    @Test
    fun `next page is incremented after load`() = runTest {
        val viewModel = ImageSearchViewModel(SuccessfulResultDataSource(photos, emptyList()))

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.nextPage }
            .isEqualTo(2)
    }

    @Test
    fun `loading is signaled to view`() = runTest(StandardTestDispatcher()) {
        val viewModel = ImageSearchViewModel(
            SuccessfulResultDataSource(photos, emptyList())
        )

        val state = mutableListOf<ImageSearchViewModel.ViewState>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(state)
        }

        viewModel.reload()

        assertThat(state)
            .transform("isLoading") { s -> s.map { it.isLoading } }
            .containsExactly(false, true, false)
    }

    @Test
    fun `loading state is updated after successful load`() = runTest {
        val viewModel = ImageSearchViewModel(SuccessfulResultDataSource(photos, emptyList()))

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.isLoading }
            .isFalse()
    }

    @Test
    fun `image load signals network error`() = runTest {
        val viewModel = ImageSearchViewModel(FailureResultDataSource(PhotoResult.Error.Network))

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .all {
                prop("photos") { it.photos }.isEmpty()
                prop("error") { it.error }.isEqualTo(ImageSearchViewModel.Error.NETWORK)
        }
    }

    @Test
    fun `image load signals server error`() = runTest {
        val viewModel = ImageSearchViewModel(FailureResultDataSource(PhotoResult.Error.Server))

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .all {
                prop("photos") { it.photos }.isEmpty()
                prop("error") { it.error }.isEqualTo(ImageSearchViewModel.Error.SERVER)
            }
    }

    @Test
    fun `image load signals unknown error`() = runTest {
        val viewModel = ImageSearchViewModel(FailureResultDataSource(PhotoResult.Error.Unknown))

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .all {
                prop("photos") { it.photos }.isEmpty()
                prop("error") { it.error }.isEqualTo(ImageSearchViewModel.Error.UNKNOWN)
            }
    }

    @Test
    fun `acknowledgement clears errors`() = runTest {
        val viewModel = ImageSearchViewModel(FailureResultDataSource())

        advanceUntilIdle()

        viewModel.acknowledgeError()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("error") { it.error }.isNull()
    }

    @Test
    fun `loading state is updated after failure load`() = runTest {
        val viewModel = ImageSearchViewModel(FailureResultDataSource())

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.isLoading }
            .isFalse()
    }

    @Test
    fun `load more appends to photos`() = runTest {
        val viewModel = ImageSearchViewModel(SuccessfulResultDataSource(photos, emptyList()))

        advanceUntilIdle()

        viewModel.loadMore()

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.photos }
            .isEqualTo(photos + photos)
    }

    @Test
    fun `reload resets presented photos`() = runTest {
        val secondList = listOf(
            Photo(
                "2A019309-997D-4FC9-99DB-F3C113B43FCE",
                "A long beach",
                URI("https://cdn.example.com/images/2A019309-997D-4FC9-99DB-F3C113B43FCE/small.jpg"),
                URI("https://cdn.example.com/images/2A019309-997D-4FC9-99DB-F3C113B43FCE/large.jpg"),
            ),
        )

        val viewModel = ImageSearchViewModel(
            SwitcharooResultDataSource(photos, secondList)
        )

        advanceUntilIdle()

        viewModel.reload()

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.photos }
            .isEqualTo(secondList)
    }

    @Test
    fun `query inputs changing does not commit`() = runTest {
        val expectedQuery = "walnut"
        val viewModel = ImageSearchViewModel(SuccessfulResultDataSource(photos, emptyList()))

        viewModel.updateQuery(expectedQuery)

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }.all {
                prop("pendingQuery") { it.pendingQuery }.isEqualTo(expectedQuery)
                prop("commitedQuery") { it.committedQuery }.isEmpty()
            }
    }

    @Test
    fun `search commits pending query`() = runTest {
        val expectedQuery = "walnut"
        val viewModel = ImageSearchViewModel(SuccessfulResultDataSource(photos, emptyList()))

        viewModel.updateQuery(expectedQuery)
        viewModel.search()

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("commitedQuery") { it.committedQuery }.isEqualTo(expectedQuery)
    }

    @Test
    fun `search resets the presented photos`() = runTest {
        val searchResults = listOf(
            Photo(
                "5A5BC185-914F-44B6-B55C-4FF7ADD2D35F",
                "A recently oiled walnut table.",
                URI("https://cdn.example.com/images/5A5BC185-914F-44B6-B55C-4FF7ADD2D35F/small.jpg"),
                URI("https://cdn.example.com/images/5A5BC185-914F-44B6-B55C-4FF7ADD2D35F/large.jpg"),
            ),
        )

        val viewModel = ImageSearchViewModel(
            SuccessfulResultDataSource(photos, searchResults)
        )

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.photos }.isEqualTo(photos)

        viewModel.updateQuery("walnut")
        viewModel.search()

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.photos }.isEqualTo(searchResults)
    }

    @Test
    fun `clearing query reverts to recent photos`() = runTest {
        val searchResults = listOf(
            Photo(
                "5A5BC185-914F-44B6-B55C-4FF7ADD2D35F",
                "A recently oiled walnut table.",
                URI("https://cdn.example.com/images/5A5BC185-914F-44B6-B55C-4FF7ADD2D35F/small.jpg"),
                URI("https://cdn.example.com/images/5A5BC185-914F-44B6-B55C-4FF7ADD2D35F/large.jpg"),
            ),
        )

        val viewModel = ImageSearchViewModel(
            SuccessfulResultDataSource(photos, searchResults)
        )

        viewModel.updateQuery("walnut")
        viewModel.search()

        advanceUntilIdle()

        viewModel.updateQuery("")
        viewModel.search()

        advanceUntilIdle()

        assertThat(viewModel::state)
            .prop("value") { it.value }
            .prop("photos") { it.photos }.isEqualTo(photos)
    }
}
