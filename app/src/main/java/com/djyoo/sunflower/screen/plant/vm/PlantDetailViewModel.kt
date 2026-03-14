package com.djyoo.sunflower.screen.plant.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djyoo.sunflower.BuildConfig
import com.djyoo.sunflower.screen.garden.data.repository.GardenRepository
import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.screen.plant.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantDetailViewModel(
    private val plantRepository: PlantRepository,
    private val gardenRepository: GardenRepository,
    private val plantId: String,
) : ViewModel() {

    private val _plant = MutableStateFlow<Plant?>(null)
    val plant: StateFlow<Plant?> = _plant.asStateFlow()

    val isPlantInGarden: StateFlow<Boolean> = gardenRepository.getGardenPlantById(plantId)
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = false,
        )

    private val _isAddingToGarden = MutableStateFlow(false)
    val isAddingToGarden: StateFlow<Boolean> = _isAddingToGarden.asStateFlow()

    private val _shareEvent = MutableSharedFlow<String>()
    val shareEvent: SharedFlow<String> = _shareEvent.asSharedFlow()

    private val _addedToGardenEvent = MutableSharedFlow<Unit>()
    val addedToGardenEvent: SharedFlow<Unit> = _addedToGardenEvent.asSharedFlow()

    /** SearchPhotosActivity로 이동할 때 사용할 검색어(식물 이름)를 전달한다. */
    private val _navigateToSearchPhotosEvent = MutableSharedFlow<String>()
    val navigateToSearchPhotosEvent: SharedFlow<String> = _navigateToSearchPhotosEvent.asSharedFlow()

    init {
        loadPlant()
    }

    fun onShareClicked() {
        val plant = _plant.value ?: return
        viewModelScope.launch {
            _shareEvent.emit(plant.name)
        }
    }

    fun hasValidUnsplashKey(): Boolean = (BuildConfig.UNSPLASH_ACCESS_KEY != "null")

    /** 사진 검색(Unsplash) 아이콘 클릭 시, 현재 식물 이름으로 검색 화면 이동 이벤트를 발생시킨다. */
    fun onSearchPhotosClicked() {
        val plant = _plant.value ?: return
        viewModelScope.launch {
            _navigateToSearchPhotosEvent.emit(plant.name)
        }
    }

    fun onAddToGardenClicked() {
        val plant = _plant.value ?: return
        if (_isAddingToGarden.value) return
        _isAddingToGarden.value = true
        viewModelScope.launch {
            try {
                gardenRepository.addPlantToGarden(plant)
                _addedToGardenEvent.emit(Unit)
            } finally {
                _isAddingToGarden.value = false
            }
        }
    }

    private fun loadPlant() {
        viewModelScope.launch {
            _plant.value = plantRepository.getPlantById(plantId)
        }
    }
}
