package com.djyoo.sunflower.screen.search.data.api

import com.djyoo.sunflower.screen.search.data.model.UnsplashSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Unsplash API Retrofit 인터페이스.
 * Base URL은 [RetrofitProvider.create] 내부에서 사용하는 Unsplash Base URL을 따른다.
 */
interface UnsplashApi {

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("client_id") clientId: String = "v5OyabaPl_tUJkeWr3AD5rHtX_KeSX1movJJgjiIQLo",
    ): UnsplashSearchResponse
}
