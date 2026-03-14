package com.djyoo.sunflower.screen.search.data.repository

import com.djyoo.sunflower.common.network.RetrofitProvider
import com.djyoo.sunflower.screen.search.data.api.UnsplashApi
import com.djyoo.sunflower.screen.search.data.model.UnsplashSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Unsplash Search Photos API를 호출하는 저장소.
 * 공통 [RetrofitProvider]로 생성한 [UnsplashApi]를 통해 요청한다.
 */
class UnsplashRepository(
    private val api: UnsplashApi = createApi(),
) {

    suspend fun searchPhotos(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
        clientId: String = DEFAULT_CLIENT_ID,
    ): Result<UnsplashSearchResponse> = withContext(Dispatchers.IO) {
        runCatching {
            api.searchPhotos(query = query, page = page, perPage = perPage, clientId = clientId)
        }
    }

    private companion object {
        const val DEFAULT_CLIENT_ID = "v5OyabaPl_tUJkeWr3AD5rHtX_KeSX1movJJgjiIQLo"

        fun createApi(): UnsplashApi =
            RetrofitProvider.create().create(UnsplashApi::class.java)
    }
}
