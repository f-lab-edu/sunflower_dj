package com.djyoo.sunflower.screen.search.data.model

import com.google.gson.annotations.SerializedName

/**
 * Unsplash 사진의 이미지 URL (small 등).
 */
data class UnsplashPhotoUrls(
    @field:SerializedName("small") val small: String,
)
