package com.djyoo.sunflower.screen.garden.vm

import com.djyoo.sunflower.screen.garden.data.repository.GardenRepository
import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.testutil.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyGardenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: GardenRepository = mockk {
        every { getAllGardenPlants() } returns flowOf(emptyList())
    }

    private fun createPlant(
        plantId: String = "plant-1",
        name: String = "Apple",
        description: String = "A delicious fruit",
        growZoneNumber: Int = 3,
        wateringInterval: Int = 7,
        imageUrl: String = "https://example.com/apple.jpg",
    ) = Plant(plantId, name, description, growZoneNumber, wateringInterval, imageUrl)

    @Test
    fun gardenPlants_emitsPlantsFromRepository() = runTest {
        // given
        val plants = listOf(
            createPlant(plantId = "plant-1", name = "Apple"),
            createPlant(plantId = "plant-2", name = "Banana"),
        )
        every { repository.getAllGardenPlants() } returns flowOf(plants)

        // when
        val viewModel = MyGardenViewModel(repository)

        // then: 첫 번째 비어있지 않은 emission 을 기다린다.
        val emitted = viewModel.gardenPlants.first { it.isNotEmpty() }
        assertEquals(plants, emitted)
    }

    @Test
    fun onPlantClicked_emitsNavigateToPlantDetailEvent() = runTest {
        // given
        val viewModel = MyGardenViewModel(repository)
        val plant = createPlant(plantId = "plant-1", name = "Apple")

        val deferredEvent = async { viewModel.navigateToPlantDetail.first() }

        // when
        viewModel.onPlantClicked(plant)
        advanceUntilIdle()

        // then
        assertEquals(plant, deferredEvent.await())
    }
}

