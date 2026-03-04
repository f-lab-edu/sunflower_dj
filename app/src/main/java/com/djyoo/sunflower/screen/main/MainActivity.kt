package com.djyoo.sunflower.screen.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.base.BaseActivity
import com.djyoo.sunflower.databinding.ActivityMainBinding
import com.djyoo.sunflower.databinding.ItemMainPagerBinding
import com.djyoo.sunflower.screen.main.vm.MainViewModel
import com.djyoo.sunflower.screen.main.vm.TabUiState
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewPager()
        observeUiState()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = MainPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabView = ItemMainPagerBinding.inflate(layoutInflater)
            val item = MainTabItem.entries[position]

            tabView.tabTitle.text = getString(item.titleResId)
            tabView.tabIcon.setImageResource(item.iconRes)

            tab.customView = tabView.root
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    mainViewModel.onTabSelected(MainTabItem.entries[position])
                }
            },
        )
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiState.collect { state ->
                    applyTabState(state.tabs)
                    applyFilterState(state.isFilterVisible)

                    if (binding.viewPager.currentItem != state.selectedTabIndex) {
                        binding.viewPager.setCurrentItem(state.selectedTabIndex, false)
                    }
                }
            }
        }
    }

    private fun applyTabState(tabs: List<TabUiState>) {
        tabs.forEachIndexed { index, tabUi: TabUiState ->

            val tabLayoutTab = binding.tabLayout.getTabAt(index) ?: return@forEachIndexed
            val view = tabLayoutTab.customView ?: return@forEachIndexed
            val tabBinding = ItemMainPagerBinding.bind(view)

            tabBinding.tabTitle.text = getString(tabUi.titleResId)
            tabBinding.tabIcon.setImageResource(tabUi.iconRes)

            val color = if (tabUi.isSelected) {

                getColor(R.color.color_4caf50)
            } else {
                getColor(R.color.color_4c4c4c)
            }

            tabBinding.tabIcon.setColorFilter(color)
            tabBinding.tabTitle.setTextColor(color)
        }
    }

    private fun applyFilterState(isFilterVisible: Boolean) {
        binding.filterIcon.isVisible = isFilterVisible
    }
}