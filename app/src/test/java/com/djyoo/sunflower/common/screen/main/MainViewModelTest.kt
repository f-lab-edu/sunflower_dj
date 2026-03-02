package com.djyoo.sunflower.common.screen.main

import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.screen.main.vm.MainUiState
import com.djyoo.sunflower.common.screen.main.vm.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {

    @Test
    fun `default tab is garden and filter hidden`() {
        val viewModel = MainViewModel()

        val state: MainUiState = viewModel.uiState.value

        assertEquals(0, state.selectedTabIndex)
        // 0번 탭은 Garden 이므로 필터는 보이지 않아야 한다.
        assertEquals(false, state.isFilterVisible)
        // 탭 리스트 검증
        assertEquals(2, state.tabs.size)
        assertEquals(R.string.title_my_garden, state.tabs[0].titleResId)
        assertEquals(R.string.title_plant_list, state.tabs[1].titleResId)
        assertEquals(true, state.tabs[0].isSelected)
        assertEquals(false, state.tabs[1].isSelected)
    }

    @Test
    fun `selecting plant tab shows filter`() {
        val viewModel = MainViewModel()

        // Plant 탭을 선택
        viewModel.onTabSelected(MainTabItem.PLANT)

        val state: MainUiState = viewModel.uiState.value

        assertEquals(1, state.selectedTabIndex)
        // Plant 탭에서는 필터가 보여야 한다.
        assertEquals(true, state.isFilterVisible)
        // 선택 상태 검증
        assertEquals(false, state.tabs[0].isSelected)
        assertEquals(true, state.tabs[1].isSelected)
    }
}

