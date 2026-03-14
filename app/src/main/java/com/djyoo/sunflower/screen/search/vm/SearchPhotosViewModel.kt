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
     * 검색어를 갱신하고 1페이지부터 새로 검색한다.
     */
    fun search(query: String) {
        if (query.isBlank()) return
        _query.value = query
        currentPage = 1
        totalPages = 1
        accumulatedResults.clear()
        loadPage(1, append = false)
    }

    /**
     * 다음 페이지를 불러와 기존 결과에 이어붙인다.
     */
    fun loadMore() {
        if (currentPage >= totalPages) return
        if (_searchResult.value is SearchPhotosUiState.Loading) return
        loadPage(currentPage + 1, append = true)
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
    }
}
