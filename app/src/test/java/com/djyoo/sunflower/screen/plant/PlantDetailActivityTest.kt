package com.djyoo.sunflower.screen.plant

import android.app.Application
import android.content.Intent
import android.os.Looper
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.image.ImageLoader
import com.djyoo.sunflower.testutil.FakeImageLoader
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PlantDetailActivityTest {

    private fun launchActivity(
        plantId: String,
        imageLoader: ImageLoader? = null,
    ): PlantDetailActivity {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val intent = Intent(application, PlantDetailActivity::class.java).apply {
            putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId)
        }
        val controller = Robolectric.buildActivity(PlantDetailActivity::class.java, intent)
        imageLoader?.let { controller.get().imageLoader = it }
        controller.setup()
        return controller.get()
    }

    @Test
    fun launchingWithAppleId_showsAppleDetailsFromAssets() {
        // given
        launchActivity("malus-pumila")

        // when
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: 제목, 워터링 정보가 화면에 표시된다.
        onView(withText("Apple")).check(matches(isDisplayed()))
        onView(withText("Watering needs")).check(matches(isDisplayed()))
        onView(withText("every 30 days")).check(matches(isDisplayed()))
    }

    @Test
    fun tappingShareButton_launchesChooserWithShareText() {
        // given
        launchActivity("malus-pumila")
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // when
        onView(withContentDescription("Share")).perform(click())

        // then: ACTION_CHOOSER 로 공유 인텐트가 발행된다.
        val application = ApplicationProvider.getApplicationContext<Application>()
        val shadowApplication = Shadows.shadowOf(application)
        val startedIntent = shadowApplication.nextStartedActivity

        assertEquals(Intent.ACTION_CHOOSER, startedIntent.action)

        val sendIntent = startedIntent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        assertEquals(Intent.ACTION_SEND, sendIntent?.action)
        assertEquals("text/plain", sendIntent?.type)

        assertEquals(
            "Check out the Apple plant in the Android Sunflower app",
            sendIntent?.getStringExtra(Intent.EXTRA_TEXT),
        )
    }

    @Test
    fun addButton_isNotVisible_beforeImageLoaded() {
        // given
        val fakeImageLoader = FakeImageLoader()
        val activity = launchActivity("malus-pumila", fakeImageLoader)

        // when
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: 이미지가 로딩되지 않았으므로 Add 버튼이 보이지 않는다.
        assertEquals(View.GONE, activity.findViewById<View>(R.id.detail_add_button).visibility)
    }

    @Test
    fun addButton_isVisible_afterImageLoaded() {
        // given
        val fakeImageLoader = FakeImageLoader()
        val activity = launchActivity("malus-pumila", fakeImageLoader)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Robolectric에서는 AppBarLayout 스크롤이 동작하지 않아
        // 확장 상태(detailTitle visible)를 수동으로 설정한다.
        activity.findViewById<View>(R.id.detail_title).visibility = View.VISIBLE

        // when: 이미지 로딩 완료
        fakeImageLoader.triggerLoaded()

        // then: 이미지 로딩 후 Add 버튼이 표시된다.
        assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.detail_add_button).visibility)
    }
}
