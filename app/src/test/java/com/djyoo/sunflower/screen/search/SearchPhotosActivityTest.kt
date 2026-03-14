package com.djyoo.sunflower.screen.search

import android.app.Application
import android.content.Intent
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(org.robolectric.RobolectricTestRunner::class)
@Config(sdk = [34])
class SearchPhotosActivityTest {

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
    fun launchWithoutQuery_doesNotCrash() {
        // EXTRA_QUERY 없이 실행해도 크래시 없이 화면이 뜨는지 검증
        launchActivity(null)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText("Photos by Unsplash")).check(matches(isDisplayed()))
    }
}
