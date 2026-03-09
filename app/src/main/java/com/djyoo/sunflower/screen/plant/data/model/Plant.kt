package com.djyoo.sunflower.screen.plant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * plants.json 에서 파싱되며 Room DB에 저장되는 식물 데이터 모델.
 */
@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey
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
