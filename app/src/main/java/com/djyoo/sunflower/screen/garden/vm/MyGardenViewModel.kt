package com.djyoo.sunflower.screen.garden.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djyoo.sunflower.screen.garden.data.repository.GardenRepository
import com.djyoo.sunflower.screen.plant.data.model.Plant
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyGardenViewModel(gardenRepository: GardenRepository) : ViewModel() {

    val gardenPlants: StateFlow<List<Plant>> = gardenRepository.getAllGardenPlants()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList(),
        )

    private val _navigateToPlantDetail = MutableSharedFlow<String>()
    val navigateToPlantDetail: SharedFlow<String> = _navigateToPlantDetail.asSharedFlow()

    fun onPlantClicked(plant: Plant) {
        viewModelScope.launch {
            _navigateToPlantDetail.emit(plant.plantId)
        }
    }
}
