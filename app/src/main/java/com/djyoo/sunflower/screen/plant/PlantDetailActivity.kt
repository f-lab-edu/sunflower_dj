package com.djyoo.sunflower.screen.plant

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.base.BaseActivity
import com.djyoo.sunflower.common.database.SunflowerDatabase
import com.djyoo.sunflower.common.image.GlideImageLoader
import com.djyoo.sunflower.common.image.ImageLoader
import com.djyoo.sunflower.databinding.ActivityPlantDetailBinding
import com.djyoo.sunflower.screen.garden.data.repository.GardenRepository
import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.screen.plant.data.repository.PlantRepository
import com.djyoo.sunflower.screen.plant.vm.PlantDetailViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PlantDetailActivity : BaseActivity<ActivityPlantDetailBinding>(R.layout.activity_plant_detail) {

    private var isTitleVisible
        get() = binding.detailTitle.isVisible
        set(value) {
            binding.detailTitle.isVisible = value
            reevaluateDetailAddButtonVisibility()
        }

    private var isPlantInGarden = false
        set(value) {
            field = value
            reevaluateDetailAddButtonVisibility()
        }

    private var hasImageLoaded = false
        set(value) {
            field = value
            reevaluateDetailAddButtonVisibility()
        }

    @VisibleForTesting
    var imageLoader: ImageLoader = GlideImageLoader()

    private val detailViewModel: PlantDetailViewModel by viewModels {
        viewModelFactory {
            initializer {
                val plantId = this@PlantDetailActivity.intent.getStringExtra(EXTRA_PLANT_ID).orEmpty()
                val plantRepository = PlantRepository(this@PlantDetailActivity.assets)
                val plantDao = SunflowerDatabase.getInstance(this@PlantDetailActivity).plantDao()
                val gardenRepository = GardenRepository(plantDao)
                PlantDetailViewModel(plantRepository, gardenRepository, plantId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.detailDescription.movementMethod = LinkMovementMethod.getInstance()

        binding.detailAppBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
                val isCollapsed = appBar.totalScrollRange + verticalOffset == 0
                isTitleVisible = !isCollapsed
            },
        )

        binding.detailBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.detailShareButton.setOnClickListener {
            detailViewModel.onShareClicked()
        }

        binding.detailAddButton.setOnClickListener {
            detailViewModel.onAddToGardenClicked()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { detailViewModel.plant.collect(::handlePlantState) }
                launch { detailViewModel.isPlantInGarden.collect { isPlantInGarden = it } }
                launch { detailViewModel.shareEvent.collect(::handleShareEvent) }
                launch { detailViewModel.addedToGardenEvent.collect { handleAddedToGarden() } }
                launch { detailViewModel.isAddingToGarden.collect(::handleAddingToGardenState) }
            }
        }
    }

    private fun reevaluateDetailAddButtonVisibility() {
        binding.detailAddButton.isVisible = !isPlantInGarden && isTitleVisible && hasImageLoaded
    }

    private fun handlePlantState(plant: Plant?) {
        plant?.let(::bindPlant) ?: clearPlantDetail()
    }

    private fun clearPlantDetail() {
        binding.detailCollapsingToolbar.title = ""
        binding.detailTitle.text = getString(R.string.plant_detail_title)
        binding.detailWateringValue.text = ""
        binding.detailDescription.text = ""
    }

    private fun handleAddedToGarden() {
        Snackbar.make(binding.root, getString(R.string.added_plant_to_garden), Snackbar.LENGTH_SHORT).show()
    }

    private fun handleAddingToGardenState(isAdding: Boolean) {
        binding.detailAddButton.isEnabled = !isAdding
    }

    private fun handleShareEvent(plantName: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_plant_text, plantName))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    private fun bindPlant(plant: Plant) {
        bindPlantContent(plant)
        loadPlantImage(plant.imageUrl)
    }

    private fun bindPlantContent(plant: Plant) {
        binding.detailCollapsingToolbar.title = plant.name
        binding.detailCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT)
        binding.detailTitle.text = plant.name
        binding.detailWateringValue.text = getString(R.string.watering_interval_days, plant.wateringInterval)
        binding.detailDescription.text = Html.fromHtml(plant.description, Html.FROM_HTML_MODE_COMPACT)
    }

    private fun loadPlantImage(url: String) {
        hasImageLoaded = false

        imageLoader.load(url, binding.detailImage) {
            hasImageLoaded = true
        }
    }

    companion object {
        const val EXTRA_PLANT_ID = "extra_plant_id"
    }
}
