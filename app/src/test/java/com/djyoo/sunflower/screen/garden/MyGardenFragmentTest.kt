package com.djyoo.sunflower.screen.garden

import android.app.Application
import android.os.Looper
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.viewpager2.widget.ViewPager2
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.database.SunflowerDatabase
import com.djyoo.sunflower.screen.main.MainActivity
import com.djyoo.sunflower.screen.plant.data.model.Plant
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MyGardenFragmentTest {

    private lateinit var testDatabase: SunflowerDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val directExecutor = java.util.concurrent.Executor { it.run() }
        testDatabase = Room.inMemoryDatabaseBuilder(context, SunflowerDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor(directExecutor)
            .setTransactionExecutor(directExecutor)
            .build()
        SunflowerDatabase.setInstance(testDatabase)
    }

    @After
    fun tearDown() {
        testDatabase.close()
    }

    private fun launchMainActivity(): MainActivity {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        return controller.get()
    }

    private fun insertGardenPlant(
        plantId: String = "cilantro-1",
        name: String = "Cilantro",
        description: String = "Fresh herb",
        growZoneNumber: Int = 1,
        wateringInterval: Int = 2,
        imageUrl: String = "https://example.com/cilantro.jpg",
    ) {
        val plant = Plant(plantId, name, description, growZoneNumber, wateringInterval, imageUrl)
        runBlocking {
            testDatabase.plantDao().insertPlant(plant)
        }
    }

    @Test
    fun tappingAddPlantButton_fromMyGarden_navigatesToPlantListTab() {
        // given: MainActivity 가 실행된 상태에서 기본 탭은 My garden
        val activity = launchMainActivity()
        val viewPager = activity.findViewById<ViewPager2>(R.id.view_pager)
        assertEquals(0, viewPager.currentItem)

        // when: My garden 화면의 "Add plant" 버튼을 탭한다.
        onView(withText("Add plant")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: Plant List 탭(인덱스 1)으로 이동해야 한다.
        assertEquals(1, viewPager.currentItem)
    }

    @Test
    fun emptyGarden_showsEmptyStateAndAddButton() {
        // given: DB 에 저장된 식물이 없는 상태에서 My garden 탭을 연다.
        launchMainActivity()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: 비어있는 안내 문구와 Add 버튼이 보인다.
        onView(withText("Your garden is empty")).check(matches(isDisplayed()))
        onView(withText("Add plant")).check(matches(isDisplayed()))
    }

    @Test
    fun gardenWithPlants_showsPlantCardAndHidesEmptyState() {
        // given: 정원에 Cilantro 식물이 하나 저장된 상태
        insertGardenPlant()
        launchMainActivity()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: 빈 정원 안내 문구는 사라지고, Cilantro 카드가 표시된다.
        // TextView 는 레이아웃에 남아 있지만 GONE 상태여야 한다.
        onView(withText("Your garden is empty")).check(matches(not(isDisplayed())))
        onView(withText("Cilantro")).check(matches(isDisplayed()))
        onView(withText("Planted")).check(matches(isDisplayed()))
        onView(withText("Last Watered")).check(matches(isDisplayed()))
        onView(withText("water in 2 days.")).check(matches(withText("water in 2 days.")))
    }

    @Test
    fun tappingGardenPlantItem_opensPlantDetailActivity() {
        // given: Cilantro 식물이 하나 저장된 상태에서 My garden 탭을 연다.
        insertGardenPlant(plantId = "cilantro-2", name = "Cilantro 2")
        launchMainActivity()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // when: Cilantro 카드를 탭한다.
        onView(withText("Cilantro 2")).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // then: PlantDetailActivity 로 이동하는 인텐트가 발생해야 한다.
        val application = ApplicationProvider.getApplicationContext<Application>()
        val shadowApplication = Shadows.shadowOf(application)
        val startedIntent = shadowApplication.nextStartedActivity

        assertEquals(
            _root_ide_package_.com.djyoo.sunflower.screen.plant.PlantDetailActivity::class.java.name,
            startedIntent.component?.className,
        )
        // 하드코딩된 plantId 가 인텐트에 잘 전달되었는지 검증
        val plantId = startedIntent.getStringExtra("extra_plant_id")
        assertEquals("cilantro-2", plantId)
    }
}

