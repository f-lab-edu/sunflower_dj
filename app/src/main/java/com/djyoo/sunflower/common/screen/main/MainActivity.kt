package com.djyoo.sunflower.common.screen.main

import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.common.base.BaseActivity
import com.djyoo.sunflower.common.screen.main.vm.MainViewModel
import com.djyoo.sunflower.common.screen.main.vm.TabUiState
import com.djyoo.sunflower.databinding.ActivityMainBinding
import com.djyoo.sunflower.databinding.ItemMainPagerBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mVmMain: MainViewModel by viewModels()

    override fun init() {
        setupViewPager()
        observeUiState()
    }

    private fun setupViewPager() {
        mBinding.viewPager.adapter = MainPagerAdapter(this)

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            val tabView = ItemMainPagerBinding.inflate(layoutInflater)
            val item = MainTabItem.entries[position]

            tabView.tabTitle.text = getString(item.titleResId)
            tabView.tabIcon.setImageResource(item.iconRes)

            tab.customView = tabView.root
        }.attach()

        mBinding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    mVmMain.onTabSelected(MainTabItem.entries[position])
                }
            },
        )
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mVmMain.uiState.collect { state ->
                    applyTabState(state.tabs)
                    applyFilterState(state.isFilterVisible)

                    if (mBinding.viewPager.currentItem != state.selectedTabIndex) {
                        mBinding.viewPager.setCurrentItem(state.selectedTabIndex, false)
                    }
                }
            }
        }
    }

    private fun applyTabState(tabs: List<TabUiState>) {
        tabs.forEachIndexed { index, tabUi: TabUiState ->

            val tabLayoutTab = mBinding.tabLayout.getTabAt(index) ?: return@forEachIndexed
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
        mBinding.filterIcon.isVisible = isFilterVisible
    }
}