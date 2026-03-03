package com.djyoo.sunflower.screen.planet

import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.base.BaseActivity
import com.djyoo.sunflower.databinding.ActivityPlantDetailBinding

class PlantDetailActivity : BaseActivity<ActivityPlantDetailBinding>(R.layout.activity_plant_detail) {

    override fun init() {
        mBinding.detailText.text = getString(R.string.plant_detail_title)
    }
}

