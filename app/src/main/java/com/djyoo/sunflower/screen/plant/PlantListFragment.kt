package com.djyoo.sunflower.screen.plant

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
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
import com.djyoo.sunflower.databinding.FragmentPlantListBinding
import com.djyoo.sunflower.screen.main.vm.MainViewModel
import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.screen.plant.data.repository.PlantRepository
import com.djyoo.sunflower.screen.plant.vm.PlantListViewModel
import kotlinx.coroutines.launch

class PlantListFragment : BaseFragment<FragmentPlantListBinding>(R.layout.fragment_plant_list) {

    private val mainViewModel: MainViewModel by activityViewModels()

    private val plantViewModel: PlantListViewModel by viewModels {
        viewModelFactory {
            initializer {
                val repository = PlantRepository(requireContext().assets)
                PlantListViewModel(repository, mainViewModel.growZoneFilter)
            }
        }
    }

    private val plantListAdapter = PlantListAdapter { plant ->
        plantViewModel.onPlantClicked(plant)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        binding.plantListRecyclerView.adapter = plantListAdapter
        binding.plantListRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.plantListRecyclerView.setHasFixedSize(true)

        binding.plantListRecyclerView.addItemDecoration(
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
                    plantViewModel.plants.collect { plants ->
                        applyPlantListState(plants)
                    }
                }
                launch {
                    plantViewModel.navigateToPlantDetail.collect { plant ->
                        openPlantDetail(plant.plantId)
                    }
                }
            }
        }
    }

    private fun applyPlantListState(plants: List<Plant>) {
        plantListAdapter.submitList(plants)
    }

    private fun openPlantDetail(plantId: String) {
        startActivity(
            Intent(activity, PlantDetailActivity::class.java).apply {
                putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId)
            },
        )
    }
}
