package com.djyoo.sunflower.screen.planet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.GridLayoutManager
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.base.BaseFragment
import com.djyoo.sunflower.common.widget.GridSpacingItemDecoration
import com.djyoo.sunflower.databinding.FragmentPlanetListBinding
import com.djyoo.sunflower.screen.planet.data.model.Plant
import com.djyoo.sunflower.screen.planet.data.repository.PlantRepository
import com.djyoo.sunflower.screen.planet.vm.PlantListViewModel
import kotlinx.coroutines.launch

class PlantListFragment : BaseFragment<FragmentPlanetListBinding>(R.layout.fragment_planet_list) {

    private val mVmPlanet: PlantListViewModel by viewModels {
        viewModelFactory {
            initializer {
                val repository = PlantRepository(requireContext().assets)
                PlantListViewModel(repository)
            }
        }
    }

    private val mPlantListAdapter = PlantListAdapter { plant ->
        mVmPlanet.onPlantClicked(plant)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        mBinding.plantListRecyclerView.adapter = mPlantListAdapter
        mBinding.plantListRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)
        mBinding.plantListRecyclerView.setHasFixedSize(true)

        mBinding.plantListRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 2,
                spacingDp = 20,
                includeBottom = false,
            ),
        )
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mVmPlanet.plants.collect { plants ->
                        applyPlantListState(plants)
                    }
                }
                launch {
                    mVmPlanet.navigateToPlantDetail.collect { _ ->
                        openPlantDetail()
                    }
                }
            }
        }
    }

    private fun applyPlantListState(plants: List<Plant>) {
        mPlantListAdapter.submitItems(plants)
    }

    private fun openPlantDetail() {
        startActivity(Intent(requireContext(), PlantDetailActivity::class.java))
    }
}
