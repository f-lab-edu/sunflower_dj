package com.djyoo.sunflower.screen.search.data.model

import com.google.gson.annotations.SerializedName

/**
 * Unsplash 사진의 업로더 사용자 정보.
 */
data class UnsplashUser(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("username") val username: String,
) {
    val attributionUrl: String
        get() = "https://unsplash.com/$username?utm_source=sunflower&utm_medium=referral"
}
