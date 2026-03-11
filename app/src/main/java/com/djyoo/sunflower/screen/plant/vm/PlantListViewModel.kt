package com.djyoo.sunflower.screen.plant.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.screen.plant.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantListViewModel(
    private val plantRepository: PlantRepository,
    private val growZoneFilter: StateFlow<Int?>,
) : ViewModel() {
    private val _allPlants = MutableStateFlow<List<Plant>>(emptyList())

    val plants: StateFlow<List<Plant>> = combine(_allPlants, growZoneFilter) { list, zone ->
        if (zone == null) list else list.filter { it.growZoneNumber == zone }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList(),
    )

    private val _navigateToPlantDetail = MutableSharedFlow<Plant>()
    val navigateToPlantDetail: SharedFlow<Plant> = _navigateToPlantDetail.asSharedFlow()

    init {
        loadPlants()
    }

    /**
     * assets에서 식물 목록을 불러와 UI 상태에 반영한다.
     */
    private fun loadPlants() {
        viewModelScope.launch {
            runCatching {
                plantRepository.loadPlantsFromAssets()
            }.onSuccess { plantList ->
                _allPlants.value = plantList
            }.onFailure { throwable ->
                // 실패 시에는 별도 에러 상태를 두지 않고 빈 리스트 유지
            }
        }
    }

    fun onPlantClicked(plant: Plant) {
        viewModelScope.launch {
            _navigateToPlantDetail.emit(plant)
        }
    }
}
