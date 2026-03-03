package com.djyoo.sunflower.screen.planet.data.repository

import android.content.res.AssetManager
import com.djyoo.sunflower.screen.planet.data.model.Plant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * assets/plants.json 을 읽어 Gson으로 파싱하여 [Plant] 목록을 제공하는 저장소.
 */
class PlantRepository(
    private val assetManager: AssetManager,
    private val gson: Gson = Gson(),
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

    private fun parsePlantList(jsonString: String): List<Plant> {
        val typeToken = object : TypeToken<List<Plant>>() {}
        return gson.fromJson(jsonString, typeToken.type) ?: emptyList()
    }

    private companion object {
        const val ASSET_FILE_NAME = "plants.json"
    }
}
