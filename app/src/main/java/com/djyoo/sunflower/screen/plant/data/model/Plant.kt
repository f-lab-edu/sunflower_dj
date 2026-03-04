package com.djyoo.sunflower.screen.plant.data.model

import com.google.gson.annotations.SerializedName

/**
 * plants.json 에서 파싱되는 식물 데이터 모델.
 */
data class Plant(
    @SerializedName("plantId")
    val plantId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("growZoneNumber")
    val growZoneNumber: Int,

    @SerializedName("wateringInterval")
    val wateringInterval: Int,

    @SerializedName("imageUrl")
    val imageUrl: String,
)
