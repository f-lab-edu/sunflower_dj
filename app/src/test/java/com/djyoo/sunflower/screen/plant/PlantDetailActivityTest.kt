package com.djyoo.sunflower.screen.plant

import android.app.Application
import android.content.Intent
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.djyoo.sunflower.R
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

    private fun launchActivity(plantId: String): PlantDetailActivity {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val intent = Intent(application, PlantDetailActivity::class.java).apply {
            putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId)
        }
        val controller = Robolectric.buildActivity(PlantDetailActivity::class.java, intent).setup()
        return controller.get()
    }

    @Test
    fun launchingWithAppleId_showsAppleDetailsFromAssets() {
        // given
        launchActivity("malus-pumila")

        // when
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val appContext = ApplicationProvider.getApplicationContext<Application>()
        val wateringText = appContext.getString(R.string.watering_interval_days, 30)

        // then: 제목, 워터링 정보가 화면에 표시된다.
        onView(withText("Apple")).check(matches(isDisplayed()))
        onView(withText(R.string.watering_needs)).check(matches(isDisplayed()))
        onView(withText(wateringText)).check(matches(isDisplayed()))
    }

    @Test
    fun tappingShareButton_launchesChooserWithShareText() {
        // given
        launchActivity("malus-pumila")
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // when
        onView(withId(R.id.detail_share_button)).perform(click())

        // then: ACTION_CHOOSER 로 공유 인텐트가 발행된다.
        val application = ApplicationProvider.getApplicationContext<Application>()
        val shadowApplication = Shadows.shadowOf(application)
        val startedIntent = shadowApplication.nextStartedActivity

        assertEquals(Intent.ACTION_CHOOSER, startedIntent.action)

        val sendIntent = startedIntent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
        assertEquals(Intent.ACTION_SEND, sendIntent?.action)
        assertEquals("text/plain", sendIntent?.type)

        val expectedText = application.getString(R.string.share_plant_text, "Apple")
        assertEquals(expectedText, sendIntent?.getStringExtra(Intent.EXTRA_TEXT))
    }
}

