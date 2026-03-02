package com.djyoo.sunflower.common.screen.main

import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.djyoo.sunflower.R
import com.google.android.material.tabs.TabLayout
import org.junit.Assert.assertEquals
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

    private fun TabLayout.findTabByTitle(title: String): TabLayout.Tab? {
        for (i in 0 until tabCount) {
            val tab = getTabAt(i) ?: continue
            val view = tab.customView ?: continue
            val titleTextView = view.findViewById<TextView>(R.id.tabTitle) ?: continue
            if (titleTextView.text.toString() == title) {
                return tab
            }
        }
        return null
    }

    private fun TabLayout.performClickOnTab(tab: TabLayout.Tab) {
        val tabStrip = getChildAt(0) as ViewGroup
        var index = -1
        for (i in 0 until tabCount) {
            if (getTabAt(i) == tab) {
                index = i
                break
            }
        }
        require(index >= 0) { "Tab not attached to this TabLayout" }

        val tabView = tabStrip.getChildAt(index)
        tabView.performClick()
    }

    @Test
    fun mainActivityStarts_withMyGardenTabSelected() {
        val activity = launchActivity()
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)

        val myGardenTitle = activity.getString(R.string.title_my_garden)
        val plantListTitle = activity.getString(R.string.title_plant_list)

        val myGardenTab = requireNotNull(tabLayout.findTabByTitle(myGardenTitle))
        val plantTab = requireNotNull(tabLayout.findTabByTitle(plantListTitle))

        // 초기에는 My Garden 탭이 선택되어 있고, Plant 탭은 선택되지 않아야 한다.
        assertTrue(myGardenTab.isSelected)
        assertTrue(!plantTab.isSelected)
    }

    @Test
    fun whenMyGardenSelected_tappingPlantText_selectsPlantTab() {
        val activity = launchActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)
        val ivFilter = activity.findViewById<ImageView>(R.id.iv_filter)

        val plantListTitle = activity.getString(R.string.title_plant_list)
        val plantTab = requireNotNull(tabLayout.findTabByTitle(plantListTitle))

        // My Garden 이 선택된 초기 상태에서 Plant 탭을 탭 (텍스트를 기준으로 찾되, 실제 클릭은 탭 전체에 수행)
        tabLayout.performClickOnTab(plantTab)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Plant 탭이 선택되고 ViewPager 페이지도 변경되어야 한다.
        assertEquals(1, viewPager.currentItem)
        // Plant 탭에서는 필터 아이콘이 보여야 한다.
        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.scrollState)
        assertEquals(true, ivFilter.visibility == ImageView.VISIBLE)
        assertTrue(plantTab.isSelected)
    }

    @Test
    fun whenPlantSelected_tappingPlantTextAgain_doesNothing() {
        val activity = launchActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)
        val ivFilter = activity.findViewById<ImageView>(R.id.iv_filter)

        val plantListTitle = activity.getString(R.string.title_plant_list)
        val plantTab = requireNotNull(tabLayout.findTabByTitle(plantListTitle))

        // 먼저 Plant 탭을 선택
        tabLayout.performClickOnTab(plantTab)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        val beforeCurrentItem = viewPager.currentItem
        val beforeFilterVisible = ivFilter.visibility == ImageView.VISIBLE

        // 이미 Plant 가 선택된 상태에서 Plant 탭을 다시 탭
        tabLayout.performClickOnTab(plantTab)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // 아무 변화가 없어야 한다.
        assertEquals(beforeCurrentItem, viewPager.currentItem)
        assertEquals(beforeFilterVisible, ivFilter.visibility == ImageView.VISIBLE)
        assertTrue(plantTab.isSelected)
    }

    @Test
    fun whenPlantSelected_tappingMyGardenText_selectsMyGardenTab() {
        val activity = launchActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)

        val myGardenTitle = activity.getString(R.string.title_my_garden)
        val plantListTitle = activity.getString(R.string.title_plant_list)

        val plantTab = requireNotNull(tabLayout.findTabByTitle(plantListTitle))
        val myGardenTab = requireNotNull(tabLayout.findTabByTitle(myGardenTitle))

        // 먼저 Plant 탭을 선택
        tabLayout.performClickOnTab(plantTab)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // 그 상태에서 My Garden 탭을 탭
        tabLayout.performClickOnTab(myGardenTab)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // My Garden 탭이 선택되고, ViewPager 페이지도 0번으로 돌아와야 한다.
        assertEquals(0, viewPager.currentItem)
        assertTrue(myGardenTab.isSelected)
        assertTrue(!plantTab.isSelected)
    }
}

