package com.djyoo.sunflower.screen.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.djyoo.sunflower.screen.garden.MyGardenFragment
import com.djyoo.sunflower.screen.plant.PlantListFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (MainTabItem.entries[position]) {
            MainTabItem.GARDEN -> MyGardenFragment()
            MainTabItem.PLANT -> PlantListFragment()
        }
    }

    override fun getItemCount(): Int {
        return MainTabItem.entries.size
    }
}