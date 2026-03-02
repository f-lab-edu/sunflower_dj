package com.djyoo.sunflower.common.screen.main.vm

import androidx.lifecycle.ViewModel
import com.djyoo.sunflower.common.screen.main.MainTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MainUiState(
    val selectedTabIndex: Int = 0,
    val showFilter: Boolean = MainTab.all[0].showFilter,
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    fun selectTab(position: Int) {
        val tab = MainTab.all[position]
        _uiState.value = _uiState.value.copy(
            selectedTabIndex = position,
            showFilter = tab.showFilter,
        )
    }
}