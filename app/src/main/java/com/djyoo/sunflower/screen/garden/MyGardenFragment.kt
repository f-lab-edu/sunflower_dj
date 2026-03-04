package com.djyoo.sunflower.screen.garden

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.base.BaseFragment
import com.djyoo.sunflower.databinding.FragmentMyGardenBinding
import com.djyoo.sunflower.screen.main.MainTabItem
import com.djyoo.sunflower.screen.main.vm.MainViewModel

class MyGardenFragment : BaseFragment<FragmentMyGardenBinding>(R.layout.fragment_my_garden) {

    // MainActivity 와 공유하는 MainViewModel (탭 상태를 관리)
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPlantButton.setOnClickListener {
            mainViewModel.onTabSelected(MainTabItem.PLANT)
        }
    }
}
