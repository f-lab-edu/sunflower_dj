package com.djyoo.sunflower.screen.search.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djyoo.sunflower.screen.search.data.model.UnsplashSearchResponse
import com.djyoo.sunflower.screen.search.data.repository.UnsplashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Unsplash 검색 사진 화면의 ViewModel.
 * [UnsplashRepository]를 통해 search/photos API를 호출하고 결과를 [searchResult]로 노출한다.
 */
class SearchPhotosViewModel(
    private val repository: UnsplashRepository = UnsplashRepository(),
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResult = MutableStateFlow<SearchPhotosUiState>(SearchPhotosUiState.Idle)
    val searchResult: StateFlow<SearchPhotosUiState> = _searchResult.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = 1
    private var totalPages = 1
    private val accumulatedResults = mutableListOf<UnsplashSearchResponse>()

    /**
     * View에서 검색을 요청했을 때 호출되는 진입점.
     * 검색어를 갱신하고 1페이지부터 새로 검색한다.
     */
    fun onSearchRequested(query: String) {
        if (query.isBlank()) return
        _query.value = query
        currentPage = 1
        totalPages = 1
        accumulatedResults.clear()
        loadPage(1, append = false)
    }

    /**
     * View에서 추가 로드를 요청했을 때 호출되는 진입점.
     * 다음 페이지를 불러와 기존 결과에 이어붙인다.
     * 이미 추가 로딩 중이거나 마지막 페이지에 도달한 경우에는 아무 것도 하지 않는다.
     */
    fun onLoadMoreRequested() {
        if (currentPage >= totalPages) return
        if (_searchResult.value is SearchPhotosUiState.Loading) return
        if (_isLoadingMore.value) return
        loadPage(currentPage + 1, append = true)
    }

    /**
     * 리스트가 스크롤될 때 View에서 호출하는 진입점.
     * 스크롤 위치와 전체 아이템 수만 전달하고, 실제 로딩 여부 판단은 ViewModel 이 담당한다.
     */
    fun onListScrolled(lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (totalItemCount <= 0) return
        if (shouldLoadMore(lastVisibleItemPosition, totalItemCount)) {
            onLoadMoreRequested()
        }
    }

    private fun shouldLoadMore(lastVisibleItemPosition: Int, totalItemCount: Int): Boolean {
        if (currentPage >= totalPages) return false
        if (_searchResult.value is SearchPhotosUiState.Loading) return false
        if (_isLoadingMore.value) return false

        return lastVisibleItemPosition >= totalItemCount - PAGINATION_TRIGGER_OFFSET
    }

    private fun loadPage(page: Int, append: Boolean) {
        val q = _query.value
        if (q.isBlank()) return

        viewModelScope.launch {
            if (append) {
                _isLoadingMore.value = true
            } else {
                _searchResult.value = SearchPhotosUiState.Loading
            }
            repository.searchPhotos(query = q, page = page, perPage = if (append) PAGE_SIZE else INITIAL_PAGE_SIZE)
                .onSuccess { response ->
                    currentPage = page
                    totalPages = response.totalPages
                    if (append) {
                        accumulatedResults.add(response)
                        val mergedResponse = UnsplashSearchResponse(
                            totalPages = response.totalPages,
                            results = accumulatedResults.flatMap { it.results },
                        )
                        _searchResult.value = SearchPhotosUiState.Success(mergedResponse)
                    } else {
                        accumulatedResults.clear()
                        accumulatedResults.add(response)
                        _searchResult.value = SearchPhotosUiState.Success(response)
                    }
                }
                .onFailure { throwable ->
                    _searchResult.value = SearchPhotosUiState.Error(throwable.message)
                }
                .also {
                    if (append) _isLoadingMore.value = false
                }
        }
    }

    private companion object {
        /** 첫 로드 시 요청 개수 */
        const val INITIAL_PAGE_SIZE = 75

        /** 이후 페이지당 요청 개수 */
        const val PAGE_SIZE = 25

        /** 페이징 트리거 오프셋 (마지막 N개 남았을 때 추가 로드) */
        const val PAGINATION_TRIGGER_OFFSET = 4
    }
}
