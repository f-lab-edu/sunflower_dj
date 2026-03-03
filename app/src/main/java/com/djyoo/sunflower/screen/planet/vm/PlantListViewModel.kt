package com.djyoo.sunflower.screen.planet.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djyoo.sunflower.screen.planet.data.model.Plant
import com.djyoo.sunflower.screen.planet.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantListViewModel(private val plantRepository: PlantRepository) : ViewModel() {
    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    val plants: StateFlow<List<Plant>> = _plants.asStateFlow()

    private val _navigateToPlantDetail = MutableSharedFlow<Plant>()
    val navigateToPlantDetail: SharedFlow<Plant> = _navigateToPlantDetail.asSharedFlow()

    init {
        loadPlants()
    }

    /**
     * assets에서 식물 목록을 불러와 UI 상태에 반영한다.
     */
    fun loadPlants() {
        viewModelScope.launch {
            runCatching {
                plantRepository.loadPlantsFromAssets()
            }.onSuccess { plantList ->
                _plants.value = plantList
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
