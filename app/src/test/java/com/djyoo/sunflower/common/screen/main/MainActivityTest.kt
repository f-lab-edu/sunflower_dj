package com.djyoo.sunflower.common.screen.main

import android.os.Looper
import android.widget.ImageView
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

    @Test
    fun clickingTabs_movesToCorrectPage() {
        val activity = launchActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)
        val ivFilter = activity.findViewById<ImageView>(R.id.iv_filter)

        // 초기 탭 / 페이지는 0번(My Garden)이어야 한다.
        assertEquals(0, viewPager.currentItem)

        // 두 번째 탭(Plant list)을 선택하면 ViewPager의 페이지도 1번으로 변경되어야 한다.
        val secondTab = tabLayout.getTabAt(1)
        requireNotNull(secondTab)

        secondTab.select()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        assertEquals(1, viewPager.currentItem)
        // Plant 탭에서는 필터 아이콘이 보여야 한다.
        assertEquals(ViewPager2.SCROLL_STATE_IDLE, viewPager.scrollState)
        assertEquals(true, ivFilter.visibility == ImageView.VISIBLE)
    }

    @Test
    fun swipingPages_updatesSelectedTab() {
        val activity = launchActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = activity.findViewById<TabLayout>(R.id.tab_layout)
        val ivFilter = activity.findViewById<ImageView>(R.id.iv_filter)

        // ViewPager에서 두 번째 페이지로 스와이프(프로그램matically 설정)
        viewPager.setCurrentItem(1, false)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // TabLayout의 선택된 탭도 두 번째 탭이어야 한다.
        assertEquals(1, tabLayout.selectedTabPosition)
        // Plant 탭에서는 필터 아이콘이 보여야 한다.
        assertTrue(ivFilter.visibility == ImageView.VISIBLE)
    }
}

