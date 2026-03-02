package com.djyoo.sunflower.common.screen.main.vm

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.djyoo.sunflower.common.screen.main.MainTabItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TabUiState(
    @StringRes val titleResId: Int,
    @DrawableRes val iconRes: Int,
    val isSelected: Boolean,
)

data class MainUiState(
    val tabs: List<TabUiState>,
    val selectedTabIndex: Int,
    val isFilterVisible: Boolean,
)

class MainViewModel : ViewModel() {

    private fun buildTabs(selectedIndex: Int): List<TabUiState> {
        return MainTabItem.all.mapIndexed { index, tab ->
            TabUiState(
                titleResId = tab.titleResId,
                iconRes = tab.iconRes,
                isSelected = index == selectedIndex,
            )
        }
    }

    private val _uiState = MutableStateFlow(
        MainUiState(
            tabs = buildTabs(selectedIndex = 0),
            selectedTabIndex = 0,
            isFilterVisible = MainTabItem.all[0].showsFilter,
        ),
    )
    val uiState: StateFlow<MainUiState> = _uiState

    fun onTabSelected(tab: MainTabItem) {
        val position = MainTabItem.all.indexOf(tab).coerceAtLeast(0)
        _uiState.value = _uiState.value.copy(
            tabs = buildTabs(selectedIndex = position),
            selectedTabIndex = position,
            isFilterVisible = tab.showsFilter,
        )
    }
}