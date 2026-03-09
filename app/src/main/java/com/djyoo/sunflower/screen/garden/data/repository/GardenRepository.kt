package com.djyoo.sunflower.screen.garden.data.repository

import com.djyoo.sunflower.screen.plant.data.dao.PlantDao
import com.djyoo.sunflower.screen.plant.data.model.Plant
import kotlinx.coroutines.flow.Flow

class GardenRepository(private val plantDao: PlantDao) {

    fun getAllGardenPlants(): Flow<List<Plant>> = plantDao.getAllPlants()

    fun getGardenPlantById(plantId: String): Flow<Plant?> = plantDao.getPlantById(plantId)

    suspend fun addPlantToGarden(plant: Plant) {
        plantDao.insertPlant(plant)
    }
}
