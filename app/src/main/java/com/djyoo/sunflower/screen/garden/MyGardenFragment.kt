package com.djyoo.sunflower.screen.garden

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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
import com.djyoo.sunflower.common.database.SunflowerDatabase
import com.djyoo.sunflower.common.widget.GridSpacingItemDecoration
import com.djyoo.sunflower.databinding.FragmentMyGardenBinding
import com.djyoo.sunflower.screen.garden.data.repository.GardenRepository
import com.djyoo.sunflower.screen.garden.vm.MyGardenViewModel
import com.djyoo.sunflower.screen.main.MainTabItem
import com.djyoo.sunflower.screen.main.vm.MainViewModel
import com.djyoo.sunflower.screen.plant.PlantDetailActivity
import com.djyoo.sunflower.screen.plant.data.model.Plant
import kotlinx.coroutines.launch

class MyGardenFragment : BaseFragment<FragmentMyGardenBinding>(R.layout.fragment_my_garden) {

    private val mainViewModel: MainViewModel by activityViewModels()

    private val myGardenViewModel: MyGardenViewModel by viewModels {
        viewModelFactory {
            initializer {
                val dao = SunflowerDatabase.getInstance(requireContext()).plantDao()
                val repository = GardenRepository(dao)
                MyGardenViewModel(repository)
            }
        }
    }

    private lateinit var gardenPlantAdapter: GardenPlantAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gardenPlantAdapter = GardenPlantAdapter(myGardenViewModel::onPlantClicked)
        setupRecyclerView()
        observeUiState()

        binding.addPlantButton.setOnClickListener {
            mainViewModel.onTabSelected(MainTabItem.PLANT)
        }
    }

    private fun setupRecyclerView() {
        binding.gardenRecyclerView.adapter = gardenPlantAdapter
        binding.gardenRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.gardenRecyclerView.setHasFixedSize(true)
        binding.gardenRecyclerView.addItemDecoration(
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
                    myGardenViewModel.gardenPlants.collect(::applyGardenState)
                }
                launch {
                    myGardenViewModel.navigateToPlantDetail.collect(::openPlantDetail)
                }
            }
        }
    }

    private fun openPlantDetail(plantId: String) {
        startActivity(
            Intent(activity, PlantDetailActivity::class.java).apply {
                putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId)
            },
        )
    }

    private fun applyGardenState(plants: List<Plant>) {
        val hasPlants = plants.isNotEmpty()
        binding.gardenRecyclerView.isVisible = hasPlants
        binding.gardenEmptyText.isVisible = !hasPlants
        binding.addPlantButton.isVisible = !hasPlants
        gardenPlantAdapter.submitList(plants)
    }
}
