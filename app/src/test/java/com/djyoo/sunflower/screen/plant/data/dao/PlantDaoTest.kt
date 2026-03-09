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
        plantId: String = "malus-pumila",
        name: String = "Apple",
        description: String = "A delicious fruit",
        growZoneNumber: Int = 3,
        wateringInterval: Int = 30,
        imageUrl: String = "https://upload.wikimedia.org/wikipedia/commons/5/55/Apple_orchard_in_Tasmania.jpg",
    ) = Plant(plantId, name, description, growZoneNumber, wateringInterval, imageUrl)

    @Test
    fun insertPlant_emitsNewItem() = runTest {
        val expected = createPlant()
        plantDao.insertPlant(expected)

        val result = plantDao.getAllPlants().first()
        assertEquals(1, result.size)
        assertEquals(expected, result.single())
    }

    @Test
    fun getAllPlants_sortedByName() = runTest {
        plantDao.insertPlant(createPlant(plantId = "coriandrum-sativum", name = "Cilantro"))
        plantDao.insertPlant(createPlant(plantId = "malus-pumila", name = "Apple"))
        plantDao.insertPlant(createPlant(plantId = "beta-vulgaris", name = "Beet"))

        val result = plantDao.getAllPlants().first()
        val expected = listOf(
            createPlant(plantId = "malus-pumila", name = "Apple"),
            createPlant(plantId = "beta-vulgaris", name = "Beet"),
            createPlant(plantId = "coriandrum-sativum", name = "Cilantro"),
        )

        assertEquals(expected, result)
    }

    @Test
    fun conflict_replaceWorks() = runTest {
        val initial = createPlant(plantId = "malus-pumila", name = "Apple", wateringInterval = 7,)
        val expected = createPlant(plantId = "malus-pumila", name = "Red Apple", wateringInterval = 10,)

        plantDao.insertPlant(initial)
        plantDao.insertPlant(expected)

        val result = plantDao.getAllPlants().first()
        assertEquals(1, result.size)
        assertEquals(expected, result.single())
    }
}
