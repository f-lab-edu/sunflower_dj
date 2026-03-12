package com.djyoo.sunflower.screen.main

import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.viewpager2.widget.ViewPager2
import com.djyoo.sunflower.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityTest {

    private fun launchActivity(): MainActivity {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        return controller.get()
    }

    @Test
    fun mainActivityStarts_withMyGardenTabSelected() {
        val activity = launchActivity()
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)

        val myGardenTab = tabLayout.getTabAt(0)
        val plantTab = tabLayout.getTabAt(1)

        requireNotNull(myGardenTab)
        requireNotNull(plantTab)

        // 초기에는 My Garden 탭이 선택되어 있고, Plant 탭은 선택되지 않아야 한다.
        assertTrue(myGardenTab.isSelected)
        assertFalse(plantTab.isSelected)
    }

    @Test
    fun whenMyGardenSelected_tappingPlantText_selectsPlantTab() {
        val activity = launchActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)
        val filterIcon = activity.findViewById<ImageView>(R.id.filter_icon)

        // My Garden 이 선택된 초기 상태에서 "Plant List" 텍스트를 탭
        onView(withText("Plant List")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val plantTab = requireNotNull(tabLayout.getTabAt(1))

        // Plant 탭이 선택되고 ViewPager 페이지도 변경되어야 한다.
        assertEquals(1, viewPager.currentItem)
        // Plant 탭에서는 필터 아이콘이 보여야 한다.
        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.scrollState)
        assertEquals(View.VISIBLE, filterIcon.visibility)
        assertTrue(plantTab.isSelected)
    }

    @Test
    fun whenPlantSelected_tappingPlantTextAgain_doesNothing() {
        val activity = launchActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)
        val filterIcon = activity.findViewById<ImageView>(R.id.filter_icon)

        // 먼저 Plant 탭을 선택
        onView(withText("Plant List")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val plantTab = requireNotNull(tabLayout.getTabAt(1))

        val beforeCurrentItem = viewPager.currentItem
        val beforeFilterVisibility = filterIcon.visibility

        // 이미 Plant 가 선택된 상태에서 Plant 텍스트를 다시 탭
        onView(withText("Plant List")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // 아무 변화가 없어야 한다.
        assertEquals(beforeCurrentItem, viewPager.currentItem)
        assertEquals(beforeFilterVisibility, filterIcon.visibility)
        assertTrue(plantTab.isSelected)
    }

    @Test
    fun whenPlantSelected_tappingMyGardenText_selectsMyGardenTab() {
        val activity = launchActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)

        // 먼저 Plant 탭을 선택
        onView(withText("Plant List")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // 그 상태에서 "My garden" 텍스트를 탭
        onView(withText("My garden")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val myGardenTab = requireNotNull(tabLayout.getTabAt(0))
        val plantTab = requireNotNull(tabLayout.getTabAt(1))

        // My Garden 탭이 선택되고, ViewPager 페이지도 0번으로 돌아와야 한다.
        assertEquals(0, viewPager.currentItem)
        assertTrue(myGardenTab.isSelected)
        assertFalse(plantTab.isSelected)
    }

    @Test
    fun titleBar_onLaunch_isVisibleAndShowsSunflower() {
        launchActivity()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val expectedTitle = "Sunflower"
        onView(withId(R.id.title_text)).check(matches(withText(expectedTitle)))
        onView(withId(R.id.title_text)).check(matches(isDisplayed()))
    }

    @Test
    fun mainActivity_hasCoordinatorLayoutWithAppBar() {
        launchActivity()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withId(R.id.app_bar_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()))
    }

    @Test
    fun appBar_whenSetExpandedFalse_titleNotVisible() {
        val activity = launchActivity()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val appBarLayout = activity.findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.setExpanded(false, false)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withId(R.id.title_text)).check(matches(not(isDisplayed())))
    }

    @Test
    fun appBar_whenSetExpandedTrue_titleVisible() {
        val activity = launchActivity()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val appBarLayout = activity.findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.setExpanded(true, false)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withId(R.id.title_text)).check(matches(isDisplayed()))
    }
}

