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
            val item = MainTab.all[position]

            tabView.tvTitle.text = item.title
            tabView.ivIcon.setImageResource(item.iconRes)

            tab.customView = tabView.root
        }.attach()

        mBinding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    mVmMain.selectTab(position)
                }
            },
        )
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mVmMain.uiState.collect { state ->
                    renderTabs(state.selectedTabIndex)
                    renderFilter(state.showFilter)

                    if (mBinding.viewPager.currentItem != state.selectedTabIndex) {
                        mBinding.viewPager.setCurrentItem(state.selectedTabIndex, false)
                    }
                }
            }
        }
    }

    private fun renderTabs(selectedIndex: Int) {
        MainTab.all.forEachIndexed { index, tab ->

            val tabLayoutTab = mBinding.tabLayout.getTabAt(index) ?: return@forEachIndexed
            val view = tabLayoutTab.customView ?: return@forEachIndexed
            val tabBinding = ItemMainPagerBinding.bind(view)

            tabBinding.tvTitle.text = tab.title
            tabBinding.ivIcon.setImageResource(tab.iconRes)

            val color = if (index == selectedIndex) {
                getColor(R.color.color_4caf50)
            } else {
                getColor(R.color.color_4c4c4c)
            }

            tabBinding.ivIcon.setColorFilter(color)
            tabBinding.tvTitle.setTextColor(color)
        }
    }

    private fun renderFilter(isShow: Boolean) {
        mBinding.ivFilter.isVisible = isShow
    }
}