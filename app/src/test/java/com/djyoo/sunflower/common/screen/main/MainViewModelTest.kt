package com.djyoo.sunflower.common.screen.main

import com.djyoo.sunflower.common.screen.main.vm.MainViewModel
import com.djyoo.sunflower.common.screen.main.vm.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {

    @Test
    fun `default tab is garden and filter hidden`() {
        val viewModel = MainViewModel()

        val state: MainUiState = viewModel.uiState.value

        assertEquals(0, state.selectedTabIndex)
        // 0번 탭은 Garden 이므로 필터는 보이지 않아야 한다.
        assertEquals(false, state.showFilter)
    }

    @Test
    fun `selecting plant tab shows filter`() {
        val viewModel = MainViewModel()

        // 1번 탭(Plant)을 선택
        viewModel.selectTab(1)

        val state: MainUiState = viewModel.uiState.value

        assertEquals(1, state.selectedTabIndex)
        // Plant 탭에서는 필터가 보여야 한다.
        assertEquals(true, state.showFilter)
    }
}

