package com.djyoo.sunflower.screen.plant

import android.os.Bundle
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.base.BaseActivity
import com.djyoo.sunflower.databinding.ActivityPlantDetailBinding

class PlantDetailActivity : BaseActivity<ActivityPlantDetailBinding>(R.layout.activity_plant_detail) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.detailText.text = getString(R.string.plant_detail_title)
    }
}

