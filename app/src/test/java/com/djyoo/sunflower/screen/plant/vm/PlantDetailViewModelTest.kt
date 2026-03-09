package com.djyoo.sunflower.screen.plant.vm

import com.djyoo.sunflower.screen.garden.data.repository.GardenRepository
import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.screen.plant.data.repository.PlantRepository
import com.djyoo.sunflower.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlantDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: PlantRepository = mockk()
    private val gardenRepository: GardenRepository = mockk {
        every { getGardenPlantById(any()) } returns flowOf(null)
    }

    @Test
    fun loadPlant_emitsPlantFromRepository() = runTest {
        val plant = Plant(
            plantId = "malus-pumila",
            name = "Apple",
            description = "desc",
            growZoneNumber = 3,
            wateringInterval = 30,
            imageUrl = "https://example.com/apple.jpg",
        )
        // Repository 가 특정 Plant 를 반환하도록 설정
        coEvery { repository.getPlantById("malus-pumila") } returns plant

        val viewModel = PlantDetailViewModel(repository, gardenRepository, "malus-pumila")

        // 초기 코루틴 작업이 모두 완료되도록 가상 시간 진행
        advanceUntilIdle()

        // ViewModel 이 Repository 결과를 그대로 plant StateFlow 에 반영했는지 검증
        assertEquals(plant, viewModel.plant.value)
    }

    @Test
    fun loadPlant_whenRepositoryReturnsNull_emitsNull() = runTest {
        // Repository 가 null 을 반환하는 경우
        coEvery { repository.getPlantById("unknown") } returns null

        val viewModel = PlantDetailViewModel(repository, gardenRepository, "unknown")

        // 초기 코루틴 작업이 모두 완료되도록 가상 시간 진행
        advanceUntilIdle()

        // 반환값이 없으면 plant StateFlow 도 null 이어야 한다.
        assertEquals(null, viewModel.plant.value)
    }

    @Test
    fun onAddToGardenClicked_calledTwice_whileInProgress_callsRepositoryOnlyOnce() = runTest {
        // given
        val plant = Plant(
            plantId = "malus-pumila",
            name = "Apple",
            description = "desc",
            growZoneNumber = 3,
            wateringInterval = 30,
            imageUrl = "https://example.com/apple.jpg",
        )
        coEvery { repository.getPlantById("malus-pumila") } returns plant
        coEvery { gardenRepository.addPlantToGarden(plant) } coAnswers {
            delay(500)
            Unit
        }

        val viewModel = PlantDetailViewModel(repository, gardenRepository, "malus-pumila")
        advanceUntilIdle() // plant 로드 완료

        // when: 첫 번째 클릭 후 아직 add 작업이 끝나지 않은 상태에서 두 번째 클릭
        viewModel.onAddToGardenClicked()
        advanceTimeBy(100)                    // t = 100ms, addPlantToGarden 진행 중

        // 중간 상태: 추가 작업 플래그는 true 여야 한다.
        assertEquals(true, viewModel.isAddingToGarden.value)

        viewModel.onAddToGardenClicked()      // 진행 중일 때 두 번째 클릭

        advanceUntilIdle()

        // then: addPlantToGarden 은 한 번만 호출되어야 한다 (연타 방지)
        coVerify(exactly = 1) { gardenRepository.addPlantToGarden(plant) }
        // 작업 완료 후 플래그는 다시 false 로 내려와야 한다.
        assertEquals(false, viewModel.isAddingToGarden.value)
    }
}

