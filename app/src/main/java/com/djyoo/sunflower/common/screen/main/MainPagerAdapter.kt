package com.djyoo.sunflower.common.screen.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.djyoo.sunflower.common.screen.fragment.MyGardenFragment
import com.djyoo.sunflower.common.screen.fragment.PlantListFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (MainTab.all[position]) {
            is MainTab.Garden -> MyGardenFragment()
            else -> PlantListFragment()
        }
    }

    override fun getItemCount(): Int {
        return MainTab.all.size
    }
}