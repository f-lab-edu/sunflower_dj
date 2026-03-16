package com.djyoo.sunflower.screen.plant.data.repository

import android.content.res.AssetManager
import com.djyoo.sunflower.common.gson.GsonProvider
import com.djyoo.sunflower.screen.plant.data.model.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * assets/plants.json 을 읽어 Gson으로 파싱하여 [Plant] 목록을 제공하는 저장소.
 */
class PlantRepository(
    private val assetManager: AssetManager,
) {

    /**
     * assets 디렉터리의 JSON 파일을 읽어 [Plant] 목록으로 파싱한다.
     * IO 스레드에서 수행된다.
     *
     * @return 파싱된 식물 목록
     */
    suspend fun loadPlantsFromAssets(): List<Plant> = withContext(Dispatchers.IO) {
        val jsonString = assetManager.open(ASSET_FILE_NAME)
            .bufferedReader()
            .use { reader -> reader.readText() }
        parsePlantList(jsonString)
    }

    /**
     * [plantId]에 해당하는 [Plant]를 조회한다.
     * assets에서 목록을 불러온 뒤 일치하는 항목을 반환한다.
     */
    suspend fun getPlantById(plantId: String): Plant? =
        loadPlantsFromAssets().find { it.plantId == plantId }

    private fun parsePlantList(jsonString: String): List<Plant> {
        val typeToken = object : com.google.gson.reflect.TypeToken<List<Plant>>() {}
        return GsonProvider.gson.fromJson(jsonString, typeToken.type) ?: emptyList()
    }

    private companion object {
        const val ASSET_FILE_NAME = "plants.json"
    }
}
