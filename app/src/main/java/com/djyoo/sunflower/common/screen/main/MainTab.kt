package com.djyoo.sunflower.common.screen.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.djyoo.sunflower.R

enum class MainTabItem(
    @StringRes val titleResId: Int,
    @DrawableRes val iconRes: Int,
    val showsFilter: Boolean,
) {
    GARDEN(R.string.title_my_garden, R.drawable.ic_my_garden_active, false),
    PLANT(R.string.title_plant_list, R.drawable.ic_plant_list_active, true);

    companion object {
        val all: List<MainTabItem> = values().toList()
    }
}