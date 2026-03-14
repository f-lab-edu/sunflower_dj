package com.djyoo.sunflower.screen.search.data.model

import com.google.gson.annotations.SerializedName

/**
 * Unsplash Search Photos API 응답 DTO.
 */
data class UnsplashSearchResponse(
    @field:SerializedName("results") val results: List<UnsplashPhoto>,
    @field:SerializedName("total_pages") val totalPages: Int,
)
