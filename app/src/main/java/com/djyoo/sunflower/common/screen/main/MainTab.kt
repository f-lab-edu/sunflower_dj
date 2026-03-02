package com.djyoo.sunflower.common.screen.main

import com.djyoo.sunflower.R

sealed class MainTab(val title: String, val iconRes: Int, val showFilter: Boolean) {
    object Garden : MainTab("My Garden", R.drawable.ic_my_garden_active, false)
    object Plant : MainTab("Plant list", R.drawable.ic_plant_list_active, true)

    companion object {
        val all = listOf(Garden, Plant)
    }
}