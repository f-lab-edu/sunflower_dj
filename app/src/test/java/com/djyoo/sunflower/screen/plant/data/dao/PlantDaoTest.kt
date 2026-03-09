package com.djyoo.sunflower.screen.plant.data.dao

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.djyoo.sunflower.common.database.SunflowerDatabase
import com.djyoo.sunflower.screen.plant.data.model.Plant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PlantDaoTest {

    private lateinit var database: SunflowerDatabase
    private lateinit var plantDao: PlantDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(context, SunflowerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        plantDao = database.plantDao()
    }

    @After
    fun tearDown() {
        database.close()
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
    fun insertPlant_emitsNewItem() = runTest {
        plantDao.insertPlant(createPlant(plantId = "apple-1", name = "Apple"))

        val result = plantDao.getAllPlants().first()
        assertEquals(1, result.size)
        assertEquals("apple-1", result[0].plantId)
        assertEquals("Apple", result[0].name)
    }

    @Test
    fun getAllPlants_sortedByName() = runTest {
        plantDao.insertPlant(createPlant(plantId = "plant-3", name = "Cherry"))
        plantDao.insertPlant(createPlant(plantId = "plant-1", name = "Apple"))
        plantDao.insertPlant(createPlant(plantId = "plant-2", name = "Banana"))

        val result = plantDao.getAllPlants().first()
        assertEquals(listOf("Apple", "Banana", "Cherry"), result.map { it.name })
    }

    @Test
    fun conflict_replaceWorks() = runTest {
        plantDao.insertPlant(createPlant(plantId = "plant-1", name = "Apple", wateringInterval = 7))

        plantDao.insertPlant(createPlant(plantId = "plant-1", name = "Red Apple", wateringInterval = 10))

        val result = plantDao.getAllPlants().first()
        assertEquals(1, result.size)
        assertEquals("Red Apple", result[0].name)
        assertEquals(10, result[0].wateringInterval)
    }
}
