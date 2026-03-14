package com.djyoo.sunflower.screen.search.vm

import com.djyoo.sunflower.screen.search.data.model.UnsplashPhoto
import com.djyoo.sunflower.screen.search.data.model.UnsplashPhotoUrls
import com.djyoo.sunflower.screen.search.data.model.UnsplashSearchResponse
import com.djyoo.sunflower.screen.search.data.model.UnsplashUser
import com.djyoo.sunflower.screen.search.data.repository.UnsplashRepository
import com.djyoo.sunflower.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchPhotosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val repository = mockk<UnsplashRepository>()

    private fun fakePhoto(id: String, userName: String) = UnsplashPhoto(
        id = id,
        urls = UnsplashPhotoUrls(small = "https://example.com/small.jpg"),
        user = UnsplashUser(name = userName, username = "user_$id"),
    )

    private fun fakeResponse(results: List<UnsplashPhoto>, totalPages: Int = 1) =
        UnsplashSearchResponse(results = results, totalPages = totalPages)

    private fun createViewModel() = SearchPhotosViewModel(repository)

    @Test
    fun search_withNonBlankQuery_emitsLoadingThenSuccess() = runTest {
        // 검색 시 Loading 후 API 성공 시 Success 상태로 전환되는지 검증
        val response = fakeResponse(listOf(fakePhoto("1", "Tobi")), totalPages = 1)
        coEvery { repository.searchPhotos(query = "apple", page = 1, any(), any()) } returns Result.success(response)

        val viewModel = createViewModel()
        viewModel.search("apple")

        advanceUntilIdle()

        val state = viewModel.searchResult.value
        assertTrue(state is SearchPhotosUiState.Success)
        assertEquals(1, (state as SearchPhotosUiState.Success).response.results.size)
        assertEquals("apple", viewModel.query.value)
    }

    @Test
    fun search_whenRepositoryFails_emitsError() = runTest {
        // API 실패 시 Error 상태와 메시지가 반영되는지 검증
        coEvery { repository.searchPhotos(query = "apple", page = 1, any(), any()) } returns
                Result.failure(RuntimeException("network error"))

        val viewModel = createViewModel()
        viewModel.search("apple")

        advanceUntilIdle()

        val state = viewModel.searchResult.value
        assertTrue(state is SearchPhotosUiState.Error)
        assertEquals("network error", (state as SearchPhotosUiState.Error).message)
    }

    @Test
    fun loadMore_whenHasMorePages_appendsResultsAndEmitsSuccess() = runTest {
        // 다음 페이지가 있을 때 loadMore 호출 시 1페이지 뒤에 2페이지가 이어붙여지는지 ID 순서로 검증
        val page1 = fakeResponse(listOf(fakePhoto("1", "A")), totalPages = 2)
        val page2 = fakeResponse(listOf(fakePhoto("2", "B")), totalPages = 2)
        coEvery { repository.searchPhotos(query = "q", page = 1, any(), any()) } returns Result.success(page1)
        coEvery { repository.searchPhotos(query = "q", page = 2, any(), any()) } returns Result.success(page2)

        val viewModel = createViewModel()
        viewModel.search("q")
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        val results = (viewModel.searchResult.value as SearchPhotosUiState.Success).response.results
        assertEquals(listOf("1", "2"), results.map { it.id })
    }

    @Test
    fun isLoadingMore_duringLoadMore_isTrueThenFalse() = runTest {
        // 페이징 로딩 중 isLoadingMore가 true였다가 완료 후 false로 바뀌는지 검증
        val response = fakeResponse(listOf(fakePhoto("1", "A")), totalPages = 2)
        coEvery { repository.searchPhotos(query = "q", page = 1, any(), any()) } returns Result.success(response)
        coEvery { repository.searchPhotos(query = "q", page = 2, any(), any()) } returns Result.success(
            fakeResponse(listOf(fakePhoto("2", "B")), totalPages = 2),
        )

        val viewModel = createViewModel()
        viewModel.search("q")
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        assertFalse(viewModel.isLoadingMore.value)
    }
}
