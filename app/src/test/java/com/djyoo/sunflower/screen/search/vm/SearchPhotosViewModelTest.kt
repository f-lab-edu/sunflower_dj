package com.djyoo.sunflower.screen.search.vm

import com.djyoo.sunflower.screen.search.data.model.UnsplashPhoto
import com.djyoo.sunflower.screen.search.data.model.UnsplashPhotoUrls
import com.djyoo.sunflower.screen.search.data.model.UnsplashSearchResponse
import com.djyoo.sunflower.screen.search.data.model.UnsplashUser
import com.djyoo.sunflower.screen.search.data.repository.UnsplashRepository
import com.djyoo.sunflower.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
        coEvery {
            repository.searchPhotos(
                query = "apple",
                page = 1,
                any()
            )
        } returns Result.success(response)

        val viewModel = createViewModel()
        viewModel.onSearchRequested("apple")

        advanceUntilIdle()

        val state = viewModel.searchResult.value
        val expected = SearchPhotosUiState.Success(response)
        assertEquals(expected, state)
        assertEquals("apple", viewModel.query.value)
    }

    @Test
    fun search_whenRepositoryFails_emitsError() = runTest {
        // API 실패 시 Error 상태와 메시지가 반영되는지 검증
        coEvery { repository.searchPhotos(query = "apple", page = 1, any()) } returns
                Result.failure(RuntimeException("network error"))

        val viewModel = createViewModel()
        viewModel.onSearchRequested("apple")

        advanceUntilIdle()

        val state = viewModel.searchResult.value
        val expected = SearchPhotosUiState.Error("network error")
        assertEquals(expected, state)
    }

    @Test
    fun loadMore_whenHasMorePages_appendsResultsAndEmitsSuccess() = runTest {
        // 다음 페이지가 있을 때 연속으로 loadMore 요청해도 2페이지 한 번만 로드되고
        // 1페이지 뒤에 2페이지가 이어붙여지는지 ID 순서로 검증
        val page1 = fakeResponse(listOf(fakePhoto("1", "A")), totalPages = 2)
        val page2 = fakeResponse(listOf(fakePhoto("2", "B")), totalPages = 2)
        coEvery { repository.searchPhotos(query = "q", page = 1, any()) } returns Result.success(
            page1
        )
        coEvery { repository.searchPhotos(query = "q", page = 2, any()) } returns Result.success(
            page2
        )

        val viewModel = createViewModel()
        viewModel.onSearchRequested("q")
        advanceUntilIdle()

        viewModel.onLoadMoreRequested()
        viewModel.onLoadMoreRequested()
        advanceUntilIdle()

        val state = viewModel.searchResult.value
        val expectedResponse = UnsplashSearchResponse(
            results = listOf(page1.results[0], page2.results[0]),
            totalPages = 2,
        )
        val expected = SearchPhotosUiState.Success(expectedResponse)
        assertEquals(expected, state)
        coVerify(exactly = 1) {
            repository.searchPhotos(query = "q", page = 2, any())
        }
    }

    @Test
    fun isLoadingMore_duringLoadMore_isTrueThenFalse() = runTest {
        // 페이징 로딩 중 isLoadingMore가 true였다가 완료 후 false로 바뀌는지 검증
        val response = fakeResponse(listOf(fakePhoto("1", "A")), totalPages = 2)
        coEvery { repository.searchPhotos(query = "q", page = 1, any()) } returns Result.success(
            response
        )
        coEvery { repository.searchPhotos(query = "q", page = 2, any()) } coAnswers {
            // 2페이지 응답이 500ms 뒤에 도착한다고 가정
            delay(500)
            Result.success(
                fakeResponse(listOf(fakePhoto("2", "B")), totalPages = 2),
            )
        }

        val viewModel = createViewModel()
        viewModel.onSearchRequested("q")
        advanceUntilIdle()

        // when: 추가 로드 요청 직후 코루틴이 시작되도록만 일정 시간 진행
        viewModel.onLoadMoreRequested()
        advanceTimeBy(100)

        // then: 중간에는 true였다가
        assertTrue(viewModel.isLoadingMore.value)

        // when: 나머지 작업이 모두 끝나면
        advanceUntilIdle()

        // then: 최종적으로 false 로 내려와야 한다.
        assertFalse(viewModel.isLoadingMore.value)
    }
}
