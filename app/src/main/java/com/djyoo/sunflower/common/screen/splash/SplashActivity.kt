package com.djyoo.sunflower.common.screen.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.common.base.BaseActivity
import com.djyoo.sunflower.common.screen.main.MainActivity
import com.djyoo.sunflower.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    override fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(mActivity, MainActivity::class.java))
            finish()
        }, 3000)
    }
}