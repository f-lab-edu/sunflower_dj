package com.djyoo.sunflower.screen.search

import android.app.Application
import android.content.Intent
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.djyoo.sunflower.screen.search.data.model.UnsplashPhoto
import com.djyoo.sunflower.screen.search.data.model.UnsplashPhotoUrls
import com.djyoo.sunflower.screen.search.data.model.UnsplashSearchResponse
import com.djyoo.sunflower.screen.search.data.model.UnsplashUser
import com.djyoo.sunflower.screen.search.data.repository.UnsplashRepository
import io.mockk.coEvery
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(org.robolectric.RobolectricTestRunner::class)
@Config(sdk = [34])
class SearchPhotosActivityTest {

    @After
    fun tearDown() {
        // 다른 테스트에 영향을 주지 않도록 MockK 상태 초기화
        unmockkAll()
    }

    private fun launchActivity(query: String? = null): SearchPhotosActivity {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val intent = Intent(app, SearchPhotosActivity::class.java).apply {
            query?.let { putExtra(SearchPhotosActivity.EXTRA_QUERY, it) }
        }
        val controller = Robolectric.buildActivity(SearchPhotosActivity::class.java, intent)
        controller.setup()
        return controller.get()
    }

    @Test
    fun launchWithQuery_showsToolbarTitle() {
        // EXTRA_QUERY로 실행 시 툴바 타이틀 "Photos by Unsplash"가 표시되는지 검증
        launchActivity("apple")
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText("Photos by Unsplash")).check(matches(isDisplayed()))
    }

    @Test
    fun launchWithAppleQuery_showsAppleUserFromRepository() {
        // given: UnsplashRepository 생성 시 Fake 응답을 반환하도록 설정
        mockkConstructor(UnsplashRepository::class)
        val photo = UnsplashPhoto(
            id = "1",
            urls = UnsplashPhotoUrls(small = "https://example.com/apple.jpg"),
            user = UnsplashUser(name = "AppleUser", username = "apple_user"),
        )
        val response = UnsplashSearchResponse(
            results = listOf(photo),
            totalPages = 1,
        )
        coEvery {
            anyConstructed<UnsplashRepository>().searchPhotos(
                query = "apple",
                page = 1,
                perPage = any(),
            )
        } returns Result.success(response)

        // when: EXTRA_QUERY 에 "apple" 을 넣고 화면을 띄운다.
        launchActivity("apple")
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: RecyclerView 에 Fake 응답의 사용자 이름이 표시된다.
        onView(withText("AppleUser")).check(matches(isDisplayed()))
    }

    @Test
    fun launchWithoutQuery_doesNotCrash() {
        // EXTRA_QUERY 없이 실행해도 크래시 없이 화면이 뜨는지 검증
        launchActivity(null)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText("Photos by Unsplash")).check(matches(isDisplayed()))
    }
}
