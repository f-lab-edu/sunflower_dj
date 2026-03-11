package com.djyoo.sunflower.screen.plant.vm

import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.screen.plant.data.repository.PlantRepository
import com.djyoo.sunflower.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlantListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createPlant(
        plantId: String = "malus-pumila",
        name: String = "Apple",
        growZoneNumber: Int = 3,
    ) = Plant(
        plantId = plantId,
        name = name,
        description = "desc",
        growZoneNumber = growZoneNumber,
        wateringInterval = 7,
        imageUrl = "https://example.com/img.jpg",
    )

    @Test
    fun plants_whenGrowZoneFilterIsNull_emitsFullList() = runTest {
        // given
        val allPlants = listOf(
            createPlant(plantId = "malus-pumila", name = "Apple", growZoneNumber = 3),
            createPlant(plantId = "solanum-lycopersicum", name = "Tomato", growZoneNumber = 9),
            createPlant(plantId = "vitis-vinifera", name = "Grape", growZoneNumber = 9),
        )
        val expected = allPlants
        val repository = mockk<PlantRepository> {
            coEvery { loadPlantsFromAssets() } returns allPlants
        }
        val growZoneFilter = MutableStateFlow<Int?>(null)

        val viewModel = PlantListViewModel(repository, growZoneFilter)

        // when: 첫 번째 비어있지 않은 emission 을 기다린다.
        val emitted = viewModel.plants.first { it.isNotEmpty() }

        // then
        assertEquals(expected, emitted)
    }

    @Test
    fun plants_whenGrowZoneFilterIs9_emitsOnlyZone9FromAssets() = runTest {
        // given: plants.json 기준 growZoneNumber 9: Tomato, Avocado, Grape, Orange (4개)
        val allPlants = listOf(
            createPlant(plantId = "malus-pumila", name = "Apple", growZoneNumber = 3),
            createPlant(plantId = "solanum-lycopersicum", name = "Tomato", growZoneNumber = 9),
            createPlant(plantId = "persea-americana", name = "Avocado", growZoneNumber = 9),
            createPlant(plantId = "vitis-vinifera", name = "Grape", growZoneNumber = 9),
            createPlant(plantId = "citrus-x-sinensis", name = "Orange", growZoneNumber = 9),
        )
        val expected = allPlants.filter { it.growZoneNumber == 9 }
        val repository = mockk<PlantRepository> {
            coEvery { loadPlantsFromAssets() } returns allPlants
        }
        val growZoneFilter = MutableStateFlow<Int?>(9)

        val viewModel = PlantListViewModel(repository, growZoneFilter)

        // when: 첫 번째 비어있지 않은 emission 을 기다린다.
        val emitted = viewModel.plants.first { it.isNotEmpty() }

        // then
        assertEquals(expected, emitted)
    }
}
