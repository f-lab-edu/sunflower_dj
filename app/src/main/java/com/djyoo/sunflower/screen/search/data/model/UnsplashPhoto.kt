package com.djyoo.sunflower.screen.search.data.model

import com.google.gson.annotations.SerializedName

/**
 * Unsplash 검색 결과의 사진 항목.
 */
data class UnsplashPhoto(
    @field:SerializedName("id") val id: String,
    @field:SerializedName("urls") val urls: UnsplashPhotoUrls,
    @field:SerializedName("user") val user: UnsplashUser,
)
