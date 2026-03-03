package com.djyoo.sunflower.screen.main

import com.djyoo.sunflower.R
import com.djyoo.sunflower.screen.main.vm.MainUiState
import com.djyoo.sunflower.screen.main.vm.MainViewModel
import com.djyoo.sunflower.screen.main.vm.TabUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {

    @Test
    fun defaultTabIsGardenAndFilterHidden() {
        val viewModel = MainViewModel()

        val actual: MainUiState = viewModel.uiState.value

        val expected = MainUiState(
            tabs = listOf(
                TabUiState(
                    titleResId = R.string.title_my_garden,
                    iconRes = MainTabItem.GARDEN.iconRes,
                    isSelected = true,
                ),
                TabUiState(
                    titleResId = R.string.title_plant_list,
                    iconRes = MainTabItem.PLANT.iconRes,
                    isSelected = false,
                ),
            ),
            selectedTabIndex = 0,
            isFilterVisible = false,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun selectingPlantTabShowsFilter() {
        val viewModel = MainViewModel()

        // Plant 탭을 선택
        viewModel.onTabSelected(MainTabItem.PLANT)

        val actual: MainUiState = viewModel.uiState.value

        val expected = MainUiState(
            tabs = listOf(
                TabUiState(
                    titleResId = R.string.title_my_garden,
                    iconRes = MainTabItem.GARDEN.iconRes,
                    isSelected = false,
                ),
                TabUiState(
                    titleResId = R.string.title_plant_list,
                    iconRes = MainTabItem.PLANT.iconRes,
                    isSelected = true,
                ),
            ),
            selectedTabIndex = 1,
            isFilterVisible = true,
        )

        assertEquals(expected, actual)
    }
}

