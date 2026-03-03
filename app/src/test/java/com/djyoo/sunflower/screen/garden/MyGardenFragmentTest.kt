package com.djyoo.sunflower.screen.garden

import android.os.Looper
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.viewpager2.widget.ViewPager2
import com.djyoo.sunflower.R
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
class MyGardenFragmentTest {

    private fun launchMainActivity(): MainActivity {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        return controller.get()
    }

    @Test
    fun tappingAddPlanetButton_fromMyGarden_navigatesToPlantListTab() {
        // given: MainActivity 가 실행된 상태에서 기본 탭은 My garden
        val activity = launchMainActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        assertEquals(0, viewPager.currentItem)

        // when: My garden 화면의 "Add planet" 버튼을 탭한다.
        onView(withText("Add planet")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: Plant List 탭(인덱스 1)으로 이동해야 한다.
        assertEquals(1, viewPager.currentItem)
    }
}

