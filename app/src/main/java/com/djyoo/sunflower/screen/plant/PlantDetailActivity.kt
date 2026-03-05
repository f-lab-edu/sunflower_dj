package com.djyoo.sunflower.screen.plant

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.base.BaseActivity
import com.djyoo.sunflower.databinding.ActivityPlantDetailBinding
import com.djyoo.sunflower.screen.plant.data.model.Plant
import com.djyoo.sunflower.screen.plant.data.repository.PlantRepository
import com.djyoo.sunflower.screen.plant.vm.PlantDetailViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.launch

class PlantDetailActivity : BaseActivity<ActivityPlantDetailBinding>(R.layout.activity_plant_detail) {

    private var isImageLoaded: Boolean = false
    private var isAppBarCollapsed: Boolean = false

    private val detailViewModel: PlantDetailViewModel by viewModels {
        viewModelFactory {
            initializer {
                val plantId = this@PlantDetailActivity.intent.getStringExtra(EXTRA_PLANT_ID).orEmpty()
                val repository = PlantRepository(this@PlantDetailActivity.assets)
                PlantDetailViewModel(repository, plantId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.detailToolbar)
        // 툴바의 기본 네비게이션 아이콘은 사용하지 않고 커스텀 버튼을 사용한다.
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.detailDescription.movementMethod = LinkMovementMethod.getInstance()

        binding.detailAppBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
                val isCollapsed = appBar.totalScrollRange + verticalOffset == 0
                isAppBarCollapsed = isCollapsed

                binding.detailTitle.isVisible = !isCollapsed
                binding.detailAddButton.isVisible = !isCollapsed && isImageLoaded
            },
        )

        binding.detailBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.detailShareButton.setOnClickListener {
            detailViewModel.onShareClicked()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { detailViewModel.plant.collect(::handlePlantState) }
                launch { detailViewModel.shareEvent.collect(::handleShareEvent) }
            }
        }
    }

    private fun handlePlantState(plant: Plant?) {
        plant?.let { bindPlant(it) } ?: clearPlantDetail()
    }

    private fun clearPlantDetail() {
        binding.detailCollapsingToolbar.title = ""
        binding.detailTitle.text = getString(R.string.plant_detail_title)
        binding.detailWateringValue.text = ""
        binding.detailDescription.text = ""
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
        // 이미지가 로딩되기 전에는 Add 버튼을 숨긴다.
        isImageLoaded = false
        binding.detailAddButton.isVisible = false

        Glide.with(this)
            .load(url)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    isImageLoaded = false
                    binding.detailAddButton.isVisible = false
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    isImageLoaded = true
//                    binding.detailAddButton.isVisible = !isAppBarCollapsed
                    binding.detailAddButton.isVisible = binding.detailTitle.isVisible
                    return false
                }
            })
            .into(binding.detailImage)
    }

    companion object {
        const val EXTRA_PLANT_ID = "extra_plant_id"
    }
}
