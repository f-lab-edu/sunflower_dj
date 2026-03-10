package com.djyoo.sunflower.screen.main.vm

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.djyoo.sunflower.screen.main.MainTabItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
        return MainTabItem.entries.mapIndexed { index, tab ->
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
            isFilterVisible = MainTabItem.entries[0].showsFilter,
        ),
    )
    val uiState: StateFlow<MainUiState> = _uiState

    private val _growZoneFilter = MutableStateFlow<Int?>(null)
    val growZoneFilter: StateFlow<Int?> = _growZoneFilter.asStateFlow()

    fun onFilterIconClicked() {
        _growZoneFilter.update { if (it == FILTER_GROW_ZONE) null else FILTER_GROW_ZONE }
    }

    private companion object {
        const val FILTER_GROW_ZONE = 9
    }

    fun onTabSelected(tab: MainTabItem) {
        val position = MainTabItem.entries.indexOf(tab).coerceAtLeast(0)
        _uiState.value = _uiState.value.copy(
            tabs = buildTabs(selectedIndex = position),
            selectedTabIndex = position,
            isFilterVisible = tab.showsFilter,
        )
    }
}