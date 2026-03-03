package com.djyoo.sunflower.screen.planet

import android.app.Application
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.djyoo.sunflower.screen.main.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PlantListFragmentTest {

    private fun launchMainActivity(): MainActivity {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        return controller.get()
    }

    @Test
    fun plantListTab_showsPlantsFromAssets() {
        // given: MainActivity 가 실행된 상태에서
        launchMainActivity()

        // when: 상단 탭에서 "Plant List" 를 탭한다.
        onView(withText("Plant List")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: assets/plants.json 의 첫 번째 아이템인 "Apple" 텍스트가 화면에 표시된다.
        onView(withText("Apple")).perform(click()) // 클릭 가능한지까지 확인
    }

    @Test
    fun tappingPlantItem_opensPlantDetailActivity() {
        // given: MainActivity 및 Plant List 탭이 선택된 상태
        launchMainActivity()
        onView(withText("Plant List")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // when: "Apple" 아이템을 탭한다.
        onView(withText("Apple")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: PlantDetailActivity 로 이동하는 인텐트가 발생해야 한다.
        val application = ApplicationProvider.getApplicationContext<Application>()
        val shadowApplication = Shadows.shadowOf(application)
        val startedIntent = shadowApplication.nextStartedActivity

        assertEquals(
            PlantDetailActivity::class.java.name,
            startedIntent.component?.className,
        )
    }
}

