package com.djyoo.sunflower.screen.search.vm

import com.djyoo.sunflower.screen.search.data.model.UnsplashSearchResponse

/**
 * 검색 사진 화면의 UI 상태.
 */
sealed class SearchPhotosUiState {
    data object Idle : SearchPhotosUiState()
    data object Loading : SearchPhotosUiState()
    data class Success(val response: UnsplashSearchResponse) : SearchPhotosUiState()
    data class Error(val message: String? = null) : SearchPhotosUiState()
}
