package com.djyoo.sunflower.screen.plant

import android.app.Application
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.djyoo.sunflower.R
import com.djyoo.sunflower.screen.main.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PlantListFragmentTest {

    /** plants.json 전체 목록 개수 (필터 해제 시 기대값) */
    private val fullPlantListSize = 17

    private fun launchMainActivity(): MainActivity {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        return controller.get()
    }

    /**
     * adapter 가 notify 계열 콜백을 한 번 호출할 때까지 대기한다.
     * 테스트 스레드에서 호출해야 하며, 메인 스레드를 블로킹하지 않는다.
     */
    private fun waitForAdapterUpdate(
        adapter: RecyclerView.Adapter<*>,
        timeoutMs: Long = 2000
    ) {
        val latch = CountDownLatch(1)

        val observer = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = latch.countDown()
            override fun onItemRangeInserted(start: Int, count: Int) = latch.countDown()
            override fun onItemRangeRemoved(start: Int, count: Int) = latch.countDown()
        }

        adapter.registerAdapterDataObserver(observer)

        val shadowLooper = Shadows.shadowOf(Looper.getMainLooper())
        val start = System.currentTimeMillis()

        try {
            while (System.currentTimeMillis() - start < timeoutMs) {
                shadowLooper.idle()

                if (latch.count == 0L) return
            }

            fail("Adapter update did not complete within $timeoutMs ms")

        } finally {
            adapter.unregisterAdapterDataObserver(observer)
        }
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

    @Test
    fun tappingFilterIcon_showsOnlyGrowZone9Plants() {
        // given: Plant List 탭 선택 (plants.json 전체 목록 표시)
        launchMainActivity()
        onView(withText("Plant List")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText("Apple")).check(matches(withText("Apple")))

        // when: 필터 아이콘 탭 (growZoneNumber 9만 표시, plants.json 기준 4개: Tomato, Avocado, Grape, Orange)
        onView(withId(R.id.filter_icon)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        // then: zone 9 식물만 리스트에 있고 Apple 은 없음.
        // Robolectric에서는 getGlobalVisibleRect()가 RecyclerView 자식에 대해 비어 있어 isDisplayed() 대신 텍스트 존재/doesNotExist 로 검증.
        onView(withText("Tomato")).check(matches(withText("Tomato")))
        onView(withText("Avocado")).check(matches(withText("Avocado")))
        onView(withText("Apple")).check(doesNotExist())
    }

    @Test
    fun tappingFilterIconAgain_showsFullList() {
        // given: Plant List 탭에서 필터 적용된 상태 (zone 9만 표시)
        val activity = launchMainActivity()
        onView(withText("Plant List")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        onView(withId(R.id.filter_icon)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText("Tomato")).check(matches(withText("Tomato")))
        onView(withText("Apple")).check(doesNotExist())

        // when: 필터 아이콘 다시 탭 (전체 목록)
        onView(withId(R.id.filter_icon)).perform(click())

        // then
        val recyclerView =
            activity.findViewById<RecyclerView>(R.id.plant_list_recycler_view)
                ?: error("RecyclerView(R.id.plant_list_recycler_view) not found")

        val adapter = recyclerView.adapter ?: error("RecyclerView adapter is null")

        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()
        assertEquals(fullPlantListSize, adapter.itemCount)
    }
}

