package com.djyoo.sunflower.screen.plant.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.screen.plant.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantDetailViewModel(
    private val plantRepository: PlantRepository,
    private val plantId: String,
) : ViewModel() {

    private val _plant = MutableStateFlow<Plant?>(null)
    val plant: StateFlow<Plant?> = _plant.asStateFlow()

    private val _shareEvent = MutableSharedFlow<String>()
    val shareEvent: SharedFlow<String> = _shareEvent.asSharedFlow()

    init {
        loadPlant()
    }

    fun onShareClicked() {
        val plant = _plant.value ?: return
        viewModelScope.launch {
            _shareEvent.emit(plant.name)
        }
    }

    private fun loadPlant() {
        viewModelScope.launch {
            _plant.value = plantRepository.getPlantById(plantId)
        }
    }
}
